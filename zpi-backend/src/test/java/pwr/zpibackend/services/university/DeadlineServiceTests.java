package pwr.zpibackend.services.university;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import pwr.zpibackend.dto.university.DeadlineDTO;
import pwr.zpibackend.exceptions.AlreadyExistsException;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.university.Deadline;
import pwr.zpibackend.models.user.Role;
import pwr.zpibackend.repositories.university.DeadlineRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
public class DeadlineServiceTests {

    @MockBean
    private DeadlineRepository deadlineRepository;

    @Autowired
    private DeadlineService deadlineService;

    private List<Deadline> deadlines;
    private List<Deadline> orderedDeadlines;
    private Deadline deadline;
    private DeadlineDTO deadlineDTO;

    @BeforeEach
    public void setUp() {
        deadline = new Deadline();
        deadline.setId(1L);
        deadline.setNamePL("Czynnosc 1");
        deadline.setNameEN("Activity 1");
        deadline.setDeadlineDate(LocalDate.of(2023, 11, 20));

        Deadline deadline2 = new Deadline();
        deadline2.setId(2L);
        deadline2.setNamePL("Czynnosc 2");
        deadline2.setNameEN("Activity 2");
        deadline2.setDeadlineDate(LocalDate.of(2023, 11, 19));

        deadlineDTO = new DeadlineDTO();
        deadlineDTO.setNamePL("Czynnosc 3");
        deadlineDTO.setNameEN("Activity 3");
        deadlineDTO.setDeadlineDate(LocalDate.of(2023, 11, 20));

        deadlines = List.of(deadline, deadline2);
        orderedDeadlines = List.of(deadline2, deadline);
    }

    @Test
    public void testGetAllDeadlines() {
        when(deadlineRepository.findAll()).thenReturn(deadlines);

        List<Deadline> result = deadlineService.getAllDeadlines();

        assertEquals(2, result.size());
        assertEquals(deadline, result.get(0));
    }

    @Test
    public void testGetAllDeadlinesOrderedByDate() {
        when(deadlineRepository.findAllByOrderByDeadlineDateAsc()).thenReturn(orderedDeadlines);

        List<Deadline> result = deadlineService.getAllDeadlinesOrderedByDateAsc();

        assertEquals(2, result.size());
        assertEquals(deadline, result.get(1));
    }

    @Test
    public void testGetDeadlineById() {
        when(deadlineRepository.findById(deadline.getId())).thenReturn(Optional.of(deadline));

        Deadline result = deadlineService.getDeadline(deadline.getId());

        assertEquals(deadline, result);
    }

    @Test
    public void testGetDeadlineByIdNotFound() {
        Long id = 1L;
        when(deadlineRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> deadlineService.getDeadline(id));
    }

    @Test
    public void testAddDeadline() {
        Deadline newDeadline = new Deadline();
        newDeadline.setNamePL(deadlineDTO.getNamePL());
        newDeadline.setNameEN(deadlineDTO.getNameEN());
        newDeadline.setDeadlineDate(deadlineDTO.getDeadlineDate());

        when(deadlineRepository.existsByNamePL(deadlineDTO.getNamePL())).thenReturn(false);
        when(deadlineRepository.existsByNameEN(deadlineDTO.getNameEN())).thenReturn(false);
        when(deadlineRepository.save(newDeadline)).thenReturn(newDeadline);

        Deadline result = deadlineService.addDeadline(deadlineDTO);

        assertEquals(newDeadline, result);
    }

    @Test
    public void testAddDeadlineNamePLAlreadyExists() {
        when(deadlineRepository.existsByNamePL(deadlineDTO.getNamePL())).thenReturn(true);

        assertThrows(AlreadyExistsException.class, () -> deadlineService.addDeadline(deadlineDTO));
    }

    @Test
    public void testAddDeadlineNameENAlreadyExists() {
        when(deadlineRepository.existsByNamePL(deadlineDTO.getNamePL())).thenReturn(false);
        when(deadlineRepository.existsByNameEN(deadlineDTO.getNameEN())).thenReturn(true);

        assertThrows(AlreadyExistsException.class, () -> deadlineService.addDeadline(deadlineDTO));
    }

    @Test
    public void testAddDeadlineDateInPast() {
        deadlineDTO.setDeadlineDate(LocalDate.of(2020, 11, 20));
        when(deadlineRepository.existsByNamePL(deadlineDTO.getNamePL())).thenReturn(false);
        when(deadlineRepository.existsByNameEN(deadlineDTO.getNameEN())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> deadlineService.addDeadline(deadlineDTO));
    }

    @Test
    public void testUpdateDeadline() {
        Deadline updatedDeadline = new Deadline();
        updatedDeadline.setId(deadline.getId());
        updatedDeadline.setNamePL(deadlineDTO.getNamePL());
        updatedDeadline.setNameEN(deadlineDTO.getNameEN());
        updatedDeadline.setDeadlineDate(deadlineDTO.getDeadlineDate());

        when(deadlineRepository.findById(deadline.getId())).thenReturn(Optional.of(deadline));
        when(deadlineRepository.existsByNamePL(deadlineDTO.getNamePL())).thenReturn(false);
        when(deadlineRepository.existsByNameEN(deadlineDTO.getNameEN())).thenReturn(false);
        when(deadlineRepository.save(updatedDeadline)).thenReturn(updatedDeadline);

        Deadline result = deadlineService.updateDeadline(deadline.getId(), deadlineDTO);

        assertEquals(deadline, result);
    }

    @Test
    public void testUpdateDeadlineNotFound() {
        when(deadlineRepository.findById(deadline.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> deadlineService.updateDeadline(deadline.getId(), deadlineDTO));
    }

    @Test
    public void testUpdateDeadlineNamePLAlreadyExists() {
        when(deadlineRepository.findById(deadline.getId())).thenReturn(Optional.of(deadline));
        when(deadlineRepository.existsByNamePL(deadlineDTO.getNamePL())).thenReturn(true);

        assertThrows(AlreadyExistsException.class, () -> deadlineService.updateDeadline(deadline.getId(), deadlineDTO));
    }

    @Test
    public void testUpdateDeadlineNameENAlreadyExists() {
        when(deadlineRepository.findById(deadline.getId())).thenReturn(Optional.of(deadline));
        when(deadlineRepository.existsByNamePL(deadlineDTO.getNamePL())).thenReturn(false);
        when(deadlineRepository.existsByNameEN(deadlineDTO.getNameEN())).thenReturn(true);

        assertThrows(AlreadyExistsException.class, () -> deadlineService.updateDeadline(deadline.getId(), deadlineDTO));
    }

    @Test
    public void testUpdateDeadlineDateInPast() {
        deadlineDTO.setDeadlineDate(LocalDate.of(2020, 11, 20));
        when(deadlineRepository.findById(deadline.getId())).thenReturn(Optional.of(deadline));
        when(deadlineRepository.existsByNamePL(deadlineDTO.getNamePL())).thenReturn(false);
        when(deadlineRepository.existsByNameEN(deadlineDTO.getNameEN())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> deadlineService.updateDeadline(deadline.getId(), deadlineDTO));
    }

    @Test
    public void testDeleteDeadline() {
        when(deadlineRepository.findById(deadline.getId())).thenReturn(Optional.of(deadline));

        Deadline result = deadlineService.deleteDeadline(deadline.getId());

        assertEquals(deadline, result);
    }

    @Test
    public void testDeleteDeadlineNotFound() {
        when(deadlineRepository.findById(deadline.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> deadlineService.deleteDeadline(deadline.getId()));
    }
}
