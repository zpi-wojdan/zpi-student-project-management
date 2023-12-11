package pwr.zpibackend.services.university;

import pwr.zpibackend.dto.university.TitleDTO;
import pwr.zpibackend.models.university.Title;

import java.util.List;

public interface ITitleService {
    List<Title> getAllTitles();
    Title addTitle(TitleDTO title);
    Title updateTitle(Long titleId, TitleDTO updatedTitle);
    Title deleteTitle(Long titleId);
    Title getTitleByName(String name);
}
