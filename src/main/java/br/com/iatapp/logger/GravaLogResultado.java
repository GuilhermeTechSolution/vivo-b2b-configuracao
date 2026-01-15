package br.com.iatapp.logger;

import br.com.iatapp.enums.TiposTestesEnum;

public class GravaLogResultado {
	
	private ResultadoLogger resultadoPePrincipal;
	private ResultadoLogger resultadoPeBackup;
	private ResultadoLogger resultadoDslam;
	private ResultadoLogger resultadoCpe;
	private ResultadoLogger resultadoOlt;
	
	public GravaLogResultado() {
		resultadoPePrincipal = new ResultadoLogger();
		resultadoPeBackup = new ResultadoLogger();
		resultadoDslam = new ResultadoLogger();
		resultadoCpe = new ResultadoLogger();
		resultadoOlt = new ResultadoLogger();
	}
	
	public void record(int codTeste, int codProcedimento, int codResultado, String log, String retornoDescricao){

		switch (TiposTestesEnum.valueOf(codTeste)) {
			case PE_PRINCIPAL:
				resultadoPePrincipal.setArrayResultadosELogs(codProcedimento, codResultado, log, retornoDescricao);
				break;
			case PE_BACKUP:
				resultadoPeBackup.setArrayResultadosELogs(codProcedimento, codResultado, log, retornoDescricao);
				break;
			case DSLAM:
				resultadoDslam.setArrayResultadosELogs(codProcedimento, codResultado, log, retornoDescricao);
				break;
			case CPE:
				resultadoCpe.setArrayResultadosELogs(codProcedimento, codResultado, log, retornoDescricao);
				break;
			case OLT:
				resultadoOlt.setArrayResultadosELogs(codProcedimento, codResultado, log, retornoDescricao);
				break;
			default:
				break;
		}
	}
	
	public int getResultado(int codTeste, int codProcedimento){
		
		switch (TiposTestesEnum.valueOf(codTeste)) {
			case PE_PRINCIPAL:
				return resultadoPePrincipal.getResultado(codProcedimento);
			case PE_BACKUP:
				return resultadoPeBackup.getResultado(codProcedimento);
			case DSLAM:
				return resultadoDslam.getResultado(codProcedimento);
			case CPE:
				return resultadoCpe.getResultado(codProcedimento);
			case OLT:
				return resultadoOlt.getResultado(codProcedimento);
			default:
				return -1;
		}
	}
	
	public String getRetornoDescricao(int codTeste, int codProcedimento){
		
		switch (TiposTestesEnum.valueOf(codTeste)) {
			case PE_PRINCIPAL:
				return resultadoPePrincipal.getRetornoDescricao(codProcedimento);
			case PE_BACKUP:
				return resultadoPeBackup.getRetornoDescricao(codProcedimento);
			case DSLAM:
				return resultadoDslam.getRetornoDescricao(codProcedimento);
			case CPE:
				return resultadoCpe.getRetornoDescricao(codProcedimento);
			case OLT:
				return resultadoOlt.getRetornoDescricao(codProcedimento);
			default:
				return "";
		}
	}
	
	public String getLog(int codTeste, int codProcedimento){
		
		switch (TiposTestesEnum.valueOf(codTeste)) {
			case PE_PRINCIPAL:
				return resultadoPePrincipal.getLog(codProcedimento);
			case PE_BACKUP:
				return resultadoPeBackup.getLog(codProcedimento);
			case DSLAM:
				return resultadoDslam.getLog(codProcedimento);
			case CPE:
				return resultadoCpe.getLog(codProcedimento);
			case OLT:
				return resultadoOlt.getLog(codProcedimento);
			default:
				return "";
		}
	}	
	
	/**
	 * Metodos Sets and Gets
	 */	

	public ResultadoLogger getResultadoPePrincipal() {
		return resultadoPePrincipal;
	}

	public void setResultadoPePrincipal(ResultadoLogger resultadoPePrincipal) {
		this.resultadoPePrincipal = resultadoPePrincipal;
	}

	public ResultadoLogger getResultadoPeBackup() {
		return resultadoPeBackup;
	}

	public void setResultadoPeBackup(ResultadoLogger resultadoPeBackup) {
		this.resultadoPeBackup = resultadoPeBackup;
	}

	public ResultadoLogger getResultadoDslam() {
		return resultadoDslam;
	}

	public void setResultadoDslam(ResultadoLogger resultadoDslam) {
		this.resultadoDslam = resultadoDslam;
	}

	public ResultadoLogger getResultadoCpe() {
		return resultadoCpe;
	}

	public void setResultadoCpe(ResultadoLogger resultadoCpe) {
		this.resultadoCpe = resultadoCpe;
	}

	public ResultadoLogger getResultadoOlt() {
		return resultadoOlt;
	}

	public void setResultadoOlt(ResultadoLogger resultadoOlt) {
		this.resultadoOlt = resultadoOlt;
	}
	
}
