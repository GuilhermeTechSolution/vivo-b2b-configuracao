package br.com.iatapp.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.json.JSONObject;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.com.iatapp.enums.BandwidthEnum;
import br.com.iatapp.enums.GlobalStrEnum;
import br.com.iatapp.enums.ModulosTesteEnum;
import br.com.iatapp.enums.TecnologiasTestesEnum;
import br.com.iatapp.enums.TiposServicoEnum;
import br.com.iatapp.enums.TiposTestesEnum;
import br.com.iatapp.helper.DataHelper;
import br.com.iatapp.helper.StringHelper;
import br.com.iatapp.logger.ExceptionLogger;
import br.com.iatapp.logger.GravaLogResultado;
import br.com.iatapp.model.SerialAtrelada;
import br.com.iatapp.model.UsuarioModel;
import br.com.iatapp.rede.RedeIpFunctions;

@Document(collection="testes_principal")
public class TestePrincipalDomain implements Serializable {
	
	@Transient
	private static final long serialVersionUID = -4085894331906450126L;
	
	@Id
	private String id;
	
	@Indexed(name = "_idTeste_01")
	private int idTeste;
	
	@Indexed(name = "_senhaAtendimento_01")
	private String senhaAtendimento;
	
	@Indexed(name = "_idVantive_01")
	private String idVantive;
	
	private String token;
	private Date dataInicioTeste;
	private Date dataFimTeste;
	private long timestampInicioTeste;
	private long timestampFimTeste;	
	private int idModulo;
	private String nomeModulo;
	private String idIntragov;
	private String idSmart;
	private String cliente;
	private String at;
	private String cnl;
	private String sbc;
	private String vrfSip;
	private String comunityVoip;
	private String pePrincipal;
	private String interfacePePrincipal;
	private String ipWanPe;
	private String peBackup;
	private String interfacePeBackup;
	private String numeroLp;
	private int idTecnologia;
	private String nomeTecnologia;
	private int idTipoModem;
	private int idModeloModem;
	private int idModeloPePrincipal;
	private int idModeloPeBackup;
	private int idUsuario;
	private String nomeUsuario;
	private int idEmpresa;
	private String nomeEmpresa;
	private int idArea;
	private String nomeArea;
	private int idRouterCliente;
	private int idServico;
	private String nomeServico;
	private int idUnidadeVelocidadeLink;
	private int velocidadeLink;
	private int idCliente;
	private boolean produtoMss;
	private String idSeguranca;
	private boolean idCertificado;
	private String mensagemGeral;
	private boolean baixaCliente;
	private boolean mensagemAlerta;
	private String vrfCliente;
	private String vrfCadastro;
	private String arrayDuplicadosStr;
	private boolean configVoip;
	
	@Transient
	private String dataInicio;
	@Transient
	private String dataFim;
	@Transient
	private String interfaceFisica;
	@Transient
	private String ipWanClienteImpar;	
	@Transient
	private String ipWanClientePar;	
	@Transient
	private String vlanServico;	
	@Transient
	private String vlanUsuario;	
	@Transient
	private int tipoInterfacePePrincipal;	
	@Transient
	private int tipoInterfacePeBackup;	
	@Transient
	private String bgpRemoteAs;	
	@Transient
	private String hostnameSwc;	
	@Transient
	private String interfaceTipoFisicaSwc;	
	@Transient
	private boolean routerStatic;	
	@Transient
	private String valorProgressBar;	
	@Transient
	private TiposTestesEnum tipoTeste;	
	@Transient
	private String logOnline;
	@Transient
	private String etapaTesteOnline;
	@Transient
	private int[] pingClientRate;	
	@Transient
	private int[] pingClientAvg;	
	@Transient
	private int idTipoLink;	
	@Transient
	private int qtdTestes;	
	@Transient
	private String duracaoTeste;
	
	// link redundante
	
	@Transient
	private String ipWanClientePar2;	
	
	// CPE
	private String ipLoopback;
	private String ipLanCpe;
	private String ipLanRede;
	private String mascaraLanCpe;
	private String interfaceLanCpe;
	private String interfaceWanCpe;
	private String interfaceLoopbackCpe;
	private int idModeloCpe;
	private String nomeModeloCpe;
	private int idFabricanteCpe;
	private String nomeFabricanteCpe;
	private String versaoCpe;
	private String serialCpe;
	
	private boolean linkRedundante;
	
	// CPE 02
	private String ipLoopback2;
	private String ipLanCpe2;
	private String ipLanRede2;
	private String mascaraLanCpe2;
	private String interfaceLanCpe2;
	private String interfaceWanCpe2;
	private String interfaceLoopbackCpe2;
	private int idModeloCpe2;
	private String nomeModeloCpe2;
	private int idFabricanteCpe2;
	private String nomeFabricanteCpe2;
	private String versaoCpe2;
	private String serialCpe2;
	
	// Circuito 02
	private String pePrincipal2;
	private String interfacePePrincipal2;
	private String ipWanPe2;
	private String peBackup2;
	private String interfacePeBackup2;

	private int tipoRede;
		
	@Transient
	private boolean conectarCpe;
	@Transient
	private String hostnameCpe;
	@Transient
	private int acessouCpe;
	@Transient
	private boolean isInterfaceLanDownShutDown;
	@Transient
	private boolean rotaLanPePrincipalDivergenteFormulario;
	@Transient
	private boolean rotaLanPeBackupDivergenteFormulario;
	
	// QoS
	private String classVoip; 
	private String classVideo;
	private String classPlatino; 
	private String classOuro;
	private String classPrata; 
	private String classMultimidiaVpnIp; 
	private String classSuporte;
	
	// DSLAM
	private String dslam;
	private int dslamPortaMaster;
	private int dslamSlot;
	private String ipModem;
	private String ipModemRede;
	private int tipoModem;
	
	@Transient
	private String hostnameDslam;
	@Transient
	private String hostnameModem;
	@Transient
	private String ipDslam;
	@Transient
	private int numeroPortasDslam;
	@Transient
	private String logExecucaoDslam;
	@Transient
	private String statusLink;
	@Transient
	private boolean executarResetPortas;
	@Transient
	private boolean executarTestePePrincipal;
	@Transient
	private int numeroPortasComProblemaDslam;
	@Transient
	private String portasComProblemaDslamStr;	
	@Transient
	private boolean executarRebootModem;
	@Transient
	private boolean ipModemCorreto;
	@Transient
	private String currentOperationalMode;
	@Transient
	private String retornoComandoDisplayInterfaces;
	@Transient
	private int indicePortaComProblema;
	@Transient
	private int[] portas;
	@Transient
	private String[] statusPortas;
	@Transient
	private int[] numeroPerfis;
	@Transient
	private int[] velocidadePerfis;
	@Transient
	private String[] nomePerfis;
	@Transient
	private int[] centralLoopAttenuationPortas;
	@Transient
	private int[] centralSnrMarginPortas;
	@Transient
	private int[] centralStatusAtenuacaoPortas;
	@Transient
	private int[] centralCrcPortas;
	@Transient
	private int[] centralLowsPortas;
	@Transient
	private int[] centralUasPortas;
	@Transient
	private int[] modemLoopAttenuationPortas;
	@Transient
	private int[] modemSnrMarginPortas;
	@Transient
	private int[] modemStatusAtenuacaoPortas;
	
