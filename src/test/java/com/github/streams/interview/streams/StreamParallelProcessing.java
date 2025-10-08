package com.github.streams.interview.streams;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;

import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Interview Question: Stream Parallel Processing
 * 
 * Problem: Demonstrate understanding of parallel streams, their benefits,
 * pitfalls, and when to use them effectively.
 * 
 * This tests understanding of:
 * - Parallel vs Sequential streams
 * - Fork-Join framework
 * - Thread safety in parallel operations
 * - Performance considerations
 * - Custom thread pools
 */
class StreamParallelProcessing {

    /**
     * Example 1: Basic parallel stream operations
     */
    public static long sumUsingParallelStream(List<Integer> numbers) {
        return numbers.parallelStream()
                .mapToLong(Integer::longValue)
                .sum();
    }

    public static long sumUsingSequentialStream(List<Integer> numbers) {
        return numbers.stream()
                .mapToLong(Integer::longValue)
                .sum();
    }

    /**
     * Example 2: Performance comparison - CPU intensive task
     */
    public static List<Long> factorialParallel(List<Integer> numbers) {
        return numbers.parallelStream()
                .map(StreamParallelProcessing::factorial)
                .collect(Collectors.toList());
    }

    public static List<Long> factorialSequential(List<Integer> numbers) {
        return numbers.stream()
                .map(StreamParallelProcessing::factorial)
                .collect(Collectors.toList());
    }

    private static long factorial(int n) {
        if (n <= 1) return 1;
        return IntStream.rangeClosed(2, n)
                .reduce(1, (a, b) -> a * b);
    }

    /**
     * Example 3: Parallel stream with custom thread pool
     */
    public static <T> List<T> processWithCustomThreadPool(List<T> items, 
                                                         int threadPoolSize,
                                                         java.util.function.Function<T, T> processor) {
        ForkJoinPool customThreadPool = new ForkJoinPool(threadPoolSize);
        try {
            return customThreadPool.submit(() ->
                items.parallelStream()
                     .map(processor)
                     .collect(Collectors.toList())
            ).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            customThreadPool.shutdown();
        }
    }

    /**
     * Example 4: Parallel reduction with combiner
     */
    public static String concatenateStringsParallel(List<String> strings) {
        return strings.parallelStream()
                .reduce("",
                    (partial, element) -> partial + element, // accumulator
                    (partial1, partial2) -> partial1 + partial2 // combiner
                );
    }

    /**
     * Example 5: Parallel grouping operation
     */
    public static Map<Integer, List<Integer>> groupByModuloParallel(List<Integer> numbers, int modulo) {
        return numbers.parallelStream()
                .collect(Collectors.groupingBy(n -> n % modulo));
    }

    /**
     * Example 6: Finding elements in parallel with short-circuiting
     */
    public static OptionalInt findFirstLargeNumberParallel(List<Integer> numbers, int threshold) {
        return numbers.parallelStream()
                .mapToInt(Integer::intValue)
                .filter(n -> n > threshold)
                .findFirst(); // Note: findFirst may not be efficient with parallel streams
    }

    public static OptionalInt findAnyLargeNumberParallel(List<Integer> numbers, int threshold) {
        return numbers.parallelStream()
                .mapToInt(Integer::intValue)
                .filter(n -> n > threshold)
                .findAny(); // findAny is more efficient with parallel streams
    }

    /**
     * Example 7: Thread safety issues demonstration
     */
    static class Counter {
        private int count = 0;
        
        public void increment() {
            count++; // Not thread-safe!
        }
        
        public int getCount() {
            return count;
        }
    }

    public static int demonstrateThreadSafetyIssue(List<Integer> numbers) {
        Counter counter = new Counter();
        
        // This will likely produce incorrect results due to race conditions
        numbers.parallelStream()
                .forEach(n -> counter.increment());
        
        return counter.getCount();
    }

    /**
     * Example 8: Correct way to handle stateful operations
     */
    public static int countElementsCorrectly(List<Integer> numbers, int threshold) {
        return (int) numbers.parallelStream()
                .filter(n -> n > threshold)
                .count(); // count() is thread-safe
    }

    /**
     * Example 9: Parallel stream with side effects (bad practice)
     */
    public static List<Integer> badSideEffectExample(List<Integer> numbers) {
        List<Integer> results = new ArrayList<>(); // Not thread-safe!
        
        numbers.parallelStream()
                .filter(n -> n % 2 == 0)
                .forEach(results::add); // Race condition!
        
        return results;
    }

    /**
     * Example 10: Correct way to collect results from parallel stream
     */
    public static List<Integer> goodCollectionExample(List<Integer> numbers) {
        return numbers.parallelStream()
                .filter(n -> n % 2 == 0)
                .collect(Collectors.toList()); // Thread-safe
    }

    /**
     * Example 11: Performance measurement utility
     */
    public static <T> long measureExecutionTime(java.util.function.Supplier<T> operation) {
        long startTime = System.nanoTime();
        operation.get();
        long endTime = System.nanoTime();
        return endTime - startTime;
    }

    /**
     * Example 12: When NOT to use parallel streams
     */
    public static List<String> processSmallListSequentially(List<String> smallList) {
        // For small datasets, sequential is often faster due to overhead
        return smallList.stream()
                .map(String::toUpperCase)
                .collect(Collectors.toList());
    }

    public static List<Integer> ioIntensiveOperationSequential(List<String> urls) {
        // I/O intensive operations often don't benefit from parallel streams
        return urls.stream()
                .map(url -> simulateNetworkCall(url))
                .collect(Collectors.toList());
    }

