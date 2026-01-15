package br.com.iatapp.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="testes_principal_logs")
public class TestePrincipalLogsDomain {
	
	@Id
	private String id;

	@Indexed(name = "_idTeste_01")
	private int idTeste;
	
	private String logGeral;
	private String logFormatado;
	
	public TestePrincipalLogsDomain() {}
	
	public TestePrincipalLogsDomain(int idTeste, String logGeral, String logFormatado) {
		this.idTeste = idTeste;
		this.logGeral = logGeral;
		this.logFormatado = logFormatado;
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

	public String getLogGeral() {
		return logGeral;
	}

	public void setLogGeral(String logGeral) {
		this.logGeral = logGeral;
	}

	public String getLogFormatado() {
		return logFormatado;
	}

	public void setLogFormatado(String logFormatado) {
		this.logFormatado = logFormatado;
	}	

}
