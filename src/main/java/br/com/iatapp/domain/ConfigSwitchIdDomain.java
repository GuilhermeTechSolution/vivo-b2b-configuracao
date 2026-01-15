package br.com.iatapp.domain;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.json.JSONObject;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import br.com.iatapp.enums.TiposTestesEnum;
import br.com.iatapp.helper.StringHelper;
import br.com.iatapp.logger.GravaLogResultadoSwitch;
import br.com.iatapp.model.UsuarioModel;
import br.com.iatapp.rede.RedeIpFunctions;

@Document(collection="configuracao_switch_ids")
public class ConfigSwitchIdDomain implements Serializable {

	private static final long serialVersionUID = 7742272810049195948L;

	@Id
	private String id;	

	@Indexed(name = "_idVantive_01")
	private String idVantive;
	
	private String ipSwa;
	
	@Indexed(name = "_hostnameSwa_01")
	private String hostnameSwa;
	
	private Date dataInicio;
	private Date dataFinal;
	private String token;
	private String servicoCliente;
	private String sVlan;
	private String cVlan;
	private String tipoInterface;
	private String uplinkLag;
	private String portaCliente;
	private String nomeCliente;
	private String velocidadeLink;
	private String atp;
	private String nomeCentral;
	private String numeroSwitch;
	private String ipSwt;
	private String vlanGerenciaSwa;
	private int idUsuario;
	private String nomeUsuario;
	private int idModeloEquipamento;
	private String nomeModeloEquipamento;
	
	@Transient
	private String valorProgressBar;
	@Transient
	private String etapaTesteOnline;
	@Transient
	private UsuarioModel usuarioSenhas;
	@Transient
	private RedeIpFunctions redeIpFunctions;
	@Transient
	private JSONObject jsonDados;
	@Transient
	private String mensagemErroConexaoServidor;
	@Transient
	private GravaLogResultadoSwitch gravaLogResultado;
	@Transient
	private TiposTestesEnum tipoTeste;
	
	public ConfigSwitchIdDomain() {
		setValorProgressBar("5");
		setJsonDados(new JSONObject());		
		setGravaLogResultado(new GravaLogResultadoSwitch());
	}
	
	@Transient
	public String getDuracaoStr() {		
		if(getDataInicio() == null || getDataFinal() == null)
			return "";		
		long durationMillis = getDataFinal().getTime() - getDataInicio().getTime();
		return DurationFormatUtils.formatDuration(durationMillis, "HH:mm:ss");
	}

	@Transient
	public int getResultado(int codTeste, int codProcedimento) {
		if (gravaLogResultado == null)
			return -1;
		return gravaLogResultado.getResultado(codTeste, codProcedimento);
	}
	
	// Retorna Descrição do resultado, quando necessário
	@Transient
	public String getRetornoDescricao(int codTeste, int codProcedimento) {
		if (gravaLogResultado == null)
			return "";
		return gravaLogResultado.getRetornoDescricao(codTeste, codProcedimento);
	}
	
	// Retorna log do resultado
	@Transient
	public String getLogResultado(int codTeste, int codProcedimento) {
		if (gravaLogResultado == null)
			return "";
		return StringHelper.asciiToHtml(gravaLogResultado.getLog(codTeste, codProcedimento));
	}
	
	@Transient
	public String getIdJoinTesteProcedimento(int codTeste, int codProcedimento) {
		return String.format("log_id_%d_%d", codTeste, codProcedimento);
	}
	
