package com.example.lr23.dto;

import com.example.lr23.model.Doctor;
import com.example.lr23.model.Nurse;

public record PersonResponse(Long id, String name, String profile) {

    public static PersonResponse from(Doctor d) {
        return new PersonResponse(d.getId(), d.getName(), d.getProfile());
    }

    public static PersonResponse from(Nurse n) {
        return new PersonResponse(n.getId(), n.getName(), n.getProfile());
    }
}
