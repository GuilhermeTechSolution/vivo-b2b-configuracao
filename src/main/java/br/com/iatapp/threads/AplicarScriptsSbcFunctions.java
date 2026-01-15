package br.com.iatapp.threads;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONObject;

import br.com.iatapp.domain.SipOneCoreProcessoIdDomain;
import br.com.iatapp.enums.GlobalStrEnum;
import br.com.iatapp.enums.ModelosSbcEnum;
import br.com.iatapp.enums.TipoProcedimentoSipOneCoreEnum;
import br.com.iatapp.helper.RedeHelper;
import br.com.iatapp.helper.StringHelper;
import br.com.iatapp.logger.ExceptionLogger;
import br.com.iatapp.rede.RedeIpFunctions;

public class AplicarScriptsSbcFunctions {
	
	private RedeIpFunctions redeIpFunctions;
	private SipOneCoreProcessoIdDomain processoIdDomain;
	private StringBuilder log;
	
	public AplicarScriptsSbcFunctions(RedeIpFunctions redeIpFunctions) {
		this.redeIpFunctions = redeIpFunctions;
	}
	
	public AplicarScriptsSbcFunctions(RedeIpFunctions redeIpFunctions, 
			SipOneCoreProcessoIdDomain processoIdDomain) {
		this.redeIpFunctions = redeIpFunctions;
		this.processoIdDomain = processoIdDomain;
		this.log = new StringBuilder();
	}
	
