package com.example.ems.service.impl;
import com.example.ems.dto.DepartmentDTO;
import com.example.ems.entity.Department;
import com.example.ems.exception.DataConflictException;
import com.example.ems.exception.DepartmentNotFoundException;
import com.example.ems.repository.DepartmentRepository;
import com.example.ems.response.Response;
import com.example.ems.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Override
    public Mono<Response> create(DepartmentDTO dto) {
        return departmentRepository.existsByName(dto.getName())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new DataConflictException("Department already exists with name: " + dto.getName()));
                    }
                    Department department = new Department();
                    department.setName(dto.getName());

                    return departmentRepository.save(department)
                            .map(saved -> Response.success("Department created with name: " + saved.getName(),
                                    new DepartmentDTO( saved.getName(), saved.getCreatedTimestamp(), saved.getLastUpdatedTimestamp())));
                });
    }

    @Override
    public Mono<Response> update(String id, DepartmentDTO dto) {
        return departmentRepository.findById(id)
                .switchIfEmpty(Mono.error(new DepartmentNotFoundException("Department not found with ID: " + id)))
                .flatMap(existing -> {
                    return departmentRepository.existsByName(dto.getName())
                            .flatMap(exists -> {
                                if (!existing.getName().equalsIgnoreCase(dto.getName()) && exists) {
                                    return Mono.error(new DataConflictException("Another department with name '" + dto.getName() + "' already exists."));
                                }
                                existing.setName(dto.getName());
                                return departmentRepository.save(existing)
                                        .map(updated -> {
                                            log.info("Updated department with ID: {}", id);
                                            return Response.success("Department updated", new DepartmentDTO( updated.getName(), updated.getCreatedTimestamp(), updated.getLastUpdatedTimestamp()));
                                        });
                            });
                });
    }

    @Override
    public Mono<Response> delete(String id) {
        return departmentRepository.findById(id)
                .switchIfEmpty(Mono.error(new DepartmentNotFoundException("Department not found with ID: " + id)))
                .flatMap(existing -> departmentRepository.delete(existing)
                        .thenReturn(Response.success("Department deleted with ID: " + id, null)));
    }

    @Override
    public Flux<DepartmentDTO> getAll() {
        return departmentRepository.findAll()
                .map(dept -> new DepartmentDTO(
                        dept.getName(),
                        dept.getCreatedTimestamp(),
                        dept.getLastUpdatedTimestamp()
                ));
    }


    @Override
    public Mono<Response> getById(String id) {
        return departmentRepository.findById(id)
                .switchIfEmpty(Mono.error(new DepartmentNotFoundException("Department not found with ID: " + id)))
                .map(dept -> Response.success("Department fetched", dept));
    }
    @Override
    public Mono<Response> getByName(Optional<String> name) {
        if (name.isPresent()) {
            return departmentRepository.findByNameIgnoreCase(name.get())
                    .switchIfEmpty(Mono.error(new DepartmentNotFoundException(
                            "Department not found with name: " + name.get())))
                    .map(dept -> Response.success("Department found by name",
                            new DepartmentDTO(
                                    dept.getName(),
                                    dept.getCreatedTimestamp(),
                                    dept.getLastUpdatedTimestamp()
                            )));
        }

        return getAll()
                .collectList()
                .map(list -> Response.success("All departments fetched", list));
    }

}
