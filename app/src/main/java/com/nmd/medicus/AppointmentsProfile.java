package com.nmd.medicus;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by adityadesai on 30/03/18.
 */

public class AppointmentsProfile {

    private HashMap<String, HashMap<Date, String>> dates;

    public AppointmentsProfile() {
    }

    public AppointmentsProfile(HashMap<String, HashMap<Date, String>> dates) {
        this.dates = dates;
    }

    public HashMap<String, HashMap<Date, String>> getDates() {
        return dates;
    }
}
