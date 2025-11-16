package lab2.bank.service.strategy;

import lab2.bank.dao.AccountDao;
import lab2.bank.model.Transaction;

public class DepositStrategy implements TransactionStrategy {

    @Override
    public void execute(Transaction transaction, AccountDao accountDao) {
        var account = accountDao.findById(transaction.getDestinationAccountId())
                .orElseThrow(() -> new IllegalStateException("Счет для пополнения не найден."));
        account.lock(); // Оу да потокобезопасность
        try {
            if (account.isFrozen()) {
                throw new IllegalStateException("Невозможно пополнить замороженный счет.");
            }
            double currentBalance = account.getBalance();
            double newBalance = currentBalance + transaction.getAmount();
            account.setBalance(newBalance);
            accountDao.update(account);
        } finally {
            account.unlock();
        }
    }
}
