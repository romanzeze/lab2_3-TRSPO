package com.example.lr23.controller;

import com.example.lr23.dto.PrescriptionRequest;
import com.example.lr23.dto.PrescriptionResponse;
import com.example.lr23.model.Role;
import com.example.lr23.service.PrescriptionService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PrescriptionController {

    private final PrescriptionService prescriptions;

    public PrescriptionController(PrescriptionService prescriptions) {
        this.prescriptions = prescriptions;
    }

    /** Лікар призначає пацієнту процедуру, ліки або операцію. */
    @PostMapping("/patients/{patientId}/prescriptions")
    @ResponseStatus(HttpStatus.CREATED)
    public PrescriptionResponse create(@PathVariable Long patientId,
                                       @RequestHeader(value = "X-Role", required = false) String role,
                                       @RequestBody(required = false) PrescriptionRequest request) {
        return prescriptions.create(patientId, request, Role.parse(role));
    }

    /** Усі призначення пацієнта; ?status=done або ?status=pending фільтрує за виконанням. */
    @GetMapping("/patients/{patientId}/prescriptions")
    public List<PrescriptionResponse> list(@PathVariable Long patientId,
                                           @RequestParam(required = false) String status) {
        return prescriptions.listForPatient(patientId, status);
    }

    @GetMapping("/prescriptions/{id}")
    public PrescriptionResponse get(@PathVariable Long id) {
        return prescriptions.get(id);
    }

    /** Виконання призначення: лікар виконує будь-яке, медсестра лише процедуру або ліки. */
    @PostMapping("/prescriptions/{id}/execute")
    public PrescriptionResponse execute(@PathVariable Long id,
                                        @RequestHeader(value = "X-Role", required = false) String role) {
        return prescriptions.execute(id, Role.parse(role));
    }

    @DeleteMapping("/prescriptions/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        prescriptions.delete(id);
    }
}
