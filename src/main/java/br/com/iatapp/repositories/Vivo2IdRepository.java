package br.com.iatapp.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.iatapp.domain.Vivo2IdDomain;

public interface Vivo2IdRepository extends MongoRepository<Vivo2IdDomain, String> {

	Vivo2IdDomain findOneById(String id);
	
	Vivo2IdDomain findTopByIdTbsOrderByDataInicioDesc(String idTbs);
	
	Vivo2IdDomain findTopByIdTbsAndFilaOrderByDataInicioDesc(String idTbs, String fila);

			
}
