package lab2.bank.service.strategy;

import lab2.bank.dao.AccountDao;
import lab2.bank.model.Transaction;

public interface TransactionStrategy {
    void execute(Transaction transaction, AccountDao accountDao);
}
