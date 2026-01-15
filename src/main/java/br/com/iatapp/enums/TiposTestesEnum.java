package br.com.iatapp.enums;

public enum TiposTestesEnum {

	NAO_ENCONTRADO(0, "Teste não encotrado"),
	PE_PRINCIPAL(1, "Teste PE Principal"),
	PE_BACKUP(2, "Teste PE Backup"),
	DSLAM(3, "Teste DSLAM"),
	CPE(4, "Teste CPE"),
	OLT(5, "Teste OLT"),
	SWITCH(6, "Teste SWITCH"),
	VIVO_2(6, "Teste VIVO 2");

	private int codigo;
	private String descricao;

	TiposTestesEnum(int codigo, String descricao) {
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
	
	public static TiposTestesEnum valueOf(int codigo) {
		
		if (codigo == PE_PRINCIPAL.getCodigo()) {
			return PE_PRINCIPAL;
		}
		
		if (codigo == PE_BACKUP.getCodigo()) {
			return PE_BACKUP;	
		}
		
		if (codigo == DSLAM.getCodigo()) {
			return DSLAM;	
		}
		
		if (codigo == CPE.getCodigo()) {
			return CPE;	
		}
		
		if (codigo == OLT.getCodigo()) {
			return OLT;	
		}
		
		if (codigo == SWITCH.getCodigo()) {
			return SWITCH;	
		}
		
		return NAO_ENCONTRADO;
	}	
}
