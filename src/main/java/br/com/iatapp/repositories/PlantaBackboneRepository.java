package br.com.iatapp.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import br.com.iatapp.domain.PlantaBackboneDomain;


public interface PlantaBackboneRepository extends MongoRepository<PlantaBackboneDomain, Object> {

	PlantaBackboneDomain findTopByIdVantive(String idVantive);

}
