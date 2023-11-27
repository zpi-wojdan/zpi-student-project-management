package pwr.zpibackend.repositories.thesis;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import pwr.zpibackend.models.thesis.Thesis;

import java.util.List;


@RepositoryRestResource
public interface ThesisRepository extends JpaRepository<Thesis, Long>{
    List<Thesis> findAllByOrderByNamePLAsc();

    List<Thesis> findAllByStatusId(Long statusId);

    List<Thesis> findAllBySupervisorIdAndStatusId(Long empId, Long statId);

    List<Thesis> findAllBySupervisorId(Long empId);
}
