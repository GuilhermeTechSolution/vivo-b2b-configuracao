package br.com.iatapp.domain;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.json.JSONObject;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.com.iatapp.rede.RedeIpFunctions;

@Document(collection="config_siponecore_processos_ids")
public class SipOneCoreProcessoIdDomain implements Serializable {
	
	private static final long serialVersionUID = 6297245621829797871L;

	@Id
	private String id;
	
	@Indexed(name = "_idTeste_01")
	private long idTeste;
	
	@Indexed(name = "_idVantive_01")
	private String idVantive;
	
	private String token;
	private String tokenFolder;
	private boolean scriptsOnly;
	private boolean migracao;
	
	private Date dataEntradaFila;
	private Date dataInicio;
	private Date dataFinal;
	private String idProcessoAnterior;
	private String produto;
	private String dadosStarStr;
	private String dadosSbcIps;
	private String dadosTxtStar;
	private String resultadoProcedimento;
	private String resultadoProcedimentoAnterior;
	private int tipoProcedimento;
	private String nomeTipoProcedimento;
	private String troncoChavePiloto;
	private String ramais;
	private String ramaisAdicionais;
	private String dominio;
	private String location;
	private String callsource;
	private String pabxid;
	private String qtdeCanais;
	private String ipCliente;
	private String tgid;
	private String ipSbcPrincipal;
	private String ipSbcRedundante;
	
	// Elementos IMS
	private String imsSITE;
	
	// Elementos IMS HSS
	private String imsHssSIFCID;
	private String imsHssSIFCID2;
	
	private int idUsuario;
	private String nomeUsuario;
	
	@JsonIgnore
	@Transient
	private RedeIpFunctions redeIpFunctions;
	@Transient
	private String valorProgressBar;
	@Transient
	private String etapaTesteOnline;
	@Transient
	private String logExecucao;
	@Transient
	private String mensagemErroConexaoServidor;
	
	@Transient
	private int modeloSbcPrimario;
	@Transient
	private int modeloSbcSecundario;
	@Transient
	private String hostSbcPrimario;
	@Transient
	private String hostSbcSecundario;
	@Transient
	private StringBuilder scriptSbcPrimario;
	@Transient
	private StringBuilder scriptSbcSecundario;
	@Transient
	private StringBuilder scriptRemocaoSbcPrimario;
	@Transient
	private StringBuilder scriptRemocaoSbcSecundario;
	@Transient
	private String scriptBcf;
	@Transient
	private String scriptRemocaoBcf;
	@Transient
	private JSONObject jsonDados;
		
	public SipOneCoreProcessoIdDomain() {
		setJsonDados(new JSONObject());
	}
	
	public String getTzDataIncio() {
		if (this.dataInicio != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			sdf.setTimeZone(TimeZone.getTimeZone("America/Fortaleza"));
			return sdf.format(this.dataInicio);
		}
		return "";
	}
	
	public String getTzDataFinal() {
		if (this.dataFinal != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			sdf.setTimeZone(TimeZone.getTimeZone("America/Fortaleza"));
			return sdf.format(this.dataFinal);
		}
		return "";
	}
	
	@Transient
	public String getDuracaoStr() {
		
		if(getDataInicio() == null || getDataFinal() == null)
			return "";
		
		long durationMillis = getDataFinal().getTime() - getDataInicio().getTime();
		return DurationFormatUtils.formatDuration(durationMillis, "HH:mm:ss");
	}
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getProduto() {
		return produto;
	}

	public void setProduto(String produto) {
		this.produto = produto;
	}

	public Date getDataEntradaFila() {
		return dataEntradaFila;
	}

	public void setDataEntradaFila(Date dataEntradaFila) {
		this.dataEntradaFila = dataEntradaFila;
	}	

	public String getResultadoProcedimentoAnterior() {
		return resultadoProcedimentoAnterior;
	}

	public void setResultadoProcedimentoAnterior(String resultadoProcedimentoAnterior) {
		this.resultadoProcedimentoAnterior = resultadoProcedimentoAnterior;
	}

