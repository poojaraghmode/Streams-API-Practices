package com.github.streams.interview.concurrency;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Interview Question: Producer-Consumer Pattern Implementation
 * 
 * Problem: Implement the Producer-Consumer pattern using different synchronization mechanisms:
 * 1. Using BlockingQueue
 * 2. Using wait/notify
 * 3. Using Semaphores
 * 4. Using CompletableFuture
 * 
 * This tests understanding of:
 * - Thread synchronization
 * - Concurrent collections
 * - Producer-Consumer problem
 * - Different concurrency utilities
 */
class ProducerConsumerPattern {

    /**
     * Solution 1: Using BlockingQueue (Recommended approach)
     * BlockingQueue handles all synchronization internally
     */
    static class BlockingQueueSolution {
        private final BlockingQueue<Integer> queue;
        private final AtomicInteger producedCount = new AtomicInteger(0);
        private final AtomicInteger consumedCount = new AtomicInteger(0);
        private volatile boolean shouldStop = false;

        public BlockingQueueSolution(int capacity) {
            this.queue = new ArrayBlockingQueue<>(capacity);
        }

        class Producer implements Runnable {
            private final int itemsToProduce;

            public Producer(int itemsToProduce) {
                this.itemsToProduce = itemsToProduce;
            }

            @Override
            public void run() {
                try {
                    for (int i = 0; i < itemsToProduce; i++) {
                        int item = i + 1;
                        queue.put(item); // Blocks if queue is full
                        producedCount.incrementAndGet();
                        System.out.println("Produced: " + item + " (Queue size: " + queue.size() + ")");
                        Thread.sleep(100); // Simulate work
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        class Consumer implements Runnable {
            private final int itemsToConsume;

            public Consumer(int itemsToConsume) {
                this.itemsToConsume = itemsToConsume;
            }

            @Override
            public void run() {
                try {
                    for (int i = 0; i < itemsToConsume; i++) {
                        Integer item = queue.take(); // Blocks if queue is empty
                        consumedCount.incrementAndGet();
                        System.out.println("Consumed: " + item + " (Queue size: " + queue.size() + ")");
                        Thread.sleep(150); // Simulate work
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        public void runSimulation(int numProducers, int numConsumers, int itemsPerProducer) throws InterruptedException {
            ExecutorService executor = Executors.newFixedThreadPool(numProducers + numConsumers);

            // Start producers
            for (int i = 0; i < numProducers; i++) {
                executor.submit(new Producer(itemsPerProducer));
            }

            // Start consumers
            int totalItems = numProducers * itemsPerProducer;
            int itemsPerConsumer = totalItems / numConsumers;
            for (int i = 0; i < numConsumers; i++) {
                executor.submit(new Consumer(itemsPerConsumer));
            }

            executor.shutdown();
            executor.awaitTermination(30, TimeUnit.SECONDS);
        }

        public int getProducedCount() { return producedCount.get(); }
        public int getConsumedCount() { return consumedCount.get(); }
    }

    /**
     * Solution 2: Using wait/notify with synchronized blocks
     * Manual synchronization - more complex but educational
     */
    static class WaitNotifySolution {
        private final List<Integer> buffer;
        private final int capacity;
        private final AtomicInteger producedCount = new AtomicInteger(0);
        private final AtomicInteger consumedCount = new AtomicInteger(0);

        public WaitNotifySolution(int capacity) {
            this.capacity = capacity;
            this.buffer = new ArrayList<>();
        }

        class Producer implements Runnable {
            private final int itemsToProduce;

            public Producer(int itemsToProduce) {
                this.itemsToProduce = itemsToProduce;
            }

            @Override
            public void run() {
                try {
                    for (int i = 0; i < itemsToProduce; i++) {
                        int item = i + 1;
                        synchronized (buffer) {
                            // Wait while buffer is full
                            while (buffer.size() >= capacity) {
                                buffer.wait();
                            }
                            
                            buffer.add(item);
                            producedCount.incrementAndGet();
                            System.out.println("Produced (wait/notify): " + item + " (Buffer size: " + buffer.size() + ")");
                            
                            // Notify waiting consumers
                            buffer.notifyAll();
                        }
                        Thread.sleep(100);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        class Consumer implements Runnable {
            private final int itemsToConsume;

            public Consumer(int itemsToConsume) {
                this.itemsToConsume = itemsToConsume;
            }

            @Override
            public void run() {
                try {
                    for (int i = 0; i < itemsToConsume; i++) {
                        synchronized (buffer) {
                            // Wait while buffer is empty
                            while (buffer.isEmpty()) {
                                buffer.wait();
                            }
                            
                            Integer item = buffer.remove(0);
                            consumedCount.incrementAndGet();
                            System.out.println("Consumed (wait/notify): " + item + " (Buffer size: " + buffer.size() + ")");
                            
                            // Notify waiting producers
                            buffer.notifyAll();
                        }
                        Thread.sleep(150);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        public void runSimulation(int numProducers, int numConsumers, int itemsPerProducer) throws InterruptedException {
            ExecutorService executor = Executors.newFixedThreadPool(numProducers + numConsumers);

            for (int i = 0; i < numProducers; i++) {
                executor.submit(new Producer(itemsPerProducer));
            }

            int totalItems = numProducers * itemsPerProducer;
            int itemsPerConsumer = totalItems / numConsumers;
            for (int i = 0; i < numConsumers; i++) {
                executor.submit(new Consumer(itemsPerConsumer));
            }

            executor.shutdown();
            executor.awaitTermination(30, TimeUnit.SECONDS);
        }

        public int getProducedCount() { return producedCount.get(); }
        public int getConsumedCount() { return consumedCount.get(); }
    }

    /**
     * Solution 3: Using Semaphores
     * Demonstrates explicit resource counting
     */
    static class SemaphoreSolution {
        private final List<Integer> buffer;
        private final Semaphore fullSlots; // Counts available items for consumers
        private final Semaphore emptySlots; // Counts available space for producers
        private final Object mutex = new Object(); // For mutual exclusion
        private final AtomicInteger producedCount = new AtomicInteger(0);
        private final AtomicInteger consumedCount = new AtomicInteger(0);

        public SemaphoreSolution(int capacity) {
            this.buffer = new ArrayList<>();
            this.fullSlots = new Semaphore(0); // Initially no items
            this.emptySlots = new Semaphore(capacity); // Initially all slots empty
        }

        class Producer implements Runnable {
            private final int itemsToProduce;

            public Producer(int itemsToProduce) {
                this.itemsToProduce = itemsToProduce;
            }

            @Override
            public void run() {
                try {
                    for (int i = 0; i < itemsToProduce; i++) {
                        int item = i + 1;
                        
                        emptySlots.acquire(); // Wait for empty slot
                        
                        synchronized (mutex) {
                            buffer.add(item);
                            producedCount.incrementAndGet();
                            System.out.println("Produced (semaphore): " + item + " (Buffer size: " + buffer.size() + ")");
                        }
                        
                        fullSlots.release(); // Signal that item is available
                        Thread.sleep(100);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        class Consumer implements Runnable {
            private final int itemsToConsume;

            public Consumer(int itemsToConsume) {
                this.itemsToConsume = itemsToConsume;
            }

            @Override
            public void run() {
                try {
                    for (int i = 0; i < itemsToConsume; i++) {
                        fullSlots.acquire(); // Wait for available item
                        
                        Integer item;
                        synchronized (mutex) {
                            item = buffer.remove(0);
                            consumedCount.incrementAndGet();
                            System.out.println("Consumed (semaphore): " + item + " (Buffer size: " + buffer.size() + ")");
                        }
                        
                        emptySlots.release(); // Signal that slot is empty
                        Thread.sleep(150);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        public void runSimulation(int numProducers, int numConsumers, int itemsPerProducer) throws InterruptedException {
            ExecutorService executor = Executors.newFixedThreadPool(numProducers + numConsumers);

            for (int i = 0; i < numProducers; i++) {
                executor.submit(new Producer(itemsPerProducer));
            }

            int totalItems = numProducers * itemsPerProducer;
            int itemsPerConsumer = totalItems / numConsumers;
            for (int i = 0; i < numConsumers; i++) {
                executor.submit(new Consumer(itemsPerConsumer));
            }

            executor.shutdown();
            executor.awaitTermination(30, TimeUnit.SECONDS);
        }

        public int getProducedCount() { return producedCount.get(); }
        public int getConsumedCount() { return consumedCount.get(); }
    }

    /**
     * Solution 4: Using CompletableFuture (Modern approach)
     * Demonstrates reactive/async programming
     */
    static class CompletableFutureSolution {
        private final BlockingQueue<Integer> queue;
        private final AtomicInteger producedCount = new AtomicInteger(0);
        private final AtomicInteger consumedCount = new AtomicInteger(0);

        public CompletableFutureSolution(int capacity) {
            this.queue = new ArrayBlockingQueue<>(capacity);
        }

        public CompletableFuture<Void> produceAsync(int itemsToProduce) {
            return CompletableFuture.runAsync(() -> {
                try {
                    for (int i = 0; i < itemsToProduce; i++) {
                        int item = i + 1;
                        queue.put(item);
                        producedCount.incrementAndGet();
                        System.out.println("Produced (async): " + item);
                        Thread.sleep(100);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        public CompletableFuture<Void> consumeAsync(int itemsToConsume) {
            return CompletableFuture.runAsync(() -> {
                try {
                    for (int i = 0; i < itemsToConsume; i++) {
                        Integer item = queue.take();
                        consumedCount.incrementAndGet();
                        System.out.println("Consumed (async): " + item);
                        Thread.sleep(150);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        public void runSimulation(int numProducers, int numConsumers, int itemsPerProducer) {
            List<CompletableFuture<Void>> futures = new ArrayList<>();

            // Start producers
            for (int i = 0; i < numProducers; i++) {
                futures.add(produceAsync(itemsPerProducer));
            }

            // Start consumers
            int totalItems = numProducers * itemsPerProducer;
            int itemsPerConsumer = totalItems / numConsumers;
            for (int i = 0; i < numConsumers; i++) {
                futures.add(consumeAsync(itemsPerConsumer));
            }

            // Wait for all to complete
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        }

        public int getProducedCount() { return producedCount.get(); }
        public int getConsumedCount() { return consumedCount.get(); }
    }

    @Test
    @Disabled
    void testBlockingQueueSolution() throws InterruptedException {
        BlockingQueueSolution solution = new BlockingQueueSolution(5);
        solution.runSimulation(2, 2, 5);
        
        Assertions.assertEquals(10, solution.getProducedCount());
        Assertions.assertEquals(10, solution.getConsumedCount());
    }

    @Test
    @Disabled
    void testWaitNotifySolution() throws InterruptedException {
        WaitNotifySolution solution = new WaitNotifySolution(5);
        solution.runSimulation(2, 2, 5);
        
        Assertions.assertEquals(10, solution.getProducedCount());
        Assertions.assertEquals(10, solution.getConsumedCount());
    }

    @Test
    @Disabled
    void testSemaphoreSolution() throws InterruptedException {
        SemaphoreSolution solution = new SemaphoreSolution(5);
        solution.runSimulation(2, 2, 5);
        
        Assertions.assertEquals(10, solution.getProducedCount());
        Assertions.assertEquals(10, solution.getConsumedCount());
    }

    @Test
    @Disabled
    void testCompletableFutureSolution() {
        CompletableFutureSolution solution = new CompletableFutureSolution(5);
        solution.runSimulation(2, 2, 5);
        
        Assertions.assertEquals(10, solution.getProducedCount());
        Assertions.assertEquals(10, solution.getConsumedCount());
    }
}