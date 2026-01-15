package br.com.iatapp.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.iatapp.domain.ConfigSwitchIdDomain;

public interface ConfigSwitchIdRepository extends MongoRepository<ConfigSwitchIdDomain, String> {

	ConfigSwitchIdDomain findOneById(String id);
			
}
