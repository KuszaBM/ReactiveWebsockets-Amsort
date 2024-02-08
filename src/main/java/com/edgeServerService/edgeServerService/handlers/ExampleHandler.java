package com.edgeServerService.edgeServerService.handlers;


import com.edgeServerService.edgeServerService.industry.DemoObject;
import com.edgeServerService.edgeServerService.industry.PhsWebsocketMessage;
import com.edgeServerService.edgeServerService.industry.SinksHolder;
import com.edgeServerService.edgeServerService.industry.WebsocketHoldingEntity;
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

import java.awt.image.DataBuffer;
import java.time.Duration;
import java.util.*;

@Component
public class ExampleHandler implements WebSocketHandler {

//    private Sinks.Many<String> sink;
//    private Flux<String> fluxOutput;
//    public ExampleHandler() {
//        sink = Sinks.many().multicast().directBestEffort();
//        fluxOutput = sink.asFlux();
//    }


    private Logger log = LoggerFactory.getLogger("ExampleHandler");
    private final SinksHolder sinksHolder;
    private final ObjectMapper mapper;
    private final Map<String, PhsWebsocketMessage<?>> messagesCache = new HashMap<>();
    private final InboundMessageHandler messageHandler;

    @Autowired
    public ExampleHandler(SinksHolder sinksHolder, InboundMessageHandler messageHandler) {
        super();
        this.sinksHolder = sinksHolder;
        this.messageHandler = messageHandler;
        this.mapper = new ObjectMapper();
    }

    private boolean tryToParseMessage(String message) {
        try {
            mapper.readValue(message, PhsWebsocketMessage.class);
            return true;
        } catch (JsonProcessingException e) {
            log.info("exception while parsing to PHS websocketMessage ");
        }
        return false;
    }

    public void sendToClient(int id, String messageType, Object messageData) {
        ObjectMapper mapper = new ObjectMapper();
        PhsWebsocketMessage message = new PhsWebsocketMessage(messageType, messageData);
        try {
            String json = mapper.writeValueAsString(message);
            WebsocketHoldingEntity holdingEntity = sinksHolder.getById(id);
            if(holdingEntity != null)
                holdingEntity.getSink().tryEmitNext(json);
            else
                throw new NoSuchElementException("No element with id: " + id);
        } catch (JsonProcessingException e) {
            log.info("exc - ", e);
        }
    }
    public void sendToClients(PhsWebsocketMessage<?> message) {
        String json = null;
            json = serializeToJson(message);
            log.info("new msg - {} | {}", json, message.data.getClass().getName());
        sinksHolder.cacheMessage(message.type, message);
        for(WebsocketHoldingEntity holdingEntity : sinksHolder.getAll()) {
            try {
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
        }).filter(this::tryToParseMessage).map(msg -> {
            PhsWebsocketMessage<?> message = null;
            try {
                message = mapper.readValue(msg, PhsWebsocketMessage.class);
            } catch (JsonProcessingException e) {
                log.info("exception while parsing to PHS websocketMessage ");
            }
            return message;
        }).filter(Objects::nonNull).map(messageHandler::handle).doOnNext(sink::tryEmitNext).then();

        sinksHolder.addConnection(new WebsocketHoldingEntity(sink, session));

        log.info("info 1 - {}", session.getAttributes().values());
        log.info("info 2 - {}", session.getHandshakeInfo().getUri());
        log.info("info 3 - {}", session.getHandshakeInfo().getLogPrefix());
        sink.tryEmitNext(session.getId());
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
