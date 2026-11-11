package com.example.lr23.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

/** Призначення пацієнту: процедура, ліки або операція. Виконується один раз. */
@Entity
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @Enumerated(EnumType.STRING)
    private PrescriptionType type;

    private String description;

    private boolean done;

    /** Хто виконав призначення; порожнє, доки воно не виконане. */
    @Enumerated(EnumType.STRING)
    private Role doneBy;

    protected Prescription() {
    }

    public Prescription(Patient patient, PrescriptionType type, String description) {
        this.patient = patient;
        this.type = type;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public Patient getPatient() {
        return patient;
    }

    public PrescriptionType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public boolean isDone() {
        return done;
    }

    public Role getDoneBy() {
        return doneBy;
    }

    public void execute(Role by) {
        this.done = true;
        this.doneBy = by;
    }
}
