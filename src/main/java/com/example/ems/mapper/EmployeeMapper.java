package com.example.ems.mapper;


import com.example.ems.dto.EmployeeDTO;
import com.example.ems.entity.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface EmployeeMapper {
    @Mapping(target = "fullName", source = "name")
    @Mapping(target = "contactEmail", source = "email")
    @Mapping(target = "department", ignore = true)
    Employee toEntity(EmployeeDTO dto);

    @Mapping(target = "name", source = "fullName")
    @Mapping(target = "email", source = "contactEmail")
    @Mapping(target = "departmentName", source = "department.name")
    EmployeeDTO toDTO(Employee employee);

    List<EmployeeDTO> toDTOList(List<Employee> employees);
}