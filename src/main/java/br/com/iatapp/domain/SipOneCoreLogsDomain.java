package br.com.iatapp.domain;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="config_siponecore_logs")
public class SipOneCoreLogsDomain implements Serializable {
	
	private static final long serialVersionUID = 7930901889746716090L;

	@Id
	private String id;

	@Indexed(name = "_idTeste_01")
	private long idTeste;
	
	private String logGeral;
	
	public SipOneCoreLogsDomain() {}
	
	public SipOneCoreLogsDomain(long idTeste, String logGeral) {
		this.idTeste = idTeste;
		this.logGeral = logGeral;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getIdTeste() {
		return idTeste;
	}

	public void setIdTeste(long idTeste) {
		this.idTeste = idTeste;
	}

	public String getLogGeral() {
		return logGeral;
	}

	public void setLogGeral(String logGeral) {
		this.logGeral = logGeral;
	}

}
