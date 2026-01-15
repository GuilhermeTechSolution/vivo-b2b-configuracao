package br.com.iatapp.enums;

public enum CodigoTimeoutEnum { 
		
	ERRO_INTERNO(0),
	READ_UNTIL_OK(1),
	READ_UNTIL_TIMEOUT(2),
	PING_PERDA_PACOTE(3),
	ERRO_IDLE_TIMEOUT(4);
	
	private int codigo;
	
	/**
	 * Construtores
	 */
	CodigoTimeoutEnum(int codigo) {
		this.codigo = codigo;
	}
	
	/**
	 * Métodos
	 */
	public int getCodigo() {
		return codigo;
	}
		
	
}
