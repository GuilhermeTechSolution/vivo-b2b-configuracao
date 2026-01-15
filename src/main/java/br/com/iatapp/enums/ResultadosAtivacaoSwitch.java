package br.com.iatapp.enums;

public enum ResultadosAtivacaoSwitch {

	ACESSOU_EQUIPAMENTO(0, "Acessou Equipamento", 0),
	
	// CONFIGURACAO
	
	CONFIGURACAO_SCRIPT(1, "Configuração de Script", 5),
	
	// PRE-CHECK
	
	CHECK_VALIDACAO_VLAN_GERENCIA_SWA(2, "Validação VLAN Gerência SWA", 1),
	CHECK_VALIDACAO_VLAN_TUNEL_SERVICO(3, "Validação VLAN Túnel Serviço", 2),
	CHECK_CONFIGURACAO_INTERFACE(4, "Check de Configuração da Interface", 3),
	CHECK_VALIDACAO_VLAN_TABLE(5, "Validação VLAN Table", 4),
	CHECK_STATUS_INTERFACE(6, "Check Status da Interface", 2);

	private int codigo;
	private String nome;
	private int index;

	ResultadosAtivacaoSwitch(int codigo, String nome, int index) {
		this.codigo = codigo;
		this.nome = nome;
		this.index = index;
	}

	public int getCodigo() {
		return codigo;
	}

	public void setCodigo(int codigo) {
		this.codigo = codigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

}
