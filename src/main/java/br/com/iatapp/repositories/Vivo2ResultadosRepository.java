package br.com.iatapp.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.iatapp.domain.Vivo2ResultadosDomain;

public interface Vivo2ResultadosRepository extends MongoRepository<Vivo2ResultadosDomain, String> {

	Vivo2ResultadosDomain findOneById(String id);
	
	List<Vivo2ResultadosDomain> findByIdVivo2(String idVivo2);
	
}
