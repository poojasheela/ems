package com.example.ems.service;
import com.example.ems.dto.DepartmentDTO;
import com.example.ems.entity.Department;
import com.example.ems.exception.DataConflictException;
import com.example.ems.exception.DepartmentNotFoundException;
import com.example.ems.repository.DepartmentRepository;
import com.example.ems.response.Response;
import com.example.ems.service.impl.DepartmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.vault.core.VaultTemplate;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import java.time.Instant;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.util.Optional;

class DepartmentServiceImplTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private DepartmentServiceImpl departmentService;

    private Department department;
    private DepartmentDTO dto;

    @MockBean
    private VaultTemplate vaultTemplate;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        dto = new DepartmentDTO("HR", Instant.now(), Instant.now());

        department = new Department();
        department.setId("1");
        department.setName("HR");
        department.setCreatedTimestamp(Instant.now());
        department.setLastUpdatedTimestamp(Instant.now());
    }

    @Test
    void testCreate_Success() {
        when(departmentRepository.existsByName("HR")).thenReturn(Mono.just(false));
        when(departmentRepository.save(any(Department.class))).thenReturn(Mono.just(department));

        Mono<Response> result = departmentService.create(dto);

        StepVerifier.create(result)
                .expectNextMatches(r -> r.getMessage().contains("Department created with name: HR"))
                .verifyComplete();
    }

    @Test
    void testCreate_Conflict() {
        when(departmentRepository.existsByName("HR")).thenReturn(Mono.just(true));

        StepVerifier.create(departmentService.create(dto))
                .expectError(DataConflictException.class)
                .verify();
    }


    @Test
    void testUpdate_Success() {
        when(departmentRepository.findById("1")).thenReturn(Mono.just(department));
        when(departmentRepository.findByNameIgnoreCase("HR")).thenReturn(Mono.empty());
        when(departmentRepository.save(any(Department.class))).thenReturn(Mono.just(department));

        StepVerifier.create(departmentService.update("1", dto))
                .expectNextMatches(r -> r.getMessage().equals("Department updated"))
                .verifyComplete();
    }

    @Test
    void testUpdate_NotFound() {
        when(departmentRepository.findById("1")).thenReturn(Mono.empty());

        StepVerifier.create(departmentService.update("1", dto))
                .expectError(DepartmentNotFoundException.class)
                .verify();
    }

    @Test
    void testUpdate_Conflict() {
        Department otherDept = new Department();
        otherDept.setId("2");
        otherDept.setName("HR");

        when(departmentRepository.findById("1")).thenReturn(Mono.just(department));
        when(departmentRepository.findByNameIgnoreCase("HR")).thenReturn(Mono.just(otherDept));

        StepVerifier.create(departmentService.update("1", dto))
                .expectError(DataConflictException.class)
                .verify();
    }


    @Test
    void testDelete_Success() {
        when(departmentRepository.findById("1")).thenReturn(Mono.just(department));
        when(departmentRepository.delete(department)).thenReturn(Mono.empty());

        StepVerifier.create(departmentService.delete("1"))
                .expectNextMatches(r -> r.getMessage().contains("Department deleted successfully"))
                .verifyComplete();
    }

    @Test
    void testDelete_NotFound() {
        when(departmentRepository.findById("1")).thenReturn(Mono.empty());

        StepVerifier.create(departmentService.delete("1"))
                .expectError(DepartmentNotFoundException.class)
                .verify();
    }


    @Test
    void testGetById_Success() {
        when(departmentRepository.findById("1")).thenReturn(Mono.just(department));

        StepVerifier.create(departmentService.getById("1"))
                .expectNextMatches(r -> r.getMessage().equals("Department fetched"))
                .verifyComplete();
    }

    @Test
    void testGetById_NotFound() {
        when(departmentRepository.findById("1")).thenReturn(Mono.empty());

        StepVerifier.create(departmentService.getById("1"))
                .expectError(DepartmentNotFoundException.class)
                .verify();
    }

    @Test
    void testGetAll_WithDepartments() {
        when(departmentRepository.findAll()).thenReturn(Flux.just(department));

        StepVerifier.create(departmentService.getAll())
                .expectNextMatches(dto -> dto.getName().equals("HR"))
                .verifyComplete();
    }

    @Test
    void testGetAll_Empty() {
        when(departmentRepository.findAll()).thenReturn(Flux.empty());

        StepVerifier.create(departmentService.getAll())
                .verifyComplete();
    }


    @Test
    void testGetByName_Found() {
        when(departmentRepository.findByNameIgnoreCase("HR")).thenReturn(Mono.just(department));

        StepVerifier.create(departmentService.getByName(Optional.of("HR")))
                .expectNextMatches(r -> r.getMessage().equals("Department found by name"))
                .verifyComplete();
    }

    @Test
    void testGetByName_NotFound() {
        when(departmentRepository.findByNameIgnoreCase("HR")).thenReturn(Mono.empty());

        StepVerifier.create(departmentService.getByName(Optional.of("HR")))
                .expectError(DepartmentNotFoundException.class)
                .verify();
    }

    @Test
    void testGetByName_AllDepartments() {
        when(departmentRepository.findAll()).thenReturn(Flux.just(department));

        StepVerifier.create(departmentService.getByName(Optional.empty()))
                .expectNextMatches(r -> r.getMessage().equals("All departments fetched")
                        && !((java.util.List<?>) r.getData()).isEmpty())
                .verifyComplete();
    }

    @Test
    void testGetByName_AllDepartments_Empty() {
        when(departmentRepository.findAll()).thenReturn(Flux.empty());

        StepVerifier.create(departmentService.getByName(Optional.empty()))
                .expectNextMatches(r -> r.getMessage().equals("All departments fetched")
                        && ((java.util.List<?>) r.getData()).isEmpty())
                .verifyComplete();
    }
}