    private static int simulateNetworkCall(String url) {
        try {
            Thread.sleep(100); // Simulate I/O delay
            return url.hashCode();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return 0;
        }
    }

    /**
     * Example 13: Ordered vs Unordered parallel streams
     */
    public static List<Integer> orderedParallelStream(List<Integer> numbers) {
        return numbers.parallelStream()
                .map(n -> n * 2)
                .collect(Collectors.toList()); // Maintains order
    }

    public static List<Integer> unorderedParallelStream(List<Integer> numbers) {
        return numbers.parallelStream()
                .unordered() // Better performance if order doesn't matter
                .map(n -> n * 2)
                .collect(Collectors.toList());
    }

    @Test
    @Disabled
    void testBasicParallelOperations() {
        List<Integer> numbers = IntStream.rangeClosed(1, 1000)
                .boxed()
                .collect(Collectors.toList());

        long parallelSum = sumUsingParallelStream(numbers);
        long sequentialSum = sumUsingSequentialStream(numbers);

        Assertions.assertEquals(sequentialSum, parallelSum);
        Assertions.assertEquals(500500L, parallelSum); // Sum of 1 to 1000
    }

    @Test
    @Disabled
    void testParallelPerformance() {
        List<Integer> numbers = Arrays.asList(10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20);

        // Measure parallel execution time
        long parallelTime = measureExecutionTime(() -> factorialParallel(numbers));
        
        // Measure sequential execution time
        long sequentialTime = measureExecutionTime(() -> factorialSequential(numbers));

        System.out.println("Parallel time: " + parallelTime / 1_000_000 + " ms");
        System.out.println("Sequential time: " + sequentialTime / 1_000_000 + " ms");

        // Results should be the same
        List<Long> parallelResults = factorialParallel(numbers);
        List<Long> sequentialResults = factorialSequential(numbers);
        Assertions.assertEquals(sequentialResults, parallelResults);
    }

    @Test  
    @Disabled
    void testCustomThreadPool() {
        List<String> items = Arrays.asList("a", "b", "c", "d", "e");
        
        List<String> results = processWithCustomThreadPool(items, 2, String::toUpperCase);
        
        Assertions.assertEquals(5, results.size());
        Assertions.assertTrue(results.contains("A"));
        Assertions.assertTrue(results.contains("E"));
    }

    @Test
    @Disabled
    void testParallelReduction() {
        List<String> strings = Arrays.asList("Hello", " ", "World", "!");
        
        String result = concatenateStringsParallel(strings);
        
        Assertions.assertTrue(result.contains("Hello"));
        Assertions.assertTrue(result.contains("World"));
        Assertions.assertEquals(12, result.length());
    }

    @Test
    @Disabled
    void testParallelGrouping() {
        List<Integer> numbers = IntStream.rangeClosed(1, 100)
                .boxed()
                .collect(Collectors.toList());

        Map<Integer, List<Integer>> groups = groupByModuloParallel(numbers, 10);

        Assertions.assertEquals(10, groups.size()); // 0-9 groups
        Assertions.assertEquals(10, groups.get(0).size()); // Numbers ending in 0
        Assertions.assertTrue(groups.get(5).contains(15));
        Assertions.assertTrue(groups.get(5).contains(25));
    }

    @Test
    @Disabled
    void testShortCircuiting() {
        List<Integer> numbers = IntStream.rangeClosed(1, 1000)
                .boxed()
                .collect(Collectors.toList());

        OptionalInt first = findFirstLargeNumberParallel(numbers, 500);
        OptionalInt any = findAnyLargeNumberParallel(numbers, 500);

        Assertions.assertTrue(first.isPresent());
        Assertions.assertTrue(any.isPresent());
        Assertions.assertTrue(first.getAsInt() > 500);
        Assertions.assertTrue(any.getAsInt() > 500);
    }

    @Test
    @Disabled
    void testThreadSafety() {
        List<Integer> numbers = IntStream.rangeClosed(1, 1000)
                .boxed()
                .collect(Collectors.toList());

        // Demonstrate thread safety issue
        int unsafeCount = demonstrateThreadSafetyIssue(numbers);
        // This assertion might fail due to race conditions
        // Assertions.assertEquals(1000, unsafeCount);

        // Correct thread-safe approach
        int safeCount = countElementsCorrectly(numbers, 0);
        Assertions.assertEquals(1000, safeCount);
    }

    @Test
    @Disabled
    void testCollectionStrategies() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        // Bad approach (might have issues with parallel streams)
        List<Integer> badResults = badSideEffectExample(numbers);
        // Size might be incorrect due to race conditions

        // Good approach
        List<Integer> goodResults = goodCollectionExample(numbers);
        Assertions.assertEquals(5, goodResults.size()); // 2, 4, 6, 8, 10
        Assertions.assertTrue(goodResults.contains(2));
        Assertions.assertTrue(goodResults.contains(10));
    }

    @Test
    @Disabled
    void testOrderedVsUnordered() {
        List<Integer> numbers = IntStream.rangeClosed(1, 10)
                .boxed()
                .collect(Collectors.toList());

        List<Integer> ordered = orderedParallelStream(numbers);
        List<Integer> unordered = unorderedParallelStream(numbers);

        // Both should have same elements, but order might differ for unordered
        Assertions.assertEquals(10, ordered.size());
        Assertions.assertEquals(10, unordered.size());
        
        // Ordered should maintain original order
        Assertions.assertEquals(Integer.valueOf(2), ordered.get(0));  // 1 * 2
        Assertions.assertEquals(Integer.valueOf(20), ordered.get(9)); // 10 * 2
    }
}