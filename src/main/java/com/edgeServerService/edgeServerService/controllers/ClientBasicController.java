package com.edgeServerService.edgeServerService.controllers;

import com.edgeServerService.edgeServerService.industry.ClientConfigurationService;
import com.edgeServerService.edgeServerService.industry.responses.ClientConfigurationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class ClientBasicController {
    private Logger log = LoggerFactory.getLogger(ClientBasicController.class);
    private final ClientConfigurationService clientConfigurationService;

    @Autowired
    public ClientBasicController(ClientConfigurationService clientConfigurationService) {
        this.clientConfigurationService = clientConfigurationService;
    }

    @CrossOrigin
    @GetMapping("/base/getConfig")
    public @ResponseBody ClientConfigurationResponse getConfig(ServerHttpRequest request) {
        log.info("getting config for client: {} | {}", request.getRemoteAddress().getHostName(), request.getRemoteAddress().getHostString());
        return clientConfigurationService.getConfig(request.getRemoteAddress().getHostName());
    }
}
