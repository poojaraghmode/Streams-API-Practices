package com.github.streams.interview.algorithms;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;

import java.util.*;

/**
 * Interview Question: Two Sum Problem
 * 
 * Problem: Given an array of integers and a target sum, find two numbers in the array
 * that add up to the target sum. Return their indices.
 * 
 * Example:
 * Input: nums = [2, 7, 11, 15], target = 9
 * Output: [0, 1] (because nums[0] + nums[1] = 2 + 7 = 9)
 * 
 * Assumptions:
 * - Each input has exactly one solution
 * - You may not use the same element twice
 */
class TwoSum {

    /**
     * Solution 1: Brute Force Approach
     * Time Complexity: O(n²)
     * Space Complexity: O(1)
     */
    public static int[] twoSumBruteForce(int[] nums, int target) {
        for (int i = 0; i < nums.length - 1; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                if (nums[i] + nums[j] == target) {
                    return new int[]{i, j};
                }
            }
        }
        throw new IllegalArgumentException("No two sum solution");
    }

    /**
     * Solution 2: HashMap Approach (Optimal)
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public static int[] twoSumHashMap(int[] nums, int target) {
        Map<Integer, Integer> numToIndex = new HashMap<>();
        
        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            
            if (numToIndex.containsKey(complement)) {
                return new int[]{numToIndex.get(complement), i};
            }
            
            numToIndex.put(nums[i], i);
        }
        
        throw new IllegalArgumentException("No two sum solution");
    }

    /**
     * Solution 3: Two Pointer Approach (requires sorted array)
     * Time Complexity: O(n log n) for sorting + O(n) for two pointers = O(n log n)
     * Space Complexity: O(n) to store original indices
     * 
     * Note: This approach modifies the problem slightly as we need to track original indices
     */
    public static int[] twoSumTwoPointers(int[] nums, int target) {
        // Create array of {value, originalIndex} pairs
        int[][] valueIndexPairs = new int[nums.length][2];
        for (int i = 0; i < nums.length; i++) {
            valueIndexPairs[i][0] = nums[i];
            valueIndexPairs[i][1] = i;
        }
        
        // Sort by value
        Arrays.sort(valueIndexPairs, (a, b) -> Integer.compare(a[0], b[0]));
        
        int left = 0;
        int right = nums.length - 1;
        
        while (left < right) {
            int sum = valueIndexPairs[left][0] + valueIndexPairs[right][0];
            
            if (sum == target) {
                return new int[]{
                    Math.min(valueIndexPairs[left][1], valueIndexPairs[right][1]),
                    Math.max(valueIndexPairs[left][1], valueIndexPairs[right][1])
                };
            } else if (sum < target) {
                left++;
            } else {
                right--;
            }
        }
        
        throw new IllegalArgumentException("No two sum solution");
    }

    /**
     * Follow-up: Find all pairs that sum to target (allowing duplicates)
     */
    public static List<List<Integer>> findAllTwoSumPairs(int[] nums, int target) {
        Map<Integer, List<Integer>> numToIndices = new HashMap<>();
        
        // Group indices by value
        for (int i = 0; i < nums.length; i++) {
            numToIndices.computeIfAbsent(nums[i], k -> new ArrayList<>()).add(i);
        }
        
        List<List<Integer>> result = new ArrayList<>();
        Set<Integer> processed = new HashSet<>();
        
        for (int i = 0; i < nums.length; i++) {
            if (processed.contains(nums[i])) continue;
            
            int complement = target - nums[i];
            
            if (numToIndices.containsKey(complement)) {
                List<Integer> indices1 = numToIndices.get(nums[i]);
                List<Integer> indices2 = numToIndices.get(complement);
                
                if (nums[i] == complement) {
                    // Same number, need at least 2 occurrences
                    if (indices1.size() >= 2) {
                        for (int j = 0; j < indices1.size() - 1; j++) {
                            result.add(Arrays.asList(indices1.get(j), indices1.get(j + 1)));
                        }
                    }
                } else {
                    // Different numbers
                    for (int idx1 : indices1) {
                        for (int idx2 : indices2) {
                            result.add(Arrays.asList(Math.min(idx1, idx2), Math.max(idx1, idx2)));
                        }
                    }
                }
                processed.add(complement);
            }
            processed.add(nums[i]);
        }
        
        return result;
    }

    @Test
    @Disabled
    void testTwoSum() {
        int[] nums = {2, 7, 11, 15};
        int target = 9;
        int[] expected = {0, 1};
        
        // Test all solutions
        Assertions.assertArrayEquals(expected, twoSumBruteForce(nums, target));
        Assertions.assertArrayEquals(expected, twoSumHashMap(nums, target));
        Assertions.assertArrayEquals(expected, twoSumTwoPointers(nums, target));
        
        // Test edge cases
        int[] nums2 = {3, 2, 4};
        int target2 = 6;
        int[] expected2 = {1, 2};
        Assertions.assertArrayEquals(expected2, twoSumHashMap(nums2, target2));
        
        // Test with duplicates
        int[] nums3 = {3, 3};
        int target3 = 6;
        int[] expected3 = {0, 1};
        Assertions.assertArrayEquals(expected3, twoSumHashMap(nums3, target3));
    }
}