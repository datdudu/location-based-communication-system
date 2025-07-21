package com.locationBasedCommunicationSystem.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class Contact {
    private String name;
    private Location location;
    private boolean online;
    private int port;
    private String ipAddress;

    public Contact(String name, Location location, boolean online, int port, String ipAddress) {
        this.name = name;
        this.location = location;
        this.online = online;
        this.port = port;
        this.ipAddress = ipAddress;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getName() { return name; }
    public Location getLocation() { return location; }
    public boolean isOnline() { return online; }
    public int getPort() { return port; }
    public String getIpAddress() { return ipAddress; }
    public void setLocation(Location location) { this.location = location; }
    public void setOnline(boolean online) { this.online = online; }
}