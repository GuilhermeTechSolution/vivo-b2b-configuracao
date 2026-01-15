package br.com.iatapp.threads;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import br.com.iatapp.config.IatConstants;
import br.com.iatapp.domain.SipOneCoreProcessoIdDomain;
import br.com.iatapp.enums.CodigoServidoresEnum;
import br.com.iatapp.enums.TipoProcedimentoSipOneCoreEnum;
import br.com.iatapp.helper.RedeHelper;
import br.com.iatapp.logger.ExceptionLogger;
import br.com.iatapp.rede.RedeIpFunctions;

public class AplicarScriptsImsFunctions {
	
	private RedeIpFunctions redeIpFunctions;
	private SipOneCoreProcessoIdDomain processoIdDomain;
	private StringBuilder log;
	
	public AplicarScriptsImsFunctions(RedeIpFunctions redeIpFunctions) {
		this.redeIpFunctions = redeIpFunctions;
	}
	
	public AplicarScriptsImsFunctions(RedeIpFunctions redeIpFunctions, 
			SipOneCoreProcessoIdDomain processoIdDomain) {
		this.redeIpFunctions = redeIpFunctions;
		this.processoIdDomain = processoIdDomain;
		this.log = new StringBuilder();
	}
	
	public void iniciaProcedimento() {
		
		try {
			
			// label etapa teste online
			processoIdDomain.setEtapaTesteOnline("Conectando servidor IMS ATR");
			
			/* CONECTAR */
			String comando = "";
			String retorno = "";
			int port = 3300;
			boolean conectou = false;
			
			for (int i=0; i < 9; i++) {
				if(IatConstants.codigoServidor == CodigoServidoresEnum.SERVIDOR_DEV.getCodigoServidor()) {
					// comando = "telnet 10.11.24.107 " + String.valueOf(port); // SP
					comando = "telnet 10.11.24.104 " + String.valueOf(port); // SP
				} else {
					comando = "telnet 10.11.24.104 " + String.valueOf(port); // SP
				}
				
				retorno = redeIpFunctions.enviarComandoAvailable(comando, new String[] {"Enter command: ", ":~$", "~]$", ":/]$", "0$"},
						RedeHelper.retornaInfoProcedimento(AplicarScriptsImsFunctions.class.getName(), processoIdDomain.getIdVantive(), "conectar-telnet"));
				
				if (StringUtils.isBlank(retorno)) {
					return;
				}
				redeIpFunctions.getLog().append(retorno);
				
	//			Trying 10.11.24.107...
	//
	//			telnet: connect to address 10.11.24.107: Connection refused
	//			[ottap-techsolutio@CTP-IAT-SERVER ~]$ 
				
				if (StringUtils.containsIgnoreCase(retorno, "Connection refused")) {
					port++;
					continue;
				}
				conectou = true;
				break;
			}
			
			if (!conectou)
				return;
			
			/* LOGIN */
			comando = "LOGIN:prov_b2b:Atr_b2b;";
			
			retorno = redeIpFunctions.enviarComandoAvailable(comando, new String[] {"Enter command: "},
					RedeHelper.retornaInfoProcedimento(AplicarScriptsImsFunctions.class.getName(), processoIdDomain.getIdVantive(), "login"));
			
			if (StringUtils.isBlank(retorno)) {
				return;
			}
			redeIpFunctions.getLog().append(retorno);
			
			// label etapa teste online
			processoIdDomain.setEtapaTesteOnline("Aplicando Scripts IMS ATR");
			
			// ----------------------------------------------------------------
			// ------------------- APLICAR SCRIPTS IMS ------------------------
			// ----------------------------------------------------------------
			
			if (processoIdDomain.getTipoProcedimento() == TipoProcedimentoSipOneCoreEnum.SCRIPTS_ALTA_FRESH.getCodigo() ||
				processoIdDomain.getTipoProcedimento() == TipoProcedimentoSipOneCoreEnum.SCRIPTS_AP_PORTABILIDADE.getCodigo() ||
				processoIdDomain.getTipoProcedimento() == TipoProcedimentoSipOneCoreEnum.ROLLBACK_ALTA_FRESH.getCodigo() ||
				processoIdDomain.getTipoProcedimento() == TipoProcedimentoSipOneCoreEnum.ROLLBACK_AP_PORTABILIDADE.getCodigo()) {
			
				// ----------------------------------------------------------------
				// ------------------- SCRIPTS REMOÇÃO HSS ------------------------
				// ----------------------------------------------------------------
				
				// Remover primeiro
	//				DELETE:VIVOHSSSUB:DOMINIO,ims4.vivo.net.br:NUMERO,1133331234:SITE,SPO.CO;
				
				// Aplicar tronco chave
				if (processoIdDomain.isMigracao())
					comando = String.format("DELETE:VIVOHSSSUB:DOMINIO,%s:NUMERO,%s:SITE,%s:CENTRA,false;", 
							processoIdDomain.getDominio(), processoIdDomain.getTroncoChavePiloto(), processoIdDomain.getImsSITE());
				else
					comando = String.format("DELETE:VIVOHSSSUB:DOMINIO,%s:NUMERO,%s:SITE,%s;", 
							processoIdDomain.getDominio(), processoIdDomain.getTroncoChavePiloto(), processoIdDomain.getImsSITE());
				
				retorno = redeIpFunctions.enviarComandoAvailable(comando, new String[] {"Enter command: "},
						RedeHelper.retornaInfoProcedimento(AplicarScriptsImsFunctions.class.getName(), processoIdDomain.getIdVantive(), "comando-remocao-tronco-chave"));
				
				if (StringUtils.isBlank(retorno)) {
					return;
				}
				redeIpFunctions.getLog().append(retorno);
				
				String[] ramais = processoIdDomain.getRamais().split(",");
				for (String ramal : ramais) {
					
					// Remover primeiro
	//					DELETE:VIVOHSSSUB:DOMINIO,ims4.vivo.net.br:NUMERO,1133331234:SITE,SPO.CO;
					if (processoIdDomain.isMigracao())
						comando = String.format("DELETE:VIVOHSSSUB:DOMINIO,%s:NUMERO,%s:SITE,%s:CENTRA,false;", 
								processoIdDomain.getDominio(), ramal, processoIdDomain.getImsSITE());
					else
						comando = String.format("DELETE:VIVOHSSSUB:DOMINIO,%s:NUMERO,%s:SITE,%s;", 
								processoIdDomain.getDominio(), ramal, processoIdDomain.getImsSITE());
					
					retorno = redeIpFunctions.enviarComandoAvailable(comando, new String[] {"Enter command: "},
							RedeHelper.retornaInfoProcedimento(AplicarScriptsImsFunctions.class.getName(), processoIdDomain.getIdVantive(), "comando-remocao-ramais"));
					
					if (StringUtils.isBlank(retorno)) {
						return;
					}
					redeIpFunctions.getLog().append(retorno);
				}
				
				if (!processoIdDomain.isMigracao()) {
					// ----------------------------------------------------------------
					// ------------------ SCRIPTS REMOCAÇÃO ATS -----------------------
					// ----------------------------------------------------------------
					
					// Remover primeiro
		//				DELETE:VIVOATSSUB:DOMINIO,ims4.vivo.net.br:NUMERO,1133331234:SITE,SPO.CO;
					
					// Aplicar tronco chave
					comando = String.format("DELETE:VIVOATSSUB:DOMINIO,%s:NUMERO,%s:SITE,%s;", 
							processoIdDomain.getDominio(), processoIdDomain.getTroncoChavePiloto(), processoIdDomain.getImsSITE());
					
					retorno = redeIpFunctions.enviarComandoAvailable(comando, new String[] {"Enter command: "},
							RedeHelper.retornaInfoProcedimento(AplicarScriptsImsFunctions.class.getName(), processoIdDomain.getIdVantive(), "comando-remocao-tronco-chave"));
					
					if (StringUtils.isBlank(retorno)) {
						return;
					}
					redeIpFunctions.getLog().append(retorno);
					
					ramais = processoIdDomain.getRamais().split(",");
					for (String ramal : ramais) {
						
						// Remover primeiro
		//					DELETE:VIVOATSSUB:DOMINIO,ims4.vivo.net.br:NUMERO,1133331234:SITE,SPO.CO;
						
						comando = String.format("DELETE:VIVOATSSUB:DOMINIO,%s:NUMERO,%s:SITE,%s;", 
								processoIdDomain.getDominio(), ramal, processoIdDomain.getImsSITE());
						
						retorno = redeIpFunctions.enviarComandoAvailable(comando, new String[] {"Enter command: "},
								RedeHelper.retornaInfoProcedimento(AplicarScriptsImsFunctions.class.getName(), processoIdDomain.getIdVantive(), "comando-remocao-ramais"));
						
						if (StringUtils.isBlank(retorno)) {
							return;
						}
						redeIpFunctions.getLog().append(retorno);
					}
				}
			}
			
			if (processoIdDomain.getTipoProcedimento() == TipoProcedimentoSipOneCoreEnum.SCRIPTS_ALTA_FRESH.getCodigo() ||
				processoIdDomain.getTipoProcedimento() == TipoProcedimentoSipOneCoreEnum.SCRIPTS_AP_MIGRACAO.getCodigo() ||
				processoIdDomain.getTipoProcedimento() == TipoProcedimentoSipOneCoreEnum.ROLLBACK_ALTA_FRESH.getCodigo() ||
				processoIdDomain.getTipoProcedimento() == TipoProcedimentoSipOneCoreEnum.ROLLBACK_AP_MIGRACAO.getCodigo()) {
				
				// ----------------------------------------------------------------
				// ------------------- SCRIPTS REMOÇÃO ENUM -----------------------
				// ----------------------------------------------------------------
				
				// Remover primeiro
	//				DELETE:VIVOENSSUB:ZONENAME,1.1.5.5.e164.arpa:NUMERO,33331234:SITE,SPO.CO;
				
				// Aplicar tronco chave
				if (processoIdDomain.isMigracao())
					comando = String.format("DELETE:VIVOENSSUB:ZONENAME,%s.%s.5.5.e164.arpa:NUMERO,%s:SITE,%s:CENTRA,false;", 
							processoIdDomain.getTroncoChavePiloto().substring(1, 2), processoIdDomain.getTroncoChavePiloto().substring(0, 1), 
							processoIdDomain.getTroncoChavePiloto(), processoIdDomain.getImsSITE());
				else
					comando = String.format("DELETE:VIVOENSSUB:ZONENAME,%s.%s.5.5.e164.arpa:NUMERO,%s:SITE,%s;", 
							processoIdDomain.getTroncoChavePiloto().substring(1, 2), processoIdDomain.getTroncoChavePiloto().substring(0, 1), 
							processoIdDomain.getTroncoChavePiloto(), processoIdDomain.getImsSITE());
				
				retorno = redeIpFunctions.enviarComandoAvailable(comando, new String[] {"Enter command: "},
						RedeHelper.retornaInfoProcedimento(AplicarScriptsImsFunctions.class.getName(), processoIdDomain.getIdVantive(), "comando-remocao-tronco-chave"));
				
				if (StringUtils.isBlank(retorno)) {
					return;
				}
				redeIpFunctions.getLog().append(retorno);
				
				String[] ramais = processoIdDomain.getRamais().split(",");
				for (String ramal : ramais) {
					
					// Remover primeiro
	//					DELETE:VIVOENSSUB:ZONENAME,1.1.5.5.e164.arpa:NUMERO,33331234:SITE,SPO.CO;
					if (processoIdDomain.isMigracao())
						comando = String.format("DELETE:VIVOENSSUB:ZONENAME,%s.%s.5.5.e164.arpa:NUMERO,%s:SITE,%s:CENTRA,false;", 
								ramal.substring(1, 2), ramal.substring(0, 1), 
								ramal, processoIdDomain.getImsSITE());
					else
						comando = String.format("DELETE:VIVOENSSUB:ZONENAME,%s.%s.5.5.e164.arpa:NUMERO,%s:SITE,%s;", 
								ramal.substring(1, 2), ramal.substring(0, 1), 
								ramal, processoIdDomain.getImsSITE());
					
					retorno = redeIpFunctions.enviarComandoAvailable(comando, new String[] {"Enter command: "},
							RedeHelper.retornaInfoProcedimento(AplicarScriptsImsFunctions.class.getName(), processoIdDomain.getIdVantive(), "comando-remocao-ramais"));
					
					if (StringUtils.isBlank(retorno)) {
						return;
					}
					redeIpFunctions.getLog().append(retorno);
				}
			}
			
			if (processoIdDomain.getTipoProcedimento() == TipoProcedimentoSipOneCoreEnum.SCRIPTS_ALTA_FRESH.getCodigo() ||
				processoIdDomain.getTipoProcedimento() == TipoProcedimentoSipOneCoreEnum.SCRIPTS_AP_PORTABILIDADE.getCodigo()) {
				
				// ----------------------------------------------------------------
				// ----------------------- SCRIPTS HSS ----------------------------
				// ----------------------------------------------------------------
			
	//			CREATE:VIVOHSSSUB:DOMINIO,ims4.vivo.net.br:NUMERO,1133331234:TRUNK,spolcvoxac5250:SIFCID,111:SIFCID2,118:RLI,SPO_:SITE,SPO.CO:ISPILOT,1;
				
				// Aplicar tronco chave
				comando = String.format("CREATE:VIVOHSSSUB:DOMINIO,%s:NUMERO,%s:TRUNK,%s:SIFCID,%s:SIFCID2,%s:RLI,%s_:SITE,%s:ISPILOT,1;", 
						processoIdDomain.getDominio(), processoIdDomain.getTroncoChavePiloto(), processoIdDomain.getPabxid(),
						processoIdDomain.getImsHssSIFCID(), processoIdDomain.getImsHssSIFCID2(), 
						processoIdDomain.getLocation().toUpperCase(), processoIdDomain.getImsSITE());
				
				retorno = redeIpFunctions.enviarComandoAvailable(comando, new String[] {"Enter command: "},
						RedeHelper.retornaInfoProcedimento(AplicarScriptsImsFunctions.class.getName(), processoIdDomain.getIdVantive(), "comando-tronco-chave"));
				
				if (StringUtils.isBlank(retorno)) {
					return;
				}
				redeIpFunctions.getLog().append(retorno);
				
				String[] ramais = processoIdDomain.getRamais().split(",");
				for (String ramal : ramais) {
					
					// Aplicar tronco chave ramais
	//				CREATE:VIVOHSSSUB:DOMINIO,ims4.vivo.net.br:NUMERO,1133331235:TRUNK,spolcvoxac5250:SIFCID,111:SIFCID2,118:RLI,SPO_:SITE,SPO.CO:ISPILOT,0;
					
					comando = String.format("CREATE:VIVOHSSSUB:DOMINIO,%s:NUMERO,%s:TRUNK,%s:SIFCID,%s:SIFCID2,%s:RLI,%s_:SITE,%s:ISPILOT,0;", 
							processoIdDomain.getDominio(), ramal, processoIdDomain.getPabxid(),
							processoIdDomain.getImsHssSIFCID(), processoIdDomain.getImsHssSIFCID2(), 
							processoIdDomain.getLocation().toUpperCase(), processoIdDomain.getImsSITE());
					
					retorno = redeIpFunctions.enviarComandoAvailable(comando, new String[] {"Enter command: "},
							RedeHelper.retornaInfoProcedimento(AplicarScriptsImsFunctions.class.getName(), processoIdDomain.getIdVantive(), "comando-ramais"));
					
					if (StringUtils.isBlank(retorno)) {
						return;
					}
					redeIpFunctions.getLog().append(retorno);
				}
				
				// ----------------------------------------------------------------
				// ----------------------- SCRIPTS ATS ----------------------------
				// ----------------------------------------------------------------
				
//				CREATE:VIVOATSSUB:DOMINIO,ims4.vivo.net.br:NUMERO,1133331234:NSNPTY,0:CW,0:VOICEMAIL,0:SITE,SPO.CO:NSCBA,0:KVALUE,0:NSCLIP,1:LP,11:CSC,1101:ENUMIDX,65535:ISPILOT,1:TRUNK,spolcvoxac5250;
				
				// Aplicar tronco chave
				comando = String.format("CREATE:VIVOATSSUB:DOMINIO,%s:NUMERO,%s:NSNPTY,0:CW,0:VOICEMAIL,0:SITE,%s:NSCBA,0:KVALUE,0:NSCLIP,1:LP,%s:CSC,%s:ENUMIDX,65535:ISPILOT,1:TRUNK,%s;", 
						processoIdDomain.getDominio(), processoIdDomain.getTroncoChavePiloto(), processoIdDomain.getImsSITE(),
						processoIdDomain.getTroncoChavePiloto().substring(0, 2), processoIdDomain.getCallsource(), processoIdDomain.getPabxid());
				
				retorno = redeIpFunctions.enviarComandoAvailable(comando, new String[] {"Enter command: "},
						RedeHelper.retornaInfoProcedimento(AplicarScriptsImsFunctions.class.getName(), processoIdDomain.getIdVantive(), "comando-tronco-chave"));
				
				if (StringUtils.isBlank(retorno)) {
					return;
				}
				redeIpFunctions.getLog().append(retorno);
				
				for (String ramal : ramais) {
					
					// Aplicar tronco chave ramais
//					CREATE:VIVOATSSUB:DOMINIO,ims4.vivo.net.br:NUMERO,1133331234:NSNPTY,0:CW,0:VOICEMAIL,0:SITE,SPO.CO:NSCBA,0:KVALUE,0:NSCLIP,1:LP,11:CSC,1101:ENUMIDX,65535:ISPILOT,0:TRUNK,spolcvoxac5250;
					
					comando = String.format("CREATE:VIVOATSSUB:DOMINIO,%s:NUMERO,%s:NSNPTY,0:CW,0:VOICEMAIL,0:SITE,%s:NSCBA,0:KVALUE,0:NSCLIP,1:LP,%s:CSC,%s:ENUMIDX,65535:ISPILOT,0:TRUNK,%s;", 
							processoIdDomain.getDominio(), ramal, processoIdDomain.getImsSITE(),
							ramal.substring(0, 2), processoIdDomain.getCallsource(), processoIdDomain.getPabxid());
					
					retorno = redeIpFunctions.enviarComandoAvailable(comando, new String[] {"Enter command: "},
							RedeHelper.retornaInfoProcedimento(AplicarScriptsImsFunctions.class.getName(), processoIdDomain.getIdVantive(), "comando-ramais"));
					
					if (StringUtils.isBlank(retorno)) {
						return;
					}
					redeIpFunctions.getLog().append(retorno);
				}
			}
			
			if (processoIdDomain.getTipoProcedimento() == TipoProcedimentoSipOneCoreEnum.SCRIPTS_ALTA_FRESH.getCodigo() ||
				processoIdDomain.getTipoProcedimento() == TipoProcedimentoSipOneCoreEnum.SCRIPTS_AP_MIGRACAO.getCodigo()) {
				
				// ----------------------------------------------------------------
				// ----------------------- SCRIPTS ENUM ---------------------------
				// ----------------------------------------------------------------
			
//				CREATE:VIVOENSSUB:DOMINIO,ims4.vivo.net.br:NUMERO,33331234:ZONENAME,1.1.5.5.e164.arpa:SRV,E2U+SIP:SITE,SPO.CO:FLAGS,U;
				
				// Aplicar tronco chave
				comando = String.format("CREATE:VIVOENSSUB:DOMINIO,%s:NUMERO,%s:ZONENAME,%s.%s.5.5.e164.arpa:SRV,E2U+SIP:SITE,%s:FLAGS,U;", 
						processoIdDomain.getDominio(), processoIdDomain.getTroncoChavePiloto(), 
						processoIdDomain.getTroncoChavePiloto().substring(1, 2), processoIdDomain.getTroncoChavePiloto().substring(0, 1), 
						processoIdDomain.getImsSITE());
				
				retorno = redeIpFunctions.enviarComandoAvailable(comando, new String[] {"Enter command: "},
						RedeHelper.retornaInfoProcedimento(AplicarScriptsImsFunctions.class.getName(), processoIdDomain.getIdVantive(), "comando-tronco-chave"));
				
				if (StringUtils.isBlank(retorno)) {
					return;
				}
				redeIpFunctions.getLog().append(retorno);
				
				String[] ramais = processoIdDomain.getRamais().split(",");
				for (String ramal : ramais) {
					
					// Aplicar tronco chave ramais
	//				CREATE:VIVOENSSUB:DOMINIO,ims4.vivo.net.br:NUMERO,33331234:ZONENAME,1.1.5.5.e164.arpa:SRV,E2U+SIP:SITE,SPO.CO:FLAGS,U;
					
					comando = String.format("CREATE:VIVOENSSUB:DOMINIO,%s:NUMERO,%s:ZONENAME,%s.%s.5.5.e164.arpa:SRV,E2U+SIP:SITE,%s:FLAGS,U;", 
							processoIdDomain.getDominio(), ramal, 
							ramal.substring(1, 2), ramal.substring(0, 1), 
							processoIdDomain.getImsSITE());
					
					retorno = redeIpFunctions.enviarComandoAvailable(comando, new String[] {"Enter command: "},
							RedeHelper.retornaInfoProcedimento(AplicarScriptsImsFunctions.class.getName(), processoIdDomain.getIdVantive(), "comando-ramais"));
					
					if (StringUtils.isBlank(retorno)) {
						return;
					}
					redeIpFunctions.getLog().append(retorno);
				}
			}
			
			
			/* LOGOUT */
			comando = "LOGOUT:prov_b2b;";
			
			retorno = redeIpFunctions.enviarComandoAvailable(comando, new String[] {":~$", "~]$", ":/]$", "0$"},
					RedeHelper.retornaInfoProcedimento(AplicarScriptsImsFunctions.class.getName(), processoIdDomain.getIdVantive(), "logout"));
			
			if (StringUtils.isBlank(retorno)) {
				return;
			}
			redeIpFunctions.getLog().append(retorno);
			
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e),
					RedeHelper.retornaInfoProcedimento(AplicarScriptsImsFunctions.class.getName(), processoIdDomain.getIdVantive(), "iniciaProcedimento"));
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
