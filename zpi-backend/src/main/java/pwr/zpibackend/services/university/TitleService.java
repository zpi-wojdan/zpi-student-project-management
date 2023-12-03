package pwr.zpibackend.services.university;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pwr.zpibackend.dto.university.TitleDTO;
import pwr.zpibackend.exceptions.AlreadyExistsException;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.university.Title;
import pwr.zpibackend.models.user.Employee;
import pwr.zpibackend.repositories.university.TitleRepository;
import pwr.zpibackend.repositories.user.EmployeeRepository;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class TitleService {

    private final TitleRepository titleRepository;
    private final EmployeeRepository employeeRepository;

    public List<Title> getAllTitles() {
        return titleRepository.findAll();
    }

    public Title getTitle(Long titleId) {
        return titleRepository.findById(titleId).orElseThrow(
                () -> new NotFoundException("title with id " + titleId + " does not exist")
        );
    }

    public Title addTitle(TitleDTO title) {
        System.out.println(title);
        if (titleRepository.existsByName(title.getName())) {
            throw new AlreadyExistsException("title with name " + title.getName() + " already exists");
        }
        Title newtitle = new Title(title.getName(), title.getNumTheses());
        return titleRepository.save(newtitle);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Title updateTitle(Long titleId, TitleDTO updatedTitle) {
        if (titleRepository.existsByName(updatedTitle.getName())) {
            if (!(Objects.equals(titleRepository.findByName(updatedTitle.getName()).get().getId(), titleId))) {
                throw new AlreadyExistsException("title with name " + updatedTitle.getName() + " already exists");
            }
        }
        Title title = titleRepository.findById(titleId).orElse(null);
        if (title != null) {
            List<Employee> employees = employeeRepository.findAllByTitleId(titleId);
            for (Employee employee : employees) {
                // if the employee had the limit of thesis based on the title, update it
                if (Objects.equals(employee.getNumTheses(), title.getNumTheses()))
                    employee.setNumTheses(updatedTitle.getNumTheses());
                else
                    // if the employee had a custom limit of thesis, update it only if the new limit is higher
                    employee.setNumTheses(Math.max(employee.getNumTheses(), updatedTitle.getNumTheses()));
                employeeRepository.save(employee);
            }

            title.setName(updatedTitle.getName());
            title.setNumTheses(updatedTitle.getNumTheses());
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
