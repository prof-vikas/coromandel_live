package com.sipl.rfidtagscanner.model;

public class RmgModel {
    private String rmg_no;
    private String till_date;
    private String total;

    public RmgModel(String rng_no, String till_date, String total) {
        this.rmg_no = rng_no;
        this.till_date = till_date;
        this.total = total;
    }

    public String getRmg_no() {
        return rmg_no;
    }

    public void setRmg_no(String rng_no) {
        this.rmg_no = rng_no;
    }

    public String getTill_date() {
        return till_date;
    }

    public void setTill_date(String till_date) {
        this.till_date = till_date;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
