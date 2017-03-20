package ch.uzh.ase.repository;

import ch.uzh.ase.domain.Resource;
import org.springframework.data.repository.CrudRepository;

public interface ResourceRepository extends CrudRepository<Resource, Long> {
}