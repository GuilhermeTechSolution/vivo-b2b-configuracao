package br.com.iatapp.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.iatapp.domain.ConfigSwitchResultadosDomain;

public interface ConfigSwitchResultadosRepository extends MongoRepository<ConfigSwitchResultadosDomain, String> {

	ConfigSwitchResultadosDomain findOneById(String id);
	
	List<ConfigSwitchResultadosDomain> findByIdConfSwitch(String idConfSwitch);
	
}
