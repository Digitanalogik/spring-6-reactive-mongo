package fi.tatu.spring6reactivemongo.mappers;

import fi.tatu.spring6reactivemongo.domain.Beer;
import fi.tatu.spring6reactivemongo.model.BeerDTO;
import org.mapstruct.Mapper;

@Mapper
public interface BeerMapper {
    Beer beerDtoToBeer(BeerDTO beerDTO);
    BeerDTO beerToBeerDTO(Beer beer);
}

