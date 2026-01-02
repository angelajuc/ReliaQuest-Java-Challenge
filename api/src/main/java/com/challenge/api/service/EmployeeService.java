package com.challenge.api.service;

import com.challenge.api.model.Employee;
import com.challenge.api.model.MockEmployee;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

    private final Map<UUID, Employee> employeeMap = new ConcurrentHashMap<>();

    public EmployeeService() {
        createMockEmployees();
    }

    private void createMockEmployees() {
        Employee employee1 =
                new MockEmployee("Peter", "Parker", "pparker360@mit.edu", "freelance photographer", 44000, 22);
        Employee employee2 = new MockEmployee(
                "Bruce", "Wayne", "bruce.wayne@wayneenterprises.com", "billionaire philanthropist", 2000000, 34);
        Employee employee3 = new MockEmployee("Tony", "Stark", "tonystark@starkindustries.com", "CEO", 1500000, 38);
        Employee employee4 = new MockEmployee("Clark", "Kent", "CKent@dailyplanet.com", "reporter", 60000, 25);

        employeeMap.put(employee1.getUuid(), employee1);
        employeeMap.put(employee2.getUuid(), employee2);
        employeeMap.put(employee3.getUuid(), employee3);
        employeeMap.put(employee4.getUuid(), employee4);
    }

    public List<Employee> findAll() {
        return new ArrayList<>(employeeMap.values());
    }

    public Employee findByUuid(UUID uuid) {
        return employeeMap.get(uuid);
    }

    public Employee addEmployee(Employee employee) {

        if (employee.getFirstName() == null || employee.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid first name");
        }

        if (employee.getLastName() == null || employee.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid last name");
        }

        if (employee.getEmail() == null || employee.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid email");
        }

        if (!employee.getEmail().contains("@")) {
            throw new IllegalArgumentException("Invalid email");
        }

        if (employee.getSalary() != null && employee.getSalary() < 0) {
            throw new IllegalArgumentException("Salary cannot be negative");
        }

        if (employee.getAge() != null && employee.getAge() < 0) {
            throw new IllegalArgumentException("Invalid age");
        }

        if (employeeExistsEmailSearch(employee.getEmail())) {
            throw new IllegalArgumentException("Employee with this email already exists");
        }

        if (employee.getJobTitle() == null) {
            throw new IllegalArgumentException("Invalid job title");
        }

        if (employee.getUuid() == null) {
            employee.setUuid(UUID.randomUUID());
        }
        if (employee.getContractHireDate() == null) {
            employee.setContractHireDate(Instant.now());
        }
        employeeMap.put(employee.getUuid(), employee);
        return employee;
    }

    public boolean employeeExistsEmailSearch(String email) {
        if (email == null) {
            return false;
        }
        for (Employee employee : employeeMap.values()) {
            if (email.equals(employee.getEmail())) {
                return true;
            }
        }
        return false;
    }
}
