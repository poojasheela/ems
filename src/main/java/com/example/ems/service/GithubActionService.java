package com.example.ems.service;

import com.example.ems.response.Response;
import reactor.core.publisher.Mono;

public interface GithubActionService {
    Mono<Response> triggerWorkflow();
}
