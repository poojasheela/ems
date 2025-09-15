package com.example.ems.controller;

import com.example.ems.dto.EmployeeDTO;
import com.example.ems.response.Response;
import com.example.ems.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.vault.core.VaultTemplate;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {
    @MockBean
    private VaultTemplate vaultTemplate;

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    private EmployeeDTO employeeDTO;
    private Response successResponse;

    @BeforeEach
    void setup() {
        employeeDTO = new EmployeeDTO(
                "1",
                "Mike",
                "mike@example.com",
                "HR",
                "password123",
                "ROLE_USER",
                Instant.now(),
                Instant.now()
        );

        successResponse = Response.success("Employee created with ID: 1", employeeDTO);
    }

    // ---------- CREATE ----------
    @Test
    void testCreateEmployee_Success() {
        when(employeeService.create(any(EmployeeDTO.class))).thenReturn(Mono.just(successResponse));

        StepVerifier.create(employeeController.createEmployee(employeeDTO))
                .expectNextMatches(response ->
                        response.getStatus() == 200 &&
                                response.getMessage().contains("Employee created with ID: 1"))
                .verifyComplete();
    }

    @Test
    void testCreateEmployee_Failure() {
        when(employeeService.create(any(EmployeeDTO.class)))
                .thenReturn(Mono.just(Response.error("Email already exists", 409, null)));

        StepVerifier.create(employeeController.createEmployee(employeeDTO))
                .expectNextMatches(response ->
                        response.getStatus() == 409 &&
                                response.getMessage().equals("Email already exists"))
                .verifyComplete();
    }

    // ---------- UPDATE ----------
    @Test
    void testUpdateEmployee_Success() {
        when(employeeService.update(eq("1"), any(EmployeeDTO.class)))
                .thenReturn(Mono.just(Response.success("Employee updated with ID: 1", employeeDTO)));

        StepVerifier.create(employeeController.updateEmployee("1", employeeDTO))
                .expectNextMatches(response ->
                        response.getStatus() == 200 &&
                                response.getMessage().contains("Employee updated with ID: 1"))
                .verifyComplete();
    }

    @Test
    void testUpdateEmployee_NotFound() {
        when(employeeService.update(eq("1"), any(EmployeeDTO.class)))
                .thenReturn(Mono.just(Response.error("Employee not found with ID: 1", 404, null)));

        StepVerifier.create(employeeController.updateEmployee("1", employeeDTO))
                .expectNextMatches(response ->
                        response.getStatus() == 404 &&
                                response.getMessage().equals("Employee not found with ID: 1"))
                .verifyComplete();
    }

    // ---------- DELETE ----------
    @Test
    void testDeleteEmployee_Success() {
        when(employeeService.delete("1"))
                .thenReturn(Mono.just(Response.success("Employee deleted with ID: 1", null)));

        StepVerifier.create(employeeController.deleteEmployee("1"))
                .expectNextMatches(response ->
                        response.getStatus() == 200 &&
                                response.getMessage().equals("Employee deleted with ID: 1"))
                .verifyComplete();
    }

    @Test
    void testDeleteEmployee_NotFound() {
        when(employeeService.delete("1"))
                .thenReturn(Mono.just(Response.error("Employee not found with ID: 1", 404, null)));

        StepVerifier.create(employeeController.deleteEmployee("1"))
                .expectNextMatches(response ->
                        response.getStatus() == 404 &&
                                response.getMessage().equals("Employee not found with ID: 1"))
                .verifyComplete();
    }

    // ---------- GET BY ID ----------
    @Test
    void testGetEmployeeById_Success() {
        when(employeeService.getById("1"))
                .thenReturn(Mono.just(Response.success("Employee fetched", employeeDTO)));

        StepVerifier.create(employeeController.getById("1"))
                .expectNextMatches(response ->
                        response.getStatus() == 200 &&
                                ((EmployeeDTO) response.getData()).getName().equals("Mike"))
                .verifyComplete();
    }

    @Test
    void testGetEmployeeById_NotFound() {
        when(employeeService.getById("1"))
                .thenReturn(Mono.just(Response.error("Employee not found with ID: 1", 404, null)));

        StepVerifier.create(employeeController.getById("1"))
                .expectNextMatches(response ->
                        response.getStatus() == 404 &&
                                response.getMessage().equals("Employee not found with ID: 1"))
                .verifyComplete();
    }

    // ---------- GET ALL ----------
//    @Test
//    void testGetAllEmployees_Success() {
//        EmployeeDTO emp2 = new EmployeeDTO(
//                "2", "John", "john@example.com", "IT", "pass123", "ROLE_ADMIN", Instant.now(), Instant.now());
//
//        when(employeeService.getAll())
//                .thenReturn(Flux.just(
//                        Response.success("Employee 1", employeeDTO),
//                        Response.success("Employee 2", emp2)
//                ));
//
//        StepVerifier.create(employeeController.getAllEmployees())
//                .expectNextMatches(response -> ((EmployeeDTO) response.getData()).getFullName().equals("Mike"))
//                .expectNextMatches(response -> ((EmployeeDTO) response.getData()).getFullName().equals("John"))
//                .verifyComplete();
//    }
//
//    @Test
//    void testGetAllEmployees_Empty() {
//        when(employeeService.getAll()).thenReturn(Flux.empty());
//
//        StepVerifier.create(employeeController.getAllEmployees())
//                .verifyComplete();
//    }

    // ---------- FILTER BY NAME ----------
    @Test
    void testGetByName_Success() {
        when(employeeService.getByName(Optional.of("Mike")))
                .thenReturn(Mono.just(Response.success("Employee found", employeeDTO)));

        StepVerifier.create(employeeController.getByName(Optional.of("Mike")))
                .expectNextMatches(response ->
                        response.getStatus() == 200 &&
                                ((EmployeeDTO) response.getData()).getName().equals("Mike"))
                .verifyComplete();
    }

    @Test
    void testGetByName_NotFound() {
        when(employeeService.getByName(Optional.of("Unknown")))
                .thenReturn(Mono.just(Response.error("No employees found", 404, null)));

        StepVerifier.create(employeeController.getByName(Optional.of("Unknown")))
                .expectNextMatches(response ->
                        response.getStatus() == 404 &&
                                response.getMessage().equals("No employees found"))
                .verifyComplete();
    }

    // ---------- FILTER BY EMAIL ----------
    @Test
    void testGetByEmail_Success() {
        when(employeeService.getByEmail(Optional.of("example.com")))
                .thenReturn(Mono.just(Response.success("Employee found", employeeDTO)));

        StepVerifier.create(employeeController.getByEmailDomain(Optional.of("example.com")))
                .expectNextMatches(response ->
                        response.getStatus() == 200 &&
                                ((EmployeeDTO) response.getData()).getEmail().equals("mike@example.com"))
                .verifyComplete();
    }

    @Test
    void testGetByEmail_NotFound() {
        when(employeeService.getByEmail(Optional.of("invalid.com")))
                .thenReturn(Mono.just(Response.error("No employees found", 404, null)));

        StepVerifier.create(employeeController.getByEmailDomain(Optional.of("invalid.com")))
                .expectNextMatches(response ->
                        response.getStatus() == 404 &&
                                response.getMessage().equals("No employees found"))
                .verifyComplete();
    }

    // ---------- PAGINATED ----------
    @Test
    void testGetPaginated_Success() {
        when(employeeService.getPaginated(Optional.of(0), Optional.of(5)))
                .thenReturn(Mono.just(Response.success("Page fetched", List.of(employeeDTO))));

        StepVerifier.create(employeeController.getPaginatedEmployees(Optional.of(0), Optional.of(5)))
                .expectNextMatches(response ->
                        response.getStatus() == 200 &&
                                !((List<?>) response.getData()).isEmpty())
                .verifyComplete();
    }

    @Test
    void testGetPaginated_NotFound() {
        when(employeeService.getPaginated(Optional.of(99), Optional.of(5)))
                .thenReturn(Mono.just(Response.error("No employees found", 404, null)));

        StepVerifier.create(employeeController.getPaginatedEmployees(Optional.of(99), Optional.of(5)))
                .expectNextMatches(response ->
                        response.getStatus() == 404 &&
                                response.getMessage().equals("No employees found"))
                .verifyComplete();
    }
}
