package com.example.ems.service.impl;

import com.example.ems.dto.EmployeeDTO;
import com.example.ems.entity.Department;
import com.example.ems.entity.Employee;
import com.example.ems.exception.DataConflictException;
import com.example.ems.exception.EmployeeNotFoundException;
import com.example.ems.exception.InvalidRequestException;
import com.example.ems.mapper.EmployeeMapper;
import com.example.ems.repository.DepartmentRepository;
import com.example.ems.repository.EmployeeRepository;
import com.example.ems.response.Response;
import com.example.ems.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.Optional;
@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeMapper mapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Mono<Response> create(EmployeeDTO dto) {
        return employeeRepository.findByContactEmail(dto.getEmail())
                .collectList()
                .flatMap(list -> {
                    if (!list.isEmpty()) {
                        return Mono.error(new DataConflictException("Email already in use."));
                    }

                    Mono<Department> deptMono = (dto.getDepartmentName() != null)
                            ? departmentRepository.findByNameIgnoreCase(dto.getDepartmentName())
                            .switchIfEmpty(Mono.error(new InvalidRequestException("Department not found")))
                            : Mono.empty();

                    return deptMono.flatMap(dept -> {
                        Employee employee = new Employee();
                        employee.setFullName(dto.getName());
                        employee.setContactEmail(dto.getEmail().toLowerCase());
                        employee.setPassword(passwordEncoder.encode(dto.getPassword()));
                        employee.setRole(dto.getRole());
                        employee.setDepartment(dept);

                        return employeeRepository.save(employee)
                                .map(saved -> Response.success("Employee created", mapper.toDTO(saved)));
                    });
                });
    }

    @Override
    public Mono<Response> update(String id, EmployeeDTO dto) {
        return employeeRepository.findById(id)
                .switchIfEmpty(Mono.error(new EmployeeNotFoundException("Employee not found")))
                .flatMap(existing -> {
                    Mono<Department> deptMono = dto.getDepartmentName() != null
                            ? departmentRepository.findByNameIgnoreCase(dto.getDepartmentName())
                            .switchIfEmpty(Mono.error(new InvalidRequestException("Department not found")))
                            : Mono.empty();

                    return deptMono.defaultIfEmpty(null)
                            .flatMap(dept -> {
                                existing.setFullName(dto.getName());
                                existing.setContactEmail(dto.getEmail().toLowerCase());
                                existing.setPassword(passwordEncoder.encode(dto.getPassword()));
                                existing.setRole(dto.getRole());
                                existing.setDepartment(dept);

                                return employeeRepository.save(existing)
                                        .map(updated -> Response.success("Employee updated", mapper.toDTO(updated)));
                            });
                });
    }

    @Override
    public Mono<Response> delete(String id) {
        return employeeRepository.findById(id)
                .switchIfEmpty(Mono.error(new EmployeeNotFoundException("Employee not found")))
                .flatMap(existing -> employeeRepository.delete(existing)
                        .thenReturn(Response.success("Employee deleted", null)));
    }

    @Override
    public Mono<Response> getById(String id) {
        return employeeRepository.findById(id)
                .switchIfEmpty(Mono.error(new EmployeeNotFoundException("Employee not found")))
                .map(emp -> Response.success("Employee fetched", mapper.toDTO(emp)));
    }

    @Override
    public Mono<Response> getByEmail(Optional<String> email) {
        if (email.isPresent()) {
            return employeeRepository.findByContactEmail(email.get())
                    .collectList()
                    .flatMap(list -> list.isEmpty()
                            ? Mono.error(new EmployeeNotFoundException("No employees found with email: " + email.get()))
                            : Mono.just(Response.success("Employee(s) fetched by email", mapper.toDTOList(list))));
        } else {
            return employeeRepository.findAll()
                    .collectList()
                    .map(list -> Response.success("All employees fetched", mapper.toDTOList(list)));
        }
    }

    @Override
    public Mono<Response> getByName(Optional<String> name) {
        if (name.isPresent() && !name.get().isBlank()) {
            return employeeRepository.findByFullName(name.get())
                    .switchIfEmpty(Mono.error(new EmployeeNotFoundException("No employee with this name")))
                    .map(emp -> Response.success("Employee fetched", mapper.toDTO(emp)));
        } else {
            return employeeRepository.findAll()
                    .collectList()
                    .map(list -> Response.success("All employees fetched", mapper.toDTOList(list)));
        }
    }

    @Override
    public Mono<Response> getAll() {
        return employeeRepository.findAll()
                .collectList()
                .map(list -> Response.success("All employees fetched", mapper.toDTOList(list)));
    }
}
