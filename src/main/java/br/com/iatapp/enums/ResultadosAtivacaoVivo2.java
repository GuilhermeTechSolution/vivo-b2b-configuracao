package br.com.iatapp.enums;

public enum ResultadosAtivacaoVivo2 {

	ACESSOU_EQUIPAMENTO(0, "Acessou Equipamento", 0),
	
	// CONFIGURACAO
	
	CONFIGURACAO_SCRIPT(1, "Configuração de Script", 5),
	
	// PRE-CHECK
	
	CHECK_VALIDACAO_IPS_LIVRES(2, "Validação IPs Livres", 1),
	CHECK_VLANS(3, "Validação Vlans", 2),
	CHECK_INTERFACE_CONFIGURACAO(4, "Interface de Configuração", 3),
	CHECK_CONFIGURACAO_IPS(5, "Check de Configuração IPs", 6),
	CHECK_VRF_SIP(6, "Check VRF SIP", 7);

	private int codigo;
	private String nome;
	private int index;

	ResultadosAtivacaoVivo2(int codigo, String nome, int index) {
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
