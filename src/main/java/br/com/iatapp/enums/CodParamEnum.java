package br.com.iatapp.enums;

public enum CodParamEnum { 
		
	NAO_ENCONTRADO(0, ""),
	TOTAL(1, "TOTAL"),
	FINALIZAR_TAREFAS_SAE(2, "FINALIZAR_TAREFAS_SAE"),
	FINALIZAR_TAREFAS_STAR(3, "FINALIZAR_TAREFAS_STAR");
	
	private int codigo;
	private String nome;
	
	/**
	 * Construtores
	 */
	CodParamEnum(int codigo, String nome) {
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

	public static CodParamEnum valueOf(int codigo) {
		
		if (codigo == TOTAL.getCodigo()) {
			return TOTAL;
		}
		
		if (codigo == FINALIZAR_TAREFAS_SAE.getCodigo()) {
			return FINALIZAR_TAREFAS_SAE;
		}
		
		return NAO_ENCONTRADO;
	}

	
	
}