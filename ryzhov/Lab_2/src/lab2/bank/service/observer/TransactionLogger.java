package lab2.bank.service.observer;

import lab2.bank.model.Transaction;

public class TransactionLogger implements Listener {
    @Override
    public void update(EventType eventType, Transaction transaction) {
        System.out.println();
        System.out.printf("[TRANSACTION LOG | %s]%n", eventType.name());
        System.out.printf("  - Transaction ID: %s%n", transaction.getId());
        System.out.printf("  - Type: %s, Amount: %.2f%n", transaction.getType(), transaction.getAmount());
        System.out.printf("  - Status: %s%n", transaction.getStatus());
        if (eventType == EventType.TRANSACTION_FAILURE) {
            System.out.printf("  - Failure Reason: %s%n", transaction.getFailureReason());
        }
        System.out.println("--------------------------------------------------");
    }
}
