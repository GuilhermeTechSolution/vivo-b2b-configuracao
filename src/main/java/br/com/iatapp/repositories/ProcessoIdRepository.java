package br.com.iatapp.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import br.com.iatapp.domain.ProcessoIdDomain;


public interface ProcessoIdRepository extends MongoRepository<ProcessoIdDomain, String> {

	ProcessoIdDomain findOneById(String id);
	
	List<ProcessoIdDomain> findByIdProcessoPrincipalAndResultadoProcedimento(String idProcessoPrincipal, String resultadoProcedimento);
	
	List<ProcessoIdDomain> findByIdProcessoPrincipalAndResultadoProcedimentoAndResultadoConfig(String idProcessoPrincipal, String resultadoProcedimento, String resultadoConfig);
	
	List<ProcessoIdDomain> findByConfManualAndDataEntradaFilaBetweenOrderByDataEntradaFilaDesc(boolean confManual, Date dataEntradaFilaGte, Date dataEntradaFilaLte);
	
	List<ProcessoIdDomain> findByDataEntradaFilaBetweenOrderByDataEntradaFilaDesc(Date dataEntradaFilaGte, Date dataEntradaFilaLte);
	
	ProcessoIdDomain findTopByIdVantive(String idVantive);

	ProcessoIdDomain findTopByIdVantiveOrderByDataInicioDesc(String idVantive);
	
	ProcessoIdDomain findTopByIdVantiveOrderByDataEntradaFilaDesc(String idVantive);
	
	ProcessoIdDomain findTopByIdVantiveAndResultadoProcedimento(String idVantive, String resultadoProcedimento);
	
	ProcessoIdDomain findTopByIdVantiveAndIdProcessoPrincipal(String idVantive, String idProcessoPrincipal);
	
	ProcessoIdDomain findTopByResultadoProcedimento(String resultadoProcedimento);
	
	ProcessoIdDomain findTopByResultadoProcedimentoAndProduto(String resultadoProcedimento, String produto);
	
	ProcessoIdDomain findTopByResultadoProcedimentoAndProdutoOrderByDataEntradaFilaAsc(String resultadoProcedimento, String produto);
	
	ProcessoIdDomain findTopByResultadoProcedimentoAndProdutoAndIdProcessoAnteriorExistsOrderByDataEntradaFilaAsc(String resultadoProcedimento, String produto, boolean exists);
	
	List<ProcessoIdDomain> findByResultadoProcedimento(String resultadoProcedimento);
	
	@Query("{ 'resultadoProcedimento' : '?0', 'idVantive' : { '$nin' : ['?1']}, 'dadosStarStr' : { '$regex' : '?2', '$options' : 'i'}}")
	List<ProcessoIdDomain> findAllCheckRaExecuting(String resultadoProcedimento, String idVantive, String subStr);
			
}
