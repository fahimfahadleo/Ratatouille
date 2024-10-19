package com.horoftech.ratatouille.models;

public class ProfileModel {
    String name;
    String id;
    String profile;
    String email;
    String uniqueID;
    String wins;
    String loses;

    public String getWins() {
        return wins;
    }

    public void setWins(String wins) {
        this.wins = wins;
    }

    public String getLoses() {
        return loses;
    }

    public void setLoses(String loses) {
        this.loses = loses;
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "ProfileModel{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", profile='" + profile + '\'' +
                ", email='" + email + '\'' +
                ", uniqueID='" + uniqueID + '\'' +
                ", wins='" + wins + '\'' +
                ", loses='" + loses + '\'' +
                '}';
    }
}
