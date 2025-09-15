package com.example.ems.controller;
import com.example.ems.dto.DepartmentDTO;
import com.example.ems.response.Response;
import com.example.ems.service.DepartmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.vault.core.VaultTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DepartmentControllerTest {

    @Mock
    private DepartmentService departmentService;

    @InjectMocks
    private DepartmentController departmentController;

    private WebTestClient webTestClient;

    private DepartmentDTO sampleDto;
    private Response sampleResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        webTestClient = WebTestClient.bindToController(departmentController).build();

        sampleDto = new DepartmentDTO();
        sampleDto.setName("HR");

        sampleResponse = new Response(
                Instant.now(),
                200,
                "Department operation successful",
                sampleDto
        );
    }

    @Test
    void testCreateDepartment() {
        when(departmentService.create(any(DepartmentDTO.class)))
                .thenReturn(Mono.just(sampleResponse));

        webTestClient.post()
                .uri("/ems/department/add")
                .bodyValue(sampleDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Response.class)
                .value(r -> {
                    assertEquals(200, r.getStatus());
                    assertEquals("Department operation successful", r.getMessage());
                });
    }

    @Test
    void testGetAllDepartments() {
        when(departmentService.getAll())
                .thenReturn(Flux.just(sampleDto));

        webTestClient.get()
                .uri("/ems/department")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(DepartmentDTO.class)
                .hasSize(1)
                .value(list -> assertEquals("HR", list.get(0).getName()));
    }

    @Test
    void testGetDepartmentById() {
        when(departmentService.getById("1"))
                .thenReturn(Mono.just(sampleResponse));

        webTestClient.get()
                .uri("/ems/department/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Response.class)
                .value(r -> assertEquals(200, r.getStatus()));
    }

//    @Test
//    void testGetDepartmentByName() {
//        when(departmentService.getByName(Optional.of("HR")))
//                .thenReturn(Mono.just(sampleResponse));
//
//        webTestClient.get()
//                .uri(uriBuilder -> uriBuilder.path("/ems/department/byName")
//                        .queryParam("name", "HR").build())
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody(Response.class)
//                .value(r -> assertEquals("HR", ((DepartmentDTO) r.getData()).getName()));
//    }

    @Test
    void testUpdateDepartment() {
        when(departmentService.update(any(String.class), any(DepartmentDTO.class)))
                .thenReturn(Mono.just(sampleResponse));

        webTestClient.put()
                .uri("/ems/department/1")
                .bodyValue(sampleDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Response.class)
                .value(r -> assertEquals(200, r.getStatus()));
    }

    @Test
    void testDeleteDepartment() {
        when(departmentService.delete("1"))
                .thenReturn(Mono.just(sampleResponse));

        webTestClient.delete()
                .uri("/ems/department/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Response.class)
                .value(r -> assertEquals(200, r.getStatus()));
    }
}
