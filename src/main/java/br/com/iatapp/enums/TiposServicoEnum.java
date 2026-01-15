package br.com.iatapp.enums;

public enum TiposServicoEnum {
	
	NAO_ENCONTRADO(0, "Não encontrado"),
	VPN_IP(1, "VPN IP"),
	IP_DEDICADO(2, "IP DEDICADO"),
	METRO_LAN(3, "METRO LAN"),
	SIP_TRUNKING(4, "SIP TRUNKING"),
	INTRAGOV(5, "INTRAGOV");
	
	private int codigo;
	private String nome;
	
	/**
	 * Construtores
	 */
	TiposServicoEnum(int codigo, String nome) {
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
	
	public static TiposServicoEnum valueOf(int status) {
		
		if (status == VPN_IP.getCodigo()) {
			return VPN_IP;
		}
		
		if (status == IP_DEDICADO.getCodigo()) {
			return IP_DEDICADO;	
		}
		
		if (status == METRO_LAN.getCodigo()) {
			return METRO_LAN;	
		}
		
		if (status == SIP_TRUNKING.getCodigo()) {
			return SIP_TRUNKING;	
		}
		
		if (status == INTRAGOV.getCodigo()) {
			return INTRAGOV;	
		}
		
		return NAO_ENCONTRADO;
	}
}
