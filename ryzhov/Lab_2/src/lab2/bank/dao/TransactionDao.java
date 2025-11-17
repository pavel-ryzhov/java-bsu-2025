package lab2.bank.dao;

import lab2.bank.model.Transaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record TransactionDao(Connection connection) {

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

    public List<Transaction> findAll() {
        var transactions = new ArrayList<Transaction>();
        var sql = "SELECT id, timestamp, type, status, amount, source_account_id, " +
                "destination_account_id, failure_reason FROM transactions ORDER BY timestamp DESC";

        try (var stmt = connection.createStatement();
             var rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении списка транзакций: " + e.getMessage());
        }
        return transactions;
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

    private Transaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        var id = UUID.fromString(rs.getString("id"));
        long timestamp = rs.getLong("timestamp");
        Transaction.TransactionType type = Transaction.TransactionType.valueOf(rs.getString("type"));
        Transaction.TransactionStatus status = Transaction.TransactionStatus.valueOf(rs.getString("status"));
        var amount = rs.getDouble("amount");
        var sourceIdStr = rs.getString("source_account_id");
        var sourceId = (sourceIdStr != null) ? UUID.fromString(sourceIdStr) : null;
        var destIdStr = rs.getString("destination_account_id");
        var destId = (destIdStr != null) ? UUID.fromString(destIdStr) : null;
        var failureReason = rs.getString("failure_reason");
        return new Transaction(id, timestamp, type, amount, sourceId, destId, status, failureReason);
    }
}