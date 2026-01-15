package br.com.iatapp.repositories;

import java.util.Date;
import java.util.List;

import br.com.iatapp.domain.ProcessoIdRetiradaDomain;

public interface RelatorioRetiradaRepositoryMT {
	
	List<ProcessoIdRetiradaDomain> buscaAnaliticoRetirada(Date dataInicio, Date dataFinal);
	
}
