package com.example.lr23.Controllers;

import com.example.lr23.entries.Patient;
import com.example.lr23.repos.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/patients")
public class PatientController {

    @Autowired
    private PatientRepository patientRepository;


    @GetMapping("")
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }


    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable(value = "id") int patientId)
            throws Exception {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new Exception("Patient not found for this id :: " + patientId));
        return ResponseEntity.ok().body(patient);
    }


    @PostMapping("")
    public Patient createPatient( @RequestBody Patient patient) {
        return patientRepository.save(patient);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Patient> updatePatient(@PathVariable(value = "id") int patientId,
                                                 @RequestBody Patient patientDetails) throws Exception {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new Exception("Patient not found for this id :: " + patientId));

        patient.setName(patientDetails.getName());
        patient.setDiagnosis(patientDetails.getDiagnosis());
        patient.setPrescription(patientDetails.getStringPrescription());

        final Patient updatedPatient = patientRepository.save(patient);
        return ResponseEntity.ok(updatedPatient);
    }


    @DeleteMapping("/{id}")
    public Map<String, Boolean> deletePatient(@PathVariable(value = "id") int patientId)
            throws Exception {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new Exception("Patient not found for this id :: " + patientId));

        patientRepository.delete(patient);
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return response;
    }
}