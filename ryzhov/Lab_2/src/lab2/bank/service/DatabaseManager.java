package lab2.bank.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseManager {
    private static final String DATABASE_URL = "jdbc:sqlite:bank.db";
    private static volatile DatabaseManager instance;
    private final Connection connection;
    private DatabaseManager() {
        try {
            connection = DriverManager.getConnection(DATABASE_URL);
            System.out.println("Соединение с SQLite установлено.");
            createTables();
        } catch (SQLException e) {
            System.err.println("Ошибка подключения к базе данных: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
    public static DatabaseManager getInstance() {
        if (instance == null) {
            synchronized (DatabaseManager.class) {
                if (instance == null) {
                    instance = new DatabaseManager();
                }
            }
        }
        return instance;
    }
    public Connection getConnection() {
        return connection;
    }
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Соединение с SQLite закрыто.");
            } catch (SQLException e) {
                System.err.println("Ошибка при закрытии соединения: " + e.getMessage());
            }
        }
    }
    private void createTables() throws SQLException {
        var sqlUsers = "CREATE TABLE IF NOT EXISTS users (" +
                "    id TEXT PRIMARY KEY," +
                "    nickname TEXT NOT NULL UNIQUE" +
                ");";

        var sqlAccounts = "CREATE TABLE IF NOT EXISTS accounts (" +
                "    id TEXT PRIMARY KEY," +
                "    user_id TEXT NOT NULL," +
                "    balance REAL NOT NULL," +
                "    is_frozen INTEGER NOT NULL DEFAULT 0," +
                "    FOREIGN KEY (user_id) REFERENCES users(id)" +
                ");";

        var sqlTransactions = "CREATE TABLE IF NOT EXISTS transactions (" +
                "    id TEXT PRIMARY KEY," +
                "    timestamp INTEGER NOT NULL," +
                "    type TEXT NOT NULL," +
                "    status TEXT NOT NULL," +
                "    amount REAL NOT NULL," +
                "    source_account_id TEXT," +
                "    destination_account_id TEXT," +
                "    failure_reason TEXT," +
                "    FOREIGN KEY (source_account_id) REFERENCES accounts(id)," +
                "    FOREIGN KEY (destination_account_id) REFERENCES accounts(id)" +
                ");";

        try (var stmt = connection.createStatement()) {
            stmt.execute(sqlUsers);
            stmt.execute(sqlAccounts);
            stmt.execute(sqlTransactions);
        }
    }
}
