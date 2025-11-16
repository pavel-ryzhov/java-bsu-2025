package lab2.bank.service.strategy;

import lab2.bank.dao.AccountDao;
import lab2.bank.model.Transaction;

public class WithdrawStrategy implements TransactionStrategy {

    @Override
    public void execute(Transaction transaction, AccountDao accountDao) {
        var account = accountDao.findById(transaction.getSourceAccountId())
                .orElseThrow(() -> new IllegalStateException("Счет для снятия не найден."));
        account.lock();
        try {
            if (account.isFrozen()) {
                throw new IllegalStateException("Невозможно снять средства с замороженного счета.");
            }
            if (account.getBalance() < transaction.getAmount()) {
                throw new IllegalStateException("Недостаточно средств на счете.");
            }
            double currentBalance = account.getBalance();
            double newBalance = currentBalance - transaction.getAmount();
            account.setBalance(newBalance);
            accountDao.update(account);
        } finally {
            account.unlock();
        }
    }
}