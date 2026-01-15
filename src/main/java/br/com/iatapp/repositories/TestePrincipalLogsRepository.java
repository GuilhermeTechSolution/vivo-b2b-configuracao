package br.com.iatapp.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.iatapp.domain.TestePrincipalLogsDomain;


public interface TestePrincipalLogsRepository extends MongoRepository<TestePrincipalLogsDomain, String> {

	TestePrincipalLogsDomain findByIdTeste(int idTeste);	
	
}
