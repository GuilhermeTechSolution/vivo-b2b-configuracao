package br.com.iatapp.enums;

public enum ResultadosTestePePrincipal {

	ACESSOU_PE(0, "Acessou PE Principal", ""),
	LOCALIZOU_INTERFACE(1, "Localizou Interface", ""),
	INTERFACE_UP(2, "Status da Interface", ""),
	PING_WAN(3, "Ping WAN", ""),
	ROUTE_MAP(4, "Route Map", ""),
	ROTAS_ESTATICAS_OU_BGP(5, "Rotas Estáticas ou BGP", ""),
	TRAFEGO_INTERFACE(6, "Tráfego na interface", ""),
	SHOW_INTERFACE_BRIEF(7, "Show interface brief", ""),
	SHOW_ARP(8, "Show tabela arp", ""),
	SHOW_BGP_SUMMARY(9, "Show bgp summary", ""),
	SHOW_BGP_ROUTES(10, "Show bgp routes", ""),
	GET_CONFIG_INTERFACE(11, "Get config interface", ""),
	CONFIG_ROUTER_STATIC(12, "Configuration router static", ""),
	CHECK_DUPLICIDADE_IPS(13, "Check de Duplicidade de IPs", ""),
	SIP_CHECK_VRF(14, "Check VRF SIP", ""),
	SIP_CHECK_COMMUNITY(15, "Check Community Voip SIP", "");	

	private int codigo;
	private String descricao;
	private String nome;

	ResultadosTestePePrincipal(int codigo, String nome, String descricao) {
		this.codigo = codigo;
		this.nome = nome;
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

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

}
