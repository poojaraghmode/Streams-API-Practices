package com.github.streams.interview.generics;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;

import java.util.*;

/**
 * Interview Question: Understanding Generic Wildcards
 * 
 * Problem: Demonstrate understanding of generic wildcards:
 * - Upper bounded wildcards (? extends T)
 * - Lower bounded wildcards (? super T)
 * - Unbounded wildcards (?)
 * - PECS principle (Producer Extends, Consumer Super)
 * 
 * This tests understanding of:
 * - Generic type system
 * - Covariance and contravariance
 * - Type safety
 * - Practical applications of wildcards
 */
class GenericWildcards {

    // Base classes for demonstration
    static class Animal {
        private String name;
        
        public Animal(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
        
        @Override
        public String toString() {
            return getClass().getSimpleName() + "(" + name + ")";
        }
    }

    static class Dog extends Animal {
        public Dog(String name) {
            super(name);
        }
        
        public void bark() {
            System.out.println(getName() + " is barking!");
        }
    }

    static class Cat extends Animal {
        public Cat(String name) {
            super(name);
        }
        
        public void meow() {
            System.out.println(getName() + " is meowing!");
        }
    }

    static class Poodle extends Dog {
        public Poodle(String name) {
            super(name);
        }
    }

    /**
     * Example 1: Upper bounded wildcard (? extends T)
     * Used when you want to READ from a collection (Producer)
     * You can read T or any subtype of T, but cannot add elements (except null)
     */
    public static double calculateTotalWeight(List<? extends Animal> animals) {
        // We can read from the list because we know all elements are Animals or subtypes
        return animals.stream()
                .mapToDouble(animal -> 10.0) // Simplified weight calculation
                .sum();
        
        // This would NOT compile:
        // animals.add(new Dog("Rex")); // Cannot add because we don't know the exact type
    }

    /**
     * Example 2: Lower bounded wildcard (? super T)
     * Used when you want to WRITE to a collection (Consumer)
     * You can add T or any subtype of T, but reading gives you Object
     */
    public static void addAnimals(List<? super Dog> animals) {
        // We can add Dogs or any subtype of Dog
        animals.add(new Dog("Rex"));
        animals.add(new Poodle("Fluffy"));
        
        // This would NOT compile:
        // animals.add(new Cat("Whiskers")); // Cat is not a subtype of Dog
        
        // Reading gives us Object, not very useful
        Object first = animals.get(0);
        // Dog dog = animals.get(0); // This would NOT compile
    }

    /**
     * Example 3: Unbounded wildcard (?)
     * Used when you don't care about the type or only use Object methods
     */
    public static int countElements(List<?> list) {
        return list.size(); // Works because size() is available on any List
        
        // This would NOT compile:
        // list.add(new Object()); // Cannot add anything except null
    }

    /**
     * Example 4: PECS (Producer Extends, Consumer Super) in action
     * Copy elements from source (producer) to destination (consumer)
     */
    public static <T> void copy(List<? extends T> source, List<? super T> destination) {
        for (T item : source) {
            destination.add(item);
        }
    }

    /**
     * Example 5: Generic method with multiple bounds
     */
    public static <T extends Animal & Comparable<T>> T findMax(List<T> animals) {
        if (animals.isEmpty()) {
            return null;
        }
        
        return animals.stream()
                .max(Comparable::compareTo)
                .orElse(null);
    }

    /**
     * Example 6: Wildcard capture helper
     * Sometimes you need to capture the wildcard type
     */
    public static void processAnimals(List<?> animals) {
        processAnimalsHelper(animals);
    }
    
    private static <T> void processAnimalsHelper(List<T> animals) {
        // Now we can work with the captured type T
        if (!animals.isEmpty()) {
            T first = animals.get(0);
            animals.add(first); // This works because T is captured
        }
    }

    /**
     * Example 7: Bounded type parameters in class definition
     */
    static class AnimalShelter<T extends Animal> {
        private List<T> animals = new ArrayList<>();
        
        public void add(T animal) {
            animals.add(animal);
        }
        