	// OLT
	private String olt;
	private int oltCartao;
	private int oltPorta;
	private int oltIdCliente;
	private String idOnt;
	private int idModeloOlt;
	private int idModeloOltBackup;
	
	// MULTILINK
	
	@Transient
	private int qtdeInterfaces;
	@Transient
	private int velocidadeLinkCada;
	@Transient
	private int idVelocidadeLinkCada;
	@Transient
	private String numMultilink;
	@Transient
	private List<SerialAtrelada> lstSerialAtrelada;
	@Transient
	private List<SerialAtrelada> lstSerialAtreladaCpe;
	@Transient
	public String[] resultadoShowLoggingMultilink = new String[2];    // [0] código [1] Descricao
	@Transient
	public String comandoShowLoggingMultilink = null;
	@Transient
	private boolean isRouterStaticBackup;
	@Transient
	private boolean isInterfaceBgpBackup;
	
	// Logs
	@Transient
	private StringBuilder logFormatado;
	@Transient
	private StringBuilder logPePrincipal;
	@Transient
	private StringBuilder logPePrincipalTemp;
	@Transient
	private StringBuilder logCpe;
	@Transient
	private StringBuilder logCpeTemp;
	@Transient
	private StringBuilder logPeBackup;
	@Transient
	private StringBuilder logDslam;
	@Transient
	private StringBuilder logModem;
	@Transient
	private String logExecucao;
	@Transient
	private GravaLogResultado gravaLogResultado;
	@Transient
	private JSONObject jsonDados;
	@Transient
	private String mensagemErroConexaoServidor;
	
	@Transient
	@JsonIgnore
	private RedeIpFunctions redeIpFunctions;
	
	@Transient
	@JsonIgnore
	private UsuarioModel usuarioSenhas;
	
	public TestePrincipalDomain() {
		setValorProgressBar("5");
		setJsonDados(new JSONObject());
		
		setLogFormatado(new StringBuilder());
		setLogPePrincipal(new StringBuilder());
		setLogPeBackup(new StringBuilder());
		setLogCpe(new StringBuilder());
		setLogDslam(new StringBuilder());
		setLogModem(new StringBuilder());
		setConectarCpe(true);
	}
	
	@Transient
	public void inicializaObjeto() {
		inicializaFormularioAtivacao();
		inicializaResultados();
		inicializaProcedimentoPe();
		inicializaProcedimentoDslam();
	}

	@Transient
	private void inicializaFormularioAtivacao() {
		
		// PEs
		if(StringUtils.isNotBlank(getPePrincipal())) {
			setPePrincipal(getPePrincipal().toLowerCase().trim());
		}		
		if(StringUtils.isNotBlank(getPeBackup())) {
			setPeBackup(getPeBackup().toLowerCase().trim());
		} else {
			setPeBackup("");
		}
		
		// Interfaces
		if(StringUtils.isNotBlank(getInterfacePePrincipal())) {
			setInterfacePePrincipal(getInterfacePePrincipal().trim());
		}		
		if(StringUtils.isNotBlank(getInterfacePeBackup())) {
			setInterfacePeBackup(getInterfacePeBackup().trim());
		} else {
			setInterfacePeBackup("");
		}
		
		// DSLAM
		if(StringUtils.isNotBlank(getDslam())) {
			setDslam(getDslam().trim());
		} else {
			setDslam("");
		}
		
		// IPs
		if(StringUtils.isNotBlank(getIpWanPe())) {
			setIpWanPe(getIpWanPe().trim());
		} else {
			setIpWanPe("");
		}
		if(StringUtils.isNotBlank(getIpModem())) {
			setIpModem(getIpModem().trim());
		} else {
			setIpModem("");
		}
	}
	
	@Transient
	public void inicializaProcedimentoPe() {
		// PE PRINCIPAL
		setIdModeloPePrincipal(0);
		setIdModeloPeBackup(0);
		setVrfCliente("");
		setIpWanClienteImpar("");
		setIpWanClientePar("");
		setPingClientRate(new int[2]);
		setPingClientAvg(new int[2]);
		setLogExecucao("");
		setIdTeste(0);
		setVlanServico("");
		setVlanUsuario("");
	}
	
	@Transient
	public void inicializaProcedimentoDslam() {
		
		setHostnameDslam("");
		setNumeroPortasDslam(0);
		setLogExecucaoDslam("");
		setStatusLink("");
		setNumeroPortasComProblemaDslam(0);
		setPortasComProblemaDslamStr("");
		setTipoModem(0);
		setCurrentOperationalMode("");
		setRetornoComandoDisplayInterfaces("");
		setIndicePortaComProblema(0);
		setIpModemRede("");
		setIpModemCorreto(false);
		setHostnameModem("");
		
		setPortas(new int[4]);
		setStatusPortas(new String[4]);
		setNumeroPerfis(new int[4]);
		setVelocidadePerfis(new int[4]);
		setNomePerfis(new String[4]);
		setCentralLoopAttenuationPortas(new int[4]);
		setCentralSnrMarginPortas(new int[4]);
		setCentralStatusAtenuacaoPortas(new int[4]);
		setCentralCrcPortas(new int[4]);
		setCentralLowsPortas(new int[4]);
		setCentralUasPortas(new int[4]);
		setModemLoopAttenuationPortas(new int[4]);
		setModemSnrMarginPortas(new int[4]);
		setModemStatusAtenuacaoPortas(new int[4]);
	}
	
	@Transient
	private void inicializaResultados() {
		// Setando referência do objeto log resultado
		setGravaLogResultado(null);
		setGravaLogResultado(new GravaLogResultado());
	}
	
	@Transient
	public String getDataTesteStr() {
		return DataHelper.convertDateToStrPTBR(getDataInicioTeste());
	}
	 
	
	@Transient
	public String getDuracaoStr() {
		
		if(getDataFimTeste() == null || getDataInicioTeste() == null)
			return "";
		
		long durationMillis = getDataFimTeste().getTime() - getDataInicioTeste().getTime();
		return DurationFormatUtils.formatDuration(durationMillis, "HH:mm:ss");		
	}
	
