package com.github.streams.interview.algorithms;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;

import java.util.*;

/**
 * Interview Question: Implement LRU (Least Recently Used) Cache
 * 
 * Problem: Design and implement a data structure for Least Recently Used (LRU) cache.
 * It should support the following operations:
 * - get(key): Get the value of the key if it exists, otherwise return -1
 * - put(key, value): Set or insert the value if the key is not present.
 *   When the cache reaches its capacity, invalidate the least recently used item.
 * 
 * Both operations should run in O(1) average time complexity.
 * 
 * This tests understanding of:
 * - Data structure design
 * - HashMap + Doubly Linked List combination
 * - Time complexity optimization
 * - Cache replacement algorithms
 */
class LRUCache<K, V> {
    
    private final int capacity;
    private final Map<K, Node<K, V>> map;
    private final Node<K, V> head;
    private final Node<K, V> tail;

    /**
     * Node class for doubly linked list
     */
    private static class Node<K, V> {
        K key;
        V value;
        Node<K, V> prev;
        Node<K, V> next;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    /**
     * Initialize LRU Cache with given capacity
     */
    public LRUCache(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        
        this.capacity = capacity;
        this.map = new HashMap<>();
        
        // Create dummy head and tail nodes to simplify edge cases
        this.head = new Node<>(null, null);
        this.tail = new Node<>(null, null);
        this.head.next = this.tail;
        this.tail.prev = this.head;
    }

    /**
     * Get value by key
     * Time Complexity: O(1)
     */
    public V get(K key) {
        Node<K, V> node = map.get(key);
        if (node == null) {
            return null;
        }
        
        // Move the accessed node to head (most recently used)
        moveToHead(node);
        return node.value;
    }

    /**
     * Put key-value pair
     * Time Complexity: O(1)
     */
    public V put(K key, V value) {
        Node<K, V> existingNode = map.get(key);
        
        if (existingNode != null) {
            // Update existing node
            V oldValue = existingNode.value;
            existingNode.value = value;
            moveToHead(existingNode);
            return oldValue;
        }
        
        // Add new node
        Node<K, V> newNode = new Node<>(key, value);
        
        if (map.size() >= capacity) {
            // Remove least recently used node (tail.prev)
            Node<K, V> lastNode = tail.prev;
            removeNode(lastNode);
            map.remove(lastNode.key);
        }
        
        addToHead(newNode);
        map.put(key, newNode);
        return null;
    }

    /**
     * Remove a specific key
     * Time Complexity: O(1)
     */
    public V remove(K key) {
        Node<K, V> node = map.get(key);
        if (node == null) {
            return null;
        }
        
        removeNode(node);
        map.remove(key);
        return node.value;
    }

    /**
     * Get current size
     */
    public int size() {
        return map.size();
    }

    /**
     * Check if cache is empty
     */
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * Check if key exists
     */
    public boolean containsKey(K key) {
        return map.containsKey(key);
    }

    /**
     * Clear all entries
     */
    public void clear() {
        map.clear();
        head.next = tail;
        tail.prev = head;
    }

    /**
     * Get all keys in access order (most recent first)
     */
    public List<K> getKeysInAccessOrder() {
        List<K> keys = new ArrayList<>();
        Node<K, V> current = head.next;
        while (current != tail) {
            keys.add(current.key);
            current = current.next;
        }
        return keys;
    }

    // Helper methods for doubly linked list operations

    private void addToHead(Node<K, V> node) {
        node.prev = head;
        node.next = head.next;
        head.next.prev = node;
        head.next = node;
    }

