package lab2.bank.dao;

import lab2.bank.model.Account;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AccountDao {

    private final Connection connection;

    public AccountDao(Connection connection) {
        this.connection = connection;
    }

    public void save(Account account) {
        var sql = "INSERT INTO accounts(id, user_id, balance, is_frozen) VALUES(?, ?, ?, ?)";
        try (var pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, account.getId().toString());
            pstmt.setString(2, account.getUserId().toString());
            pstmt.setDouble(3, account.getBalance());
            pstmt.setInt(4, account.isFrozen() ? 1 : 0);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Ошибка при сохранении счета: " + e.getMessage());
        }
    }

    public void update(Account account) {
        var sql = "UPDATE accounts SET balance = ?, is_frozen = ? WHERE id = ?";
        try (var pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, account.getBalance());
            pstmt.setInt(2, account.isFrozen() ? 1 : 0);
            pstmt.setString(3, account.getId().toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Ошибка при обновлении счета: " + e.getMessage());
        }
    }

    public Optional<Account> findById(UUID id) {
        var sql = "SELECT id, user_id, balance, is_frozen FROM accounts WHERE id = ?";
        try (var pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id.toString());
            var rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToAccount(rs));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при поиске счета: " + e.getMessage());
        }
        return Optional.empty();
    }

    public List<Account> findAll() {
        var accounts = new ArrayList<Account>();
        var sql = "SELECT id, user_id, balance, is_frozen FROM accounts ORDER BY user_id";
        try (var stmt = connection.createStatement();
             var rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                accounts.add(mapResultSetToAccount(rs));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении списка всех счетов: " + e.getMessage());
        }
        return accounts;
    }

    public List<Account> findByUserId(UUID userId) {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT id, user_id, balance, is_frozen FROM accounts WHERE user_id = ?";
        try (var pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, userId.toString());
            var rs = pstmt.executeQuery();
            while (rs.next()) {
                accounts.add(mapResultSetToAccount(rs));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при поиске счетов по ID пользователя: " + e.getMessage());
        }
        return accounts;
    }

    private Account mapResultSetToAccount(ResultSet rs) throws SQLException {
        var id = UUID.fromString(rs.getString("id"));
        var userId = UUID.fromString(rs.getString("user_id"));
        var balance = rs.getDouble("balance");
        var isFrozen = rs.getInt("is_frozen") == 1;
        return new Account(id, userId, balance, isFrozen);
    }
}