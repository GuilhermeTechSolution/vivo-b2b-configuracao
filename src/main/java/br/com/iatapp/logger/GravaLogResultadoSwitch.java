package br.com.iatapp.logger;

public class GravaLogResultadoSwitch {
	
	private ResultadoLogger resultadoSwitch;
	
	public GravaLogResultadoSwitch() {
		resultadoSwitch = new ResultadoLogger();
	}
	
	public void record(int codTeste, int codProcedimento, int codResultado, String log, String retornoDescricao, String nomeProcedimento, int indiceProcedimento) {
		resultadoSwitch.setArrayResultadosELogs(codProcedimento, codResultado, log, retornoDescricao, nomeProcedimento, indiceProcedimento);
	}
	
	public int getResultado(int codTeste, int codProcedimento) {		
		return resultadoSwitch.getResultado(codProcedimento);
	}
	
	public String getRetornoDescricao(int codTeste, int codProcedimento){
		return resultadoSwitch.getRetornoDescricao(codProcedimento);
	}
	
	public String getLog(int codTeste, int codProcedimento){
		return resultadoSwitch.getLog(codProcedimento);
	}
		
	public ResultadoLogger getResultadoSwitch() {
		return resultadoSwitch;
	}

	public void setResultadoSwitch(ResultadoLogger resultadoSwitch) {
		this.resultadoSwitch = resultadoSwitch;
	}	
		
}
