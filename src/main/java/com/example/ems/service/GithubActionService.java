package com.example.ems.service;

import com.example.ems.response.Response;
import reactor.core.publisher.Mono;

public interface GithubActionService {
    public Mono<Void> triggerWorkflow(String branch, String triggerReason);

}
