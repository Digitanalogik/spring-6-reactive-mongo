package fi.tatu.spring6reactivemongo.services;

import fi.tatu.spring6reactivemongo.model.CustomerDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomerService {

    Flux<CustomerDTO> listCustomers();

    Mono<CustomerDTO> getById(String id);

    Mono<CustomerDTO> findFirstByCustomerName(String customerName);

    Mono<CustomerDTO> saveCustomer(Mono<CustomerDTO> customerDtoMono);
    Mono<CustomerDTO> saveCustomer(CustomerDTO customerDto);

    Mono<CustomerDTO> updateCustomer(String customerId, CustomerDTO customerDTO);

    Mono<CustomerDTO> patchCustomer(String customerId, CustomerDTO customerDTO);

    Mono<Void> deleteCustomerById(String customerId);

}
