package com.example.ems.security;
import com.example.ems.service.impl.CustomUserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import static com.example.ems.constants.Constants.*;

@EnableMethodSecurity
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final CustomAuthenticationEntryPoint customAuthEntryPoint;
    private final CustomUserDetailsServiceImpl userDetailsService;

    private static final String[] SWAGGER_WHITELIST = {
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/v3/api-docs.yaml"
    };

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(auth -> auth
                        .pathMatchers(SWAGGER_WHITELIST).permitAll()
                        .pathMatchers(HttpMethod.POST, EMPLOYEE_ADD,DEPARTMENT_ADD ).hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PUT, EMPLOYEE_BASE, DEPARTMENT_BASE).hasRole("ADMIN")
                        .pathMatchers(HttpMethod.DELETE, EMPLOYEE_BASE, DEPARTMENT_BASE).hasRole("ADMIN")
                        .pathMatchers(HttpMethod.GET, EMPLOYEE_BASE, DEPARTMENT_BASE).hasAnyRole("ADMIN", "USER")
                        .anyExchange().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .exceptionHandling(ex -> ex.authenticationEntryPoint(customAuthEntryPoint))
                .build();
    }

    @Bean
    public ReactiveUserDetailsService reactiveUserDetailsService() {
        return userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}