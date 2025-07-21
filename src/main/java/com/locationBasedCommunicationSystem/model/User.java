package com.locationBasedCommunicationSystem.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
@Getter
@Setter
@Data
public class User {
    private String name;
    private Location location;
    private boolean online;
    private double communicationRadius;
    private int port;
    private Map<String, Contact> contacts = new ConcurrentHashMap<>();

    public User(String name, Location location, boolean online, double communicationRadius, int port) {
        this.name = name;
        this.location = location;
        this.online = online;
        this.communicationRadius = communicationRadius;
        this.port = port;
    }

    public String getName() { return name; }
    public Location getLocation() { return location; }
    public boolean isOnline() { return online; }
    public double getCommunicationRadius() { return communicationRadius; }
    public int getPort() { return port; }
    public Map<String, Contact> getContacts() { return contacts; }

    public void setLocation(Location location) { this.location = location; }
    public void setOnline(boolean online) { this.online = online; }
    public void setCommunicationRadius(double communicationRadius) { this.communicationRadius = communicationRadius; }
    public void addContact(Contact contact) { contacts.put(contact.getName(), contact); }
}