	@Transient
	public String getVelocidadeStr(int idUnidadeVelocidade) {
		switch (BandwidthEnum.valueOf(idUnidadeVelocidade)) {
		case KBPS:
			return  "" + getVelocidadeLink() + " " + BandwidthEnum.KBPS.getTipo();
		case MBPS:
			return "" + getVelocidadeLink() + " " + BandwidthEnum.MBPS.getTipo();
		case GBPS:
			return "" + getVelocidadeLink() + " " + BandwidthEnum.GBPS.getTipo();
		default:
			break;
		}
		return "";
	}
	
	@Transient
	public String getModuloStr(int idModulo) {
		switch (ModulosTesteEnum.valueOf(idModulo)) {
		case CHECK_PROGRAMACAO:
			return  ModulosTesteEnum.CHECK_PROGRAMACAO.getDescricao();
		case CHECK_CONFIGURACAO:
			return ModulosTesteEnum.CHECK_CONFIGURACAO.getDescricao();
		case ATIVACAO_LP:
			return ModulosTesteEnum.ATIVACAO_LP.getDescricao();
		case ATIVACAO_CPE:
			return ModulosTesteEnum.ATIVACAO_CPE.getDescricao();
		case CHECK_DUPLICIDADE_IPS:
			return ModulosTesteEnum.CHECK_DUPLICIDADE_IPS.getDescricao();
		case CHECK_VOIP:
			return ModulosTesteEnum.CHECK_VOIP.getDescricao();
		default:
			break;
		}
		return "";
	}
	
	@Transient
	public String getTipoServicoStr(int idServico) {
		switch (TiposServicoEnum.valueOf(idServico)) {
		case VPN_IP:
			return  TiposServicoEnum.VPN_IP.getNome();
		case IP_DEDICADO:
			return  TiposServicoEnum.IP_DEDICADO.getNome();
		case SIP_TRUNKING:
			return  TiposServicoEnum.SIP_TRUNKING.getNome();
		case INTRAGOV:
			return  TiposServicoEnum.INTRAGOV.getNome();
		default:
			break;
		}
		return "";
	}
	
	@Transient
	public String getTecnologiaStr(int idTecnologia) {
		switch (TecnologiasTestesEnum.valueOf(idTecnologia)) {
		case PRAPS:
			return  TecnologiasTestesEnum.PRAPS.getNome();
		case GPON:
			return  TecnologiasTestesEnum.GPON.getNome();
		case FSP:
			return  TecnologiasTestesEnum.FSP.getNome();
		case SWT:
			return  TecnologiasTestesEnum.SWT.getNome();
		default:
			break;
		}
		return "";
	}
	
	// Mostrar o resultado ( OK, FALHOU, WARNING ...)
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
	
	@Transient
	public String getPortaComProblemaDslamStr() {
		
		if(getPortas() == null)
			return "";
		
		return "'0/" + getDslamSlot() + "/" + getPortas()[getIndicePortaComProblema()] + "'";
	}
	
	@Transient
	public long getVelocidadePerfisTodasPortas() {
		long valor = 0;
		for (int c = 0; c < getNumeroPortasDslam(); c++) {
			valor += getVelocidadePerfis()[c];
		}
		return valor;
	}
	
	@Transient
	public static String removeComando(String str) {
		
		String infoProcedimento = StringHelper.class.getName() + "\nProcedimento: removeComando - PrapsCertificacaoModel";
		
		try {			
			if (str == null)
				return "";
			
			String[] linhasRetorno = str.split("" + GlobalStrEnum.BREAK_LINE.toString() + "");
			if (linhasRetorno == null)
				return str;
			if (linhasRetorno.length <= 1 )
				return str;
			
			int index = 1;
			// Para remover linhas em branco antes do comando
			for (String item : linhasRetorno) {
				if (StringUtils.isBlank(item.trim()))
					index++;
				else
					break;
			}
			
			return StringUtils.join(linhasRetorno, "\n", index, linhasRetorno.length);
		    
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
			return str;
		}
	}
	
	/**
	 *	METODOS SETS AND GETS 
	 */

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getIdTeste() {
		return idTeste;
	}

	public void setIdTeste(int idTeste) {
		this.idTeste = idTeste;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(String dataInicio) {
		this.dataInicio = dataInicio;
	}

	public String getDataFim() {
		return dataFim;
	}

	public void setDataFim(String dataFim) {
		this.dataFim = dataFim;
	}

	public String getIdVantive() {
		return idVantive;
	}

	public void setIdVantive(String idVantive) {
		this.idVantive = idVantive;
	}

	public String getIdIntragov() {
		return idIntragov;
	}

	public void setIdIntragov(String idIntragov) {
		this.idIntragov = idIntragov;
	}

	public String getCliente() {
		return cliente;
	}

	public void setCliente(String cliente) {
		this.cliente = cliente;
	}

	public String getPePrincipal() {
		return pePrincipal;
	}

	public void setPePrincipal(String pePrincipal) {
		this.pePrincipal = pePrincipal;
	}

	public String getInterfacePePrincipal() {
		return interfacePePrincipal;
	}

	public void setInterfacePePrincipal(String interfacePePrincipal) {
		this.interfacePePrincipal = interfacePePrincipal;
	}

	public String getIpWanPe() {
		return ipWanPe;
	}

	public void setIpWanPe(String ipWanPe) {
		this.ipWanPe = ipWanPe;
	}

	public String getPeBackup() {
		return peBackup;
	}

	public void setPeBackup(String peBackup) {
		this.peBackup = peBackup;
	}

	public String getInterfacePeBackup() {
		return interfacePeBackup;
	}

	public void setInterfacePeBackup(String interfacePeBackup) {
		this.interfacePeBackup = interfacePeBackup;
	}

	public String getIpLoopback() {
		return ipLoopback;
	}

	public void setIpLoopback(String ipLoopback) {
		this.ipLoopback = ipLoopback;
	}

	public String getIpModem() {
		return ipModem;
	}

	public void setIpModem(String ipModem) {
		this.ipModem = ipModem;
	}

	public String getIpModemRede() {
		return ipModemRede;
	}

	public void setIpModemRede(String ipModemRede) {
		this.ipModemRede = ipModemRede;
	}

	public int getIdTecnologia() {
		return idTecnologia;
	}

	public void setIdTecnologia(int idTecnologia) {
		this.idTecnologia = idTecnologia;
	}

	public int getIdTipoModem() {
		return idTipoModem;
	}

	public void setIdTipoModem(int idTipoModem) {
		this.idTipoModem = idTipoModem;
	}

	public int getIdModeloModem() {
		return idModeloModem;
	}

	public void setIdModeloModem(int idModeloModem) {
		this.idModeloModem = idModeloModem;
	}

	public int getIdModeloCpe() {
		return idModeloCpe;
	}

	public void setIdModeloCpe(int idModeloCpe) {
		this.idModeloCpe = idModeloCpe;
	}

	public int getIdModeloPePrincipal() {
		return idModeloPePrincipal;
	}

	public void setIdModeloPePrincipal(int idModeloPePrincipal) {
		this.idModeloPePrincipal = idModeloPePrincipal;
	}

	public int getIdModeloPeBackup() {
		return idModeloPeBackup;
	}

	public void setIdModeloPeBackup(int idModeloPeBackup) {
		this.idModeloPeBackup = idModeloPeBackup;
	}

	public int getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}

