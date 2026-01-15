package br.com.iatapp.enums;

public enum ModulosTesteEnum {

	NAO_ENCONTRADO(0, "Não encontrado"),
	ATIVACAO_CPE(1, "Ativação Router"),
	CHECK_PROGRAMACAO(2, "Check Programação"),
	ATIVACAO_LP(3, "Ativação LP"),
	CHECK_CONFIGURACAO(4, "Check Configuração"),
	CHECK_DUPLICIDADE_IPS(5, "Check Duplicidade IPs"),
	CHECK_VOIP(6, "Check Voip");

	private int codigo;
	private String descricao;

	ModulosTesteEnum(int codigo, String descricao) {
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
	
	public static ModulosTesteEnum valueOf(int codigo) {
		
		if (codigo == ATIVACAO_CPE.getCodigo()) {
			return ATIVACAO_CPE;
		}
		
		if (codigo == CHECK_PROGRAMACAO.getCodigo()) {
			return CHECK_PROGRAMACAO;	
		}
		
		if (codigo == ATIVACAO_LP.getCodigo()) {
			return ATIVACAO_LP;	
		}
		
		if (codigo == CHECK_CONFIGURACAO.getCodigo()) {
			return CHECK_CONFIGURACAO;	
		}
		
		if (codigo == CHECK_DUPLICIDADE_IPS.getCodigo()) {
			return CHECK_DUPLICIDADE_IPS;	
		}
		
		if (codigo == CHECK_VOIP.getCodigo()) {
			return CHECK_VOIP;	
		}
		
		return NAO_ENCONTRADO;
	}	
}
