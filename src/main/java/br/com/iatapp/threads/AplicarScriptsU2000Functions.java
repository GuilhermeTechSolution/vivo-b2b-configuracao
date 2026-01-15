package br.com.iatapp.threads;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import br.com.iatapp.domain.SipOneCoreProcessoIdDomain;
import br.com.iatapp.enums.GlobalStrEnum;
import br.com.iatapp.enums.JsonKeysEnum;
import br.com.iatapp.enums.TipoProcedimentoSipOneCoreEnum;
import br.com.iatapp.helper.RedeHelper;
import br.com.iatapp.logger.ExceptionLogger;
import br.com.iatapp.rede.RedeIpFunctions;

public class AplicarScriptsU2000Functions {
	
//	private final static char CR  = (char) 0x0D;
//	private final static char LF  = (char) 0x0A;
//	private final static String CRLF  = "" + CR + LF;
	
	private RedeIpFunctions redeIpFunctions;
	private SipOneCoreProcessoIdDomain processoIdDomain;
	private StringBuilder log;
	
	public AplicarScriptsU2000Functions(RedeIpFunctions redeIpFunctions) {
		this.redeIpFunctions = redeIpFunctions;
	}
	
	public AplicarScriptsU2000Functions(RedeIpFunctions redeIpFunctions, 
			SipOneCoreProcessoIdDomain processoIdDomain) {
		this.redeIpFunctions = redeIpFunctions;
		this.processoIdDomain = processoIdDomain;
		this.log = new StringBuilder();
	}
	
