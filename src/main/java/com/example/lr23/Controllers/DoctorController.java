package com.example.lr23.Controllers;

import com.example.lr23.entries.Doctor;
import com.example.lr23.repos.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/doctors")
public class DoctorController {

    @Autowired
    private DoctorRepository doctorRepository;


    @GetMapping("/")
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }


    @GetMapping("/{id}")
    public Doctor getDoctorById(@PathVariable(value = "id") int doctorId) throws Exception {
        return doctorRepository.findById(doctorId)
                .orElseThrow(() -> new Exception("Doctor not found for this id :: " + doctorId));
    }


    @PostMapping("/")
    public Doctor createDoctor(@RequestBody Doctor doctor) {
        return doctorRepository.save(doctor);
    }


    @PutMapping("/{id}")
    public Doctor updateDoctor(@PathVariable(value = "id") int doctorId,
                               @RequestBody Doctor doctorDetails) throws Exception {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new Exception("Doctor not found for this id :: " + doctorId));
        doctor.setName(doctorDetails.getName());
        doctor.setProfile(doctorDetails.getProfile());
        final Doctor updatedDoctor = doctorRepository.save(doctor);
        return updatedDoctor;
    }

    // Delete doctor
    @DeleteMapping("/{id}")
    public Map<String, Boolean> deleteDoctor(@PathVariable(value = "id") int doctorId)
            throws Exception {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new Exception("Doctor not found for this id :: " + doctorId));

        doctorRepository.delete(doctor);
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return response;
    }
}






