package br.com.iatapp.domain;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="migracao_processos_ids")
public class MigracaoVivoSipDomain implements Serializable {

	private static final long serialVersionUID = -3043296389750538749L;
	
	@Id
	private String id;
	
	private Date dataEntradaFila;
	private Date dataInicio;
	private Date dataFinal;
	private String idProcessoAnterior;
	private String idVantive;
	private String produto;
	private String dadosStarStr;
	private String resultadoProcedimento;
	private String resultadoProcedimentoAnterior;
	private String logDadosRede;
	private String pingPreConfig;
	private String pingPosConfig;
	private String logPreConfigBackbone;
	private String logPosConfigBackbone;
	private String logConfigSbc;
	private boolean idAlteracao;
	private int idUsuario;
	private String nomeUsuario;
	
	private int qtd;
	private boolean confManual;
	private int prioridade;
		
	public MigracaoVivoSipDomain() {}
	
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
	
	public String getIdVantive() {
		return idVantive;
	}

	public void setIdVantive(String idVantive) {
		this.idVantive = idVantive;
	}

	public String getProduto() {
		return produto;
	}

	public void setProduto(String produto) {
		this.produto = produto;
	}

	public String getDadosStarStr() {
		return dadosStarStr;
	}

	public void setDadosStarStr(String dadosStarStr) {
		this.dadosStarStr = dadosStarStr;
	}

	public String getResultadoProcedimento() {
		return resultadoProcedimento;
	}

	public void setResultadoProcedimento(String resultadoProcedimento) {
		this.resultadoProcedimento = resultadoProcedimento;
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

	public String getIdProcessoAnterior() {
		return idProcessoAnterior;
	}

	public void setIdProcessoAnterior(String idProcessoAnterior) {
		this.idProcessoAnterior = idProcessoAnterior;
	}

	public String getResultadoProcedimentoAnterior() {
		return resultadoProcedimentoAnterior;
	}

	public void setResultadoProcedimentoAnterior(String resultadoProcedimentoAnterior) {
		this.resultadoProcedimentoAnterior = resultadoProcedimentoAnterior;
	}

	public boolean isIdAlteracao() {
		return idAlteracao;
	}

	public void setIdAlteracao(boolean idAlteracao) {
		this.idAlteracao = idAlteracao;
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

	public String getLogPreConfigBackbone() {
		return logPreConfigBackbone;
	}

	public void setLogPreConfigBackbone(String logPreConfigBackbone) {
		this.logPreConfigBackbone = logPreConfigBackbone;
	}

	public String getLogPosConfigBackbone() {
		return logPosConfigBackbone;
	}

	public void setLogPosConfigBackbone(String logPosConfigBackbone) {
		this.logPosConfigBackbone = logPosConfigBackbone;
	}

	public String getLogConfigSbc() {
		return logConfigSbc;
	}

	public void setLogConfigSbc(String logConfigSbc) {
		this.logConfigSbc = logConfigSbc;
	}

	public int getQtd() {
		return qtd;
	}

	public void setQtd(int qtd) {
		this.qtd = qtd;
	}

	public String getLogDadosRede() {
		return logDadosRede;
	}

	public void setLogDadosRede(String logDadosRede) {
		this.logDadosRede = logDadosRede;
	}

	public String getPingPreConfig() {
		return pingPreConfig;
	}

	public void setPingPreConfig(String pingPreConfig) {
		this.pingPreConfig = pingPreConfig;
	}

	public String getPingPosConfig() {
		return pingPosConfig;
	}

	public void setPingPosConfig(String pingPosConfig) {
		this.pingPosConfig = pingPosConfig;
	}

}
