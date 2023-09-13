package fi.tatu.spring6reactivemongo.web.fn;

import fi.tatu.spring6reactivemongo.domain.Customer;
import fi.tatu.spring6reactivemongo.model.CustomerDTO;
import fi.tatu.spring6reactivemongo.services.CustomerServiceImplTest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.hamcrest.Matchers.greaterThan;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@AutoConfigureWebTestClient
public class CustomerEndpointTest {

    @Autowired
    WebTestClient webTestClient;

    public CustomerDTO getSavedTestCustomer(){
        FluxExchangeResult<CustomerDTO> customerDTOFluxExchangeResult = webTestClient.post().uri(CustomerRouterConfig.CUSTOMER_PATH)
                .body(Mono.just(CustomerServiceImplTest.getTestCustomer()), CustomerDTO.class)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                .returnResult(CustomerDTO.class);

        List<String> location = customerDTOFluxExchangeResult.getResponseHeaders().get(HttpHeaders.LOCATION);

        return webTestClient.get().uri(CustomerRouterConfig.CUSTOMER_PATH)
                .exchange().returnResult(CustomerDTO.class).getResponseBody().blockFirst();
    }


    @Test
    @Order(1)
    void testListCustomers() {
        webTestClient.get().uri(CustomerRouterConfig.CUSTOMER_PATH)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .expectBody().jsonPath("$.size()").value(greaterThan(1));
    }

    @Test
    @Order(2)
    void testGetCustomerByIdFound() {
        CustomerDTO testDto = getSavedTestCustomer();

        webTestClient.get().uri(CustomerRouterConfig.CUSTOMER_PATH_ID, testDto.getId())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .expectBody(CustomerDTO.class);
    }

    @Test
    @Order(3)
    void testGetCustomerByIdNotFound() {
        webTestClient.get().uri(CustomerRouterConfig.CUSTOMER_PATH_ID, 999)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Order(4)
    void testCreateCustomer() {
        CustomerDTO testDto = CustomerServiceImplTest.getTestCustomerDto();

        webTestClient.post()
                .uri(CustomerRouterConfig.CUSTOMER_PATH)
                .body(Mono.just(testDto), CustomerDTO.class)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists(HttpHeaders.LOCATION);
    }

    @Test
    @Order(5)
    void testCreateCustomerBadName() {
        Customer testCustomer = CustomerServiceImplTest.getTestCustomer();
        testCustomer.setCustomerName("");

        webTestClient.post()
                .uri(CustomerRouterConfig.CUSTOMER_PATH)
                .body(Mono.just(testCustomer), CustomerDTO.class)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @Order(6)
    void testUpdateCustomer() {
        CustomerDTO testDto = getSavedTestCustomer();
        testDto.setCustomerName("New");

        webTestClient.put()
                .uri(CustomerRouterConfig.CUSTOMER_PATH_ID, testDto.getId())
                .body(Mono.just(testDto), CustomerDTO.class)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    @Order(7)
    void testUpdateCustomerBadRequest() {
        CustomerDTO testDto = getSavedTestCustomer();
        testDto.setCustomerName("");

        webTestClient.put()
                .uri(CustomerRouterConfig.CUSTOMER_PATH_ID, testDto.getId())
                .body(Mono.just(testDto), CustomerDTO.class)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @Order(8)
    void testUpdateCustomerNotFound() {
        webTestClient.put()
                .uri(CustomerRouterConfig.CUSTOMER_PATH_ID, 999)
                .body(Mono.just(CustomerServiceImplTest.getTestCustomer()), CustomerDTO.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Order(9)
    void testDeleteCustomer() {
        CustomerDTO testDto = getSavedTestCustomer();

        webTestClient.delete()
                .uri(CustomerRouterConfig.CUSTOMER_PATH_ID, testDto.getId())
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    @Order(10)
    void testDeleteCustomerNotFound() {
        webTestClient.delete()
                .uri(CustomerRouterConfig.CUSTOMER_PATH_ID, 999)
                .exchange()
                .expectStatus().isNotFound();
    }


    @Test
    @Order(11)
    void testPatchCustomerFound() {
        CustomerDTO customerDTO = getSavedTestCustomer();

        webTestClient.patch()
                .uri(CustomerRouterConfig.CUSTOMER_PATH_ID, customerDTO.getId())
                .body(Mono.just(customerDTO), CustomerDTO.class)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    @Order(12)
    void testPatchCustomerNotFound() {
        webTestClient.patch()
                .uri(CustomerRouterConfig.CUSTOMER_PATH_ID, 999)
                .body(Mono.just(CustomerServiceImplTest.getTestCustomer()), CustomerDTO.class)
                .exchange()
                .expectStatus().isNotFound();
    }
}
