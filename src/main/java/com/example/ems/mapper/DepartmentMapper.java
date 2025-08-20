package com.example.ems.mapper;

import com.example.ems.dto.DepartmentDTO;
import com.example.ems.entity.Department;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import java.util.List;

@Mapper(componentModel = "spring", nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface DepartmentMapper {
    DepartmentDTO toDTO(Department department);
    Department toEntity(DepartmentDTO dto);
    List<DepartmentDTO> toDTOList(List<Department> departments);
}
