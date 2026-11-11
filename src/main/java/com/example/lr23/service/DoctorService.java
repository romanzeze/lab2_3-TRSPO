package com.example.lr23.service;

import com.example.lr23.dto.PersonRequest;
import com.example.lr23.dto.PersonResponse;
import com.example.lr23.exception.NotFoundException;
import com.example.lr23.model.Doctor;
import com.example.lr23.repository.DoctorRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DoctorService {

    private final DoctorRepository doctors;

    public DoctorService(DoctorRepository doctors) {
        this.doctors = doctors;
    }

    public PersonResponse create(PersonRequest request) {
        return PersonResponse.from(doctors.save(new Doctor(request.name().trim(), request.profile())));
    }

    @Transactional(readOnly = true)
    public List<PersonResponse> list() {
        return doctors.findAll().stream().map(PersonResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public PersonResponse get(Long id) {
        return PersonResponse.from(find(id));
    }

    public PersonResponse update(Long id, PersonRequest request) {
        Doctor doctor = find(id);
        doctor.setName(request.name().trim());
        doctor.setProfile(request.profile());
        return PersonResponse.from(doctor);
    }

    public void delete(Long id) {
        doctors.delete(find(id));
    }

    private Doctor find(Long id) {
        return doctors.findById(id).orElseThrow(() -> new NotFoundException("Doctor not found: " + id));
    }
}