	public int getIdRouterCliente() {
		return idRouterCliente;
	}

	public void setIdRouterCliente(int idRouterCliente) {
		this.idRouterCliente = idRouterCliente;
	}

	public int getIdServico() {
		return idServico;
	}

	public void setIdServico(int idServico) {
		this.idServico = idServico;
	}

	public int getIdUnidadeVelocidadeLink() {
		return idUnidadeVelocidadeLink;
	}

	public void setIdUnidadeVelocidadeLink(int idUnidadeVelocidadeLink) {
		this.idUnidadeVelocidadeLink = idUnidadeVelocidadeLink;
	}

	public int getVelocidadeLink() {
		return velocidadeLink;
	}

	public void setVelocidadeLink(int velocidadeLink) {
		this.velocidadeLink = velocidadeLink;
	}

	public String getNumeroLp() {
		return numeroLp;
	}

	public void setNumeroLp(String numeroLp) {
		this.numeroLp = numeroLp;
	}

	public UsuarioModel getUsuarioSenhas() {
		return usuarioSenhas;
	}

	public void setUsuarioSenhas(UsuarioModel usuarioSenhas) {
		this.usuarioSenhas = usuarioSenhas;
	}

	public GravaLogResultado getGravaLogResultado() {
		return gravaLogResultado;
	}

	public void setGravaLogResultado(GravaLogResultado gravaLogResultado) {
		this.gravaLogResultado = gravaLogResultado;
	}

	public String getValorProgressBar() {
		return valorProgressBar;
	}

	public void setValorProgressBar(String valorProgressBar) {
		this.valorProgressBar = valorProgressBar;
	}

	public RedeIpFunctions getRedeIpFunctions() {
		return redeIpFunctions;
	}

	public void setRedeIpFunctions(RedeIpFunctions redeIpFunctions) {
		this.redeIpFunctions = redeIpFunctions;
	}

	public String getLogExecucao() {
		return logExecucao;
	}

	public void setLogExecucao(String logExecucao) {
		this.logExecucao = logExecucao;
	}

	public TiposTestesEnum getTipoTeste() {
		return tipoTeste;
	}

	public void setTipoTeste(TiposTestesEnum tipoTeste) {
		this.tipoTeste = tipoTeste;
	}

	public StringBuilder getLogFormatado() {
		return logFormatado;
	}

	public void setLogFormatado(StringBuilder logFormatado) {
		this.logFormatado = logFormatado;
	}

	public StringBuilder getLogPePrincipal() {
		return logPePrincipal;
	}

	public void setLogPePrincipal(StringBuilder logPePrincipal) {
		this.logPePrincipal = logPePrincipal;
	}

	public StringBuilder getLogPePrincipalTemp() {
		return logPePrincipalTemp;
	}

	public void setLogPePrincipalTemp(StringBuilder logPePrincipalTemp) {
		this.logPePrincipalTemp = logPePrincipalTemp;
	}

	public StringBuilder getLogCpe() {
		return logCpe;
	}

	public void setLogCpe(StringBuilder logCpe) {
		this.logCpe = logCpe;
	}

	public StringBuilder getLogCpeTemp() {
		return logCpeTemp;
	}

	public void setLogCpeTemp(StringBuilder logCpeTemp) {
		this.logCpeTemp = logCpeTemp;
	}

	public StringBuilder getLogPeBackup() {
		return logPeBackup;
	}

	public void setLogPeBackup(StringBuilder logPeBackup) {
		this.logPeBackup = logPeBackup;
	}

	public StringBuilder getLogDslam() {
		return logDslam;
	}

	public void setLogDslam(StringBuilder logDslam) {
		this.logDslam = logDslam;
	}

	public StringBuilder getLogModem() {
		return logModem;
	}

	public void setLogModem(StringBuilder logModem) {
		this.logModem = logModem;
	}

	public JSONObject getJsonDados() {
		return jsonDados;
	}

	public void setJsonDados(JSONObject jsonDados) {
		this.jsonDados = jsonDados;
	}

	public String getVrfCliente() {
		return vrfCliente;
	}

	public void setVrfCliente(String vrfCliente) {
		this.vrfCliente = vrfCliente;
	}

	public String getIpWanClienteImpar() {
		return ipWanClienteImpar;
	}

	public void setIpWanClienteImpar(String ipWanClienteImpar) {
		this.ipWanClienteImpar = ipWanClienteImpar;
	}

	public String getIpWanClientePar() {
		return ipWanClientePar;
	}

	public void setIpWanClientePar(String ipWanClientePar) {
		this.ipWanClientePar = ipWanClientePar;
	}

	public boolean isConectarCpe() {
		return conectarCpe;
	}

	public void setConectarCpe(boolean conectarCpe) {
		this.conectarCpe = conectarCpe;
	}

	public String getOlt() {
		return olt;
	}

	public void setOlt(String olt) {
		this.olt = olt;
	}

	public String getHostnameSwc() {
		return hostnameSwc;
	}

	public void setHostnameSwc(String hostnameSwc) {
		this.hostnameSwc = hostnameSwc;
	}

	public String getInterfaceTipoFisicaSwc() {
		return interfaceTipoFisicaSwc;
	}

	public void setInterfaceTipoFisicaSwc(String interfaceTipoFisicaSwc) {
		this.interfaceTipoFisicaSwc = interfaceTipoFisicaSwc;
	}

	public String getVlanServico() {
		return vlanServico;
	}

	public void setVlanServico(String vlanServico) {
		this.vlanServico = vlanServico;
	}

	public String getVlanUsuario() {
		return vlanUsuario;
	}

	public void setVlanUsuario(String vlanUsuario) {
		this.vlanUsuario = vlanUsuario;
	}

	public int[] getPingClientRate() {
		return pingClientRate;
	}

	public void setPingClientRate(int[] pingClientRate) {
		this.pingClientRate = pingClientRate;
	}

	public int[] getPingClientAvg() {
		return pingClientAvg;
	}

	public void setPingClientAvg(int[] pingClientAvg) {
		this.pingClientAvg = pingClientAvg;
	}

	public String getDslam() {
		return dslam;
	}

	public void setDslam(String dslam) {
		this.dslam = dslam;
	}

