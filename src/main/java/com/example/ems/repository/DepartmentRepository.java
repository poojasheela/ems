package com.example.ems.repository;
import com.example.ems.entity.Department;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
@Repository
public interface DepartmentRepository extends ReactiveMongoRepository<Department, String> {

    Mono<Boolean> existsByName(String name);

    Mono<Department> findByNameIgnoreCase(String name);
}