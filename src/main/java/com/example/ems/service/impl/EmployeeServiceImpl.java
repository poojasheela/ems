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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.Instant;
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
        return employeeRepository.findByContactEmail(dto.getEmail().toLowerCase())
                .collectList()
                .flatMap(list -> {
                    if (!list.isEmpty()) {
                        return Mono.error(new DataConflictException("Email already in use."));
                    }

                    Mono<String> deptIdMono = (dto.getDepartmentName() != null)
                            ? departmentRepository.findByNameIgnoreCase(dto.getDepartmentName())
                            .switchIfEmpty(Mono.error(new InvalidRequestException("Department not found")))
                            .map(Department::getId)
                            : Mono.justOrEmpty(null);

                    return deptIdMono.flatMap(deptId -> {
                        Employee employee = new Employee();
                        employee.setFullName(dto.getName());
                        employee.setContactEmail(dto.getEmail().toLowerCase());
                        employee.setPassword(passwordEncoder.encode(dto.getPassword()));
                        employee.setRole(dto.getRole());
                        employee.setDepartmentId(deptId);
                        employee.setCreatedTimestamp(Instant.now());
                        employee.setLastUpdatedTimestamp(Instant.now());

                        return employeeRepository.save(employee)
                                .flatMap(saved -> mapToDTO(saved, "Employee created"));
                    });
                });
    }

    @Override
    public Mono<Response> update(String id, EmployeeDTO dto) {
        return employeeRepository.findById(id)
                .switchIfEmpty(Mono.error(new EmployeeNotFoundException("Employee not found")))
                .flatMap(existing -> {
                    Mono<String> deptIdMono = (dto.getDepartmentName() != null)
                            ? departmentRepository.findByNameIgnoreCase(dto.getDepartmentName())
                            .switchIfEmpty(Mono.error(new InvalidRequestException("Department not found")))
                            .map(Department::getId)
                            : Mono.justOrEmpty(null);

                    return deptIdMono.flatMap(deptId -> {
                        existing.setFullName(dto.getName());
                        existing.setContactEmail(dto.getEmail().toLowerCase());
                        existing.setPassword(passwordEncoder.encode(dto.getPassword()));
                        existing.setRole(dto.getRole());
                        existing.setDepartmentId(deptId);
                        existing.setLastUpdatedTimestamp(Instant.now());

                        return employeeRepository.save(existing)
                                .flatMap(saved -> mapToDTO(saved, "Employee updated"));
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
                .flatMap(emp -> mapToDTO(emp, "Employee fetched"));
    }

    @Override
    public Mono<Response> getByEmail(Optional<String> email) {
        if (email.isPresent()) {
            return employeeRepository.findByContactEmail(email.get().toLowerCase())
                    .collectList()
                    .flatMap(list -> list.isEmpty()
                            ? Mono.error(new EmployeeNotFoundException("No employees found with email: " + email.get()))
                            : Flux.fromIterable(list)
                            .flatMap(this::mapToDTOMono)
                            .collectList()
                            .map(dtos -> Response.success("Employee(s) fetched by email", dtos)));
        } else {
            return getAll();
        }
    }

    @Override
    public Mono<Response> getByName(Optional<String> name) {
        if (name.isPresent() && !name.get().isBlank()) {
            return employeeRepository.findByFullName(name.get())
                    .switchIfEmpty(Mono.error(new EmployeeNotFoundException("No employee with this name")))
                    .flatMap(emp -> mapToDTO(emp, "Employee fetched"));
        } else {
            return getAll();
        }
    }

    @Override
    public Mono<Response> getAll() {
        return employeeRepository.findAll()
                .flatMap(this::mapToDTOMono)
                .collectList()
                .map(list -> Response.success("All employees fetched", list));
    }

    @Override
    public Mono<Response> getPaginated(Optional<Integer> pageOpt, Optional<Integer> sizeOpt) {
        int page = pageOpt.orElse(0);
        int size = sizeOpt.orElse(10);
        int skip = page * size;

        return employeeRepository.findAll()
                .skip(skip)
                .take(size)
                .flatMap(this::mapToDTOMono)
                .collectList()
                .map(list -> Response.success("Paginated employees fetched", list));
    }

    private Mono<Response> mapToDTO(Employee emp, String message) {
        if (emp.getDepartmentId() != null) {
            return departmentRepository.findById(emp.getDepartmentId())
                    .map(dep -> {
                        EmployeeDTO dto = mapper.toDTO(emp);
                        dto.setDepartmentName(dep.getName());
                        return Response.success(message, dto);
                    });
        } else {
            EmployeeDTO dto = mapper.toDTO(emp);
            dto.setDepartmentName(null);
            return Mono.just(Response.success(message, dto));
        }
    }

    private Mono<EmployeeDTO> mapToDTOMono(Employee emp) {
        if (emp.getDepartmentId() != null) {
            return departmentRepository.findById(emp.getDepartmentId())
                    .map(dep -> {
                        EmployeeDTO dto = mapper.toDTO(emp);
                        dto.setDepartmentName(dep.getName());
                        return dto;
                    });
        } else {
            EmployeeDTO dto = mapper.toDTO(emp);
            dto.setDepartmentName(null);
            return Mono.just(dto);
        }
    }
}
