package com.example.lr23.service;

import com.example.lr23.dto.DiagnosisRequest;
import com.example.lr23.dto.DischargeRequest;
import com.example.lr23.dto.PatientRequest;
import com.example.lr23.dto.PatientResponse;
import com.example.lr23.exception.BadRequestException;
import com.example.lr23.exception.ConflictException;
import com.example.lr23.exception.NotFoundException;
import com.example.lr23.model.Patient;
import com.example.lr23.model.Role;
import com.example.lr23.repository.PatientRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PatientService {

    private final PatientRepository patients;

    public PatientService(PatientRepository patients) {
        this.patients = patients;
    }

    public PatientResponse create(PatientRequest request) {
        return PatientResponse.from(patients.save(new Patient(request.name().trim())));
    }

    @Transactional(readOnly = true)
    public List<PatientResponse> list() {
        return patients.findAll().stream().map(PatientResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public PatientResponse get(Long id) {
        return PatientResponse.from(find(id));
    }

    public PatientResponse update(Long id, PatientRequest request) {
        Patient patient = find(id);
        patient.setName(request.name().trim());
        return PatientResponse.from(patient);
    }

    public void delete(Long id) {
        patients.delete(find(id));
    }

    /** Діагноз визначає лише лікар. */
    public PatientResponse setDiagnosis(Long id, DiagnosisRequest request, Role role) {
        Patient patient = find(id);
        Access.requireDoctor(role);
        if (request == null || request.diagnosis() == null || request.diagnosis().isBlank()) {
            throw new BadRequestException("diagnosis is required");
        }
        patient.setDiagnosis(request.diagnosis().trim());
        return PatientResponse.from(patient);
    }

    /** Виписка можлива лише лікарем і лише з кінцевим діагнозом; повторно виписати пацієнта не можна. */
    public PatientResponse discharge(Long id, DischargeRequest request, Role role) {
        Patient patient = find(id);
        Access.requireDoctor(role);
        if (request == null || request.finalDiagnosis() == null || request.finalDiagnosis().isBlank()) {
            throw new BadRequestException("finalDiagnosis is required");
        }
        if (patient.isDischarged()) {
            throw new ConflictException("The patient is already discharged");
        }
        patient.discharge(request.finalDiagnosis().trim());
        return PatientResponse.from(patient);
    }

    Patient find(Long id) {
        return patients.findById(id).orElseThrow(() -> new NotFoundException("Patient not found: " + id));
    }
}
