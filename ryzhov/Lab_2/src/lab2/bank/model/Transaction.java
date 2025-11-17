package lab2.bank.model;

import java.util.UUID;

public class Transaction {

    public enum TransactionType {
        DEPOSIT,    // Пополнение
        WITHDRAW,   // Снятие
        TRANSFER,   // Перевод
        FREEZE      // Заморозка
    }

    public enum TransactionStatus {
        PENDING,
        SUCCESSFUL,
        FAILED
    }

    private final UUID id;
    private final long timestamp;
    private final TransactionType type;
    private final double amount;

    private final UUID sourceAccountId;
    private final UUID destinationAccountId;

    private TransactionStatus status;
    private String failureReason;


    public static Transaction createDeposit(UUID accountId, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма пополнения должна быть положительной.");
        }
        return new Transaction(TransactionType.DEPOSIT, amount, null, accountId);
    }

    public static Transaction createWithdrawal(UUID accountId, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма снятия должна быть положительной.");
        }
        return new Transaction(TransactionType.WITHDRAW, amount, accountId, null);
    }

    public static Transaction createTransfer(UUID sourceAccountId, UUID destinationAccountId, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма перевода должна быть положительной.");
        }
        if (sourceAccountId.equals(destinationAccountId)) {
            throw new IllegalArgumentException("Счет отправителя и получателя не могут совпадать.");
        }
        return new Transaction(TransactionType.TRANSFER, amount, sourceAccountId, destinationAccountId);
    }

    public static Transaction createFreeze(UUID accountId) {
        return new Transaction(TransactionType.FREEZE, 0.0, accountId, null);
    }

    public UUID getId() {
        return id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public TransactionType getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public UUID getSourceAccountId() {
        return sourceAccountId;
    }

    public UUID getDestinationAccountId() {
        return destinationAccountId;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public void markAsSuccessful() {
        this.status = TransactionStatus.SUCCESSFUL;
        this.failureReason = null;
    }

    public void markAsFailed(String reason) {
        this.status = TransactionStatus.FAILED;
        this.failureReason = reason;
    }

    /**
     * Приватный конструктор, чтобы использовать его в статических фабричных методах для создания экземпляров.
     */
    private Transaction(TransactionType type, double amount, UUID sourceAccountId, UUID destinationAccountId) {
        this(UUID.randomUUID(), System.currentTimeMillis(), type, amount, sourceAccountId, destinationAccountId, TransactionStatus.PENDING, null);
    }

    /**
     * Этот конструктор нужен для воссоздания объекта из базы данных
     */
    public Transaction(UUID id, long timestamp, TransactionType type, double amount, UUID sourceAccountId, UUID destinationAccountId, TransactionStatus status, String failureReason) {
        this.id = id;
        this.timestamp = timestamp;
        this.type = type;
        this.amount = amount;
        this.sourceAccountId = sourceAccountId;
        this.destinationAccountId = destinationAccountId;
        this.status = status;
        this.failureReason = failureReason;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", type=" + type +
                ", amount=" + amount +
                ", sourceAccountId=" + sourceAccountId +
                ", destinationAccountId=" + destinationAccountId +
                ", status=" + status +
                ", failureReason='" + failureReason + '\'' +
                '}';
    }
}
