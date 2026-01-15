package br.com.iatapp.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.iatapp.domain.TestePrincipalResultadosLogsDomain;


public interface TestePrincipalResultadosLogsRepository extends MongoRepository<TestePrincipalResultadosLogsDomain, String> {

	List<TestePrincipalResultadosLogsDomain> findByIdTeste(int idTeste);

	TestePrincipalResultadosLogsDomain findByIdTesteAndIdTipoAndIdItem(int idTeste, int idTipo, int idItem);
	
}
