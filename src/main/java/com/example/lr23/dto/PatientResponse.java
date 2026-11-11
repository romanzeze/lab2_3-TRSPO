package com.example.lr23.dto;

import com.example.lr23.model.Patient;

public record PatientResponse(Long id, String name, String diagnosis, String finalDiagnosis, boolean discharged) {

    public static PatientResponse from(Patient p) {
        return new PatientResponse(p.getId(), p.getName(), p.getDiagnosis(), p.getFinalDiagnosis(), p.isDischarged());
    }
}
