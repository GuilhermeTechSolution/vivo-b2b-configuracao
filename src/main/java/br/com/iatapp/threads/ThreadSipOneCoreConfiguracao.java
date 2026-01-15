package br.com.iatapp.threads;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import br.com.iatapp.config.IatConstants;
import br.com.iatapp.dao.VivoB2BDao;
import br.com.iatapp.domain.SipOneCoreLogsDomain;
import br.com.iatapp.domain.SipOneCoreProcessoIdDomain;
import br.com.iatapp.enums.CodigoServidoresEnum;
import br.com.iatapp.enums.ModelosSbcEnum;
import br.com.iatapp.enums.TipoProcedimentoSipOneCoreEnum;
import br.com.iatapp.helper.DataHelper;
import br.com.iatapp.helper.RedeHelper;
import br.com.iatapp.helper.StringHelper;
import br.com.iatapp.logger.ExceptionLogger;
import br.com.iatapp.repositories.SipOneCoreLogsRepository;
import br.com.iatapp.repositories.SipOneCoreProcessoIdRepository;
import br.com.iatapp.service.PasswordService;
import br.com.iatapp.service.SendHttpRequestService;


public class ThreadSipOneCoreConfiguracao extends Thread {
	
//	Script Siptrunking - SBC IMS BCF v13.xlsm
	
	private HttpSession session;
	private SipOneCoreProcessoIdDomain processoIdDomain;
	private SipOneCoreProcessoIdRepository processoIdRepository;
	private SipOneCoreLogsDomain logsDomain;
	private SipOneCoreLogsRepository logsRepository;
	private String folderPath = "";
	
	public ThreadSipOneCoreConfiguracao(HttpSession session, 
			SipOneCoreProcessoIdDomain processoIdDomain, 
			SipOneCoreProcessoIdRepository processoIdRepository,
			SipOneCoreLogsRepository logsRepository) {
		this.session = session;
		this.processoIdDomain = processoIdDomain;
		this.processoIdRepository = processoIdRepository;
		this.logsRepository = logsRepository;
		this.folderPath = IatConstants.SCRIPTS_PATH + this.processoIdDomain.getToken() + "_" + DataHelper.getCurrentLocalDateTimeStamp();
		this.processoIdDomain.setTokenFolder(this.folderPath);
		if (IatConstants.codigoServidor == CodigoServidoresEnum.SERVIDOR_PROD.getCodigoServidor())
			this.folderPath += "/";
		else
			this.folderPath += "\\";
	}
	
