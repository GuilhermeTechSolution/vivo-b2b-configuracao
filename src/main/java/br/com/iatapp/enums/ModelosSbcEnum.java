package br.com.iatapp.enums;

public enum ModelosSbcEnum { 
		
	NAO_ENCONTRADO(0, "Não encontrado"),
	GENBAND(1, "GENBAND"),
	SONUS(2, "SONUS"),
	ORACLE(3, "ORACLE");
	
	private int codigo;
	private String nome;
	
	/**
	 * Construtores
	 */
	ModelosSbcEnum(int codigo, String nome) {
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
		
		if (modeloPe == GENBAND.getCodigo()) {
			return GENBAND.getNome();
		}
		
		if (modeloPe == SONUS.getCodigo()) {
			return SONUS.getNome();	
		}
		
		if (modeloPe == ORACLE.getCodigo()) {
			return ORACLE.getNome();
		}
		
		return NAO_ENCONTRADO.getNome();
	}	
	
	public static ModelosSbcEnum valueOf(int modeloPe) {
		
		if (modeloPe == GENBAND.getCodigo()) {
			return GENBAND;
		}
		
		if (modeloPe == SONUS.getCodigo()) {
			return SONUS;	
		}
		
		if (modeloPe == ORACLE.getCodigo()) {
			return ORACLE;	
		}
		
		return NAO_ENCONTRADO;
	}	
}
