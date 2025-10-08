package com.github.streams.interview.algorithms;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;

import java.util.*;
import java.util.stream.IntStream;

/**
 * Interview Question: Sliding Window Maximum
 * 
 * Problem: Given an array of integers and a window size k, find the maximum element
 * in each sliding window of size k.
 * 
 * Example:
 * Input: nums = [1,3,-1,-3,5,3,6,7], k = 3
 * Output: [3,3,5,5,6,7]
 * 
 * Explanation:
 * Window [1,3,-1] -> max = 3
 * Window [3,-1,-3] -> max = 3
 * Window [-1,-3,5] -> max = 5
 * Window [-3,5,3] -> max = 5
 * Window [5,3,6] -> max = 6
 * Window [3,6,7] -> max = 7
 * 
 * This tests understanding of:
 * - Sliding window technique
 * - Deque data structure
 * - Time complexity optimization
 * - Stream API usage
 */
class SlidingWindowMaximum {

    /**
     * Solution 1: Brute Force Approach
     * Time Complexity: O(n*k)
     * Space Complexity: O(1)
     */
    public static int[] maxSlidingWindowBruteForce(int[] nums, int k) {
        if (nums == null || nums.length == 0 || k <= 0) {
            return new int[0];
        }

        int n = nums.length;
        int[] result = new int[n - k + 1];

        for (int i = 0; i <= n - k; i++) {
            int max = nums[i];
            for (int j = i + 1; j < i + k; j++) {
                max = Math.max(max, nums[j]);
            }
            result[i] = max;
        }

        return result;
    }

    /**
     * Solution 2: Using Deque (Optimal)
     * Time Complexity: O(n)
     * Space Complexity: O(k)
     */
    public static int[] maxSlidingWindowDeque(int[] nums, int k) {
        if (nums == null || nums.length == 0 || k <= 0) {
            return new int[0];
        }

        int n = nums.length;
        int[] result = new int[n - k + 1];
        Deque<Integer> deque = new ArrayDeque<>(); // Store indices

        for (int i = 0; i < n; i++) {
            // Remove indices that are out of current window
            while (!deque.isEmpty() && deque.peekFirst() < i - k + 1) {
                deque.pollFirst();
            }

            // Remove indices whose corresponding values are smaller than current element
            while (!deque.isEmpty() && nums[deque.peekLast()] < nums[i]) {
                deque.pollLast();
            }

            deque.addLast(i);

            // Add to result if we have processed at least k elements
            if (i >= k - 1) {
                result[i - k + 1] = nums[deque.peekFirst()];
            }
        }

        return result;
    }

    /**
     * Solution 3: Using TreeMap (for maintaining sorted order)
     * Time Complexity: O(n log k)
     * Space Complexity: O(k)
     */
    public static int[] maxSlidingWindowTreeMap(int[] nums, int k) {
        if (nums == null || nums.length == 0 || k <= 0) {
            return new int[0];
        }

        int n = nums.length;
        int[] result = new int[n - k + 1];
        TreeMap<Integer, Integer> window = new TreeMap<>();

        // Initialize first window
        for (int i = 0; i < k; i++) {
            window.put(nums[i], window.getOrDefault(nums[i], 0) + 1);
        }
        result[0] = window.lastKey();

        // Slide the window
        for (int i = k; i < n; i++) {
            // Remove leftmost element
            int leftmost = nums[i - k];
            window.put(leftmost, window.get(leftmost) - 1);
            if (window.get(leftmost) == 0) {
                window.remove(leftmost);
            }

            // Add new element
            window.put(nums[i], window.getOrDefault(nums[i], 0) + 1);

            // Maximum is the last key
            result[i - k + 1] = window.lastKey();
        }

        return result;
    }

    /**
     * Solution 4: Using Java 8 Streams (Less efficient but demonstrates Stream usage)
     * Time Complexity: O(n*k)
     * Space Complexity: O(k)
     */
    public static int[] maxSlidingWindowStream(int[] nums, int k) {
        if (nums == null || nums.length == 0 || k <= 0) {
            return new int[0];
        }

        return IntStream.rangeClosed(0, nums.length - k)
                .map(i -> IntStream.range(i, i + k)
                        .map(j -> nums[j])
                        .max()
                        .orElse(Integer.MIN_VALUE))
                .toArray();
    }

