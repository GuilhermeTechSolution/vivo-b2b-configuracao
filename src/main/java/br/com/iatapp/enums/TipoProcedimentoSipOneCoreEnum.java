package br.com.iatapp.enums;

public enum TipoProcedimentoSipOneCoreEnum {
	
	NAO_ENCONTRADO(0, "Não encontrado"),
	SCRIPTS_ALTA_FRESH(1, "SCRIPTS_ALTA_FRESH"),
	SCRIPTS_AP_PORTABILIDADE(2, "SCRIPTS_AP_PORTABILIDADE"),
	SCRIPTS_AP_MIGRACAO(3, "SCRIPTS_AP_MIGRACAO"),
	ROLLBACK_ALTA_FRESH(4, "ROLLBACK_ALTA_FRESH"),
	ROLLBACK_AP_PORTABILIDADE(5, "ROLLBACK_AP_PORTABILIDADE"),
	ROLLBACK_AP_MIGRACAO(6, "ROLLBACK_AP_MIGRACAO");
	
	private int codigo;
	private String nome;
	
	/**
	 * Construtores
	 */
	TipoProcedimentoSipOneCoreEnum(int codigo, String nome) {
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
	
	public static TipoProcedimentoSipOneCoreEnum valueOf(int codigo) {
		
		if (codigo == SCRIPTS_ALTA_FRESH.getCodigo()) {
			return SCRIPTS_ALTA_FRESH;
		}
		
		if (codigo == SCRIPTS_AP_PORTABILIDADE.getCodigo()) {
			return SCRIPTS_AP_PORTABILIDADE;
		}
		
		if (codigo == SCRIPTS_AP_MIGRACAO.getCodigo()) {
			return SCRIPTS_AP_MIGRACAO;
		}
		
		if (codigo == ROLLBACK_ALTA_FRESH.getCodigo()) {
			return ROLLBACK_ALTA_FRESH;
		}
		
		if (codigo == ROLLBACK_AP_PORTABILIDADE.getCodigo()) {
			return ROLLBACK_AP_PORTABILIDADE;
		}
		
		if (codigo == ROLLBACK_AP_MIGRACAO.getCodigo()) {
			return ROLLBACK_AP_MIGRACAO;
		}
		
		return NAO_ENCONTRADO;
	}
	
	public static String getNome(int codigo) {
		
		if (codigo == SCRIPTS_ALTA_FRESH.getCodigo()) {
			return SCRIPTS_ALTA_FRESH.getNome();
		}
		
		if (codigo == SCRIPTS_AP_PORTABILIDADE.getCodigo()) {
			return SCRIPTS_AP_PORTABILIDADE.getNome();
		}
		
		if (codigo == SCRIPTS_AP_MIGRACAO.getCodigo()) {
			return SCRIPTS_AP_MIGRACAO.getNome();
		}
		
		if (codigo == ROLLBACK_ALTA_FRESH.getCodigo()) {
			return ROLLBACK_ALTA_FRESH.getNome();
		}
		
		if (codigo == ROLLBACK_AP_PORTABILIDADE.getCodigo()) {
			return ROLLBACK_AP_PORTABILIDADE.getNome();
		}
		
		if (codigo == ROLLBACK_AP_MIGRACAO.getCodigo()) {
			return ROLLBACK_AP_MIGRACAO.getNome();
		}
		
		return NAO_ENCONTRADO.getNome();
	}
}
