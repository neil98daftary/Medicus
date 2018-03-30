package com.nmd.medicus;

import java.util.Date;

/**
 * Created by USER on 31-03-2018.
 */

public class AppointmentModel {

    String uid;
    Date time;

    public AppointmentModel(String uid, Date time) {
        this.uid = uid;
        this.time = time;
    }

    public String getUid() {
        return uid;
    }

    public Date getTime() {
        return time;
    }
}
