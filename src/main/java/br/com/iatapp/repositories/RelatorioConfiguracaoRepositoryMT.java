package br.com.iatapp.repositories;

import java.util.Date;
import java.util.List;

import br.com.iatapp.domain.ProcessoIdDomain;

public interface RelatorioConfiguracaoRepositoryMT {
	
	List<ProcessoIdDomain> buscaResumoConfiguracao(Date dataInicio, Date dataFinal, String produto);
	
	List<ProcessoIdDomain> buscaAnaliticoConfiguracao(Date dataInicio, Date dataFinal, String produto);
	
}
