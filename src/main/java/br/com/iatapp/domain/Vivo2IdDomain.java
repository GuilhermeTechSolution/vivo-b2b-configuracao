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

import br.com.iatapp.enums.TiposTestesEnum;
import br.com.iatapp.helper.StringHelper;
import br.com.iatapp.logger.GravaLogResultadoVivo2;
import br.com.iatapp.model.UsuarioModel;
import br.com.iatapp.rede.RedeIpFunctions;

@Document(collection="vivo_2_ids")
public class Vivo2IdDomain implements Serializable {

	private static final long serialVersionUID = 4389250904983189948L;

	@Id
	private String id;

	@Indexed(name = "_hostname_01")
	private String hostname;
	private String hostnameEquipamento;
	private String hostnameSwitch;

	@Indexed(name = "_rdist_01")
	private String rdist;

	@Indexed(name = "_backbone_01")
	private String backbone;
	
	private String servico;
	private String cliente;
	private String rt;
	private String rd;
	private String idTbs;
	private String designador;
	private String cidade;
	private String velocidadeLink;
	private int idUnidadeVelocidadeLink;
	private String interfaceBackbone;
	private String vlanRede;
	private String vlanUsuario;
	private String ipLan;
	private String mascaraLan;
	private String ipv6Lan;
	private String mascaraIpv6Lan;
	private String ipLoopback;
	private String ipv6Loopback;
	private String ipWan;
	private String mascaraWan;
	private String ipv6Wan;
	private String mascaraIpv6Wan;
	private String logGeral;
	private String resultadoProcedimento;

	private Date dataInicio;
	private Date dataFinal;
	private String token;
	private int idUsuario;
	private String nomeUsuario;
	private int idModeloEquipamento;
	private String nomeModeloEquipamento;
	private int idModeloSwitch;
	private String nomeModeloSwitch;
	private String interfaceConexaoSwt;
	
	private boolean idCertificado;
	private String mensagemGeral;
	private boolean mensagemAlerta;
	private String fila;

	@Transient
	private String vrf;
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
	private GravaLogResultadoVivo2 gravaLogResultado;
	@Transient
	private TiposTestesEnum tipoTeste;
	@Transient
	private Date dataEntradaFila;

	private boolean registryChangedByUser;

	private String userChangedRegistration;

	private Date dateChangedRegistration;

	public boolean getRegistryChangedByUser() {
		return registryChangedByUser;
	}

	public void setRegistryChangedByUser(boolean registryChangedByUser) {
		this.registryChangedByUser = registryChangedByUser;
	}

	public String getUserChangedRegistration() {
		return userChangedRegistration;
	}

	public void setUserChangedRegistration(String userChangedRegistration) {
		this.userChangedRegistration = userChangedRegistration;
	}

	public Date getDateChangedRegistration() {
		return dateChangedRegistration;
	}

	public void setDateChangedRegistration(Date dateChangedRegistration) {
		this.dateChangedRegistration = dateChangedRegistration;
	}

