package br.com.iatapp.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.iatapp.domain.ConfigSwitchLogDomain;

public interface ConfigSwitchLogRepository extends MongoRepository<ConfigSwitchLogDomain, String> {

	ConfigSwitchLogDomain findOneById(String id);
	
	ConfigSwitchLogDomain findOneByIdConfSwitch(String idConfSwitch);
			
}
