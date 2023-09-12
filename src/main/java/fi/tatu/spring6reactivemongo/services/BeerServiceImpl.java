package fi.tatu.spring6reactivemongo.services;

import fi.tatu.spring6reactivemongo.mappers.BeerMapper;
import fi.tatu.spring6reactivemongo.model.BeerDTO;
import fi.tatu.spring6reactivemongo.repositories.BeerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class BeerServiceImpl implements BeerService {

    private final BeerMapper beerMapper;
    private final BeerRepository beerRepository;

    @Override
    public Mono<BeerDTO> saveBeer(Mono<BeerDTO> beerDTO) {
        return beerDTO.map(beerMapper::beerDtoToBeer)
                .flatMap(beerRepository::save)
                .map(beerMapper::beerToBeerDTO);
    }

    @Override
    public Mono<BeerDTO> getById(String id) {
        return null;
    }
}
