package com.edgeServerService.edgeServerService.configs;

import com.edgeServerService.edgeServerService.handlers.GeneralHandler;
import com.edgeServerService.edgeServerService.handlers.UserSpecifiedHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class WebSocketConfig {
    @Bean
    public HandlerMapping webSocketHandlerMapping(GeneralHandler generalHandler, UserSpecifiedHandler userSpecifiedHandler) {
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put("/general", generalHandler);
        map.put("/user/{user}/station", userSpecifiedHandler);
        int order = -1; // before annotated controllers

        return new SimpleUrlHandlerMapping(map, order);
    }

    @Bean
    public WebSocketHandlerAdapter webSocketHandlerAdapter() {
        return new WebSocketHandlerAdapter();
    }
}
