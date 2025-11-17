package lab2.bank.service.strategy;

import lab2.bank.dao.AccountDao;
import lab2.bank.model.Transaction;

public class TransferStrategy implements TransactionStrategy {

    @Override
    public void execute(Transaction transaction, AccountDao accountDao) {
        var sourceId = transaction.getSourceAccountId();
        var destId = transaction.getDestinationAccountId();
        double amount = transaction.getAmount();

        // --- ЗАЩИТА ОТ DEADLOCK!!!!!!!!!!! ---
        // Определяем строгий порядок блокировки счетов
        // Это гарантирует, что любой поток, переводящий деньги между этими двумя счетами,
        // будет пытаться захватить блокировки в фиксированном порядке
        boolean sourceIsFirst = sourceId.compareTo(destId) < 0;

        var firstId = sourceIsFirst ? sourceId : destId;
        var secondId = sourceIsFirst ? destId : sourceId;

        var firstAccount = accountDao.findById(firstId)
                .orElseThrow(() -> new IllegalStateException("Один из счетов не найден."));
        var secondAccount = accountDao.findById(secondId)
                .orElseThrow(() -> new IllegalStateException("Один из счетов не найден."));

        firstAccount.lock();
        secondAccount.lock();
        try {
            var sourceAccount = sourceIsFirst ? firstAccount : secondAccount;
            var destAccount = sourceIsFirst ? secondAccount : firstAccount;
            if (sourceAccount.isFrozen() || destAccount.isFrozen()) {
                throw new IllegalStateException("Один из счетов заморожен, перевод невозможен.");
            }
            if (sourceAccount.getBalance() < amount) {
                throw new IllegalStateException("Недостаточно средств на счете-отправителе.");
            }
            sourceAccount.setBalance(sourceAccount.getBalance() - amount);
            destAccount.setBalance(destAccount.getBalance() + amount);
            accountDao.update(sourceAccount);
            accountDao.update(destAccount);
        } finally {
            secondAccount.unlock();
            firstAccount.unlock();
        }
    }
}
