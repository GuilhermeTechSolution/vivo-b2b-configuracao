package br.com.iatapp.enums;

public enum ResultadosStatus {

	NAO_EXECUTADO(0, "Teste não foi executado"), 
	OK(1, "Teste Ok"), 
	FALHOU(2, "Teste Falhou"), 
	WARNING(3, "Teste Warning");

	private int codigo;
	private String descricao;

	ResultadosStatus(int codigo, String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;
	}

	public int getCodigo() {
		return codigo;
	}

	public void setCodigo(int codigo) {
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

}
