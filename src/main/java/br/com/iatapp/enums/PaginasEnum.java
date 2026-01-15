package br.com.iatapp.enums;

public enum PaginasEnum {
	
	ATIVACAO_CPE(1),
	CHECK_PROGRAMACAO(2), 
	CONSULTAR_TESTES(3), 
	ADMIN_USUARIOS(4), 
	ATIVACAO_LP(5), 
	RELATORIO_UTILIZACAO(6), 
	SCRIPTS(7),
	DASHBOARD(8),
	CHECK_PROGRAMACAO_MONITOR(9),
	CHECK_CONFIGURACAO(10),
	CHECK_PENDENCIA(11),
	CHAMADOS(12),
	CHECK_DUPLICIDADE_IPS(13), 
	RELATORIO_CONFIGURACAO(14),
	CONFIG_SIP_ONECORE(15),
	CONFIG_REDE_IP(16),
	CONFIG_SWITCH(17),
	MIGRACAO_VIVOSIP(18),
	VIVO_2(19);
	
	private int idPagina;
	
	PaginasEnum(int idPagina) {
		this.idPagina = idPagina;
	}

	public int getIdPagina() {
		return idPagina;
	}

}
