package lab2.bank.service.observer;

import lab2.bank.model.Transaction;

@FunctionalInterface
public interface Listener {
    void update(EventType type, Transaction transaction);
}
