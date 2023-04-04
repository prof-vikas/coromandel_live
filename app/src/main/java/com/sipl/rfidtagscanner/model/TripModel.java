package com.sipl.rfidtagscanner.model;

public class TripModel {
    private String date;
    private String trips;

    public TripModel(String date, String trips) {
        this.date = date;
        this.trips = trips;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTrips() {
        return trips;
    }

    public void setTrips(String trips) {
        this.trips = trips;
    }
}
