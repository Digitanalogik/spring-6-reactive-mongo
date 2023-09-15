package fi.tatu.spring6reactivemongo.services;

import fi.tatu.spring6reactivemongo.domain.Customer;
import fi.tatu.spring6reactivemongo.mappers.CustomerMapper;
import fi.tatu.spring6reactivemongo.mappers.CustomerMapperImpl;
import fi.tatu.spring6reactivemongo.model.CustomerDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@Slf4j
@SpringBootTest
public class CustomerServiceImplTest {

    @Autowired
    CustomerService customerService;

    @Autowired
    CustomerMapper customerMapper;

    CustomerDTO customerDTO;

    @BeforeEach
    void setUp() {
        customerDTO = customerMapper.customerToCustomerDTO(getTestCustomer());
    }

    public static Customer getTestCustomer() {
        return Customer.builder()
                .customerName("Test Customer")
                .build();
    }

    public static CustomerDTO getTestCustomerDto() {
        return new CustomerMapperImpl().customerToCustomerDTO(getTestCustomer());
    }

    public CustomerDTO getSavedCustomerDto() {
        return customerService.saveCustomer(getTestCustomerDto()).block();
    }

    @Test
    void findAll() {
        CustomerDTO dto = getSavedCustomerDto();

        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        Mono<CustomerDTO> foundDto = customerService.findFirstByCustomerName(dto.getCustomerName());

        foundDto.subscribe(d -> {
            log.info("Found: {}", d.toString());
            atomicBoolean.set(true);
        });

        await().untilTrue(atomicBoolean);
    }

    @Test
    @DisplayName("Find The First Customer By The Name")
    void findFirstByCustomerNameTest() {
        CustomerDTO dto = getSavedCustomerDto();

        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        Mono<CustomerDTO> foundDto = customerService.findFirstByCustomerName(dto.getCustomerName());

        foundDto.subscribe(d -> {
            log.info("Found: {}", d.toString());
            atomicBoolean.set(true);
        });

        await().untilTrue(atomicBoolean);
    }

    @Test
    @DisplayName("Test Save Customer Using Subscriber")
    void saveCustomerUseSubscriber() {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        AtomicReference<CustomerDTO> atomicDto = new AtomicReference<>();

        Mono<CustomerDTO> savedMono = customerService.saveCustomer(Mono.just(customerDTO));

        savedMono.subscribe(savedDto -> {
            log.info("Saved Customer: {}", savedDto.getId());
            atomicBoolean.set(true);
            atomicDto.set(savedDto);
        });

        await().untilTrue(atomicBoolean);

        CustomerDTO persistedDto = atomicDto.get();
        assertThat(persistedDto).isNotNull();
        assertThat(persistedDto.getId()).isNotNull();
    }

    @Test
    @DisplayName("Test Save Customer Using Block")
    void testSaveCustomerUseBlock() {
        CustomerDTO savedDto = customerService.saveCustomer(Mono.just(getTestCustomerDto())).block();
        assertThat(savedDto).isNotNull();
        assertThat(savedDto.getId()).isNotNull();
    }

    @Test
    @DisplayName("Test Update Customer Using Reactive Streams")
    void testUpdateStreaming() {
        final String newName = "New Customer Name";
        AtomicReference<CustomerDTO> atomicDto = new AtomicReference<>();

        customerService.saveCustomer(Mono.just(getTestCustomerDto()))
                .map(savedCustomerDto ->  {
                    savedCustomerDto.setCustomerName(newName);
                    return savedCustomerDto;
                })
                .flatMap(customerService::saveCustomer)
                .flatMap(savedUpdatedDto -> customerService.getById(savedUpdatedDto.getId()))
                .subscribe(dtoFromDb -> {
                    atomicDto.set(dtoFromDb);
                });

        await().until(() -> atomicDto.get() != null);
        assertThat(atomicDto.get().getCustomerName()).isEqualTo(newName);
    }

    @Test
    @DisplayName("Test Update Customer Using Block")
    void testUpdateBlocking() {
        final String newName = "New Customer Name";
        CustomerDTO savedCustomerDto = getSavedCustomerDto();
        savedCustomerDto.setCustomerName(newName);

        CustomerDTO updatedDto = customerService.saveCustomer(Mono.just(savedCustomerDto)).block();

        CustomerDTO fetchedDto = customerService.getById(updatedDto.getId()).block();
        assertThat(fetchedDto.getCustomerName()).isEqualTo(newName);
    }

    @Test
    @DisplayName("Test Delete Customer Using Block")
    void testDeleteCustomer() {
        CustomerDTO customerToDelete = getSavedCustomerDto();

        customerService.deleteCustomerById(customerToDelete.getId()).block();

        Mono<CustomerDTO> expectedEmptyCustomerMono = customerService.getById(customerToDelete.getId());
        CustomerDTO emptyCustomer = expectedEmptyCustomerMono.block();

        assertThat(emptyCustomer).isNull();
    }
}