package br.com.iatapp.domain;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import br.com.iatapp.enums.TecnologiasTestesEnum;

@Document(collection="configuracao_processos_ids")
public class ProcessoIdDomain  implements Serializable {

	private static final long serialVersionUID = 5038586936446284445L;
	
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
	private String resultadosCheckDuplicidade;
	private String resultadosCheckConfiguracao;
	private String resultadosFinalizarTarefasStar;
	private String resultadosFinalizarTarefasSae;
	private String resultadoConfig;
	private String resultadoProcedimento;
	private String resultadoProcedimentoAnterior;
	private String alertaConfig;
	private String movimentouTarefaPendenciaSae;
	private String tipoAtividade;
	private boolean idAlteracao;
	private int idUsuario;
	private String nomeUsuario;
		
	private long qtd;
	private boolean confManual;
	private int prioridade;
	
	public ProcessoIdDomain() {
	}
	
	@Transient
	public void resetProcesso() {
		// Reset campos que serao preenchidos dinamicamente
		this.dataFinal = null;
		this.dadosStarStr = null;
		this.resultadosCheckDuplicidade = null;
		this.resultadosCheckConfiguracao = null;
		this.resultadosFinalizarTarefasStar = null;
		this.resultadosFinalizarTarefasSae = null;
		this.resultadoConfig = null;
		this.alertaConfig = null;
	}
	
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
	
	@Transient
	public String getDslamOltInfo() {
		if (StringUtils.isNotBlank(dadosStarStr)) {
			try {
				JSONObject aux = new JSONObject(dadosStarStr);
				if(aux.getInt("idTecnologia") == TecnologiasTestesEnum.PRAPS.getCodigo()) {
					if(StringUtils.isNotBlank(aux.optString("dslam", "")))
						return String.format("%s.%s.%s", aux.getString("dslam"), aux.getString("dslamSlot"), aux.getString("dslamPortaMaster"));
				} else if(aux.getInt("idTecnologia") == TecnologiasTestesEnum.GPON.getCodigo()) {
					if(StringUtils.isNotBlank(aux.optString("olt", "")))
						return aux.getString("olt");
				}
					
			} catch (Exception e) { }
				
		}
		return "";
	}
	
	@Transient
	public String getTecnologiaStr() {
		if (StringUtils.isNotBlank(dadosStarStr)) {
			try {
				JSONObject aux = new JSONObject(dadosStarStr);
				switch (TecnologiasTestesEnum.valueOf(aux.getInt("idTecnologia"))) {
				case PRAPS:
					return TecnologiasTestesEnum.PRAPS.getNome();
				case GPON:
					return TecnologiasTestesEnum.GPON.getNome();
				case SWT:
					return TecnologiasTestesEnum.SWT.getNome();
				default:
					break;
				}
			} catch (Exception e) { }
		}
		return "";
	}
	
	@Transient
	public String getTipoVendaStr() {
		if (StringUtils.isNotBlank(dadosStarStr)) {
			try {
				JSONObject aux = new JSONObject(dadosStarStr);
				return aux.getString("tipoProcesso");
			} catch (Exception e) { }
		}
		return "";
	}
	
	@Transient
	public String getRaPrincipalStr() {
		if (StringUtils.isNotBlank(dadosStarStr)) {
			try {
				JSONObject aux = new JSONObject(dadosStarStr);
				return aux.getString("pePrincipal");
			} catch (Exception e) { }
		}
		return "";
	}
	
	@Transient
	public String getRaBackupStr() {
		if (StringUtils.isNotBlank(dadosStarStr)) {
			try {
				JSONObject aux = new JSONObject(dadosStarStr);
				return aux.getString("peBackup");
			} catch (Exception e) { }
		}
		return "";
	}
	
	@Transient
	public String getMotivoStr() {
		if (StringUtils.isNotBlank(resultadosCheckConfiguracao)) {
			try {
				JSONObject aux = new JSONObject(resultadosCheckConfiguracao);
				return aux.getString("motivo");
			} catch (Exception e) { }
		}
		return "";
	}
	
