package br.com.iatapp.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="testes_principal_resultados_logs")
public class TestePrincipalResultadosLogsDomain {
	
	@Id
	private String id;
	
	@Indexed(name = "_idTeste_01")
	private int idTeste;
	
	private int idTipo;
	private int idItem;
	private int idStatus;
	private String mensagem;
	private String logTeste;
	
	public TestePrincipalResultadosLogsDomain() {}
	
	public TestePrincipalResultadosLogsDomain(int idTeste, int idTipo, int idItem, int idStatus, String mensagem, String logTeste) {
		this.idTeste = idTeste;
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

	public int getIdTeste() {
		return idTeste;
	}

	public void setIdTeste(int idTeste) {
		this.idTeste = idTeste;
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
	
}
