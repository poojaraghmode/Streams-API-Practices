package com.github.streams.interview.collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Interview Question: Find duplicate elements in an array
 * 
 * Problem: Given an array of integers, find all duplicate elements and return them.
 * 
 * Example:
 * Input: [1, 2, 3, 4, 2, 5, 6, 3, 7, 8, 1]
 * Output: [1, 2, 3]
 * 
 * Follow-up: What if you need to find the frequency of each duplicate?
 */
class DuplicateElementsInArray {

    /**
     * Solution 1: Using HashSet to track seen elements
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static List<Integer> findDuplicatesUsingSet(int[] arr) {
        Set<Integer> seen = new HashSet<>();
        Set<Integer> duplicates = new LinkedHashSet<>();
        
        for (int num : arr) {
            if (!seen.add(num)) {
                duplicates.add(num);
            }
        }
        
        return new ArrayList<>(duplicates);
    }

    /**
     * Solution 2: Using Java 8 Streams
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static List<Integer> findDuplicatesUsingStreams(int[] arr) {
        return Arrays.stream(arr)
                .boxed()
                .collect(Collectors.groupingBy(
                    num -> num,
                    Collectors.counting()
                ))
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() > 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * Solution 3: Using frequency map to get duplicates with their counts
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static Map<Integer, Long> findDuplicatesWithFrequency(int[] arr) {
        return Arrays.stream(arr)
                .boxed()
                .collect(Collectors.groupingBy(
                    num -> num,
                    Collectors.counting()
                ))
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() > 1)
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue
                ));
    }

    @Test
    @Disabled
    void testFindDuplicates() {
        int[] input = {1, 2, 3, 4, 2, 5, 6, 3, 7, 8, 1};
        
        List<Integer> result1 = findDuplicatesUsingSet(input);
        List<Integer> result2 = findDuplicatesUsingStreams(input);
        
        List<Integer> expected = Arrays.asList(2, 3, 1);
        
        Assertions.assertTrue(result1.containsAll(expected) && expected.containsAll(result1));
        Assertions.assertTrue(result2.containsAll(expected) && expected.containsAll(result2));
        
        // Test frequency solution
        Map<Integer, Long> frequencies = findDuplicatesWithFrequency(input);
        Assertions.assertEquals(2L, frequencies.get(1));
        Assertions.assertEquals(2L, frequencies.get(2));
        Assertions.assertEquals(2L, frequencies.get(3));
    }
}