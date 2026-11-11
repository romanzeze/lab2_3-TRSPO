package com.example.lr23.controller;

import com.example.lr23.dto.PersonRequest;
import com.example.lr23.dto.PersonResponse;
import com.example.lr23.service.NurseService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/nurses")
public class NurseController {

    private final NurseService nurses;

    public NurseController(NurseService nurses) {
        this.nurses = nurses;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PersonResponse create(@Valid @RequestBody PersonRequest request) {
        return nurses.create(request);
    }

    @GetMapping
    public List<PersonResponse> list() {
        return nurses.list();
    }

    @GetMapping("/{id}")
    public PersonResponse get(@PathVariable Long id) {
        return nurses.get(id);
    }

    @PutMapping("/{id}")
    public PersonResponse update(@PathVariable Long id, @Valid @RequestBody PersonRequest request) {
        return nurses.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        nurses.delete(id);
    }
}
