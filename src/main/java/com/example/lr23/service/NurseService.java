package com.example.lr23.service;

import com.example.lr23.dto.PersonRequest;
import com.example.lr23.dto.PersonResponse;
import com.example.lr23.exception.NotFoundException;
import com.example.lr23.model.Nurse;
import com.example.lr23.repository.NurseRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class NurseService {

    private final NurseRepository nurses;

    public NurseService(NurseRepository nurses) {
        this.nurses = nurses;
    }

    public PersonResponse create(PersonRequest request) {
        return PersonResponse.from(nurses.save(new Nurse(request.name().trim(), request.profile())));
    }

    @Transactional(readOnly = true)
    public List<PersonResponse> list() {
        return nurses.findAll().stream().map(PersonResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public PersonResponse get(Long id) {
        return PersonResponse.from(find(id));
    }

    public PersonResponse update(Long id, PersonRequest request) {
        Nurse nurse = find(id);
        nurse.setName(request.name().trim());
        nurse.setProfile(request.profile());
        return PersonResponse.from(nurse);
    }

    public void delete(Long id) {
        nurses.delete(find(id));
    }

    private Nurse find(Long id) {
        return nurses.findById(id).orElseThrow(() -> new NotFoundException("Nurse not found: " + id));
    }
}
