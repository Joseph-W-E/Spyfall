package com.example.joey.spyfall.Spyfall;

/**
 * Created by Joey on 08-Nov-16.
 */

public class PlayerInformation {

    private String location;
    private String role;
    private boolean spy;

    public PlayerInformation(String location, String role) {
        this.location = location;
        this.role = role;
    }

    public PlayerInformation(boolean spy) {
        this.spy = spy;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isSpy() {
        return spy;
    }

    public void setSpy(boolean spy) {
        this.spy = spy;
    }
}
