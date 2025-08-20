package com.example.ems.service.impl;
import com.example.ems.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsServiceImpl implements ReactiveUserDetailsService {

    private final EmployeeRepository employeeRepository;

    @Override
    public Mono<UserDetails> findByUsername(String email) {
        return employeeRepository.findByContactEmail(email)
                .next()
                .switchIfEmpty(Mono.error(new UsernameNotFoundException(
                        "User not found with email: " + email)))
                .map(employee -> User.builder()
                        .username(employee.getContactEmail())
                        .password(employee.getPassword())
                        .roles(employee.getRole())
                        .build()
                );
    }

}
