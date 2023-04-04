package com.sipl.rfidtagscanner.model;

public class supervisorModel {
   private String rng_no;
    private String till_date;
    private Integer total;
    private String date;
    private Integer trips;

    public supervisorModel(String rng_no, String till_date, Integer total, String date, Integer trips) {
        this.rng_no = rng_no;
        this.till_date = till_date;
        this.total = total;
        this.date = date;
        this.trips = trips;
    }

    public String getRng_no() {
        return rng_no;
    }

    public void setRng_no(String rng_no) {
        this.rng_no = rng_no;
    }

    public String getTill_date() {
        return till_date;
    }

    public void setTill_date(String till_date) {
        this.till_date = till_date;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getTrips() {
        return trips;
    }

    public void setTrips(Integer trips) {
        this.trips = trips;
    }
}