	public int getDslamPortaMaster() {
		return dslamPortaMaster;
	}

	public void setDslamPortaMaster(int dslamPortaMaster) {
		this.dslamPortaMaster = dslamPortaMaster;
	}

	public int getDslamSlot() {
		return dslamSlot;
	}

	public void setDslamSlot(int dslamSlot) {
		this.dslamSlot = dslamSlot;
	}

	public String getHostnameDslam() {
		return hostnameDslam;
	}

	public void setHostnameDslam(String hostnameDslam) {
		this.hostnameDslam = hostnameDslam;
	}

	public String getIpDslam() {
		return ipDslam;
	}

	public void setIpDslam(String ipDslam) {
		this.ipDslam = ipDslam;
	}

	public int getNumeroPortasDslam() {
		return numeroPortasDslam;
	}

	public void setNumeroPortasDslam(int numeroPortasDslam) {
		this.numeroPortasDslam = numeroPortasDslam;
	}

	public String getLogExecucaoDslam() {
		return logExecucaoDslam;
	}

	public void setLogExecucaoDslam(String logExecucaoDslam) {
		this.logExecucaoDslam = logExecucaoDslam;
	}

	public String getStatusLink() {
		return statusLink;
	}

	public void setStatusLink(String statusLink) {
		this.statusLink = statusLink;
	}

	public int getNumeroPortasComProblemaDslam() {
		return numeroPortasComProblemaDslam;
	}

	public void setNumeroPortasComProblemaDslam(int numeroPortasComProblemaDslam) {
		this.numeroPortasComProblemaDslam = numeroPortasComProblemaDslam;
	}

	public String getPortasComProblemaDslamStr() {
		return portasComProblemaDslamStr;
	}

	public void setPortasComProblemaDslamStr(String portasComProblemaDslamStr) {
		this.portasComProblemaDslamStr = portasComProblemaDslamStr;
	}

	public int getTipoModem() {
		return tipoModem;
	}

	public void setTipoModem(int tipoModem) {
		this.tipoModem = tipoModem;
	}

	public boolean isExecutarRebootModem() {
		return executarRebootModem;
	}

	public void setExecutarRebootModem(boolean executarRebootModem) {
		this.executarRebootModem = executarRebootModem;
	}

	public String getCurrentOperationalMode() {
		return currentOperationalMode;
	}

	public void setCurrentOperationalMode(String currentOperationalMode) {
		this.currentOperationalMode = currentOperationalMode;
	}

	public String getRetornoComandoDisplayInterfaces() {
		return retornoComandoDisplayInterfaces;
	}

	public void setRetornoComandoDisplayInterfaces(String retornoComandoDisplayInterfaces) {
		this.retornoComandoDisplayInterfaces = retornoComandoDisplayInterfaces;
	}

	public int getIndicePortaComProblema() {
		return indicePortaComProblema;
	}

	public void setIndicePortaComProblema(int indicePortaComProblema) {
		this.indicePortaComProblema = indicePortaComProblema;
	}

	public int[] getPortas() {
		return portas;
	}

	public void setPortas(int[] portas) {
		this.portas = portas;
	}

	public String[] getStatusPortas() {
		return statusPortas;
	}

	public void setStatusPortas(String[] statusPortas) {
		this.statusPortas = statusPortas;
	}

	public int[] getNumeroPerfis() {
		return numeroPerfis;
	}

	public void setNumeroPerfis(int[] numeroPerfis) {
		this.numeroPerfis = numeroPerfis;
	}

	public int[] getVelocidadePerfis() {
		return velocidadePerfis;
	}

	public void setVelocidadePerfis(int[] velocidadePerfis) {
		this.velocidadePerfis = velocidadePerfis;
	}

	public String[] getNomePerfis() {
		return nomePerfis;
	}

	public void setNomePerfis(String[] nomePerfis) {
		this.nomePerfis = nomePerfis;
	}

	public int[] getCentralLoopAttenuationPortas() {
		return centralLoopAttenuationPortas;
	}

	public void setCentralLoopAttenuationPortas(int[] centralLoopAttenuationPortas) {
		this.centralLoopAttenuationPortas = centralLoopAttenuationPortas;
	}

	public int[] getCentralSnrMarginPortas() {
		return centralSnrMarginPortas;
	}

	public void setCentralSnrMarginPortas(int[] centralSnrMarginPortas) {
		this.centralSnrMarginPortas = centralSnrMarginPortas;
	}

	public int[] getCentralStatusAtenuacaoPortas() {
		return centralStatusAtenuacaoPortas;
	}

	public void setCentralStatusAtenuacaoPortas(int[] centralStatusAtenuacaoPortas) {
		this.centralStatusAtenuacaoPortas = centralStatusAtenuacaoPortas;
	}

	public int[] getCentralCrcPortas() {
		return centralCrcPortas;
	}

	public void setCentralCrcPortas(int[] centralCrcPortas) {
		this.centralCrcPortas = centralCrcPortas;
	}

	public int[] getCentralLowsPortas() {
		return centralLowsPortas;
	}

	public void setCentralLowsPortas(int[] centralLowsPortas) {
		this.centralLowsPortas = centralLowsPortas;
	}

	public int[] getCentralUasPortas() {
		return centralUasPortas;
	}

	public void setCentralUasPortas(int[] centralUasPortas) {
		this.centralUasPortas = centralUasPortas;
	}

	public int[] getModemLoopAttenuationPortas() {
		return modemLoopAttenuationPortas;
	}

	public void setModemLoopAttenuationPortas(int[] modemLoopAttenuationPortas) {
		this.modemLoopAttenuationPortas = modemLoopAttenuationPortas;
	}

	public int[] getModemSnrMarginPortas() {
		return modemSnrMarginPortas;
	}

	public void setModemSnrMarginPortas(int[] modemSnrMarginPortas) {
		this.modemSnrMarginPortas = modemSnrMarginPortas;
	}

	public int[] getModemStatusAtenuacaoPortas() {
		return modemStatusAtenuacaoPortas;
	}

	public void setModemStatusAtenuacaoPortas(int[] modemStatusAtenuacaoPortas) {
		this.modemStatusAtenuacaoPortas = modemStatusAtenuacaoPortas;
	}

	public String getHostnameModem() {
		return hostnameModem;
	}

	public void setHostnameModem(String hostnameModem) {
		this.hostnameModem = hostnameModem;
	}

	public boolean isIpModemCorreto() {
		return ipModemCorreto;
	}

	public void setIpModemCorreto(boolean ipModemCorreto) {
		this.ipModemCorreto = ipModemCorreto;
	}

	public String getHostnameCpe() {
		return hostnameCpe;
	}

	public void setHostnameCpe(String hostnameCpe) {
		this.hostnameCpe = hostnameCpe;
	}