    /**
     * Solution 5: Using PriorityQueue (Max Heap)
     * Time Complexity: O(n log k)
     * Space Complexity: O(k)
     */
    public static int[] maxSlidingWindowHeap(int[] nums, int k) {
        if (nums == null || nums.length == 0 || k <= 0) {
            return new int[0];
        }

        int n = nums.length;
        int[] result = new int[n - k + 1];
        
        // Max heap storing {value, index} pairs
        PriorityQueue<int[]> maxHeap = new PriorityQueue<>((a, b) -> {
            if (a[0] != b[0]) return Integer.compare(b[0], a[0]); // Max heap by value
            return Integer.compare(b[1], a[1]); // If values equal, prefer later index
        });

        // Initialize first window
        for (int i = 0; i < k; i++) {
            maxHeap.offer(new int[]{nums[i], i});
        }
        result[0] = maxHeap.peek()[0];

        // Slide the window
        for (int i = k; i < n; i++) {
            // Add new element
            maxHeap.offer(new int[]{nums[i], i});

            // Remove elements outside current window
            while (!maxHeap.isEmpty() && maxHeap.peek()[1] <= i - k) {
                maxHeap.poll();
            }

            // Current maximum
            result[i - k + 1] = maxHeap.peek()[0];
        }

        return result;
    }

    /**
     * Related Problem: Sliding Window Minimum
     */
    public static int[] minSlidingWindow(int[] nums, int k) {
        if (nums == null || nums.length == 0 || k <= 0) {
            return new int[0];
        }

        int n = nums.length;
        int[] result = new int[n - k + 1];
        Deque<Integer> deque = new ArrayDeque<>();

        for (int i = 0; i < n; i++) {
            // Remove indices out of window
            while (!deque.isEmpty() && deque.peekFirst() < i - k + 1) {
                deque.pollFirst();
            }

            // Remove indices whose values are greater than current (for minimum)
            while (!deque.isEmpty() && nums[deque.peekLast()] > nums[i]) {
                deque.pollLast();
            }

            deque.addLast(i);

            if (i >= k - 1) {
                result[i - k + 1] = nums[deque.peekFirst()];
            }
        }

        return result;
    }

    /**
     * Related Problem: First negative number in each window
     */
    public static int[] firstNegativeInWindow(int[] nums, int k) {
        if (nums == null || nums.length == 0 || k <= 0) {
            return new int[0];
        }

        int n = nums.length;
        int[] result = new int[n - k + 1];
        Queue<Integer> negativeIndices = new LinkedList<>();

        // Process first window
        for (int i = 0; i < k; i++) {
            if (nums[i] < 0) {
                negativeIndices.offer(i);
            }
        }
        result[0] = negativeIndices.isEmpty() ? 0 : nums[negativeIndices.peek()];

        // Process remaining windows
        for (int i = k; i < n; i++) {
            // Remove indices outside current window
            while (!negativeIndices.isEmpty() && negativeIndices.peek() <= i - k) {
                negativeIndices.poll();
            }

            // Add current element if negative
            if (nums[i] < 0) {
                negativeIndices.offer(i);
            }

            // First negative in current window
            result[i - k + 1] = negativeIndices.isEmpty() ? 0 : nums[negativeIndices.peek()];
        }

        return result;
    }

    /**
     * Utility method to measure execution time
     */
    private static long measureTime(Runnable operation) {
        long start = System.nanoTime();
        operation.run();
        return System.nanoTime() - start;
    }

    @Test
    @Disabled
    void testBasicFunctionality() {
        int[] nums = {1, 3, -1, -3, 5, 3, 6, 7};
        int k = 3;
        int[] expected = {3, 3, 5, 5, 6, 7};

        // Test all solutions
        Assertions.assertArrayEquals(expected, maxSlidingWindowBruteForce(nums, k));
        Assertions.assertArrayEquals(expected, maxSlidingWindowDeque(nums, k));
        Assertions.assertArrayEquals(expected, maxSlidingWindowTreeMap(nums, k));
        Assertions.assertArrayEquals(expected, maxSlidingWindowStream(nums, k));
        Assertions.assertArrayEquals(expected, maxSlidingWindowHeap(nums, k));
    }