        public List<T> getAnimals() {
            return new ArrayList<>(animals);
        }
        
        // Method that accepts more general types
        public void addAll(Collection<? extends T> newAnimals) {
            animals.addAll(newAnimals);
        }
        
        // Method that works with more specific types
        public void transferTo(Collection<? super T> destination) {
            destination.addAll(animals);
        }
    }

    /**
     * Example 8: Raw types vs Parameterized types (what NOT to do)
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void demonstrateRawTypes() {
        // Raw type - avoid this!
        List rawList = new ArrayList();
        rawList.add("String");
        rawList.add(42);
        
        // This will cause ClassCastException at runtime
        // String str = (String) rawList.get(1);
        
        // Parameterized type - preferred
        List<String> stringList = new ArrayList<>();
        stringList.add("String");
        // stringList.add(42); // Compile-time error - much better!
    }

    @Test
    @Disabled
    void testGenericWildcards() {
        // Test upper bounded wildcards
        List<Dog> dogs = Arrays.asList(new Dog("Rex"), new Poodle("Fluffy"));
        List<Cat> cats = Arrays.asList(new Cat("Whiskers"), new Cat("Shadow"));
        List<Animal> animals = new ArrayList<>();
        animals.addAll(dogs);
        animals.addAll(cats);
        
        double totalWeight = calculateTotalWeight(dogs);
        Assertions.assertEquals(20.0, totalWeight, 0.01);
        
        totalWeight = calculateTotalWeight(animals);
        Assertions.assertEquals(40.0, totalWeight, 0.01);
        
        // Test lower bounded wildcards
        List<Animal> animalList = new ArrayList<>();
        addAnimals(animalList); // List<Animal> is super of Dog
        Assertions.assertEquals(2, animalList.size());
        
        List<Dog> dogList = new ArrayList<>();
        addAnimals(dogList); // List<Dog> is also valid
        Assertions.assertEquals(2, dogList.size());
        
        // Test unbounded wildcards
        Assertions.assertEquals(2, countElements(dogs));
        Assertions.assertEquals(2, countElements(cats));
        Assertions.assertEquals(4, countElements(animals));
        
        // Test PECS principle
        List<Dog> source = Arrays.asList(new Dog("Buddy"), new Poodle("Princess"));
        List<Animal> destination = new ArrayList<>();
        copy(source, destination);
        Assertions.assertEquals(2, destination.size());
        
        // Test AnimalShelter
        AnimalShelter<Dog> dogShelter = new AnimalShelter<>();
        dogShelter.add(new Dog("Max"));
        dogShelter.add(new Poodle("Bella"));
        
        List<Poodle> poodles = Arrays.asList(new Poodle("Charlie"));
        dogShelter.addAll(poodles); // Works because Poodle extends Dog
        
        List<Animal> allAnimals = new ArrayList<>();
        dogShelter.transferTo(allAnimals); // Works because Animal is super of Dog
        
        Assertions.assertEquals(3, dogShelter.getAnimals().size());
        Assertions.assertEquals(3, allAnimals.size());
    }

    /**
     * Additional Examples: Common Generic Patterns
     */
    
    // Builder pattern with generics
    static class GenericBuilder<T> {
        private T item;
        
        public GenericBuilder<T> with(T item) {
            this.item = item;
            return this;
        }
        
        public T build() {
            return item;
        }
    }
    
    // Generic factory method
    public static <T> List<T> createList(T... items) {
        return Arrays.asList(items);
    }
    
    // Type inference demonstration
    @Test
    @Disabled
    void testTypeInference() {
        // Type inference works
        List<String> strings = createList("a", "b", "c");
        Assertions.assertEquals(3, strings.size());
        
        // Diamond operator (Java 7+)
        Map<String, List<Integer>> map = new HashMap<>();
        map.put("numbers", Arrays.asList(1, 2, 3));
        
        // Builder pattern
        String result = new GenericBuilder<String>()
                .with("Hello World")
                .build();
        Assertions.assertEquals("Hello World", result);
    }
}