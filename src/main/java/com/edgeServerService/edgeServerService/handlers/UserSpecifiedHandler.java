package com.edgeServerService.edgeServerService.handlers;

import com.edgeServerService.edgeServerService.industry.PhsAdapter;
import com.edgeServerService.edgeServerService.industry.PhsWebsocketMessage;
import com.edgeServerService.edgeServerService.industry.PhsWebsocketMessageVerifier;
import com.edgeServerService.edgeServerService.industry.SinksHolder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class UserSpecifiedHandler implements WebSocketHandler {

    private Logger log = LoggerFactory.getLogger("UserSpecifiedHandler");
    private final SinksHolder sinksHolder;
    private final PhsAdapter phsAdapter;
    private final ObjectMapper mapper;
    private final Map<String, PhsWebsocketMessage<?>> messagesCache = new HashMap<>();
    private final InboundMessageHandler messageHandler;
    @Autowired
    public UserSpecifiedHandler(SinksHolder sinksHolder, PhsAdapter phsAdapter, ObjectMapper mapper, InboundMessageHandler messageHandler) {
        this.sinksHolder = sinksHolder;
        this.phsAdapter = phsAdapter;
        this.mapper = mapper;
        this.messageHandler = messageHandler;
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        Sinks.Many<String> sink = Sinks.many().unicast().onBackpressureBuffer();
        Flux<String> fluxOutput = sink.asFlux();
        Mono<Void> input = session
                .receive()
                .map(WebSocketMessage::getPayloadAsText)
                .doOnNext(msg -> {
                    log.info("new msg = {}", msg);
                })
                .filter(msg -> {
                    if(msg.equals("PING")) {
                        sink.tryEmitNext("PONG");
                        return false;
                    } else {return true;}
                })
                .filter(PhsWebsocketMessageVerifier::tryToParseMessage)
                .map(msg -> {
                    PhsWebsocketMessage<?> message = null;
                    try {
                        message = mapper.readValue(msg, PhsWebsocketMessage.class);
                    } catch (JsonProcessingException e) {
                        log.info("exception while parsing to PHS websocketMessage");
                    }
                    return message;
                })
                .filter(Objects::nonNull)
                .map(messageHandler::handle).doOnNext(sink::tryEmitNext).then();
        Mono<Void> output = session.send(fluxOutput.map(session::textMessage));
        return Mono.zip(input, output).then();
    }
}
