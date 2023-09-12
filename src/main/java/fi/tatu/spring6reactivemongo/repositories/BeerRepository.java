package fi.tatu.spring6reactivemongo.repositories;

import fi.tatu.spring6reactivemongo.domain.Beer;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface BeerRepository extends ReactiveMongoRepository<Beer, String> {
}
