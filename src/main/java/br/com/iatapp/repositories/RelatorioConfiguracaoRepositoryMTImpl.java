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

import br.com.iatapp.domain.ProcessoIdDomain;

@Repository
public class RelatorioConfiguracaoRepositoryMTImpl implements RelatorioConfiguracaoRepositoryMT {

	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
public List<ProcessoIdDomain> buscaResumoConfiguracao(Date dataInicio, Date dataFinal, String produto) {
		
		// match
		MatchOperation matchOperation = null;
		if(StringUtils.isBlank(produto)) {
			matchOperation = Aggregation.match(
					Criteria.where("dataInicio").gte(dataInicio)
						.andOperator(Criteria.where("dataInicio").lte(dataFinal))
	        );
		} else {
			matchOperation = Aggregation.match(
					Criteria.where("dataInicio").gte(dataInicio)
						.andOperator(
								Criteria.where("dataInicio").lte(dataFinal),
								Criteria.where("produto").is(produto))
	        );
		}
		
		// group
		GroupOperation groupIdVantive = Aggregation.group("idVantive")
				.last("produto").as("produto")
				.last("idVantive").as("idVantive")
				.last("dataEntradaFila").as("dataEntradaFila")
				.last("dataInicio").as("dataInicio")
				.last("dataFinal").as("dataFinal")
				.last("idProcessoPrincipal").as("idProcessoPrincipal")
				.last("resultadoProcedimento").as("resultadoProcedimento")
				.last("dadosStarStr").as("dadosStarStr")
				.last("resultadosCheckDuplicidade").as("resultadosCheckDuplicidade")
				.last("resultadosCheckConfiguracao").as("resultadosCheckConfiguracao")
				.last("resultadoConfig").as("resultadoConfig")
				.last("resultadosFinalizarTarefasStar").as("resultadosFinalizarTarefasStar")
				.last("resultadosFinalizarTarefasSae").as("resultadosFinalizarTarefasSae")
				.last("alertaConfig").as("alertaConfig")
				.last("movimentouTarefaPendenciaSae").as("movimentouTarefaPendenciaSae");
		
		List<String> listaStatusNin = new ArrayList<>();
		listaStatusNin.add("tecnologia_nao_suportada_no_momento");
		listaStatusNin.add("tipo_venda_nao_suportado_no_momento");
		listaStatusNin.add("aguardando_execucao");
		listaStatusNin.add("em_execucao");
		listaStatusNin.add("movimentou_caixa_manual_por_qtd_tentativas");
		listaStatusNin.add("produto_nao_suportado_no_momento");
//		listaStatusNin.add("error_aprovisionamento_dslam_atm");
//		listaStatusNin.add("error_chipset_atm_dslam");
		
		// removendo a lista de tarefas
		MatchOperation matchTarefas = Aggregation.match(
				Criteria.where("resultadoProcedimento").nin(listaStatusNin)
	    );
		
		
//		// removendo do backlog os ids de movimentacao de tarefas
//		MatchOperation matchTarefaBacklog = Aggregation.match(
//					Criteria.where("movimentouTarefaPendenciaSae").is(null)
//	    );
//		
//		// group
//		GroupOperation groupResultadoProcedimento = Aggregation.group("resultadoProcedimento")
//				.count().as("qtd")
//				.last("resultadoProcedimento").as("resultadoProcedimento");
//		
//		//Convert the aggregation result into a List
//		AggregationResults<ProcessoIdDomain> groupResults = mongoTemplate.aggregate(
//					Aggregation.newAggregation(matchOperation, groupIdVantive, matchTarefaBacklog, groupResultadoProcedimento), 
//					"configuracao_processos_ids", 
//					ProcessoIdDomain.class);
		
		// group
		GroupOperation groupResultadoProcedimento = Aggregation.group("resultadoProcedimento")
				.count().as("qtd")
				.last("resultadoProcedimento").as("resultadoProcedimento");
		
		//Convert the aggregation result into a List
		AggregationResults<ProcessoIdDomain> groupResults = mongoTemplate.aggregate(
					Aggregation.newAggregation(matchOperation, groupIdVantive, matchTarefas, groupResultadoProcedimento), 
					"configuracao_processos_ids", 
					ProcessoIdDomain.class);
		
		List<ProcessoIdDomain> result = groupResults.getMappedResults();
		
		// pegando o total
		List<ProcessoIdDomain> listaResult = new ArrayList<>();
		long qtdTotal = 0;
		for(ProcessoIdDomain processoIdDomain : result) {
			listaResult.add(processoIdDomain);
			qtdTotal += processoIdDomain.getQtd();
		}
		ProcessoIdDomain processoIdDomain = new ProcessoIdDomain();
		processoIdDomain.setResultadoProcedimento("TOTAL");
		processoIdDomain.setQtd(qtdTotal);		
		listaResult.add(listaResult.size(), processoIdDomain);
		
		return listaResult;
	}
//	public List<ProcessoIdDomain> buscaResumoConfiguracao(Date dataInicio, Date dataFinal, String produto) {
//		
//		List<String> listaStatusNin = new ArrayList<>();
//		listaStatusNin.add("tecnologia_nao_suportada_no_momento");
//		listaStatusNin.add("tipo_venda_nao_suportado_no_momento");
//		listaStatusNin.add("aguardando_execucao");
//		listaStatusNin.add("em_execucao");
//		listaStatusNin.add("movimentou_caixa_manual_por_qtd_tentativas");
//		//listaStatusNin.add("error_aprovisionamento_dslam_atm");
//		//listaStatusNin.add("error_chipset_atm_dslam");
//		//listaStatusNin.add("produto_nao_suportado_no_momento");
//		
//		
//		// match
//		MatchOperation matchOperation = null;
//		if(StringUtils.isBlank(produto)) {
//			matchOperation = Aggregation.match(
//					Criteria.where("resultadoProcedimento").nin(listaStatusNin)
//						.andOperator(
//								Criteria.where("dataInicio").gte(dataInicio),
//								Criteria.where("dataInicio").lte(dataFinal))
//	        );
//		} else {
//			matchOperation = Aggregation.match(
//					Criteria.where("resultadoProcedimento").nin(listaStatusNin)
//						.andOperator(
//								Criteria.where("dataInicio").gte(dataInicio),
//								Criteria.where("dataInicio").lte(dataFinal),
//								Criteria.where("produto").is(produto))
//	        );
//		}
//		
//		// group
//		GroupOperation groupIdVantive = Aggregation.group("idVantive")
//				.last("produto").as("produto")
//				.last("idVantive").as("idVantive")
//				.last("dataEntradaFila").as("dataEntradaFila")
//				.last("dataInicio").as("dataInicio")
//				.last("dataFinal").as("dataFinal")
//				.last("idProcessoPrincipal").as("idProcessoPrincipal")
//				.last("resultadoProcedimento").as("resultadoProcedimento")
//				.last("dadosStarStr").as("dadosStarStr")
//				.last("resultadosCheckDuplicidade").as("resultadosCheckDuplicidade")
//				.last("resultadosCheckConfiguracao").as("resultadosCheckConfiguracao")
//				.last("resultadoConfig").as("resultadoConfig")
//				.last("resultadosFinalizarTarefasStar").as("resultadosFinalizarTarefasStar")
//				.last("resultadosFinalizarTarefasSae").as("resultadosFinalizarTarefasSae")
//				.last("alertaConfig").as("alertaConfig")
//				.last("movimentouTarefaPendenciaSae").as("movimentouTarefaPendenciaSae");
//		
//		
////		// removendo do backlog os ids de movimentacao de tarefas
////		MatchOperation matchTarefaBacklog = Aggregation.match(
////					Criteria.where("movimentouTarefaPendenciaSae").is(null)
////	    );
////		
////		// group
////		GroupOperation groupResultadoProcedimento = Aggregation.group("resultadoProcedimento")
////				.count().as("qtd")
////				.last("resultadoProcedimento").as("resultadoProcedimento");
////		
////		//Convert the aggregation result into a List
////		AggregationResults<ProcessoIdDomain> groupResults = mongoTemplate.aggregate(
////					Aggregation.newAggregation(matchOperation, groupIdVantive, matchTarefaBacklog, groupResultadoProcedimento), 
////					"configuracao_processos_ids", 
////					ProcessoIdDomain.class);
//		
//		// group
//		GroupOperation groupResultadoProcedimento = Aggregation.group("resultadoProcedimento")
//				.count().as("qtd")
//				.last("resultadoProcedimento").as("resultadoProcedimento");
//		
//		//Convert the aggregation result into a List
//		AggregationResults<ProcessoIdDomain> groupResults = mongoTemplate.aggregate(
//					Aggregation.newAggregation(matchOperation, groupIdVantive, groupResultadoProcedimento), 
//					"configuracao_processos_ids", 
//					ProcessoIdDomain.class);
//		
//		List<ProcessoIdDomain> result = groupResults.getMappedResults();
//		
//		// pegando o total
//		List<ProcessoIdDomain> listaResult = new ArrayList<>();
//		long qtdTotal = 0;
//		for(ProcessoIdDomain processoIdDomain : result) {
//			listaResult.add(processoIdDomain);
//			qtdTotal += processoIdDomain.getQtd();
//		}
//		ProcessoIdDomain processoIdDomain = new ProcessoIdDomain();
//		processoIdDomain.setResultadoProcedimento("TOTAL");
//		processoIdDomain.setQtd(qtdTotal);		
//		listaResult.add(listaResult.size(), processoIdDomain);
//		
//		return listaResult;
//	}

