package br.com.iatapp.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import br.com.iatapp.domain.SipOneCoreProcessoIdDomain;


public interface SipOneCoreProcessoIdRepository extends MongoRepository<SipOneCoreProcessoIdDomain, String> {

	SipOneCoreProcessoIdDomain findOneById(String id);
	
	SipOneCoreProcessoIdDomain findOneByToken(String token);
	
	SipOneCoreProcessoIdDomain findTopByIdVantive(String idVantive);

	SipOneCoreProcessoIdDomain findTopByIdVantiveOrderByDataInicioDesc(String idVantive);
	
	SipOneCoreProcessoIdDomain findTopByIdVantiveOrderByDataEntradaFilaDesc(String idVantive);
	
	SipOneCoreProcessoIdDomain findTopByIdVantiveAndResultadoProcedimento(String idVantive, String resultadoProcedimento);
	
	SipOneCoreProcessoIdDomain findTopByResultadoProcedimento(String resultadoProcedimento);
	
	SipOneCoreProcessoIdDomain findTopByResultadoProcedimentoAndProduto(String resultadoProcedimento, String produto);
	
	SipOneCoreProcessoIdDomain findTopByResultadoProcedimentoAndProdutoOrderByDataEntradaFilaAsc(String resultadoProcedimento, String produto);
	
	SipOneCoreProcessoIdDomain findTopByResultadoProcedimentoAndProdutoAndIdProcessoAnteriorExistsOrderByDataEntradaFilaAsc(String resultadoProcedimento, String produto, boolean exists);
	
	List<SipOneCoreProcessoIdDomain> findByResultadoProcedimento(String resultadoProcedimento);
	
	@Query("{ 'resultadoProcedimento' : '?0', 'idVantive' : { '$nin' : ['?1']}, 'dadosStarStr' : { '$regex' : '?2', '$options' : 'i'}}")
	List<SipOneCoreProcessoIdDomain> findAllCheckRaExecuting(String resultadoProcedimento, String idVantive, String subStr);

	List<SipOneCoreProcessoIdDomain> findByDataInicioGreaterThanEqualAndDataFinalLessThanAndIdUsuarioNot(Date dataInicio, Date dataFinal, int idUsuario);
	
}
