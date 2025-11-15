package com.stablest.web_crawler.service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class WorkerService {
    final private ExecutorService workerPool;
    final private ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
    private static WorkerService INSTANCE;

    private WorkerService(int numberOfThreads) {
        this.workerPool = Executors.newFixedThreadPool(numberOfThreads);
        INSTANCE = this;
    }

    static public void createWorkers(int numberOfThreads) {
        if (INSTANCE == null) {
            new WorkerService(numberOfThreads);
        }
    }
    static public WorkerService getInstance() {
        return INSTANCE;
    }

    public ExecutorService getWorkerPool() { return workerPool; }

    public <T> CompletableFuture<T> createAsyncTask(Supplier<T> fn) {
        return CompletableFuture.supplyAsync(fn, workerPool);
    }

    public CompletableFuture<Void> createAsyncTask(Runnable fn) {
        return CompletableFuture.runAsync(fn, workerPool);
    }

    public void next() {

    }

    public void shutdown() {
        workerPool.shutdown();
    }
}
