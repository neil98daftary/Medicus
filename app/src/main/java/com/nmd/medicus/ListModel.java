package com.nmd.medicus;

/**
 * Created by neil on 21/2/18.
 */

public class ListModel {
    private  String name;
    private  String image;
    private String id;

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getId() {
        return id;
    }

    public ListModel(String name, String image, String id) {
        this.name = name;
        this.image = image;
        this.id = id;
    }
}