	public void iniciaProcedimento() {
		
		try {
			
			// label etapa teste online
			processoIdDomain.setEtapaTesteOnline("Conectando servidor SBC Primário");
			
			aplicarScript(processoIdDomain.getHostSbcPrimario(), processoIdDomain.getModeloSbcPrimario(), 
					processoIdDomain.getScriptSbcPrimario(), processoIdDomain.getScriptRemocaoSbcPrimario());
			
			// label etapa teste online
			processoIdDomain.setEtapaTesteOnline("Conectando servidor SBC Backup");
			
			aplicarScript(processoIdDomain.getHostSbcSecundario(), processoIdDomain.getModeloSbcSecundario(), 
					processoIdDomain.getScriptSbcSecundario(), processoIdDomain.getScriptRemocaoSbcSecundario());
			
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e),
					RedeHelper.retornaInfoProcedimento(AplicarScriptsSbcFunctions.class.getName(), processoIdDomain.getIdVantive(), "iniciaProcedimento"));
		}
		
	}
	
	private void aplicarScript(String hostname, int modelo, StringBuilder sbScript, StringBuilder sbScriptRemocao) {
		
		// Pegando os ips
		try {
			
			/* CONECTAR */
			String comando = "";
			String retorno = "";
				
			comando = String.format("grep -i '%s' /etc/hosts", hostname);
			
			retorno = redeIpFunctions.enviarComandoAvailable(comando, new String[] {">", "#", "~]$",":~$", ":/]$"},
					RedeHelper.retornaInfoProcedimento(AplicarScriptsSbcFunctions.class.getName(), processoIdDomain.getIdVantive(), "/etc/hosts"));
			
			if (StringUtils.isBlank(retorno)) {
				return;
			}
			redeIpFunctions.getLog().append(retorno);
			
			boolean aplicarScript = false;
			String strEquipamentoCharParada = "";
			
			String[] linhasRetorno = retorno.split("" + GlobalStrEnum.BREAK_LINE.toString() + "");
			LinkedHashMap<String,String> hm = new LinkedHashMap<String,String>();
			
			for (int c = 0; c < linhasRetorno.length; c++) {
				String linhaAux = linhasRetorno[c];
				String strIp = StringHelper.searchPattern(linhaAux, GlobalStrEnum.IPADDRESS_PATTERN.toString());
				// Buscar os ips referente ao modelo sbc
//				[g0050914@gbrssh1:~]$ grep -i 'NGN-BR-SPO-si-SBC' /etc/hosts
//				10.229.12.72    ngn-br-spo-si-sbc1      GENBAND COMUTACAO
//				10.229.12.74    ngn-br-spo-si-sbc2      GENBAND COMUTACAO
//				10.113.124.114  ngn-br-spo-si-sbc3      SONUS   COMUTACAO
//				10.113.124.115  ngn-br-spo-si-sbc4      SONUS   COMUTACAO
				if(linhaAux != null &&
					StringUtils.isNotBlank(strIp) &&
					StringUtils.containsIgnoreCase(linhaAux, ModelosSbcEnum.getNome(modelo))) {
					
//					10.229.12.72	[01;31m[Kngn-br-spo-si-sbc[m[K1	GENBAND	COMUTACAO
//					10.229.12.74	[01;31m[Kngn-br-spo-si-sbc[m[K2	GENBAND	COMUTACAO
					// formatar
					linhaAux = StringHelper.removerCodigoCores(linhaAux);
					linhaAux = StringUtils.substringAfter(linhaAux, strIp);
					strEquipamentoCharParada = StringUtils.substringBefore(linhaAux.trim(), " ");
					if (StringUtils.isBlank(strEquipamentoCharParada))
						strEquipamentoCharParada = "";
					hm.put(strIp, strEquipamentoCharParada);
				}
			}
			
			strEquipamentoCharParada = "";
			for (Map.Entry<String, String> em : hm.entrySet()) {
				
				try {
					
					String strIp = em.getKey();
					String strHostname = em.getValue();
					
					if (StringUtils.isBlank(strIp))
						continue;
					
					// Conectando no equipamento
					//if(!redeIpFunctions.conectarEquipamento(strIp, "s_iatb2b_cf", "!0erGz&$X$71", 
					if(!redeIpFunctions.conectarEquipamento(strIp, "s_iatb2b_cf", "LAd#4RY%&lm6F!", 
							RedeHelper.retornaInfoProcedimento(AplicarScriptsSbcFunctions.class.getName(), 
									processoIdDomain.getIdVantive(), "conectarEquipamento"))) {
						continue;
					}
					
					if (modelo == ModelosSbcEnum.GENBAND.getCodigo()) {
						comando = "cli ha";
						
						retorno = redeIpFunctions.enviarComandoAvailable(comando, new String[] {">", "#", "~]$",":~$", ":/]$"},
								RedeHelper.retornaInfoProcedimento(AplicarScriptsSbcFunctions.class.getName(), processoIdDomain.getIdVantive(), "cli ha"));
						
						if (StringUtils.isBlank(retorno)) {
							return;
						}
						redeIpFunctions.getLog().append(retorno);
						
						linhasRetorno = retorno.split("" + GlobalStrEnum.BREAK_LINE.toString() + "");
						for (int c = 0; c < linhasRetorno.length; c++) {
							String linhaAux = linhasRetorno[c].trim();
							if(StringUtils.containsIgnoreCase(linhaAux, "Local ") && StringUtils.containsIgnoreCase(linhaAux, "Active")) {
								aplicarScript = true;
								strEquipamentoCharParada = strHostname + "%";
								break;
							}
						}
						
						if (aplicarScript)
							break;
						else {
							comando = "exit";
							
							retorno = redeIpFunctions.enviarComandoAvailable(comando, new String[] {">", "#", "~]$",":~$", ":/]$"},
									RedeHelper.retornaInfoProcedimento(AplicarScriptsSbcFunctions.class.getName(), processoIdDomain.getIdVantive(), "exit"));
							
							if (StringUtils.isBlank(retorno)) {
								return;
							}
							redeIpFunctions.getLog().append(retorno);
							continue;
						}
					}
					
					if (modelo == ModelosSbcEnum.SONUS.getCodigo()) {
						aplicarScript = true;
						strEquipamentoCharParada = strHostname + "%";
						break;
					}
					
				} catch (Exception e) {
					ExceptionLogger.record(ExceptionUtils.getStackTrace(e), 
							RedeHelper.retornaInfoProcedimento(AplicarScriptsSbcFunctions.class.getName(), 
									processoIdDomain.getIdVantive(), "conectarEquipamento"));
					break;
				}
			}
			
			if (aplicarScript) {
				
				// label etapa teste online
				processoIdDomain.setEtapaTesteOnline("Aplicando Scripts SBC");
				
				// Remover primeiro
				linhasRetorno = sbScriptRemocao.toString().split("" + GlobalStrEnum.BREAK_LINE.toString() + "");
				for (int c = 0; c < linhasRetorno.length; c++) {
					comando = linhasRetorno[c].trim();
					if (modelo == ModelosSbcEnum.SONUS.getCodigo())
						retorno = redeIpFunctions.enviarComandoAvailable(comando, new String[] {strEquipamentoCharParada, ">", "#", "~]$",":~$", ":/]$"},
								RedeHelper.retornaInfoProcedimento(AplicarScriptsSbcFunctions.class.getName(), processoIdDomain.getIdVantive(), "sbScriptRemocao"));
					else
						retorno = redeIpFunctions.enviarComandoAvailable(comando, new String[] {">", "#", "~]$",":~$", ":/]$"},
								RedeHelper.retornaInfoProcedimento(AplicarScriptsSbcFunctions.class.getName(), processoIdDomain.getIdVantive(), "sbScriptRemocao"));

					if (StringUtils.isBlank(retorno)) {
						return;
					}
					redeIpFunctions.getLog().append(retorno);
				}
				
				if (processoIdDomain.getTipoProcedimento() == TipoProcedimentoSipOneCoreEnum.SCRIPTS_ALTA_FRESH.getCodigo() ||
					processoIdDomain.getTipoProcedimento() == TipoProcedimentoSipOneCoreEnum.SCRIPTS_AP_PORTABILIDADE.getCodigo()) {
				
					// Aplicar script
					linhasRetorno = sbScript.toString().split("" + GlobalStrEnum.BREAK_LINE.toString() + "");
					for (int c = 0; c < linhasRetorno.length; c++) {
						comando = linhasRetorno[c].trim();
						if (modelo == ModelosSbcEnum.SONUS.getCodigo())
							retorno = redeIpFunctions.enviarComandoAvailable(comando, new String[] {strEquipamentoCharParada, ">", "#", "~]$",":~$", ":/]$"},
									RedeHelper.retornaInfoProcedimento(AplicarScriptsSbcFunctions.class.getName(), processoIdDomain.getIdVantive(), "sbScript"));
						else
							retorno = redeIpFunctions.enviarComandoAvailable(comando, new String[] {">", "#", "~]$",":~$", ":/]$"},
									RedeHelper.retornaInfoProcedimento(AplicarScriptsSbcFunctions.class.getName(), processoIdDomain.getIdVantive(), "sbScript"));
						
						if (StringUtils.isBlank(retorno)) {
							return;
						}
						redeIpFunctions.getLog().append(retorno);
					}
				}
				
				comando = "exit";
				
				retorno = redeIpFunctions.enviarComandoAvailable(comando, new String[] {">", "#", "~]$",":~$", ":/]$"},
						RedeHelper.retornaInfoProcedimento(AplicarScriptsSbcFunctions.class.getName(), processoIdDomain.getIdVantive(), "exit"));
				
				if (StringUtils.isBlank(retorno)) {
					return;
				}
				redeIpFunctions.getLog().append(retorno);
				
				if (modelo == ModelosSbcEnum.SONUS.getCodigo()) {
					// ENTER
					retorno = redeIpFunctions.enviarComandoAvailable("", new String[] {">", "#", "~]$",":~$", ":/]$"},
							RedeHelper.retornaInfoProcedimento(AplicarScriptsSbcFunctions.class.getName(), processoIdDomain.getIdVantive(), "exit"));
					
					if (StringUtils.isBlank(retorno)) {
						return;
					}
					redeIpFunctions.getLog().append(retorno);
					
					if (StringUtils.containsIgnoreCase(retorno, hostname.toLowerCase())) {
						comando = "exit";
						retorno = redeIpFunctions.enviarComandoAvailable(comando, new String[] {">", "#", "~]$",":~$", ":/]$"},
								RedeHelper.retornaInfoProcedimento(AplicarScriptsSbcFunctions.class.getName(), processoIdDomain.getIdVantive(), "exit"));
						
						if (StringUtils.isBlank(retorno)) {
							return;
						}
						redeIpFunctions.getLog().append(retorno);
					}
				}
			}
			
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e),
					RedeHelper.retornaInfoProcedimento(AplicarScriptsSbcFunctions.class.getName(), 
							processoIdDomain.getIdVantive(), "aplicarScript"));
		}
	}
	
	public void iniciaProcedimentoRemocaoSbcAtual(JSONObject jsonTxtStart) {
		
		try {
			
			// label etapa teste online
			processoIdDomain.setEtapaTesteOnline("Apagando configuração antiga SBC Primário");
			
			aplicarScriptRemocaoSbcAtual(jsonTxtStart.getString("sbcAtualPrimario"), 
					ModelosSbcEnum.GENBAND.getCodigo(), 
					jsonTxtStart.getString("scriptDeletePriLine1"), 
					jsonTxtStart.getString("scriptDeletePriLine2"));
			
			// label etapa teste online
			processoIdDomain.setEtapaTesteOnline("Apagando configuração antiga SBC Backup");
			
			aplicarScriptRemocaoSbcAtual(jsonTxtStart.getString("sbcAtualSecundario"), 
					ModelosSbcEnum.GENBAND.getCodigo(), 
					jsonTxtStart.getString("scriptDeleteSecLine1"), 
					jsonTxtStart.getString("scriptDeleteSecLine2"));
			
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e),
					RedeHelper.retornaInfoProcedimento(AplicarScriptsSbcFunctions.class.getName(), processoIdDomain.getIdVantive(), "iniciaProcedimento"));
		}
		
	}

	private void aplicarScriptRemocaoSbcAtual(String hostname, int modelo, String cmd1, String cmd2) {
		
		// Pegando os ips
		try {
			
			/* CONECTAR */
			String comando = "";
			String retorno = "";
				
			comando = String.format("grep -i '%s' /etc/hosts", hostname);
			
			retorno = redeIpFunctions.enviarComandoAvailable(comando, new String[] {">", "#", "~]$",":~$", ":/]$"},
					RedeHelper.retornaInfoProcedimento(AplicarScriptsSbcFunctions.class.getName(), processoIdDomain.getIdVantive(), "/etc/hosts"));
			
			if (StringUtils.isBlank(retorno)) {
				return;
			}
			redeIpFunctions.getLog().append(retorno);
			
			boolean aplicarScript = false;
			String strEquipamentoCharParada = "";
			
			String[] linhasRetorno = retorno.split("" + GlobalStrEnum.BREAK_LINE.toString() + "");
			LinkedHashMap<String,String> hm = new LinkedHashMap<String,String>();
			
			for (int c = 0; c < linhasRetorno.length; c++) {
				String linhaAux = linhasRetorno[c];
				String strIp = StringHelper.searchPattern(linhaAux, GlobalStrEnum.IPADDRESS_PATTERN.toString());
				// Buscar os ips referente ao modelo sbc
//				[g0050914@gbrssh1:~]$ grep -i 'NGN-BR-SPO-si-SBC' /etc/hosts
//				10.229.12.72    ngn-br-spo-si-sbc1      GENBAND COMUTACAO
//				10.229.12.74    ngn-br-spo-si-sbc2      GENBAND COMUTACAO
//				10.113.124.114  ngn-br-spo-si-sbc3      SONUS   COMUTACAO
//				10.113.124.115  ngn-br-spo-si-sbc4      SONUS   COMUTACAO
				if(linhaAux != null &&
					StringUtils.isNotBlank(strIp) &&
					StringUtils.containsIgnoreCase(linhaAux, ModelosSbcEnum.getNome(modelo))) {
					
//					10.229.12.72	[01;31m[Kngn-br-spo-si-sbc[m[K1	GENBAND	COMUTACAO
//					10.229.12.74	[01;31m[Kngn-br-spo-si-sbc[m[K2	GENBAND	COMUTACAO
					// formatar
					linhaAux = StringHelper.removerCodigoCores(linhaAux);
					linhaAux = StringUtils.substringAfter(linhaAux, strIp);
					strEquipamentoCharParada = StringUtils.substringBefore(linhaAux.trim(), " ");
					if (StringUtils.isBlank(strEquipamentoCharParada))
						strEquipamentoCharParada = "";
					hm.put(strIp, strEquipamentoCharParada);
				}
			}
			
			strEquipamentoCharParada = "";
			for (Map.Entry<String, String> em : hm.entrySet()) {
				
				try {
					
					String strIp = em.getKey();
					String strHostname = em.getValue();
					
					if (StringUtils.isBlank(strIp))
						continue;
					
					// Conectando no equipamento
					// if(!redeIpFunctions.conectarEquipamento(strIp, "s_iatb2b_cf", "!0erGz&$X$71",
					if(!redeIpFunctions.conectarEquipamento(strIp, "s_iatb2b_cf", "LAd#4RY%&lm6F!",
							RedeHelper.retornaInfoProcedimento(AplicarScriptsSbcFunctions.class.getName(), 
									processoIdDomain.getIdVantive(), "conectarEquipamento"))) {
						continue;
					}
					
					if (modelo == ModelosSbcEnum.GENBAND.getCodigo()) {
						comando = "cli ha";
						
						retorno = redeIpFunctions.enviarComandoAvailable(comando, new String[] {">", "#", "~]$",":~$", ":/]$"},
								RedeHelper.retornaInfoProcedimento(AplicarScriptsSbcFunctions.class.getName(), processoIdDomain.getIdVantive(), "cli ha"));
						
						if (StringUtils.isBlank(retorno)) {
							return;
						}
						redeIpFunctions.getLog().append(retorno);
						
						linhasRetorno = retorno.split("" + GlobalStrEnum.BREAK_LINE.toString() + "");
						for (int c = 0; c < linhasRetorno.length; c++) {
							String linhaAux = linhasRetorno[c].trim();
							if(StringUtils.containsIgnoreCase(linhaAux, "Local ") && StringUtils.containsIgnoreCase(linhaAux, "Active")) {
								aplicarScript = true;
								strEquipamentoCharParada = strHostname + "%";
								break;
							}
						}
						
						if (aplicarScript)
							break;
						else {
							comando = "exit";
							
							retorno = redeIpFunctions.enviarComandoAvailable(comando, new String[] {">", "#", "~]$",":~$", ":/]$"},
									RedeHelper.retornaInfoProcedimento(AplicarScriptsSbcFunctions.class.getName(), processoIdDomain.getIdVantive(), "exit"));
							
							if (StringUtils.isBlank(retorno)) {
								return;
							}
							redeIpFunctions.getLog().append(retorno);
							continue;
						}
					}
					
				} catch (Exception e) {
					ExceptionLogger.record(ExceptionUtils.getStackTrace(e), 
							RedeHelper.retornaInfoProcedimento(AplicarScriptsSbcFunctions.class.getName(), 
									processoIdDomain.getIdVantive(), "conectarEquipamento"));
					break;
				}
			}
			
			if (aplicarScript) {
				
				// Remover cmd1
				retorno = redeIpFunctions.enviarComandoAvailable(cmd1, new String[] {">", "#", "~]$",":~$", ":/]$"},
						RedeHelper.retornaInfoProcedimento(AplicarScriptsSbcFunctions.class.getName(), processoIdDomain.getIdVantive(), "sbScriptRemocao"));
				
				if (StringUtils.isBlank(retorno)) {
					return;
				}
				redeIpFunctions.getLog().append(retorno);
				
				// Remover cmd2
				retorno = redeIpFunctions.enviarComandoAvailable(cmd2, new String[] {">", "#", "~]$",":~$", ":/]$"},
						RedeHelper.retornaInfoProcedimento(AplicarScriptsSbcFunctions.class.getName(), processoIdDomain.getIdVantive(), "sbScriptRemocao"));
				
				if (StringUtils.isBlank(retorno)) {
					return;
				}
				redeIpFunctions.getLog().append(retorno);
				
				comando = "exit";
				
				retorno = redeIpFunctions.enviarComandoAvailable(comando, new String[] {">", "#", "~]$",":~$", ":/]$"},
						RedeHelper.retornaInfoProcedimento(AplicarScriptsSbcFunctions.class.getName(), processoIdDomain.getIdVantive(), "exit"));
				
				if (StringUtils.isBlank(retorno)) {
					return;
				}
				redeIpFunctions.getLog().append(retorno);
			}
			
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e),
					RedeHelper.retornaInfoProcedimento(AplicarScriptsSbcFunctions.class.getName(), 
							processoIdDomain.getIdVantive(), "aplicarScript"));
		}
	}
	
	/**
	 * Metodos Sets and Gets 
	 */
	
	public RedeIpFunctions getRedeIpFunctions() {
		return redeIpFunctions;
	}

	public void setRedeIpFunctions(RedeIpFunctions redeIpFunctions) {
		this.redeIpFunctions = redeIpFunctions;
	}

	public StringBuilder getLog() {
		return log;
	}

	public void setLog(StringBuilder log) {
		this.log = log;
	}
	
}
