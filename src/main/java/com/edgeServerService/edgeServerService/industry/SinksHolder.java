package com.edgeServerService.edgeServerService.industry;

import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Sinks;

import java.net.http.WebSocket;
import java.util.HashMap;
import java.util.Map;

public class SinksHolder {
    private final Map<WebSocketSession, Sinks.Many<String>> map;

    public SinksHolder() {
        this.map = new HashMap<>();
    }
}