	public String getInterfaceLanCpe() {
		return interfaceLanCpe;
	}

	public void setInterfaceLanCpe(String interfaceLanCpe) {
		this.interfaceLanCpe = interfaceLanCpe;
	}

	public String getInterfaceWanCpe() {
		return interfaceWanCpe;
	}

	public void setInterfaceWanCpe(String interfaceWanCpe) {
		this.interfaceWanCpe = interfaceWanCpe;
	}

	public int getAcessouCpe() {
		return acessouCpe;
	}

	public void setAcessouCpe(int acessouCpe) {
		this.acessouCpe = acessouCpe;
	}

	public boolean isInterfaceLanDownShutDown() {
		return isInterfaceLanDownShutDown;
	}

	public void setInterfaceLanDownShutDown(boolean isInterfaceLanDownShutDown) {
		this.isInterfaceLanDownShutDown = isInterfaceLanDownShutDown;
	}

	public int getQtdeInterfaces() {
		return qtdeInterfaces;
	}

	public void setQtdeInterfaces(int qtdeInterfaces) {
		this.qtdeInterfaces = qtdeInterfaces;
	}

	public int getVelocidadeLinkCada() {
		return velocidadeLinkCada;
	}

	public void setVelocidadeLinkCada(int velocidadeLinkCada) {
		this.velocidadeLinkCada = velocidadeLinkCada;
	}

	public int getIdVelocidadeLinkCada() {
		return idVelocidadeLinkCada;
	}

	public void setIdVelocidadeLinkCada(int idVelocidadeLinkCada) {
		this.idVelocidadeLinkCada = idVelocidadeLinkCada;
	}

	public String getNumMultilink() {
		return numMultilink;
	}

	public void setNumMultilink(String numMultilink) {
		this.numMultilink = numMultilink;
	}

	public List<SerialAtrelada> getLstSerialAtrelada() {
		return lstSerialAtrelada;
	}

	public void setLstSerialAtrelada(List<SerialAtrelada> lstSerialAtrelada) {
		this.lstSerialAtrelada = lstSerialAtrelada;
	}

	public List<SerialAtrelada> getLstSerialAtreladaCpe() {
		return lstSerialAtreladaCpe;
	}

	public void setLstSerialAtreladaCpe(List<SerialAtrelada> lstSerialAtreladaCpe) {
		this.lstSerialAtreladaCpe = lstSerialAtreladaCpe;
	}

	public String[] getResultadoShowLoggingMultilink() {
		return resultadoShowLoggingMultilink;
	}

	public void setResultadoShowLoggingMultilink(String[] resultadoShowLoggingMultilink) {
		this.resultadoShowLoggingMultilink = resultadoShowLoggingMultilink;
	}

	public String getComandoShowLoggingMultilink() {
		return comandoShowLoggingMultilink;
	}

	public void setComandoShowLoggingMultilink(String comandoShowLoggingMultilink) {
		this.comandoShowLoggingMultilink = comandoShowLoggingMultilink;
	}

	public boolean isRouterStaticBackup() {
		return isRouterStaticBackup;
	}

	public void setRouterStaticBackup(boolean isRouterStaticBackup) {
		this.isRouterStaticBackup = isRouterStaticBackup;
	}

	public boolean isInterfaceBgpBackup() {
		return isInterfaceBgpBackup;
	}

	public void setInterfaceBgpBackup(boolean isInterfaceBgpBackup) {
		this.isInterfaceBgpBackup = isInterfaceBgpBackup;
	}

	public int getIdTipoLink() {
		return idTipoLink;
	}

	public void setIdTipoLink(int idTipoLink) {
		this.idTipoLink = idTipoLink;
	}

	public int getTipoInterfacePePrincipal() {
		return tipoInterfacePePrincipal;
	}

	public void setTipoInterfacePePrincipal(int tipoInterfacePePrincipal) {
		this.tipoInterfacePePrincipal = tipoInterfacePePrincipal;
	}

	public String getBgpRemoteAs() {
		return bgpRemoteAs;
	}

	public void setBgpRemoteAs(String bgpRemoteAs) {
		this.bgpRemoteAs = bgpRemoteAs;
	}

	public boolean isRouterStatic() {
		return routerStatic;
	}

	public void setRouterStatic(boolean routerStatic) {
		this.routerStatic = routerStatic;
	}

	public int getTipoInterfacePeBackup() {
		return tipoInterfacePeBackup;
	}

	public void setTipoInterfacePeBackup(int tipoInterfacePeBackup) {
		this.tipoInterfacePeBackup = tipoInterfacePeBackup;
	}
	
	public String getVersaoCpe() {
		return versaoCpe;
	}

	public void setVersaoCpe(String versaoCpe) {
		this.versaoCpe = versaoCpe;
	}

	public String getSerialCpe() {
		return serialCpe;
	}

	public void setSerialCpe(String serialCpe) {
		this.serialCpe = serialCpe;
	}

	public String getIpLanCpe() {
		return ipLanCpe;
	}

	public void setIpLanCpe(String ipLanCpe) {
		this.ipLanCpe = ipLanCpe;
	}

	public String getMascaraLanCpe() {
		return mascaraLanCpe;
	}

	public void setMascaraLanCpe(String mascaraLanCpe) {
		this.mascaraLanCpe = mascaraLanCpe;
	}

	public int getIdFabricanteCpe() {
		return idFabricanteCpe;
	}

	public void setIdFabricanteCpe(int idFabricanteCpe) {
		this.idFabricanteCpe = idFabricanteCpe;
	}

	public String getNomeFabricanteCpe() {
		return nomeFabricanteCpe;
	}

	public void setNomeFabricanteCpe(String nomeFabricanteCpe) {
		this.nomeFabricanteCpe = nomeFabricanteCpe;
	}

	public int getIdCliente() {
		return idCliente;
	}

	public void setIdCliente(int idCliente) {
		this.idCliente = idCliente;
	}

	public String getNomeModeloCpe() {
		return nomeModeloCpe;
	}

	public void setNomeModeloCpe(String nomeModeloCpe) {
		this.nomeModeloCpe = nomeModeloCpe;
	}

	public String getIpLanRede() {
		return ipLanRede;
	}

	public void setIpLanRede(String ipLanRede) {
		this.ipLanRede = ipLanRede;
	}

	public String getInterfaceLoopbackCpe() {
		return interfaceLoopbackCpe;
	}

	public void setInterfaceLoopbackCpe(String interfaceLoopbackCpe) {
		this.interfaceLoopbackCpe = interfaceLoopbackCpe;
	}

	public String getEtapaTesteOnline() {
		return etapaTesteOnline;
	}

	public void setEtapaTesteOnline(String etapaTesteOnline) {
		this.etapaTesteOnline = etapaTesteOnline;
	}

