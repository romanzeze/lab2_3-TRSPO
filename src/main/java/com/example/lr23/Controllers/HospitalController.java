package com.example.lr23.Controllers;

import com.example.lr23.entries.Doctor;
import com.example.lr23.repos.DoctorRepository;
import com.example.lr23.entries.Nurse;
import com.example.lr23.repos.NurseRepository;
import com.example.lr23.entries.Patient;
import com.example.lr23.repos.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/hospital")
public class HospitalController {
    @Autowired
    private final NurseRepository nurseRepository;
    @Autowired
    private final DoctorRepository doctorRepository;
    @Autowired
    private final PatientRepository patientRepository;

    public HospitalController(NurseRepository nurseRepository, DoctorRepository doctorRepository,
                              PatientRepository patientRepository) {
        this.nurseRepository = nurseRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    @PostMapping("/nurse-cure")
    public ResponseEntity<String> nurseCure(@RequestParam int nurseId, @RequestParam int patientId) {
        Optional<Nurse> nurseOptional = nurseRepository.findById(nurseId);
        Optional<Patient> patientOptional = patientRepository.findById(patientId);

        if (nurseOptional.isEmpty() || patientOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid nurse or patient ID");
        }

        Nurse nurse = nurseOptional.get();
        Patient patient = patientOptional.get();

        if (!Objects.equals(patient.getStringPrescription(), "Operation")) {
            patient.curePatient();
            patientRepository.save(patient);
        }
        else
        {
            return ResponseEntity.ok("Nurse is not capable of operating a patient");
        }

        return ResponseEntity.ok("Nurse " + nurse.getName() + " cured patient " + patient.getName());
    }

    @PostMapping("/doctor-cure")
    public ResponseEntity<String> doctorCure(@RequestParam int doctorId, @RequestParam int patientId) {
        Optional<Doctor> doctorOptional = doctorRepository.findById(doctorId);
        Optional<Patient> patientOptional = patientRepository.findById(patientId);

        if (doctorOptional.isEmpty() || patientOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid doctor or patient ID");
        }

        Doctor doctor = doctorOptional.get();
        Patient patient = patientOptional.get();

        patient.curePatient();
        patientRepository.save(patient);

        return ResponseEntity.ok("Doctor " + doctor.getName() + " cured patient " + patient.getName());
    }
}
