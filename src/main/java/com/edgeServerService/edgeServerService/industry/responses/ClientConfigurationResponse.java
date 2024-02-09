package com.edgeServerService.edgeServerService.industry.responses;

public class ClientConfigurationResponse {
    private String ipAddress;
    private String id;

    public ClientConfigurationResponse(String ipAddress, String id) {
        this.ipAddress = ipAddress;
        this.id = id;
    }

    public ClientConfigurationResponse() {
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
