package br.com.iatapp.enums;

public enum TiposScriptsEnum {

	NAO_ENCONTRADO(0, "Não encontrado"),
	BCF_U2000(1, "Script BCF U2000"),
	IMS_ATR(2, "Script IMS ATR"),
	SBC(3, "Script SBC");

	private int codigo;
	private String descricao;

	TiposScriptsEnum(int codigo, String descricao) {
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
	
	public static TiposScriptsEnum valueOf(int codigo) {
		
		if (codigo == BCF_U2000.getCodigo()) {
			return BCF_U2000;
		}
		
		if (codigo == IMS_ATR.getCodigo()) {
			return IMS_ATR;
		}
		
		if (codigo == SBC.getCodigo()) {
			return SBC;
		}
		
		return NAO_ENCONTRADO;
	}	
}
