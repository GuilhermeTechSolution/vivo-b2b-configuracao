package br.com.iatapp.repositories;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import br.com.iatapp.domain.ProcessoIdVivo2Domain;

@Repository
public class RelatorioConfiguracaoVivo2RepositoryMTImpl implements RelatorioConfiguracaoVivo2RepositoryMT {

	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public List<ProcessoIdVivo2Domain> buscaAnaliticoQip(Date dataInicio, Date dataFinal, String servico) {
		
		List<String> fila = new ArrayList<>();
		fila.add("QIP");
		fila.add("SIP");
		
		// match
		MatchOperation matchOperation = null;		
		if(StringUtils.isBlank(servico)) {			
			matchOperation = Aggregation.match(
					Criteria.where("dataInicio").gte(dataInicio)
						.andOperator(
								Criteria.where("dataInicio").lte(dataFinal),
								Criteria.where("fila").in(fila))
	        );
		} else {
			matchOperation = Aggregation.match(
					Criteria.where("dataInicio").gte(dataInicio)
						.andOperator(
								Criteria.where("dataInicio").lte(dataFinal),
								Criteria.where("servico").is(servico),
								Criteria.where("fila").in(fila))
	        );
		}
		
		// group
		GroupOperation groupIdTbs = Aggregation.group("idTbs")
				.last("servico").as("servico")
				.last("idTbs").as("idTbs")
				.last("dataEntradaFila").as("dataEntradaFila")
				.last("dataInicio").as("dataInicio")
				.last("dataFinal").as("dataFinal")
				.last("resultadoProcedimento").as("resultadoProcedimento")
				.last("cliente").as("cliente")
				.last("fila").as("fila")
				.last("resultadoAlocarBlocoIpv4Lan").as("resultadoAlocarBlocoIpv4Lan")
				.last("resultadoAlocarBlocoIpv4Wan").as("resultadoAlocarBlocoIpv4Wan")
				.last("resultadoAlocarBlocoIpv6Wan").as("resultadoAlocarBlocoIpv6Wan")
				.last("resultadoAlocarBlocoIpv6Lan").as("resultadoAlocarBlocoIpv6Lan")
				.last("resultadoAlocarSubnetIpv6Wan").as("resultadoAlocarSubnetIpv6Wan")
				.last("resultadoAlocarSubnetIpv6Lan").as("resultadoAlocarSubnetIpv6Lan")
				.last("ipv4WanBlock").as("ipv4WanBlock")
				.last("ipv4LanBlock").as("ipv4LanBlock")
				.last("ipv6WanBlock").as("ipv6WanBlock")
				.last("ipv6LanBlock").as("ipv6LanBlock")
				.last("ipv6WanSubnet").as("ipv6WanSubnet")
				.last("ipv6LanSubnet").as("ipv6LanSubnet");
		
		List<String> listaStatusNin = new ArrayList<>();
		listaStatusNin.add("aguardando_execucao");
		listaStatusNin.add("em_execucao");
		listaStatusNin.add("tarefa_em_andamento");
		listaStatusNin.add("error_buscar_dados_tbs");
		
		// removendo a lista de tarefas
		MatchOperation matchResultados = Aggregation.match(
				Criteria.where("resultadoProcedimento").nin(listaStatusNin)
	    );
		
		//Convert the aggregation result into a List
		AggregationResults<ProcessoIdVivo2Domain> groupResults = mongoTemplate.aggregate(
					Aggregation.newAggregation(matchOperation, groupIdTbs, matchResultados), 
					"vivo2_processos_robo_ids", 
					ProcessoIdVivo2Domain.class);
		
		List<ProcessoIdVivo2Domain> result = groupResults.getMappedResults();
		
		return result;
	}

	@Override
	public List<ProcessoIdVivo2Domain> buscaAnaliticoConfiguracao(Date dataInicio, Date dataFinal, String servico) {
		
		List<String> fila = new ArrayList<>();
		fila.add("QIP");
		fila.add("SIP");
		
		// match
		MatchOperation matchOperation = null;		
		if(StringUtils.isBlank(servico)) {			
			matchOperation = Aggregation.match(
					Criteria.where("dataInicio").gte(dataInicio)
						.andOperator(
								Criteria.where("dataInicio").lte(dataFinal),
								Criteria.where("fila").nin(fila))
	        );
		} else {
			matchOperation = Aggregation.match(
					Criteria.where("dataInicio").gte(dataInicio)
						.andOperator(
								Criteria.where("dataInicio").lte(dataFinal),
								Criteria.where("servico").is(servico),
								Criteria.where("fila").nin(fila))
	        );
		}
		
		// group
		GroupOperation groupIdTbs = Aggregation.group("idTbs")
				.last("servico").as("servico")
				.last("idTbs").as("idTbs")
				.last("dataEntradaFila").as("dataEntradaFila")
				.last("dataInicio").as("dataInicio")
				.last("dataFinal").as("dataFinal")
				.last("resultadoProcedimento").as("resultadoProcedimento")
				.last("cliente").as("cliente")
				.last("fila").as("fila")
				.last("motivo").as("motivo");
		
		List<String> listaStatusNin = new ArrayList<>();
		listaStatusNin.add("aguardando_execucao");
		listaStatusNin.add("em_execucao");
		listaStatusNin.add("tarefa_em_andamento");
		listaStatusNin.add("error_buscar_dados_tbs");
		
		// removendo a lista de tarefas
		MatchOperation matchResultados = Aggregation.match(
				Criteria.where("resultadoProcedimento").nin(listaStatusNin)
	    );
		
		//Convert the aggregation result into a List
		AggregationResults<ProcessoIdVivo2Domain> groupResults = mongoTemplate.aggregate(
					Aggregation.newAggregation(matchOperation, groupIdTbs, matchResultados), 
					"vivo2_processos_robo_ids", 
					ProcessoIdVivo2Domain.class);
		
		List<ProcessoIdVivo2Domain> result = groupResults.getMappedResults();
		
		return result;
	}

}
