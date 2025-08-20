package com.example.ems.repository;

import com.example.ems.entity.Employee;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
@Repository
public interface EmployeeRepository extends ReactiveMongoRepository<Employee, String> {

    Flux<Employee> findByContactEmail(String contactEmail);

    Mono<Employee> findByFullName(String fullName);

    Flux<Employee> findAll();
}
