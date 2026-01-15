package br.com.iatapp.model;

import java.util.Map;

import br.com.iatapp.dao.UsuariosDao;


public class UsuarioModel {
	
	private int idUsuario;
	private int idPerfil;
	private String nomePerfil;
	private String email;
	private String senha;
	private String novaSenha;
	private String novaSenhaRepetir;
	private String nome;
	private String telefone;
	private String numeroRe;
	private String dataCadastro;
	private int ativo;
	private String dataExpiracaoSenha;
	private int idUsuarioCadastro;
	private String nomeUsuarioCadastro;
	private int senhaExpirou;
	private String senhaAntiga;
	private String repitaNovaSenha;
	private int idPagina;
	private int idPermissao;
	private String nomePagina;
	private int idMenu;
	private String nomeMenu;
	private int idSenha;
	private String idMobileDevice;
	
	private String loginPe;
	private String senhaPe;
	private String loginRedeIp;
	private String senhaRedeIp;
	private String loginCpe;
	private String senhaCpe;
	private String senhaEnableCpe;

	private Map<Integer, Integer> mapPermissoesPaginas;
	
	/**
	 * 
	 */
	public void preencheListaPermissoesPagina() {
		try {
			mapPermissoesPaginas = new UsuariosDao().retornaListaPermissoesPagina(this);
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


	public int getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}

	public int getIdPerfil() {
		return idPerfil;
	}

	public void setIdPerfil(int idPerfil) {
		this.idPerfil = idPerfil;
	}

	public String getNomePerfil() {
		return nomePerfil;
	}

	public void setNomePerfil(String nomePerfil) {
		this.nomePerfil = nomePerfil;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getNovaSenha() {
		return novaSenha;
	}

	public void setNovaSenha(String novaSenha) {
		this.novaSenha = novaSenha;
	}

	public String getNovaSenhaRepetir() {
		return novaSenhaRepetir;
	}

	public void setNovaSenhaRepetir(String novaSenhaRepetir) {
		this.novaSenhaRepetir = novaSenhaRepetir;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String getNumeroRe() {
		return numeroRe;
	}

	public void setNumeroRe(String numeroRe) {
		this.numeroRe = numeroRe;
	}

	public String getLoginPe() {
		return loginPe;
	}

	public void setLoginPe(String loginPe) {
		this.loginPe = loginPe;
	}

	public String getSenhaPe() {
		return senhaPe;
	}

	public void setSenhaPe(String senhaPe) {
		this.senhaPe = senhaPe;
	}

	public String getLoginRedeIp() {
		return loginRedeIp;
	}

	public void setLoginRedeIp(String loginRedeIp) {
		this.loginRedeIp = loginRedeIp;
	}

	public String getSenhaRedeIp() {
		return senhaRedeIp;
	}

	public void setSenhaRedeIp(String senhaRedeIp) {
		this.senhaRedeIp = senhaRedeIp;
	}

	public String getLoginCpe() {
		return loginCpe;
	}

	public void setLoginCpe(String loginCpe) {
		this.loginCpe = loginCpe;
	}

	public String getSenhaCpe() {
		return senhaCpe;
	}

	public void setSenhaCpe(String senhaCpe) {
		this.senhaCpe = senhaCpe;
	}

	public String getSenhaEnableCpe() {
		return senhaEnableCpe;
	}

	public void setSenhaEnableCpe(String senhaEnableCpe) {
		this.senhaEnableCpe = senhaEnableCpe;
	}

	public String getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(String dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public int getAtivo() {
		return ativo;
	}

	public void setAtivo(int ativo) {
		this.ativo = ativo;
	}

	public String getDataExpiracaoSenha() {
		return dataExpiracaoSenha;
	}

	public void setDataExpiracaoSenha(String dataExpiracaoSenha) {
		this.dataExpiracaoSenha = dataExpiracaoSenha;
	}

	public int getIdUsuarioCadastro() {
		return idUsuarioCadastro;
	}

	public void setIdUsuarioCadastro(int idUsuarioCadastro) {
		this.idUsuarioCadastro = idUsuarioCadastro;
	}

	public String getNomeUsuarioCadastro() {
		return nomeUsuarioCadastro;
	}

	public void setNomeUsuarioCadastro(String nomeUsuarioCadastro) {
		this.nomeUsuarioCadastro = nomeUsuarioCadastro;
	}

	public int getSenhaExpirou() {
		return senhaExpirou;
	}

	public void setSenhaExpirou(int senhaExpirou) {
		this.senhaExpirou = senhaExpirou;
	}

	public String getSenhaAntiga() {
		return senhaAntiga;
	}

	public void setSenhaAntiga(String senhaAntiga) {
		this.senhaAntiga = senhaAntiga;
	}

	public String getRepitaNovaSenha() {
		return repitaNovaSenha;
	}

	public void setRepitaNovaSenha(String repitaNovaSenha) {
		this.repitaNovaSenha = repitaNovaSenha;
	}

	public int getIdPagina() {
		return idPagina;
	}

	public void setIdPagina(int idPagina) {
		this.idPagina = idPagina;
	}

	public int getIdPermissao() {
		return idPermissao;
	}

	public void setIdPermissao(int idPermissao) {
		this.idPermissao = idPermissao;
	}

	public String getNomePagina() {
		return nomePagina;
	}

	public void setNomePagina(String nomePagina) {
		this.nomePagina = nomePagina;
	}

	public int getIdMenu() {
		return idMenu;
	}

	public void setIdMenu(int idMenu) {
		this.idMenu = idMenu;
	}

	public String getNomeMenu() {
		return nomeMenu;
	}

	public void setNomeMenu(String nomeMenu) {
		this.nomeMenu = nomeMenu;
	}

	public int getIdSenha() {
		return idSenha;
	}

	public void setIdSenha(int idSenha) {
		this.idSenha = idSenha;
	}

	public Map<Integer, Integer> getMapPermissoesPaginas() {
		return mapPermissoesPaginas;
	}

	public void setMapPermissoesPaginas(Map<Integer, Integer> mapPermissoesPaginas) {
		this.mapPermissoesPaginas = mapPermissoesPaginas;
	}

	public String getIdMobileDevice() {
		return idMobileDevice;
	}

	public void setIdMobileDevice(String idMobileDevice) {
		this.idMobileDevice = idMobileDevice;
	}

}
