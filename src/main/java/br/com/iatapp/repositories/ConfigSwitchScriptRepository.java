package br.com.iatapp.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.iatapp.domain.ConfigSwitchScriptDomain;

public interface ConfigSwitchScriptRepository extends MongoRepository<ConfigSwitchScriptDomain, String> {

	ConfigSwitchScriptDomain findOneById(String id);
	
	List<ConfigSwitchScriptDomain> findByFabricanteAndServicosClienteIn(String fabricante, List<String> servicosCliente);
			
}
