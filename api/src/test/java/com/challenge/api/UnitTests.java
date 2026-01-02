package com.challenge.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.challenge.api.controller.EmployeeController;
import com.challenge.api.model.Employee;
import com.challenge.api.model.MockEmployee;
import com.challenge.api.service.EmployeeService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

@SpringBootTest
public class UnitTests {

    @Autowired
    private EmployeeController controller;

    @Autowired
    private EmployeeService service;

    @Test
    void contextLoads() throws Exception {
        assertThat(controller).isNotNull();
        assertThat(service).isNotNull();
    }

    @Test
    void returnsAllEmployees() {
        List<Employee> employees = controller.getAllEmployees();

        assertThat(employees).isNotNull();
        assertThat(employees).hasSize(4);
        assertThat(employees).extracting("firstName").contains("Peter", "Bruce", "Tony", "Clark");
    }

    @Test
    void getAllEmployeesReturnsUpdatedList() {
        List<Employee> list1 = controller.getAllEmployees();
        List<Employee> list2 = controller.getAllEmployees();

        assertThat(list1).isNotSameAs(list2);
        assertThat(list1).hasSize(list2.size());
    }

    @Test
    void includesNewCreatedEmployee() {
        int initialSize = controller.getAllEmployees().size();

        MockEmployee newEmployee =
                new MockEmployee("Diana", "Prince", "dprince@themyscira.com", "Ambassador", 80000, 30);
        controller.createEmployee(newEmployee);

        List<Employee> updatedList = controller.getAllEmployees();
        assertThat(updatedList).hasSize(initialSize + 1);
    }

    @Test
    void validUuidReturnsEmployee() {
        List<Employee> employees = controller.getAllEmployees();
        Employee firstEmployee = employees.get(0);
        UUID validUuid = firstEmployee.getUuid();

        ResponseEntity<Employee> response = controller.getEmployeeByUuid(validUuid);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getUuid()).isEqualTo(validUuid);
        assertThat(response.getBody().getFirstName()).isEqualTo(firstEmployee.getFirstName());
    }

    @Test
    void invalidUuidReturns404() {
        UUID invalidUuid = UUID.randomUUID();

        ResponseEntity<Employee> response = controller.getEmployeeByUuid(invalidUuid);

        assertThat(response.getStatusCode().value()).isEqualTo(404);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void newEmployeeFindable() {
        Employee newEmployee = new MockEmployee("Barry", "Allen",
                "ballen@starlabs.com", "Forensic Scientist", 65000, 28);
        Employee created = controller.createEmployee(newEmployee);

        ResponseEntity<Employee> response = controller.getEmployeeByUuid(created.getUuid());

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getEmail()).isEqualTo("ballen@starlabs.com");
    }

    @Test
    void returnsCreatedEmployee() {
        Employee newEmployee = new MockEmployee("Diana", "Prince",
                "dprince@themyscira.com", "Ambassador", 80000, 30);

        Employee created = controller.createEmployee(newEmployee);

        assertThat(created).isNotNull();
        assertThat(created.getFirstName()).isEqualTo("Diana");
        assertThat(created.getLastName()).isEqualTo("Prince");
        assertThat(created.getEmail()).isEqualTo("dprince@themyscira.com");
        assertThat(created.getUuid()).isNotNull();
        assertThat(created.getContractHireDate()).isNotNull();
    }

    @Test
    void generatesUuid() {
        Employee newEmployee = new MockEmployee("John", "Cena",
                "johncena@bigguy.com", "Actor", 120000, 42);
        newEmployee.setUuid(null);

        Employee created = controller.createEmployee(newEmployee);

        assertThat(created.getUuid()).isNotNull();
    }

    @Test
    void setsHireDate() {
        Employee newEmployee = new MockEmployee("Stevie", "Wonder",
                "steven-wonder@wonderland.com", "Singer", 98200, 28);
        newEmployee.setContractHireDate(null);

        Employee created = controller.createEmployee(newEmployee);

        assertThat(created.getContractHireDate()).isNotNull();
    }

    @Test
    void testCreateEmployee_DuplicateEmail_StillCreates() {

        Employee employee1 = new MockEmployee("John", "Doe",
                "jdoe@gmail.com", "accountant", 78000, 36);
        controller.createEmployee(employee1);

        Employee employee2 = new MockEmployee("Jane", "Doe",
                "jdoe@gmail.com", "lawyer", 98000, 45);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            controller.createEmployee(employee2);
        });

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(exception.getReason()).contains("Employee with this email already exists");
    }

    @Test
    void putItAllTogetherTest() {
        int initialCount = controller.getAllEmployees().size();

        Employee newEmployee = new MockEmployee("Bruce", "Banner", "BBanner47@hulkhulk.com", "Scientist", 85000, 33);
        Employee created = controller.createEmployee(newEmployee);
        UUID newUuid = created.getUuid();

        ResponseEntity<Employee> response = controller.getEmployeeByUuid(newUuid);
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody().getEmail()).isEqualTo("BBanner47@hulkhulk.com");

        List<Employee> allEmployees = controller.getAllEmployees();
        assertThat(allEmployees).hasSize(initialCount + 1);
        assertThat(allEmployees).extracting("email").contains("BBanner47@hulkhulk.com");
    }
}
