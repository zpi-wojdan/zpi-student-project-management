package pwr.zpibackend.repositories.thesis;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import pwr.zpibackend.models.thesis.Thesis;

import java.util.List;


@RepositoryRestResource
public interface ThesisRepository extends JpaRepository<Thesis, Long>{
    List<Thesis> findAllByOrderByNamePLAsc();

    List<Thesis> findAllByStatusName(String name);

    List<Thesis> findAllBySupervisorIdAndStatusName(Long empId, String statName);

    List<Thesis> findAllBySupervisorId(Long empId);
    List<Thesis> findAllBySupervisor_IdAndAndStatus_NameIn(Long empId, List<String> statNames);

    List<Thesis> findAllByStatusNameIn(List<String> statusNames, Sort sort);
}
