package br.com.iatapp.enums;

public enum PermissoesEnum {
	
	SEM_PERMISSAO(0),
	LEITURA(1), 
	ESCRITA(2);
	
	private int idPermissao;
	
	PermissoesEnum(int idPermissao) {
		this.idPermissao = idPermissao;
	}

	public int getIdPermissao() {
		return idPermissao;
	}

}
