package br.com.iatapp.logger;

public class GravaLogResultadoVivo2 {
	
	private ResultadoLogger resultadoVivo2;
	
	public GravaLogResultadoVivo2() {
		resultadoVivo2 = new ResultadoLogger();
	}
	
	public void record(int codTeste, int codProcedimento, int codResultado, String log, String retornoDescricao, String nomeProcedimento, int indiceProcedimento) {
		resultadoVivo2.setArrayResultadosELogs(codProcedimento, codResultado, log, retornoDescricao, nomeProcedimento, indiceProcedimento);
	}
	
	public int getResultado(int codTeste, int codProcedimento) {		
		return resultadoVivo2.getResultado(codProcedimento);
	}
	
	public String getRetornoDescricao(int codTeste, int codProcedimento){
		return resultadoVivo2.getRetornoDescricao(codProcedimento);
	}
	
	public String getLog(int codTeste, int codProcedimento){
		return resultadoVivo2.getLog(codProcedimento);
	}
		
	public ResultadoLogger getResultadoVivo2() {
		return resultadoVivo2;
	}

	public void setResultadoVivo2(ResultadoLogger resultadoVivo2) {
		this.resultadoVivo2 = resultadoVivo2;
	}	
		
}