	@Override
	public void run() {
		
		try {
		
			System.out.println("Starting ID: " + processoIdDomain.getIdVantive());
			
			processoIdDomain.setValorProgressBar("5");
			processoIdDomain.setEtapaTesteOnline("Iniciando configuração");
			processoIdDomain.setResultadoProcedimento("em_execucao");
			this.processoIdRepository.save(processoIdDomain);
			
			// ----------------------------------------------------------------
			// -------------------------- STAR --------------------------------
			// ----------------------------------------------------------------
			
			processoIdDomain.setValorProgressBar("10");
			processoIdDomain.setEtapaTesteOnline("Buscando informações do Star");
			
			// buscando os dados no star
			JSONObject jsonDados = null;
			boolean errorProcedimento = false;
			for(int c = 0; c < 3; c++) {
				jsonDados = buscaDadosStar(processoIdDomain.getIdVantive());
				if(jsonDados == null) {			
					processoIdDomain.setValorProgressBar("15");
					errorProcedimento = true;
					try { Thread.sleep(5000); } catch (Exception e) {}
					continue;
				}
				
				errorProcedimento = false;
				break;
			}	
			
			if(errorProcedimento) {
				processoIdDomain.setResultadoProcedimento("error_buscar_dados_star");
				processoIdDomain.setDataFinal(new Date());
				this.processoIdRepository.save(processoIdDomain);
				processoIdDomain.setValorProgressBar("100");
				verificaObjetoSessao();
				return;
			}
			
			// setando os dados do Star
			processoIdDomain.setDadosStarStr(jsonDados.toString());
			processoIdDomain.setValorProgressBar("20");
			processoIdDomain.setEtapaTesteOnline("Analisando informações do Star");
			
			// verificando os campos do Star
			
			if(!verificaCamposStar(jsonDados)) {
				processoIdDomain.setValorProgressBar("100");
				verificaObjetoSessao();
				return;
			}
			
			// ----------------------------------------------------------------
			// ---------------------- IPS GERENCIA SBCS -----------------------
			// ----------------------------------------------------------------
			
			if(!buscarSbcIps(jsonDados)) {
				processoIdDomain.setValorProgressBar("100");
				verificaObjetoSessao();
				return;
			}
			
			// ----------------------------------------------------------------
			// ---------------------- BUSCA CAMPOS TXT STAR -------------------
			// ----------------------------------------------------------------
			
			processoIdDomain.setValorProgressBar("30");
			processoIdDomain.setEtapaTesteOnline("Buscando arquivos txt do Star");
			
			errorProcedimento = false;
			for(int c = 0; c < 3; c++) {
				jsonDados = buscaDadosTxtStar(processoIdDomain.getIdVantive());
				if(jsonDados == null) {
					processoIdDomain.setValorProgressBar("60");
					errorProcedimento = true;
					try { Thread.sleep(5000); } catch (Exception e) {}
					continue;
				}
				
				errorProcedimento = false;
				break;
			}	
			
			if(errorProcedimento) {
				processoIdDomain.setResultadoProcedimento("error_buscar_dados_txt_star");
				processoIdDomain.setDataFinal(new Date());
				this.processoIdRepository.save(processoIdDomain);
				processoIdDomain.setValorProgressBar("100");
				verificaObjetoSessao();
				return;
			}
			
			processoIdDomain.setDadosTxtStar(jsonDados.toString());
			
			// salvando
			try { this.processoIdRepository.save(processoIdDomain); } catch (Exception e) {}
			
			
			// ----------------------------------------------------------------
			// ---------------------- GERAÇÃO DE SCRIPTS ----------------------
			// ----------------------------------------------------------------
			
			processoIdDomain.setValorProgressBar("40");
			processoIdDomain.setEtapaTesteOnline("Gerando scripts");
			
			JSONObject jsonDadosSbcIps = new JSONObject(processoIdDomain.getDadosSbcIps());
			JSONObject jsonDadosTxtStar = new JSONObject(processoIdDomain.getDadosTxtStar());
			JSONObject jsonDadosStar = new JSONObject(processoIdDomain.getDadosStarStr());
			
			String ifc = "";
			String scscfonecore = "";
			String scscfatca = "";
			String dominio = "ims4.vivo.net.br";
			String troncoChave = jsonDadosStar.getString("sipPiloto");
			String canais = jsonDadosStar.getString("sipCanais");
			String ipCliente = jsonDadosStar.getString("sipIpCliente");		
			String pabxid = jsonDadosTxtStar.getString("pabxid");
			String callsource = jsonDadosTxtStar.getString("callSource");
			String tgid = jsonDadosTxtStar.getString("tgid");
			String sbcPri = jsonDadosSbcIps.getString("sbcPri");
			String sbcSec = jsonDadosSbcIps.getString("sbcSec");
			String ipPrincipal = jsonDadosSbcIps.getString("ipPrincipal");
			String ipRedundante = jsonDadosSbcIps.getString("ipRedundante");
			String uport = "0";
			
			String location = jsonDadosTxtStar.getString("location").toLowerCase();
			if(location.length() == 3) location += "_";
			
			String cn = troncoChave.substring(0, 2);
			if(cn.equals("11")) {
				ifc = "11";
				scscfonecore = "spo-co-scscf01.ims.mnc010.mcc724.3gppnetwork.org";
				scscfatca = "scscf1-spoco.ims4.vivo.net.br";
				processoIdDomain.setImsSITE("SPO.CO");
				processoIdDomain.setImsHssSIFCID(ifc + "1");
				processoIdDomain.setImsHssSIFCID2(ifc + "8");
			} else {
				ifc = "12";
			    scscfonecore = "spo-mb-scscf01.ims.mnc010.mcc724.3gppnetwork.org";
			    scscfatca = "scscf1-spopd.ims4.vivo.net.br";
			    processoIdDomain.setImsSITE("SPO.PD");
				processoIdDomain.setImsHssSIFCID(ifc + "1");
				processoIdDomain.setImsHssSIFCID2(ifc + "8");
			}
			
			String tgid2 = "";
			int tgidNumber = Integer.parseInt(tgid);
			if(tgidNumber < 10000)
				tgid2 = String.valueOf(tgidNumber + 40000);
			else if(tgidNumber >= 10000 && tgidNumber < 20000)
				tgid2 = String.valueOf(tgidNumber + 20000);
			else if(tgidNumber >= 20000 && tgidNumber < 30000)
				tgid2 = String.valueOf(tgidNumber + 30000);
			
			// ----------------------------------------------------------------
			// -------------------- PREENCHER OBJETO --------------------------
			// ----------------------------------------------------------------
			
			processoIdDomain.setNomeTipoProcedimento(TipoProcedimentoSipOneCoreEnum.getNome(processoIdDomain.getTipoProcedimento()));
			processoIdDomain.setTroncoChavePiloto(troncoChave);
			JSONArray arrStream = jsonDadosStar.optJSONArray("sipRamais");
			if (arrStream != null) {
				List<String> lstStream = new ArrayList<String>();
				for(int i = 0; i < arrStream.length(); i++){
					lstStream.add(arrStream.getString(i));
				}
				if (lstStream.size() > 0)
					processoIdDomain.setRamais(String.join(",", lstStream));
				//processoIdDomain.setRamais(lstStream.stream().collect(Collectors.joining(","));
			}
			if(jsonDadosStar.has("sipRamaisAdicionais")) {
				arrStream = jsonDadosStar.getJSONArray("sipRamaisAdicionais");
				if (arrStream != null) {
					List<String> lstStream = new ArrayList<String>();
					for(int i = 0; i < arrStream.length(); i++){
						lstStream.add(arrStream.getString(i));
					}
					processoIdDomain.setRamaisAdicionais(String.join(",", lstStream));
					//processoIdDomain.setRamais(lstStream.stream().collect(Collectors.joining(","));
				}
			}
			processoIdDomain.setDominio(dominio);
			processoIdDomain.setLocation(location.contains("_") ? StringUtils.substringBeforeLast(location, "_") : location);
			processoIdDomain.setCallsource(callsource);
			processoIdDomain.setPabxid(pabxid);
			processoIdDomain.setQtdeCanais(canais);
			processoIdDomain.setIpCliente(ipCliente);
			processoIdDomain.setTgid(tgid);
			processoIdDomain.setIpSbcPrincipal(ipPrincipal);
			processoIdDomain.setIpSbcRedundante(ipRedundante);
			
			// ----------------------------------------------------------------
			// -------------------- CRIAR DIRETÓRIO ---------------------------
			// ----------------------------------------------------------------
			try {
				Process p = null;
				if (IatConstants.codigoServidor == CodigoServidoresEnum.SERVIDOR_PROD.getCodigoServidor())
					p = Runtime.getRuntime().exec(new String[] {"/bin/bash", "-c", "mkdir " + folderPath });
				else
					p = Runtime.getRuntime().exec(new String[] {"cmd", "/c", "mkdir " + folderPath });
				p.waitFor();
			} catch (Exception e) {
				e.printStackTrace();
				processoIdDomain.setResultadoProcedimento("error_criar_diretorio_scripts");
				processoIdDomain.setDataFinal(new Date());
				this.processoIdRepository.save(processoIdDomain);
				processoIdDomain.setValorProgressBar("100");
				verificaObjetoSessao();
				return;
			}
			
			if (processoIdDomain.getTipoProcedimento() == TipoProcedimentoSipOneCoreEnum.SCRIPTS_AP_MIGRACAO.getCodigo()) {
				
				// ----------------------------------------------------------------
				// ---------------------- SCRIPTS ENS -----------------------------
				// ----------------------------------------------------------------
				
				// ----------------------------------------------------------------
				// -------------------- REMOVE ENS FIRST --------------------------
				// ----------------------------------------------------------------
				
				sleepMs(1000);
				
				// scriptEnsOneCore
				StringBuilder scriptEnsOneCore = new StringBuilder();
				scriptEnsBanner(scriptEnsOneCore, "remove");
				
				// scriptEnsAtca
				StringBuilder scriptEnsAtca = new StringBuilder();
				scriptEnsBanner(scriptEnsAtca, "remove");
				
				// scriptEnsOneCore
				removerScriptEns(scriptEnsOneCore, troncoChave, "piloto", "onecore");
				// scriptEnsAtca
				removerScriptEns(scriptEnsAtca, troncoChave, "piloto", "atca");
				
				// adicionando os ramais
				JSONArray arrayRamais = jsonDadosStar.optJSONArray("sipRamais");
				if (arrayRamais != null) {
					for(int c = 0; c < arrayRamais.length(); c++) {
						if(arrayRamais.getString(c).equals(troncoChave))
							continue;
						
						removerScriptEns(scriptEnsOneCore, arrayRamais.getString(c), "ramal", "onecore");
						removerScriptEns(scriptEnsAtca, arrayRamais.getString(c), "ramal", "atca");
					}
				}
				
				// ramais adicionais
				if(jsonDadosStar.has("sipRamaisAdicionais")) {
					arrayRamais = jsonDadosStar.getJSONArray("sipRamaisAdicionais");
					for(int c = 0; c < arrayRamais.length(); c++) {
						if(arrayRamais.getString(c).equals(troncoChave))
							continue;
						
						removerScriptEns(scriptEnsOneCore, arrayRamais.getString(c), "ramal", "onecore");
						removerScriptEns(scriptEnsAtca, arrayRamais.getString(c), "ramal", "atca");
					}
				}
				
				sleepMs(1000);
				
				// scriptEnsOneCore
				scriptEnsBanner(scriptEnsOneCore, "create");
				// scriptEnsAtca
				scriptEnsBanner(scriptEnsAtca, "create");
				
				// scriptEnsOneCore
				//criarScriptEns(scriptEnsOneCore, troncoChave, dominio, "piloto", "onecore");
				criarScriptEns(scriptEnsOneCore, troncoChave, dominio, "ramal", "onecore");
				
				// scriptEnsAtca
				//criarScriptEns(scriptEnsAtca, troncoChave, dominio, "piloto", "atca");
				criarScriptEns(scriptEnsAtca, troncoChave, dominio, "ramal", "atca");
		
				// adicionando os ramais
				arrayRamais = jsonDadosStar.optJSONArray("sipRamais");
				if (arrayRamais != null) {
					for(int c = 0; c < arrayRamais.length(); c++) {
						if(arrayRamais.getString(c).equals(troncoChave))
							continue;
						
						criarScriptEns(scriptEnsOneCore, arrayRamais.getString(c), dominio, "ramal", "onecore");
						criarScriptEns(scriptEnsAtca, arrayRamais.getString(c), dominio, "ramal", "atca");
					}
				}
				
				// ramais adicionais
				if(jsonDadosStar.has("sipRamaisAdicionais")) {
					arrayRamais = jsonDadosStar.getJSONArray("sipRamaisAdicionais");
					for(int c = 0; c < arrayRamais.length(); c++) {
						if(arrayRamais.getString(c).equals(troncoChave))
							continue;
						
						criarScriptEns(scriptEnsOneCore, arrayRamais.getString(c), dominio, "ramal", "onecore");
						criarScriptEns(scriptEnsAtca, arrayRamais.getString(c), dominio, "ramal", "atca");
					}
				}
				
				// criando arquivo one core
				String nomeArquivo = String.format("%sENUM_Script_ims_SPG_ONECORE_%s_id-%s_MIGRACAO.txt", folderPath, tgid, processoIdDomain.getIdVantive());
				try {
					FileUtils.writeStringToFile(new File(nomeArquivo), scriptEnsOneCore.toString(), "UTF-8");
				} catch (Exception e) { }
				
				// criando arquivo atca
				nomeArquivo = String.format("%sENUM_Script_ims_SPG_ATCA_%s_id-%s_MIGRACAO.txt", folderPath, tgid, processoIdDomain.getIdVantive());
				try {
					FileUtils.writeStringToFile(new File(nomeArquivo), scriptEnsAtca.toString(), "UTF-8");
				} catch (Exception e) { }
				
			}
			
			if (processoIdDomain.getTipoProcedimento() == TipoProcedimentoSipOneCoreEnum.SCRIPTS_ALTA_FRESH.getCodigo() ||
				processoIdDomain.getTipoProcedimento() == TipoProcedimentoSipOneCoreEnum.SCRIPTS_AP_PORTABILIDADE.getCodigo()) {
				
				
				// ----------------------------------------------------------------
				// -------------------- REMOVE IMS FIRST --------------------------
				// ----------------------------------------------------------------
				
				sleepMs(1000);
				
				// scriptImsOneCore
				StringBuilder scriptImsOneCore = new StringBuilder();
				scriptImsBanner(scriptImsOneCore, "remove");
				
				// scriptImsAtca
				StringBuilder scriptImsAtca = new StringBuilder();
				scriptImsBanner(scriptImsAtca, "remove");
				
				// ----------------------------------------------------------------
				// --------------------- script_hss_hsub --------------------------
				// ----------------------------------------------------------------
				
				// scriptImsOneCore
				removerScriptIms(scriptImsOneCore, troncoChave, "piloto", "onecore", "gera_hss_hsub");
				// scriptImsAtca
				removerScriptIms(scriptImsAtca, troncoChave, "piloto", "atca", "gera_hss_hsub");
		
				// adicionando os ramais
				JSONArray arrayRamais = jsonDadosStar.optJSONArray("sipRamais");
				if (arrayRamais != null) {
					for(int c = 0; c < arrayRamais.length(); c++) {
						if(arrayRamais.getString(c).equals(troncoChave))
							continue;
						
						removerScriptIms(scriptImsOneCore, arrayRamais.getString(c), "ramal", "onecore", "gera_hss_hsub");
						removerScriptIms(scriptImsAtca, arrayRamais.getString(c), "ramal", "atca", "gera_hss_hsub");
					}
				}
				
				// ramais adicionais
				if(jsonDadosStar.has("sipRamaisAdicionais")) {
					arrayRamais = jsonDadosStar.getJSONArray("sipRamaisAdicionais");
					for(int c = 0; c < arrayRamais.length(); c++) {
						if(arrayRamais.getString(c).equals(troncoChave))
							continue;
						
						removerScriptIms(scriptImsOneCore, arrayRamais.getString(c), "ramal", "onecore", "gera_hss_hsub");
						removerScriptIms(scriptImsAtca, arrayRamais.getString(c), "ramal", "atca", "gera_hss_hsub");
					}
				}
				
				// ----------------------------------------------------------------
				// ------------------- script_hss_hsdainf -------------------------
				// ----------------------------------------------------------------
				
				// scriptImsOneCore
				//removerScriptIms(scriptImsOneCore, troncoChave, "piloto", "onecore", "gera_hss_hsdainf");
				removerScriptIms(scriptImsOneCore, troncoChave, "ramal", "onecore", "gera_hss_hsdainf");
				// scriptImsAtca
				//removerScriptIms(scriptImsAtca, troncoChave, "piloto", "atca", "gera_hss_hsdainf");
				removerScriptIms(scriptImsAtca, troncoChave, "ramal", "atca", "gera_hss_hsdainf");
		
				// adicionando os ramais
				arrayRamais = jsonDadosStar.optJSONArray("sipRamais");
				if (arrayRamais != null) {
					for(int c = 0; c < arrayRamais.length(); c++) {
						if(arrayRamais.getString(c).equals(troncoChave))
							continue;
						
						removerScriptIms(scriptImsOneCore, arrayRamais.getString(c), "ramal", "onecore", "gera_hss_hsdainf");
						removerScriptIms(scriptImsAtca, arrayRamais.getString(c), "ramal", "atca", "gera_hss_hsdainf");
					}
				}
				
				// ramais adicionais
				if(jsonDadosStar.has("sipRamaisAdicionais")) {
					arrayRamais = jsonDadosStar.getJSONArray("sipRamaisAdicionais");
					for(int c = 0; c < arrayRamais.length(); c++) {
						if(arrayRamais.getString(c).equals(troncoChave))
							continue;
						
						removerScriptIms(scriptImsOneCore, arrayRamais.getString(c), "ramal", "onecore", "gera_hss_hsdainf");
						removerScriptIms(scriptImsAtca, arrayRamais.getString(c), "ramal", "atca", "gera_hss_hsdainf");
					}
				}
				
				// ----------------------------------------------------------------
				// ----------------------- script_ens -----------------------------
				// ----------------------------------------------------------------
				
				// scriptImsOneCore
				removerScriptEns(scriptImsOneCore, troncoChave, "piloto", "onecore");
				// scriptImsAtca
				removerScriptEns(scriptImsAtca, troncoChave, "piloto", "atca");
		
				// adicionando os ramais
				arrayRamais = jsonDadosStar.optJSONArray("sipRamais");
				if (arrayRamais != null) {
					for(int c = 0; c < arrayRamais.length(); c++) {
						if(arrayRamais.getString(c).equals(troncoChave))
							continue;
						
						removerScriptEns(scriptImsOneCore, arrayRamais.getString(c), "ramal", "onecore");
						removerScriptEns(scriptImsAtca, arrayRamais.getString(c), "ramal", "atca");
					}
				}
				
				// ramais adicionais
				if(jsonDadosStar.has("sipRamaisAdicionais")) {
					arrayRamais = jsonDadosStar.getJSONArray("sipRamaisAdicionais");
					for(int c = 0; c < arrayRamais.length(); c++) {
						if(arrayRamais.getString(c).equals(troncoChave))
							continue;
						
						removerScriptEns(scriptImsOneCore, arrayRamais.getString(c), "ramal", "onecore");
						removerScriptEns(scriptImsAtca, arrayRamais.getString(c), "ramal", "atca");
					}
				}
				
				// ----------------------------------------------------------------
				// ---------------------- SCRIPTS IMS -----------------------------
				// ----------------------------------------------------------------
				
				sleepMs(1000);
				
				// scriptImsOneCore
				scriptImsBanner(scriptImsOneCore, "create");
				// scriptImsAtca
				scriptImsBanner(scriptImsAtca, "create");
		
				// scriptImsOneCore
				criarScriptIms(scriptImsOneCore, troncoChave, dominio, pabxid, ifc, scscfonecore, callsource, "piloto", "onecore", location);
				// scriptImsAtca
				criarScriptIms(scriptImsAtca, troncoChave, dominio, pabxid, ifc, scscfatca, callsource, "piloto", "atca", location);
		
				// adicionando os ramais
				arrayRamais = jsonDadosStar.optJSONArray("sipRamais");
				if (arrayRamais != null) {
					for(int c = 0; c < arrayRamais.length(); c++) {
						if(arrayRamais.getString(c).equals(troncoChave))
							continue;
						
						criarScriptIms(scriptImsOneCore, arrayRamais.getString(c), dominio, pabxid, ifc, scscfonecore, callsource, "ramal", "onecore", location);
						criarScriptIms(scriptImsAtca, arrayRamais.getString(c), dominio, pabxid, ifc, scscfatca, callsource, "ramal", "atca", location);
					}
				}
				
				// ramais adicionais
				if(jsonDadosStar.has("sipRamaisAdicionais")) {
					arrayRamais = jsonDadosStar.getJSONArray("sipRamaisAdicionais");
					for(int c = 0; c < arrayRamais.length(); c++) {
						if(arrayRamais.getString(c).equals(troncoChave))
							continue;
						
						criarScriptIms(scriptImsOneCore, arrayRamais.getString(c), dominio, pabxid, ifc, scscfonecore, callsource, "ramal", "onecore", location);
						criarScriptIms(scriptImsAtca, arrayRamais.getString(c), dominio, pabxid, ifc, scscfatca, callsource, "ramal", "atca", location);
					}
				}
				
				String strTipoProcedimento = "";
				switch (TipoProcedimentoSipOneCoreEnum.valueOf(processoIdDomain.getTipoProcedimento())) {
					case SCRIPTS_ALTA_FRESH:
						strTipoProcedimento = "_ALTAFRESH";
						break;
						
					case SCRIPTS_AP_PORTABILIDADE:
						strTipoProcedimento = "_ALTAPLANTA";
						break;
						
					default:
				}
				
				// criando arquivo one core
				String nomeArquivo = String.format("%sScript_ims_SPG_ONECORE_TG_%s_id-%s%s.txt", folderPath, tgid, processoIdDomain.getIdVantive(), strTipoProcedimento);
				try {
					FileUtils.writeStringToFile(new File(nomeArquivo), scriptImsOneCore.toString(), "UTF-8");
				} catch (Exception e) { }
				
				// criando arquivo atca
				nomeArquivo = String.format("%sScript_ims_SPG_ATCA_TG_%s_id-%s%s.txt", folderPath, tgid, processoIdDomain.getIdVantive(), strTipoProcedimento);
				try {
					FileUtils.writeStringToFile(new File(nomeArquivo), scriptImsAtca.toString(), "UTF-8");
				} catch (Exception e) { }
		
				// ----------------------------------------------------------------
				// ---------------------- SCRIPTS BCF -----------------------------
				// ----------------------------------------------------------------
				
				// ----------------------------------------------------------------
				// -------------------- REMOVE BCF FIRST --------------------------
				// ----------------------------------------------------------------
				
				sleepMs(1000);
				
				// scriptBcf
				StringBuilder scriptBcf = new StringBuilder();
				scriptBcfBanner(scriptBcf, "remove");
				
				removerScriptBcf(scriptBcf, tgid, tgid2);
				
				sleepMs(1000);
				
				// scriptBcf
				scriptBcfBanner(scriptBcf, "create");
				
				criarScriptBcf(scriptBcf, tgid, tgid2, ipPrincipal, ipRedundante, pabxid, canais);
				
				// criando arquivo bcf
				nomeArquivo = String.format("%sScript_BCF_ONECORE_TG_%s_id-%s%s.txt", folderPath, tgid, processoIdDomain.getIdVantive(), strTipoProcedimento);
				try {
					FileUtils.writeStringToFile(new File(nomeArquivo), scriptBcf.toString(), "UTF-8");
				} catch (Exception e) { }
				
				// ----------------------------------------------------------------
				// ---------------------- SCRIPTS SBC -----------------------------
				// ----------------------------------------------------------------
				
				// ----------------------------------------------------------------
				// -------------------- REMOVE SBC FIRST --------------------------
				// ----------------------------------------------------------------
				
				sleepMs(1000);
				
				// scriptSbc
				StringBuilder scriptSbc = new StringBuilder();
				criarScriptSbc(scriptSbc, tgid, troncoChave, pabxid, ipCliente, sbcPri, sbcSec, dominio, canais, uport);
				
				// criando arquivo bcf
				nomeArquivo = String.format("%sScript_sbc_TG_%s_id-%s%s.txt", folderPath, tgid, processoIdDomain.getIdVantive(), strTipoProcedimento);
				try {
					FileUtils.writeStringToFile(new File(nomeArquivo), scriptSbc.toString(), "UTF-8");
				} catch (Exception e) { }
				

			}
			
			if (processoIdDomain.getTipoProcedimento() == TipoProcedimentoSipOneCoreEnum.ROLLBACK_AP_MIGRACAO.getCodigo()) {
				
				// ----------------------------------------------------------------
				// --------------------- ROLLBACK ENS -----------------------------
				// ----------------------------------------------------------------
				
				sleepMs(1000);
				
				// scriptEnsOneCore
				StringBuilder scriptEnsOneCore = new StringBuilder();
				removerScriptEns(scriptEnsOneCore, troncoChave, "piloto", "onecore");
				
				// scriptEnsAtca
				StringBuilder scriptEnsAtca = new StringBuilder();
				removerScriptEns(scriptEnsAtca, troncoChave, "piloto", "atca");
		
				// adicionando os ramais
				JSONArray arrayRamais = jsonDadosStar.optJSONArray("sipRamais");
				if (arrayRamais != null) {
					for(int c = 0; c < arrayRamais.length(); c++) {
						if(arrayRamais.getString(c).equals(troncoChave))
							continue;
						
						removerScriptEns(scriptEnsOneCore, arrayRamais.getString(c), "ramal", "onecore");
						removerScriptEns(scriptEnsAtca, arrayRamais.getString(c), "ramal", "atca");
					}
				}
				
				// ramais adicionais
				if(jsonDadosStar.has("sipRamaisAdicionais")) {
					arrayRamais = jsonDadosStar.getJSONArray("sipRamaisAdicionais");
					for(int c = 0; c < arrayRamais.length(); c++) {
						if(arrayRamais.getString(c).equals(troncoChave))
							continue;
						
						removerScriptEns(scriptEnsOneCore, arrayRamais.getString(c), "ramal", "onecore");
						removerScriptEns(scriptEnsAtca, arrayRamais.getString(c), "ramal", "atca");
					}
				}
				
				// criando arquivo one core
				String nomeArquivo = String.format("%sROLLBACK_ims_SPG_ONECORE_ENUM_%s_id-%s_ALTAPLANTA_MIGRACAO.txt", folderPath, tgid, processoIdDomain.getIdVantive());
				try {
					FileUtils.writeStringToFile(new File(nomeArquivo), scriptEnsOneCore.toString(), "UTF-8");
				} catch (Exception e) { }
				
				// criando arquivo atca
				nomeArquivo = String.format("%sROLLBACK_ims_SPG_ATCA_ENUM_%s_id-%s_ALTAPLANTA_MIGRACAO.txt", folderPath, tgid, processoIdDomain.getIdVantive());
				try {
					FileUtils.writeStringToFile(new File(nomeArquivo), scriptEnsAtca.toString(), "UTF-8");
				} catch (Exception e) { }
				
			}
			
			if (processoIdDomain.getTipoProcedimento() == TipoProcedimentoSipOneCoreEnum.ROLLBACK_ALTA_FRESH.getCodigo() ||
				processoIdDomain.getTipoProcedimento() == TipoProcedimentoSipOneCoreEnum.ROLLBACK_AP_PORTABILIDADE.getCodigo()) {
				
				// ----------------------------------------------------------------
				// ---------------------- ROLLBACK IMS ----------------------------
				// ----------------------------------------------------------------
				
				sleepMs(1000);
				
				// ----------------------------------------------------------------
				// --------------------- script_hss_hsub --------------------------
				// ----------------------------------------------------------------
		
				// scriptImsOneCore
				StringBuilder scriptRollbackImsOneCore = new StringBuilder();
				removerScriptIms(scriptRollbackImsOneCore, troncoChave, "piloto", "onecore", "gera_hss_hsub");
				
				// scriptImsAtca
				StringBuilder scriptRollbackImsAtca = new StringBuilder();
				removerScriptIms(scriptRollbackImsAtca, troncoChave, "piloto", "atca", "gera_hss_hsub");
		
				// adicionando os ramais
				JSONArray arrayRamais = jsonDadosStar.optJSONArray("sipRamais");
				if (arrayRamais != null) {
					for(int c = 0; c < arrayRamais.length(); c++) {
						if(arrayRamais.getString(c).equals(troncoChave))
							continue;
						
						removerScriptIms(scriptRollbackImsOneCore, arrayRamais.getString(c), "ramal", "onecore", "gera_hss_hsub");
						removerScriptIms(scriptRollbackImsAtca, arrayRamais.getString(c), "ramal", "atca", "gera_hss_hsub");
					}
				}
				
				// ramais adicionais
				if(jsonDadosStar.has("sipRamaisAdicionais")) {
					arrayRamais = jsonDadosStar.getJSONArray("sipRamaisAdicionais");
					for(int c = 0; c < arrayRamais.length(); c++) {
						if(arrayRamais.getString(c).equals(troncoChave))
							continue;
						
						removerScriptIms(scriptRollbackImsOneCore, arrayRamais.getString(c), "ramal", "onecore", "gera_hss_hsub");
						removerScriptIms(scriptRollbackImsAtca, arrayRamais.getString(c), "ramal", "atca", "gera_hss_hsub");
					}
				}
				
				// ----------------------------------------------------------------
				// ------------------- script_hss_hsdainf -------------------------
				// ----------------------------------------------------------------
				
				// scriptImsOneCore
				removerScriptIms(scriptRollbackImsOneCore, troncoChave, "piloto", "onecore", "gera_hss_hsdainf");
				
				// scriptImsAtca
				removerScriptIms(scriptRollbackImsAtca, troncoChave, "piloto", "atca", "gera_hss_hsdainf");
		
				// adicionando os ramais
				arrayRamais = jsonDadosStar.optJSONArray("sipRamais");
				if (arrayRamais != null) {
					for(int c = 0; c < arrayRamais.length(); c++) {
						if(arrayRamais.getString(c).equals(troncoChave))
							continue;
						
						removerScriptIms(scriptRollbackImsOneCore, arrayRamais.getString(c), "ramal", "onecore", "gera_hss_hsdainf");
						removerScriptIms(scriptRollbackImsAtca, arrayRamais.getString(c), "ramal", "atca", "gera_hss_hsdainf");
					}
				}
				
				// ramais adicionais
				if(jsonDadosStar.has("sipRamaisAdicionais")) {
					arrayRamais = jsonDadosStar.getJSONArray("sipRamaisAdicionais");
					for(int c = 0; c < arrayRamais.length(); c++) {
						if(arrayRamais.getString(c).equals(troncoChave))
							continue;
						
						removerScriptIms(scriptRollbackImsOneCore, arrayRamais.getString(c), "ramal", "onecore", "gera_hss_hsdainf");
						removerScriptIms(scriptRollbackImsAtca, arrayRamais.getString(c), "ramal", "atca", "gera_hss_hsdainf");
					}
				}
				
				// ----------------------------------------------------------------
				// ----------------------- script_ens -----------------------------
				// ----------------------------------------------------------------
				
				if (processoIdDomain.getTipoProcedimento() == TipoProcedimentoSipOneCoreEnum.ROLLBACK_ALTA_FRESH.getCodigo()) {
					
					// scriptImsOneCore
					removerScriptEns(scriptRollbackImsOneCore, troncoChave, "piloto", "onecore");
					
					// scriptImsAtca
					removerScriptEns(scriptRollbackImsAtca, troncoChave, "piloto", "atca");
			
					// adicionando os ramais
					arrayRamais = jsonDadosStar.optJSONArray("sipRamais");
					if (arrayRamais != null) {
						for(int c = 0; c < arrayRamais.length(); c++) {
							if(arrayRamais.getString(c).equals(troncoChave))
								continue;
							
							removerScriptEns(scriptRollbackImsOneCore, arrayRamais.getString(c), "ramal", "onecore");
							removerScriptEns(scriptRollbackImsAtca, arrayRamais.getString(c), "ramal", "atca");
						}
					}
					
					// ramais adicionais
					if(jsonDadosStar.has("sipRamaisAdicionais")) {
						arrayRamais = jsonDadosStar.getJSONArray("sipRamaisAdicionais");
						for(int c = 0; c < arrayRamais.length(); c++) {
							if(arrayRamais.getString(c).equals(troncoChave))
								continue;
							
							removerScriptEns(scriptRollbackImsOneCore, arrayRamais.getString(c), "ramal", "onecore");
							removerScriptEns(scriptRollbackImsAtca, arrayRamais.getString(c), "ramal", "atca");
						}
					}
				}
				
				String strTipoProcedimento = "";
				switch (TipoProcedimentoSipOneCoreEnum.valueOf(processoIdDomain.getTipoProcedimento())) {
					case ROLLBACK_ALTA_FRESH:
						strTipoProcedimento = "_ALTAFRESH";
						break;
						
					case ROLLBACK_AP_PORTABILIDADE:
						strTipoProcedimento = "_ALTAPLANTA";
						break;
						
					default:
				}
				
				// criando arquivo one core
				String nomeArquivo = String.format("%sROLLBACK_ims_SPG_ONECORE_%s_id-%s%s.txt", folderPath, tgid, processoIdDomain.getIdVantive(), strTipoProcedimento);
				try {
					FileUtils.writeStringToFile(new File(nomeArquivo), scriptRollbackImsOneCore.toString(), "UTF-8");
				} catch (Exception e) { }
				
				// criando arquivo atca
				nomeArquivo = String.format("%sROLLBACK_ims_SPG_ATCA_%s_id-%s%s.txt", folderPath, tgid, processoIdDomain.getIdVantive(), strTipoProcedimento);
				try {
					FileUtils.writeStringToFile(new File(nomeArquivo), scriptRollbackImsAtca.toString(), "UTF-8");
				} catch (Exception e) { }
				
				
				// ----------------------------------------------------------------
				// ---------------------- ROLLBACK BCF ----------------------------
				// ----------------------------------------------------------------
				
				sleepMs(1000);
		
				// scriptBcf		
				StringBuilder scriptRollbackBcf = new StringBuilder();
				removerScriptBcf(scriptRollbackBcf, tgid, tgid2);
				
				// criando arquivo bcf
				nomeArquivo = String.format("%sROLLBACK_BCF_ONECORE_TG_%s_id-%s%s.txt", folderPath, tgid, processoIdDomain.getIdVantive(), strTipoProcedimento);
				try {
					FileUtils.writeStringToFile(new File(nomeArquivo), scriptRollbackBcf.toString(), "UTF-8");
				} catch (Exception e) { }
				
				// ----------------------------------------------------------------
				// ---------------------- ROLLBACK SBC ----------------------------
				// ----------------------------------------------------------------
				
				sleepMs(1000);
		
				// scriptSbc		
				StringBuilder scriptRollbackSbc = new StringBuilder();		
				removerScriptSbc(scriptRollbackSbc, tgid, troncoChave, pabxid, sbcPri, sbcSec, uport);
				
				// criando arquivo bcf
				nomeArquivo = String.format("%sROLLBACK_sbc_TG_%s_id-%s%s.txt", folderPath, tgid, processoIdDomain.getIdVantive(), strTipoProcedimento);
				try {
					FileUtils.writeStringToFile(new File(nomeArquivo), scriptRollbackSbc.toString(), "UTF-8");
				} catch (Exception e) { }
				
			}
			
			// ----------------------------------------------------------------
			// ---------------------- APLICAÇÃO SCRIPTS -----------------------
			// ----------------------------------------------------------------
			
			if (!processoIdDomain.isScriptsOnly()) {
				ThreadSipOneCoreAplicarScript thread = new ThreadSipOneCoreAplicarScript(
						this.session, this.processoIdDomain, this.processoIdRepository);
				thread.start();
				thread.join();
			}
			
			// ----------------------------------------------------------------
			// ---------------------- FINAL PROCEDIMENTO ----------------------
			// ----------------------------------------------------------------
			
			processoIdDomain.setResultadoProcedimento("ok");
			processoIdDomain.setValorProgressBar("90");
			processoIdDomain.setEtapaTesteOnline("Salvando informações no banco de dados");
			
			// salvando o resultado
			processoIdDomain.setDataFinal(new Date());
			this.processoIdRepository.save(processoIdDomain);
			
			if (!processoIdDomain.isScriptsOnly()) {
				// inserindo log Mongodb
				try {
					logsDomain = new SipOneCoreLogsDomain(processoIdDomain.getIdTeste(), processoIdDomain.getLogExecucao());
					this.logsRepository.save(logsDomain);
				} catch (Exception e) {
					ExceptionLogger.record(ExceptionUtils.getStackTrace(e),
							RedeHelper.retornaInfoProcedimento(ThreadSipOneCoreConfiguracao.class.getName(), String.valueOf(processoIdDomain.getIdTeste()), "insereTesteBancoDados MONGODB"));
				}
			}
			
			processoIdDomain.setValorProgressBar("100");
			processoIdDomain.setEtapaTesteOnline("Configuração finalizada");
			
			verificaObjetoSessao();
			
			return;
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e),
					RedeHelper.retornaInfoProcedimento(ThreadSipOneCoreConfiguracao.class.getName(), processoIdDomain.getIdVantive(), "ThreadSipOneCoreConfiguracao"));
		}
	}

	private boolean criarScriptSbc(StringBuilder script, String tgid, String troncoChave, String pabxid, String ipCliente, String sbcPri, String sbcSec, String dominio, String canais, String uport) {
		
		switch (sbcPri) {
		case "NGN-BR-BRU-AC-SBC":
			script.append(String.format("#### Executar os comandos abaixo no SBC %s (SONUS SWe) ####", sbcPri));
			script.append((char) 13); script.append((char) 10);
			
			processoIdDomain.setHostSbcPrimario(sbcPri);
			processoIdDomain.setModeloSbcPrimario(ModelosSbcEnum.SONUS.getCodigo());
			processoIdDomain.setScriptSbcPrimario(new StringBuilder());
			processoIdDomain.setScriptRemocaoSbcPrimario(new StringBuilder());
			
			scriptSbcBanner(script, "remove");
			removeSubSonus(script, tgid, troncoChave, pabxid, processoIdDomain.getScriptRemocaoSbcPrimario());
			scriptSbcBanner(script, "create");
			subSonus(script, tgid, troncoChave, pabxid, ipCliente, sbcPri, sbcSec, dominio, canais, processoIdDomain.getScriptSbcPrimario());
			
			script.append((char) 13); script.append((char) 10);
			script.append(String.format("#### Executar os comandos abaixo no SBC %s (GENBAND Q21)####", sbcSec));
			
			processoIdDomain.setHostSbcSecundario(sbcSec);
			processoIdDomain.setModeloSbcSecundario(ModelosSbcEnum.GENBAND.getCodigo());
			processoIdDomain.setScriptSbcSecundario(new StringBuilder());
			processoIdDomain.setScriptRemocaoSbcSecundario(new StringBuilder());
			
			scriptSbcBanner(script, "remove");
			removeSubGenband(script, pabxid, uport, processoIdDomain.getScriptRemocaoSbcSecundario());
			scriptSbcBanner(script, "create");
			subGenband(script, pabxid, dominio, ipCliente, uport, canais, troncoChave, processoIdDomain.getScriptSbcSecundario());
			
			break;
		
		case "NGN-BR-ARQ-FI-SBC":
			
			script.append(String.format("#### Executar os comandos abaixo no SBC %s (GENBAND Q21) ####", sbcPri));
			script.append((char) 13); script.append((char) 10);
			
			processoIdDomain.setHostSbcPrimario(sbcPri);
			processoIdDomain.setModeloSbcPrimario(ModelosSbcEnum.GENBAND.getCodigo());
			processoIdDomain.setScriptSbcPrimario(new StringBuilder());
			processoIdDomain.setScriptRemocaoSbcPrimario(new StringBuilder());
			
			scriptSbcBanner(script, "remove");
			removeSubGenband(script, pabxid, uport, processoIdDomain.getScriptRemocaoSbcPrimario());
			scriptSbcBanner(script, "create");
			subGenband(script, pabxid, dominio, ipCliente, uport, canais, troncoChave, processoIdDomain.getScriptSbcPrimario());
			
			script.append((char) 13); script.append((char) 10);
			script.append(String.format("#### Executar os comandos abaixo no SBC %s (SONUS SWe) ####", sbcSec));
			
			processoIdDomain.setHostSbcSecundario(sbcSec);
			processoIdDomain.setModeloSbcSecundario(ModelosSbcEnum.SONUS.getCodigo());
			processoIdDomain.setScriptSbcSecundario(new StringBuilder());
			processoIdDomain.setScriptRemocaoSbcSecundario(new StringBuilder());
			
			scriptSbcBanner(script, "remove");
			removeSubSonus(script, tgid, troncoChave, pabxid, processoIdDomain.getScriptRemocaoSbcSecundario());
			scriptSbcBanner(script, "create");
			subSonus(script, tgid, troncoChave, pabxid, ipCliente, sbcPri, sbcSec, dominio, canais, processoIdDomain.getScriptSbcSecundario());
			
			break;
		
		case "NGN-BR-CAS-CT-SBC":
		case "NGN-BR-CAS-BON-SBC":
		case "NGN-BR-SPO-PA-SBC":
		case "NGN-BR-SPO-SI-SBC":
	
			script.append(String.format("#### Executar os comandos abaixo no SBCs %s e %s (Ambos SONUS SWe)####", sbcPri, sbcSec));
			script.append((char) 13); script.append((char) 10);
			
			processoIdDomain.setHostSbcPrimario(sbcPri);
			processoIdDomain.setModeloSbcPrimario(ModelosSbcEnum.SONUS.getCodigo());
			processoIdDomain.setScriptSbcPrimario(new StringBuilder());
			processoIdDomain.setScriptRemocaoSbcPrimario(new StringBuilder());
			processoIdDomain.setHostSbcSecundario(sbcSec);
			processoIdDomain.setModeloSbcSecundario(ModelosSbcEnum.SONUS.getCodigo());
			
			scriptSbcBanner(script, "remove");
			removeSubSonus(script, tgid, troncoChave, pabxid, processoIdDomain.getScriptRemocaoSbcPrimario());
			scriptSbcBanner(script, "create");
			subSonus(script, tgid, troncoChave, pabxid, ipCliente, sbcPri, sbcSec, dominio, canais, processoIdDomain.getScriptSbcPrimario());
			processoIdDomain.setScriptSbcSecundario(processoIdDomain.getScriptSbcPrimario());
			processoIdDomain.setScriptRemocaoSbcSecundario(processoIdDomain.getScriptRemocaoSbcPrimario());
			
			break;
		
		default:
			
			script.append(String.format("#### Executar os comandos abaixo no SBCs %s e %s (Ambos GENBAND Q21) ####", sbcPri, sbcSec));
			script.append((char) 13); script.append((char) 10);
			
			processoIdDomain.setHostSbcPrimario(sbcPri);
			processoIdDomain.setModeloSbcPrimario(ModelosSbcEnum.GENBAND.getCodigo());
			processoIdDomain.setScriptSbcPrimario(new StringBuilder());
			processoIdDomain.setScriptRemocaoSbcPrimario(new StringBuilder());
			processoIdDomain.setHostSbcSecundario(sbcSec);
			processoIdDomain.setModeloSbcSecundario(ModelosSbcEnum.GENBAND.getCodigo());
			
			scriptSbcBanner(script, "remove");
			removeSubGenband(script, pabxid, uport, processoIdDomain.getScriptRemocaoSbcPrimario());
			scriptSbcBanner(script, "create");
			subGenband(script, pabxid, dominio, ipCliente, uport, canais, troncoChave, processoIdDomain.getScriptSbcPrimario());
			processoIdDomain.setScriptSbcSecundario(processoIdDomain.getScriptSbcPrimario());
			processoIdDomain.setScriptRemocaoSbcSecundario(processoIdDomain.getScriptRemocaoSbcPrimario());
			
			break;
		}
		
		return true;
	}
	
	private void subSonus(StringBuilder script, String tgid, String troncoChave, String pabxid, String ipCliente, String sbcPri, String sbcSec, String dominio, String canais, StringBuilder scriptSbc) {
		
		StringBuilder scriptLocal = new StringBuilder();
		
		scriptLocal.append("");
        scriptLocal.append((char) 13); script.append((char) 10);
        scriptLocal.append("configure");
        scriptLocal.append((char) 13); script.append((char) 10);
        scriptLocal.append(String.format("set profiles digitParameterHandling dmPmRule voxip%s_%s subRule 0 ruleType digit", tgid, troncoChave));
        scriptLocal.append((char) 13); script.append((char) 10);
        scriptLocal.append(String.format("set profiles digitParameterHandling dmPmRule voxip%s_%s subRule 0 digitManipulation numberType callingNumber", tgid, troncoChave));
        scriptLocal.append((char) 13); script.append((char) 10);
        scriptLocal.append(String.format("set profiles digitParameterHandling dmPmRule voxip%s_%s subRule 0 digitManipulation numberParameterManipulation natureOfAddress none", tgid, troncoChave));
        scriptLocal.append((char) 13); script.append((char) 10);
        scriptLocal.append(String.format("set profiles digitParameterHandling dmPmRule voxip%s_%s subRule 0 digitManipulation numberParameterManipulation numberingPlanIndicator none", tgid, troncoChave));
        scriptLocal.append((char) 13); script.append((char) 10);
        scriptLocal.append(String.format("set profiles digitParameterHandling dmPmRule voxip%s_%s subRule 0 digitManipulation numberParameterManipulation numberLength noInput", tgid, troncoChave));
        scriptLocal.append((char) 13); script.append((char) 10);
        scriptLocal.append(String.format("set profiles digitParameterHandling dmPmRule voxip%s_%s subRule 0 digitManipulation numberParameterManipulation presentation none", tgid, troncoChave));
        scriptLocal.append((char) 13); script.append((char) 10);
        scriptLocal.append(String.format("set profiles digitParameterHandling dmPmRule voxip%s_%s subRule 0 digitManipulation numberParameterManipulation screening none", tgid, troncoChave));
        scriptLocal.append((char) 13); script.append((char) 10);
        scriptLocal.append(String.format("set profiles digitParameterHandling dmPmRule voxip%s_%s subRule 0 digitManipulation numberParameterManipulation includeInEgress none", tgid, troncoChave));
        scriptLocal.append((char) 13); script.append((char) 10);
        scriptLocal.append(String.format("set profiles digitParameterHandling dmPmRule voxip%s_%s subRule 0 digitManipulation digitStringManipulation startDigitPosition 0", tgid, troncoChave));
        scriptLocal.append((char) 13); script.append((char) 10);
        scriptLocal.append(String.format("set profiles digitParameterHandling dmPmRule voxip%s_%s subRule 0 digitManipulation digitStringManipulation numberOfDigits 0", tgid, troncoChave));
        scriptLocal.append((char) 13); script.append((char) 10);
        scriptLocal.append(String.format("set profiles digitParameterHandling dmPmRule voxip%s_%s subRule 0 digitManipulation digitStringManipulation replacement type variable", tgid, troncoChave));
        scriptLocal.append((char) 13); script.append((char) 10);
        scriptLocal.append(String.format("set profiles digitParameterHandling dmPmRule voxip%s_%s subRule 0 digitManipulation digitStringManipulation replacement digitString billingNumber", tgid, troncoChave));
        scriptLocal.append((char) 13); script.append((char) 10);
        scriptLocal.append(String.format("set profiles digitParameterHandling dmPmRule voxip%s_%s subRule 0 digitManipulation digitStringManipulation replacement startDigitPosition 0", tgid, troncoChave));
        scriptLocal.append((char) 13); script.append((char) 10);
        scriptLocal.append(String.format("set profiles digitParameterHandling dmPmRule voxip%s_%s subRule 0 digitManipulation digitStringManipulation replacement numberOfDigits 0", tgid, troncoChave));
        scriptLocal.append((char) 13); script.append((char) 10);
        scriptLocal.append(String.format("set profiles digitParameterHandling dmPmRule voxip%s_%s subRule 0 digitManipulation digitStringManipulation replacement value %s", tgid, troncoChave, troncoChave));
        scriptLocal.append((char) 13); script.append((char) 10);
        scriptLocal.append(String.format("set profiles digitParameterHandling dmPmRule voxip%s_%s subRule 0 digitManipulation digitStringManipulation action none", tgid, troncoChave));
        scriptLocal.append((char) 13); script.append((char) 10);
        scriptLocal.append(String.format("commit"));
        scriptLocal.append((char) 13); script.append((char) 10);
        scriptLocal.append(String.format(""));
		scriptLocal.append((char) 13); script.append((char) 10);
		scriptLocal.append(String.format("set profiles ipSignalingPeerGroup %s_IPSIGP description \"SIPTRUNKING TG %s\"", pabxid, tgid));
		scriptLocal.append((char) 13); script.append((char) 10);
		scriptLocal.append(String.format("set profiles ipSignalingPeerGroup %s_IPSIGP sendAllIpAddressAndFQDN enable", pabxid));
		scriptLocal.append((char) 13); script.append((char) 10);
		scriptLocal.append(String.format("set profiles ipSignalingPeerGroup %s_IPSIGP ipSignalingPeerGroupData 1 serviceStatus inService", pabxid));
		scriptLocal.append((char) 13); script.append((char) 10);
		scriptLocal.append(String.format("set profiles ipSignalingPeerGroup %s_IPSIGP ipSignalingPeerGroupData 1 ipAddress %s", pabxid, ipCliente));
		scriptLocal.append((char) 13); script.append((char) 10);
		scriptLocal.append(String.format("set profiles ipSignalingPeerGroup %s_IPSIGP ipSignalingPeerGroupData 1 ipPort 5060", pabxid));
		scriptLocal.append((char) 13); script.append((char) 10);
		scriptLocal.append(String.format("commit"));
		scriptLocal.append((char) 13); script.append((char) 10);
		scriptLocal.append(String.format(""));
		scriptLocal.append((char) 13); script.append((char) 10);
		scriptLocal.append(String.format("set addressContext default zone privaccess-voxip ipPeer %s ipAddress %s", pabxid, ipCliente));
		scriptLocal.append((char) 13); script.append((char) 10);
		scriptLocal.append(String.format("set addressContext default zone privaccess-voxip ipPeer %s ipPort 5060", pabxid));
		scriptLocal.append((char) 13); script.append((char) 10);
		scriptLocal.append(String.format("set addressContext default zone privaccess-voxip ipPeer %s policy description \"\"", pabxid));
		scriptLocal.append((char) 13); script.append((char) 10);
		scriptLocal.append(String.format("commit"));
		scriptLocal.append((char) 13); script.append((char) 10);
		scriptLocal.append(String.format(""));
		scriptLocal.append((char) 13); script.append((char) 10);
	    
	    if(sbcPri.equals("NGN-BR-BRU-AC-SBC") || sbcSec.equals("NGN-BR-BRU-AC-SBC"))
	    	scriptLocal.append(String.format("set addressContext default zone privaccess-voxip sipTrunkGroup %s media mediaIpInterfaceGroupName Access-Sip-Trunk.IPIG", pabxid));
	    else
	    	scriptLocal.append(String.format("set addressContext default zone privaccess-voxip sipTrunkGroup %s media mediaIpInterfaceGroupName ACCESS-SIP-TRUNK.IPIG", pabxid));
	    
	    scriptLocal.append((char) 13); script.append((char) 10);
	    scriptLocal.append(String.format("set addressContext default zone privaccess-voxip sipTrunkGroup %s media lateMediaSupport passthru", pabxid));
	    scriptLocal.append((char) 13); script.append((char) 10);
	    scriptLocal.append(String.format("set addressContext default zone privaccess-voxip sipTrunkGroup %s media mediaIpAddress 10.255.240.112", pabxid));
	    scriptLocal.append((char) 13); script.append((char) 10);
	    scriptLocal.append(String.format("set addressContext default zone privaccess-voxip sipTrunkGroup %s media sourceAddressFiltering disabled", pabxid));
	    scriptLocal.append((char) 13); script.append((char) 10);
	    scriptLocal.append(String.format("set addressContext default zone privaccess-voxip sipTrunkGroup %s media mediaAddrType matchSigAddrType", pabxid));
	    scriptLocal.append((char) 13); script.append((char) 10);
	    scriptLocal.append(String.format("set addressContext default zone privaccess-voxip sipTrunkGroup %s media sdpAttributesSelectiveRelay enabled", pabxid));
	    scriptLocal.append((char) 13); script.append((char) 10);
	    scriptLocal.append(String.format("set addressContext default zone privaccess-voxip sipTrunkGroup %s policy carrier voxip", pabxid));
	    scriptLocal.append((char) 13); script.append((char) 10);
	    scriptLocal.append(String.format("set addressContext default zone privaccess-voxip sipTrunkGroup %s policy country 55", pabxid));
	    scriptLocal.append((char) 13); script.append((char) 10);
	    scriptLocal.append(String.format("set addressContext default zone privaccess-voxip sipTrunkGroup %s policy sipDomain %s", pabxid, dominio.toUpperCase()));
	    scriptLocal.append((char) 13); script.append((char) 10);
	    scriptLocal.append(String.format("set addressContext default zone privaccess-voxip sipTrunkGroup %s policy digitParameterHandling numberingPlan TELEF_NUM_PLAN", pabxid));
	    scriptLocal.append((char) 13); script.append((char) 10);
	    scriptLocal.append(String.format("set addressContext default zone privaccess-voxip sipTrunkGroup %s policy digitParameterHandling ingressDmPmRule voxip%s_%s", pabxid, tgid, troncoChave));
	    scriptLocal.append((char) 13); script.append((char) 10);
	    scriptLocal.append(String.format("set addressContext default zone privaccess-voxip sipTrunkGroup %s policy callRouting elementRoutingPriority TELEF_ERP_VOX", pabxid));
	    scriptLocal.append((char) 13); script.append((char) 10);
	    scriptLocal.append(String.format("set addressContext default zone privaccess-voxip sipTrunkGroup %s policy media packetServiceProfile PSP_ACCESS_SLINE", pabxid));
	    scriptLocal.append((char) 13); script.append((char) 10);
	    scriptLocal.append(String.format("set addressContext default zone privaccess-voxip sipTrunkGroup %s policy signaling ipSignalingProfile ACCESS_LINE_IPSP", pabxid));
	    scriptLocal.append((char) 13); script.append((char) 10);
	    scriptLocal.append(String.format("set addressContext default zone privaccess-voxip sipTrunkGroup %s policy ipSignalingPeerGroup %s_IPSIGP", pabxid, pabxid));
	    scriptLocal.append((char) 13); script.append((char) 10);
	    scriptLocal.append(String.format("set addressContext default zone privaccess-voxip sipTrunkGroup %s signaling variantType q1912", pabxid));
	    scriptLocal.append((char) 13); script.append((char) 10);
	    scriptLocal.append(String.format("set addressContext default zone privaccess-voxip sipTrunkGroup %s signaling causeCodeMapping cpcSipCauseMappingProfile TELEF_CPC2SIP", pabxid));
	    scriptLocal.append((char) 13); script.append((char) 10);
	    scriptLocal.append(String.format("set addressContext default zone privaccess-voxip sipTrunkGroup %s signaling timers sessionKeepalive 0", pabxid));
	    scriptLocal.append((char) 13); script.append((char) 10);
	    scriptLocal.append(String.format("set addressContext default zone privaccess-voxip sipTrunkGroup %s signaling timers sessionMinSE 0", pabxid));
	    scriptLocal.append((char) 13); script.append((char) 10);
	    scriptLocal.append(String.format("set addressContext default zone privaccess-voxip sipTrunkGroup %s signaling transportPreference preference1 udp", pabxid));
	    scriptLocal.append((char) 13); script.append((char) 10);
	    scriptLocal.append(String.format("set addressContext default zone privaccess-voxip sipTrunkGroup %s signaling transportPreference preference2 tcp", pabxid));
	    scriptLocal.append((char) 13); script.append((char) 10);
	    scriptLocal.append(String.format("set addressContext default zone privaccess-voxip sipTrunkGroup %s signaling rel100Support enabled", pabxid));
	    scriptLocal.append((char) 13); script.append((char) 10);
	    scriptLocal.append(String.format("set addressContext default zone privaccess-voxip sipTrunkGroup %s signaling relayNonInviteRequest enabled", pabxid));
	    scriptLocal.append((char) 13); script.append((char) 10);
	    scriptLocal.append(String.format("set addressContext default zone privaccess-voxip sipTrunkGroup %s signaling honorMaddrParam enabled", pabxid));
	    scriptLocal.append((char) 13); script.append((char) 10);
	    scriptLocal.append(String.format("set addressContext default zone privaccess-voxip sipTrunkGroup %s services transparencyProfile ACCESS_LINE_TP", pabxid));
	    scriptLocal.append((char) 13); script.append((char) 10);
	    scriptLocal.append(String.format("set addressContext default zone privaccess-voxip sipTrunkGroup %s ingressIpPrefix %s 32", pabxid, ipCliente));
	    scriptLocal.append((char) 13); script.append((char) 10);
	    scriptLocal.append(String.format("set addressContext default zone privaccess-voxip sipTrunkGroup %s cac callLimit %s", pabxid, canais));
	    scriptLocal.append((char) 13); script.append((char) 10);
	    scriptLocal.append(String.format("set addressContext default zone privaccess-voxip sipTrunkGroup %s cac emergencyOversubscription 10", pabxid));
	    scriptLocal.append((char) 13); script.append((char) 10);
	    scriptLocal.append(String.format("set addressContext default zone privaccess-voxip sipTrunkGroup %s state enabled", pabxid));
	    scriptLocal.append((char) 13); script.append((char) 10);
	    scriptLocal.append(String.format("set addressContext default zone privaccess-voxip sipTrunkGroup %s mode inService", pabxid));
	    scriptLocal.append((char) 13); script.append((char) 10);
	    scriptLocal.append(String.format("commit"));
	    scriptLocal.append((char) 13); script.append((char) 10);
	    scriptLocal.append(String.format(""));
	    scriptLocal.append((char) 13); script.append((char) 10);
	    
	    scriptSbc.append(scriptLocal.toString());
	    script.append(scriptLocal.toString());
	}
	
	private void subGenband(StringBuilder script, String pabxid, String dominio, String ipCliente, String uport, String canais, String troncoChave, StringBuilder scriptSbc) {
		
		StringBuilder scriptLocal = new StringBuilder();
		
		scriptLocal.append(String.format(""));
		scriptLocal.append((char) 13); script.append((char) 10);
		scriptLocal.append(String.format("cli iedge add PBX_%s %s", pabxid, uport));
		scriptLocal.append((char) 13); script.append((char) 10);
		scriptLocal.append(String.format("cli iedge edit PBX_%s %s SIP Enable", pabxid, uport));
		scriptLocal.append((char) 13); script.append((char) 10);
		scriptLocal.append(String.format("cli iedge edit PBX_%s %s type sipgw", pabxid, uport));
		scriptLocal.append((char) 13); script.append((char) 10);
		scriptLocal.append(String.format("cli iedge edit PBX_%s %s realm privaccess-trunking", pabxid, uport));
		scriptLocal.append((char) 13); script.append((char) 10);
		scriptLocal.append(String.format("cli iedge edit PBX_%s %s static %s", pabxid, uport, ipCliente));
		scriptLocal.append((char) 13); script.append((char) 10);
		scriptLocal.append(String.format("cli iedge edit PBX_%s %s dtg %s", pabxid, uport, pabxid));
		scriptLocal.append((char) 13); script.append((char) 10);
		scriptLocal.append(String.format("cli iedge edit PBX_%s %s dtgc %s", pabxid, uport, dominio));
		scriptLocal.append((char) 13); script.append((char) 10);
		scriptLocal.append(String.format("cli iedge edit PBX_%s %s newsrcitg %s", pabxid, uport, pabxid));
		scriptLocal.append((char) 13); script.append((char) 10);	    
		scriptLocal.append(String.format("cli iedge edit PBX_%s %s new_tgc_ingress %s", pabxid, uport, dominio));
		scriptLocal.append((char) 13); script.append((char) 10);
		scriptLocal.append(String.format("cli iedge edit PBX_%s %s newsrcdtg bcf_onecore_%s", pabxid, uport, dominio));
		scriptLocal.append((char) 13); script.append((char) 10);
		scriptLocal.append(String.format("cli iedge edit PBX_%s %s xcalls %s", pabxid, uport, canais));
		scriptLocal.append((char) 13); script.append((char) 10);
		scriptLocal.append(String.format("cli iedge edit PBX_%s %s use4904tg enable", pabxid, uport));
		scriptLocal.append((char) 13); script.append((char) 10);
		scriptLocal.append(String.format("cli iedge edit PBX_%s %s fmm-ingress-profile sbc-Pilot-ID-prod-p", pabxid, uport));
		scriptLocal.append((char) 13); script.append((char) 10);
		scriptLocal.append(String.format("cli iedge edit PBX_%s %s paidusername +55%s", pabxid, uport, troncoChave));
		scriptLocal.append((char) 13); script.append((char) 10);
		scriptLocal.append(String.format(""));
		scriptLocal.append((char) 13); script.append((char) 10);
		
		scriptSbc.append(scriptLocal.toString());
		script.append(scriptLocal.toString());
		
	}
	
	private boolean criarScriptBcf(StringBuilder script, String tgid, String tgid2, String ipPrincipal, String ipRedundante, String pabxid, String canais) {
		
//		'Print #1, "USE ME:MEID=5;"
//		Print #1, "ADD SIPTG: TGID=" & tg_id & ",LADDRID=1,TGTYPE=TGRP_PBX,REGPEER=N,IPT=IPV4,PIP=""" & ipsbc_principal & """,TGN=""" & pabxid & """,MEDDN=""0"",PBXID=""" & pabxid & """,CRP=REJECT,FASTREG=N,IOI=""" & tg_id & """,CHB=N,SPIT=N,SPOT=Y,RURICNOT=""" & ipsbc_principal & "_BCF2SBC_TEL2SIP"",PAICNOT=""TEL2SIP_PAI_BCF2SBC"",OUTFPN=""IP-PBX-DEL-TGID-ADD-IMS4-DOMAIN"",MAX=" & qtde_canais & ";"
//		Print #1, "ADD SIPTG: TGID=" & tg_id2 & ",LADDRID=1,TGTYPE=TGRP_PBX,REGPEER=N,IPT=IPV4,PIP=""" & ipsbc_redundante & """,TGN=""" & pabxid & """,MEDDN=""0"",PBXID=""" & pabxid & """,CRP=REJECT,FASTREG=N,IOI=""" & tg_id & """,CHB=N,SPIT=N,SPOT=Y,RURICNOT=""" & ipsbc_redundante & "_BCF2SBC_TEL2SIP"",PAICNOT=""TEL2SIP_PAI_BCF2SBC"",OUTFPN=""IP-PBX-DEL-TGID-ADD-IMS4-DOMAIN"",MAX=" & qtde_canais & ";"
//		Print #1, "ADD SRT: SRTID=" & tg_id & ",TSM=SEQ,TG1=" & tg_id & ",TG2=" & tg_id2 & ";"
//		Print #1, "ADD RT: RTID=" & tg_id & ",PBXID=""" & pabxid & """,NUMTYPE=WILDCARD,SRST=SEQ,SR1=" & tg_id & ";"
		
		StringBuilder scriptLocal = new StringBuilder();
		
		scriptLocal.append(String.format("ADD SIPTG: TGID=%s,LADDRID=1,TGTYPE=TGRP_PBX,REGPEER=N,IPT=IPV4,PIP=\"%s\",TGN=\"%s\",MEDDN=\"0\",PBXID=\"%s\",CRP=REJECT,FASTREG=N,IOI=\"%s\",CHB=N,SPIT=N,SPOT=Y,RURICNOT=\"%s_BCF2SBC_TEL2SIP\",PAICNOT=\"TEL2SIP_PAI_BCF2SBC\",OUTFPN=\"IP-PBX-DEL-TGID-ADD-IMS4-DOMAIN\",MAX=%s;", tgid, ipPrincipal, pabxid, pabxid, tgid, ipPrincipal, canais));
		scriptLocal.append((char) 13); script.append((char) 10);
		scriptLocal.append(String.format("ADD SIPTG: TGID=%s,LADDRID=1,TGTYPE=TGRP_PBX,REGPEER=N,IPT=IPV4,PIP=\"%s\",TGN=\"%s\",MEDDN=\"0\",PBXID=\"%s\",CRP=REJECT,FASTREG=N,IOI=\"%s\",CHB=N,SPIT=N,SPOT=Y,RURICNOT=\"%s_BCF2SBC_TEL2SIP\",PAICNOT=\"TEL2SIP_PAI_BCF2SBC\",OUTFPN=\"IP-PBX-DEL-TGID-ADD-IMS4-DOMAIN\",MAX=%s;", tgid2, ipRedundante, pabxid, pabxid, tgid, ipRedundante, canais));
		scriptLocal.append((char) 13); script.append((char) 10);
		scriptLocal.append(String.format("ADD SRT: SRTID=%s,TSM=SEQ,TG1=%s,TG2=%s;", tgid, tgid, tgid2));
		scriptLocal.append((char) 13); script.append((char) 10);
		scriptLocal.append(String.format("ADD RT: RTID=%s,PBXID=\"%s\",NUMTYPE=WILDCARD,SRST=SEQ,SR1=%s;", tgid, pabxid, tgid));
		scriptLocal.append((char) 13); script.append((char) 10);
		
		processoIdDomain.setScriptBcf(scriptLocal.toString());
		script.append(scriptLocal.toString());
		return true;
	}
	
	private boolean criarScriptIms(StringBuilder scriptIms, String numeroRamal, String dominio, String pabxid, String ifc, String scscf, String callsource, String tipoRamal, String ims, String location) {
		
//		password = CInt(Rnd * 10000)
//		int password = (int) (Math.random() * 10000);
		// Telefonica solicitou geração de 16 caracteres alphanumericos
		String password = new PasswordService().getPassword(16);
		
//		script_ims = "USE ME:MENAME=HSS;"
//		script_ims = script_ims & Chr(13) & Chr(10) & "ADD HSDAINF:IMPI=""+55" & num & """, HUSERNAME=""+55" & num & """, PWD=""" & password & """, REALM=""" & dominio & """;"
//		script_ims = script_ims & Chr(13) & Chr(10) & "ADD HSUB:SUBID=""+55" & num & """, IMPI=""+55" & num & """, IMPU=""sip:+55" & num & "@" & dominio & """, PBXUSERFLAG=FALSE;"
//		script_ims = script_ims & Chr(13) & Chr(10) & "ADD HIMPU:IMPI=""+55" & num & """, IMPU=""tel:+55" & num & """;"
		
		scriptIms.append("USE ME:MENAME=HSS;");
		scriptIms.append((char) 13); scriptIms.append((char) 10);
		scriptIms.append(String.format("ADD HSDAINF:IMPI=\"+55%s\", HUSERNAME=\"+55%s\", PWD=\"%s\", REALM=\"%s\";", numeroRamal, numeroRamal, password, dominio));
		scriptIms.append((char) 13); scriptIms.append((char) 10);
		scriptIms.append(String.format("ADD HSUB:SUBID=\"+55%s\", IMPI=\"+55%s\", IMPU=\"sip:+55%s@%s\", PBXUSERFLAG=FALSE;", numeroRamal, numeroRamal, numeroRamal, dominio));
		scriptIms.append((char) 13); scriptIms.append((char) 10);
		scriptIms.append(String.format("ADD HIMPU:IMPI=\"+55%s\", IMPU=\"tel:+55%s\";", numeroRamal, numeroRamal));
		
//		If piloto_ramal = "piloto" Then
//		    script_ims = script_ims & Chr(13) & Chr(10) & "ADD HIMPU:IMPI=""+55" & num & """, IMPU=""sip:" & pabxid & """;"
//		End If		
		
		if(tipoRamal.equals("piloto")) {
			scriptIms.append((char) 13); scriptIms.append((char) 10);
			scriptIms.append(String.format("ADD HIMPU:IMPI=\"+55%s\", IMPU=\"sip:%s\";", numeroRamal, pabxid));
		}
		
//		script_ims = script_ims & Chr(13) & Chr(10) & "SET HVNTPLID:IMPU=""sip:+55" & num & "@" & dominio & """, VNTPLID=1;"
//		script_ims = script_ims & Chr(13) & Chr(10) & "SET HVNTPLID:IMPU=""tel:+55" & num & """, VNTPLID=1;"
		
		scriptIms.append((char) 13); scriptIms.append((char) 10);
		scriptIms.append(String.format("SET HVNTPLID:IMPU=\"sip:+55%s@%s\", VNTPLID=1;", numeroRamal, dominio));
		scriptIms.append((char) 13); scriptIms.append((char) 10);
		scriptIms.append(String.format("SET HVNTPLID:IMPU=\"tel:+55%s\", VNTPLID=1;", numeroRamal));
		
//		If piloto_ramal = "piloto" Then
//		    script_ims = script_ims & Chr(13) & Chr(10) & "SET HVNTPLID:IMPU=""sip:" & pabxid & """, VNTPLID=1;"
//		End If
		
		if(tipoRamal.equals("piloto")) {
			scriptIms.append((char) 13); scriptIms.append((char) 10);
			scriptIms.append(String.format("SET HVNTPLID:IMPU=\"sip:%s\", VNTPLID=1;", pabxid));
		}
		
//		script_ims = script_ims & Chr(13) & Chr(10) & "SET HREGAUTH:IMPU=""tel:+55" & num & """, REGAUTH=TRUE;"
//		script_ims = script_ims & Chr(13) & Chr(10) & "SET HREGAUTH:IMPU=""sip:+55" & num & "@" & dominio & """, REGAUTH=TRUE;"
		
		scriptIms.append((char) 13); scriptIms.append((char) 10);
		scriptIms.append(String.format("SET HREGAUTH:IMPU=\"tel:+55%s\", REGAUTH=TRUE;", numeroRamal));
		scriptIms.append((char) 13); scriptIms.append((char) 10);
		scriptIms.append(String.format("SET HREGAUTH:IMPU=\"sip:+55%s@%s\", REGAUTH=TRUE;", numeroRamal, dominio));
		
//		If piloto_ramal = "piloto" Then
//		    script_ims = script_ims & Chr(13) & Chr(10) & "SET HREGAUTH:IMPU=""sip:" & pabxid & """, REGAUTH=TRUE;"
//		    script_ims = script_ims & Chr(13) & Chr(10) & "SET HIRS:IRSID=1, IMPULIST=""\""sip:+55" & num & "@" & dominio & "\""&\""tel:+55" & num & "\""&\""sip:" & pabxid & "\"""";"
//		Else
//		    script_ims = script_ims & Chr(13) & Chr(10) & "SET HIRS:IRSID=1, IMPULIST=""\""sip:+55" & num & "@" & dominio & "\""&\""tel:+55" & num & "\"""";"
//		End If
		
		if(tipoRamal.equals("piloto")) {
			scriptIms.append((char) 13); scriptIms.append((char) 10);
			scriptIms.append(String.format("SET HREGAUTH:IMPU=\"sip:%s\", REGAUTH=TRUE;", pabxid));
			scriptIms.append((char) 13); scriptIms.append((char) 10);
			scriptIms.append(String.format("SET HIRS:IRSID=1, IMPULIST=\"\\\"sip:+55%s@%s\\\"&\\\"tel:+55%s\\\"&\\\"sip:%s\\\"\";", numeroRamal, dominio, numeroRamal, pabxid));
		} else {
			scriptIms.append((char) 13); scriptIms.append((char) 10);
			scriptIms.append(String.format("SET HIRS:IRSID=1, IMPULIST=\"\\\"sip:+55%s@%s\\\"&\\\"tel:+55%s\\\"\";", numeroRamal, dominio, numeroRamal));
		}
		
//		script_ims = script_ims & Chr(13) & Chr(10) & "SET HDEFIMPU:IRSID=1,IMPU=""sip:+55" & num & "@" & dominio & """;"
//		script_ims = script_ims & Chr(13) & Chr(10) & "SET HSPSHARE:BASEIMPU=""sip:+55" & num & "@" & dominio & """, IMPU=""tel:+55" & num & """;"
		
		scriptIms.append((char) 13); scriptIms.append((char) 10);
		scriptIms.append(String.format("SET HDEFIMPU:IRSID=1,IMPU=\"sip:+55%s@%s\";", numeroRamal, dominio));
		scriptIms.append((char) 13); scriptIms.append((char) 10);
		scriptIms.append(String.format("SET HSPSHARE:BASEIMPU=\"sip:+55%s@%s\", IMPU=\"tel:+55%s\";", numeroRamal, dominio, numeroRamal));
		
//		If piloto_ramal = "piloto" Then
//		    script_ims = script_ims & Chr(13) & Chr(10) & "SET HSPSHARE: BASEIMPU=""sip:+55" & num & "@" & dominio & """, IMPU=""sip:" & pabxid & """;"
//		    script_ims = script_ims & Chr(13) & Chr(10) & "SET HALIASPU: ALIASID=1, IMPULIST=""\""sip:+55" & num & "@" & dominio & "\""&\""tel:+55" & num & "\""&\""sip:" & pabxid & "\"""";"
//		Else
//		    script_ims = script_ims & Chr(13) & Chr(10) & "SET HALIASPU:ALIASID=1, IMPULIST=""\""sip:+55" & num & "@" & dominio & "\""&\""tel:+55" & num & "\"""";"
//		End If
		
		if(tipoRamal.equals("piloto")) {
			scriptIms.append((char) 13); scriptIms.append((char) 10);
			scriptIms.append(String.format("SET HSPSHARE: BASEIMPU=\"sip:+55%s@%s\", IMPU=\"sip:%s\";", numeroRamal, dominio, pabxid));
			scriptIms.append((char) 13); scriptIms.append((char) 10);
			scriptIms.append(String.format("SET HALIASPU: ALIASID=1, IMPULIST=\"\\\"sip:+55%s@%s\\\"&\\\"tel:+55%s\\\"&\\\"sip:%s\\\"\";", numeroRamal, dominio, numeroRamal, pabxid));
		} else {
			scriptIms.append((char) 13); scriptIms.append((char) 10);
			scriptIms.append(String.format("SET HALIASPU:ALIASID=1, IMPULIST=\"\\\"sip:+55%s@%s\\\"&\\\"tel:+55%s\\\"\";", numeroRamal, dominio, numeroRamal));
		}
		
//		script_ims = script_ims & Chr(13) & Chr(10) & "ADD HSIFC:IMPU=""sip:+55" & num & "@" & dominio & """, SIFCID=" & ifc & "1;"
//		script_ims = script_ims & Chr(13) & Chr(10) & "ADD HSIFC:IMPU=""sip:+55" & num & "@" & dominio & """, SIFCID=" & ifc & "8;"
		
		scriptIms.append((char) 13); scriptIms.append((char) 10);
		scriptIms.append(String.format("ADD HSIFC:IMPU=\"sip:+55%s@%s\", SIFCID=%s1;", numeroRamal, dominio, ifc));
		scriptIms.append((char) 13); scriptIms.append((char) 10);
		scriptIms.append(String.format("ADD HSIFC:IMPU=\"sip:+55%s@%s\", SIFCID=%s8;", numeroRamal, dominio, ifc));
		
//		If ims = "atca" Then
//		    script_ims = script_ims & Chr(13) & Chr(10) & "ADD HSIFC:IMPU=""sip:+55" & num & "@" & dominio & """, SIFCID=" & ifc & "0;"
//		End If
			
		if(ims.equals("atca")) {
			scriptIms.append((char) 13); scriptIms.append((char) 10);
			scriptIms.append(String.format("ADD HSIFC:IMPU=\"sip:+55%s@%s\", SIFCID=%s0;", numeroRamal, dominio, ifc));
		}
		
//		script_ims = script_ims & Chr(13) & Chr(10) & "ADD HNNRLI:IMPU=""sip:+55" & num & "@" & dominio & """,RLINDEX=1,RLI=""" & location & """,RLT=IEEE-802.11a;"
//		script_ims = script_ims & Chr(13) & Chr(10) & "ADD HNNRLI:IMPU=""tel:+55" & num & """,RLINDEX=1,RLI=""" & location & """,RLT=IEEE-802.11a;"
//		script_ims = script_ims & Chr(13) & Chr(10) & "ADD HCAPSCSCF:SUBID=""+55" & num & """, SCSCF=""sip:" & scscf & ";pbxid=" & pabxid & """;"
//		script_ims = script_ims & Chr(13) & Chr(10)
//
//		If alta_planta <> 1 Then
//
//		script_ims = script_ims & Chr(13) & Chr(10) & "USE ME:MENAME=ENS;"
//		script_ims = script_ims & Chr(13) & Chr(10) & "ADD DNAPTRREC:E164NUM=""" & Right(num, 8) & """, ZONENAME=""" & Mid(num, 2, 1) & "." & Left(num, 1) & ".5.5.e164.arpa"", ORDER=10, PREFERENCE=101, FLAGS=""U"", SERVICE=""E2U+sip"", REGEXP=""!^(.*)$!sip:+\\1@" & dominio & "\;user=phone!"";"
//		script_ims = script_ims & Chr(13) & Chr(10)
//
//		End If
		
		scriptIms.append((char) 13); scriptIms.append((char) 10);
		scriptIms.append(String.format("ADD HNNRLI:IMPU=\"sip:+55%s@%s\",RLINDEX=1,RLI=\"%s\",RLT=IEEE-802.11a;", numeroRamal, dominio, location));
		scriptIms.append((char) 13); scriptIms.append((char) 10);
		scriptIms.append(String.format("ADD HNNRLI:IMPU=\"tel:+55%s\",RLINDEX=1,RLI=\"%s\",RLT=IEEE-802.11a;", numeroRamal, location));
		scriptIms.append((char) 13); scriptIms.append((char) 10);
		scriptIms.append(String.format("ADD HCAPSCSCF:SUBID=\"+55%s\", SCSCF=\"sip:%s;pbxid=%s\";", numeroRamal, scscf, pabxid));
		scriptIms.append((char) 13); scriptIms.append((char) 10);
		scriptIms.append((char) 13); scriptIms.append((char) 10);
		
		if (processoIdDomain.getTipoProcedimento() == TipoProcedimentoSipOneCoreEnum.SCRIPTS_ALTA_FRESH.getCodigo()) {
			scriptIms.append(String.format("USE ME:MENAME=ENS;"));
			scriptIms.append((char) 13); scriptIms.append((char) 10);
			scriptIms.append(String.format("ADD DNAPTRREC:E164NUM=\"%s\", ZONENAME=\"%s.%s.5.5.e164.arpa\", ORDER=10, PREFERENCE=101, FLAGS=\"U\", SERVICE=\"E2U+sip\", REGEXP=\"!^(.*)$!sip:+\\\\1@%s\\;user=phone!\";", numeroRamal.substring(numeroRamal.length() - 8), numeroRamal.substring(1, 2), numeroRamal.substring(0, 1), dominio));
			scriptIms.append((char) 13); scriptIms.append((char) 10);
		}
		
//		If piloto_ramal = "piloto" And ims = "onecore" Then
//
//		    string1 = "ADD MSR:IMPU=""sip:+55" & num & "@" & dominio & """, SERVICEDATA/MMTelServices/version=1, SERVICEDATA/MMTelServices/complete-originating-identity-presentation/originating-identity-presentation/active=""true"", SERVICEDATA/MMTelServices/complete-originating-identity-presentation/operator-originating-identity-presentation/authorized=""true"","
//		    string2 = "SERVICEDATA/MMTelServices/complete-originating-identity-presentation/operator-originating-identity-presentation/restriction-override=override-not-active, SERVICEDATA/MMTelServices/complete-originating-identity-presentation/operator-originating-identity-presentation/caller-display-name-present=""false"", SERVICEDATA/MMTelServices/complete-business-trunking/operator-business-trunking/authorized=""true"","
//		    string3 = "SERVICEDATA/MMTelServices/complete-business-trunking/operator-business-trunking/GroupIdentity=""sip:" & pabxid & """, SERVICEDATA/MMTelServices/complete-business-trunking/operator-business-trunking/concurrent-call=0,SERVICEDATA/MMTelServices/complete-business-trunking/operator-business-trunking/cli-screen-service=ServiceInvalid, SERVICEDATA/MMTelServices/complete-customized-collect-call/customized-collect-call/active=""true"","
//		    string4 = "SERVICEDATA/MMTel-extension/basic-part/call-source-code=" & callsource & ", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/local=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/local-toll=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/national-toll=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/international-toll=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority1=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority2=""true"","
//		    string5 = "SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority3=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority4=""true"",SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority5=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority6=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority7=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority8=""true"","
//		    string6 = "SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority9=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority10=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority11=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority12=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority13=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority14=""true"","
//		    string7 = "SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority15=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority16=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority17=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority18=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority19=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority20=""true"","
//		    string8 = "SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority21=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority22=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority23=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority24=""true"",SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority25=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority26=""true"","
//		    string9 = "SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority27=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority28=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority29=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority30=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority31=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority32=""true"","
//		    string10 = "SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-high-entertainment-call-out=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-opertator=""true"",SERVICEDATA/MMTel-extension/basic-part/call-out-authority/calltyping-local=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/calltyping-local-toll=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/calltyping-ld=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/calltyping-international-nanp=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/calltyping-international-world=""true"","
//		    string11 = "SERVICEDATA/MMTel-extension/basic-part/call-out-authority/calltyping-da=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/calltyping-osm=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/calltyping-osp=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/calltyping-osp1=""true"", SERVICEDATA/MMTel-extension/basic-part/user-category=ordinary, SERVICEDATA/MMTel-extension/basic-part/limitation-of-parallel-calls=65535, SERVICEDATA/MMTel-extension/basic-part/announcement-set-id=254,SERVICEDATA/MMTel-extension/basic-part/voice-mailbox-address-index=65535, SERVICEDATA/MMTel-extension/basic-part/VCC-flag=1, SERVICEDATA/MMTel-extension/basic-part/display-index=65534, SERVICEDATA/OdbForImsOrientedServices/OdbForImsMultimediaTelephonyServices="""", SERVICEDATA/OdbForImsOrientedServices/OwedRestriction="""", SERVICEDATA/OdbForImsOrientedServices/LocationCallBarringService="""", SERVICEDATA/OdbForImsOrientedServices/DynamicHomeZoneService="""";"
//		    
//		    script_ims = script_ims & Chr(13) & Chr(10) & "USE ME:MENAME=ATS;"
//		    script_ims = script_ims & Chr(13) & Chr(10) & string1 & " " & string2 & " " & string3 & " " & string4 & " " & string5 & " " & string6 & string7 & " " & string8 & " " & string9 & " " & string10 & " " & string11
//		ElseIf piloto_ramal = "ramal" And ims = "onecore" Then
//			    
//
//		    string1 = "ADD MSR:IMPU=""sip:+55" & num & "@ims4.vivo.net.br"", SERVICEDATA/MMTelServices/version=1, SERVICEDATA/MMTelServices/complete-originating-identity-presentation/originating-identity-presentation/active=""true"", SERVICEDATA/MMTelServices/complete-originating-identity-presentation/operator-originating-identity-presentation/authorized=""true"", SERVICEDATA/MMTelServices/complete-originating-identity-presentation/operator-originating-identity-presentation/restriction-override=override-not-active, SERVICEDATA/MMTelServices/complete-originating-identity-presentation/operator-originating-identity-presentation/caller-display-name-present=""false"", SERVICEDATA/MMTelServices/complete-business-trunking-membership/business-trunking-membership/active=""true"", SERVICEDATA/MMTelServices/complete-business-trunking-membership/business-trunking-membership/display-pilot-number=""false"", SERVICEDATA/MMTelServices/complete-business-trunking-membership/operator-business-trunking-membership/authorized=""true"","
//		    string2 = "SERVICEDATA/MMTelServices/complete-business-trunking-membership/operator-business-trunking-membership/GroupIdentity=""sip:" & pabxid & """, SERVICEDATA/MMTelServices/complete-business-trunking-membership/operator-business-trunking-membership/share-pilot-service=""true"", SERVICEDATA/MMTelServices/complete-customized-collect-call/customized-collect-call/active=""true"", SERVICEDATA/MMTel-extension/basic-part/call-source-code=" & callsource & ", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/local=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/local-toll=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/national-toll=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/international-toll=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority1=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority2=""true"","
//		    string3 = "SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority3=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority4=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority5=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority6=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority7=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority8=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority9=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority10=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority11=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority12=""true"","
//		    string4 = "SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority13=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority14=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority15=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority16=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority17=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority18=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority19=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority20=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority21=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority22=""true"","
//		    string5 = "SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority23=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority24=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority25=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority26=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority27=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority28=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority29=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority30=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority31=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority32=""true"","
//		    string6 = "SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-high-entertainment-call-out=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-opertator=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/calltyping-local=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/calltyping-local-toll=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/calltyping-ld=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/calltyping-international-nanp=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/calltyping-international-world=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/calltyping-da=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/calltyping-osm=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/calltyping-osp=""true"", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/calltyping-osp1=""true"","
//		    string7 = "SERVICEDATA/MMTel-extension/basic-part/user-category=ordinary,SERVICEDATA/MMTel-extension/basic-part/limitation-of-parallel-calls=65535, SERVICEDATA/MMTel-extension/basic-part/announcement-set-id=254, SERVICEDATA/MMTel-extension/basic-part/voice-mailbox-address-index=65535, SERVICEDATA/MMTel-extension/basic-part/VCC-flag=1, SERVICEDATA/MMTel-extension/basic-part/display-index=65534, SERVICEDATA/OdbForImsOrientedServices/OdbForImsMultimediaTelephonyServices="""", SERVICEDATA/OdbForImsOrientedServices/OwedRestriction="""", SERVICEDATA/OdbForImsOrientedServices/LocationCallBarringService="""", SERVICEDATA/OdbForImsOrientedServices/DynamicHomeZoneService="""";"
//		    
//		    script_ims = script_ims & Chr(13) & Chr(10) & "USE ME:MENAME=ATS;"
//		    script_ims = script_ims & Chr(13) & Chr(10) & string1 & string2 & string3 & string4 & string5 & string6 & string7
//		    
//		End If
		
		
		
		if(tipoRamal.equals("piloto") && ims.equals("onecore")) {
			
			String string1 = String.format("ADD MSR:IMPU=\"sip:+55%s@%s\", SERVICEDATA/MMTelServices/version=1, SERVICEDATA/MMTelServices/complete-originating-identity-presentation/originating-identity-presentation/active=\"true\", SERVICEDATA/MMTelServices/complete-originating-identity-presentation/operator-originating-identity-presentation/authorized=\"true\",", numeroRamal, dominio);
			String string2 = "SERVICEDATA/MMTelServices/complete-originating-identity-presentation/operator-originating-identity-presentation/restriction-override=override-not-active, SERVICEDATA/MMTelServices/complete-originating-identity-presentation/operator-originating-identity-presentation/caller-display-name-present=\"false\", SERVICEDATA/MMTelServices/complete-business-trunking/operator-business-trunking/authorized=\"true\",";
			String string3 = String.format("SERVICEDATA/MMTelServices/complete-business-trunking/operator-business-trunking/GroupIdentity=\"sip:%s\", SERVICEDATA/MMTelServices/complete-business-trunking/operator-business-trunking/concurrent-call=0,SERVICEDATA/MMTelServices/complete-business-trunking/operator-business-trunking/cli-screen-service=ServiceInvalid, SERVICEDATA/MMTelServices/complete-customized-collect-call/customized-collect-call/active=\"true\",", pabxid);
			String string4 = String.format("SERVICEDATA/MMTel-extension/basic-part/call-source-code=%s, SERVICEDATA/MMTel-extension/basic-part/call-out-authority/local=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/local-toll=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/national-toll=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/international-toll=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority1=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority2=\"true\",", callsource);
			String string5 = "SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority3=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority4=\"true\",SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority5=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority6=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority7=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority8=\"true\",";
			String string6 = "SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority9=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority10=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority11=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority12=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority13=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority14=\"true\",";
			String string7 = "SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority15=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority16=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority17=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority18=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority19=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority20=\"true\",";
			String string8 = "SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority21=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority22=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority23=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority24=\"true\",SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority25=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority26=\"true\",";
			String string9 = "SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority27=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority28=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority29=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority30=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority31=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority32=\"true\",";
			String string10 = "SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-high-entertainment-call-out=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-opertator=\"true\",SERVICEDATA/MMTel-extension/basic-part/call-out-authority/calltyping-local=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/calltyping-local-toll=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/calltyping-ld=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/calltyping-international-nanp=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/calltyping-international-world=\"true\",";
			String string11 = "SERVICEDATA/MMTel-extension/basic-part/call-out-authority/calltyping-da=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/calltyping-osm=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/calltyping-osp=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/calltyping-osp1=\"true\", SERVICEDATA/MMTel-extension/basic-part/user-category=ordinary, SERVICEDATA/MMTel-extension/basic-part/limitation-of-parallel-calls=65535, SERVICEDATA/MMTel-extension/basic-part/announcement-set-id=254,SERVICEDATA/MMTel-extension/basic-part/voice-mailbox-address-index=65535, SERVICEDATA/MMTel-extension/basic-part/VCC-flag=1, SERVICEDATA/MMTel-extension/basic-part/display-index=65534, SERVICEDATA/OdbForImsOrientedServices/OdbForImsMultimediaTelephonyServices=\"\", SERVICEDATA/OdbForImsOrientedServices/OwedRestriction=\"\", SERVICEDATA/OdbForImsOrientedServices/LocationCallBarringService=\"\", SERVICEDATA/OdbForImsOrientedServices/DynamicHomeZoneService=\"\";";
				    
			scriptIms.append((char) 13); scriptIms.append((char) 10);
			scriptIms.append(String.format("USE ME:MENAME=ATS;"));
			scriptIms.append((char) 13); scriptIms.append((char) 10);
			scriptIms.append(string1 + " ");
			scriptIms.append(string2 + " ");
			scriptIms.append(string3 + " ");
			scriptIms.append(string4 + " ");
			scriptIms.append(string5 + " ");
			scriptIms.append(string6);
			scriptIms.append(string7 + " ");
			scriptIms.append(string8 + " ");
			scriptIms.append(string9 + " ");
			scriptIms.append(string10 + " ");
			scriptIms.append(string11);
			
		} else if(tipoRamal.equals("ramal") && ims.equals("onecore")) {
			
			String string1 = String.format("ADD MSR:IMPU=\"sip:+55%s@%s\", SERVICEDATA/MMTelServices/version=1, SERVICEDATA/MMTelServices/complete-originating-identity-presentation/originating-identity-presentation/active=\"true\", SERVICEDATA/MMTelServices/complete-originating-identity-presentation/operator-originating-identity-presentation/authorized=\"true\", SERVICEDATA/MMTelServices/complete-originating-identity-presentation/operator-originating-identity-presentation/restriction-override=override-not-active, SERVICEDATA/MMTelServices/complete-originating-identity-presentation/operator-originating-identity-presentation/caller-display-name-present=\"false\", SERVICEDATA/MMTelServices/complete-business-trunking-membership/business-trunking-membership/active=\"true\", SERVICEDATA/MMTelServices/complete-business-trunking-membership/business-trunking-membership/display-pilot-number=\"false\", SERVICEDATA/MMTelServices/complete-business-trunking-membership/operator-business-trunking-membership/authorized=\"true\",", numeroRamal, dominio);
			String string2 = String.format("SERVICEDATA/MMTelServices/complete-business-trunking-membership/operator-business-trunking-membership/GroupIdentity=\"sip:%s\", SERVICEDATA/MMTelServices/complete-business-trunking-membership/operator-business-trunking-membership/share-pilot-service=\"true\", SERVICEDATA/MMTelServices/complete-customized-collect-call/customized-collect-call/active=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-source-code=%s, SERVICEDATA/MMTel-extension/basic-part/call-out-authority/local=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/local-toll=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/national-toll=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/international-toll=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority1=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority2=\"true\",", pabxid, callsource);
			String string3 = "SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority3=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority4=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority5=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority6=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority7=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority8=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority9=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority10=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority11=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority12=\"true\",";
		    String string4 = "SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority13=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority14=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority15=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority16=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority17=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority18=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority19=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority20=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority21=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority22=\"true\",";
		    String string5 = "SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority23=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority24=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority25=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority26=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority27=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority28=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority29=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority30=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority31=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-call-out-authority32=\"true\",";
		    String string6 = "SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-high-entertainment-call-out=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/customized-opertator=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/calltyping-local=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/calltyping-local-toll=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/calltyping-ld=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/calltyping-international-nanp=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/calltyping-international-world=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/calltyping-da=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/calltyping-osm=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/calltyping-osp=\"true\", SERVICEDATA/MMTel-extension/basic-part/call-out-authority/calltyping-osp1=\"true\",";
		    String string7 = "SERVICEDATA/MMTel-extension/basic-part/user-category=ordinary,SERVICEDATA/MMTel-extension/basic-part/limitation-of-parallel-calls=65535, SERVICEDATA/MMTel-extension/basic-part/announcement-set-id=254, SERVICEDATA/MMTel-extension/basic-part/voice-mailbox-address-index=65535, SERVICEDATA/MMTel-extension/basic-part/VCC-flag=1, SERVICEDATA/MMTel-extension/basic-part/display-index=65534, SERVICEDATA/OdbForImsOrientedServices/OdbForImsMultimediaTelephonyServices=\"\", SERVICEDATA/OdbForImsOrientedServices/OwedRestriction=\"\", SERVICEDATA/OdbForImsOrientedServices/LocationCallBarringService=\"\", SERVICEDATA/OdbForImsOrientedServices/DynamicHomeZoneService=\"\";";
		    
//		    script_ims = script_ims & Chr(13) & Chr(10) & "USE ME:MENAME=ATS;"
//		    script_ims = script_ims & Chr(13) & Chr(10) & string1 & string2 & string3 & string4 & string5 & string6 & string7
		    scriptIms.append((char) 13); scriptIms.append((char) 10);
			scriptIms.append(String.format("USE ME:MENAME=ATS;"));
			scriptIms.append((char) 13); scriptIms.append((char) 10);
			scriptIms.append(string1);
			scriptIms.append(string2);
			scriptIms.append(string3);
			scriptIms.append(string4);
			scriptIms.append(string5);
			scriptIms.append(string6);
			scriptIms.append(string7);
			
		}
		
//		script_ims = script_ims & Chr(13) & Chr(10) & Chr(13) & Chr(10) & Chr(13) & Chr(10)
		scriptIms.append((char) 13); scriptIms.append((char) 10);
		scriptIms.append((char) 13); scriptIms.append((char) 10);
		scriptIms.append((char) 13); scriptIms.append((char) 10);
		
		return true;
	}
	
	private boolean criarScriptEns(StringBuilder scriptEns, String numeroRamal, String dominio, String tipoRamal, String ens) {
		
		if (tipoRamal.equals("piloto")) {
			scriptEns.append("USE ME:MENAME=ENS;");
			scriptEns.append((char) 13); scriptEns.append((char) 10);
		}
		
		//"ADD DNAPTRREC:E164NUM=""" & Right(num, 8) & """, ZONENAME=""" & Mid(num, 2, 1) & "." & Left(num, 1) & ".5.5.e164.arpa"", ORDER=10, PREFERENCE=101, FLAGS=""U"", SERVICE=""E2U+sip"", REGEXP=""!^(.*)$!sip:+\\1@" & dominio & "\;user=phone!"";"
		
		scriptEns.append(String.format("ADD DNAPTRREC:E164NUM=\"%s\", ZONENAME=\"%s.%s.5.5.e164.arpa\", ORDER=10, PREFERENCE=101, FLAGS=\"U\", SERVICE=\"E2U+sip\", REGEXP=\"!^(.*)$!sip:+\\\\1@%s\\;user=phone!\";", numeroRamal.substring(numeroRamal.length() - 8), numeroRamal.substring(1, 2), numeroRamal.substring(0, 1), dominio));
		scriptEns.append((char) 13); scriptEns.append((char) 10);
		
		return true;
	}
	
	private boolean scriptImsBanner(StringBuilder scriptIms, String type) {
		
		if (type.equals("create")) {
			
//			Print #1, ""
//			Print #1, "/*==========================================================*/"
//			Print #1, "/*===================== SCRIPT CRIAÇÃO =====================*/"
//			Print #1, "/*==========================================================*/"
//
//			Print #2, ""
//			Print #2, "/*==========================================================*/"
//			Print #2, "/*===================== SCRIPT CRIAÇÃO =====================*/"
//			Print #2, "/*==========================================================*/"
//			
//			Print #1, ""
//			Print #2, ""
			scriptIms.append("");
			scriptIms.append((char) 13); scriptIms.append((char) 10);
			scriptIms.append("/*==========================================================*/");
			scriptIms.append((char) 13); scriptIms.append((char) 10);
			scriptIms.append("/*===================== SCRIPT CRIAÇÃO =====================*/");
			scriptIms.append((char) 13); scriptIms.append((char) 10);
			scriptIms.append("/*==========================================================*/");
			scriptIms.append((char) 13); scriptIms.append((char) 10);
			scriptIms.append("");
			scriptIms.append((char) 13); scriptIms.append((char) 10);
		}
		
		if (type.equals("remove")) {
			
			//			Print #1, "/*==========================================================*/"
			//			Print #1, "/*=============== SCRIPT REMOÇÃO (SE CRIADO) ===============*/"
			//			Print #1, "/*==========================================================*/"
			//
			//			Print #2, "/*==========================================================*/"
			//			Print #2, "/*=============== SCRIPT REMOÇÃO (SE CRIADO) ===============*/"
			//			Print #2, "/*==========================================================*/"
			//
			//			Print #1, ""
			//			Print #2, ""
			
			scriptIms.append("");
			scriptIms.append((char) 13); scriptIms.append((char) 10);
			scriptIms.append("/*==========================================================*/");
			scriptIms.append((char) 13); scriptIms.append((char) 10);
			scriptIms.append("/*=============== SCRIPT REMOÇÃO (SE CRIADO) ===============*/");
			scriptIms.append((char) 13); scriptIms.append((char) 10);
			scriptIms.append("/*==========================================================*/");
			scriptIms.append((char) 13); scriptIms.append((char) 10);
			scriptIms.append("");
			scriptIms.append((char) 13); scriptIms.append((char) 10);
			
			return true;
		}
		
		return true;
	}
	
	private boolean scriptBcfBanner(StringBuilder scriptBcf, String type) {
		
		if (type.equals("create")) {
			
//			Print #1, ""
//			Print #1, "=========================================================="
//			Print #1, "===================== SCRIPT CRIAÇÃO ====================="
//			Print #1, "=========================================================="
//			Print #1, ""
			
			scriptBcf.append("");
			scriptBcf.append((char) 13); scriptBcf.append((char) 10);
			scriptBcf.append("/*==========================================================*/");
			scriptBcf.append((char) 13); scriptBcf.append((char) 10);
			scriptBcf.append("/*===================== SCRIPT CRIAÇÃO =====================*/");
			scriptBcf.append((char) 13); scriptBcf.append((char) 10);
			scriptBcf.append("/*==========================================================*/");
			scriptBcf.append((char) 13); scriptBcf.append((char) 10);
			scriptBcf.append("");
			scriptBcf.append((char) 13); scriptBcf.append((char) 10);
		}
		
		if (type.equals("remove")) {
			
//			Print #1, ""
//			Print #1, "=========================================================="
//			Print #1, "=============== SCRIPT REMOÇÃO (SE CRIADO) ==============="
//			Print #1, "=========================================================="
//			Print #1, ""
			
			scriptBcf.append("");
			scriptBcf.append((char) 13); scriptBcf.append((char) 10);
			scriptBcf.append("/*==========================================================*/");
			scriptBcf.append((char) 13); scriptBcf.append((char) 10);
			scriptBcf.append("/*=============== SCRIPT REMOÇÃO (SE CRIADO) ===============*/");
			scriptBcf.append((char) 13); scriptBcf.append((char) 10);
			scriptBcf.append("/*==========================================================*/");
			scriptBcf.append((char) 13); scriptBcf.append((char) 10);
			scriptBcf.append("");
			scriptBcf.append((char) 13); scriptBcf.append((char) 10);
			
			return true;
		}
		
		return true;
	}
	
	private boolean scriptSbcBanner(StringBuilder scriptSbc, String type) {
		
		if (type.equals("create")) {
			
//			Print #1, ""
//			Print #1, "=========================================================="
//			Print #1, "===================== SCRIPT CRIAÇÃO ====================="
//			Print #1, "=========================================================="
//			Print #1, ""
			
			scriptSbc.append("");
			scriptSbc.append((char) 13); scriptSbc.append((char) 10);
			scriptSbc.append("/*==========================================================*/");
			scriptSbc.append((char) 13); scriptSbc.append((char) 10);
			scriptSbc.append("/*===================== SCRIPT CRIAÇÃO =====================*/");
			scriptSbc.append((char) 13); scriptSbc.append((char) 10);
			scriptSbc.append("/*==========================================================*/");
			scriptSbc.append((char) 13); scriptSbc.append((char) 10);
			scriptSbc.append("");
			scriptSbc.append((char) 13); scriptSbc.append((char) 10);
		}
		
		if (type.equals("remove")) {
			
//			Print #1, ""
//			Print #1, "=========================================================="
//			Print #1, "=============== SCRIPT REMOÇÃO (SE CRIADO) ==============="
//			Print #1, "=========================================================="
//			Print #1, ""
			
			scriptSbc.append("");
			scriptSbc.append((char) 13); scriptSbc.append((char) 10);
			scriptSbc.append("/*==========================================================*/");
			scriptSbc.append((char) 13); scriptSbc.append((char) 10);
			scriptSbc.append("/*=============== SCRIPT REMOÇÃO (SE CRIADO) ===============*/");
			scriptSbc.append((char) 13); scriptSbc.append((char) 10);
			scriptSbc.append("/*==========================================================*/");
			scriptSbc.append((char) 13); scriptSbc.append((char) 10);
			scriptSbc.append("");
			scriptSbc.append((char) 13); scriptSbc.append((char) 10);
			
			return true;
		}
		
		return true;
	}
	
	private boolean scriptEnsBanner(StringBuilder scriptEns, String type) {
		
		if (type.equals("create")) {
			
//			Print #1, ""
//			Print #1, "/*==========================================================*/"
//			Print #1, "/*===================== SCRIPT CRIAÇÃO =====================*/"
//			Print #1, "/*==========================================================*/"
//
//			Print #2, ""
//			Print #2, "/*==========================================================*/"
//			Print #2, "/*===================== SCRIPT CRIAÇÃO =====================*/"
//			Print #2, "/*==========================================================*/"
//			
//			Print #1, ""
//			Print #2, ""
			scriptEns.append("");
			scriptEns.append((char) 13); scriptEns.append((char) 10);
			scriptEns.append("/*==========================================================*/");
			scriptEns.append((char) 13); scriptEns.append((char) 10);
			scriptEns.append("/*===================== SCRIPT CRIAÇÃO =====================*/");
			scriptEns.append((char) 13); scriptEns.append((char) 10);
			scriptEns.append("/*==========================================================*/");
			scriptEns.append((char) 13); scriptEns.append((char) 10);
			scriptEns.append("");
			scriptEns.append((char) 13); scriptEns.append((char) 10);
		}
		
		if (type.equals("remove")) {
			
			//			Print #1, "/*==========================================================*/"
			//			Print #1, "/*=============== SCRIPT REMOÇÃO (SE CRIADO) ===============*/"
			//			Print #1, "/*==========================================================*/"
			//
			//			Print #2, "/*==========================================================*/"
			//			Print #2, "/*=============== SCRIPT REMOÇÃO (SE CRIADO) ===============*/"
			//			Print #2, "/*==========================================================*/"
			//
			//			Print #1, ""
			//			Print #2, ""
			
			scriptEns.append("");
			scriptEns.append((char) 13); scriptEns.append((char) 10);
			scriptEns.append("/*==========================================================*/");
			scriptEns.append((char) 13); scriptEns.append((char) 10);
			scriptEns.append("/*=============== SCRIPT REMOÇÃO (SE CRIADO) ===============*/");
			scriptEns.append((char) 13); scriptEns.append((char) 10);
			scriptEns.append("/*==========================================================*/");
			scriptEns.append((char) 13); scriptEns.append((char) 10);
			scriptEns.append("");
			scriptEns.append((char) 13); scriptEns.append((char) 10);
			
			return true;
		}
		
		return true;
	}
	
	private boolean removerScriptIms(StringBuilder scriptIms, String numeroRamal, String tipoRamal, String ims, String type) {
		
		if (type.equals("gera_hss_hsub")) {
			
			if (tipoRamal.equals("piloto")) {
				scriptIms.append("USE ME:MENAME=HSS;");
				scriptIms.append((char) 13); scriptIms.append((char) 10);
			}
			
	//		gera_hss_hsub = "RMV HSUB: SUBID=""+55" & num & """;"
			
			scriptIms.append(String.format("RMV HSUB: SUBID=\"+55%s\";", numeroRamal));
			scriptIms.append((char) 13); scriptIms.append((char) 10);
			
			return true;
		}
		
		if (type.equals("gera_hss_hsdainf")) {
			
	//		gera_hss_hsdainf = "RMV HSDAINF: IMPI=""+55" & num & """;"
			
			scriptIms.append(String.format("RMV HSDAINF: IMPI=\"+55%s\";", numeroRamal));
			scriptIms.append((char) 13); scriptIms.append((char) 10);
			
			return true;
		}
		
		return true;
	}
	
	private boolean removerScriptBcf(StringBuilder script, String tgid, String tgid2) {
		
//		'Print #1, "USE ME:MEID=5;"
//		Print #1, "RMV RT: RTID=" & tg_id & ";"
//		Print #1, "RMV SRT: SRTID=" & tg_id & ";"
//		Print #1, "RMV SIPTG: TGID=" & tg_id2 & ";"
//		Print #1, "RMV SIPTG: TGID=" & tg_id & ";"
		
		StringBuilder scriptLocal = new StringBuilder();
		
		scriptLocal.append(String.format("RMV RT: RTID=%s;", tgid));
		scriptLocal.append((char) 13); script.append((char) 10);
		scriptLocal.append(String.format("RMV SRT: SRTID=%s;", tgid));
		scriptLocal.append((char) 13); script.append((char) 10);
		scriptLocal.append(String.format("RMV SIPTG: TGID=%s;", tgid2));
		scriptLocal.append((char) 13); script.append((char) 10);
		scriptLocal.append(String.format("RMV SIPTG: TGID=%s;", tgid));
		scriptLocal.append((char) 13); script.append((char) 10);
		
		processoIdDomain.setScriptRemocaoBcf(scriptLocal.toString());
		script.append(scriptLocal.toString());
		
		return true;
	}
	
	private boolean removerScriptSbc(StringBuilder script, String tgid, String troncoChave, String pabxid, String sbcPri, String sbcSec, String uport) {
		
		switch (sbcPri) {
		case "NGN-BR-BRU-AC-SBC":
			processoIdDomain.setHostSbcPrimario(sbcPri);
			processoIdDomain.setModeloSbcPrimario(ModelosSbcEnum.SONUS.getCodigo());
			processoIdDomain.setScriptRemocaoSbcPrimario(new StringBuilder());
			
			script.append(String.format("#### Executar os comandos abaixo no SBC %s (SONUS SWe) ####", sbcPri));
			script.append((char) 13); script.append((char) 10);
			removeSubSonus(script, tgid, troncoChave, pabxid, processoIdDomain.getScriptRemocaoSbcPrimario());
			
			processoIdDomain.setHostSbcSecundario(sbcSec);
			processoIdDomain.setModeloSbcSecundario(ModelosSbcEnum.GENBAND.getCodigo());
			processoIdDomain.setScriptRemocaoSbcSecundario(new StringBuilder());
			
			script.append((char) 13); script.append((char) 10);
			script.append(String.format("#### Executar os comandos abaixo no SBC %s (GENBAND Q21)####", sbcSec));
			removeSubGenband(script, pabxid, uport, processoIdDomain.getScriptRemocaoSbcSecundario());
			
			break;
		
		case "NGN-BR-ARQ-FI-SBC":
			
			processoIdDomain.setHostSbcPrimario(sbcPri);
			processoIdDomain.setModeloSbcPrimario(ModelosSbcEnum.GENBAND.getCodigo());
			processoIdDomain.setScriptRemocaoSbcPrimario(new StringBuilder());
			
			script.append(String.format("#### Executar os comandos abaixo no SBC %s (GENBAND Q21) ####", sbcPri));
			script.append((char) 13); script.append((char) 10);
			removeSubGenband(script, pabxid, uport, processoIdDomain.getScriptRemocaoSbcPrimario());
			
			processoIdDomain.setHostSbcSecundario(sbcSec);
			processoIdDomain.setModeloSbcSecundario(ModelosSbcEnum.SONUS.getCodigo());
			processoIdDomain.setScriptRemocaoSbcSecundario(new StringBuilder());
			
			script.append((char) 13); script.append((char) 10);
			script.append(String.format("#### Executar os comandos abaixo no SBC %s (SONUS SWe) ####", sbcSec));
			removeSubSonus(script, tgid, troncoChave, pabxid, processoIdDomain.getScriptRemocaoSbcSecundario());
			
			break;
		
		case "NGN-BR-CAS-CT-SBC":
		case "NGN-BR-CAS-BON-SBC":
		case "NGN-BR-SPO-PA-SBC":
		case "NGN-BR-SPO-SI-SBC":
			
			processoIdDomain.setHostSbcPrimario(sbcPri);
			processoIdDomain.setModeloSbcPrimario(ModelosSbcEnum.SONUS.getCodigo());
			processoIdDomain.setScriptRemocaoSbcPrimario(new StringBuilder());
			processoIdDomain.setHostSbcSecundario(sbcSec);
			processoIdDomain.setModeloSbcSecundario(ModelosSbcEnum.SONUS.getCodigo());
			
			script.append(String.format("#### Executar os comandos abaixo no SBCs %s e %s (Ambos SONUS SWe)####", sbcPri, sbcSec));
			script.append((char) 13); script.append((char) 10);
			removeSubSonus(script, tgid, troncoChave, pabxid, processoIdDomain.getScriptRemocaoSbcPrimario());
			processoIdDomain.setScriptRemocaoSbcSecundario(processoIdDomain.getScriptRemocaoSbcPrimario());
			
			break;
		
		default:
			
			processoIdDomain.setHostSbcPrimario(sbcPri);
			processoIdDomain.setModeloSbcPrimario(ModelosSbcEnum.GENBAND.getCodigo());
			processoIdDomain.setScriptRemocaoSbcPrimario(new StringBuilder());
			processoIdDomain.setHostSbcSecundario(sbcSec);
			processoIdDomain.setModeloSbcSecundario(ModelosSbcEnum.GENBAND.getCodigo());
			
			script.append(String.format("#### Executar os comandos abaixo no SBCs %s e %s (Ambos GENBAND Q21) ####", sbcPri, sbcSec));
			script.append((char) 13); script.append((char) 10);
			removeSubGenband(script, pabxid, uport, processoIdDomain.getScriptRemocaoSbcPrimario());
			processoIdDomain.setScriptRemocaoSbcSecundario(processoIdDomain.getScriptRemocaoSbcPrimario());
			
			break;
		}
		
		return true;
	}
	
	private boolean removerScriptEns(StringBuilder scriptEns, String numeroRamal, String tipoRamal, String ens) {
		
		if (tipoRamal.equals("piloto")) {
			scriptEns.append("");
			scriptEns.append((char) 13); scriptEns.append((char) 10);
			scriptEns.append("USE ME:MENAME=ENS;");
			scriptEns.append((char) 13); scriptEns.append((char) 10);
		}
		
		//remove_ens = "RMV DNAPTRREC:E164NUM=""" & Right(num, 8) & """, ZONENAME=""" & Mid(num, 2, 1) & "." & Left(num, 1) & ".5.5.e164.arpa"", ENUMFLAG=ENS_TYPE;"
		
		scriptEns.append(String.format("RMV DNAPTRREC:E164NUM=\"%s\", ZONENAME=\"%s.%s.5.5.e164.arpa\", ENUMFLAG=ENS_TYPE;", numeroRamal.substring(numeroRamal.length() - 8), numeroRamal.substring(1, 2), numeroRamal.substring(0, 1)));
		scriptEns.append((char) 13); scriptEns.append((char) 10);
		
		return true;
	}
	
	private void removeSubSonus(StringBuilder script, String tgid, String troncoChave, String pabxid, StringBuilder scriptSbc) {
		
		
//		sonus:
//		    Print #1, ""
//		    Print #1, "configure"
//		    Print #1, "set addressContext default zone privaccess-voxip sipTrunkGroup " & pabxid & " state disabled"
//		    Print #1, "set addressContext default zone privaccess-voxip sipTrunkGroup " & pabxid & " mode outOfService"
//		    Print #1, "commit"
//		    Print #1, "delete addressContext default zone privaccess-voxip sipTrunkGroup " & pabxid
//		    Print #1, "delete addressContext default zone privaccess-voxip ipPeer " & pabxid
//		    Print #1, "delete profiles ipSignalingPeerGroup " & pabxid & "_IPSIGP"
//		    Print #1, "delete profiles digitParameterHandling dmPmRule voxip" & tg_id & "_" & tronco_chave
//		    Print #1, "commit"
//		    Print #1, ""
//		    Print #1, ""
//		    Print #1, ""
		
		StringBuilder scriptLocal = new StringBuilder();
		
		scriptLocal.append("");
		scriptLocal.append((char) 13); script.append((char) 10);
		scriptLocal.append("configure");
		scriptLocal.append((char) 13); script.append((char) 10);
		scriptLocal.append(String.format("set addressContext default zone privaccess-voxip sipTrunkGroup %s state disabled", pabxid));
		scriptLocal.append((char) 13); script.append((char) 10);
		scriptLocal.append(String.format("set addressContext default zone privaccess-voxip sipTrunkGroup %s mode outOfService", pabxid));
		scriptLocal.append((char) 13); script.append((char) 10);
		scriptLocal.append(String.format("commit"));
		scriptLocal.append((char) 13); script.append((char) 10);
		scriptLocal.append(String.format("delete addressContext default zone privaccess-voxip sipTrunkGroup %s", pabxid));
		scriptLocal.append((char) 13); script.append((char) 10);
		scriptLocal.append(String.format("delete addressContext default zone privaccess-voxip ipPeer %s", pabxid));
		scriptLocal.append((char) 13); script.append((char) 10);
		scriptLocal.append(String.format("delete profiles ipSignalingPeerGroup %s_IPSIGP", pabxid));
		scriptLocal.append((char) 13); script.append((char) 10);
		scriptLocal.append(String.format("delete profiles digitParameterHandling dmPmRule voxip%s_%s", tgid, troncoChave));
		scriptLocal.append((char) 13); script.append((char) 10);
		scriptLocal.append(String.format("commit"));
		scriptLocal.append((char) 13); script.append((char) 10);
		scriptLocal.append(String.format(""));
		scriptLocal.append((char) 13); script.append((char) 10);
		
		scriptSbc.append(scriptLocal.toString());
		script.append(scriptLocal.toString());
	}
	
	private void removeSubGenband(StringBuilder script, String pabxid, String uport, StringBuilder scriptSbc) {
		
//		genband:
//		    Print #1, ""
//		    Print #1, "cli iedge delete PBX_" & pabxid & " " & uport & ""
//		    Print #1, ""
//		    Print #1, ""
//		    Print #1, ""
		
		StringBuilder scriptLocal = new StringBuilder();
		
		scriptLocal.append(String.format(""));
		scriptLocal.append((char) 13); script.append((char) 10);
		scriptLocal.append(String.format("cli iedge delete PBX_%s %s", pabxid, uport));
		scriptLocal.append((char) 13); script.append((char) 10);
		scriptLocal.append(String.format(""));
		scriptLocal.append((char) 13); script.append((char) 10);
		
		scriptSbc.append(scriptLocal.toString());
		script.append(scriptLocal.toString());
	}

	/**
	 * 
	 * @param jsonDados
	 * @return
	 */
	private boolean buscarSbcIps(JSONObject jsonDados) {
		
		JSONObject aux = null;
		try {
			aux = new VivoB2BDao().buscarSbcIps(jsonDados.getString("cnl"), jsonDados.getString("at"));
		} catch (Exception e) {
			e.printStackTrace();
		}		
		
		if(aux == null || 
				StringUtils.isBlank(aux.optString("sbcPri", "")) || 
				StringUtils.isBlank(aux.optString("ipPrincipal", "")) || 
				StringUtils.isBlank(aux.optString("ipRedundante", "")) || 
				StringUtils.isBlank(aux.optString("sbcSec", ""))) {
			processoIdDomain.setResultadoProcedimento("falta_informacoes_sbc_ips");
			processoIdDomain.setDataFinal(new Date());
			this.processoIdRepository.save(processoIdDomain);			
			return false;
		}
		
		// setando os dados IPs SBC
		processoIdDomain.setDadosSbcIps(aux.toString());
		
		// salvando
		try { this.processoIdRepository.save(processoIdDomain); } catch (Exception e) {}
		
		return true;
	}
	
	/**
	 * 
	 * @param jsonDados
	 * @return
	 */
	private boolean verificaCamposStar(JSONObject jsonDados) {
		
		if(IatConstants.codigoServidor == CodigoServidoresEnum.SERVIDOR_DEV.getCodigoServidor()) {
			System.out.println("tipoProcesso: " + jsonDados.optString("tipoProcesso", ""));
			System.out.println("produto: " + jsonDados.optString("produto", ""));
			System.out.println("idTecnologia: " + jsonDados.optString("idTecnologia", ""));
			System.out.println("pePrincipal: " + jsonDados.optString("pePrincipal", ""));
			System.out.println("peBackup: " + jsonDados.optString("peBackup", ""));
			System.out.println("interfacePePrincipal: " + jsonDados.optString("interfacePePrincipal", ""));
			System.out.println("interfacePeBackup: " + jsonDados.optString("interfacePeBackup", ""));
			System.out.println("ipWanPe: " + jsonDados.optString("ipWanPe", ""));
			System.out.println("ipLoopback: " + jsonDados.optString("ipLoopback", ""));
			System.out.println("ipLanCpe: " + jsonDados.optString("ipLanCpe", ""));
		}
		
		// tipo processo
		if(!StringUtils.containsIgnoreCase(jsonDados.getString("tipoProcesso"), "Venda Normal") 
				&& !StringUtils.containsIgnoreCase(jsonDados.getString("tipoProcesso"), "Retirada")) {
			processoIdDomain.setResultadoProcedimento("tipo_venda_nao_suportado_no_momento");
			processoIdDomain.setDataFinal(new Date());
			this.processoIdRepository.save(processoIdDomain);			
			return false;
		}
		
		// produto
		if(!StringUtils.containsIgnoreCase(jsonDados.getString("produto"), "SIP ")
				&& !StringUtils.containsIgnoreCase(jsonDados.getString("produto"), "IP DEDICADO")) {
			processoIdDomain.setResultadoProcedimento("produto_nao_suportado_no_momento");
			processoIdDomain.setDataFinal(new Date());
			this.processoIdRepository.save(processoIdDomain);			
			return false;
		}
		
		// sipRamais
		try {
			if(!jsonDados.has("sipRamais") || jsonDados.optJSONArray("sipRamais") == null) {
//				processoIdDomain.setResultadoProcedimento("falta_informacoes_star_sip_ramais");
//				processoIdDomain.setDataFinal(new Date());
//				this.processoIdRepository.save(processoIdDomain);			
//				return false;
				// Alguns casos não é necessário sipRamais
				// Portanto deve continuar
				jsonDados.put("sipRamais", new JSONArray());
			}
		} catch (Exception e) {
			processoIdDomain.setResultadoProcedimento("falta_informacoes_star_sip_ramais");
			processoIdDomain.setDataFinal(new Date());
			this.processoIdRepository.save(processoIdDomain);			
			return false;
		}
		
		// sipPiloto
		if(StringUtils.isBlank(jsonDados.optString("sipPiloto", ""))) {
			processoIdDomain.setResultadoProcedimento("falta_informacoes_star_sip_piloto");
			processoIdDomain.setDataFinal(new Date());
			this.processoIdRepository.save(processoIdDomain);			
			return false;
		}
		
		// sipIpCliente
		if(StringUtils.isBlank(jsonDados.optString("sipIpCliente", ""))) {
			processoIdDomain.setResultadoProcedimento("falta_informacoes_star_sip_ip_cliente");
			processoIdDomain.setDataFinal(new Date());
			this.processoIdRepository.save(processoIdDomain);			
			return false;
		}
		
		// sipCanais
		if(StringUtils.isBlank(jsonDados.optString("sipCanais", ""))) {
			processoIdDomain.setResultadoProcedimento("falta_informacoes_star_sip_canais");
			processoIdDomain.setDataFinal(new Date());
			this.processoIdRepository.save(processoIdDomain);			
			return false;
		}
		
		// sipCanais
		if(StringUtils.isBlank(jsonDados.optString("at", "")) || StringUtils.isBlank(jsonDados.optString("cnl", ""))) {
			processoIdDomain.setResultadoProcedimento("falta_informacoes_star_sip_at_cnl");
			processoIdDomain.setDataFinal(new Date());
			this.processoIdRepository.save(processoIdDomain);			
			return false;
		}
		
		// Tipo Tráfego = Rajada
		// SIP Centralizado = SIM
		if (StringUtils.containsIgnoreCase(jsonDados.optString("sipTipoTrafego"), "Rajada") || 
			StringUtils.containsIgnoreCase(jsonDados.optString("sipCentralizado"), "SIM")) {
			
			if (StringUtils.containsIgnoreCase(jsonDados.optString("sipCentralizado"), "SIM")) {
				processoIdDomain.setResultadoProcedimento("sip_centralizado_nao_deve_ser_configurado_como_one_core");
			} else if (StringUtils.containsIgnoreCase(jsonDados.optString("sipTipoTrafego"), "Rajada")) {
				processoIdDomain.setResultadoProcedimento("sip_tipo_trafego_rajada_nao_deve_ser_configurado_como_one_core");
			}
			
			processoIdDomain.setDataFinal(new Date());
			this.processoIdRepository.save(processoIdDomain);
			return false;
		}
		
		// salvando
		try { this.processoIdRepository.save(processoIdDomain); } catch (Exception e) {}
		
		return true;
	}

	/**
	 * buscaDadosStar
	 * @param idVantive
	 * @return
	 */
	private JSONObject buscaDadosStar(String idVantive) {
		
		for (int c1 = 0; c1 < 3; c1++) {
			try {
				// buscando os dados no star
				String token = new StringHelper().generateToken();		
				JSONObject jsonReq = new JSONObject();
				
				SendHttpRequestService sendHttpRequestService = new SendHttpRequestService();
				String retorno = "";
				//sendHttpRequestService.sendGetRequest(String.format("%s/vivo-b2b-api/star/buscarId/%s/%s/false", IatConstants.API_VIVO_B2B_URL, token, idVantive), token, jsonReq);
				if(IatConstants.DEBUG)
					System.out.println(retorno);
				
				if(StringUtils.isBlank(retorno) || !retorno.equals("busca_iniciada"))
					continue;
				
				// loop para esperar a consulta do star
				
				int c = 0;
				while (c < 30) {
					// sleep
					try { Thread.sleep(5 * 1000); } catch (Exception e) {}
					
					sendHttpRequestService = new SendHttpRequestService();
					//retorno = sendHttpRequestService.sendGetRequest(String.format("%s/vivo-b2b-api/star/statusBuscaId/%s", IatConstants.API_VIVO_B2B_URL, token), token, jsonReq);
					c++;
					if(IatConstants.DEBUG)
						System.out.println(retorno);
					
					if(StringUtils.isBlank(retorno) || retorno.equals("error") || retorno.equals("token_invalido"))
						return null;
					
					if(retorno.equals("busca_em_andamento"))
						continue;
					
					break;
				}
				
				// convertendo o retorno para json
				
				JSONObject jsonDados = null;
				try {
					jsonDados = new JSONObject(retorno);
				} catch (Exception e) {}
				
				if(jsonDados == null || StringUtils.isBlank(jsonDados.optString("tipoProcesso", "")) || StringUtils.isBlank(jsonDados.optString("status", "")))
					continue;
				
				return jsonDados;
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}	
		}
		
		return null;		
	}
	
	/**
	 * buscaDadosTxtStar
	 * @param idVantive
	 * @return
	 */
	private JSONObject buscaDadosTxtStar(String idVantive) {
		
		for (int c1 = 0; c1 < 3; c1++) {
			try {
				// buscando os dados no star
				String token = new StringHelper().generateToken();
				JSONObject jsonReq = new JSONObject();
				
				SendHttpRequestService sendHttpRequestService = new SendHttpRequestService();
				String retorno = "";
				//sendHttpRequestService.sendGetRequest(String.format("%s/vivo-b2b-api/star/buscarSipOneCore/%s/%s", 
				//		IatConstants.API_VIVO_B2B_URL, token, idVantive), token, jsonReq);
				if(IatConstants.DEBUG)
					System.out.println(retorno);
				
				if(StringUtils.isBlank(retorno) || !retorno.equals("busca_iniciada"))
					continue;
				
				// loop para esperar a consulta do star
				
				int c = 0;
				while (c < 30) {
					// sleep
					try { Thread.sleep(5 * 1000); } catch (Exception e) {}
					
					sendHttpRequestService = new SendHttpRequestService();
					//retorno = sendHttpRequestService.sendGetRequest(String.format("%s/vivo-b2b-api/star/statusBuscaSipOneCore/%s", 
					//		IatConstants.API_VIVO_B2B_URL, token), token, jsonReq);
					c++;
					if(IatConstants.DEBUG)
						System.out.println(retorno);
					
					if(StringUtils.isBlank(retorno) || retorno.equals("error") || retorno.equals("token_invalido"))
						return null;
					
					if(retorno.equals("busca_em_andamento"))
						continue;
					
					break;
				}
				
				// convertendo o retorno para json
				
				JSONObject jsonDados = null;
				try {
					jsonDados = new JSONObject(retorno);
				} catch (Exception e) {}
				
				if(jsonDados == null || jsonDados.isEmpty() || 
						StringUtils.isBlank(jsonDados.optString("location")) ||
						StringUtils.isBlank(jsonDados.optString("callSource")) ||
						StringUtils.isBlank(jsonDados.optString("pabxid")) ||
						StringUtils.isBlank(jsonDados.optString("tgid")))
					continue;
				
				return jsonDados;
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
		
		return null;
	}
	
	/**
	 * Metodo que verifica objeto na sessão 
	 */
	public void verificaObjetoSessao() {
		
		try {
			Thread.sleep(60000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if(session != null && processoIdDomain != null && session.getAttribute("processoIdDomain_" + processoIdDomain.getToken()) != null) {
			// Removendo o objeto de testes da sessao
			session.setAttribute("processoIdDomain_" + processoIdDomain.getToken(), null);
			session.removeAttribute("processoIdDomain_" + processoIdDomain.getToken());
		}
	}
	
	public void sleepMs(long delayMs) {
		try {
			Thread.sleep(delayMs);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
