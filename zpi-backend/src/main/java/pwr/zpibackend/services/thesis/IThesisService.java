package pwr.zpibackend.services.thesis;

import pwr.zpibackend.dto.thesis.ThesisDTO;
import pwr.zpibackend.models.thesis.Thesis;

import java.util.List;

public interface IThesisService {
    List<Thesis> getAllTheses();
    List<Thesis> getAllPublicTheses();
    Thesis getThesis(Long id);
    Thesis getThesisByStudentId(Long studentId);
    Thesis addThesis(ThesisDTO thesis);
    Thesis updateThesis(Long id, ThesisDTO thesis);
    Thesis deleteThesis(Long id);
    List<Thesis> getAllThesesByStatusName(String name);
    List<Thesis> getAllThesesExcludingStatusName(String name);
    List<Thesis> getAllThesesForEmployeeByStatusName(Long empId, String statName);
    List<Thesis> getAllThesesForEmployee(Long id);
    List<Thesis> getAllThesesForEmployeeByStatusNameList(Long empId, List<String> statNames);
    List<Thesis> updateThesesStatusInBulk(String statName, List<Long> thesesIds);
    List<Thesis> deleteThesesByStudyCycle(Long cycId);
    List<Thesis> deleteThesesInBulk(List<Long> thesesIds);
}
