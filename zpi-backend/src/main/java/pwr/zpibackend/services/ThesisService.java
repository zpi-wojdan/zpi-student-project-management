package pwr.zpibackend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pwr.zpibackend.models.Thesis;
import pwr.zpibackend.repositories.ThesisRepository;

import java.util.List;

@Service
public class ThesisService {

    private final ThesisRepository thesisRepository;

    public ThesisService(ThesisRepository thesisRepository) {
        this.thesisRepository = thesisRepository;
    }

    public List<Thesis> getAllTheses() {
        return thesisRepository.findAll();
    }

    public Thesis getThesis(Long id) {
        return thesisRepository.findById(id).get();
    }
}
