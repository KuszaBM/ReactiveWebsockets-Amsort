package com.edgeServerService.edgeServerService.industry;

public class PhsWebsocketMessage<T> {
    public String type;
    public T data;

    public PhsWebsocketMessage(String messageType, T messageData) {
        this.type = messageType;
        this.data = messageData;
    }

    public PhsWebsocketMessage() {
        this.type = null;
        this.data = null;
    }
}
