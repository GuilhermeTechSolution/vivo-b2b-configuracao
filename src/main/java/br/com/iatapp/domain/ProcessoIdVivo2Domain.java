package br.com.iatapp.domain;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="vivo2_processos_robo_ids")
public class ProcessoIdVivo2Domain implements Serializable {

	private static final long serialVersionUID = -2495251064077496431L;

	@Id
	private String id;
	
	private Date dataEntradaFila;
	private Date dataInicio;
	private Date dataFinal;
	private String idProcessoAnterior;
	private String resultadoProcedimento;
	private String resultadoProcedimentoAnterior;
	private int idUsuario;
	private String nomeUsuario;
	
	private String designador;
	private String cidade;
	private String routerCliente;
	private String cn;
	private String cliente;
	private String ipv6WanBlock;
	private String ipv6LanBlock;
	private String ipv6WanSubnet;
	private String ipv6LanSubnet;
	private String ipv4LanBlock;
	private String ipv4WanBlock;
	private String idTbs;
	private String servico;
	private String fila;
	private String motivo;
	
	private boolean alocarBlocoIpv4Wan;
	private boolean alocarBlocoIpv4Lan;
	private boolean alocarBlocoIpv6Wan;
	private boolean alocarBlocoIpv6Lan;
	private boolean alocarSubnetIpv6Wan;
	private boolean alocarSubnetIpv6Lan;
	
	private boolean resultadoAlocarBlocoIpv4Lan;
	private boolean resultadoAlocarBlocoIpv4Wan;
	private boolean resultadoAlocarBlocoIpv6Wan;
	private boolean resultadoAlocarBlocoIpv6Lan;
	private boolean resultadoAlocarSubnetIpv6Wan;
	private boolean resultadoAlocarSubnetIpv6Lan;
	
	private long qtd;
	private boolean confManual;
	private int prioridade;
		
	public ProcessoIdVivo2Domain() {}
	
	@Transient
	public String getTzDataEntradaFila() {
		if (this.dataEntradaFila != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			sdf.setTimeZone(TimeZone.getTimeZone("America/Fortaleza"));
			return sdf.format(this.dataEntradaFila);
		}
		return "";
	}
	
	@Transient
	public String getTzDataInicio() {
		if (this.dataInicio != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			sdf.setTimeZone(TimeZone.getTimeZone("America/Fortaleza"));
			return sdf.format(this.dataInicio);
		}
		return "";
	}
	
