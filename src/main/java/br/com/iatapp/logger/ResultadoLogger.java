package br.com.iatapp.logger;

import org.apache.commons.lang3.StringUtils;

public class ResultadoLogger {
	
	public static final int NUMERO_PROCEDIMENTOS = 30;

	private int[] codigosProcedimentos = new int[NUMERO_PROCEDIMENTOS];
	private int[] codigosResultados = new int[NUMERO_PROCEDIMENTOS];
	private String[] logsResultados = new String[NUMERO_PROCEDIMENTOS];
	private String[] retornosDescricao = new String[NUMERO_PROCEDIMENTOS];
	private String[] nomesProcedimentos = new String[NUMERO_PROCEDIMENTOS];
	
	public ResultadoLogger() {
		for (int c = 0; c < NUMERO_PROCEDIMENTOS; c++) {
			codigosResultados[c] = -1;
		}
	}
	
	public void setArrayResultadosELogs(int codigoProcedimento, int codigoResultado, String log, String retornoDescricao) {
		codigosResultados[codigoProcedimento] = codigoResultado;
		logsResultados[codigoProcedimento] = log;
		retornosDescricao[codigoProcedimento] = retornoDescricao;
	}
	
	public void setArrayResultadosELogs(int codigoProcedimento, int codigoResultado, String log, String retornoDescricao, String nomeProcedimento, int indiceProcedimento) {
		codigosResultados[codigoProcedimento] = codigoResultado;
		logsResultados[codigoProcedimento] = log;
		retornosDescricao[codigoProcedimento] = retornoDescricao;
		nomesProcedimentos[codigoProcedimento] = nomeProcedimento;
	}
	
//	public void setArrayResultadosELogs(int codigoProcedimento, int codigoResultado, String log, String retornoDescricao, String nomeProcedimento, int indiceProcedimento) {
//		codigosProcedimentos[indiceProcedimento] = codigoProcedimento;
//		codigosResultados[indiceProcedimento] = codigoResultado;
//		logsResultados[indiceProcedimento] = log;
//		retornosDescricao[indiceProcedimento] = retornoDescricao;
//		nomesProcedimentos[indiceProcedimento] = nomeProcedimento;
//		indicesProcedimentos[indiceProcedimento] = indiceProcedimento;
//	}
	
	public int getResultado(int codigoProcedimento) {
		return codigosResultados[codigoProcedimento];
	}
	
	public String getRetornoDescricao(int codigoProcedimento) {
		return StringUtils.isNotBlank(retornosDescricao[codigoProcedimento]) ? retornosDescricao[codigoProcedimento] : "";
	}
	
	public String getLog(int codigoProcedimento) {
		return StringUtils.isNotBlank(logsResultados[codigoProcedimento]) ? logsResultados[codigoProcedimento] : "";
	}
	
	public void setResultado(int codigoProcedimento, int codigoResultado) {
		codigosResultados[codigoProcedimento] = codigoResultado;
	}
	
	public void setRetornoDescricao(int codigoProcedimento ,String retornoDescricao) {
		retornosDescricao[codigoProcedimento] = retornoDescricao;
	}
	
	public void setLog(int codigoProcedimento ,String log) {
		logsResultados[codigoProcedimento] = log;
	}
	
	/**
	 * Metodos Sets and Gets
	 */

	public int[] getCodigosResultados() {
		return codigosResultados;
	}

	public void setCodigosResultados(int[] codigosResultados) {
		this.codigosResultados = codigosResultados;
	}

	public String[] getLogsResultados() {
		return logsResultados;
	}

	public void setLogsResultados(String[] logsResultados) {
		this.logsResultados = logsResultados;
	}

	public String[] getRetornosDescricao() {
		return retornosDescricao;
	}

	public void setRetornosDescricao(String[] retornosDescricao) {
		this.retornosDescricao = retornosDescricao;
	}

	public String[] getNomesProcedimentos() {
		return nomesProcedimentos;
	}

	public void setNomesProcedimentos(String[] nomesProcedimentos) {
		this.nomesProcedimentos = nomesProcedimentos;
	}

	public int[] getCodigosProcedimentos() {
		return codigosProcedimentos;
	}

	public void setCodigosProcedimentos(int[] codigosProcedimentos) {
		this.codigosProcedimentos = codigosProcedimentos;
	}
	
}
