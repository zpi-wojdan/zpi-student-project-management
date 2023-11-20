package pwr.zpibackend.services.thesis;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pwr.zpibackend.dto.thesis.ThesisDTO;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.university.Program;
import pwr.zpibackend.models.user.Employee;
import pwr.zpibackend.models.thesis.Thesis;
import pwr.zpibackend.repositories.thesis.StatusRepository;
import pwr.zpibackend.repositories.university.ProgramRepository;
import pwr.zpibackend.repositories.university.StudyCycleRepository;
import pwr.zpibackend.repositories.user.EmployeeRepository;
import pwr.zpibackend.repositories.thesis.ThesisRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ThesisService {

    private final ThesisRepository thesisRepository;
    private final EmployeeRepository employeeRepository;
    private final ProgramRepository programRepository;
    private final StudyCycleRepository studyCycleRepository;
    private final StatusRepository statusRepository;

    public List<Thesis> getAllTheses() {
        return thesisRepository.findAll();
    }

    public Thesis getThesis(Long id) {
        return thesisRepository.findById(id)
                .orElseThrow(NotFoundException::new);
    }

    public Thesis addThesis(ThesisDTO thesis) {
        Employee supervisor = employeeRepository
                .findById(thesis.getSupervisorId())
                .orElseThrow(NotFoundException::new);

        Thesis newThesis = new Thesis();
        newThesis.setNamePL(thesis.getNamePL());
        newThesis.setNameEN(thesis.getNameEN());
        newThesis.setDescriptionPL(thesis.getDescriptionPL());
        newThesis.setDescriptionEN(thesis.getDescriptionEN());
        newThesis.setNumPeople(thesis.getNumPeople());
        newThesis.setSupervisor(supervisor);
        newThesis.setPrograms(new ArrayList<>());
        thesis.getProgramIds().forEach(programId -> {
            Program program = programRepository.findById(programId).orElseThrow(NotFoundException::new);
            newThesis.getPrograms().add(program);
        });
        newThesis.setStudyCycle(studyCycleRepository.findById(
                thesis.getStudyCycleId()).orElseThrow(NotFoundException::new)
        );
        newThesis.setStatus(statusRepository.findById(thesis.getStatusId()).orElseThrow(NotFoundException::new));
        newThesis.setOccupied(0);

        thesisRepository.saveAndFlush(newThesis);
        return newThesis;
    }

    public Thesis updateThesis(Long id, ThesisDTO thesis) {
        if (thesisRepository.existsById(id)) {
            Thesis updated = thesisRepository.findById(id).get();
            updated.setNamePL(thesis.getNamePL());
            updated.setNameEN(thesis.getNameEN());
            updated.setDescriptionPL(thesis.getDescriptionPL());
            updated.setDescriptionEN(thesis.getDescriptionEN());
            updated.setNumPeople(thesis.getNumPeople());

            updated.setSupervisor(employeeRepository.findById(
                    thesis.getSupervisorId()).orElseThrow(NotFoundException::new)
            );
            thesis.getProgramIds().forEach(programId -> {
                Program program = programRepository.findById(programId).orElseThrow(NotFoundException::new);
                updated.getPrograms().add(program);
            });
            updated.setStudyCycle(studyCycleRepository.findById(
                    thesis.getStudyCycleId()).orElseThrow(NotFoundException::new)
            );
            updated.setStatus(statusRepository.findById(thesis.getStatusId()).orElseThrow(NotFoundException::new));
            thesisRepository.saveAndFlush(updated);
            return updated;
        }
        throw new NotFoundException();
    }

}
