package br.com.iatapp.commons;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import br.com.iatapp.domain.ConfigSwitchIdDomain;
import br.com.iatapp.domain.ConfigSwitchScriptDomain;
import br.com.iatapp.enums.GlobalStrEnum;
import br.com.iatapp.enums.ResultadosStatus;
import br.com.iatapp.helper.RedeHelper;
import br.com.iatapp.helper.StringHelper;
import br.com.iatapp.logger.ExceptionLogger;
import br.com.iatapp.model.RetornoModel;
import br.com.iatapp.rede.AtivacaoSwitchFunctions;
import br.com.iatapp.rede.RedeIpFunctions;
import br.com.iatapp.rede.SimpleTelnet;

public class SwitchCoriantCommons {
	
	RedeIpFunctions redeIpFunctions;
	ConfigSwitchIdDomain configSwitchIdDomain;
	
	public SwitchCoriantCommons(RedeIpFunctions redeIpFunctions, ConfigSwitchIdDomain configSwitchIdDomain) {
		this.redeIpFunctions = redeIpFunctions;
		this.configSwitchIdDomain = configSwitchIdDomain;
	}
	
	/**
	 * checkStatusInterface
	 * @param hostname
	 * @param vlanGerencia
	 * @return
	 */
	public RetornoModel findUserConfigure(String hostname, String ipSwa) {
		
		RetornoModel retornoModel;		
		
		try {			
			String comando = String.format("enable");
			String retorno = redeIpFunctions.enviarComandoAvailableMoreCoriant(comando, new String[] {hostname + "#", hostname + ">", ")#"}, new String[] {"-- more --", "--More--", "-- More --"}, SimpleTelnet.SPACE,
					RedeHelper.retornaInfoProcedimento(SwitchCoriantCommons.class.getName(), hostname, "findUserConfigure"));
			redeIpFunctions.getLog().append(StringHelper.addNewComand(comando, retorno));
			
			comando = String.format("configure terminal");
			retorno = redeIpFunctions.enviarComandoAvailableMoreCoriant(comando, new String[] {hostname + "#", hostname + ">", ")#"}, new String[] {"-- more --", "--More--", "-- More --"}, SimpleTelnet.SPACE,
					RedeHelper.retornaInfoProcedimento(SwitchCoriantCommons.class.getName(), hostname, "findUserConfigure"));
			redeIpFunctions.getLog().append(StringHelper.addNewComand(comando, retorno));
			
			if (StringUtils.isBlank(retorno) 
					|| StringUtils.containsIgnoreCase(retorno, "Invalid input detected") 
					|| !StringUtils.containsIgnoreCase(retorno, ")#")) {
				
				// Saindo do equipamento
				redeIpFunctions.fecharConexaoEquipamento(RedeHelper.retornaInfoProcedimento(SwitchCoriantCommons.class.getName(), configSwitchIdDomain.getIdVantive(), "findUserConfigure"));
				
				// tentando conectar com as senhas fixas
				JSONArray array = redeIpFunctions.retornaListaSenhasSwitchCoriant();
				boolean conectouModoConfigure = false;
				for(int c = 0; c < array.length(); c++) {
					JSONObject json = array.getJSONObject(c);
					if(redeIpFunctions.conectarEquipamentoSwitch(configSwitchIdDomain.getIpSwa(), json.getString("login"), json.getString("senha"),
							RedeHelper.retornaInfoProcedimento(AtivacaoSwitchFunctions.class.getName(), configSwitchIdDomain.getIdVantive(), "findUserConfigure"), json)) {
						
						comando = String.format("enable");
						retorno = redeIpFunctions.enviarComandoAvailableMoreCoriant(comando, new String[] {hostname + "#", hostname + ">", ")#"}, new String[] {"-- more --", "--More--", "-- More --"}, SimpleTelnet.SPACE,
								RedeHelper.retornaInfoProcedimento(SwitchCoriantCommons.class.getName(), hostname, "findUserConfigure"));
						redeIpFunctions.getLog().append(StringHelper.addNewComand(comando, retorno));
						
						comando = String.format("configure terminal");
						retorno = redeIpFunctions.enviarComandoAvailableMoreCoriant(comando, new String[] {hostname + "#", hostname + ">", ")#"}, new String[] {"-- more --", "--More--", "-- More --"}, SimpleTelnet.SPACE,
								RedeHelper.retornaInfoProcedimento(SwitchCoriantCommons.class.getName(), hostname, "findUserConfigure"));
						redeIpFunctions.getLog().append(StringHelper.addNewComand(comando, retorno));
						
						if (StringUtils.isBlank(retorno) 
								|| StringUtils.containsIgnoreCase(retorno, "Invalid input detected") 
								|| !StringUtils.containsIgnoreCase(retorno, ")#")) {
							// Saindo do equipamento
							redeIpFunctions.fecharConexaoEquipamento(RedeHelper.retornaInfoProcedimento(SwitchCoriantCommons.class.getName(), configSwitchIdDomain.getIdVantive(), "findUserConfigure"));
							continue;
						}
						
						conectouModoConfigure = true;
						break;
					}
				}
				
				if(!conectouModoConfigure) {
					retornoModel = new RetornoModel();
					retornoModel.setCodigo(ResultadosStatus.FALHOU.getCodigo());
					retornoModel.setResultado(false);
					retornoModel.setRetorno("Erro acessar o modo de configuração");
					retornoModel.setLog(StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()));
					return retornoModel;
				}					
			}
			
			comando = String.format("exit");
			retorno = redeIpFunctions.enviarComandoAvailableMoreCoriant(comando, new String[] {hostname + "#", hostname + ">", ")#"}, new String[] {"-- more --", "--More--", "-- More --"}, SimpleTelnet.SPACE,
					RedeHelper.retornaInfoProcedimento(SwitchCoriantCommons.class.getName(), hostname, "iniciaProcedimentoConfiguracao"));
			redeIpFunctions.getLog().append(StringHelper.addNewComand(comando, retorno));
			
			retornoModel = new RetornoModel();
			retornoModel.setCodigo(ResultadosStatus.OK.getCodigo());
			retornoModel.setResultado(true);
			retornoModel.setRetorno("Acessou modo de configuração");
			retornoModel.setLog(StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()));			
			return retornoModel;
				
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e),
					RedeHelper.retornaInfoProcedimento(SwitchCoriantCommons.class.getName(), configSwitchIdDomain.getHostnameSwa(), "checkVlanGerencia"));			
			
			retornoModel = new RetornoModel();
			retornoModel.setCodigo(ResultadosStatus.FALHOU.getCodigo());
			retornoModel.setResultado(false);
			retornoModel.setRetorno("Erro");
			retornoModel.setLog("");
			return retornoModel;
		}
	}
	
	/**
	 * checkStatusInterface
	 * @param hostname
	 * @param vlanGerencia
	 * @return
	 */
	public RetornoModel checkStatusInterface(String hostname, String tipoInterface, String portaCliente) {
		
		RetornoModel retornoModel;		
		
		try {
			String comando = String.format("show interface %s %s", tipoInterface, portaCliente);
			String retorno = redeIpFunctions.enviarComandoAvailableMoreCoriant(comando, new String[] {hostname + "#", hostname + ">"}, new String[] {"-- more --", "--More--", "-- More --"}, SimpleTelnet.SPACE,
					RedeHelper.retornaInfoProcedimento(SwitchCoriantCommons.class.getName(), hostname, "checkStatusInterface"));
			redeIpFunctions.getLog().append(StringHelper.addNewComand(comando, retorno));
			
			String[] linhasComando = retorno.split(GlobalStrEnum.BREAK_LINE.toString());
			for(int c = 0; c < linhasComando.length; c++) {
				String linha = linhasComando[c].trim();
				if(StringUtils.containsIgnoreCase(linha, "L1 Fault Status:")) {
					String status = StringUtils.substringAfterLast(linha, ":").trim();
					if(StringUtils.equalsIgnoreCase(status, "Link down")) {
						retornoModel = new RetornoModel();
						retornoModel.setCodigo(ResultadosStatus.OK.getCodigo());
						retornoModel.setResultado(true);
						retornoModel.setRetorno(String.format("Check Ok. %s", status));
						retornoModel.setLog(StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()));			
						return retornoModel;
					}
					
					retornoModel = new RetornoModel();
					retornoModel.setCodigo(ResultadosStatus.FALHOU.getCodigo());
					retornoModel.setResultado(false);
					retornoModel.setRetorno(String.format("Check Falhou. %s", status));
					retornoModel.setLog(StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()));			
					return retornoModel;
				}
			}
			
			retornoModel = new RetornoModel();
			retornoModel.setCodigo(ResultadosStatus.FALHOU.getCodigo());
			retornoModel.setResultado(false);
			retornoModel.setRetorno("Status da Interface não foi encontrado");
			retornoModel.setLog(StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()));			
			return retornoModel;
				
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e),
					RedeHelper.retornaInfoProcedimento(SwitchCoriantCommons.class.getName(), configSwitchIdDomain.getHostnameSwa(), "checkVlanGerencia"));			
			
			retornoModel = new RetornoModel();
			retornoModel.setCodigo(ResultadosStatus.FALHOU.getCodigo());
			retornoModel.setResultado(false);
			retornoModel.setRetorno("Erro");
			retornoModel.setLog("");
			return retornoModel;
		}
	}
	
	/**
	 * iniciaProcedimentoConfiguracaoDatacom
	 * @param hostname
	 * @param lstScripts
	 * @return
	 */
	public RetornoModel iniciaProcedimentoConfiguracao(ConfigSwitchIdDomain configSwitchIdDomain, String hostname, List<ConfigSwitchScriptDomain> lstScripts) {
		
		RetornoModel retornoModel;
		
		if(lstScripts == null || lstScripts.isEmpty()) {
			retornoModel = new RetornoModel();
			retornoModel.setCodigo(ResultadosStatus.WARNING.getCodigo());
			retornoModel.setResultado(false);
			retornoModel.setRetorno("Não existem scripts cadastrados");
			retornoModel.setLog(StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()));
			return retornoModel;
		}
		
		try {
			// acessar modo de configuracao
			
			String comando = String.format("configure terminal");
			String retorno = redeIpFunctions.enviarComandoAvailableMoreCoriant(comando, new String[] {hostname + "#", hostname + ">", ")#"}, new String[] {"-- more --", "--More--", "-- More --"}, SimpleTelnet.SPACE,
					RedeHelper.retornaInfoProcedimento(SwitchCoriantCommons.class.getName(), hostname, "iniciaProcedimentoConfiguracao"));
			redeIpFunctions.getLog().append(StringHelper.addNewComand(comando, retorno));
			
			if (StringUtils.isBlank(retorno) || StringUtils.containsIgnoreCase(retorno, "Invalid input detected") || !StringUtils.containsIgnoreCase(retorno, ")#")) {
				retornoModel = new RetornoModel();
				retornoModel.setCodigo(ResultadosStatus.FALHOU.getCodigo());
				retornoModel.setResultado(false);
				retornoModel.setRetorno("Erro acessar o modo de configuração");
				retornoModel.setLog(StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()));
				return retornoModel;
			}
			
			// aplicando os scripts

			boolean configuracaoFalhou = false;
			for(ConfigSwitchScriptDomain configSwitchScriptDomain: lstScripts) {
				
				String scriptStr = configSwitchScriptDomain.replaceVariaveisScript(configSwitchIdDomain);
				String[] linhasComando = scriptStr.split(GlobalStrEnum.BREAK_LINE.toString());				
				
				// enviando os comandos
				for(int c = 0; c < linhasComando.length; c++) {					
					String linha = linhasComando[c].trim();
					retorno = redeIpFunctions.enviarComandoAvailableMoreCoriant(linha, new String[] {hostname + "#", hostname + ">", ")#"}, new String[] {"-- more --", "--More--", "-- More --"}, SimpleTelnet.SPACE,
							RedeHelper.retornaInfoProcedimento(SwitchCoriantCommons.class.getName(), hostname, "iniciaProcedimentoConfiguracao"));
					redeIpFunctions.getLog().append(StringHelper.addNewComand(comando, retorno));
					
					if (StringUtils.isBlank(retorno) 
							|| StringUtils.containsIgnoreCase(retorno, "Invalid input detected")
							|| StringUtils.containsIgnoreCase(retorno, "syntax error:")
							|| StringUtils.containsIgnoreCase(retorno, "Unrecognised input")) {
						configuracaoFalhou = true;
						break;
					}
				}				
			}
						
			// sair do modo de configuracao
			
			comando = String.format("exit");
			retorno = redeIpFunctions.enviarComandoAvailableMoreCoriant(comando, new String[] {hostname + "#", hostname + ">", ")#", "[yes/no/CANCEL]"}, new String[] {"-- more --", "--More--", "-- More --"}, SimpleTelnet.SPACE,
					RedeHelper.retornaInfoProcedimento(SwitchCoriantCommons.class.getName(), hostname, "iniciaProcedimentoConfiguracao"));
			redeIpFunctions.getLog().append(StringHelper.addNewComand(comando, retorno));
			
			if (StringUtils.isBlank(retorno)) {
				retornoModel = new RetornoModel();
				retornoModel.setCodigo(ResultadosStatus.FALHOU.getCodigo());
				retornoModel.setResultado(false);
				retornoModel.setRetorno("Erro comando");
				retornoModel.setLog(StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()));
				return retornoModel;
			}
			
			if(StringUtils.containsIgnoreCase(retorno, "[yes/no/CANCEL]")) {
				comando = String.format("yes");
				retorno = redeIpFunctions.enviarComandoAvailableMoreCoriant(comando, new String[] {hostname + "#", hostname + ">", ")#", "[yes/no/CANCEL]"}, new String[] {"-- more --", "--More--", "-- More --"}, SimpleTelnet.SPACE,
						RedeHelper.retornaInfoProcedimento(SwitchCoriantCommons.class.getName(), hostname, "iniciaProcedimentoConfiguracao"));
				redeIpFunctions.getLog().append(StringHelper.addNewComand(comando, retorno));
			}
			
			if(configuracaoFalhou) {
				retornoModel = new RetornoModel();
				retornoModel.setCodigo(ResultadosStatus.WARNING.getCodigo());
				retornoModel.setResultado(true);
				retornoModel.setRetorno("Script retornou erros em linhas de configuração");
				retornoModel.setLog(StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()));			
				return retornoModel;
			}
			
			retornoModel = new RetornoModel();
			retornoModel.setCodigo(ResultadosStatus.OK.getCodigo());
			retornoModel.setResultado(true);
			retornoModel.setRetorno("Configuração de Script Ok");
			retornoModel.setLog(StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()));			
			return retornoModel;
				
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e),
					RedeHelper.retornaInfoProcedimento(SwitchCoriantCommons.class.getName(), configSwitchIdDomain.getHostnameSwa(), "checkVlanGerencia"));			
			
			retornoModel = new RetornoModel();
			retornoModel.setCodigo(ResultadosStatus.FALHOU.getCodigo());
			retornoModel.setResultado(false);
			retornoModel.setRetorno("Erro");
			retornoModel.setLog("");
			return retornoModel;
		}
	}	
	
	/**
	 * checkVlanGerencia
	 * @param hostname
	 * @param vlanGerencia
	 * @return
	 */
	public RetornoModel checkVlanGerencia(String hostname, String vlanGerencia) {
		
		RetornoModel retornoModel;		
		
		try {
			String comando = String.format("show run vlan id %s", vlanGerencia);
			String retorno = redeIpFunctions.enviarComandoAvailableMoreCoriant(comando, new String[] {hostname + "#", hostname + ">"}, new String[] {"-- more --", "--More--", "-- More --"}, SimpleTelnet.SPACE,
					RedeHelper.retornaInfoProcedimento(SwitchCoriantCommons.class.getName(), hostname, "checkVlanGerencia"));
			
			redeIpFunctions.getLog().append(StringHelper.addNewComand(comando, retorno));
			
			if (StringUtils.isBlank(retorno)) {
				retornoModel = new RetornoModel();
				retornoModel.setCodigo(ResultadosStatus.FALHOU.getCodigo());
				retornoModel.setResultado(false);
				retornoModel.setRetorno("Erro comando");
				retornoModel.setLog(StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()));
				return retornoModel;
			}
			
			if(StringUtils.containsIgnoreCase(retorno, String.format("interface vlan %s", vlanGerencia))) {
				retornoModel = new RetornoModel();
				retornoModel.setCodigo(ResultadosStatus.OK.getCodigo());
				retornoModel.setResultado(true);
				retornoModel.setRetorno("Configuração Ok");
				retornoModel.setLog(StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()));			
				return retornoModel;
			}
			
			retornoModel = new RetornoModel();
			retornoModel.setCodigo(ResultadosStatus.FALHOU.getCodigo());
			retornoModel.setResultado(false);
			retornoModel.setRetorno("Configuração não encontrada");
			retornoModel.setLog(StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()));			
			return retornoModel;
				
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e),
					RedeHelper.retornaInfoProcedimento(SwitchCoriantCommons.class.getName(), configSwitchIdDomain.getHostnameSwa(), "checkVlanGerencia"));			
			
			retornoModel = new RetornoModel();
			retornoModel.setCodigo(ResultadosStatus.FALHOU.getCodigo());
			retornoModel.setResultado(false);
			retornoModel.setRetorno("Erro");
			retornoModel.setLog("");
			return retornoModel;
		}
	}
	
	/**
	 * checkVlanTunelServico
	 * @param hostname
	 * @param sVlan
	 * @return
	 */
	public RetornoModel checkVlanTunelServico(String hostname, ConfigSwitchIdDomain configSwitchIdDomain) {
		
		RetornoModel retornoModel;		
		
		try {
			String comando = String.format("show run vlan id %s", configSwitchIdDomain.getsVlan());
			String retorno = redeIpFunctions.enviarComandoAvailableMoreCoriant(comando, new String[] {hostname + "#", hostname + ">"}, new String[] {"-- more --", "--More--", "-- More --"}, SimpleTelnet.SPACE,
					RedeHelper.retornaInfoProcedimento(SwitchCoriantCommons.class.getName(), hostname, "checkVlanTunelServico"));
			
			redeIpFunctions.getLog().append(StringHelper.addNewComand(comando, retorno));
			
			if (StringUtils.isBlank(retorno)) {
				retornoModel = new RetornoModel();
				retornoModel.setCodigo(ResultadosStatus.FALHOU.getCodigo());
				retornoModel.setResultado(false);
				retornoModel.setRetorno("Erro comando");
				retornoModel.setLog(StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()));
				return retornoModel;
			}
			
			if(StringUtils.containsIgnoreCase(retorno, String.format("interface vlan %s", configSwitchIdDomain.getsVlan()))) {
				retornoModel = new RetornoModel();
				retornoModel.setCodigo(ResultadosStatus.OK.getCodigo());
				retornoModel.setResultado(true);
				retornoModel.setRetorno("Configuração Ok");
				retornoModel.setLog(StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()));			
				return retornoModel;
			}
			
			if(StringUtils.containsIgnoreCase(retorno, "syntax error:")) {
				
				comando = String.format("show running-config vlan-mapping interface %s-%s", configSwitchIdDomain.getTipoInterface(), configSwitchIdDomain.getPortaCliente());
				retorno = redeIpFunctions.enviarComandoAvailableMoreCoriant(comando, new String[] {hostname + "#", hostname + ">"}, new String[] {"-- more --", "--More--", "-- More --"}, SimpleTelnet.SPACE,
						RedeHelper.retornaInfoProcedimento(SwitchCoriantCommons.class.getName(), hostname, "checkVlanTunelServico"));
				
				redeIpFunctions.getLog().append(StringHelper.addNewComand(comando, retorno));
				
				if(StringUtils.containsIgnoreCase(retorno, "vlan-id " + configSwitchIdDomain.getVlanGerenciaSwa())
						&& StringUtils.containsIgnoreCase(retorno, "vlan-id " + configSwitchIdDomain.getsVlan())
						&& StringUtils.containsIgnoreCase(retorno, "vlan-id " + configSwitchIdDomain.getcVlan())) {
					retornoModel = new RetornoModel();
					retornoModel.setCodigo(ResultadosStatus.OK.getCodigo());
					retornoModel.setResultado(true);
					retornoModel.setRetorno("Configuração Ok");
					retornoModel.setLog(StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()));			
					return retornoModel;
				}
			}
			
			retornoModel = new RetornoModel();
			retornoModel.setCodigo(ResultadosStatus.FALHOU.getCodigo());
			retornoModel.setResultado(false);
			retornoModel.setRetorno("Configuração não encontrada");
			retornoModel.setLog(StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()));			
			return retornoModel;
			
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e),
					RedeHelper.retornaInfoProcedimento(SwitchCoriantCommons.class.getName(), configSwitchIdDomain.getHostnameSwa(), "checkVlanTunelServico"));			
			
			retornoModel = new RetornoModel();
			retornoModel.setCodigo(ResultadosStatus.FALHOU.getCodigo());
			retornoModel.setResultado(false);
			retornoModel.setRetorno("Erro");
			retornoModel.setLog("");
			return retornoModel;
		}
	}
	
	/**
	 * checkConfiguracaoInterface
	 * @param hostname
	 * @param tipoInterface
	 * @param portaCliente
	 * @return
	 */
	public RetornoModel checkConfiguracaoInterface(String hostname, ConfigSwitchIdDomain configSwitchIdDomain) {
		
		RetornoModel retornoModel;		
		
		try {
			String comando = String.format("show run interface %s %s", configSwitchIdDomain.getTipoInterface(), configSwitchIdDomain.getPortaCliente());
			String retorno = redeIpFunctions.enviarComandoAvailableMoreCoriant(comando, new String[] {hostname + "#", hostname + ">"}, new String[] {"-- more --", "--More--", "-- More --"}, SimpleTelnet.SPACE,
					RedeHelper.retornaInfoProcedimento(SwitchCoriantCommons.class.getName(), hostname, "checkConfiguracaoInterface"));
			
			redeIpFunctions.getLog().append(StringHelper.addNewComand(comando, retorno));
			
			if (StringUtils.isBlank(retorno)) {
				retornoModel = new RetornoModel();
				retornoModel.setCodigo(ResultadosStatus.FALHOU.getCodigo());
				retornoModel.setResultado(false);
				retornoModel.setRetorno("Erro comando");
				retornoModel.setLog(StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()));
				return retornoModel;
			}
			
			if(StringUtils.containsIgnoreCase(retorno, String.format("description"))
					&& (StringUtils.containsIgnoreCase(retorno, configSwitchIdDomain.getIpSwt())
							|| StringUtils.containsIgnoreCase(retorno, configSwitchIdDomain.getIpSwt().replaceAll("\\.", "_")))) {
				retornoModel = new RetornoModel();
				retornoModel.setCodigo(ResultadosStatus.OK.getCodigo());
				retornoModel.setResultado(true);
				retornoModel.setRetorno("Configuração Ok");
				retornoModel.setLog(StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()));			
				return retornoModel;
			}
			
			retornoModel = new RetornoModel();
			retornoModel.setCodigo(ResultadosStatus.FALHOU.getCodigo());
			retornoModel.setResultado(false);
			retornoModel.setRetorno("Configuração não encontrada");
			retornoModel.setLog(StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()));			
			return retornoModel;
				
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e),
					RedeHelper.retornaInfoProcedimento(SwitchCoriantCommons.class.getName(), configSwitchIdDomain.getHostnameSwa(), "checkConfiguracaoInterface"));			
			
			retornoModel = new RetornoModel();
			retornoModel.setCodigo(ResultadosStatus.FALHOU.getCodigo());
			retornoModel.setResultado(false);
			retornoModel.setRetorno("Erro");
			retornoModel.setLog("");
			return retornoModel;
		}
	}
	
	/**
	 * checkValidacaoVlanTable
	 * @param hostname
	 * @param tipoInterface
	 * @param portaCliente
	 * @return
	 */
	public RetornoModel checkValidacaoVlanTable(String hostname, ConfigSwitchIdDomain configSwitchIdDomain) {
		
		RetornoModel retornoModel;		
		
		try {
			String comando = String.format("show vlan-translate table interface %s %s", configSwitchIdDomain.getTipoInterface(), configSwitchIdDomain.getPortaCliente());
			String retorno = redeIpFunctions.enviarComandoAvailableMoreCoriant(comando, new String[] {hostname + "#", hostname + ">"}, new String[] {"-- more --", "--More--", "-- More --"}, SimpleTelnet.SPACE,
					RedeHelper.retornaInfoProcedimento(SwitchCoriantCommons.class.getName(), hostname, "checkValidacaoVlanTable"));
			
			redeIpFunctions.getLog().append(StringHelper.addNewComand(comando, retorno));
			
			if (StringUtils.isBlank(retorno)) {
				retornoModel = new RetornoModel();
				retornoModel.setCodigo(ResultadosStatus.FALHOU.getCodigo());
				retornoModel.setResultado(false);
				retornoModel.setRetorno("Erro comando");
				retornoModel.setLog(StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()));
				return retornoModel;
			}
			
			if(StringUtils.containsIgnoreCase(retorno, configSwitchIdDomain.getsVlan())
					&& StringUtils.containsIgnoreCase(retorno, configSwitchIdDomain.getcVlan())) {
				retornoModel = new RetornoModel();
				retornoModel.setCodigo(ResultadosStatus.OK.getCodigo());
				retornoModel.setResultado(true);
				retornoModel.setRetorno("Configuração Ok");
				retornoModel.setLog(StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()));			
				return retornoModel;
			}
			
			retornoModel = new RetornoModel();
			retornoModel.setCodigo(ResultadosStatus.FALHOU.getCodigo());
			retornoModel.setResultado(false);
			retornoModel.setRetorno("Configuração não encontrada");
			retornoModel.setLog(StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()));			
			return retornoModel;
				
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e),
					RedeHelper.retornaInfoProcedimento(SwitchCoriantCommons.class.getName(), configSwitchIdDomain.getHostnameSwa(), "checkValidacaoVlanTable"));			
			
			retornoModel = new RetornoModel();
			retornoModel.setCodigo(ResultadosStatus.FALHOU.getCodigo());
			retornoModel.setResultado(false);
			retornoModel.setRetorno("Erro");
			retornoModel.setLog("");
			return retornoModel;
		}
	}
		
}
