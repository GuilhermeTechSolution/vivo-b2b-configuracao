package br.com.iatapp.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.iatapp.domain.Vivo2ScriptDomain;

public interface Vivo2ScriptRepository extends MongoRepository<Vivo2ScriptDomain, String> {

	Vivo2ScriptDomain findOneById(String id);
	
	List<Vivo2ScriptDomain> findByFabricanteAndServicosClienteIn(String fabricante, List<String> servicosCliente);
			
}