	public String getIdProcessoAnterior() {
		return idProcessoAnterior;
	}

	public void setIdProcessoAnterior(String idProcessoAnterior) {
		this.idProcessoAnterior = idProcessoAnterior;
	}


	public String getDadosSbcIps() {
		return dadosSbcIps;
	}


	public void setDadosSbcIps(String dadosSbcIps) {
		this.dadosSbcIps = dadosSbcIps;
	}


	public String getDadosTxtStar() {
		return dadosTxtStar;
	}


	public void setDadosTxtStar(String dadosTxtStar) {
		this.dadosTxtStar = dadosTxtStar;
	}


	public String getToken() {
		return token;
	}


	public void setToken(String token) {
		this.token = token;
	}


	public String getTokenFolder() {
		return tokenFolder;
	}


	public void setTokenFolder(String tokenFolder) {
		this.tokenFolder = tokenFolder;
	}


	public String getValorProgressBar() {
		return valorProgressBar;
	}


	public void setValorProgressBar(String valorProgressBar) {
		this.valorProgressBar = valorProgressBar;
	}


	public String getEtapaTesteOnline() {
		return etapaTesteOnline;
	}


	public void setEtapaTesteOnline(String etapaTesteOnline) {
		this.etapaTesteOnline = etapaTesteOnline;
	}


	public int getTipoProcedimento() {
		return tipoProcedimento;
	}


	public void setTipoProcedimento(int tipoProcedimento) {
		this.tipoProcedimento = tipoProcedimento;
	}


	public String getNomeTipoProcedimento() {
		return nomeTipoProcedimento;
	}


	public void setNomeTipoProcedimento(String nomeTipoProcedimento) {
		this.nomeTipoProcedimento = nomeTipoProcedimento;
	}


	public String getTroncoChavePiloto() {
		return troncoChavePiloto;
	}


	public void setTroncoChavePiloto(String troncoChavePiloto) {
		this.troncoChavePiloto = troncoChavePiloto;
	}


	public String getRamais() {
		return ramais;
	}


	public void setRamais(String ramais) {
		this.ramais = ramais;
	}


	public String getRamaisAdicionais() {
		return ramaisAdicionais;
	}


	public void setRamaisAdicionais(String ramaisAdicionais) {
		this.ramaisAdicionais = ramaisAdicionais;
	}


	public String getDominio() {
		return dominio;
	}


	public void setDominio(String dominio) {
		this.dominio = dominio;
	}


	public String getLocation() {
		return location;
	}


	public void setLocation(String location) {
		this.location = location;
	}


	public String getCallsource() {
		return callsource;
	}


	public void setCallsource(String callsource) {
		this.callsource = callsource;
	}


	public String getPabxid() {
		return pabxid;
	}


	public void setPabxid(String pabxid) {
		this.pabxid = pabxid;
	}


	public String getQtdeCanais() {
		return qtdeCanais;
	}


	public void setQtdeCanais(String qtdeCanais) {
		this.qtdeCanais = qtdeCanais;
	}


	public String getIpCliente() {
		return ipCliente;
	}


	public void setIpCliente(String ipCliente) {
		this.ipCliente = ipCliente;
	}


	public String getTgid() {
		return tgid;
	}


	public void setTgid(String tgid) {
		this.tgid = tgid;
	}


	public String getIpSbcPrincipal() {
		return ipSbcPrincipal;
	}


	public void setIpSbcPrincipal(String ipSbcPrincipal) {
		this.ipSbcPrincipal = ipSbcPrincipal;
	}


	public String getIpSbcRedundante() {
		return ipSbcRedundante;
	}


	public void setIpSbcRedundante(String ipSbcRedundante) {
		this.ipSbcRedundante = ipSbcRedundante;
	}


	public String getImsHssSIFCID() {
		return imsHssSIFCID;
	}


	public void setImsHssSIFCID(String imsHssSIFCID) {
		this.imsHssSIFCID = imsHssSIFCID;
	}


	public String getImsHssSIFCID2() {
		return imsHssSIFCID2;
	}


	public void setImsHssSIFCID2(String imsHssSIFCID2) {
		this.imsHssSIFCID2 = imsHssSIFCID2;
	}


