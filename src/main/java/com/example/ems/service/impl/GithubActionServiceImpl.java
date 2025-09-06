package com.example.ems.service.impl;

import com.example.ems.response.Response;
import com.example.ems.service.GithubActionService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.time.Instant;
import java.util.Map;
@Service
@Slf4j
public class GithubActionServiceImpl implements GithubActionService {

    @Value("${github.token}")
    private String token;

    @Value("${github.owner}")
    private String owner;

    @Value("${github.repo}")
    private String repo;

    @Value("${github.workflow}")
    private String workflow;

    @Value("${github.ref}")
    private String ref;

    private WebClient webClient;
    @PostConstruct
    public void init() {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.github.com")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token.trim())
                .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.github+json")
                .build();
    }

    @Override
    public Mono<Response> triggerWorkflow() {
        String url = String.format("/repos/%s/%s/actions/workflows/%s/dispatches",
                owner, repo, workflow);

        Map<String, Object> requestBody = Map.of("ref", ref);

        log.info("Triggering GitHub Action at URL: {}", url);

        return webClient.post()
                .uri(url)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Void.class)
                .map(voidResp -> new Response(
                        Instant.now(),
                        200,
                        "GitHub workflow triggered successfully",
                        null))
                .onErrorResume(ex -> {
                    log.error("Error triggering GitHub workflow", ex);
                    return Mono.just(new Response(
                            Instant.now(),
                            500,
                            "Failed to trigger GitHub workflow: " + ex.getMessage(),
                            null));
                });
    }
}
