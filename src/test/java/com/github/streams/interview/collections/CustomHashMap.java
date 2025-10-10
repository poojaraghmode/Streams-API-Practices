package com.github.streams.interview.collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Interview Question: Implement a Custom HashMap
 * 
 * Problem: Implement a basic HashMap with the following operations:
 * - put(key, value)
 * - get(key)
 * - remove(key)
 * - size()
 * - isEmpty()
 * 
 * This tests understanding of:
 * - Hash functions
 * - Collision handling (chaining)
 * - Dynamic resizing
 * - Load factor management
 */
class CustomHashMap<K, V> {
    
    private static class Node<K, V> {
        final K key;
        V value;
        Node<K, V> next;
        final int hash;

        Node(int hash, K key, V value, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private static final double DEFAULT_LOAD_FACTOR = 0.75;

    private Node<K, V>[] table;
    private int size;
    private int threshold;
    private final double loadFactor;

    @SuppressWarnings("unchecked")
    public CustomHashMap() {
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        this.threshold = (int) (DEFAULT_INITIAL_CAPACITY * DEFAULT_LOAD_FACTOR);
        this.table = new Node[DEFAULT_INITIAL_CAPACITY];
    }

    @SuppressWarnings("unchecked")
    public CustomHashMap(int initialCapacity, double loadFactor) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal initial capacity: " + initialCapacity);
        }
        if (loadFactor <= 0 || Double.isNaN(loadFactor)) {
            throw new IllegalArgumentException("Illegal load factor: " + loadFactor);
        }
        
        this.loadFactor = loadFactor;
        this.threshold = (int) (initialCapacity * loadFactor);
        this.table = new Node[initialCapacity];
    }

    /**
     * Hash function to determine bucket index
     */
    private int hash(Object key) {
        if (key == null) return 0;
        int h = key.hashCode();
        return h ^ (h >>> 16); // XOR with right-shifted bits to reduce collisions
    }

    /**
     * Get the bucket index for a given hash
     */
    private int indexFor(int hash, int length) {
        return hash & (length - 1); // Equivalent to hash % length for power of 2 lengths
    }

    /**
     * Put a key-value pair into the map
     */
    public V put(K key, V value) {
        int hash = hash(key);
        int index = indexFor(hash, table.length);

        // Check if key already exists
        for (Node<K, V> node = table[index]; node != null; node = node.next) {
            if (node.hash == hash && Objects.equals(key, node.key)) {
                V oldValue = node.value;
                node.value = value;
                return oldValue;
            }
        }

        // Add new node
        addNode(hash, key, value, index);
        return null;
    }

    /**
     * Add a new node to the table
     */
    private void addNode(int hash, K key, V value, int bucketIndex) {
        Node<K, V> newNode = new Node<>(hash, key, value, table[bucketIndex]);
        table[bucketIndex] = newNode;
        size++;

        // Check if resize is needed
        if (size >= threshold) {
            resize();
        }
    }

    /**
     * Get value for a given key
     */
    public V get(Object key) {
        int hash = hash(key);
        int index = indexFor(hash, table.length);

        for (Node<K, V> node = table[index]; node != null; node = node.next) {
            if (node.hash == hash && Objects.equals(key, node.key)) {
                return node.value;
            }
        }
        return null;
    }

    /**
     * Remove a key-value pair from the map
     */
    public V remove(Object key) {
        int hash = hash(key);
        int index = indexFor(hash, table.length);

        Node<K, V> prev = null;
        Node<K, V> current = table[index];

        while (current != null) {
            if (current.hash == hash && Objects.equals(key, current.key)) {
                if (prev == null) {
                    table[index] = current.next;
                } else {
                    prev.next = current.next;
                }
                size--;
                return current.value;
            }
            prev = current;
            current = current.next;
        }
        return null;
    }

    /**
     * Check if the map contains a specific key
     */
    public boolean containsKey(Object key) {
        return get(key) != null || (get(key) == null && containsNullValue(key));
    }

    private boolean containsNullValue(Object key) {
        int hash = hash(key);
        int index = indexFor(hash, table.length);

        for (Node<K, V> node = table[index]; node != null; node = node.next) {
            if (node.hash == hash && Objects.equals(key, node.key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Resize the table when load factor threshold is exceeded
     */
    @SuppressWarnings("unchecked")
    private void resize() {
        Node<K, V>[] oldTable = table;
        int oldCapacity = oldTable.length;
        int newCapacity = oldCapacity * 2;

        if (newCapacity < 0) {
            // Overflow
            if (oldCapacity == Integer.MAX_VALUE) {
                return;
            }
            newCapacity = Integer.MAX_VALUE;
        }

        Node<K, V>[] newTable = new Node[newCapacity];
        table = newTable;
        threshold = (int) (newCapacity * loadFactor);

        // Rehash all existing nodes
        for (int i = 0; i < oldCapacity; i++) {
            Node<K, V> node = oldTable[i];
            if (node != null) {
                oldTable[i] = null;
                
                do {
                    Node<K, V> next = node.next;
                    int index = indexFor(node.hash, newCapacity);
                    node.next = newTable[index];
                    newTable[index] = node;
                    node = next;
                } while (node != null);
            }
        }
    }

    /**
     * Get the current size of the map
     */
    public int size() {
        return size;
    }

    /**
     * Check if the map is empty
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Clear all entries from the map
     */
    public void clear() {
        for (int i = 0; i < table.length; i++) {
            table[i] = null;
        }
        size = 0;
    }

    /**
     * Get all keys in the map
     */
    public List<K> keySet() {
        List<K> keys = new ArrayList<>();
        for (Node<K, V> node : table) {
            while (node != null) {
                keys.add(node.key);
                node = node.next;
            }
        }
        return keys;
    }

    /**
     * Get all values in the map
     */
    public List<V> values() {
        List<V> values = new ArrayList<>();
        for (Node<K, V> node : table) {
            while (node != null) {
                values.add(node.value);
                node = node.next;
            }
        }
        return values;
    }

    @Test
    @Disabled
    void testCustomHashMap() {
        CustomHashMap<String, Integer> map = new CustomHashMap<>();

        // Test put and get
        Assertions.assertNull(map.put("one", 1));
        Assertions.assertNull(map.put("two", 2));
        Assertions.assertNull(map.put("three", 3));

        Assertions.assertEquals(1, (int) map.get("one"));
        Assertions.assertEquals(2, (int) map.get("two"));
        Assertions.assertEquals(3, (int) map.get("three"));

        // Test size
        Assertions.assertEquals(3, map.size());
        Assertions.assertFalse(map.isEmpty());

        // Test update existing key
        Assertions.assertEquals(1, (int) map.put("one", 10));
        Assertions.assertEquals(10, (int) map.get("one"));
        Assertions.assertEquals(3, map.size()); // Size should remain same

        // Test remove
        Assertions.assertEquals(10, (int) map.remove("one"));
        Assertions.assertNull(map.get("one"));
        Assertions.assertEquals(2, map.size());

        // Test containsKey
        Assertions.assertTrue(map.containsKey("two"));
        Assertions.assertFalse(map.containsKey("one"));

        // Test with null key
        map.put(null, 100);
        Assertions.assertEquals(100, (int) map.get(null));

        // Test resize by adding many elements
        for (int i = 0; i < 20; i++) {
            map.put("key" + i, i);
        }
        Assertions.assertTrue(map.size() > 20);

        // Test clear
        map.clear();
        Assertions.assertEquals(0, map.size());
        Assertions.assertTrue(map.isEmpty());
    }
}