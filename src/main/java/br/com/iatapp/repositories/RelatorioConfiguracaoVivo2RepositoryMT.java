package br.com.iatapp.repositories;

import java.util.Date;
import java.util.List;

import br.com.iatapp.domain.ProcessoIdVivo2Domain;

public interface RelatorioConfiguracaoVivo2RepositoryMT {
	
	List<ProcessoIdVivo2Domain> buscaAnaliticoQip(Date dataInicio, Date dataFinal, String servico);
	
	List<ProcessoIdVivo2Domain> buscaAnaliticoConfiguracao(Date dataInicio, Date dataFinal, String servico);
	
}
