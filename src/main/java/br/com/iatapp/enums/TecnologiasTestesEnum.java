package br.com.iatapp.enums;

public enum TecnologiasTestesEnum { 
		
	NAO_ENCONTRADO(0, ""),
	PRAPS(1, "Praps"),
	GPON(2, "Gpon"),
	FSP(3, "FSP"),
	SERIAL(4, "Serial"),
	MULTILINK(5, "Multilink"),
	INTRAGOV_PRAPS(6, "Intragov Praps"),
	INTRAGOV_GPON(7, "Intragov Gpon"),
	INTRAGOV_SERIAL(8, "Intragov Serial"),
	INTRAGOV_MULTILINK(9, "Intragov Multilink"),
	SWT(10, "Switch");
	
	private int codigo;
	private String nome;
	
	/**
	 * Construtores
	 */
	TecnologiasTestesEnum(int codigo, String nome) {
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

	public static TecnologiasTestesEnum valueOf(int codigo) {
		
		if (codigo == PRAPS.getCodigo()) {
			return PRAPS;
		}
		
		if (codigo == GPON.getCodigo()) {
			return GPON;
		}
		
		if (codigo == FSP.getCodigo()) {
			return FSP;
		}
		
		if (codigo == SWT.getCodigo()) {
			return SWT;
		}
		
		return NAO_ENCONTRADO;
	}

	
	
}