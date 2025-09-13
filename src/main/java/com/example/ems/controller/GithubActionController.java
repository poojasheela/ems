package com.example.ems.controller;

import com.example.ems.response.Response;
import com.example.ems.service.GithubActionService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
//
//@RestController
//@RequestMapping("/ems/github")
//@RequiredArgsConstructor
//@Slf4j
//public class GithubActionController {
//
//    private final GithubActionService githubActionService;
//
//    @PostMapping("/trigger-workflow")
//    public Mono<ResponseEntity<Response>> triggerGithubWorkflow() {
//        log.info("Received request to trigger GitHub workflow");
//        return githubActionService.triggerWorkflow()
//                .map(response -> ResponseEntity.status(response.getStatus()).body(response));
//    }
//}
@RestController
@AllArgsConstructor
public class GithubActionController {

    private final GithubActionService githubActionService;



    @PostMapping("/trigger-build")
    public Mono<ResponseEntity<String>> triggerGithubAction(
            @RequestParam(defaultValue = "master") String branch,
            @RequestParam(defaultValue = "Manual trigger from REST API") String reason) {

        return githubActionService.triggerWorkflow(branch, reason)
                .thenReturn(ResponseEntity.ok("GitHub Action triggered successfully."))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to trigger GitHub Action: " + e.getMessage())));
    }
}