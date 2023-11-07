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

    public Thesis getThesis(Long id) throws NotFoundException {
        return thesisRepository.findById(id)
                .orElseThrow(NotFoundException::new);
    }

    public Thesis addThesis(Thesis thesis) throws NotFoundException {
        Employee supervisor = employeeRepository
                .findById(thesis.getSupervisor().getMail())
                .orElseThrow(NotFoundException::new);

        Thesis newThesis = new Thesis();
        newThesis.setNamePL(thesis.getNamePL());
        newThesis.setNameEN(thesis.getNameEN());
        newThesis.setDescriptionPL(thesis.getDescriptionPL());
        newThesis.setDescriptionEN(thesis.getDescriptionEN());
        newThesis.setNum_people(thesis.getNum_people());
        newThesis.setSupervisor(supervisor);
        newThesis.setPrograms(thesis.getPrograms());
        newThesis.setStudyCycle(thesis.getStudyCycle());
        newThesis.setStatus(thesis.getStatus());
        newThesis.setOccupied(0);

        thesisRepository.saveAndFlush(newThesis);
        return thesis;
    }

    public Thesis updateThesis(Long id, Thesis param) throws NotFoundException {
        System.out.println(param.getSupervisor().getName());
        if (thesisRepository.existsById(id)) {
            Thesis updated = thesisRepository.findById(id).get();
            updated.setNamePL(param.getNamePL());
            updated.setNameEN(param.getNameEN());
            updated.setDescriptionPL(param.getDescriptionPL());
            updated.setDescriptionEN(param.getDescriptionEN());
            updated.setNum_people(param.getNum_people());

            if (employeeRepository.existsById(param.getSupervisor().getMail())) {
                Employee supervisor = employeeRepository.findById(param.getSupervisor().getMail()).get();
                updated.setSupervisor(supervisor);
            }
            else{
                throw new NotFoundException();
            }

            updated.setPrograms(param.getPrograms());
            updated.setStudyCycle(param.getStudyCycle());
            thesisRepository.saveAndFlush(updated);
            return updated;
        }
        throw new NotFoundException();
    }

}
