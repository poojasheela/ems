package com.example.ems.security;

import com.example.ems.entity.Employee;
import com.example.ems.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            employeeRepository.findByContactEmail("admin@gmail.com")
                    .switchIfEmpty(
                            Mono.defer(() -> {
                                Employee admin = new Employee();
                                admin.setFullName("Admin");
                                admin.setContactEmail("admin@gmail.com");
                                admin.setPassword(passwordEncoder.encode("admin123"));
                                admin.setRole("ADMIN");
                                return employeeRepository.save(admin)
                                        .doOnSuccess(saved -> log.info("Admin user created: admin@gmail.com / admin123"));
                            })
                    )
                    .doOnNext(existing -> log.info("Admin user already exists"))
                    .subscribe();
        };
    }
}