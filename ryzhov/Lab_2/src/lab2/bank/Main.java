package lab2.bank;

import lab2.bank.dao.AccountDao;
import lab2.bank.dao.TransactionDao;
import lab2.bank.dao.UserDao;
import lab2.bank.service.AsyncTransactionProcessor;
import lab2.bank.service.DatabaseManager;
import lab2.bank.service.TransactionService;
import lab2.bank.service.observer.EventManager;
import lab2.bank.service.observer.EventType;
import lab2.bank.service.observer.TransactionLogger;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        var dbManager = DatabaseManager.getInstance();
        var userDao = new UserDao(dbManager.getConnection());
        var accountDao = new AccountDao(dbManager.getConnection());
        var transactionDao = new TransactionDao(dbManager.getConnection());
        var eventManager = new EventManager();
        eventManager.subscribe(EventType.TRANSACTION_SUCCESS, new TransactionLogger());
        eventManager.subscribe(EventType.TRANSACTION_FAILURE, new TransactionLogger());
        var transactionService = new TransactionService(accountDao, transactionDao, eventManager);
        var processor = new AsyncTransactionProcessor(4);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nЗавершение работы...");
            processor.shutdown();
            dbManager.closeConnection();
            System.out.println("Ресурсы освобождены.");
        }));
        SwingUtilities.invokeLater(() -> {
            var gui = new BankGUI(userDao, accountDao, transactionDao, transactionService, processor);
            eventManager.subscribe(EventType.TRANSACTION_SUCCESS, gui);
            gui.setVisible(true);
        });
    }
}
