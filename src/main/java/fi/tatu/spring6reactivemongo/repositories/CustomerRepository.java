package fi.tatu.spring6reactivemongo.repositories;

import fi.tatu.spring6reactivemongo.domain.Customer;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface CustomerRepository extends ReactiveMongoRepository<Customer, String> {
}
