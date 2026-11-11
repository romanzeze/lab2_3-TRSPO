package com.example.lr23.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    /** Поточний діагноз, який визначає лікар. */
    private String diagnosis;

    /** Кінцевий діагноз, що фіксується під час виписки. */
    private String finalDiagnosis;

    private boolean discharged;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Prescription> prescriptions = new ArrayList<>();

    protected Patient() {
    }

    public Patient(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getFinalDiagnosis() {
        return finalDiagnosis;
    }

    public boolean isDischarged() {
        return discharged;
    }

    /** Виписує пацієнта, фіксуючи кінцевий діагноз. */
    public void discharge(String finalDiagnosis) {
        this.finalDiagnosis = finalDiagnosis;
        this.discharged = true;
    }

    public List<Prescription> getPrescriptions() {
        return prescriptions;
    }
}
