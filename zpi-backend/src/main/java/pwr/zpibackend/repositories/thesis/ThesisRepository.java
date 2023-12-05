package pwr.zpibackend.repositories.thesis;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import pwr.zpibackend.models.thesis.Thesis;

import java.util.List;
import java.util.Optional;


@RepositoryRestResource
public interface ThesisRepository extends JpaRepository<Thesis, Long>{
    List<Thesis> findAllByOrderByNamePLAsc();

    List<Thesis> findAllByStatusName(String name, Sort sort);

    List<Thesis> findAllBySupervisorIdAndStatusName(Long empId, String statName, Sort sort);

    List<Thesis> findAllBySupervisorId(Long empId, Sort sort);
    List<Thesis> findAllBySupervisor_IdAndStatus_NameIn(Long empId, List<String> statNames, Sort sort);

    List<Thesis> findAllByStatusNameIn(List<String> statusNames, Sort sort);
    List<Thesis> findAllByLeader_Id(Long leaderId);
    List<Thesis> findAllByOrderByStudyCycleNameDescIdDesc();
    Optional<Thesis> findByReservations_Id(Long id);
    List<Thesis> findAllByStudyCycle_Id(Long cycId);
}
