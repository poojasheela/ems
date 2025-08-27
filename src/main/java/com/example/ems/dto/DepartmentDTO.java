package com.example.ems.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class DepartmentDTO {


    @NotBlank(message = "Department name must not be blank")
    private String name;

    @CreatedDate
    private Instant createdTimestamp;

    @LastModifiedDate
    private Instant lastUpdatedTimestamp;
}