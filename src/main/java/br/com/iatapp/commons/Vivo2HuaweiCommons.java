package br.com.iatapp.commons;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONObject;

import br.com.iatapp.domain.Vivo2IdDomain;
import br.com.iatapp.domain.Vivo2ScriptDomain;
import br.com.iatapp.enums.GlobalStrEnum;
import br.com.iatapp.enums.JsonKeysEnum;
import br.com.iatapp.enums.ResultadosStatus;
import br.com.iatapp.helper.RedeHelper;
import br.com.iatapp.helper.StringHelper;
import br.com.iatapp.logger.ExceptionLogger;
import br.com.iatapp.model.RetornoModel;
import br.com.iatapp.rede.RedeIpFunctions;
import br.com.iatapp.rede.SimpleTelnet;

public class Vivo2HuaweiCommons {
	
	RedeIpFunctions redeIpFunctions;
	Vivo2IdDomain vivo2IdDomain;
	String hostnameEquipamento;
	
	public Vivo2HuaweiCommons(RedeIpFunctions redeIpFunctions, Vivo2IdDomain vivo2IdDomain) {
		this.redeIpFunctions = redeIpFunctions;
		this.vivo2IdDomain = vivo2IdDomain;
		this.hostnameEquipamento = vivo2IdDomain.getHostnameEquipamento().toLowerCase();
	}
	
	/**
	 * checkIpsLivres
	 * @return
	 */
	public RetornoModel checkIpsLivres() {
		
		RetornoModel retornoModel;
		
		// Check IP LAN
		retornoModel = checkIpv4Livre(vivo2IdDomain.getIpLan());
		if(!retornoModel.isResultado()) {
			retornoModel.setRetorno("Error Check Ipv4 Lan");
			return retornoModel;
		}
		
		// Check IP WAN
		retornoModel = checkIpv4Livre(vivo2IdDomain.getIpWan());
		if(!retornoModel.isResultado()) {
			retornoModel.setRetorno("Error Check Ipv4 Wan");
			return retornoModel;
		}
		
//		// Check IPV6 LAN
//		retornoModel = checkIpv6Livre(vivo2IdDomain.getIpv6Lan(), vivo2IdDomain.getMascaraIpv6Lan(), "60");
//		if(!retornoModel.isResultado()) {
//			retornoModel.setRetorno("Error Check Ipv6 Lan");
//			return retornoModel;
//		}
//		
//		// Check IPV6 WAN
//		retornoModel = checkIpv6Livre(vivo2IdDomain.getIpv6Wan(), vivo2IdDomain.getMascaraIpv6Wan(), "126");
//		if(!retornoModel.isResultado()) {
//			retornoModel.setRetorno("Error Check Ipv6 Wan");
//			return retornoModel;
//		}
		
		// resultado
		retornoModel.setRetorno("Check IPs Livres OK");
		return retornoModel;
		
	}
	
	/**
	 * checkIpv4Livre
	 * @return
	 */
	public RetornoModel checkIpv4Livre(String ipv4) {
		
		RetornoModel retornoModel;
		
		try {
			String comando = "display ip routing-table " + ipv4;
			String retorno = redeIpFunctions.enviarComandoAvailableMore(comando, new String[] {hostnameEquipamento + "#", hostnameEquipamento + ">"}, new String[] {"-- more --", "--More--", "-- More --"}, SimpleTelnet.SPACE,
					RedeHelper.retornaInfoProcedimento(Vivo2HuaweiCommons.class.getName(), vivo2IdDomain.getIdTbs(), "checkIpsLivres"));
			
			if (StringUtils.isBlank(retorno)) {
				retornoModel = new RetornoModel();
				retornoModel.setCodigo(ResultadosStatus.FALHOU.getCodigo());
				retornoModel.setResultado(false);
				retornoModel.setRetorno("Erro");
				retornoModel.setLog(StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()));			
				return retornoModel;
			}
			
			// Seta Log
			redeIpFunctions.setLog(redeIpFunctions.getLog().append(retorno));
			
			retorno = StringHelper.removeComando(retorno);
			
			if(StringUtils.containsIgnoreCase(retorno, ipv4)) {
				retornoModel = new RetornoModel();
				retornoModel.setCodigo(ResultadosStatus.FALHOU.getCodigo());
				retornoModel.setResultado(false);
				retornoModel.setRetorno(String.format("IP %s está duplicado", ipv4));
				retornoModel.setLog(StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()));			
				return retornoModel;
			}
			
