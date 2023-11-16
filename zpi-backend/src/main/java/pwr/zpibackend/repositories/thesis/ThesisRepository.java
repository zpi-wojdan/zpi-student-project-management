package pwr.zpibackend.repositories.thesis;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import pwr.zpibackend.models.thesis.Thesis;


@RepositoryRestResource
public interface ThesisRepository extends JpaRepository<Thesis, Long>{ }