	public Vivo2IdDomain() {
		setValorProgressBar("5");
		setJsonDados(new JSONObject());		
		setGravaLogResultado(new GravaLogResultadoVivo2());
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

	public String getBackbone() {
		return backbone;
	}

	public void setBackbone(String backbone) {
		this.backbone = backbone;
	}

	public String getServico() {
		return servico;
	}

	public void setServico(String servico) {
		this.servico = servico;
	}

	public String getCliente() {
		return cliente;
	}

	public void setCliente(String cliente) {
		this.cliente = cliente;
	}

	public String getRt() {
		return rt;
	}

	public void setRt(String rt) {
		this.rt = rt;
	}

	public String getRd() {
		return rd;
	}

	public void setRd(String rd) {
		this.rd = rd;
	}

	public String getIdTbs() {
		return idTbs;
	}

	public void setIdTbs(String idTbs) {
		this.idTbs = idTbs;
	}

	public String getInterfaceBackbone() {
		return interfaceBackbone;
	}

	public void setInterfaceBackbone(String interfaceBackbone) {
		this.interfaceBackbone = interfaceBackbone;
	}

	public String getVlanRede() {
		return vlanRede;
	}

	public void setVlanRede(String vlanRede) {
		this.vlanRede = vlanRede;
	}

	public String getVlanUsuario() {
		return vlanUsuario;
	}

	public void setVlanUsuario(String vlanUsuario) {
		this.vlanUsuario = vlanUsuario;
	}

	public String getIpLan() {
		return ipLan;
	}

	public void setIpLan(String ipLan) {
		this.ipLan = ipLan;
	}

	public String getMascaraLan() {
		return mascaraLan;
	}

	public void setMascaraLan(String mascaraLan) {
		this.mascaraLan = mascaraLan;
	}

	public String getIpLoopback() {
		return ipLoopback;
	}

	public void setIpLoopback(String ipLoopback) {
		this.ipLoopback = ipLoopback;
	}

	public String getIpv6Loopback() {
		return ipv6Loopback;
	}

	public void setIpv6Loopback(String ipv6Loopback) {
		this.ipv6Loopback = ipv6Loopback;
	}

	public String getIpWan() {
		return ipWan;
	}

	public void setIpWan(String ipWan) {
		this.ipWan = ipWan;
	}

	public String getIpv6Wan() {
		return ipv6Wan;
	}

	public void setIpv6Wan(String ipv6Wan) {
		this.ipv6Wan = ipv6Wan;
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

	public UsuarioModel getUsuarioSenhas() {
		return usuarioSenhas;
	}

	public void setUsuarioSenhas(UsuarioModel usuarioSenhas) {
		this.usuarioSenhas = usuarioSenhas;
	}

	public RedeIpFunctions getRedeIpFunctions() {
		return redeIpFunctions;
	}

	public void setRedeIpFunctions(RedeIpFunctions redeIpFunctions) {
		this.redeIpFunctions = redeIpFunctions;
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

	public GravaLogResultadoVivo2 getGravaLogResultado() {
		return gravaLogResultado;
	}

	public void setGravaLogResultado(GravaLogResultadoVivo2 gravaLogResultado) {
		this.gravaLogResultado = gravaLogResultado;
	}

	public TiposTestesEnum getTipoTeste() {
		return tipoTeste;
	}

	public void setTipoTeste(TiposTestesEnum tipoTeste) {
		this.tipoTeste = tipoTeste;
	}

	public String getLogGeral() {
		return logGeral;
	}

	public void setLogGeral(String logGeral) {
		this.logGeral = logGeral;
	}

	public String getVelocidadeLink() {
		return velocidadeLink;
	}

	public void setVelocidadeLink(String velocidadeLink) {
		this.velocidadeLink = velocidadeLink;
	}

	public int getIdUnidadeVelocidadeLink() {
		return idUnidadeVelocidadeLink;
	}

	public void setIdUnidadeVelocidadeLink(int idUnidadeVelocidadeLink) {
		this.idUnidadeVelocidadeLink = idUnidadeVelocidadeLink;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getRdist() {
		return rdist;
	}

	public void setRdist(String rdist) {
		this.rdist = rdist;
	}

	public String getMascaraIpv6Wan() {
		return mascaraIpv6Wan;
	}

	public void setMascaraIpv6Wan(String mascaraIpv6Wan) {
		this.mascaraIpv6Wan = mascaraIpv6Wan;
	}

	public String getHostnameEquipamento() {
		return hostnameEquipamento;
	}

	public void setHostnameEquipamento(String hostnameEquipamento) {
		this.hostnameEquipamento = hostnameEquipamento;
	}

	public String getIpv6Lan() {
		return ipv6Lan;
	}

	public void setIpv6Lan(String ipv6Lan) {
		this.ipv6Lan = ipv6Lan;
	}

	public String getMascaraIpv6Lan() {
		return mascaraIpv6Lan;
	}

	public void setMascaraIpv6Lan(String mascaraIpv6Lan) {
		this.mascaraIpv6Lan = mascaraIpv6Lan;
	}

	public String getMascaraWan() {
		return mascaraWan;
	}

	public void setMascaraWan(String mascaraWan) {
		this.mascaraWan = mascaraWan;
	}

	public String getDesignador() {
		return designador;
	}

	public void setDesignador(String designador) {
		this.designador = designador;
	}

	public boolean isIdCertificado() {
		return idCertificado;
	}

	public void setIdCertificado(boolean idCertificado) {
		this.idCertificado = idCertificado;
	}

	public String getMensagemGeral() {
		return mensagemGeral;
	}

	public void setMensagemGeral(String mensagemGeral) {
		this.mensagemGeral = mensagemGeral;
	}

	public boolean isMensagemAlerta() {
		return mensagemAlerta;
	}

	public void setMensagemAlerta(boolean mensagemAlerta) {
		this.mensagemAlerta = mensagemAlerta;
	}

	public String getResultadoProcedimento() {
		return resultadoProcedimento;
	}

	public void setResultadoProcedimento(String resultadoProcedimento) {
		this.resultadoProcedimento = resultadoProcedimento;
	}

	public Date getDataEntradaFila() {
		return dataEntradaFila;
	}

	public void setDataEntradaFila(Date dataEntradaFila) {
		this.dataEntradaFila = dataEntradaFila;
	}

	public String getFila() {
		return fila;
	}

	public void setFila(String fila) {
		this.fila = fila;
	}

	public String getHostnameSwitch() {
		return hostnameSwitch;
	}

	public void setHostnameSwitch(String hostnameSwitch) {
		this.hostnameSwitch = hostnameSwitch;
	}

	public int getIdModeloSwitch() {
		return idModeloSwitch;
	}

	public void setIdModeloSwitch(int idModeloSwitch) {
		this.idModeloSwitch = idModeloSwitch;
	}

	public String getNomeModeloSwitch() {
		return nomeModeloSwitch;
	}

	public void setNomeModeloSwitch(String nomeModeloSwitch) {
		this.nomeModeloSwitch = nomeModeloSwitch;
	}

	public String getInterfaceConexaoSwt() {
		return interfaceConexaoSwt;
	}

	public void setInterfaceConexaoSwt(String interfaceConexaoSwt) {
		this.interfaceConexaoSwt = interfaceConexaoSwt;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public String getVrf() {
		return vrf;
	}

	public void setVrf(String vrf) {
		this.vrf = vrf;
	}
	
}
