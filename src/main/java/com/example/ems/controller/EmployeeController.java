package com.example.ems.controller;

import com.example.ems.dto.EmployeeDTO;
import com.example.ems.response.Response;
import com.example.ems.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Optional;

import java.util.Optional;
@Slf4j
@RestController
@RequestMapping("/ems/employee")
@RequiredArgsConstructor
@Tag(name = "Employee API", description = "Endpoints for managing employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping("/add")
    @Operation(summary = "Create a new employee", description = "Adds a new employee to the database")
    public Mono<Response> createEmployee(@Valid @RequestBody EmployeeDTO employeeDTO) {
        log.info("Employee creation : {}", employeeDTO);
        return employeeService.create(employeeDTO);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an employee", description = "Updates employee details by ID")
    public Mono<Response> updateEmployee(@PathVariable String id, @Valid @RequestBody EmployeeDTO employeeDTO) {
        log.info("Employee update requested for ID {}: {}", id, employeeDTO);
        return employeeService.update(id, employeeDTO);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an employee", description = "Deletes employee by ID")
    public Mono<Response> deleteEmployee(@PathVariable String id) {
        log.info("Employee delete requested for ID {}", id);
        return employeeService.delete(id);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get employee by ID", description = "Fetches employee details by ID")
    public Mono<Response> getById(@PathVariable String id) {
        log.info("Fetching employee with ID {}", id);
        return employeeService.getById(id);
    }

    @GetMapping("/filter-by-name")
    @Operation(summary = "Filter by name", description = "Fetch employees matching the given name")
    public Mono<Response> getByName(@RequestParam Optional<String> name) {
        log.info("Fetching employee(s) by name: {}", name.orElse("N/A"));
        return employeeService.getByName(name);
    }

    @GetMapping("/filter-by-email")
    @Operation(summary = "Filter by email domain", description = "Fetch employees having email ending with the given domain")
    public Mono<Response> getByEmailDomain(@RequestParam Optional<String> domain) {
        log.info("Fetching employee(s) by email domain: {}", domain.orElse("N/A"));
        return employeeService.getByEmail(domain);
    }

    @GetMapping
    @Operation(summary = "Get all employees", description = "Fetch all employees")
    public Mono<Response> getAllEmployees() {
        log.info("Fetching all employees");
        return employeeService.getAll();
    }


    @GetMapping("/filter-by-page")
    @Operation(summary = "Get paginated employees", description = "Fetch employees with optional pagination parameters")
    public Mono<Response> getPaginatedEmployees(
            @RequestParam Optional<Integer> page,
            @RequestParam Optional<Integer> size) {

        log.info("Fetching employees with pagination. Page: {}, Size: {}", page.orElse(0), size.orElse(10));

        return employeeService.getPaginated(page, size);
    }

}