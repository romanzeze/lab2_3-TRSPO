package com.example.lr23.controller;

import com.example.lr23.dto.DiagnosisRequest;
import com.example.lr23.dto.DischargeRequest;
import com.example.lr23.dto.PatientRequest;
import com.example.lr23.dto.PatientResponse;
import com.example.lr23.model.Role;
import com.example.lr23.service.PatientService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/patients")
public class PatientController {

    private final PatientService patients;

    public PatientController(PatientService patients) {
        this.patients = patients;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PatientResponse create(@Valid @RequestBody PatientRequest request) {
        return patients.create(request);
    }

    @GetMapping
    public List<PatientResponse> list() {
        return patients.list();
    }

    @GetMapping("/{id}")
    public PatientResponse get(@PathVariable Long id) {
        return patients.get(id);
    }

    @PutMapping("/{id}")
    public PatientResponse update(@PathVariable Long id, @Valid @RequestBody PatientRequest request) {
        return patients.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        patients.delete(id);
    }

    /** Лікар визначає діагноз. Роль передається в заголовку X-Role. */
    @PostMapping("/{id}/diagnosis")
    public PatientResponse setDiagnosis(@PathVariable Long id,
                                        @RequestHeader(value = "X-Role", required = false) String role,
                                        @RequestBody(required = false) DiagnosisRequest request) {
        return patients.setDiagnosis(id, request, Role.parse(role));
    }

    /** Виписка пацієнта з фіксацією кінцевого діагнозу. */
    @PostMapping("/{id}/discharge")
    public PatientResponse discharge(@PathVariable Long id,
                                     @RequestHeader(value = "X-Role", required = false) String role,
                                     @RequestBody(required = false) DischargeRequest request) {
        return patients.discharge(id, request, Role.parse(role));
    }
}
