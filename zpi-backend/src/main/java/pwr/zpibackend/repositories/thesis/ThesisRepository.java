package pwr.zpibackend.repositories.thesis;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import pwr.zpibackend.models.thesis.Thesis;

import java.util.List;


@RepositoryRestResource
public interface ThesisRepository extends JpaRepository<Thesis, Long>{
    List<Thesis> findAllByOrderByNamePLAsc();
    List<Thesis> findAllByStatusName(String name);
    @Query("SELECT t FROM Thesis t " +
            "WHERE t.supervisor.id = :empId " +
            "AND t.status.name = :statName")
    List<Thesis> findAllByEmployeeIdAndStatusName(@Param("empId") Long empId, @Param("statName") String statName);
    @Query("SELECT t FROM Thesis t " +
            "WHERE t.supervisor.id = :empId")
    List<Thesis> findAllByEmployeeId(@Param("empId") Long empId);
}
