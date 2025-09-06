package com.example.ems.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {

    private String id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    @Pattern(
            regexp = "^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,6}$",
            message = "Email must be in lowercase and valid"
    )
    private String email;

    @NotBlank(message = "Password is required")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @NotBlank(message = "Role is required")
    private String role;

    @NotBlank(message = "Department name is required")
    private String departmentName;

    @CreatedDate
    private Instant createdTimestamp;

    @LastModifiedDate
    private Instant lastUpdatedTimestamp;
}