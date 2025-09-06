package com.example.ems.controller;
import com.example.ems.dto.DepartmentDTO;
import com.example.ems.response.Response;
import com.example.ems.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/ems/department")
@Tag(name = "Department API", description = "Reactive endpoints for managing departments using MongoDB")
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping("/add")
    @Operation(summary = "Create a new department", description = "Adds a new department to MongoDB")
    public Mono<Response> createDepartment(@Valid @RequestBody DepartmentDTO dto) {
        log.info("Request received to create department: {}", dto);
        return departmentService.create(dto);
    }


    @PutMapping("/{id}")
    public Mono<Response> updateDepartment(
            @PathVariable String id,
            @Valid @RequestBody DepartmentDTO dto) {
        log.info("Department update requested for ID {}: {}", id, dto);
        return departmentService.update(id, dto);
    }


    @GetMapping
    @Operation(summary = "Get all departments", description = "Fetches all departments from MongoDB")
    public  Flux<DepartmentDTO> getAllDepartments() {
        log.info("Request received to fetch all departments");
        return departmentService.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get department by ID", description = "Fetches department details by ID from MongoDB")
    public Mono<Response> getDepartmentById(@PathVariable String id) {
        log.info("Request received to fetch department with ID {}", id);
        return departmentService.getById(id);
    }

    @GetMapping("/byName")
    @Operation(summary = "Get department by name", description = "Fetches department details by name from MongoDB")
    public Mono<Response> getByName(@RequestParam Optional<String> name) {
        log.info("Request received to fetch department by name: {}", name.orElse("N/A"));
        return departmentService.getByName(name);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a department", description = "Deletes a department by ID in MongoDB")
    public Mono<Response> deleteDepartment(@PathVariable String id) {
        log.info("Request received to delete department {}", id);
        return departmentService.delete(id);
    }
}