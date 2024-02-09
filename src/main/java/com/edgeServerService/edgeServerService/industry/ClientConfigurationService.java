package com.edgeServerService.edgeServerService.industry;

import com.edgeServerService.edgeServerService.industry.responses.ClientConfigurationResponse;
import com.edgeServerService.edgeServerService.repositories.InMemoryBaseClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientConfigurationService {
    private final BaseClientRepository baseClientRepository;

    @Autowired
    public ClientConfigurationService(InMemoryBaseClientRepository baseClientRepository) {
        this.baseClientRepository = baseClientRepository;
    }

    public ClientConfigurationResponse getConfig(String address) {
        return baseClientRepository.getConfig(address);
    }
}