	@Transient
	public String getTzDataFinal() {
		if (this.dataFinal != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			sdf.setTimeZone(TimeZone.getTimeZone("America/Fortaleza"));
			return sdf.format(this.dataFinal);
		}
		return "";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getDataEntradaFila() {
		return dataEntradaFila;
	}

	public void setDataEntradaFila(Date dataEntradaFila) {
		this.dataEntradaFila = dataEntradaFila;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

	public String getIdProcessoAnterior() {
		return idProcessoAnterior;
	}

	public void setIdProcessoAnterior(String idProcessoAnterior) {
		this.idProcessoAnterior = idProcessoAnterior;
	}

	public String getResultadoProcedimento() {
		return resultadoProcedimento;
	}

	public void setResultadoProcedimento(String resultadoProcedimento) {
		this.resultadoProcedimento = resultadoProcedimento;
	}

	public String getResultadoProcedimentoAnterior() {
		return resultadoProcedimentoAnterior;
	}

	public void setResultadoProcedimentoAnterior(String resultadoProcedimentoAnterior) {
		this.resultadoProcedimentoAnterior = resultadoProcedimentoAnterior;
	}

	public int getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}

	public String getNomeUsuario() {
		return nomeUsuario;
	}

	public void setNomeUsuario(String nomeUsuario) {
		this.nomeUsuario = nomeUsuario;
	}

	public String getDesignador() {
		return designador;
	}

	public void setDesignador(String designador) {
		this.designador = designador;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public String getRouterCliente() {
		return routerCliente;
	}

	public void setRouterCliente(String routerCliente) {
		this.routerCliente = routerCliente;
	}

	public String getCn() {
		return cn;
	}

	public void setCn(String cn) {
		this.cn = cn;
	}

	public String getIpv6LanBlock() {
		return ipv6LanBlock;
	}

	public void setIpv6LanBlock(String ipv6LanBlock) {
		this.ipv6LanBlock = ipv6LanBlock;
	}

	public String getCliente() {
		return cliente;
	}

	public void setCliente(String cliente) {
		this.cliente = cliente;
	}

	public String getIpv6WanBlock() {
		return ipv6WanBlock;
	}

	public void setIpv6WanBlock(String ipv6WanBlock) {
		this.ipv6WanBlock = ipv6WanBlock;
	}

	public String getIpv4LanBlock() {
		return ipv4LanBlock;
	}

	public void setIpv4LanBlock(String ipv4LanBlock) {
		this.ipv4LanBlock = ipv4LanBlock;
	}

	public String getIpv4WanBlock() {
		return ipv4WanBlock;
	}

	public void setIpv4WanBlock(String ipv4WanBlock) {
		this.ipv4WanBlock = ipv4WanBlock;
	}

	public String getIdTbs() {
		return idTbs;
	}

	public void setIdTbs(String idTbs) {
		this.idTbs = idTbs;
	}

	public String getServico() {
		return servico;
	}

	public void setServico(String servico) {
		this.servico = servico;
	}

	public boolean isAlocarBlocoIpv4Wan() {
		return alocarBlocoIpv4Wan;
	}

	public void setAlocarBlocoIpv4Wan(boolean alocarBlocoIpv4Wan) {
		this.alocarBlocoIpv4Wan = alocarBlocoIpv4Wan;
	}

	public boolean isAlocarBlocoIpv4Lan() {
		return alocarBlocoIpv4Lan;
	}

	public void setAlocarBlocoIpv4Lan(boolean alocarBlocoIpv4Lan) {
		this.alocarBlocoIpv4Lan = alocarBlocoIpv4Lan;
	}

	public boolean isAlocarBlocoIpv6Wan() {
		return alocarBlocoIpv6Wan;
	}

	public void setAlocarBlocoIpv6Wan(boolean alocarBlocoIpv6Wan) {
		this.alocarBlocoIpv6Wan = alocarBlocoIpv6Wan;
	}

	public boolean isAlocarBlocoIpv6Lan() {
		return alocarBlocoIpv6Lan;
	}

	public void setAlocarBlocoIpv6Lan(boolean alocarBlocoIpv6Lan) {
		this.alocarBlocoIpv6Lan = alocarBlocoIpv6Lan;
	}

	public boolean isAlocarSubnetIpv6Wan() {
		return alocarSubnetIpv6Wan;
	}

	public void setAlocarSubnetIpv6Wan(boolean alocarSubnetIpv6Wan) {
		this.alocarSubnetIpv6Wan = alocarSubnetIpv6Wan;
	}

	public boolean isAlocarSubnetIpv6Lan() {
		return alocarSubnetIpv6Lan;
	}

	public void setAlocarSubnetIpv6Lan(boolean alocarSubnetIpv6Lan) {
		this.alocarSubnetIpv6Lan = alocarSubnetIpv6Lan;
	}

	public boolean isResultadoAlocarBlocoIpv4Lan() {
		return resultadoAlocarBlocoIpv4Lan;
	}

	public void setResultadoAlocarBlocoIpv4Lan(boolean resultadoAlocarBlocoIpv4Lan) {
		this.resultadoAlocarBlocoIpv4Lan = resultadoAlocarBlocoIpv4Lan;
	}

	public boolean isResultadoAlocarBlocoIpv4Wan() {
		return resultadoAlocarBlocoIpv4Wan;
	}

	public void setResultadoAlocarBlocoIpv4Wan(boolean resultadoAlocarBlocoIpv4Wan) {
		this.resultadoAlocarBlocoIpv4Wan = resultadoAlocarBlocoIpv4Wan;
	}

	public boolean isResultadoAlocarBlocoIpv6Wan() {
		return resultadoAlocarBlocoIpv6Wan;
	}

	public void setResultadoAlocarBlocoIpv6Wan(boolean resultadoAlocarBlocoIpv6Wan) {
		this.resultadoAlocarBlocoIpv6Wan = resultadoAlocarBlocoIpv6Wan;
	}

	public boolean isResultadoAlocarBlocoIpv6Lan() {
		return resultadoAlocarBlocoIpv6Lan;
	}

	public void setResultadoAlocarBlocoIpv6Lan(boolean resultadoAlocarBlocoIpv6Lan) {
		this.resultadoAlocarBlocoIpv6Lan = resultadoAlocarBlocoIpv6Lan;
	}

	public boolean isResultadoAlocarSubnetIpv6Wan() {
		return resultadoAlocarSubnetIpv6Wan;
	}

	public void setResultadoAlocarSubnetIpv6Wan(boolean resultadoAlocarSubnetIpv6Wan) {
		this.resultadoAlocarSubnetIpv6Wan = resultadoAlocarSubnetIpv6Wan;
	}

	public boolean isResultadoAlocarSubnetIpv6Lan() {
		return resultadoAlocarSubnetIpv6Lan;
	}

	public void setResultadoAlocarSubnetIpv6Lan(boolean resultadoAlocarSubnetIpv6Lan) {
		this.resultadoAlocarSubnetIpv6Lan = resultadoAlocarSubnetIpv6Lan;
	}

	public long getQtd() {
		return qtd;
	}

	public void setQtd(long qtd) {
		this.qtd = qtd;
	}

	public boolean isConfManual() {
		return confManual;
	}

	public void setConfManual(boolean confManual) {
		this.confManual = confManual;
	}

	public int getPrioridade() {
		return prioridade;
	}

	public void setPrioridade(int prioridade) {
		this.prioridade = prioridade;
	}

	public String getFila() {
		return fila;
	}

	public void setFila(String fila) {
		this.fila = fila;
	}

	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	public String getIpv6WanSubnet() {
		return ipv6WanSubnet;
	}

	public void setIpv6WanSubnet(String ipv6WanSubnet) {
		this.ipv6WanSubnet = ipv6WanSubnet;
	}

	public String getIpv6LanSubnet() {
		return ipv6LanSubnet;
	}

	public void setIpv6LanSubnet(String ipv6LanSubnet) {
		this.ipv6LanSubnet = ipv6LanSubnet;
	}
	
}
