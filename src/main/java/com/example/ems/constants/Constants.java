package com.example.ems.constants;
public class Constants {
    public static final String ADMIN ="ADMIN";
    public static final String USER="USER";
    public static final String EMPLOYEE_BASE = "/ems/employee/**";
    public static final String EMPLOYEE_ADD = "/ems/employee/add";
    public static final String DEPARTMENT_BASE = "/ems/department/**";
    public static final String DEPARTMENT_ADD = "/ems/department/add";
    public static final String[] SWAGGER_WHITELIST = {
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/v3/api-docs.yaml",
            "/swagger-resources/**",
            "/webjars/**"
    };
}