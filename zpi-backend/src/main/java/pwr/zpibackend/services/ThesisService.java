package pwr.zpibackend.services;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pwr.zpibackend.models.Thesis;
import pwr.zpibackend.repositories.EmployeeRepository;
import pwr.zpibackend.repositories.ThesisRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class ThesisService {

    private final ThesisRepository thesisRepository;

    public List<Thesis> getAllTheses() {
        return thesisRepository.findAll();
    }

    public Thesis getThesis(Long id) {
        return thesisRepository.findById(id).get();
    }

    public Thesis addThesis(Thesis thesis)
    {
        thesisRepository.saveAndFlush(thesis);
        return thesis;
    }
}
