package lab2.bank.service;

import lab2.bank.service.command.Command;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class AsyncTransactionProcessor {
    private final BlockingQueue<Command> commandQueue;
    private final ExecutorService executor;
    public AsyncTransactionProcessor(int poolSize) {
        if (poolSize <= 0) {
            throw new IllegalArgumentException("Размер пула потоков должен быть положительным.");
        }
        this.commandQueue = new LinkedBlockingQueue<>();
        this.executor = Executors.newFixedThreadPool(poolSize);
        startWorkers(poolSize);
    }
    private void startWorkers(int workerCount) {
        System.out.printf("Запуск %d рабочих потоков для обработки транзакций...\n", workerCount);
        for (int i = 0; i < workerCount; i++) {
            executor.submit(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        commandQueue.take().execute();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    } catch (Exception e) {
                        System.err.println("Ошибка в рабочем потоке: " + e.getMessage());
                    }
                }
                System.out.println("Рабочий поток завершает работу.");
            });
        }
    }
    public void submit(Command command) {
        try {
            commandQueue.put(command);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Поток-производитель был прерван при добавлении команды в очередь.");
        }
    }
    public void shutdown() {
        System.out.println("Остановка асинхронного обработчика...");
        executor.shutdownNow();
    }
}
