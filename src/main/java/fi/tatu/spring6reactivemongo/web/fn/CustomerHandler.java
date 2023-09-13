package fi.tatu.spring6reactivemongo.web.fn;

import fi.tatu.spring6reactivemongo.model.CustomerDTO;
import fi.tatu.spring6reactivemongo.services.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebInputException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerHandler {
    private final CustomerService customerService;
    private final Validator validator;

    private void validate(CustomerDTO customerDTO) {
        Errors errors = new BeanPropertyBindingResult(customerDTO, "customerDto");
        validator.validate(customerDTO, errors);

        if (errors.hasErrors()) {
            throw new ServerWebInputException(errors.toString());
        }
    }

    private void log(ServerRequest request) {
        log.info("Request: {}", request.toString());
    }

    public Mono<ServerResponse> listCustomers(ServerRequest request) {
        log(request);

        // Flux<CustomerDTO> flux;
        // ToDo: If query parameters are needed, filter here
        // if (request.queryParam("customerName").isPresent()) {
        //    flux = customerService.findByCustomerName(request.queryParam("customerName").get());
        // } else {
        //    flux = customerService.listCustomers();
        // }

        Flux<CustomerDTO> flux = customerService.listCustomers();

        return ServerResponse.ok()
                .body(flux, CustomerDTO.class);
    }

    public Mono<ServerResponse> getCustomerById(ServerRequest request) {
        log(request);
        return ServerResponse.ok()
                .body(customerService.getById(request.pathVariable("customerId"))
                                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))),
                        CustomerDTO.class);
    }

    public Mono<ServerResponse> createNewCustomer(ServerRequest request) {
        log(request);
        return customerService.saveCustomer(
                request.bodyToMono(CustomerDTO.class)
                        .doOnNext(this::validate)
        )
        .flatMap(customerDto -> ServerResponse
                .created(UriComponentsBuilder
                        .fromPath(BeerRouterConfig.BEER_PATH_ID)
                        .build(customerDto.getId()))
                .build());
    }

    public Mono<ServerResponse> updateCustomerById(ServerRequest request) {
        log(request);
        return request.bodyToMono(CustomerDTO.class)
                .doOnNext(this::validate)
                .flatMap(customerDto -> customerService.updateCustomer(request.pathVariable("customerId"), customerDto))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap(savedDto -> ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> patchCustomerById(ServerRequest request) {
        log(request);
        return request.bodyToMono(CustomerDTO.class)
                .doOnNext(this::validate)
                .flatMap(customerDto -> customerService.patchCustomer(request.pathVariable("customerId"), customerDto))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap(saveDto -> ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> deleteCustomerById(ServerRequest request) {
        log(request);
        return customerService.getById(request.pathVariable("customerId"))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap(customerDto -> customerService.deleteCustomerById(customerDto.getId()))
                .then(ServerResponse.noContent().build());
    }
}
