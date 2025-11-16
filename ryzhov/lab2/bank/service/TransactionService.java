package lab2.bank.service;

import lab2.bank.dao.AccountDao;
import lab2.bank.dao.TransactionDao;
import lab2.bank.model.Transaction;
import lab2.bank.service.observer.EventManager;
import lab2.bank.service.observer.EventType;
import lab2.bank.service.strategy.*;

import java.util.EnumMap;
import java.util.Map;

public class TransactionService {
    private final AccountDao accountDao;
    private final TransactionDao transactionDao;
    private final EventManager eventManager;

    private final Map<Transaction.TransactionType, TransactionStrategy> strategies;

    public TransactionService(AccountDao accountDao, TransactionDao transactionDao, EventManager eventManager) {
        this.accountDao = accountDao;
        this.transactionDao = transactionDao;
        this.eventManager = eventManager;

        this.strategies = new EnumMap<>(Transaction.TransactionType.class);
        this.strategies.put(Transaction.TransactionType.DEPOSIT, new DepositStrategy());
        this.strategies.put(Transaction.TransactionType.WITHDRAW, new WithdrawStrategy());
        this.strategies.put(Transaction.TransactionType.TRANSFER, new TransferStrategy());
        this.strategies.put(Transaction.TransactionType.FREEZE, new FreezeStrategy());
    }

    public void processTransaction(Transaction transaction) {
        var strategy = strategies.get(transaction.getType());
        if (strategy == null) {
            transaction.markAsFailed("Неизвестный или неподдерживаемый тип транзакции: " + transaction.getType());
            eventManager.notify(EventType.TRANSACTION_FAILURE, transaction);
            transactionDao.save(transaction);
            return;
        }
        try {
            strategy.execute(transaction, accountDao);
            transaction.markAsSuccessful();
            eventManager.notify(EventType.TRANSACTION_SUCCESS, transaction);
        } catch (Exception e) {
            transaction.markAsFailed(e.getMessage());
            eventManager.notify(EventType.TRANSACTION_FAILURE, transaction);
        } finally {
            transactionDao.save(transaction);
        }
    }
}
