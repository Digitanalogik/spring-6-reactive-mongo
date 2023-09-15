package fi.soininen.tatu.spring6reactivemongo.mappers;

import fi.soininen.tatu.spring6reactivemongo.domain.Customer;
import fi.soininen.tatu.spring6reactivemongo.model.CustomerDTO;
import org.mapstruct.Mapper;

@Mapper
public interface CustomerMapper {
    Customer customerDtoToCustomer(CustomerDTO customerDTO);
    CustomerDTO customerToCustomerDTO(Customer customer);
}
