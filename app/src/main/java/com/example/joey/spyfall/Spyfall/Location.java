package com.example.joey.spyfall.Spyfall;

import java.util.ArrayList;

/**
 * Created by Joey on 08-Nov-16.
 */

public class Location {

    private String location;
    private ArrayList<String> singleRoles;
    private ArrayList<String> multiRoles;

    public Location() {
        location = "default";
        singleRoles = new ArrayList<>();
        multiRoles = new ArrayList<>();
    }

    public Location(String location, ArrayList<String> singleRoles, ArrayList<String> multiRoles) {
        this.location = location;
        this.singleRoles = singleRoles;
        this.multiRoles = multiRoles;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ArrayList<String> getSingleRoles() {
        return singleRoles;
    }

    public void setSingleRoles(ArrayList<String> singleRoles) {
        this.singleRoles = singleRoles;
    }

    public ArrayList<String> getMultiRoles() {
        return multiRoles;
    }

    public void setMultiRoles(ArrayList<String> multiRoles) {
        this.multiRoles = multiRoles;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("Location: %s\n", location));

        sb.append("Single Roles:\n");
        for (String role : singleRoles) {
            sb.append(String.format("%s\n", role));
        }

        sb.append("Multi Roles:\n");
        for (String role : multiRoles) {
            sb.append(String.format("%s\n", role));
        }

        return sb.toString();
    }
}
