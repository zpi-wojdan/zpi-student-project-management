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

    @Query("SELECT t FROM Thesis t " +
            "WHERE t.status.Id = :statusId")
    List<Thesis> findAllByStatusId(@Param("statusId") Long statusId);
    @Query("SELECT t FROM Thesis t " +
            "WHERE t.supervisor.id = :empId " +
            "AND t.status.Id = :statId")
    List<Thesis> findAllByEmployeeIdAndStatusName(@Param("empId") Long empId, @Param("statId") Long statId);
    @Query("SELECT t FROM Thesis t " +
            "WHERE t.supervisor.id = :empId")
    List<Thesis> findAllByEmployeeId(@Param("empId") Long empId);
}
