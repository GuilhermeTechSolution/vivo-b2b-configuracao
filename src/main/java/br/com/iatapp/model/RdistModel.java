package br.com.iatapp.model;

import java.util.Map;

import br.com.iatapp.dao.VivoB2BDao;

public class RdistModel {
	
	private int idPerfil;
	private int id;
	private String rdist;
	private String backbone;
	
	
	private Map<Integer, Integer> mapPermissoesPaginas;
	
	/**
	 * 
	 */
	public void preencheListaPermissoesPagina() {
		try {
			mapPermissoesPaginas = new VivoB2BDao().retornaListaPermissoesPagina(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param idPagina
	 * @return
	 */
	public int getPermissaoPagina(int idPagina) {
		if(mapPermissoesPaginas == null) {
			return 0;
		}
		if (mapPermissoesPaginas.containsKey(idPagina))
			return mapPermissoesPaginas.get(idPagina);
		else
			return 0;
	}

	public int getIdPerfil() {
		return idPerfil;
	}

	public void setIdPerfil(int idPerfil) {
		this.idPerfil = idPerfil;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getRdist() {
		return rdist;
	}

	public void setRdist(String rdist) {
		this.rdist = rdist;
	}

	public String getBackbone() {
		return backbone;
	}

	public void setBackbone(String backbone) {
		this.backbone = backbone;
	}
	
}
