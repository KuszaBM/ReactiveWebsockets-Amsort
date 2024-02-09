package com.edgeServerService.edgeServerService.industry;

import com.edgeServerService.edgeServerService.industry.entities.Container;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;

@Component
public class PhsAdapter {
    private final WebClient webClient;

    @Autowired
    public PhsAdapter(WebClient webClient) {
        this.webClient = webClient;
    }

    public Flux<Container> getAllContainers() {
        return webClient.get().uri("http://127.0.0.1:8082/container/getAll").retrieve().bodyToFlux(Container.class).log();
    }
}
