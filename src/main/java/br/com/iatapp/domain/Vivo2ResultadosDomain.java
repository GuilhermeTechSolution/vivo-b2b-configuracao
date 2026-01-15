package br.com.iatapp.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="vivo_2_resultados")
public class Vivo2ResultadosDomain {
	
	@Id
	private String id;
	
	@Indexed(name = "_idVivo2_01")
	private String idVivo2;
	
	private int idTipo;
	private int idItem;
	private int idStatus;
	private String mensagem;
	private String logTeste;
	private String nome;
	
	public Vivo2ResultadosDomain() {}
	
	public Vivo2ResultadosDomain(String idVivo2, int idTipo, int idItem, int idStatus, String mensagem, String logTeste) {
		this.idVivo2 = idVivo2;
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

	public String getIdVivo2() {
		return idVivo2;
	}

	public void setIdVivo2(String idVivo2) {
		this.idVivo2 = idVivo2;
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
