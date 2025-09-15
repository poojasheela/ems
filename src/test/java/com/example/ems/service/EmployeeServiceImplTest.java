package com.example.ems.service;

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
import com.example.ems.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.vault.core.VaultTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import com.example.ems.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private EmployeeMapper mapper;

    @Mock
    private PasswordEncoder passwordEncoder;


    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee employee;
    private Department department;
    private EmployeeDTO dto;

    @BeforeEach
    void setup() {
        department = new Department("dept1", "HR", Instant.now(), Instant.now());

        employee = new Employee();
        employee.setId("emp1");
        employee.setFullName("John Doe");
        employee.setContactEmail("john@example.com");
        employee.setPassword("encodedPass");
        employee.setRole("USER");
        employee.setDepartmentId("dept1");
        employee.setCreatedTimestamp(Instant.now());
        employee.setLastUpdatedTimestamp(Instant.now());

        dto = new EmployeeDTO();
        dto.setName("John Doe");
        dto.setEmail("john@example.com");
        dto.setPassword("pass123");
        dto.setRole("USER");
        dto.setDepartmentName("HR");
    }

    @Test
    void testCreate_Success() {
        when(employeeRepository.findByContactEmail(anyString())).thenReturn(Flux.empty());
        when(departmentRepository.findByNameIgnoreCase("HR")).thenReturn(Mono.just(department));
        when(passwordEncoder.encode("pass123")).thenReturn("encodedPass");
        when(employeeRepository.save(any(Employee.class))).thenReturn(Mono.just(employee));
        when(departmentRepository.findById("dept1")).thenReturn(Mono.just(department));
        when(mapper.toDTO(any(Employee.class))).thenReturn(dto);

        StepVerifier.create(employeeService.create(dto))
                .expectNextMatches(response ->
                        response.getMessage().equals("Employee created") &&
                                ((EmployeeDTO) response.getData()).getName().equals("John Doe"))
                .verifyComplete();

        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void testCreate_EmailConflict() {
        when(employeeRepository.findByContactEmail("john@example.com")).thenReturn(Flux.just(employee));

        StepVerifier.create(employeeService.create(dto))
                .expectError(DataConflictException.class)
                .verify();
    }

    @Test
    void testCreate_DepartmentNotFound() {
        when(employeeRepository.findByContactEmail(anyString())).thenReturn(Flux.empty());
        when(departmentRepository.findByNameIgnoreCase("HR")).thenReturn(Mono.empty());

        StepVerifier.create(employeeService.create(dto))
                .expectError(InvalidRequestException.class)
                .verify();
    }

    @Test
    void testUpdate_Success() {
        when(employeeRepository.findById("emp1")).thenReturn(Mono.just(employee));
        when(departmentRepository.findByNameIgnoreCase("HR")).thenReturn(Mono.just(department));
        when(passwordEncoder.encode("pass123")).thenReturn("encodedPass");
        when(employeeRepository.save(any(Employee.class))).thenReturn(Mono.just(employee));
        when(departmentRepository.findById("dept1")).thenReturn(Mono.just(department));
        when(mapper.toDTO(any(Employee.class))).thenReturn(dto);

        StepVerifier.create(employeeService.update("emp1", dto))
                .expectNextMatches(response ->
                        response.getMessage().equals("Employee updated") &&
                                ((EmployeeDTO) response.getData()).getName().equals("John Doe"))
                .verifyComplete();
    }

    @Test
    void testUpdate_EmployeeNotFound() {
        when(employeeRepository.findById("emp1")).thenReturn(Mono.empty());

        StepVerifier.create(employeeService.update("emp1", dto))
                .expectError(EmployeeNotFoundException.class)
                .verify();
    }

    @Test
    void testDelete_Success() {
        when(employeeRepository.findById("emp1")).thenReturn(Mono.just(employee));
        when(employeeRepository.delete(employee)).thenReturn(Mono.empty());

        StepVerifier.create(employeeService.delete("emp1"))
                .expectNextMatches(response -> response.getMessage().equals("Employee deleted"))
                .verifyComplete();

        verify(employeeRepository).delete(employee);
    }

    @Test
    void testDelete_NotFound() {
        when(employeeRepository.findById("emp1")).thenReturn(Mono.empty());

        StepVerifier.create(employeeService.delete("emp1"))
                .expectError(EmployeeNotFoundException.class)
                .verify();
    }

    @Test
    void testGetById_Success() {
        when(employeeRepository.findById("emp1")).thenReturn(Mono.just(employee));
        when(departmentRepository.findById("dept1")).thenReturn(Mono.just(department));
        when(mapper.toDTO(employee)).thenReturn(dto);

        StepVerifier.create(employeeService.getById("emp1"))
                .expectNextMatches(response -> ((EmployeeDTO) response.getData()).getName().equals("John Doe"))
                .verifyComplete();
    }

    @Test
    void testGetById_NotFound() {
        when(employeeRepository.findById("emp1")).thenReturn(Mono.empty());

        StepVerifier.create(employeeService.getById("emp1"))
                .expectError(EmployeeNotFoundException.class)
                .verify();
    }

    @Test
    void testGetByEmail_Success() {
        when(employeeRepository.findByContactEmail("john@example.com")).thenReturn(Flux.just(employee));
        when(departmentRepository.findById("dept1")).thenReturn(Mono.just(department));
        when(mapper.toDTO(employee)).thenReturn(dto);

        StepVerifier.create(employeeService.getByEmail(Optional.of("john@example.com")))
                .expectNextMatches(response -> {
                    List<?> data = (List<?>) response.getData();
                    return ((EmployeeDTO) data.get(0)).getEmail().equals("john@example.com");
                })
                .verifyComplete();
    }

    @Test
    void testGetByEmail_NotFound() {
        when(employeeRepository.findByContactEmail("john@example.com")).thenReturn(Flux.empty());

        StepVerifier.create(employeeService.getByEmail(Optional.of("john@example.com")))
                .expectError(EmployeeNotFoundException.class)
                .verify();
    }

    @Test
    void testGetByName_Success() {
        when(employeeRepository.findByFullName("John Doe")).thenReturn(Mono.just(employee));
        when(departmentRepository.findById("dept1")).thenReturn(Mono.just(department));
        when(mapper.toDTO(employee)).thenReturn(dto);

        StepVerifier.create(employeeService.getByName(Optional.of("John Doe")))
                .expectNextMatches(response -> ((EmployeeDTO) response.getData()).getName().equals("John Doe"))
                .verifyComplete();
    }

    @Test
    void testGetByName_NotFound() {
        when(employeeRepository.findByFullName("John Doe")).thenReturn(Mono.empty());

        StepVerifier.create(employeeService.getByName(Optional.of("John Doe")))
                .expectError(EmployeeNotFoundException.class)
                .verify();
    }

    @Test
    void testGetAll_Success() {
        when(employeeRepository.findAll()).thenReturn(Flux.just(employee));
        when(departmentRepository.findById("dept1")).thenReturn(Mono.just(department));
        when(mapper.toDTO(employee)).thenReturn(dto);

        StepVerifier.create(employeeService.getAll())
                .expectNextMatches(response -> !((List<?>) response.getData()).isEmpty())
                .verifyComplete();
    }

    @Test
    void testGetPaginated_Success() {
        when(employeeRepository.findAll()).thenReturn(Flux.just(employee));
        when(departmentRepository.findById("dept1")).thenReturn(Mono.just(department));
        when(mapper.toDTO(employee)).thenReturn(dto);

        StepVerifier.create(employeeService.getPaginated(Optional.of(0), Optional.of(1)))
                .expectNextMatches(response -> !((List<?>) response.getData()).isEmpty())
                .verifyComplete();
    }
}
