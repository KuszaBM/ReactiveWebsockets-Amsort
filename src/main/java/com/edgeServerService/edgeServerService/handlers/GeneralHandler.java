package com.edgeServerService.edgeServerService.handlers;


import com.edgeServerService.edgeServerService.industry.*;
import com.edgeServerService.edgeServerService.industry.entities.Container;
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

import java.util.*;

@Component
public class GeneralHandler implements WebSocketHandler {
    private Logger log = LoggerFactory.getLogger("GeneralHandler");
    private final SinksHolder sinksHolder;
    private final ObjectMapper mapper;
    private final PhsAdapter phsAdapter;
    private final Map<String, PhsWebsocketMessage<?>> messagesCache = new HashMap<>();
    private final InboundMessageHandler messageHandler;

    @Autowired
    public GeneralHandler(SinksHolder sinksHolder, PhsAdapter phsAdapter, InboundMessageHandler messageHandler) {
        super();
        this.sinksHolder = sinksHolder;
        this.phsAdapter = phsAdapter;
        this.messageHandler = messageHandler;
        this.mapper = new ObjectMapper();
    }
    public void sendToClient(int id, PhsWebsocketMessage<?> message) {
        String json = serializeToJson(message);
        WebsocketHoldingEntity holdingEntity = sinksHolder.getById(id);
        if(holdingEntity != null)
            holdingEntity.getSink().tryEmitNext(json);
        else
            throw new NoSuchElementException("No element with id: " + id);
    }
    public void sendToClients(PhsWebsocketMessage<?> message) {
        String json = serializeToJson(message);
        //Adding message to cache, so it will be sent to new connected clients too
        sinksHolder.cacheMessage(message.type, message);
        for(WebsocketHoldingEntity holdingEntity : sinksHolder.getAll()) {
            try {
                //Taking sink specified for each client and sanding message to client
                holdingEntity.getSink().tryEmitNext(json);
            } catch (Exception e) {
                log.info("Exception while sending message to - {}", holdingEntity.getSession().getHandshakeInfo().getRemoteAddress());
            }

        }
    }
    @Override
    public Mono<Void> handle(WebSocketSession session) {
        Sinks.Many<String> sink = Sinks.many().unicast().onBackpressureBuffer();
        Flux<String> fluxOutput = sink.asFlux();
        Mono<Void> input = session.receive().map(WebSocketMessage::getPayloadAsText).doOnNext(msg -> {
            log.info("new msg = {}", msg);
        }).filter(msg -> {
            if(msg.equals("PING")) {
                sink.tryEmitNext("PONG");
                return false;
            } else {
                return true;
            }
        }).filter(PhsWebsocketMessageVerifier::tryToParseMessage).map(msg -> {
            PhsWebsocketMessage<?> message = null;
            try {
                message = mapper.readValue(msg, PhsWebsocketMessage.class);
            } catch (JsonProcessingException e) {
                log.info("exception while parsing to PHS websocketMessage");
            }
            return message;
        }).filter(Objects::nonNull).map(messageHandler::handle).doOnNext(sink::tryEmitNext).then();

        sinksHolder.addConnection(new WebsocketHoldingEntity(sink, session));

        log.info("info 1 - {}", session.getAttributes().values());
        log.info("info 2 - {}", session.getHandshakeInfo().getUri());
        log.info("info 3 - {}", session.getHandshakeInfo().getLogPrefix());
        sink.tryEmitNext(session.getId());
        List<Container> containers = new ArrayList<>();
        phsAdapter.getAllContainers().doOnNext(containers::add).doOnComplete(() -> {
            sink.tryEmitNext(serializeToJson(new PhsWebsocketMessage<>("CONTAINER_ARRAY", containers)));
        }).subscribe();

        try {
            for (PhsWebsocketMessage<?> msg : sinksHolder.getAllCachedMessage()) {
                sink.tryEmitNext(serializeToJson(msg));
                log.info("send serialized message: {} to {}", serializeToJson(msg), session.getHandshakeInfo().getRemoteAddress());
            }
        } catch (Exception e) {
            log.info("exception sending cached massages = ", e);
        }
        Mono<Void> output = session.send(fluxOutput.map(session::textMessage));
        return Mono.zip(input, output).then();
    }
    private String serializeToJson(PhsWebsocketMessage<?> message) {
        try {
            return mapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