	public String getImsSITE() {
		return imsSITE;
	}


	public void setImsSITE(String imsSITE) {
		this.imsSITE = imsSITE;
	}
	
	
	public RedeIpFunctions getRedeIpFunctions() {
		return redeIpFunctions;
	}


	public void setRedeIpFunctions(RedeIpFunctions redeIpFunctions) {
		this.redeIpFunctions = redeIpFunctions;
	}


	public String getMensagemErroConexaoServidor() {
		return mensagemErroConexaoServidor;
	}


	public void setMensagemErroConexaoServidor(String mensagemErroConexaoServidor) {
		this.mensagemErroConexaoServidor = mensagemErroConexaoServidor;
	}


	public String getLogExecucao() {
		return logExecucao;
	}


	public void setLogExecucao(String logExecucao) {
		this.logExecucao = logExecucao;
	}


	public int getModeloSbcPrimario() {
		return modeloSbcPrimario;
	}


	public void setModeloSbcPrimario(int modeloSbcPrimario) {
		this.modeloSbcPrimario = modeloSbcPrimario;
	}


	public int getModeloSbcSecundario() {
		return modeloSbcSecundario;
	}


	public void setModeloSbcSecundario(int modeloSbcSecundario) {
		this.modeloSbcSecundario = modeloSbcSecundario;
	}


	public String getScriptBcf() {
		return scriptBcf;
	}


	public void setScriptBcf(String scriptBcf) {
		this.scriptBcf = scriptBcf;
	}


	public String getHostSbcPrimario() {
		return hostSbcPrimario;
	}


	public void setHostSbcPrimario(String hostSbcPrimario) {
		this.hostSbcPrimario = hostSbcPrimario;
	}


	public String getHostSbcSecundario() {
		return hostSbcSecundario;
	}


	public void setHostSbcSecundario(String hostSbcSecundario) {
		this.hostSbcSecundario = hostSbcSecundario;
	}


	public StringBuilder getScriptSbcPrimario() {
		return scriptSbcPrimario;
	}


	public void setScriptSbcPrimario(StringBuilder scriptSbcPrimario) {
		this.scriptSbcPrimario = scriptSbcPrimario;
	}


	public StringBuilder getScriptSbcSecundario() {
		return scriptSbcSecundario;
	}


	public void setScriptSbcSecundario(StringBuilder scriptSbcSecundario) {
		this.scriptSbcSecundario = scriptSbcSecundario;
	}


	public long getIdTeste() {
		return idTeste;
	}


	public void setIdTeste(long idTeste) {
		this.idTeste = idTeste;
	}

	public boolean isScriptsOnly() {
		return scriptsOnly;
	}

	public void setScriptsOnly(boolean scriptsOnly) {
		this.scriptsOnly = scriptsOnly;
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

	public StringBuilder getScriptRemocaoSbcPrimario() {
		return scriptRemocaoSbcPrimario;
	}

	public void setScriptRemocaoSbcPrimario(StringBuilder scriptRemocaoSbcPrimario) {
		this.scriptRemocaoSbcPrimario = scriptRemocaoSbcPrimario;
	}

	public StringBuilder getScriptRemocaoSbcSecundario() {
		return scriptRemocaoSbcSecundario;
	}

	public void setScriptRemocaoSbcSecundario(StringBuilder scriptRemocaoSbcSecundario) {
		this.scriptRemocaoSbcSecundario = scriptRemocaoSbcSecundario;
	}

	public String getScriptRemocaoBcf() {
		return scriptRemocaoBcf;
	}

	public void setScriptRemocaoBcf(String scriptRemocaoBcf) {
		this.scriptRemocaoBcf = scriptRemocaoBcf;
	}

	public JSONObject getJsonDados() {
		return jsonDados;
	}

	public void setJsonDados(JSONObject jsonDados) {
		this.jsonDados = jsonDados;
	}

	public boolean isMigracao() {
		return migracao;
	}

	public void setMigracao(boolean migracao) {
		this.migracao = migracao;
	}
	
}
