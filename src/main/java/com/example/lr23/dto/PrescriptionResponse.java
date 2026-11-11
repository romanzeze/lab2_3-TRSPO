package com.example.lr23.dto;

import com.example.lr23.model.Prescription;
import com.example.lr23.model.PrescriptionType;
import com.example.lr23.model.Role;

public record PrescriptionResponse(
        Long id, Long patientId, PrescriptionType type, String description, boolean done, Role doneBy) {

    public static PrescriptionResponse from(Prescription p) {
        return new PrescriptionResponse(p.getId(), p.getPatient().getId(), p.getType(), p.getDescription(),
                p.isDone(), p.getDoneBy());
    }
}
