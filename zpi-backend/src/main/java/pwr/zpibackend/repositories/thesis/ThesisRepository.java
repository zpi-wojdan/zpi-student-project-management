package pwr.zpibackend.repositories.thesis;

import org.springframework.data.domain.Sort;
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

    List<Thesis> findAllByStatusNameIn(List<String> statusNames, Sort sort);

    // - - - - - - -

    List<Thesis> findAllByStatusName(String name);
    List<Thesis> findAllBySupervisor_IdAndAndStatus_NameIn(Long empId, List<String> statNames);
    @Query("SELECT t FROM Thesis t " +
            "WHERE t.supervisor.id = :empId " +
            "AND t.status.name = :statName")
    List<Thesis> findAllByEmployeeIdAndStatusName(@Param("empId") Long empId, @Param("statName") String statName);
}
