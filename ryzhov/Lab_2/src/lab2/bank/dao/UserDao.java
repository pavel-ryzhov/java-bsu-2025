package lab2.bank.dao;

import lab2.bank.model.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserDao {

    private final Connection connection;

    public UserDao(Connection connection) {
        this.connection = connection;
    }

    public void save(User user) {
        var sql = "INSERT INTO users(id, nickname) VALUES(?, ?)";
        try (var pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, user.getId().toString());
            pstmt.setString(2, user.getNickname());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Ошибка при сохранении пользователя: " + e.getMessage());
        }
    }

    public Optional<User> findById(UUID id) {
        var sql = "SELECT id, nickname FROM users WHERE id = ?";
        try (var pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id.toString());
            var rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при поиске пользователя: " + e.getMessage());
        }
        return Optional.empty();
    }

    public List<User> findAll() {
        var users = new ArrayList<User>();
        var sql = "SELECT id, nickname FROM users";
        try (var stmt = connection.createStatement();
             var rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении списка всех пользователей: " + e.getMessage());
        }
        return users;
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        UUID id = UUID.fromString(rs.getString("id"));
        String nickname = rs.getString("nickname");
        return new User(id, nickname);
    }
}