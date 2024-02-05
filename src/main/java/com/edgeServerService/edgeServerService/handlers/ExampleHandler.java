package com.edgeServerService.edgeServerService.handlers;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

@Component
public class ExampleHandler implements WebSocketHandler {

//    private Sinks.Many<String> sink;
//    private Flux<String> fluxOutput;
//    public ExampleHandler() {
//        sink = Sinks.many().multicast().directBestEffort();
//        fluxOutput = sink.asFlux();
//    }
    private
    Logger log = LoggerFactory.getLogger("ExampleHandler");
    @Override
    public Mono<Void> handle(WebSocketSession session) {
        Sinks.Many<String> sink = Sinks.many().unicast().onBackpressureBuffer();
        Flux<String> fluxOutput = sink.asFlux();
        Mono<Void> input = session.receive().doOnNext(msg -> {
            log.info("new PING- from {} | instance: {}", session.getId(), sink.hashCode());
            sink.tryEmitNext("PONG");
        }).then();
        log.info("log 1");
        Flux.interval(Duration.ofMillis(4000)).doOnNext((s) -> sink.tryEmitNext("DUPSKO12")).subscribe();
        sink.tryEmitNext(session.getId());
        log.info("log 2");
        Mono<Void> output = session.send(fluxOutput.map(session::textMessage));
        log.info("log 3");
        return Mono.zip(input, output).then();
    }
}
