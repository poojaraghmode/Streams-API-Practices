package com.github.streams.interview.collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;

import java.util.*;

/**
 * Interview Question: Implement a Generic Singly Linked List
 * 
 * Problem: Implement a generic singly linked list from scratch with the following operations:
 * - add(element), add(index, element)
 * - remove(index), remove(element)
 * - get(index), set(index, element)
 * - indexOf(element), contains(element)
 * - size(), isEmpty(), clear()
 * - reverse(), detectCycle(), findMiddle()
 * 
 * This tests understanding of:
 * - Linked list data structure
 * - Generic programming
 * - Pointer manipulation
 * - Edge case handling
 * - Algorithm implementation
 */
class LinkedListImplementation<T> implements Iterable<T> {

    private Node<T> head;
    private int size;

    private static class Node<T> {
        T data;
        Node<T> next;

        Node(T data) {
            this.data = data;
            this.next = null;
        }
    }

    /**
     * Initialize empty linked list
     */
    public LinkedListImplementation() {
        this.head = null;
        this.size = 0;
    }

    /**
     * Add element to the end of the list
     * Time Complexity: O(n)
     */
    public boolean add(T element) {
        if (head == null) {
            head = new Node<>(element);
        } else {
            Node<T> current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = new Node<>(element);
        }
        size++;
        return true;
    }

