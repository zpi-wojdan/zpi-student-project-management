package pwr.zpibackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import pwr.zpibackend.models.Thesis;


@RepositoryRestResource
public interface ThesisRepository extends JpaRepository<Thesis, Long>{ }
