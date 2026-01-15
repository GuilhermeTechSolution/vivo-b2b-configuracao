package br.com.iatapp.domain;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="retirada_processos_ids")
public class ProcessoIdRetiradaDomain implements Serializable {
	
	private static final long serialVersionUID = 2162729413793253513L;

	@Id
	private String id;
	
	private Date dataEntradaFila;
	private Date dataInicio;
	private Date dataFinal;
	private String idProcessoPrincipal;
	private String idProcessoAnterior;
	private String idVantive;
	private String produto;
	private String dadosStarStr;
	private String dadosStarIdAnteriorStr;
	private String resultadoProcedimento;
	private String resultadoProcedimentoAnterior;
	private String resultadosFinalizarTarefasStar;
	private String nomeTarefaStar;
	private String logExecucao;
	private boolean idAlteracao;
	private boolean possuiIpWan;
	private int idUsuario;
	private String nomeUsuario;
	
	private long qtd;
	private boolean confManual;
	private int prioridade;
		
	public ProcessoIdRetiradaDomain() {}
	
	@Transient
	public String getTzDataInicio() {
		if (this.dataInicio != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			sdf.setTimeZone(TimeZone.getTimeZone("America/Fortaleza"));
			return sdf.format(this.dataInicio);
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

	public String getIdProcessoPrincipal() {
		return idProcessoPrincipal;
	}

	public void setIdProcessoPrincipal(String idProcessoPrincipal) {
		this.idProcessoPrincipal = idProcessoPrincipal;
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

	public String getNomeTarefaStar() {
		return nomeTarefaStar;
	}

	public void setNomeTarefaStar(String nomeTarefaStar) {
		this.nomeTarefaStar = nomeTarefaStar;
	}

	public String getLogExecucao() {
		return logExecucao;
	}

	public void setLogExecucao(String logExecucao) {
		this.logExecucao = logExecucao;
	}

	public String getResultadosFinalizarTarefasStar() {
		return resultadosFinalizarTarefasStar;
	}

	public void setResultadosFinalizarTarefasStar(String resultadosFinalizarTarefasStar) {
		this.resultadosFinalizarTarefasStar = resultadosFinalizarTarefasStar;
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

	public String getDadosStarIdAnteriorStr() {
		return dadosStarIdAnteriorStr;
	}

	public void setDadosStarIdAnteriorStr(String dadosStarIdAnteriorStr) {
		this.dadosStarIdAnteriorStr = dadosStarIdAnteriorStr;
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

	public boolean isPossuiIpWan() {
		return possuiIpWan;
	}

	public void setPossuiIpWan(boolean possuiIpWan) {
		this.possuiIpWan = possuiIpWan;
	}

}
