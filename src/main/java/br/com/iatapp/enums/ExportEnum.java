package br.com.iatapp.enums;
public enum ExportEnum {
	
	NAO_ENCONTRADO(0, "Não encontrado"),
	RELATORIO_CONFIGURACAO(1, "Relatório Configuração"),
	RELATORIO_SIP_ONECORE(2, "Relatório SIP OneCore"),
	RELATORIO_CONFIGURACAO_VIVO2(3, "Relatório Configuração Vivo 2"),
	RELATORIO_RETIRADA(4, "Relatório Retirada");
	
	private int codigo;
	private String nome;
	
	/**
	 * Construtores
	 */
	ExportEnum(int codigo, String nome) {
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
	
	public static ExportEnum valueOf(int codigo) {
		
		if (codigo == RELATORIO_CONFIGURACAO.getCodigo()) {
			return RELATORIO_CONFIGURACAO;
		}
		
		if (codigo == RELATORIO_SIP_ONECORE.getCodigo()) {
			return RELATORIO_SIP_ONECORE;
		}
		
		if (codigo == RELATORIO_CONFIGURACAO_VIVO2.getCodigo()) {
			return RELATORIO_CONFIGURACAO_VIVO2;
		}
		
		if (codigo == RELATORIO_RETIRADA.getCodigo()) {
			return RELATORIO_RETIRADA;
		}
		
		return NAO_ENCONTRADO;
	}
}
