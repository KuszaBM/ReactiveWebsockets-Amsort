package com.edgeServerService.edgeServerService.controllers;

import com.edgeServerService.edgeServerService.handlers.PongHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import reactor.core.publisher.Flux;

@Controller
public class SlotController {
    private final PongHandler pongHandler;

    @Autowired
    public SlotController(PongHandler pongHandler) {
        this.pongHandler = pongHandler;
    }

//    @CrossOrigin
//    @MessageMapping("/ping-endpoint")
//    @SendTo("/topic/ping")
//    public Flux<String> handleWebSocket(Flux<String> messages) {
//        return handleWebSocket(messages);
//    }
}
