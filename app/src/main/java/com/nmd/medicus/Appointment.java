package com.nmd.medicus;

/**
 * Created by adityadesai on 29/03/18.
 */

public class Appointment {
    private String date, start;

    public Appointment(String date, String start) {
        this.date = date;
        this.start = start;
    }

    public String getDate() {
        return date;
    }

    public String getStart() {
        return start;
    }
}
