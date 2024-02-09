package com.edgeServerService.edgeServerService.controllers;

import com.edgeServerService.edgeServerService.handlers.GeneralHandler;
import com.edgeServerService.edgeServerService.industry.PhsWebsocketMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class TestController {
    private final GeneralHandler exampleHandler;

    @Autowired
    public TestController(GeneralHandler exampleHandler) {
        this.exampleHandler = exampleHandler;
    }

    @PostMapping("/dupsko/{id}")
    public void sentToClient(@PathVariable int id) {
    }
    @PostMapping("/dupsko/{messageType}/all")
    public void sendToClients(@PathVariable String messageType, @RequestBody Flux<Object> object) {
        exampleHandler.sendToClients(new PhsWebsocketMessage<>(messageType, object.blockFirst()));
    }
}
