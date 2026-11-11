package com.example.lr23.repository;

import com.example.lr23.model.Prescription;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    List<Prescription> findByPatientIdOrderById(Long patientId);

    List<Prescription> findByPatientIdAndDoneOrderById(Long patientId, boolean done);
}
