package com.example.lr23.entries;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.expression.Operation;

@Entity
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String diagnosis;
    private String prescription;
    private boolean isCured;

    public Patient(int id, String name, String diagnosis, String prescription) {
        this.id = id;
        this.name = name;
        this.diagnosis = diagnosis;
        this.prescription = prescription;
        this.isCured = false;
    }

    public Patient() {
        this.id = 0;
        this.name = "NULL";
        this.diagnosis = "NULL";
        this.prescription = "Pharmacy";
        this.isCured = false;
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

    public void curePatient() {
        this.isCured = true;
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

    public String getStringPrescription() {
        return prescription;
    }

    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }
}