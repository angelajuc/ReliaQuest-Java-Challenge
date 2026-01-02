package com.challenge.api.controller;

import com.challenge.api.model.Employee;
import com.challenge.api.model.MockEmployee;
import com.challenge.api.service.EmployeeService;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * Fill in the missing aspects of this Spring Web REST Controller. Don't forget to add a Service layer.
 */
@RestController
@RequestMapping("/api/v1/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * @implNote Need not be concerned with an actual persistence layer. Generate mock Employee models as necessary.
     * @return One or more Employees.
     */
    @GetMapping
    public List<Employee> getAllEmployees() {
        return employeeService.findAll();
    }

    /**
     * @implNote Need not be concerned with an actual persistence layer. Generate mock Employee model as necessary.
     * @param uuid Employee UUID
     * @return Requested Employee if exists
     */
    @GetMapping("/{uuid}")
    public ResponseEntity<Employee> getEmployeeByUuid(@PathVariable UUID uuid) {

        if (uuid == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "UUID cant be null");
        }
        Employee employee = employeeService.findByUuid(uuid);
        if (employee != null) {
            return ResponseEntity.ok(employee);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * @implNote Need not be concerned with an actual persistence layer.
     * @param requestBody hint!
     * @return Newly created Employee
     */
    @PostMapping
    public Employee createEmployee(@RequestBody Object requestBody) {
        if (requestBody == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body cant be null");
        }
        if (!(requestBody instanceof Employee)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid employee format.");
        }

        Employee employee = (MockEmployee) requestBody;

        try {
            return employeeService.addEmployee(employee);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
