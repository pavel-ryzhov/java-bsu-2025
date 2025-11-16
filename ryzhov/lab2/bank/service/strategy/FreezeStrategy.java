package lab2.bank.service.strategy;

import lab2.bank.dao.AccountDao;
import lab2.bank.model.Transaction;

public class FreezeStrategy implements TransactionStrategy {

    @Override
    public void execute(Transaction transaction, AccountDao accountDao) {
        var account = accountDao.findById(transaction.getSourceAccountId())
                .orElseThrow(() -> new IllegalStateException("Счет для заморозки не найден."));
        account.lock();
        try {
            if (account.isFrozen()) {
                return;
            }
            account.setFrozen(true);
            accountDao.update(account);
        } finally {
            account.unlock();
        }
    }
}
