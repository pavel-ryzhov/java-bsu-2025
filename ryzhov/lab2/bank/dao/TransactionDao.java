package lab2.bank.dao;

import lab2.bank.model.Transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.UUID;

public class TransactionDao {

    private final Connection connection;

    public TransactionDao(Connection connection) {
        this.connection = connection;
    }

    public void save(Transaction tx) {
        var sql = "INSERT INTO transactions(id, timestamp, type, status, amount, source_account_id, destination_account_id, failure_reason) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
        try (var pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, tx.getId().toString());
            pstmt.setLong(2, tx.getTimestamp());
            pstmt.setString(3, tx.getType().name());
            pstmt.setString(4, tx.getStatus().name());
            pstmt.setDouble(5, tx.getAmount());

            setNullableString(pstmt, 6, tx.getSourceAccountId());
            setNullableString(pstmt, 7, tx.getDestinationAccountId());

            pstmt.setString(8, tx.getFailureReason());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Ошибка при сохранении транзакции: " + e.getMessage());
        }
    }

    public void updateStatus(Transaction tx) {
        var sql = "UPDATE transactions SET status = ?, failure_reason = ? WHERE id = ?";
        try (var pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, tx.getStatus().name());
            pstmt.setString(2, tx.getFailureReason());
            pstmt.setString(3, tx.getId().toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Ошибка при обновлении статуса транзакции: " + e.getMessage());
        }
    }

    private void setNullableString(PreparedStatement pstmt, int index, UUID value) throws SQLException {
        if (value != null) {
            pstmt.setString(index, value.toString());
        } else {
            pstmt.setNull(index, Types.VARCHAR);
        }
    }
}