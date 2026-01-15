package br.com.iatapp.enums;

public enum PerfisEnum {

	ADMINISTRADOR(1, "Administrador"),
	LP_ROUTER(2, "LP/Router"),
	CONFIG(5, "Configuração"),
	TECNICO_CAMPO(3, "Técnico de Campo"),
	DESPACHO(7, "Despacho"),
	APROVISIONAMENTO(11, "Aprovisionamento"),
	TECNICO_CAMPO_REPARO(12, "Técnico de Campo Reparo"),
	TECNICO_CAMPO_MULTISKILL(13, "Técnico de Campo MultiSkill");

	private int id;
	private String descricao;

	PerfisEnum(int id, String descricao) {
		this.id = id;
		this.descricao = descricao;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

}
