package com.example.ems.service.impl;

import com.example.ems.response.Response;
import com.example.ems.service.GithubActionService;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.time.Instant;
import java.util.Map;
//@Service
//@Slf4j
//public class GithubActionServiceImpl implements GithubActionService {
//
//    @Value("${github.token}")
//    private String token;
//
//    @Value("${github.owner}")
//    private String owner;
//
//    @Value("${github.repo}")
//    private String repo;
//
//    @Value("${github.workflow}")
//    private String workflow;
//
//    @Value("${github.ref}")
//    private String ref;
//
//    private WebClient webClient;
//    @PostConstruct
//    public void init() {
//        this.webClient = WebClient.builder()
//                .baseUrl("https://api.github.com")
//                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token.trim())
//                .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.github+json")
//                .build();
//    }
//
//    @Override
//    public Mono<Response> triggerWorkflow() {
//        String url = String.format("/repos/%s/%s/actions/workflows/%s/dispatches",
//                owner, repo, workflow);
//
//        Map<String, Object> requestBody = Map.of("ref", ref);
//
//        log.info("Triggering GitHub Action at URL: {}", url);
//
//        return webClient.post()
//                .uri(url)
//                .bodyValue(requestBody)
//                .retrieve()
//                .bodyToMono(Void.class)
//                .map(voidResp -> new Response(
//                        Instant.now(),
//                        200,
//                        "GitHub workflow triggered successfully",
//                        null))
//                .onErrorResume(ex -> {
//                    log.error("Error triggering GitHub workflow", ex);
//                    return Mono.just(new Response(
//                            Instant.now(),
//                            500,
//                            "Failed to trigger GitHub workflow: " + ex.getMessage(),
//                            null));
//                });
//    }
//}
@Service
@Slf4j
@AllArgsConstructor
public class GithubActionServiceImpl implements GithubActionService {

        private final WebClient webClient;
        private final String owner ="poojasheela";
        private final String repo = "ems";
        private final String workflowFileName = "Build.yml";
        private final String githubToken ="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";

        public GithubActionServiceImpl(WebClient.Builder webClientBuilder) {
            this.webClient = webClientBuilder.baseUrl("https://api.github.com").build();
        }
        public GithubActionServiceImpl() {

            this(WebClient.builder());
        }


        @Override
        public Mono<Void> triggerWorkflow(String branch, String triggerReason) {
            String url = String.format("/repos/%s/%s/actions/workflows/%s/dispatches",
                    owner, repo, workflowFileName);


            String requestBody = String.format("{\"ref\":\"%s\", \"inputs\": {\"reason\":\"%s\"}}", branch, triggerReason);

            return webClient.post()
                    .uri(url)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + githubToken)
                    .header(HttpHeaders.ACCEPT, "application/vnd.github.v3+json")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(requestBody))
                    .retrieve()
                    .bodyToMono(Void.class);
        }
    }

