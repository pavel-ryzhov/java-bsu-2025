package lab2.bank.model;

import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Account {
    private final UUID id;
    private final UUID userId;
    private double balance;
    private boolean isFrozen;
    private final Lock lock = new ReentrantLock();

    public Account(UUID id, UUID userId, double balance, boolean isFrozen) {
        this.id = id;
        this.userId = userId;
        this.balance = balance;
        this.isFrozen = isFrozen;
    }

    public Account(UUID userId) {
        this(UUID.randomUUID(), userId, 0, false);
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public boolean isFrozen() {
        return isFrozen;
    }

    public void setFrozen(boolean frozen) {
        isFrozen = frozen;
    }

    public Lock getLock() {
        return lock;
    }

    public void lock() {
        lock.lock();
    }

    public void unlock() {
        lock.unlock();
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", userId=" + userId +
                ", balance=" + balance +
                ", isFrozen=" + isFrozen +
                ", lock=" + lock +
                '}';
    }
}