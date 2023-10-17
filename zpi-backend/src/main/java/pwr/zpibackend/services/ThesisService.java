package pwr.zpibackend.services;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.Employee;
import pwr.zpibackend.models.Thesis;
import pwr.zpibackend.repositories.EmployeeRepository;
import pwr.zpibackend.repositories.ThesisRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ThesisService {

    private final ThesisRepository thesisRepository;

    public List<Thesis> getAllTheses() {
        return thesisRepository.findAll();
    }

    public Thesis getThesis(Long id) {
        Optional<Thesis> thesis = thesisRepository.findById(id);
        return thesis.orElse(null);
    }

    public Thesis addThesis(Thesis thesis)
    {
        thesisRepository.saveAndFlush(thesis);
        return thesis;
    }

    public Thesis updateThesis(Long id, String new_namePl, String new_nameEN_passed,
                               String new_description, Integer new_num_people,
                               Employee new_supervisor, String new_faculty,
                               String new_field, String new_edu_cycle) throws NotFoundException {
        if (thesisRepository.existsById(id)) {
            Thesis updated = thesisRepository.findById(id).get();
            updated.setNamePL(new_namePl);
            updated.setNameEN(new_nameEN_passed);
            updated.setDescription(new_description);
            updated.setNum_people(new_num_people);
            updated.setSupervisor(new_supervisor);
            updated.setFaculty(new_faculty);
            updated.setField(new_field);
            updated.setEdu_cycle(new_edu_cycle);
            thesisRepository.saveAndFlush(updated);
            return updated;
        }
        throw new NotFoundException();
    }

}