    @Test
    @Disabled
    void testEdgeCases() {
        // Empty array
        Assertions.assertArrayEquals(new int[0], maxSlidingWindowDeque(new int[0], 3));
        Assertions.assertArrayEquals(new int[0], maxSlidingWindowDeque(null, 3));

        // Window size equals array length
        int[] nums = {1, 2, 3, 4, 5};
        int[] expected = {5};
        Assertions.assertArrayEquals(expected, maxSlidingWindowDeque(nums, 5));

        // Window size is 1
        expected = new int[]{1, 2, 3, 4, 5};
        Assertions.assertArrayEquals(expected, maxSlidingWindowDeque(nums, 1));

        // All elements are same
        int[] sameElements = {7, 7, 7, 7};
        expected = new int[]{7, 7};
        Assertions.assertArrayEquals(expected, maxSlidingWindowDeque(sameElements, 3));

        // Decreasing array
        int[] decreasing = {5, 4, 3, 2, 1};
        expected = new int[]{5, 4, 3};
        Assertions.assertArrayEquals(expected, maxSlidingWindowDeque(decreasing, 3));

        // Increasing array
        int[] increasing = {1, 2, 3, 4, 5};
        expected = new int[]{3, 4, 5};
        Assertions.assertArrayEquals(expected, maxSlidingWindowDeque(increasing, 3));
    }

    @Test
    @Disabled
    void testRelatedProblems() {
        int[] nums = {1, 3, -1, -3, 5, 3, 6, 7};
        int k = 3;

        // Test sliding window minimum
        int[] expectedMin = {-1, -3, -3, -3, 3, 3};
        Assertions.assertArrayEquals(expectedMin, minSlidingWindow(nums, k));

        // Test first negative in window
        int[] numsWithNegatives = {12, -1, -7, 8, -15, 30, 16, 28};
        int[] expectedNegative = {-1, -1, -7, -15, -15, 0};
        Assertions.assertArrayEquals(expectedNegative, firstNegativeInWindow(numsWithNegatives, 3));
    }

    @Test
    @Disabled
    void testPerformanceComparison() {
        // Generate large test array
        int[] largeArray = IntStream.range(1, 10001).toArray();
        int k = 100;

        System.out.println("Performance comparison for array size: " + largeArray.length + ", window size: " + k);

        // Test deque solution (should be fastest)
        long dequeTime = measureTime(() -> maxSlidingWindowDeque(largeArray, k));
        System.out.println("Deque solution: " + dequeTime / 1_000_000 + " ms");

        // Test TreeMap solution
        long treeMapTime = measureTime(() -> maxSlidingWindowTreeMap(largeArray, k));
        System.out.println("TreeMap solution: " + treeMapTime / 1_000_000 + " ms");

        // Test Heap solution
        long heapTime = measureTime(() -> maxSlidingWindowHeap(largeArray, k));
        System.out.println("Heap solution: " + heapTime / 1_000_000 + " ms");

        // Verify all solutions give same result
        int[] dequeResult = maxSlidingWindowDeque(largeArray, k);
        int[] treeMapResult = maxSlidingWindowTreeMap(largeArray, k);
        int[] heapResult = maxSlidingWindowHeap(largeArray, k);

        Assertions.assertArrayEquals(dequeResult, treeMapResult);
        Assertions.assertArrayEquals(dequeResult, heapResult);

        // Deque should be the fastest
        Assertions.assertTrue(dequeTime <= treeMapTime);
        Assertions.assertTrue(dequeTime <= heapTime);
    }

    @Test
    @Disabled
    void testLargeNumbers() {
        int[] nums = {Integer.MAX_VALUE, Integer.MIN_VALUE, 0, -1, 1};
        int k = 3;
        int[] expected = {Integer.MAX_VALUE, Integer.MAX_VALUE, 1};
        
        Assertions.assertArrayEquals(expected, maxSlidingWindowDeque(nums, k));
    }
}