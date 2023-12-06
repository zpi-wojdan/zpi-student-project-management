package pwr.zpibackend.services.university;

import pwr.zpibackend.dto.university.StudyCycleDTO;
import pwr.zpibackend.models.university.StudyCycle;

import java.util.List;

public interface IStudyCycleService {
    List<StudyCycle> getAllStudyCycles();
    StudyCycle getStudyCycleById(Long id);
    StudyCycle saveStudyCycle(StudyCycleDTO studyCycle);
    StudyCycle deleteStudyCycle(Long id);
    StudyCycle updateStudyCycle(Long id, StudyCycleDTO updatedStudyCycle);
}
