package pwr.zpibackend.repositories.thesis;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import pwr.zpibackend.models.thesis.Thesis;
import pwr.zpibackend.models.university.Deadline;

import java.util.List;


@RepositoryRestResource
public interface ThesisRepository extends JpaRepository<Thesis, Long>{
    List<Thesis> findAllByOrderByNamePLAsc();

    List<Thesis> findAllByStatusName(String name, Sort sort);

    List<Thesis> findAllBySupervisorIdAndStatusName(Long empId, String statName, Sort sort);

    List<Thesis> findAllBySupervisorId(Long empId, Sort sort);
    List<Thesis> findAllBySupervisor_IdAndAndStatus_NameIn(Long empId, List<String> statNames, Sort sort);

    List<Thesis> findAllByStatusNameIn(List<String> statusNames, Sort sort);
    List<Thesis> findAllByOrderByStudyCycleNameDescIdDesc();
}
