package com.edgeServerService.edgeServerService.industry;

import com.edgeServerService.edgeServerService.industry.responses.ClientConfigurationResponse;

public interface BaseClientRepository {
    public ClientConfigurationResponse getConfig(String address);
}
