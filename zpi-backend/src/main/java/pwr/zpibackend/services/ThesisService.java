package pwr.zpibackend.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pwr.zpibackend.dto.reports.StudentWithThesisDTO;
import pwr.zpibackend.dto.reports.SupervisorDTO;
import pwr.zpibackend.dto.reports.ThesisGroupDTO;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.Employee;
import pwr.zpibackend.models.Reservation;
import pwr.zpibackend.models.Thesis;
import pwr.zpibackend.repositories.EmployeeRepository;
import pwr.zpibackend.repositories.ThesisRepository;

import java.util.List;
import java.util.stream.Collectors;

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
                .findById(thesis.getSupervisor().getId())
                .orElseThrow(NotFoundException::new);

        Thesis newThesis = new Thesis();
        newThesis.setNamePL(thesis.getNamePL());
        newThesis.setNameEN(thesis.getNameEN());
        newThesis.setDescriptionPL(thesis.getDescriptionPL());
        newThesis.setDescriptionEN(thesis.getDescriptionEN());
        newThesis.setNumPeople(thesis.getNumPeople());
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
            updated.setNumPeople(param.getNumPeople());

            if (employeeRepository.existsById(param.getSupervisor().getId())) {
                Employee supervisor = employeeRepository.findById(param.getSupervisor().getId()).get();
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

    public List<ThesisGroupDTO> getThesisGroups() {
        return thesisRepository.findAll().stream()
                .map(thesis -> {
                    ThesisGroupDTO report = new ThesisGroupDTO();
                    report.setThesisNamePL(thesis.getNamePL());
                    report.setThesisNameEN(thesis.getNameEN());
                    SupervisorDTO supervisor = new SupervisorDTO();
                    supervisor.setName(thesis.getSupervisor().getName());
                    supervisor.setSurname(thesis.getSupervisor().getSurname());
                    supervisor.setMail(thesis.getSupervisor().getMail());
                    supervisor.setTitle(thesis.getSupervisor().getTitle().getName());
                    report.setSupervisor(supervisor);
                    report.setStudents(thesis.getReservations().stream()
                            .map(reservation -> {
                                StudentWithThesisDTO student = new StudentWithThesisDTO();
                                student.setName(reservation.getStudent().getName());
                                student.setSurname(reservation.getStudent().getSurname());
                                student.setMail(reservation.getStudent().getMail());
                                student.setIndex(reservation.getStudent().getIndex());
                                return student;
                            })
                            .collect(Collectors.toList()));
                    return report;
                })
                .collect(Collectors.toList());
    }

}