    /**
     * Add element at specific index
     * Time Complexity: O(n)
     */
    public void add(int index, T element) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        if (index == 0) {
            Node<T> newNode = new Node<>(element);
            newNode.next = head;
            head = newNode;
        } else {
            Node<T> current = head;
            for (int i = 0; i < index - 1; i++) {
                current = current.next;
            }
            Node<T> newNode = new Node<>(element);
            newNode.next = current.next;
            current.next = newNode;
        }
        size++;
    }

    /**
     * Remove element at specific index
     * Time Complexity: O(n)
     */
    public T remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        T removedData;
        if (index == 0) {
            removedData = head.data;
            head = head.next;
        } else {
            Node<T> current = head;
            for (int i = 0; i < index - 1; i++) {
                current = current.next;
            }
            removedData = current.next.data;
            current.next = current.next.next;
        }
        size--;
        return removedData;
    }

    /**
     * Remove first occurrence of element
     * Time Complexity: O(n)
     */
    public boolean remove(Object element) {
        if (head == null) {
            return false;
        }

        if (Objects.equals(head.data, element)) {
            head = head.next;
            size--;
            return true;
        }

        Node<T> current = head;
        while (current.next != null) {
            if (Objects.equals(current.next.data, element)) {
                current.next = current.next.next;
                size--;
                return true;
            }
            current = current.next;
        }
        return false;
    }

    /**
     * Get element at specific index
     * Time Complexity: O(n)
     */
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        Node<T> current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.data;
    }

    /**
     * Set element at specific index
     * Time Complexity: O(n)
     */
    public T set(int index, T element) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        Node<T> current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        T oldData = current.data;
        current.data = element;
        return oldData;
    }

    /**
     * Find index of first occurrence of element
     * Time Complexity: O(n)
     */
    public int indexOf(Object element) {
        Node<T> current = head;
        int index = 0;
        while (current != null) {
            if (Objects.equals(current.data, element)) {
                return index;
            }
            current = current.next;
            index++;
        }
        return -1;
    }

    /**
     * Check if list contains element
     * Time Complexity: O(n)
     */
    public boolean contains(Object element) {
        return indexOf(element) != -1;
    }

    /**
     * Get current size
     * Time Complexity: O(1)
     */
    public int size() {
        return size;
    }

    /**
     * Check if list is empty
     * Time Complexity: O(1)
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Clear all elements
     * Time Complexity: O(1)
     */
    public void clear() {
        head = null;
        size = 0;
    }

    /**
     * Reverse the linked list iteratively
     * Time Complexity: O(n), Space Complexity: O(1)
     */
    public void reverse() {
        Node<T> prev = null;
        Node<T> current = head;
        Node<T> next;

        while (current != null) {
            next = current.next;
            current.next = prev;
            prev = current;
            current = next;
        }
        head = prev;
    }

    /**
     * Reverse the linked list recursively
     * Time Complexity: O(n), Space Complexity: O(n) due to recursion stack
     */
    public void reverseRecursively() {
        head = reverseRecursiveHelper(head);
    }

    private Node<T> reverseRecursiveHelper(Node<T> node) {
        if (node == null || node.next == null) {
            return node;
        }

        Node<T> reversedHead = reverseRecursiveHelper(node.next);
        node.next.next = node;
        node.next = null;
        return reversedHead;
    }

    /**
     * Find middle element using slow-fast pointer technique
     * Time Complexity: O(n), Space Complexity: O(1)
     */
    public T findMiddle() {
        if (head == null) {
            return null;
        }

        Node<T> slow = head;
        Node<T> fast = head;

        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        return slow.data;
    }

    /**
     * Detect if there's a cycle in the linked list using Floyd's algorithm
     * Time Complexity: O(n), Space Complexity: O(1)
     */
    public boolean hasCycle() {
        if (head == null || head.next == null) {
            return false;
        }

        Node<T> slow = head;
        Node<T> fast = head;

        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast) {
                return true;
            }
        }
        return false;
    }

    /**
     * Find the start of cycle (if exists)
     * Time Complexity: O(n), Space Complexity: O(1)
     */
    public Node<T> findCycleStart() {
        if (!hasCycle()) {
            return null;
        }

        Node<T> slow = head;
        Node<T> fast = head;

        // Find meeting point
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast) {
                break;
            }
        }

        // Find start of cycle
        slow = head;
        while (slow != fast) {
            slow = slow.next;
            fast = fast.next;
        }
        return slow;
    }

    /**
     * Remove nth node from end
     * Time Complexity: O(n), Space Complexity: O(1)
     */
    public boolean removeNthFromEnd(int n) {
        if (n <= 0 || head == null) {
            return false;
        }

        Node<T> fast = head;
        Node<T> slow = head;

        // Move fast pointer n steps ahead
        for (int i = 0; i < n; i++) {
            if (fast == null) {
                return false; // n is greater than list length
            }
            fast = fast.next;
        }

        // If fast is null, remove head
        if (fast == null) {
            head = head.next;
            size--;
            return true;
        }

        // Move both pointers until fast reaches end
        while (fast.next != null) {
            slow = slow.next;
            fast = fast.next;
        }

        // Remove the node
        slow.next = slow.next.next;
        size--;
        return true;
    }

    /**
     * Merge with another sorted linked list
     * Time Complexity: O(m + n), Space Complexity: O(1)
     */
    public void mergeSorted(LinkedListImplementation<T> other, Comparator<T> comparator) {
        if (other == null || other.head == null) {
            return;
        }

        Node<T> dummy = new Node<>(null);
        Node<T> current = dummy;
        Node<T> list1 = this.head;
        Node<T> list2 = other.head;

        while (list1 != null && list2 != null) {
            if (comparator.compare(list1.data, list2.data) <= 0) {
                current.next = list1;
                list1 = list1.next;
            } else {
                current.next = list2;
                list2 = list2.next;
            }
            current = current.next;
        }

        // Attach remaining nodes
        current.next = (list1 != null) ? list1 : list2;
        this.head = dummy.next;
        this.size += other.size;
    }

    /**
     * Convert to array
     * Time Complexity: O(n)
     */
    @SuppressWarnings("unchecked")
    public T[] toArray() {
        T[] array = (T[]) new Object[size];
        Node<T> current = head;
        int index = 0;
        while (current != null) {
            array[index++] = current.data;
            current = current.next;
        }
        return array;
    }

    /**
     * Iterator implementation
     */
    @Override
    public Iterator<T> iterator() {
        return new LinkedListIterator();
    }

    private class LinkedListIterator implements Iterator<T> {
        private Node<T> current = head;

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            T data = current.data;
            current = current.next;
            return data;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        Node<T> current = head;
        while (current != null) {
            sb.append(current.data);
            if (current.next != null) {
                sb.append(", ");
            }
            current = current.next;
        }
        sb.append("]");
        return sb.toString();
    }

    @Test
    @Disabled
    void testBasicOperations() {
        LinkedListImplementation<String> list = new LinkedListImplementation<>();

        // Test add
        list.add("A");
        list.add("B");
        list.add("C");
        Assertions.assertEquals(3, list.size());
        Assertions.assertEquals("A", list.get(0));
        Assertions.assertEquals("C", list.get(2));

        // Test add at index
        list.add(1, "X");
        Assertions.assertEquals(4, list.size());
        Assertions.assertEquals("X", list.get(1));
        Assertions.assertEquals("B", list.get(2));

        // Test set
        String old = list.set(1, "Y");
        Assertions.assertEquals("X", old);
        Assertions.assertEquals("Y", list.get(1));

        // Test indexOf and contains
        Assertions.assertEquals(0, list.indexOf("A"));
        Assertions.assertEquals(1, list.indexOf("Y"));
        Assertions.assertTrue(list.contains("B"));
        Assertions.assertFalse(list.contains("Z"));

        // Test remove by index
        String removed = list.remove(1);
        Assertions.assertEquals("Y", removed);
        Assertions.assertEquals(3, list.size());

        // Test remove by element
        boolean success = list.remove("B");
        Assertions.assertTrue(success);
        Assertions.assertEquals(2, list.size());
    }

    @Test
    @Disabled
    void testAdvancedOperations() {
        LinkedListImplementation<Integer> list = new LinkedListImplementation<>();
        for (int i = 1; i <= 5; i++) {
            list.add(i);
        }

        // Test find middle
        Integer middle = list.findMiddle();
        Assertions.assertEquals(Integer.valueOf(3), middle);

        // Test reverse
        list.reverse();
        Assertions.assertEquals(Integer.valueOf(5), list.get(0));
        Assertions.assertEquals(Integer.valueOf(1), list.get(4));

        // Test reverse back
        list.reverseRecursively();
        Assertions.assertEquals(Integer.valueOf(1), list.get(0));
        Assertions.assertEquals(Integer.valueOf(5), list.get(4));

        // Test remove nth from end
        boolean removed = list.removeNthFromEnd(2); // Remove 4
        Assertions.assertTrue(removed);
        Assertions.assertEquals(4, list.size());
        Assertions.assertFalse(list.contains(4));
    }

    @Test
    @Disabled
    void testIterator() {
        LinkedListImplementation<String> list = new LinkedListImplementation<>();
        list.add("A");
        list.add("B");
        list.add("C");

        StringBuilder result = new StringBuilder();
        for (String item : list) {
            result.append(item);
        }
        Assertions.assertEquals("ABC", result.toString());
    }

    @Test
    @Disabled
    void testEdgeCases() {
        LinkedListImplementation<Integer> list = new LinkedListImplementation<>();

        // Test empty list
        Assertions.assertTrue(list.isEmpty());
        Assertions.assertEquals(0, list.size());
        Assertions.assertNull(list.findMiddle());
        Assertions.assertFalse(list.hasCycle());

        // Test single element
        list.add(42);
        Assertions.assertEquals(Integer.valueOf(42), list.findMiddle());
        Assertions.assertFalse(list.hasCycle());

        // Test clear
        list.clear();
        Assertions.assertTrue(list.isEmpty());

        // Test exceptions
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> list.get(0));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> list.remove(0));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> list.add(-1, 1));
    }

    @Test
    @Disabled
    void testMergeSorted() {
        LinkedListImplementation<Integer> list1 = new LinkedListImplementation<>();
        LinkedListImplementation<Integer> list2 = new LinkedListImplementation<>();

        list1.add(1);
        list1.add(3);
        list1.add(5);

        list2.add(2);
        list2.add(4);
        list2.add(6);

        list1.mergeSorted(list2, Integer::compareTo);

        Assertions.assertEquals(6, list1.size());
        for (int i = 1; i <= 6; i++) {
            Assertions.assertEquals(Integer.valueOf(i), list1.get(i - 1));
        }
    }
}