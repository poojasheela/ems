package com.example.ems.entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "departments")
public class Department {
    @Id
    private String id;

    private String name;

    @CreatedDate
    private Instant createdTimestamp;

    @LastModifiedDate
    private Instant lastUpdatedTimestamp;
}