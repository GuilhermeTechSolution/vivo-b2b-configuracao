package br.com.iatapp.repositories;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import br.com.iatapp.domain.ProcessoIdRetiradaDomain;

@Repository
public class RelatorioRetiradaRepositoryMTImpl implements RelatorioRetiradaRepositoryMT {

	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public List<ProcessoIdRetiradaDomain> buscaAnaliticoRetirada(Date dataInicio, Date dataFinal) {
		
		List<String> listaTarefas = new ArrayList<>();
		listaTarefas.add("Shutdown");
		listaTarefas.add("Desbloquear Acesso Op Rede");
		listaTarefas.add("Bloquear Acesso Op Rede");
		
		// match
		MatchOperation matchOperation = null;		
		matchOperation = Aggregation.match(
				Criteria.where("dataInicio").gte(dataInicio)
					.andOperator(
							Criteria.where("dataInicio").lte(dataFinal),
							Criteria.where("nomeTarefaStar").in(listaTarefas))
        );
		
		// group
		GroupOperation groupIdVantive = Aggregation.group("idVantive")
				.last("produto").as("produto")
				.last("idVantive").as("idVantive")
				.last("dataInicio").as("dataInicio")
				.last("dataFinal").as("dataFinal")
				.last("resultadoProcedimento").as("resultadoProcedimento")
				.last("resultadosFinalizarTarefasStar").as("resultadosFinalizarTarefasStar")
				.last("dadosStarStr").as("dadosStarStr")
				.last("nomeTarefaStar").as("nomeTarefaStar");
		
		List<String> listaStatusNin = new ArrayList<>();
		listaStatusNin.add("tecnologia_nao_suportada_no_momento");
		listaStatusNin.add("tipo_venda_nao_suportado_no_momento");
		listaStatusNin.add("aguardando_execucao");
		listaStatusNin.add("em_execucao");
		listaStatusNin.add("produto_nao_suportado_no_momento");
		
		MatchOperation matchStatus = Aggregation.match(
				Criteria.where("resultadoProcedimento").nin(listaStatusNin)
	    );
		
		//Convert the aggregation result into a List
		AggregationResults<ProcessoIdRetiradaDomain> groupResults = mongoTemplate.aggregate(
					Aggregation.newAggregation(matchOperation, groupIdVantive, matchStatus), 
					"retirada_processos_ids", 
					ProcessoIdRetiradaDomain.class);
		
		List<ProcessoIdRetiradaDomain> result = groupResults.getMappedResults();
		
		return result;
	}
	
}
