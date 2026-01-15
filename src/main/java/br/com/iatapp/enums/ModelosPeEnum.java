package br.com.iatapp.enums;

public enum ModelosPeEnum { 
		
	NAO_ENCONTRADO(0, "Não encontrado"),
	CISCO_XE(1, "Cisco XE"),
	CISCO_XR(2, "Cisco XR"),
	JUNIPER(3, "Juniper"),
	HUAWEI(4, "Huawei"),
	ALCATEL(5, "Alcatel"),
	NOKIA(6, "Nokia");
	
	private int codigo;
	private String nome;
	
	/**
	 * Construtores
	 */
	ModelosPeEnum(int codigo, String nome) {
		this.codigo = codigo;
		this.nome = nome;
	}
	
	/**
	 * Métodos
	 */
	public int getCodigo() {
		return codigo;
	}
	
	public String getNome() {
		return nome;
	}
	
	public static String getNome(int modeloPe) {
		
		if (modeloPe == CISCO_XE.getCodigo()) {
			return CISCO_XE.getNome();
		}
		
		if (modeloPe == CISCO_XR.getCodigo()) {
			return CISCO_XR.getNome();	
		}
		
		if (modeloPe == JUNIPER.getCodigo()) {
			return JUNIPER.getNome();	
		}
		
		if (modeloPe == HUAWEI.getCodigo()) {
			return HUAWEI.getNome();	
		}
		
		if (modeloPe == ALCATEL.getCodigo()) {
			return ALCATEL.getNome();	
		}
		
		if (modeloPe == NOKIA.getCodigo()) {
			return NOKIA.getNome();	
		}
		
		return NAO_ENCONTRADO.getNome();
	}	
	
	public static ModelosPeEnum valueOf(int modeloPe) {
		
		if (modeloPe == CISCO_XE.getCodigo()) {
			return CISCO_XE;
		}
		
		if (modeloPe == CISCO_XR.getCodigo()) {
			return CISCO_XR;	
		}
		
		if (modeloPe == JUNIPER.getCodigo()) {
			return JUNIPER;	
		}
		
		if (modeloPe == HUAWEI.getCodigo()) {
			return HUAWEI;	
		}
		
		if (modeloPe == ALCATEL.getCodigo()) {
			return ALCATEL;	
		}
		
		if (modeloPe == NOKIA.getCodigo()) {
			return NOKIA;	
		}
		
		return NAO_ENCONTRADO;
	}	
}
