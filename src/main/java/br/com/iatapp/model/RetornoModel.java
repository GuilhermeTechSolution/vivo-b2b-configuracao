package br.com.iatapp.model;

public class RetornoModel {
	
	private int codigo;
	private String retorno;
	private boolean resultado;
	private String log;
	
	public void reset() {
		codigo = 0;
		retorno = null;
		resultado = false;
		log = null;
	}

	public int getCodigo() {
		return codigo;
	}

	public void setCodigo(int codigo) {
		this.codigo = codigo;
	}

	public String getRetorno() {
		return retorno;
	}

	public void setRetorno(String retorno) {
		this.retorno = retorno;
	}	

	public boolean isResultado() {
		return resultado;
	}

	public void setResultado(boolean resultado) {
		this.resultado = resultado;
	}

	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}
	
}
