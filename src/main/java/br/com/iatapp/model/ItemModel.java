package br.com.iatapp.model;

import java.io.Serializable;

public class ItemModel implements Serializable {

	private static final long serialVersionUID = -6558923040167241413L;

	private String id;
	private String descricao;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

}
