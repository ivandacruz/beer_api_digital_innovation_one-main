package one.digitalInovation.beerstock.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import one.digitalInovation.beerstock.dto.BeerDTO;
import one.digitalInovation.beerstock.entity.Beer;
import one.digitalInovation.beerstock.exception.BeerAlreadyRegisteredException;
import one.digitalInovation.beerstock.exception.BeerNotFoundException;
import one.digitalInovation.beerstock.exception.BeerStockExceededException;
import one.digitalInovation.beerstock.mapper.BeerMapper;
import one.digitalInovation.beerstock.repository.BeerRepository;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class BeerService {

	private final BeerRepository beerRepository;
	private final BeerMapper beerMapper = BeerMapper.INSTANCE;
	
	public BeerDTO createBeer(BeerDTO beerDTO) throws BeerAlreadyRegisteredException {
		verifyIfIsAlreadyRegistered(beerDTO.getName());
		Beer beer = beerMapper.toModel(beerDTO);
		Beer savedBeer = beerRepository.save(beer);
		return beerMapper.toDTO(savedBeer);
		}
	
	public BeerDTO findByName(String name) throws BeerNotFoundException {
		Beer foundBeer = beerRepository.findByName(name)
				.orElseThrow(() -> new BeerNotFoundException(name));
		return beerMapper.toDTO(foundBeer);
	}
		
	public List<BeerDTO> ListAll(){
		return beerRepository.findAll()
				.stream()
				.map(beerMapper::toDTO)
				.collect(Collectors.toList());
	}
	
	public void deleteByID(Long id) throws BeerNotFoundException {
		verifyIfExists(id);
		beerRepository.deleteById(id);
	}
	
	private void verifyIfIsAlreadyRegistered(String name) throws BeerAlreadyRegisteredException {
		Optional<Beer> opSavedBeer = beerRepository.findByName(name);
		if (opSavedBeer.isPresent()) {
			throw new BeerAlreadyRegisteredException(name);
		}
	}
		private Beer verifyIfExists(Long id) throws BeerNotFoundException {
			return beerRepository.findById(id)
					.orElseThrow(() -> new BeerNotFoundException(null));
	}
		 public BeerDTO increment(Long id, int quantityToIncrement) throws BeerNotFoundException, BeerStockExceededException {
		        Beer beerToIncrementStock = verifyIfExists(id);
		        int quantityAfterIncrement = quantityToIncrement + beerToIncrementStock.getQuantity();
		        if (quantityAfterIncrement <= beerToIncrementStock.getMax()) {
		            beerToIncrementStock.setQuantity(beerToIncrementStock.getQuantity() + quantityToIncrement);
		            Beer incrementedBeerStock = beerRepository.save(beerToIncrementStock);
		            return beerMapper.toDTO(incrementedBeerStock);
		        }
		        throw new BeerStockExceededException(id, quantityToIncrement);
		 }
}