    private void removeNode(Node<K, V> node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private void moveToHead(Node<K, V> node) {
        removeNode(node);
        addToHead(node);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("LRUCache{capacity=").append(capacity).append(", size=").append(size()).append(", keys=");
        sb.append(getKeysInAccessOrder());
        sb.append("}");
        return sb.toString();
    }

    /**
     * Alternative implementation using LinkedHashMap
     * Java's LinkedHashMap can be configured to maintain access order
     */
    static class LRUCacheUsingLinkedHashMap<K, V> extends LinkedHashMap<K, V> {
        private final int capacity;

        public LRUCacheUsingLinkedHashMap(int capacity) {
            // true = access order, false = insertion order
            super(capacity + 1, 1.0f, true);
            this.capacity = capacity;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return size() > capacity;
        }

        public V getValue(K key) {
            return super.get(key); // This will move to end if access-ordered
        }
    }

    /**
     * LFU (Least Frequently Used) Cache for comparison
     * Bonus implementation to show understanding of different cache strategies
     */
    static class LFUCache<K, V> {
        private final int capacity;
        private final Map<K, V> values;
        private final Map<K, Integer> frequencies;
        private final Map<Integer, LinkedHashSet<K>> frequencyGroups;
        private int minFrequency;

        public LFUCache(int capacity) {
            this.capacity = capacity;
            this.values = new HashMap<>();
            this.frequencies = new HashMap<>();
            this.frequencyGroups = new HashMap<>();
            this.minFrequency = 1;
        }

        public V get(K key) {
            if (!values.containsKey(key)) {
                return null;
            }
            
            updateFrequency(key);
            return values.get(key);
        }

        public V put(K key, V value) {
            if (capacity <= 0) return null;
            
            if (values.containsKey(key)) {
                V oldValue = values.get(key);
                values.put(key, value);
                updateFrequency(key);
                return oldValue;
            }
            
            if (values.size() >= capacity) {
                evictLFU();
            }
            
            values.put(key, value);
            frequencies.put(key, 1);
            frequencyGroups.computeIfAbsent(1, k -> new LinkedHashSet<>()).add(key);
            minFrequency = 1;
            return null;
        }

        private void updateFrequency(K key) {
            int freq = frequencies.get(key);
            frequencies.put(key, freq + 1);
            
            // Remove from old frequency group
            frequencyGroups.get(freq).remove(key);
            if (freq == minFrequency && frequencyGroups.get(freq).isEmpty()) {
                minFrequency++;
            }
            
            // Add to new frequency group
            frequencyGroups.computeIfAbsent(freq + 1, k -> new LinkedHashSet<>()).add(key);
        }

        private void evictLFU() {
            K keyToEvict = frequencyGroups.get(minFrequency).iterator().next();
            frequencyGroups.get(minFrequency).remove(keyToEvict);
            values.remove(keyToEvict);
            frequencies.remove(keyToEvict);
        }
    }

    @Test
    @Disabled
    void testLRUCache() {
        LRUCache<Integer, String> cache = new LRUCache<>(3);

        // Test basic put and get
        cache.put(1, "one");
        cache.put(2, "two");
        cache.put(3, "three");
        
        Assertions.assertEquals("one", cache.get(1));
        Assertions.assertEquals("two", cache.get(2));
        Assertions.assertEquals("three", cache.get(3));
        Assertions.assertEquals(3, cache.size());

        // Test LRU eviction
        cache.put(4, "four"); // Should evict least recently used (1)
        Assertions.assertNull(cache.get(1)); // 1 should be evicted
        Assertions.assertEquals("four", cache.get(4));

        // Test access order update
        cache.get(2); // Move 2 to front
        cache.put(5, "five"); // Should evict 3 (least recently used)
        Assertions.assertNull(cache.get(3));
        Assertions.assertEquals("two", cache.get(2));
        Assertions.assertEquals("five", cache.get(5));

        // Test update existing key
        String oldValue = cache.put(2, "TWO");
        Assertions.assertEquals("two", oldValue);
        Assertions.assertEquals("TWO", cache.get(2));

        // Test key access order
        List<Integer> keys = cache.getKeysInAccessOrder();
        Assertions.assertEquals(Integer.valueOf(2), keys.get(0)); // Most recent

        // Test remove
        String removedValue = cache.remove(4);
        Assertions.assertEquals("four", removedValue);
        Assertions.assertFalse(cache.containsKey(4));
        Assertions.assertEquals(2, cache.size());

        // Test clear
        cache.clear();
        Assertions.assertTrue(cache.isEmpty());
        Assertions.assertEquals(0, cache.size());
    }

    @Test
    @Disabled
    void testLRUCacheUsingLinkedHashMap() {
        LRUCacheUsingLinkedHashMap<Integer, String> cache = new LRUCacheUsingLinkedHashMap<>(3);

        cache.put(1, "one");
        cache.put(2, "two");
        cache.put(3, "three");
        
        Assertions.assertEquals("one", cache.getValue(1));
        Assertions.assertEquals(3, cache.size());

        cache.put(4, "four"); // Should evict least recently used
        Assertions.assertEquals(3, cache.size());
        
        // Test that it maintains LRU behavior
        Assertions.assertTrue(cache.containsKey(1)); // Should still be there due to recent access
        Assertions.assertTrue(cache.containsKey(4));
    }

    @Test
    @Disabled
    void testLFUCache() {
        LFUCache<Integer, String> cache = new LFUCache<>(2);

        cache.put(1, "one");
        cache.put(2, "two");
        
        Assertions.assertEquals("one", cache.get(1)); // freq of 1 becomes 2
        
        cache.put(3, "three"); // Should evict 2 (freq 1) instead of 1 (freq 2)
        Assertions.assertNull(cache.get(2));
        Assertions.assertEquals("one", cache.get(1));
        Assertions.assertEquals("three", cache.get(3));
    }

    @Test
    @Disabled
    void testEdgeCases() {
        // Test capacity 1
        LRUCache<Integer, String> smallCache = new LRUCache<>(1);
        smallCache.put(1, "one");
        smallCache.put(2, "two");
        Assertions.assertNull(smallCache.get(1));
        Assertions.assertEquals("two", smallCache.get(2));

        // Test illegal capacity
        Assertions.assertThrows(IllegalArgumentException.class, () -> new LRUCache<Integer, String>(0));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new LRUCache<Integer, String>(-1));
    }
}