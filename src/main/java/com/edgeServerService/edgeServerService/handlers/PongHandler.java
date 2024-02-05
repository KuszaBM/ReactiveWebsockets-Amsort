package com.edgeServerService.edgeServerService.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class PongHandler {
    Logger log = LoggerFactory.getLogger("PongHandler");

    public Flux<String> pongResponse(Flux<String> messages) {
        return messages.doOnNext((msg) -> {
            log.info("new ping + {}", msg);
        }).map((msg) -> "\"PONG\"");
    }
}
