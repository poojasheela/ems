package com.example.ems.service.impl;

import com.example.ems.config.GithubProperties;
import com.example.ems.response.Response;
import com.example.ems.service.GithubActionService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

//@Service
//@Slf4j
//public class GithubActionServiceImpl implements GithubActionService {
//
//    private final WebClient webClient;
//    private final String owner;
//    private final String repo;
//    private final String workflowFileName;
//    private final String githubToken;
//
//    public GithubActionServiceImpl(
//            WebClient.Builder webClientBuilder,
//            @Value("${github.owner}") String owner,
//            @Value("${github.repo}") String repo,
//            @Value("${github.workflowFileName}") String workflowFileName,
//            @Value("${github.token}") String githubToken
//    ) {
//        this.owner = owner;
//        this.repo = repo;
//        this.workflowFileName = workflowFileName;
//        this.githubToken = githubToken;
//
//        this.webClient = webClientBuilder.baseUrl("https://api.github.com").build();
//    }
//
//    @Override
//    public Mono<Void> triggerWorkflow(String branch, String triggerReason) {
//        String url = String.format("/repos/%s/%s/actions/workflows/%s/dispatches",
//                owner, repo, workflowFileName);
//
//        String requestBody = String.format("{\"ref\":\"%s\", \"inputs\": {\"reason\":\"%s\"}}", branch, triggerReason);
//
//        return webClient.post()
//                .uri(url)
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + githubToken)
//                .header(HttpHeaders.ACCEPT, "application/vnd.github.v3+json")
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(BodyInserters.fromValue(requestBody))
//                .retrieve()
//                .bodyToMono(Void.class)
//                .doOnSuccess(unused -> log.info("Workflow triggered successfully"))
//                .doOnError(error -> log.error("Failed to trigger workflow", error));
//    }
//}
@Service
@Slf4j
public class GithubActionServiceImpl implements GithubActionService {

    private final WebClient webClient;
    private final GithubProperties githubProperties;

    public GithubActionServiceImpl(WebClient.Builder webClientBuilder, GithubProperties githubProperties) {
        this.githubProperties = githubProperties;
        this.webClient = webClientBuilder.baseUrl(githubProperties.getUri()).build();
    }

    @Override
    public Mono<Void> triggerWorkflow(String branch, String triggerReason) {
        String url = String.format("/repos/%s/%s/actions/workflows/%s/dispatches",
                githubProperties.getOwner(),
                githubProperties.getRepo(),
                githubProperties.getWorkflowFileName());

        String requestBody = String.format("{\"ref\":\"%s\", \"inputs\": {\"reason\":\"%s\"}}",
                branch, triggerReason);

        return webClient.post()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + githubProperties.getToken())
                .header(HttpHeaders.ACCEPT, "application/vnd.github.v3+json")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(requestBody))
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(unused -> log.info("Workflow triggered successfully"))
                .doOnError(error -> log.error("Failed to trigger workflow", error));
    }
}
