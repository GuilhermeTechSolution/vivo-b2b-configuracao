package br.com.iatapp.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="configuracao_switch_resultados")
public class ConfigSwitchResultadosDomain {
	
	@Id
	private String id;
	
	@Indexed(name = "_idConfSwitch_01")
	private String idConfSwitch;
	
	private int idTipo;
	private int idItem;
	private int idStatus;
	private String mensagem;
	private String logTeste;
	private String nome;
	
	public ConfigSwitchResultadosDomain() {}
	
	public ConfigSwitchResultadosDomain(String idConfSwitch, int idTipo, int idItem, int idStatus, String mensagem, String logTeste) {
		this.idConfSwitch = idConfSwitch;
		this.idTipo = idTipo;
		this.idItem = idItem;
		this.idStatus = idStatus;
		this.mensagem = mensagem;
		this.logTeste = logTeste;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIdConfSwitch() {
		return idConfSwitch;
	}

	public void setIdConfSwitch(String idConfSwitch) {
		this.idConfSwitch = idConfSwitch;
	}

	public int getIdTipo() {
		return idTipo;
	}

	public void setIdTipo(int idTipo) {
		this.idTipo = idTipo;
	}

	public int getIdItem() {
		return idItem;
	}

	public void setIdItem(int idItem) {
		this.idItem = idItem;
	}

	public String getLogTeste() {
		return logTeste;
	}

	public void setLogTeste(String logTeste) {
		this.logTeste = logTeste;
	}

	public int getIdStatus() {
		return idStatus;
	}

	public void setIdStatus(int idStatus) {
		this.idStatus = idStatus;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
}
