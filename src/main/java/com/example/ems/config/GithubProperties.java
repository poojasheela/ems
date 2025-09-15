package com.example.ems.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "github")
@Data
public class GithubProperties {
    private String token;
    private String owner;
    private String repo;
    private String workflowFileName;
    private String uri;
}