	public void iniciaProcedimento() {
		
		try {
			
			// label etapa teste online
			processoIdDomain.setEtapaTesteOnline("Conectando servidor U2000");
			
			/* CONECTAR */
			//String comando = "telnet 10.8.16.2 31114";
			String comando = "telnet 201.69.136.99 31114";
			String retorno = "";
			// CR and LF are control characters, respectively coded 0x0D (13 decimal) and 0x0A (10 decimal).
			retorno = redeIpFunctions.enviarComandoAvailable(comando, new String[] {"is '^]'."},
					RedeHelper.retornaInfoProcedimento(AplicarScriptsU2000Functions.class.getName(), processoIdDomain.getIdVantive(), "conectar-telnet"));
			
			if (StringUtils.isBlank(retorno)) {
				return;
			}
			redeIpFunctions.getLog().append(retorno);
			
			if (StringUtils.containsIgnoreCase(retorno, "Connection refused")) {
				// 201.69.136.99 - SP CTP
				// 201.69.174.102 - RJ BAR
				comando = "telnet 201.69.174.102 31114";
				retorno = "";
				// CR and LF are control characters, respectively coded 0x0D (13 decimal) and 0x0A (10 decimal).
				retorno = redeIpFunctions.enviarComandoAvailable(comando, new String[] {"is '^]'."},
						RedeHelper.retornaInfoProcedimento(AplicarScriptsU2000Functions.class.getName(), processoIdDomain.getIdVantive(), "conectar-telnet"));
				
				if (StringUtils.isBlank(retorno)) {
					return;
				}
				redeIpFunctions.getLog().append(retorno);
				
	//			[ottap-techsolutio@CTP-IAT-SERVER ~]$ telnet 201.69.174.102 31114
	//			Trying 201.69.174.102...
	//			telnet: connect to address 201.69.174.102: Connection refused
				
				if (StringUtils.containsIgnoreCase(retorno, "Connection refused")) {
//					comando = "telnet 201.69.174.102 31114";
//					retorno = "";
//					
//					// CR and LF are control characters, respectively coded 0x0D (13 decimal) and 0x0A (10 decimal).
//					retorno = redeIpFunctions.enviarComandoAvailable(comando, new String[] {"is '^]'."},
//							RedeHelper.retornaInfoProcedimento(AplicarScriptsU2000Functions.class.getName(), processoIdDomain.getIdVantive(), "conectar-telnet"));
//					
//					if (StringUtils.isBlank(retorno)) {
//						return;
//					}
//					redeIpFunctions.getLog().append(retorno);
//					
//					if (StringUtils.containsIgnoreCase(retorno, "Connection refused")) {
						processoIdDomain.getJsonDados().put(JsonKeysEnum.SIPONECORE_LOGIN_U2000.getCodigo(), false);
						return;
//					}
				}
			}
			
			/* LOGIN */
			comando = "LGI:OP=\"erick_sg\",PWD=\"Erick.123\";";
			
			retorno = redeIpFunctions.enviarComandoAvailableSemAppendLogTotal(comando, new String[] {"---    END"},
					RedeHelper.retornaInfoProcedimento(AplicarScriptsU2000Functions.class.getName(), processoIdDomain.getIdVantive(), "login"));
			
			if (StringUtils.isBlank(retorno)) {
				return;
			}
			redeIpFunctions.getLog().append(retorno);
			
//			RETCODE = 1  The Login User Name or Password is Error
//
//					The Login User Name or Password is Error
//
//					---    END
//					Internal Exception! 
//					Connection closed by foreign host.
//
//					[ottap-techsolutio@CTP-IAT-SERVER ~]$ 
			
			if (StringUtils.containsIgnoreCase(retorno, "Password is Error")) {
				processoIdDomain.getJsonDados().put(JsonKeysEnum.SIPONECORE_LOGIN_U2000.getCodigo(), false);
				return;
			}
			
			// label etapa teste online
			processoIdDomain.setEtapaTesteOnline("Aplicando Scripts BCF U2000");
			
			// ----------------------------------------------------------------
			// ------------------- APLICAR SCRIPTS BCF ------------------------
			// ----------------------------------------------------------------
			
			//REG NE:NAME="SPCON-IMSVMH-CSC01";
			comando = "REG NE:NAME=\"SPCON-IMSVMH-CSC01\";";
			
			retorno = redeIpFunctions.enviarComandoAvailable(comando, new String[] {"---    END"},
					RedeHelper.retornaInfoProcedimento(AplicarScriptsU2000Functions.class.getName(), processoIdDomain.getIdVantive(), "login"));
			
			if (StringUtils.isBlank(retorno)) {
				return;
			}
			redeIpFunctions.getLog().append(retorno);
			
			// Remover primeiro
			String[] linhasRetorno = processoIdDomain.getScriptRemocaoBcf().split("" + GlobalStrEnum.BREAK_LINE.toString() + "");
			for (int c = 0; c < linhasRetorno.length; c++) {
				comando = linhasRetorno[c].trim();
				retorno = redeIpFunctions.enviarComandoBufferAvailable(comando, new String[] {"---    END"},
						RedeHelper.retornaInfoProcedimento(AplicarScriptsU2000Functions.class.getName(), processoIdDomain.getIdVantive(), "getScriptRemocaoBcf"));
				
				if (StringUtils.isBlank(retorno)) {
					return;
				}
				redeIpFunctions.getLog().append(retorno);
			}
			
			if (processoIdDomain.getTipoProcedimento() == TipoProcedimentoSipOneCoreEnum.SCRIPTS_ALTA_FRESH.getCodigo() ||
				processoIdDomain.getTipoProcedimento() == TipoProcedimentoSipOneCoreEnum.SCRIPTS_AP_PORTABILIDADE.getCodigo()) {
			
				// Aplicar script
				linhasRetorno = processoIdDomain.getScriptBcf().split("" + GlobalStrEnum.BREAK_LINE.toString() + "");
				for (int c = 0; c < linhasRetorno.length; c++) {
					comando = linhasRetorno[c].trim();
					retorno = redeIpFunctions.enviarComandoBufferAvailable(comando, new String[] {"---    END"},
							RedeHelper.retornaInfoProcedimento(AplicarScriptsU2000Functions.class.getName(), processoIdDomain.getIdVantive(), "getScriptBcf"));
					
					if (StringUtils.isBlank(retorno)) {
						return;
					}
					redeIpFunctions.getLog().append(retorno);
				}
			}
			
			//REG NE:NAME="SPMRB-IMSVMH-CSC01";
			comando = "REG NE:NAME=\"SPMRB-IMSVMH-CSC01\";";
			
			retorno = redeIpFunctions.enviarComandoAvailable(comando, new String[] {"---    END"},
					RedeHelper.retornaInfoProcedimento(AplicarScriptsU2000Functions.class.getName(), processoIdDomain.getIdVantive(), "login"));
			
			if (StringUtils.isBlank(retorno)) {
				return;
			}
			redeIpFunctions.getLog().append(retorno);
			
			// Remover primeiro
			linhasRetorno = processoIdDomain.getScriptRemocaoBcf().split("" + GlobalStrEnum.BREAK_LINE.toString() + "");
			for (int c = 0; c < linhasRetorno.length; c++) {
				comando = linhasRetorno[c].trim();
				retorno = redeIpFunctions.enviarComandoBufferAvailable(comando, new String[] {"---    END"},
						RedeHelper.retornaInfoProcedimento(AplicarScriptsU2000Functions.class.getName(), processoIdDomain.getIdVantive(), "getScriptRemocaoBcf"));
				
				if (StringUtils.isBlank(retorno)) {
					return;
				}
				redeIpFunctions.getLog().append(retorno);
			}
			
			if (processoIdDomain.getTipoProcedimento() == TipoProcedimentoSipOneCoreEnum.SCRIPTS_ALTA_FRESH.getCodigo() ||
				processoIdDomain.getTipoProcedimento() == TipoProcedimentoSipOneCoreEnum.SCRIPTS_AP_PORTABILIDADE.getCodigo()) {
			
				// Aplicar script
				linhasRetorno = processoIdDomain.getScriptBcf().split("" + GlobalStrEnum.BREAK_LINE.toString() + "");
				for (int c = 0; c < linhasRetorno.length; c++) {
					comando = linhasRetorno[c].trim();
					retorno = redeIpFunctions.enviarComandoBufferAvailable(comando, new String[] {"---    END"},
							RedeHelper.retornaInfoProcedimento(AplicarScriptsU2000Functions.class.getName(), processoIdDomain.getIdVantive(), "getScriptBcf"));
					
					if (StringUtils.isBlank(retorno)) {
						return;
					}
					redeIpFunctions.getLog().append(retorno);
				}
			}
			
			/* LOGOUT */
			comando = "LGO:OP=\"erick_sg\";";
			
			retorno = redeIpFunctions.enviarComandoAvailableSemAppendLogTotal(comando, new String[] {":~$", "~]$", ":/]$", "0$"},
					RedeHelper.retornaInfoProcedimento(AplicarScriptsU2000Functions.class.getName(), processoIdDomain.getIdVantive(), "logout"));
			
			if (StringUtils.isBlank(retorno)) {
				return;
			}
			redeIpFunctions.getLog().append(retorno);
			
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e),
					RedeHelper.retornaInfoProcedimento(AplicarScriptsU2000Functions.class.getName(), processoIdDomain.getIdVantive(), "iniciaProcedimento"));
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