	@Transient
	public String getIdJoinTesteProcedimentoResultado(int codTeste, int codProcedimento) {
		return String.format("log_id_%d_%d_result", codTeste, codProcedimento);
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

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getIdVantive() {
		return idVantive;
	}

	public void setIdVantive(String idVantive) {
		this.idVantive = idVantive;
	}	

	public String getIpSwa() {
		return ipSwa;
	}

	public void setIpSwa(String ipSwa) {
		this.ipSwa = ipSwa;
	}

	public String getHostnameSwa() {
		return hostnameSwa;
	}

	public void setHostnameSwa(String hostnameSwa) {
		this.hostnameSwa = hostnameSwa;
	}

	public String getsVlan() {
		return sVlan;
	}

	public void setsVlan(String sVlan) {
		this.sVlan = sVlan;
	}

	public String getcVlan() {
		return cVlan;
	}

	public void setcVlan(String cVlan) {
		this.cVlan = cVlan;
	}

	public String getUplinkLag() {
		return uplinkLag;
	}

	public void setUplinkLag(String uplinkLag) {
		this.uplinkLag = uplinkLag;
	}

	public String getPortaCliente() {
		return portaCliente;
	}

	public void setPortaCliente(String portaCliente) {
		this.portaCliente = portaCliente;
	}

	public String getNomeCliente() {
		return nomeCliente;
	}

	public void setNomeCliente(String nomeCliente) {
		this.nomeCliente = nomeCliente;
	}

	public String getVelocidadeLink() {
		return velocidadeLink;
	}

	public void setVelocidadeLink(String velocidadeLink) {
		this.velocidadeLink = velocidadeLink;
	}

	public String getAtp() {
		return atp;
	}

	public void setAtp(String atp) {
		this.atp = atp;
	}

	public String getNomeCentral() {
		return nomeCentral;
	}

	public void setNomeCentral(String nomeCentral) {
		this.nomeCentral = nomeCentral;
	}

	public String getNumeroSwitch() {
		return numeroSwitch;
	}

	public void setNumeroSwitch(String numeroSwitch) {
		this.numeroSwitch = numeroSwitch;
	}

	public String getIpSwt() {
		return ipSwt;
	}

	public void setIpSwt(String ipSwt) {
		this.ipSwt = ipSwt;
	}

	public String getVlanGerenciaSwa() {
		return vlanGerenciaSwa;
	}

	public void setVlanGerenciaSwa(String vlanGerenciaSwa) {
		this.vlanGerenciaSwa = vlanGerenciaSwa;
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

	public RedeIpFunctions getRedeIpFunctions() {
		return redeIpFunctions;
	}

	public void setRedeIpFunctions(RedeIpFunctions redeIpFunctions) {
		this.redeIpFunctions = redeIpFunctions;
	}

	public UsuarioModel getUsuarioSenhas() {
		return usuarioSenhas;
	}

	public void setUsuarioSenhas(UsuarioModel usuarioSenhas) {
		this.usuarioSenhas = usuarioSenhas;
	}

	public JSONObject getJsonDados() {
		return jsonDados;
	}

	public void setJsonDados(JSONObject jsonDados) {
		this.jsonDados = jsonDados;
	}

	public String getMensagemErroConexaoServidor() {
		return mensagemErroConexaoServidor;
	}

	public void setMensagemErroConexaoServidor(String mensagemErroConexaoServidor) {
		this.mensagemErroConexaoServidor = mensagemErroConexaoServidor;
	}

	public String getServicoCliente() {
		return servicoCliente;
	}

	public void setServicoCliente(String servicoCliente) {
		this.servicoCliente = servicoCliente;
	}

	public String getTipoInterface() {
		return tipoInterface;
	}

	public void setTipoInterface(String tipoInterface) {
		this.tipoInterface = tipoInterface;
	}

	public GravaLogResultadoSwitch getGravaLogResultado() {
		return gravaLogResultado;
	}

	public void setGravaLogResultado(GravaLogResultadoSwitch gravaLogResultado) {
		this.gravaLogResultado = gravaLogResultado;
	}

	public TiposTestesEnum getTipoTeste() {
		return tipoTeste;
	}

	public void setTipoTeste(TiposTestesEnum tipoTeste) {
		this.tipoTeste = tipoTeste;
	}

	public int getIdModeloEquipamento() {
		return idModeloEquipamento;
	}

	public void setIdModeloEquipamento(int idModeloEquipamento) {
		this.idModeloEquipamento = idModeloEquipamento;
	}

	public String getNomeModeloEquipamento() {
		return nomeModeloEquipamento;
	}

	public void setNomeModeloEquipamento(String nomeModeloEquipamento) {
		this.nomeModeloEquipamento = nomeModeloEquipamento;
	}
	
}
