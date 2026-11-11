package com.example.lr23.service;

import com.example.lr23.dto.PrescriptionRequest;
import com.example.lr23.dto.PrescriptionResponse;
import com.example.lr23.exception.BadRequestException;
import com.example.lr23.exception.ConflictException;
import com.example.lr23.exception.ForbiddenException;
import com.example.lr23.exception.NotFoundException;
import com.example.lr23.model.Patient;
import com.example.lr23.model.Prescription;
import com.example.lr23.model.PrescriptionType;
import com.example.lr23.model.Role;
import com.example.lr23.repository.PatientRepository;
import com.example.lr23.repository.PrescriptionRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PrescriptionService {

    private final PrescriptionRepository prescriptions;
    private final PatientRepository patients;

    public PrescriptionService(PrescriptionRepository prescriptions, PatientRepository patients) {
        this.prescriptions = prescriptions;
        this.patients = patients;
    }

    /** Призначення створює лише лікар; виписаному пацієнту нові призначення створювати не можна. */
    public PrescriptionResponse create(Long patientId, PrescriptionRequest request, Role role) {
        Patient patient = findPatient(patientId);
        Access.requireDoctor(role);
        PrescriptionType type = request == null ? null : PrescriptionType.parse(request.type());
        if (type == null) {
            throw new BadRequestException("type must be PROCEDURE, MEDICINE or OPERATION");
        }
        if (patient.isDischarged()) {
            throw new ConflictException("The patient is already discharged");
        }
        Prescription prescription = prescriptions.save(new Prescription(patient, type, request.description()));
        return PrescriptionResponse.from(prescription);
    }

    /** Призначення пацієнта; status = done | pending фільтрує за виконанням, без нього повертаються всі. */
    @Transactional(readOnly = true)
    public List<PrescriptionResponse> listForPatient(Long patientId, String status) {
        findPatient(patientId);
        List<Prescription> found;
        if (status == null || status.isBlank()) {
            found = prescriptions.findByPatientIdOrderById(patientId);
        } else if (status.equalsIgnoreCase("done")) {
            found = prescriptions.findByPatientIdAndDoneOrderById(patientId, true);
        } else if (status.equalsIgnoreCase("pending")) {
            found = prescriptions.findByPatientIdAndDoneOrderById(patientId, false);
        } else {
            throw new BadRequestException("status must be done or pending");
        }
        return found.stream().map(PrescriptionResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public PrescriptionResponse get(Long id) {
        return PrescriptionResponse.from(find(id));
    }

    /**
     * Лікар може виконати будь-яке призначення, медсестра лише процедуру або ліки. Виконати можна один раз.
     */
    public PrescriptionResponse execute(Long id, Role role) {
        Prescription prescription = find(id);
        Access.requireStaff(role);
        if (role == Role.NURSE && prescription.getType() == PrescriptionType.OPERATION) {
            throw new ForbiddenException("A nurse cannot perform an operation");
        }
        if (prescription.isDone()) {
            throw new ConflictException("The prescription is already executed");
        }
        prescription.execute(role);
        return PrescriptionResponse.from(prescription);
    }

    public void delete(Long id) {
        prescriptions.delete(find(id));
    }

    private Patient findPatient(Long id) {
        return patients.findById(id).orElseThrow(() -> new NotFoundException("Patient not found: " + id));
    }

    private Prescription find(Long id) {
        return prescriptions.findById(id).orElseThrow(() -> new NotFoundException("Prescription not found: " + id));
    }
}