	public int getIdModulo() {
		return idModulo;
	}

	public void setIdModulo(int idModulo) {
		this.idModulo = idModulo;
	}

	public String getNomeModulo() {
		return nomeModulo;
	}

	public void setNomeModulo(String nomeModulo) {
		this.nomeModulo = nomeModulo;
	}

	public int getOltCartao() {
		return oltCartao;
	}

	public void setOltCartao(int oltCartao) {
		this.oltCartao = oltCartao;
	}

	public int getOltPorta() {
		return oltPorta;
	}

	public void setOltPorta(int oltPorta) {
		this.oltPorta = oltPorta;
	}

	public int getOltIdCliente() {
		return oltIdCliente;
	}

	public void setOltIdCliente(int oltIdCliente) {
		this.oltIdCliente = oltIdCliente;
	}

	public String getIdOnt() {
		return idOnt;
	}

	public void setIdOnt(String idOnt) {
		this.idOnt = idOnt;
	}

	public int getIdModeloOlt() {
		return idModeloOlt;
	}

	public void setIdModeloOlt(int idModeloOlt) {
		this.idModeloOlt = idModeloOlt;
	}

	public int getIdModeloOltBackup() {
		return idModeloOltBackup;
	}

	public void setIdModeloOltBackup(int idModeloOltBackup) {
		this.idModeloOltBackup = idModeloOltBackup;
	}

	public String getIdSmart() {
		return idSmart;
	}

	public void setIdSmart(String idSmart) {
		this.idSmart = idSmart;
	}

	public String getMensagemErroConexaoServidor() {
		return mensagemErroConexaoServidor;
	}

	public void setMensagemErroConexaoServidor(String mensagemErroConexaoServidor) {
		this.mensagemErroConexaoServidor = mensagemErroConexaoServidor;
	}

	public boolean isExecutarResetPortas() {
		return executarResetPortas;
	}

	public void setExecutarResetPortas(boolean executarResetPortas) {
		this.executarResetPortas = executarResetPortas;
	}

	public boolean isExecutarTestePePrincipal() {
		return executarTestePePrincipal;
	}

	public void setExecutarTestePePrincipal(boolean executarTestePePrincipal) {
		this.executarTestePePrincipal = executarTestePePrincipal;
	}

	public String getNomeUsuario() {
		return nomeUsuario;
	}

	public void setNomeUsuario(String nomeUsuario) {
		this.nomeUsuario = nomeUsuario;
	}

	public int getQtdTestes() {
		return qtdTestes;
	}

	public void setQtdTestes(int qtdTestes) {
		this.qtdTestes = qtdTestes;
	}

	public String getNomeServico() {
		return nomeServico;
	}

	public void setNomeServico(String nomeServico) {
		this.nomeServico = nomeServico;
	}

	public String getDuracaoTeste() {
		return duracaoTeste;
	}

	public void setDuracaoTeste(String duracaoTeste) {
		this.duracaoTeste = duracaoTeste;
	}

	public String getNomeTecnologia() {
		return nomeTecnologia;
	}

	public void setNomeTecnologia(String nomeTecnologia) {
		this.nomeTecnologia = nomeTecnologia;
	}

	public boolean isRotaLanPePrincipalDivergenteFormulario() {
		return rotaLanPePrincipalDivergenteFormulario;
	}

	public void setRotaLanPePrincipalDivergenteFormulario(boolean rotaLanPePrincipalDivergenteFormulario) {
		this.rotaLanPePrincipalDivergenteFormulario = rotaLanPePrincipalDivergenteFormulario;
	}

	public boolean isRotaLanPeBackupDivergenteFormulario() {
		return rotaLanPeBackupDivergenteFormulario;
	}

	public void setRotaLanPeBackupDivergenteFormulario(boolean rotaLanPeBackupDivergenteFormulario) {
		this.rotaLanPeBackupDivergenteFormulario = rotaLanPeBackupDivergenteFormulario;
	}

	public String getClassVoip() {
		return classVoip;
	}

	public void setClassVoip(String classVoip) {
		this.classVoip = classVoip;
	}

	public String getClassVideo() {
		return classVideo;
	}

	public void setClassVideo(String classVideo) {
		this.classVideo = classVideo;
	}

	public String getClassPlatino() {
		return classPlatino;
	}

	public void setClassPlatino(String classPlatino) {
		this.classPlatino = classPlatino;
	}

	public String getClassOuro() {
		return classOuro;
	}

	public void setClassOuro(String classOuro) {
		this.classOuro = classOuro;
	}

	public String getClassPrata() {
		return classPrata;
	}

	public void setClassPrata(String classPrata) {
		this.classPrata = classPrata;
	}

	public String getClassMultimidiaVpnIp() {
		return classMultimidiaVpnIp;
	}

	public void setClassMultimidiaVpnIp(String classMultimidiaVpnIp) {
		this.classMultimidiaVpnIp = classMultimidiaVpnIp;
	}

	public String getClassSuporte() {
		return classSuporte;
	}

