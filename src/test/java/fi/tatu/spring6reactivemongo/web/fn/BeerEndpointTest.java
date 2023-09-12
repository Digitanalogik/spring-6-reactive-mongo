package fi.tatu.spring6reactivemongo.web.fn;

import fi.tatu.spring6reactivemongo.domain.Beer;
import fi.tatu.spring6reactivemongo.model.BeerDTO;
import fi.tatu.spring6reactivemongo.services.BeerServiceImplTest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@AutoConfigureWebTestClient
public class BeerEndpointTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    @Order(1)
    void testListBeers() {
        webTestClient.get().uri(BeerRouterConfig.BEER_PATH)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .expectBody().jsonPath("$.size()", hasSize(greaterThan(1)));
    }

    @Test
    @Order(2)
    void testGetBeerById() {
        webTestClient.get().uri(BeerRouterConfig.BEER_PATH_ID, 1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .expectBody(BeerDTO.class);
    }

    @Test
    @Order(3)
    void testGetBeerByIdNotFound() {
        webTestClient.get().uri(BeerRouterConfig.BEER_PATH_ID, 999)
                .exchange()
                .expectStatus().isNotFound();
    }


    @Test
    @Order(4)
    void testCreateBeer() {
        final String NEW_BEER_URL = "http://localhost:8080/api/v3/beer/4";

        webTestClient.post()
                .uri(BeerRouterConfig.BEER_PATH)
                .body(Mono.just(BeerServiceImplTest.getTestBeer()), BeerDTO.class)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().location(NEW_BEER_URL);
    }

    @Test
    @Order(5)
    void testCreateBeerBadName() {
        Beer testBeer = BeerServiceImplTest.getTestBeer();
        testBeer.setBeerName("");

        webTestClient.post()
                .uri(BeerRouterConfig.BEER_PATH)
                .body(Mono.just(testBeer), BeerDTO.class)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @Order(6)
    void testUpdateBeer() {
        webTestClient.put()
                .uri(BeerRouterConfig.BEER_PATH_ID, 1)
                .body(Mono.just(BeerServiceImplTest.getTestBeer()), BeerDTO.class)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    @Order(7)
    void testUpdateBeerBadRequest() {
        Beer testBeer = BeerServiceImplTest.getTestBeer();
        testBeer.setBeerStyle("");

        webTestClient.put()
                .uri(BeerRouterConfig.BEER_PATH_ID, 1)
                .body(Mono.just(testBeer), BeerDTO.class)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @Order(8)
    void testUpdateBeerNotFound() {
        webTestClient.put()
                .uri(BeerRouterConfig.BEER_PATH_ID, 999)
                .body(Mono.just(BeerServiceImplTest.getTestBeer()), BeerDTO.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Order(9)
    void testDeleteBeer() {
        webTestClient.delete()
                .uri(BeerRouterConfig.BEER_PATH_ID, 1)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    @Order(10)
    void testDeleteBeerNotFound() {
        webTestClient.delete()
                .uri(BeerRouterConfig.BEER_PATH_ID, 999)
                .exchange()
                .expectStatus().isNotFound();
    }


    @Test
    @Order(10)
    void testPatchBeer() {

        webTestClient.patch()
                .uri(BeerRouterConfig.BEER_PATH_ID, 2)
                .body(Mono.just(BeerServiceImplTest.getTestBeer()), BeerDTO.class)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @Order(11)
    void testPatchBeerNotFound() {

        webTestClient.patch()
                .uri(BeerRouterConfig.BEER_PATH_ID, 999)
                .body(Mono.just(BeerServiceImplTest.getTestBeer()), BeerDTO.class)
                .exchange()
                .expectStatus().isNotFound();
    }


}
