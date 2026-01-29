package com.example.myfood;

public class LocationData {
    private double latitude;
    private double longitude;
    private long timestamp;
    private String UserId;

    public LocationData(double latitude, double longitude, long timestamp, String userId) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
        UserId = userId;
    }

    public LocationData() {

    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }
}