	public void setClassSuporte(String classSuporte) {
		this.classSuporte = classSuporte;
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

	public String getInterfaceFisica() {
		return interfaceFisica;
	}

	public void setInterfaceFisica(String interfaceFisica) {
		this.interfaceFisica = interfaceFisica;
	}

	public Date getDataInicioTeste() {
		return dataInicioTeste;
	}

	public void setDataInicioTeste(Date dataInicioTeste) {
		this.dataInicioTeste = dataInicioTeste;
	}

	public Date getDataFimTeste() {
		return dataFimTeste;
	}

	public void setDataFimTeste(Date dataFimTeste) {
		this.dataFimTeste = dataFimTeste;
	}

	public long getTimestampInicioTeste() {
		return timestampInicioTeste;
	}

	public void setTimestampInicioTeste(long timestampInicioTeste) {
		this.timestampInicioTeste = timestampInicioTeste;
	}

	public long getTimestampFimTeste() {
		return timestampFimTeste;
	}

	public void setTimestampFimTeste(long timestampFimTeste) {
		this.timestampFimTeste = timestampFimTeste;
	}

	public boolean isProdutoMss() {
		return produtoMss;
	}

	public void setProdutoMss(boolean produtoMss) {
		this.produtoMss = produtoMss;
	}

	public String getIdSeguranca() {
		return idSeguranca;
	}

	public void setIdSeguranca(String idSeguranca) {
		this.idSeguranca = idSeguranca;
	}	

	public boolean isBaixaCliente() {
		return baixaCliente;
	}

	public void setBaixaCliente(boolean baixaCliente) {
		this.baixaCliente = baixaCliente;
	}

	public String getSenhaAtendimento() {
		return senhaAtendimento;
	}

	public void setSenhaAtendimento(String senhaAtendimento) {
		this.senhaAtendimento = senhaAtendimento;
	}

	public int getIdEmpresa() {
		return idEmpresa;
	}

	public void setIdEmpresa(int idEmpresa) {
		this.idEmpresa = idEmpresa;
	}

	public String getNomeEmpresa() {
		return nomeEmpresa;
	}

	public void setNomeEmpresa(String nomeEmpresa) {
		this.nomeEmpresa = nomeEmpresa;
	}

	public int getIdArea() {
		return idArea;
	}

	public void setIdArea(int idArea) {
		this.idArea = idArea;
	}

	public String getNomeArea() {
		return nomeArea;
	}

	public void setNomeArea(String nomeArea) {
		this.nomeArea = nomeArea;
	}

	public boolean isMensagemAlerta() {
		return mensagemAlerta;
	}

	public void setMensagemAlerta(boolean mensagemAlerta) {
		this.mensagemAlerta = mensagemAlerta;
	}

	public String getVrfCadastro() {
		return vrfCadastro;
	}

	public void setVrfCadastro(String vrfCadastro) {
		this.vrfCadastro = vrfCadastro;
	}

	public String getIpLoopback2() {
		return ipLoopback2;
	}

	public void setIpLoopback2(String ipLoopback2) {
		this.ipLoopback2 = ipLoopback2;
	}

	public String getIpLanCpe2() {
		return ipLanCpe2;
	}

	public void setIpLanCpe2(String ipLanCpe2) {
		this.ipLanCpe2 = ipLanCpe2;
	}

	public String getIpLanRede2() {
		return ipLanRede2;
	}

	public void setIpLanRede2(String ipLanRede2) {
		this.ipLanRede2 = ipLanRede2;
	}

	public String getMascaraLanCpe2() {
		return mascaraLanCpe2;
	}

	public void setMascaraLanCpe2(String mascaraLanCpe2) {
		this.mascaraLanCpe2 = mascaraLanCpe2;
	}

	public String getInterfaceLanCpe2() {
		return interfaceLanCpe2;
	}

	public void setInterfaceLanCpe2(String interfaceLanCpe2) {
		this.interfaceLanCpe2 = interfaceLanCpe2;
	}

	public String getInterfaceWanCpe2() {
		return interfaceWanCpe2;
	}

	public void setInterfaceWanCpe2(String interfaceWanCpe2) {
		this.interfaceWanCpe2 = interfaceWanCpe2;
	}

	public String getInterfaceLoopbackCpe2() {
		return interfaceLoopbackCpe2;
	}

	public void setInterfaceLoopbackCpe2(String interfaceLoopbackCpe2) {
		this.interfaceLoopbackCpe2 = interfaceLoopbackCpe2;
	}

	public int getIdModeloCpe2() {
		return idModeloCpe2;
	}

	public void setIdModeloCpe2(int idModeloCpe2) {
		this.idModeloCpe2 = idModeloCpe2;
	}

	public String getNomeModeloCpe2() {
		return nomeModeloCpe2;
	}

	public void setNomeModeloCpe2(String nomeModeloCpe2) {
		this.nomeModeloCpe2 = nomeModeloCpe2;
	}

	public int getIdFabricanteCpe2() {
		return idFabricanteCpe2;
	}

	public void setIdFabricanteCpe2(int idFabricanteCpe2) {
		this.idFabricanteCpe2 = idFabricanteCpe2;
	}

	public String getNomeFabricanteCpe2() {
		return nomeFabricanteCpe2;
	}

	public void setNomeFabricanteCpe2(String nomeFabricanteCpe2) {
		this.nomeFabricanteCpe2 = nomeFabricanteCpe2;
	}

	public String getVersaoCpe2() {
		return versaoCpe2;
	}

	public void setVersaoCpe2(String versaoCpe2) {
		this.versaoCpe2 = versaoCpe2;
	}

	public String getSerialCpe2() {
		return serialCpe2;
	}

	public void setSerialCpe2(String serialCpe2) {
		this.serialCpe2 = serialCpe2;
	}

	public String getPePrincipal2() {
		return pePrincipal2;
	}

	public void setPePrincipal2(String pePrincipal2) {
		this.pePrincipal2 = pePrincipal2;
	}

	public String getInterfacePePrincipal2() {
		return interfacePePrincipal2;
	}

	public void setInterfacePePrincipal2(String interfacePePrincipal2) {
		this.interfacePePrincipal2 = interfacePePrincipal2;
	}

	public String getIpWanPe2() {
		return ipWanPe2;
	}

	public void setIpWanPe2(String ipWanPe2) {
		this.ipWanPe2 = ipWanPe2;
	}

	public String getPeBackup2() {
		return peBackup2;
	}

	public void setPeBackup2(String peBackup2) {
		this.peBackup2 = peBackup2;
	}

	public String getInterfacePeBackup2() {
		return interfacePeBackup2;
	}

	public void setInterfacePeBackup2(String interfacePeBackup2) {
		this.interfacePeBackup2 = interfacePeBackup2;
	}

	public boolean isLinkRedundante() {
		return linkRedundante;
	}

	public void setLinkRedundante(boolean linkRedundante) {
		this.linkRedundante = linkRedundante;
	}

	public String getIpWanClientePar2() {
		return ipWanClientePar2;
	}

	public void setIpWanClientePar2(String ipWanClientePar2) {
		this.ipWanClientePar2 = ipWanClientePar2;
	}

	public String getArrayDuplicadosStr() {
		return arrayDuplicadosStr;
	}

	public void setArrayDuplicadosStr(String arrayDuplicadosStr) {
		this.arrayDuplicadosStr = arrayDuplicadosStr;
	}

	public String getAt() {
		return at;
	}

	public void setAt(String at) {
		this.at = at;
	}

	public String getCnl() {
		return cnl;
	}

	public void setCnl(String cnl) {
		this.cnl = cnl;
	}

	public String getSbc() {
		return sbc;
	}

	public void setSbc(String sbc) {
		this.sbc = sbc;
	}

	public String getVrfSip() {
		return vrfSip;
	}

	public void setVrfSip(String vrfSip) {
		this.vrfSip = vrfSip;
	}

	public String getComunityVoip() {
		return comunityVoip;
	}

	public void setComunityVoip(String comunityVoip) {
		this.comunityVoip = comunityVoip;
	}

	public boolean isConfigVoip() {
		return configVoip;
	}

	public void setConfigVoip(boolean configVoip) {
		this.configVoip = configVoip;
	}

	public String getLogOnline() {
		return logOnline;
	}

	public void setLogOnline(String logOnline) {
		this.logOnline = logOnline;
	}

	public int getTipoRede() {
		return tipoRede;
	}

	public void setTipoRede(int tipoRede) {
		this.tipoRede = tipoRede;
	}
}
