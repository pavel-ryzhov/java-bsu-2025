package lab2.bank.service.command;

import lab2.bank.model.Transaction;
import lab2.bank.service.TransactionService;

public class ProcessTransactionCommand implements Command {

    private final Transaction transaction;

    private final TransactionService transactionService;

    public ProcessTransactionCommand(Transaction transaction, TransactionService transactionService) {
        this.transaction = transaction;
        this.transactionService = transactionService;
    }

    @Override
    public void execute() {
        transactionService.processTransaction(transaction);
    }
}
