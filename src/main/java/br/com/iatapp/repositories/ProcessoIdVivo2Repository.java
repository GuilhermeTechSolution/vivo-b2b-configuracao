package br.com.iatapp.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.iatapp.domain.ProcessoIdVivo2Domain;


public interface ProcessoIdVivo2Repository extends MongoRepository<ProcessoIdVivo2Domain, String> {

	ProcessoIdVivo2Domain findOneById(String id);
	
	ProcessoIdVivo2Domain findTopByIdTbsOrderByDataEntradaFilaDesc(String idTbs);
	
	ProcessoIdVivo2Domain findTopByIdTbsAndFilaOrderByDataEntradaFilaDesc(String idTbs, String fila);

	List<ProcessoIdVivo2Domain> findByIdTbs(String idTbs);
	
	ProcessoIdVivo2Domain findTopByResultadoProcedimentoAndFilaOrderByDataEntradaFilaAsc(String resultadoProcedimento, String fila);
	
	List<ProcessoIdVivo2Domain> findByConfManualAndDataEntradaFilaBetweenOrderByDataEntradaFilaDesc(boolean confManual, Date dataEntradaFilaGte, Date dataEntradaFilaLte);

	List<ProcessoIdVivo2Domain> findByDataEntradaFilaBetweenOrderByDataEntradaFilaDesc(Date dataEntradaFilaGte, Date dataEntradaFilaLte);
	
}