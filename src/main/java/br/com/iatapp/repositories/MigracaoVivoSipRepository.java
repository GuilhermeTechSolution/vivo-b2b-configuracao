package br.com.iatapp.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.iatapp.domain.MigracaoVivoSipDomain;


public interface MigracaoVivoSipRepository extends MongoRepository<MigracaoVivoSipDomain, String> {

	MigracaoVivoSipDomain findOneById(String id);
	
	List<MigracaoVivoSipDomain> findByConfManualAndDataEntradaFilaBetweenOrderByDataEntradaFilaDesc(boolean confManual, Date dataEntradaFilaGte, Date dataEntradaFilaLte);
	
	MigracaoVivoSipDomain findTopByIdVantive(String idVantive);

	MigracaoVivoSipDomain findTopByIdVantiveOrderByDataInicioDesc(String idVantive);
	
	MigracaoVivoSipDomain findTopByIdVantiveOrderByDataEntradaFilaDesc(String idVantive);
	
	MigracaoVivoSipDomain findTopByIdVantiveAndResultadoProcedimento(String idVantive, String resultadoProcedimento);
	
	MigracaoVivoSipDomain findTopByResultadoProcedimento(String resultadoProcedimento);
	
	List<MigracaoVivoSipDomain> findByResultadoProcedimento(String resultadoProcedimento);
	
	List<MigracaoVivoSipDomain> findByOrderByDataEntradaFilaDesc();
			
}
