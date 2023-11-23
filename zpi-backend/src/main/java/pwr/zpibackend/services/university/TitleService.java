package pwr.zpibackend.services.university;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pwr.zpibackend.dto.university.TitleDTO;
import pwr.zpibackend.exceptions.AlreadyExistsException;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.university.Title;
import pwr.zpibackend.repositories.university.TitleRepository;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class TitleService {

    private final TitleRepository titleRepository;

    public List<Title> getAllTitles() {
        return titleRepository.findAll();
    }

    public Title getTitle(Long titleId) {
        return titleRepository.findById(titleId).orElseThrow(
                () -> new NotFoundException("title with id " + titleId + " does not exist")
        );
    }

    public Title addTitle(TitleDTO title) {
        if (titleRepository.existsByName(title.getName())) {
            throw new AlreadyExistsException("title with name " + title.getName() + " already exists");
        }
        Title newtitle = new Title(title.getName());
        return titleRepository.save(newtitle);
    }

    public Title updateTitle(Long titleId, TitleDTO updatedTitle) {
        if (titleRepository.existsByName(updatedTitle.getName())) {
            if (!(Objects.equals(titleRepository.findByName(updatedTitle.getName()).get().getId(), titleId))) {
                throw new AlreadyExistsException("title with name " + updatedTitle.getName() + " already exists");
            }
        }
        Title title = titleRepository.findById(titleId).orElse(null);
        if (title != null) {
            title.setName(updatedTitle.getName());
            return titleRepository.save(title);
        }
        throw new NotFoundException("title with id " + titleId + " does not exist");
    }

    public Title deleteTitle(Long titleId) {
        Title title = titleRepository.findById(titleId).orElse(null);
        if (title != null) {
            titleRepository.delete(title);
            return title;
        } else {
            throw new NotFoundException("title with id " + titleId + " does not exist");
        }
    }

    public Title getTitleByName(String name) {
        return titleRepository.findByName(name).orElseThrow(
                () -> new NotFoundException("title with name " + name + " does not exist")
        );
    }

}
