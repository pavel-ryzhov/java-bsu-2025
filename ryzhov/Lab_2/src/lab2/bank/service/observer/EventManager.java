package lab2.bank.service.observer;

import lab2.bank.model.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.EnumMap;

public class EventManager {
    private final Map<EventType, List<Listener>> listeners = new EnumMap<>(EventType.class);

    public synchronized void subscribe(EventType eventType, Listener listener) {
        this.listeners.computeIfAbsent(eventType, _ -> new ArrayList<>()).add(listener);
    }

    public synchronized void unsubscribe(EventType eventType, Listener listener) {
        var eventListeners = this.listeners.get(eventType);
        if (eventListeners != null) {
            eventListeners.remove(listener);
        }
    }

    public void notify(EventType eventType, Transaction transaction) {
        List<Listener> listenersToNotify;
        //Это если какой-то гений решит отписаться прямо во время получения уведомления
        synchronized (this) {
            var eventListeners = this.listeners.get(eventType);
            if (eventListeners == null || eventListeners.isEmpty()) {
                return;
            }
            listenersToNotify = new ArrayList<>(eventListeners);
        }
        listenersToNotify.forEach(l -> l.update(eventType, transaction));
    }
}
