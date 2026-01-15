package br.com.iatapp.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.iatapp.domain.TestePrincipalDomain;


public interface TestePrincipalRepository extends MongoRepository<TestePrincipalDomain, String> {

	TestePrincipalDomain findByIdTeste(int idTeste);
	
	List<TestePrincipalDomain> findByIdVantiveAndIdUsuarioGreaterThanAndTimestampFimTesteGreaterThanOrderByIdTesteDesc(String idVantive, int idUsuario, long timestampFimTeste);
	
	TestePrincipalDomain findTopByIdVantiveOrderByIdTesteDesc(String idVantive);
	
	TestePrincipalDomain findTopByIdVantiveAndIdModuloNotOrderByIdTesteDesc(String idVantive, int idModulo);

	TestePrincipalDomain findTopByClienteAndVrfCadastroAndIdCertificadoOrderByIdTesteDesc(String cliente, String vrfCadastro, boolean idCertificado);
	
	TestePrincipalDomain findTopByClienteAndIdCertificadoAndVrfClienteNotOrderByIdTesteDesc(String cliente, boolean idCertificado, String vrfCliente);
	
	TestePrincipalDomain findTopByIdVantiveAndConfigVoipOrderByIdTesteDesc(String idVantive, boolean configVoip);
	
}
