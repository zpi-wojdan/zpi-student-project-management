package pwr.zpibackend.services.impl.university;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pwr.zpibackend.dto.university.StudyCycleDTO;
import pwr.zpibackend.exceptions.AlreadyExistsException;
import pwr.zpibackend.models.university.StudyCycle;
import pwr.zpibackend.repositories.university.StudyCycleRepository;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.services.university.IStudyCycleService;

import java.util.List;

@Service
@AllArgsConstructor
public class StudyCycleService implements IStudyCycleService {

    private StudyCycleRepository studyCycleRepository;

    public List<StudyCycle> getAllStudyCycles() {
        return studyCycleRepository.findAll();
    }

    public StudyCycle getStudyCycleById(Long id) {
        return studyCycleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("StudyCycle with id " + id + " does not exist"));
    }

    public StudyCycle saveStudyCycle(StudyCycleDTO studyCycle) {
        if (studyCycleRepository.existsByName(studyCycle.getName())) {
            throw new AlreadyExistsException("studyCycle with name " + studyCycle.getName() + " already exists");
        }
        StudyCycle newStudyCycle = new StudyCycle();
        newStudyCycle.setName(studyCycle.getName());
        return studyCycleRepository.save(newStudyCycle);
    }


    public StudyCycle deleteStudyCycle(Long id) {
        StudyCycle studyCycle = studyCycleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("StudyCycle with id " + id + " does not exist"));
        studyCycleRepository.delete(studyCycle);
        return studyCycle;
    }

    public StudyCycle updateStudyCycle(Long id, StudyCycleDTO updatedStudyCycle) {
        if (studyCycleRepository.existsByName(updatedStudyCycle.getName())) {
            if (!(studyCycleRepository.findByName(updatedStudyCycle.getName()).get().getId() == id)) {
                throw new AlreadyExistsException("studyCycle with name " + updatedStudyCycle.getName() + " already exists");
            }
        }
        StudyCycle existingStudyCycle = studyCycleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("StudyCycle with id " + id + " does not exist"));
        existingStudyCycle.setName(updatedStudyCycle.getName());
        return studyCycleRepository.save(existingStudyCycle);
    }
}

