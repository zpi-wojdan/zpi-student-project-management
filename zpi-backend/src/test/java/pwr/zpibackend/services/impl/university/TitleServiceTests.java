package pwr.zpibackend.services.impl.university;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pwr.zpibackend.dto.university.TitleDTO;
import pwr.zpibackend.exceptions.AlreadyExistsException;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.university.Title;
import pwr.zpibackend.repositories.university.TitleRepository;
import pwr.zpibackend.repositories.user.EmployeeRepository;
import pwr.zpibackend.services.impl.university.TitleService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TitleServiceTests {

    @Mock
    private TitleRepository titleRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private TitleService titleService;

    @Test
    public void testGetAllTitles() {
        when(titleRepository.findAll()).thenReturn(Arrays.asList(new Title("Test Title", 1), new Title("Test Title 2", 2)));

        List<Title> result = titleService.getAllTitles();

        verify(titleRepository, times(1)).findAll();
    }

    @Test
    public void testAddTitle() {
        TitleDTO titleDTO = new TitleDTO("Test Title", 1);
        Title title = new Title("Test Title", 1);
        when(titleRepository.existsByName(titleDTO.getName())).thenReturn(false);
        when(titleRepository.save(any(Title.class))).thenReturn(title);

        Title result = titleService.addTitle(titleDTO);

        verify(titleRepository, times(1)).existsByName(titleDTO.getName());
        verify(titleRepository, times(1)).save(any(Title.class));
    }

    @Test
    public void testAddTitle_AlreadyExistsException() {
        TitleDTO titleDTO = new TitleDTO("Test Title", 1);
        when(titleRepository.existsByName(titleDTO.getName())).thenReturn(true);

        assertThrows(AlreadyExistsException.class, () -> titleService.addTitle(titleDTO));
    }

    @Test
    public void testUpdateTitle() {
        Long titleId = 1L;
        TitleDTO updatedTitleDTO = new TitleDTO("Updated Test Title", 2);
        Title title = new Title("Test Title", 1);
        when(titleRepository.findById(titleId)).thenReturn(Optional.of(title));
        when(titleRepository.existsByName(updatedTitleDTO.getName())).thenReturn(false);
        when(titleRepository.save(any(Title.class))).thenReturn(title);

        Title result = titleService.updateTitle(titleId, updatedTitleDTO);

        verify(titleRepository, times(1)).findById(titleId);
        verify(titleRepository, times(1)).existsByName(updatedTitleDTO.getName());
        verify(titleRepository, times(1)).save(any(Title.class));
    }

    @Test
    public void testUpdateTitle_NotFoundException() {
        Long titleId = 1L;
        TitleDTO updatedTitleDTO = new TitleDTO("Updated Test Title", 2);
        when(titleRepository.findById(titleId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> titleService.updateTitle(titleId, updatedTitleDTO));
    }

    @Test
    public void testDeleteTitle() {
        Long titleId = 1L;
        Title title = new Title("Test Title", 1);
        when(titleRepository.findById(titleId)).thenReturn(Optional.of(title));

        Title result = titleService.deleteTitle(titleId);

        verify(titleRepository, times(1)).findById(titleId);
        verify(titleRepository, times(1)).delete(title);
    }

    @Test
    public void testDeleteTitle_NotFoundException() {
        Long titleId = 1L;
        when(titleRepository.findById(titleId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> titleService.deleteTitle(titleId));
    }

    @Test
    public void testGetTitleByName() {
        String name = "Test Title";
        Title title = new Title("Test Title", 1);
        when(titleRepository.findByName(name)).thenReturn(Optional.of(title));

        Title result = titleService.getTitleByName(name);

        verify(titleRepository, times(1)).findByName(name);
    }

    @Test
    public void testGetTitleByName_NotFoundException() {
        String name = "Test Title";
        when(titleRepository.findByName(name)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> titleService.getTitleByName(name));
    }
}