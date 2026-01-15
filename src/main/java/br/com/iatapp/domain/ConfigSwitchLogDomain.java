package br.com.iatapp.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="configuracao_switch_logs")
public class ConfigSwitchLogDomain {
	
	@Id
	private String id;

	@Indexed(name = "_idConfSwitch_01")
	private String idConfSwitch;
	
	private String logGeral;
	
	public ConfigSwitchLogDomain() {}
	
	public ConfigSwitchLogDomain(String idConfSwitch, String logGeral) {
		this.idConfSwitch = idConfSwitch;
		this.logGeral = logGeral;
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

	public String getLogGeral() {
		return logGeral;
	}

	public void setLogGeral(String logGeral) {
		this.logGeral = logGeral;
	}

}
