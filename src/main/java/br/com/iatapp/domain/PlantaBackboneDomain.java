package br.com.iatapp.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="coleta_backbone")
public class PlantaBackboneDomain implements Serializable {

	private static final long serialVersionUID = -5719779659439939744L;
	
	@Id
	private String id;
	
	@Indexed(name = "_idxBackbone")
	private String backbone;
	
	@Indexed(name = "_idxInterfaceBackbone")
	private String interfaceBackbone;
	
	private String idVantive;
	private String ipWanPe;
	private String ipWanCpe;
	private List<String> ipLoopback;
	private List<String> rotasLoopback;
	private String description;
	private String status;
	private String protocol;
	private String vrf;
	private int idModelo;
	private String nomeModelo;
	private String tipoBackbone;
	private Date dataAtualizacao;
	private String logExecucao;
		
	public PlantaBackboneDomain() {}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBackbone() {
		return backbone;
	}

	public void setBackbone(String backbone) {
		this.backbone = backbone;
	}

	public String getInterfaceBackbone() {
		return interfaceBackbone;
	}

	public void setInterfaceBackbone(String interfaceBackbone) {
		this.interfaceBackbone = interfaceBackbone;
	}

	public String getIdVantive() {
		return idVantive;
	}

	public void setIdVantive(String idVantive) {
		this.idVantive = idVantive;
	}

	public String getIpWanPe() {
		return ipWanPe;
	}

	public void setIpWanPe(String ipWanPe) {
		this.ipWanPe = ipWanPe;
	}

	public String getIpWanCpe() {
		return ipWanCpe;
	}

	public void setIpWanCpe(String ipWanCpe) {
		this.ipWanCpe = ipWanCpe;
	}

	public List<String> getIpLoopback() {
		return ipLoopback;
	}

	public void setIpLoopback(List<String> ipLoopback) {
		this.ipLoopback = ipLoopback;
	}

	public List<String> getRotasLoopback() {
		return rotasLoopback;
	}

	public void setRotasLoopback(List<String> rotasLoopback) {
		this.rotasLoopback = rotasLoopback;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getVrf() {
		return vrf;
	}

	public void setVrf(String vrf) {
		this.vrf = vrf;
	}

	public int getIdModelo() {
		return idModelo;
	}

	public void setIdModelo(int idModelo) {
		this.idModelo = idModelo;
	}

	public String getNomeModelo() {
		return nomeModelo;
	}

	public void setNomeModelo(String nomeModelo) {
		this.nomeModelo = nomeModelo;
	}

	public String getTipoBackbone() {
		return tipoBackbone;
	}

	public void setTipoBackbone(String tipoBackbone) {
		this.tipoBackbone = tipoBackbone;
	}

	public Date getDataAtualizacao() {
		return dataAtualizacao;
	}

	public void setDataAtualizacao(Date dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}

	public String getLogExecucao() {
		return logExecucao;
	}

	public void setLogExecucao(String logExecucao) {
		this.logExecucao = logExecucao;
	}

}
