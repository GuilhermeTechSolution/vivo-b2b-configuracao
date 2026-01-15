package br.com.iatapp.enums;

public enum CodigoErroAcessoEquipamentoEnum {

	NAO_EXECUTADO(0, "Teste não foi executado"), 
	IP_HOSTNAME_INVALIDO(1, "Teste Falhou. O IP/Hostname informado não foi encontrado."), 
	EQUIPAMENTO_NAO_RESPONDE(2, "Teste Falhou. O Equipamento não responde."), 
	LOGIN_SENHA_INVALIDO(3, "Teste Falhou. Login e/ou senha inválidos."),
	EQUIPAMENTO_PERDEU_5_PINGS_SEQ(4, "Equipamento perdeu 5 pings na sequência"),
	COMANDO_INVALIDO(5, "Equipamento enviou comando ou parâmetro inválido"),
	EQUIPAMENTO_ERRO_IDLE_TIMEOUT(6, "Equipamento está demorando para responder ao Ping e retornou a mensagem que irá expirar o 'Idle Timeout'. Foi necessário interromper o teste."),
	FALHA_AUTENTICACAO(7, "Teste Falhou. Falha de autenticação."),
	NO_ROUTE(8, "Equipamento não possui rota.");

	private int codigo;
	private String descricao;

	CodigoErroAcessoEquipamentoEnum(int codigo, String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;
	}
	
	public static CodigoErroAcessoEquipamentoEnum valueOf(int codigo) {
		
		if (codigo == IP_HOSTNAME_INVALIDO.getCodigo()) {
			return IP_HOSTNAME_INVALIDO;
		}
		
		if (codigo == EQUIPAMENTO_NAO_RESPONDE.getCodigo()) {
			return EQUIPAMENTO_NAO_RESPONDE;	
		}
		
		if (codigo == LOGIN_SENHA_INVALIDO.getCodigo()) {
			return LOGIN_SENHA_INVALIDO;	
		}
		
		if (codigo == EQUIPAMENTO_PERDEU_5_PINGS_SEQ.getCodigo()) {
			return EQUIPAMENTO_PERDEU_5_PINGS_SEQ;	
		}
		
		if (codigo == EQUIPAMENTO_ERRO_IDLE_TIMEOUT.getCodigo()) {
			return EQUIPAMENTO_ERRO_IDLE_TIMEOUT;	
		}
		
		if (codigo == FALHA_AUTENTICACAO.getCodigo()) {
			return FALHA_AUTENTICACAO;	
		}
		
		return NAO_EXECUTADO;
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
