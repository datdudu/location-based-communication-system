package com.locationBasedCommunicationSystem.util;

import com.locationBasedCommunicationSystem.model.Location;

public class DistanceCalculator {
    private static final double EARTH_RADIUS = 6371000; // metros

    /**
     * Calcula distância entre duas localizações usando fórmula de Haversine
     */
    public static double calculateDistance(Location loc1, Location loc2) {
        double lat1Rad = Math.toRadians(loc1.getLatitude());
        double lat2Rad = Math.toRadians(loc2.getLatitude());
        double deltaLat = Math.toRadians(loc2.getLatitude() - loc1.getLatitude());
        double deltaLon = Math.toRadians(loc2.getLongitude() - loc1.getLongitude());

        double a = Math.sin(deltaLat/2) * Math.sin(deltaLat/2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.sin(deltaLon/2) * Math.sin(deltaLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return EARTH_RADIUS * c;
    }

    public static boolean isWithinRadius(Location loc1, Location loc2, double radius) {
        return calculateDistance(loc1, loc2) <= radius;
    }
}