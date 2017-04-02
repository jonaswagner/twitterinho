package ch.uzh.ase.repository;

import ch.uzh.ase.domain.Sentiment;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by flaviokeller on 28.03.17.
 */

public interface SentimentRepository extends CrudRepository<Sentiment, Long> {
}
