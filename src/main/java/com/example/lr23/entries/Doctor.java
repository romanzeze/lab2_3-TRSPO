package com.example.lr23.entries;

import jakarta.persistence.*;

@Entity
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String profile;

    public Doctor(int id, String name, String profile) {
        this.id = id;
        this.name = name;
        this.profile = profile;
    }

    public Doctor() {
        this.id = 0;
        this.name = "NULL";
        this.profile = "NULL";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }
}
