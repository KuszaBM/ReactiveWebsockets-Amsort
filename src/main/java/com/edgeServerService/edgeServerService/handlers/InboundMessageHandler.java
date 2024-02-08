package com.edgeServerService.edgeServerService.handlers;

import com.edgeServerService.edgeServerService.industry.PhsWebsocketMessage;
import com.edgeServerService.edgeServerService.industry.WebsocketHoldingEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class InboundMessageHandler {
//    private final RestTemplate restTemplate;
//
//    @Autowired
//    public InboundMessageHandler(RestTemplate restTemplate) {
//        this.restTemplate = restTemplate;
//    }

    public String handle(PhsWebsocketMessage<?> message) {
        switch (message.type) {
//            case "TO_PHS":
//                restTemplate.
            case "DASHBOARD-UPDATE":
                return "OK";
            default:
                return "DONE";
        }
    }
}
