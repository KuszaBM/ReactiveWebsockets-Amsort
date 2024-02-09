package com.edgeServerService.edgeServerService.repositories;

import com.edgeServerService.edgeServerService.industry.BaseClientRepository;
import com.edgeServerService.edgeServerService.industry.responses.ClientConfigurationResponse;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class InMemoryBaseClientRepository implements BaseClientRepository {

    private final HashMap<String, ClientConfigurationResponse> configMap = new HashMap<>();
    public InMemoryBaseClientRepository() {
        configMap.put("172.27.27.77", new ClientConfigurationResponse("172.27.27.77", "station_1"));
    }
    @Override
    public ClientConfigurationResponse getConfig(String address) {
        ClientConfigurationResponse response = configMap.get(address);
        if(response == null)
            response = new ClientConfigurationResponse(address, "CLIENT_UNKNOWN");
        return response;
    }
}
