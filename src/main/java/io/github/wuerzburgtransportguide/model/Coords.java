package io.github.wuerzburgtransportguide.model;

/** Pair of coordinates consisting of latitude and longitude. */
public class Coords {
    private double latitude;
    private double longitude;

    public Coords() {}

    public Coords(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }
}
