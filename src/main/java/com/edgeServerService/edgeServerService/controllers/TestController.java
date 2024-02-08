package com.edgeServerService.edgeServerService.controllers;

import com.edgeServerService.edgeServerService.handlers.ExampleHandler;
import com.edgeServerService.edgeServerService.industry.DemoObject;
import com.edgeServerService.edgeServerService.industry.PhsWebsocketMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class TestController {
    private final ExampleHandler exampleHandler;

    @Autowired
    public TestController(ExampleHandler exampleHandler) {
        this.exampleHandler = exampleHandler;
    }

    @PostMapping("/dupsko/{id}")
    public void sentToClient(@PathVariable int id, @RequestBody DemoObject object) {
        exampleHandler.sendToClient(id, "TEST_MSG", object);
    }
    @PostMapping("/dupsko/{messageType}/all")
    public void sendToClients(@PathVariable String messageType, @RequestBody Flux<Object> object) {
        exampleHandler.sendToClients(new PhsWebsocketMessage<>(messageType, object.blockFirst()));
    }
}
