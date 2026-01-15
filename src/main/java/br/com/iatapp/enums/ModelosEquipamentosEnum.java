package br.com.iatapp.enums;

public enum ModelosEquipamentosEnum { 
		
	NAO_ENCONTRADO(0, "Não encontrado"),
	CISCO_XE(1, "Cisco XE"),
	CISCO_XR(2, "Cisco XR"),
	JUNIPER(3, "Juniper"),
	HUAWEI(4, "Huawei"),
	ALCATEL(5, "Alcatel"),
	NOKIA(6, "Nokia"),
	CORIANT(7, "CORIANT"),
	DATACOM(8, "DATACOM"),
	DATACOM_DM4050(9, "DATACOM_DM4050"),
	BROCADE(10, "DATACOM_DM4050");
	
	private int codigo;
	private String nome;
	
	/**
	 * Construtores
	 */
	ModelosEquipamentosEnum(int codigo, String nome) {
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
		
		if (modeloPe == DATACOM.getCodigo()) {
			return DATACOM.getNome();	
		}
		
		if (modeloPe == CORIANT.getCodigo()) {
			return CORIANT.getNome();	
		}
		
		if (modeloPe == DATACOM_DM4050.getCodigo()) {
			return DATACOM_DM4050.getNome();	
		}
		
		return NAO_ENCONTRADO.getNome();
	}	
	
	public static ModelosEquipamentosEnum valueOf(int modeloPe) {
		
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
		
		if (modeloPe == DATACOM.getCodigo()) {
			return DATACOM;	
		}
		
		if (modeloPe == CORIANT.getCodigo()) {
			return CORIANT;	
		}
		
		if (modeloPe == DATACOM_DM4050.getCodigo()) {
			return DATACOM_DM4050;	
		}
		
		if (modeloPe == BROCADE.getCodigo()) {
			return BROCADE;	
		}
		
		return NAO_ENCONTRADO;
	}	
}
