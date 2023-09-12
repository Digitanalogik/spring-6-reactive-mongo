package fi.tatu.spring6reactivemongo.web.fn;

import fi.tatu.spring6reactivemongo.model.BeerDTO;
import fi.tatu.spring6reactivemongo.services.BeerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.util.UriComponentsBuilder;
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

    public Mono<ServerResponse> getBeerById(ServerRequest request) {
        log.info("Request: {}", request.toString());
        return ServerResponse.ok()
                .body(beerService.getById(request.pathVariable("beerId")), BeerDTO.class);
    }

    public Mono<ServerResponse> createNewBeer(ServerRequest request) {
        log.info("Request: {}", request.toString());
        return beerService.saveBeer(request.bodyToMono(BeerDTO.class))
                .flatMap(beerDto -> ServerResponse
                        .created(UriComponentsBuilder
                                .fromPath(BeerRouterConfig.BEER_PATH_ID)
                                .build(beerDto.getId()))
                .build());
    }
}
