package br.com.iatapp.enums;

public enum JsonKeysEnum {
	
	SIPONECORE_LOGIN_U2000("SIPONECORE_LOGIN_U2000"),
	
	// VIVO 2
	VIVO_2_INTERFACE_CONFIGURACAO("VIVO_2_INTERFACE_CONFIGURACAO");
	
	private String codigo;
	
	/**
	 * Construtores
	 */
	JsonKeysEnum(String codigo) {
		this.codigo = codigo;
	}
	
	/**
	 * Métodos
	 */
	public String getCodigo() {
		return codigo;
	}
}
