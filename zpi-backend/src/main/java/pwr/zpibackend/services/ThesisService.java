package pwr.zpibackend.services;

import lombok.AllArgsConstructor;
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
    private final EmployeeRepository employeeRepository;

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

    public Thesis updateThesis(Long id, Thesis param) throws NotFoundException {
        System.out.println(param.getSupervisor().getName());
        if (thesisRepository.existsById(id)) {
            Thesis updated = thesisRepository.findById(id).get();
            updated.setNamePL(param.getNamePL());
            updated.setNameEN(param.getNameEN());
            updated.setDescription(param.getDescription());
            updated.setNum_people(param.getNum_people());

            if (employeeRepository.existsById(param.getSupervisor().getMail())) {
                Employee supervisor = employeeRepository.findById(param.getSupervisor().getMail()).get();
                updated.setSupervisor(supervisor);
            }
            else{
                throw new NotFoundException();
            }

            updated.setFaculty(param.getFaculty());
            updated.setField(param.getField());
            updated.setEdu_cycle(param.getEdu_cycle());
            thesisRepository.saveAndFlush(updated);
            return updated;
        }
        throw new NotFoundException();
    }

}
