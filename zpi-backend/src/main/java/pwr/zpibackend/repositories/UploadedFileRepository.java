package pwr.zpibackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import pwr.zpibackend.models.UploadedFile;

@RepositoryRestResource
public interface UploadedFileRepository extends JpaRepository<UploadedFile, Long>{

}