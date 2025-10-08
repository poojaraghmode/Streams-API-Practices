package com.github.streams.interview.streams;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;

import java.util.*;
import java.util.stream.Collectors;
import java.util.function.Function;

/**
 * Interview Question: Employee Stream Operations
 * 
 * Problem: Given a list of employees, perform various stream operations
 * to answer common business questions.
 * 
 * This tests understanding of:
 * - Stream operations (filter, map, collect, groupBy)
 * - Collectors
 * - Optional handling
 * - Complex data transformations
 */
class EmployeeStreamOperations {

    static class Employee {
        private String name;
        private String department;
        private double salary;
        private int age;
        private String city;

        public Employee(String name, String department, double salary, int age, String city) {
            this.name = name;
            this.department = department;
            this.salary = salary;
            this.age = age;
            this.city = city;
        }

        // Getters
        public String getName() { return name; }
        public String getDepartment() { return department; }
        public double getSalary() { return salary; }
        public int getAge() { return age; }
        public String getCity() { return city; }

        @Override
        public String toString() {
            return String.format("Employee{name='%s', dept='%s', salary=%.2f, age=%d, city='%s'}", 
                               name, department, salary, age, city);
        }
    }

    private static List<Employee> getEmployees() {
        return Arrays.asList(
            new Employee("Alice", "Engineering", 75000, 28, "New York"),
            new Employee("Bob", "Engineering", 80000, 32, "San Francisco"),
            new Employee("Charlie", "Marketing", 60000, 25, "New York"),
            new Employee("Diana", "Engineering", 90000, 30, "Seattle"),
            new Employee("Eve", "HR", 55000, 35, "New York"),
            new Employee("Frank", "Marketing", 65000, 29, "San Francisco"),
            new Employee("Grace", "Engineering", 95000, 33, "Seattle"),
            new Employee("Henry", "HR", 50000, 24, "New York"),
            new Employee("Ivy", "Engineering", 85000, 27, "San Francisco"),
            new Employee("Jack", "Marketing", 70000, 31, "Seattle")
        );
    }

    /**
     * Question 1: Find all employees in Engineering department with salary > 80000
     */
    public static List<Employee> findHighPaidEngineers(List<Employee> employees) {
        return employees.stream()
                .filter(emp -> "Engineering".equals(emp.getDepartment()))
                .filter(emp -> emp.getSalary() > 80000)
                .collect(Collectors.toList());
    }

    /**
     * Question 2: Group employees by department and calculate average salary per department
     */
    public static Map<String, Double> getAverageSalaryByDepartment(List<Employee> employees) {
        return employees.stream()
                .collect(Collectors.groupingBy(
                    Employee::getDepartment,
                    Collectors.averagingDouble(Employee::getSalary)
                ));
    }

    /**
     * Question 3: Find the highest paid employee in each department
     */
    public static Map<String, Optional<Employee>> getHighestPaidByDepartment(List<Employee> employees) {
        return employees.stream()
                .collect(Collectors.groupingBy(
                    Employee::getDepartment,
                    Collectors.maxBy(Comparator.comparing(Employee::getSalary))
                ));
    }

    /**
     * Question 4: Get names of employees sorted by salary (descending) and then by age (ascending)
     */
    public static List<String> getSortedEmployeeNames(List<Employee> employees) {
        return employees.stream()
                .sorted(Comparator
                    .comparing(Employee::getSalary, Comparator.reverseOrder())
                    .thenComparing(Employee::getAge))
                .map(Employee::getName)
                .collect(Collectors.toList());
    }

    /**
     * Question 5: Find employees who are older than average age
     */
    public static List<Employee> getEmployeesOlderThanAverage(List<Employee> employees) {
        double averageAge = employees.stream()
                .mapToInt(Employee::getAge)
                .average()
                .orElse(0.0);
        
        return employees.stream()
                .filter(emp -> emp.getAge() > averageAge)
                .collect(Collectors.toList());
    }

    /**
     * Question 6: Count employees by city and department combination
     */
    public static Map<String, Long> getEmployeeCountByCityAndDepartment(List<Employee> employees) {
        return employees.stream()
                .collect(Collectors.groupingBy(
                    emp -> emp.getCity() + " - " + emp.getDepartment(),
                    Collectors.counting()
                ));
    }

    /**
     * Question 7: Find the second highest salary
     */
    public static Optional<Double> getSecondHighestSalary(List<Employee> employees) {
        return employees.stream()
                .mapToDouble(Employee::getSalary)
                .distinct()
                .boxed()
                .sorted(Comparator.reverseOrder())
                .skip(1)
                .findFirst();
    }

    /**
     * Question 8: Partition employees into high earners (>= 70000) and others
     */
    public static Map<Boolean, List<Employee>> partitionByHighEarners(List<Employee> employees) {
        return employees.stream()
                .collect(Collectors.partitioningBy(emp -> emp.getSalary() >= 70000));
    }

    /**
     * Question 9: Get department-wise employee count with only departments having > 2 employees
     */
    public static Map<String, Long> getDepartmentsWithMultipleEmployees(List<Employee> employees) {
        return employees.stream()
                .collect(Collectors.groupingBy(
                    Employee::getDepartment,
                    Collectors.counting()
                ))
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() > 2)
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue
                ));
    }

    /**
     * Question 10: Create a summary statistics of salaries
     */
    public static DoubleSummaryStatistics getSalarySummaryStatistics(List<Employee> employees) {
        return employees.stream()
                .collect(Collectors.summarizingDouble(Employee::getSalary));
    }

    @Test
    @Disabled
    void testEmployeeStreamOperations() {
        List<Employee> employees = getEmployees();

        // Test 1: High paid engineers
        List<Employee> highPaidEngineers = findHighPaidEngineers(employees);
        Assertions.assertEquals(4, highPaidEngineers.size());
        Assertions.assertTrue(highPaidEngineers.stream()
                .allMatch(emp -> "Engineering".equals(emp.getDepartment()) && emp.getSalary() > 80000));

        // Test 2: Average salary by department
        Map<String, Double> avgSalaries = getAverageSalaryByDepartment(employees);
        Assertions.assertTrue(avgSalaries.containsKey("Engineering"));
        Assertions.assertTrue(avgSalaries.containsKey("Marketing"));
        Assertions.assertTrue(avgSalaries.containsKey("HR"));

        // Test 3: Highest paid by department
        Map<String, Optional<Employee>> highestPaid = getHighestPaidByDepartment(employees);
        Assertions.assertTrue(highestPaid.get("Engineering").isPresent());
        Assertions.assertEquals("Grace", highestPaid.get("Engineering").get().getName());

        // Test 4: Sorted employee names
        List<String> sortedNames = getSortedEmployeeNames(employees);
        Assertions.assertEquals("Grace", sortedNames.get(0)); // Highest salary

        // Test 5: Second highest salary
        Optional<Double> secondHighest = getSecondHighestSalary(employees);
        Assertions.assertTrue(secondHighest.isPresent());
        Assertions.assertEquals(90000.0, secondHighest.get(), 0.01);

        // Test 6: Partition by high earners
        Map<Boolean, List<Employee>> partitioned = partitionByHighEarners(employees);
        Assertions.assertTrue(partitioned.get(true).size() > 0);
        Assertions.assertTrue(partitioned.get(false).size() > 0);
    }
}