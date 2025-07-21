package com.locationBasedCommunicationSystem.service;

import com.locationBasedCommunicationSystem.model.*;
import com.locationBasedCommunicationSystem.util.DistanceCalculator;

public class UserService {
    private User user;

    public UserService(User user) {
        this.user = user;
    }

    public void updateLocation(Location newLocation) {
        user.setLocation(newLocation);
        updateContactsInRadius();
    }

    public void updateStatus(boolean isOnline) {
        user.setOnline(isOnline);
    }

    public void updateRadius(double newRadius) {
        user.setCommunicationRadius(newRadius);
        updateContactsInRadius();
    }

    public void updateContactsInRadius() {
        for (Contact contact : user.getContacts().values()) {
            boolean inRadius = DistanceCalculator.isWithinRadius(
                    user.getLocation(), contact.getLocation(), user.getCommunicationRadius());
        }
    }
}