package fi.tatu.spring6reactivemongo.web.fn;

import fi.tatu.spring6reactivemongo.model.BeerDTO;
import fi.tatu.spring6reactivemongo.services.BeerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class BeerHandler {
    private final BeerService beerService;

    public Mono<ServerResponse> listBeers(ServerRequest request) {
        log.info("Request: {}", request.toString());
        return ServerResponse.ok()
                .body(beerService.listBeers(), BeerDTO.class);
    }
}