			retornoModel = new RetornoModel();
			retornoModel.setCodigo(ResultadosStatus.OK.getCodigo());
			retornoModel.setResultado(true);
			retornoModel.setRetorno(comando);
			retornoModel.setLog(StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()));			
			return retornoModel;
			
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e),
					RedeHelper.retornaInfoProcedimento(Vivo2HuaweiCommons.class.getName(), vivo2IdDomain.getIdTbs(), "checkIpsLivres"));
			retornoModel = new RetornoModel();
			retornoModel.setCodigo(ResultadosStatus.FALHOU.getCodigo());
			retornoModel.setResultado(false);
			retornoModel.setRetorno("Erro");
			retornoModel.setLog("");			
			return retornoModel;
		}
	}
	
	/**
	 * checkIpv6Livre
	 * @return
	 */
	public RetornoModel checkIpv6Livre(String ipv6, String mascara1, String mascara2) {
		
		RetornoModel retornoModel;
		
		try {
			String comando = "display ipv6 routing-table " + ipv6;
			String retorno = redeIpFunctions.enviarComandoAvailableMore(comando, new String[] {hostnameEquipamento + "#", hostnameEquipamento + ">"}, new String[] {"-- more --", "--More--", "-- More --"}, SimpleTelnet.SPACE,
					RedeHelper.retornaInfoProcedimento(Vivo2HuaweiCommons.class.getName(), vivo2IdDomain.getIdTbs(), "checkIpsLivres"));
			
			if (StringUtils.isBlank(retorno)) {
				retornoModel = new RetornoModel();
				retornoModel.setCodigo(ResultadosStatus.FALHOU.getCodigo());
				retornoModel.setResultado(false);
				retornoModel.setRetorno("Erro");
				retornoModel.setLog("");			
				return retornoModel;
			}
			
			// Seta Log
			redeIpFunctions.setLog(redeIpFunctions.getLog().append(retorno));
			
			retorno = StringHelper.removeComando(retorno);
			
			if(!StringUtils.containsIgnoreCase(retorno, ipv6)) {
				retornoModel = new RetornoModel();
				retornoModel.setCodigo(ResultadosStatus.FALHOU.getCodigo());
				retornoModel.setResultado(false);
				retornoModel.setRetorno(String.format("IP %s está duplicado", ipv6));
				retornoModel.setLog(StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()));			
				return retornoModel;
			}
			
			retornoModel = new RetornoModel();
			retornoModel.setCodigo(ResultadosStatus.OK.getCodigo());
			retornoModel.setResultado(true);
			retornoModel.setRetorno(comando);
			retornoModel.setLog(StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()));			
			return retornoModel;
			
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e),
					RedeHelper.retornaInfoProcedimento(Vivo2HuaweiCommons.class.getName(), vivo2IdDomain.getIdTbs(), "checkIpsLivres"));
			retornoModel = new RetornoModel();
			retornoModel.setCodigo(ResultadosStatus.FALHOU.getCodigo());
			retornoModel.setResultado(false);
			retornoModel.setRetorno("Erro");
			retornoModel.setLog("");			
			return retornoModel;
		}
	}	
	
	/**
	 * checkVlans
	 * @return
	 */
	public RetornoModel checkInterfaceConfiguracao() {
		
		RetornoModel retornoModel;
		JSONObject jsonDados = vivo2IdDomain.getJsonDados();
		
		try {
			String comando = "display interface description | include " + vivo2IdDomain.getVlanRede();
			String retorno = redeIpFunctions.enviarComandoAvailableMore(comando, new String[] {hostnameEquipamento + "#", hostnameEquipamento + ">"}, new String[] {"-- more --", "--More--", "-- More --"}, SimpleTelnet.SPACE,
					RedeHelper.retornaInfoProcedimento(Vivo2HuaweiCommons.class.getName(), vivo2IdDomain.getIdTbs(), "checkIpsLivres"));
			
			if (StringUtils.isBlank(retorno)) {
				retornoModel = new RetornoModel();
				retornoModel.setCodigo(ResultadosStatus.FALHOU.getCodigo());
				retornoModel.setResultado(false);
				retornoModel.setRetorno("Erro");
				retornoModel.setLog("");			
				return retornoModel;
			}
			
			// Seta Log
			redeIpFunctions.setLog(redeIpFunctions.getLog().append(retorno));
			
			// procurando a interface para configuracao
			
			String[] linhasRetorno = retorno.split(GlobalStrEnum.BREAK_LINE.toString());
			for (int c1 = 0; c1 < linhasRetorno.length; c1++) {
				
				String linha = linhasRetorno[c1].trim();
				
				if(StringUtils.containsIgnoreCase(linha, "." +  vivo2IdDomain.getVlanRede()) 
						&& StringUtils.countMatches(linha, "up") > 1) {
					
					if(StringUtils.isBlank(jsonDados.optString(JsonKeysEnum.VIVO_2_INTERFACE_CONFIGURACAO.getCodigo()))) {
						String aux = StringUtils.substringBefore(linha, "." +  vivo2IdDomain.getVlanRede()).trim();
						jsonDados.put(JsonKeysEnum.VIVO_2_INTERFACE_CONFIGURACAO.getCodigo(), aux);
						continue;
					}
					
					String aux = jsonDados.getString(JsonKeysEnum.VIVO_2_INTERFACE_CONFIGURACAO.getCodigo());
					if(!StringUtils.containsIgnoreCase(linha, aux)) {
						retornoModel = new RetornoModel();
						retornoModel.setCodigo(ResultadosStatus.FALHOU.getCodigo());
						retornoModel.setResultado(false);
						retornoModel.setRetorno(String.format("Existe mais de uma Interface configurada com na Vlan de Rede %s", vivo2IdDomain.getVlanRede()));
						retornoModel.setLog(StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()));			
						return retornoModel;
					}
					
					continue;
				}
			}
			
			// verificando se apareceu 
			if(StringUtils.isBlank(jsonDados.optString(JsonKeysEnum.VIVO_2_INTERFACE_CONFIGURACAO.getCodigo()))) {
				retornoModel = new RetornoModel();
				retornoModel.setCodigo(ResultadosStatus.FALHOU.getCodigo());
				retornoModel.setResultado(false);
				retornoModel.setRetorno(String.format("Interface não encontrada"));
				retornoModel.setLog(StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()));			
				return retornoModel;
			}
			
			// incrementando
			String aux = jsonDados.getString(JsonKeysEnum.VIVO_2_INTERFACE_CONFIGURACAO.getCodigo());
			if(aux.startsWith("GE"))
				aux = aux.replace("GE", "GigabitEthernet");
			else if(aux.startsWith("Ge"))
				aux = aux.replace("Ge", "GigabitEthernet");
			else if(aux.startsWith("Gi"))
				aux = aux.replace("Gi", "GigabitEthernet");
			
			if(StringUtils.containsIgnoreCase(retorno, "." + vivo2IdDomain.getVlanRede() + vivo2IdDomain.getVlanUsuario() + " ")) {
				retornoModel = new RetornoModel();
				retornoModel.setCodigo(ResultadosStatus.FALHOU.getCodigo());
				retornoModel.setResultado(false);
				retornoModel.setRetorno(String.format("Já existe uma interface configurada com a Vlan de Usuário"));
				retornoModel.setLog(StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()));			
				return retornoModel;
			}
			
			jsonDados.put(JsonKeysEnum.VIVO_2_INTERFACE_CONFIGURACAO.getCodigo(), aux + "." + vivo2IdDomain.getVlanRede() + vivo2IdDomain.getVlanUsuario());
			
			// retorno ok
			retornoModel = new RetornoModel();
			retornoModel.setCodigo(ResultadosStatus.OK.getCodigo());
			retornoModel.setResultado(true);
			retornoModel.setRetorno(String.format("Interface encontrada %s", jsonDados.getString(JsonKeysEnum.VIVO_2_INTERFACE_CONFIGURACAO.getCodigo())));
			retornoModel.setLog(StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()));			
			return retornoModel;
			
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e),
					RedeHelper.retornaInfoProcedimento(Vivo2HuaweiCommons.class.getName(), vivo2IdDomain.getIdTbs(), "checkInterfaceConfiguracao"));
			retornoModel = new RetornoModel();
			retornoModel.setCodigo(ResultadosStatus.FALHOU.getCodigo());
			retornoModel.setResultado(false);
			retornoModel.setRetorno("Erro na execução");
			retornoModel.setLog("");			
			return retornoModel;
		}
	}
	
	/**
	 * iniciaProcedimentoConfiguracao
	 * @param lstScripts
	 * @return
	 */
	public RetornoModel iniciaProcedimentoConfiguracao(List<Vivo2ScriptDomain> lstScripts) {
		
		RetornoModel retornoModel;
		
		if(lstScripts == null || lstScripts.isEmpty()) {
			retornoModel = new RetornoModel();
			retornoModel.setCodigo(ResultadosStatus.WARNING.getCodigo());
			retornoModel.setResultado(false);
			retornoModel.setRetorno("Não existem scripts cadastrados");
			retornoModel.setLog(StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()));
			return retornoModel;
		}
		
		// modo conf
		String comando = String.format("system-view");
		String retorno = redeIpFunctions.enviarComandoAvailableMore(comando, new String[] {">", "]"}, "-- more --", "",
				RedeHelper.retornaInfoProcedimento(Vivo2HuaweiCommons.class.getName(), "", ""));
		if(StringUtils.isBlank(retorno)) {
			retornoModel = new RetornoModel();
			retornoModel.setCodigo(ResultadosStatus.FALHOU.getCodigo());
			retornoModel.setResultado(false);
			retornoModel.setRetorno("Não foi possível acessar o modo de Configuração");
			retornoModel.setLog(StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()));
			return retornoModel;
		}
		
		redeIpFunctions.setLog(redeIpFunctions.getLog().append(retorno));
		
		// verificando se entrou no modo config
		if(!StringUtils.containsIgnoreCase(retorno, "]")) {
			retornoModel = new RetornoModel();
			retornoModel.setCodigo(ResultadosStatus.FALHOU.getCodigo());
			retornoModel.setResultado(false);
			retornoModel.setRetorno("Não foi possível acessar o modo de Configuração");
			retornoModel.setLog(StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()));
			return retornoModel;
		}
		
		// aplicando os scripts

		boolean configuracaoFalhou = false;
		for(Vivo2ScriptDomain vivo2ScriptDomain: lstScripts) {
			
			String scriptStr = vivo2ScriptDomain.replaceVariaveisScript(vivo2IdDomain);
			
//			System.out.println("\n\n\n\nSCRIPT CONFIGURACAO\n\n\n\n\n");
//			System.out.println(scriptStr);
//			System.out.println("\n\n\n\n\n");
			
			String[] linhasComando = scriptStr.split(GlobalStrEnum.BREAK_LINE.toString());				
			
			// aplicando o script
			for(int c = 0; c < linhasComando.length; c++) {
				
				retorno = redeIpFunctions.enviarComandoAvailableMore(linhasComando[c], new String[] {">", "]"}, "-- more --", "",
						RedeHelper.retornaInfoProcedimento(Vivo2HuaweiCommons.class.getName(), "", ""));
				
				redeIpFunctions.getLog().append(StringUtils.isNotBlank(retorno) ? retorno : "");
				
				if(StringUtils.containsIgnoreCase(retorno, "Error") || StringUtils.containsIgnoreCase(retorno, "Unrecognized")) {
					configuracaoFalhou = true;
					break;
				}
			}
		}
		
		// sair do modo de configuracao
		
		comando = String.format("quit");
		retorno = redeIpFunctions.enviarComandoAvailableMore(comando, new String[] {hostnameEquipamento + "#", hostnameEquipamento + ">", ")#", "]:"}, "-- more --", "",
				RedeHelper.retornaInfoProcedimento(Vivo2HuaweiCommons.class.getName(), "", ""));
		
		redeIpFunctions.getLog().append(StringUtils.isNotBlank(retorno) ? retorno : "");
		
		if (StringUtils.isBlank(retorno)) {
			retornoModel = new RetornoModel();
			retornoModel.setCodigo(ResultadosStatus.FALHOU.getCodigo());
			retornoModel.setResultado(false);
			retornoModel.setRetorno("Erro no procedimento de configuração");
			retornoModel.setLog(StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()));
			return retornoModel;
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
	}
	
	
	/**
	 * checkConfiguracaoIps
	 * @return
	 */
	public RetornoModel checkConfiguracaoIps() {
		
		RetornoModel retornoModel;
		
		// Check IP LAN
		retornoModel = checkIpv4Livre(vivo2IdDomain.getIpLan());
		if(retornoModel.isResultado()) {
			retornoModel.setRetorno("Error Check Ipv4 Lan");
			return retornoModel;
		}
		
		// Check IP WAN
		retornoModel = checkIpv4Livre(vivo2IdDomain.getIpWan());
		if(retornoModel.isResultado()) {
			retornoModel.setRetorno("Error Check Ipv4 Wan");
			return retornoModel;
		}
		
//		// Check IPV6 LAN
//		retornoModel = checkIpv6Livre(vivo2IdDomain.getIpv6Lan(), vivo2IdDomain.getMascaraIpv6Lan(), "60");
//		if(retornoModel.isResultado()) {
//			retornoModel.setRetorno("Error Check Ipv6 Lan");
//			return retornoModel;
//		}
//		
//		// Check IPV6 WAN
//		retornoModel = checkIpv6Livre(vivo2IdDomain.getIpv6Wan(), vivo2IdDomain.getMascaraIpv6Wan(), "126");
//		if(retornoModel.isResultado()) {
//			retornoModel.setRetorno("Error Check Ipv6 Wan");
//			return retornoModel;
//		}
		
		// resultado
		retornoModel.setRetorno("Configuração de IPs OK");
		return retornoModel;
		
	}
	
	
}