	@Transient
	public String getIpLanStr() {
		if (StringUtils.isNotBlank(dadosStarStr)) {
			try {
				JSONObject aux = new JSONObject(dadosStarStr);
				return aux.getString("ipLanCpe");
			} catch (Exception e) { }
		}
		return "";
	}
	
	@Transient
	public String getIpLoopbackStr() {
		if (StringUtils.isNotBlank(dadosStarStr)) {
			try {
				JSONObject aux = new JSONObject(dadosStarStr);
				return aux.getString("ipLoopback");
			} catch (Exception e) { }
		}
		return "";
	}
	
	@Transient
	public String getIpWanPeStr() {
		if (StringUtils.isNotBlank(dadosStarStr)) {
			try {
				JSONObject aux = new JSONObject(dadosStarStr);
				return aux.getString("ipWanPe");
			} catch (Exception e) { }
		}
		return "";
	}
	
	@Transient
	public String getInterfacePePrincipalStr() {
		if (StringUtils.isNotBlank(dadosStarStr)) {
			try {
				JSONObject aux = new JSONObject(dadosStarStr);
				return aux.getString("interfacePePrincipal");
			} catch (Exception e) { }
		}
		return "";
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

	public String getDadosStarStr() {
		return dadosStarStr;
	}

	public void setDadosStarStr(String dadosStarStr) {
		this.dadosStarStr = dadosStarStr;
	}

	public String getResultadosCheckDuplicidade() {
		return resultadosCheckDuplicidade;
	}

	public void setResultadosCheckDuplicidade(String resultadosCheckDuplicidade) {
		this.resultadosCheckDuplicidade = resultadosCheckDuplicidade;
	}

	public String getResultadoProcedimento() {
		return resultadoProcedimento;
	}

	public void setResultadoProcedimento(String resultadoProcedimento) {
		this.resultadoProcedimento = resultadoProcedimento;
	}

	public String getResultadosCheckConfiguracao() {
		return resultadosCheckConfiguracao;
	}

	public void setResultadosCheckConfiguracao(String resultadosCheckConfiguracao) {
		this.resultadosCheckConfiguracao = resultadosCheckConfiguracao;
	}

	public String getResultadosFinalizarTarefasStar() {
		return resultadosFinalizarTarefasStar;
	}

	public void setResultadosFinalizarTarefasStar(String resultadosFinalizarTarefasStar) {
		this.resultadosFinalizarTarefasStar = resultadosFinalizarTarefasStar;
	}

	public String getResultadosFinalizarTarefasSae() {
		return resultadosFinalizarTarefasSae;
	}

	public void setResultadosFinalizarTarefasSae(String resultadosFinalizarTarefasSae) {
		this.resultadosFinalizarTarefasSae = resultadosFinalizarTarefasSae;
	}

	public String getResultadoConfig() {
		return resultadoConfig;
	}

	public void setResultadoConfig(String resultadoConfig) {
		this.resultadoConfig = resultadoConfig;
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

	public String getAlertaConfig() {
		return alertaConfig;
	}

	public void setAlertaConfig(String alertaConfig) {
		this.alertaConfig = alertaConfig;
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

	public long getQtd() {
		return qtd;
	}

	public void setQtd(long qtd) {
		this.qtd = qtd;
	}

	public String getMovimentouTarefaPendenciaSae() {
		return movimentouTarefaPendenciaSae;
	}

	public void setMovimentouTarefaPendenciaSae(String movimentouTarefaPendenciaSae) {
		this.movimentouTarefaPendenciaSae = movimentouTarefaPendenciaSae;
	}

	public boolean isIdAlteracao() {
		return idAlteracao;
	}

	public void setIdAlteracao(boolean idAlteracao) {
		this.idAlteracao = idAlteracao;
	}

	public int getPrioridade() {
		return prioridade;
	}

	public void setPrioridade(int prioridade) {
		this.prioridade = prioridade;
	}

	public boolean isConfManual() {
		return confManual;
	}

	public void setConfManual(boolean confManual) {
		this.confManual = confManual;
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

	public String getTipoAtividade() {
		return tipoAtividade;
	}

	public void setTipoAtividade(String tipoAtividade) {
		this.tipoAtividade = tipoAtividade;
	}
	
}
