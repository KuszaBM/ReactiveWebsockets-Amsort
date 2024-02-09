package com.edgeServerService.edgeServerService.industry.entities;

import java.sql.Timestamp;

public class Container {
    private String id;
    private String barcode;
    private Timestamp timestamp;

    public Container(String id, String barcode, Timestamp timestamp) {
        this.id = id;
        this.barcode = barcode;
        this.timestamp = timestamp;
    }

    public Container(String id, String barcode) {
        this.id = id;
        this.barcode = barcode;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }
    public Container() {
        this.id = null;
        this.barcode = null;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