	@Override
	public List<ProcessoIdDomain> buscaAnaliticoConfiguracao(Date dataInicio, Date dataFinal, String produto) {
		
		// match
		MatchOperation matchOperation = null;		
		if(StringUtils.isBlank(produto)) {			
			matchOperation = Aggregation.match(
					Criteria.where("dataInicio").gte(dataInicio)
						.andOperator(
								Criteria.where("dataInicio").lte(dataFinal))
	        );
		} else {
			matchOperation = Aggregation.match(
					Criteria.where("dataInicio").gte(dataInicio)
						.andOperator(
								Criteria.where("dataInicio").lte(dataFinal),
								Criteria.where("produto").is(produto))
	        );
		}
		
		// group
		GroupOperation groupIdVantive = Aggregation.group("idVantive")
				.last("produto").as("produto")
				.last("idVantive").as("idVantive")
				.last("dataEntradaFila").as("dataEntradaFila")
				.last("dataInicio").as("dataInicio")
				.last("dataFinal").as("dataFinal")
				.last("idProcessoPrincipal").as("idProcessoPrincipal")
				.last("resultadoProcedimento").as("resultadoProcedimento")
				.last("dadosStarStr").as("dadosStarStr")
				.last("resultadosCheckDuplicidade").as("resultadosCheckDuplicidade")
				.last("resultadosCheckConfiguracao").as("resultadosCheckConfiguracao")
				.last("resultadoConfig").as("resultadoConfig")
				.last("resultadosFinalizarTarefasStar").as("resultadosFinalizarTarefasStar")
				.last("resultadosFinalizarTarefasSae").as("resultadosFinalizarTarefasSae")
				.last("movimentouTarefaPendenciaSae").as("movimentouTarefaPendenciaSae");
		
		List<String> listaStatusNin = new ArrayList<>();
		listaStatusNin.add("tecnologia_nao_suportada_no_momento");
		listaStatusNin.add("tipo_venda_nao_suportado_no_momento");
		listaStatusNin.add("aguardando_execucao");
		listaStatusNin.add("em_execucao");
		listaStatusNin.add("movimentou_caixa_manual_por_qtd_tentativas");
		listaStatusNin.add("produto_nao_suportado_no_momento");
//		listaStatusNin.add("error_aprovisionamento_dslam_atm");
//		listaStatusNin.add("error_chipset_atm_dslam");
		
		// removendo a lista de tarefas
		MatchOperation matchTarefas = Aggregation.match(
				Criteria.where("resultadoProcedimento").nin(listaStatusNin)
	    );
		
//		// removendo do backlog os ids de movimentacao de tarefas
//		MatchOperation matchTarefaBacklog = Aggregation.match(
//					Criteria.where("movimentouTarefaPendenciaSae").is(null)
//	    );
//				
//		//Convert the aggregation result into a List
//		AggregationResults<ProcessoIdDomain> groupResults = mongoTemplate.aggregate(
//					Aggregation.newAggregation(matchOperation, groupIdVantive, matchTarefaBacklog), 
//					"configuracao_processos_ids", 
//					ProcessoIdDomain.class);
		
		//Convert the aggregation result into a List
		AggregationResults<ProcessoIdDomain> groupResults = mongoTemplate.aggregate(
					Aggregation.newAggregation(matchOperation, groupIdVantive, matchTarefas), 
					"configuracao_processos_ids", 
					ProcessoIdDomain.class);
		
		List<ProcessoIdDomain> result = groupResults.getMappedResults();
		
		return result;
	}
//	public List<ProcessoIdDomain> buscaAnaliticoConfiguracao(Date dataInicio, Date dataFinal, String produto) {
//		
//		List<String> listaStatusNin = new ArrayList<>();
//		listaStatusNin.add("tecnologia_nao_suportada_no_momento");
//		listaStatusNin.add("tipo_venda_nao_suportado_no_momento");
//		listaStatusNin.add("aguardando_execucao");
//		listaStatusNin.add("em_execucao");
//		listaStatusNin.add("movimentou_caixa_manual_por_qtd_tentativas");
//		//listaStatusNin.add("error_aprovisionamento_dslam_atm");
//		//listaStatusNin.add("error_chipset_atm_dslam");
//		//listaStatusNin.add("produto_nao_suportado_no_momento");
//		
//		// match
//		MatchOperation matchOperation = null;		
//		if(StringUtils.isBlank(produto)) {			
//			matchOperation = Aggregation.match(
//					Criteria.where("resultadoProcedimento").nin(listaStatusNin)
//						.andOperator(
//								Criteria.where("dataInicio").gte(dataInicio),
//								Criteria.where("dataInicio").lte(dataFinal))
//	        );
//		} else {
//			matchOperation = Aggregation.match(
//					Criteria.where("resultadoProcedimento").nin(listaStatusNin)
//						.andOperator(
//								Criteria.where("dataInicio").gte(dataInicio),
//								Criteria.where("dataInicio").lte(dataFinal),
//								Criteria.where("produto").is(produto))
//	        );
//		}
//		
//		// group
//		GroupOperation groupIdVantive = Aggregation.group("idVantive")
//				.last("produto").as("produto")
//				.last("idVantive").as("idVantive")
//				.last("dataEntradaFila").as("dataEntradaFila")
//				.last("dataInicio").as("dataInicio")
//				.last("dataFinal").as("dataFinal")
//				.last("idProcessoPrincipal").as("idProcessoPrincipal")
//				.last("resultadoProcedimento").as("resultadoProcedimento")
//				.last("dadosStarStr").as("dadosStarStr")
//				.last("resultadosCheckDuplicidade").as("resultadosCheckDuplicidade")
//				.last("resultadosCheckConfiguracao").as("resultadosCheckConfiguracao")
//				.last("resultadoConfig").as("resultadoConfig")
//				.last("resultadosFinalizarTarefasStar").as("resultadosFinalizarTarefasStar")
//				.last("resultadosFinalizarTarefasSae").as("resultadosFinalizarTarefasSae")
//				.last("movimentouTarefaPendenciaSae").as("movimentouTarefaPendenciaSae");
//		
////		// removendo do backlog os ids de movimentacao de tarefas
////		MatchOperation matchTarefaBacklog = Aggregation.match(
////					Criteria.where("movimentouTarefaPendenciaSae").is(null)
////	    );
////				
////		//Convert the aggregation result into a List
////		AggregationResults<ProcessoIdDomain> groupResults = mongoTemplate.aggregate(
////					Aggregation.newAggregation(matchOperation, groupIdVantive, matchTarefaBacklog), 
////					"configuracao_processos_ids", 
////					ProcessoIdDomain.class);
//		
//		//Convert the aggregation result into a List
//		AggregationResults<ProcessoIdDomain> groupResults = mongoTemplate.aggregate(
//					Aggregation.newAggregation(matchOperation, groupIdVantive), 
//					"configuracao_processos_ids", 
//					ProcessoIdDomain.class);
//		
//		List<ProcessoIdDomain> result = groupResults.getMappedResults();
//		
//		return result;
//	}
	
}
