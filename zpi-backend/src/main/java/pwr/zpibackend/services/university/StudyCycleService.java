package pwr.zpibackend.services.university;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pwr.zpibackend.dto.university.StudyCycleDTO;
import pwr.zpibackend.models.university.StudyCycle;
import pwr.zpibackend.repositories.university.StudyCycleRepository;
import pwr.zpibackend.exceptions.NotFoundException;

import java.util.List;

@Service
@AllArgsConstructor
public class StudyCycleService {

    private StudyCycleRepository studyCycleRepository;

    public List<StudyCycle> getAllStudyCycles() {
        return studyCycleRepository.findAll();
    }

    public StudyCycle getStudyCycleById(Long id) throws NotFoundException {
        return studyCycleRepository.findById(id)
                .orElseThrow(NotFoundException::new);
    }

    public StudyCycle saveStudyCycle(StudyCycleDTO studyCycle) {
        StudyCycle newStudyCycle = new StudyCycle();
        newStudyCycle.setName(studyCycle.getName());
        return studyCycleRepository.save(newStudyCycle);
    }


    public StudyCycle deleteStudyCycle(Long id) throws NotFoundException {
        StudyCycle studyCycle = studyCycleRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        studyCycleRepository.delete(studyCycle);
        return studyCycle;
    }

    public StudyCycle updateStudyCycle(Long id, StudyCycleDTO updatedStudyCycle) throws NotFoundException {
        StudyCycle existingStudyCycle = studyCycleRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        existingStudyCycle.setName(updatedStudyCycle.getName());
        return studyCycleRepository.save(existingStudyCycle);
    }
}

