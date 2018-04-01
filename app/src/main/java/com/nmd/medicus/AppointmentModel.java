package com.nmd.medicus;

/**
 * Created by adityadesai on 31/03/18.
 */

public class AppointmentModel {
    private String name, email, image, uid, day;

    public AppointmentModel(String name, String email, String image, String uid, String day) {
        this.name = name;
        this.email = email;
        this.image = image;
        this.uid = uid;
        this.day = day;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getImage() {
        return image;
    }

    public String getUid() {
        return uid;
    }

    public String getDay() {
        return day;
    }
}
