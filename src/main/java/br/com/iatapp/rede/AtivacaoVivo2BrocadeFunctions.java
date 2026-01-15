package br.com.iatapp.rede;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import br.com.iatapp.domain.Vivo2IdDomain;
import br.com.iatapp.enums.GlobalStrEnum;
import br.com.iatapp.helper.RedeHelper;
import br.com.iatapp.helper.StringHelper;
import br.com.iatapp.logger.ExceptionLogger;

public class AtivacaoVivo2BrocadeFunctions {
	
	private RedeIpFunctions redeIpFunctions;
	private Vivo2IdDomain vivo2IdDomain;
	
	public AtivacaoVivo2BrocadeFunctions(RedeIpFunctions redeIpFunctions) {
		this.redeIpFunctions = redeIpFunctions;
	}
	
	/**
	 * AtivacaoVivo2CiscoFunctions
	 * @param redeIpFunctions
	 * @param vivo2IdDomain
	 */
	public AtivacaoVivo2BrocadeFunctions(RedeIpFunctions redeIpFunctions, Vivo2IdDomain vivo2IdDomain) {
		
		try {
			this.redeIpFunctions = redeIpFunctions;
			this.vivo2IdDomain = vivo2IdDomain;
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e),
					RedeHelper.retornaInfoProcedimento(AtivacaoVivo2BrocadeFunctions.class.getName(), vivo2IdDomain.getIdTbs(), "AtivacaoVivo2CiscoFunctions"));
		}
	}
	
	/**
	 * iniciaBuscaInterface
	 * @param hostnameEquipamento
	 */
	public void iniciaBuscaInterface(String hostnameEquipamento) {
		
		if(StringUtils.isBlank(vivo2IdDomain.getRdist()))
			return;
		
		/**
		 * Para os casos de Recife vamos utilizar os seguintes Po
			L-BR-PE-RCE-LN-SCR-01
			Po 11(t)
			Po 12(t)
			Po 13(t)
			Po 14(t)
		 */
		
		if (StringUtils.equalsIgnoreCase(hostnameEquipamento, "L-BR-PE-RCE-LN-SCR-01")) {
			
			String portNumbers[] = new String[] {"11", "12", "13", "14"};
			for (int c1 = 0; c1 < portNumbers.length; c1++) {
				String comando = "show interface port-channel " + portNumbers[c1];
				String retorno = redeIpFunctions.enviarComandoAvailableMore(comando, new String[] {hostnameEquipamento + "#", hostnameEquipamento + ">"}, new String[] {"-- more --", "--More--", "-- More --"}, SimpleTelnet.SPACE,
						RedeHelper.retornaInfoProcedimento(AtivacaoVivo2BrocadeFunctions.class.getName(), vivo2IdDomain.getIdTbs(), "iniciaBuscaInterface"));
			
				if (!StringUtils.containsIgnoreCase(retorno, vivo2IdDomain.getRdist()))
					continue;
				
				String description = StringHelper.searchLine(retorno, "Description");
				if(StringUtils.isBlank(description)) {
					description = StringHelper.searchLine(retorno, "description");
					if(StringUtils.isBlank(description))
						continue;
				}
				
				String bundleNumber = StringUtils.substringAfterLast(description, "BE");
				if(StringUtils.isBlank(bundleNumber)) {
					bundleNumber = StringUtils.substringAfterLast(description, "Bundle-Ether");
					if(StringUtils.isBlank(bundleNumber))
						continue;
				}
				
				if(StringUtils.contains(bundleNumber, " "))
					bundleNumber = StringUtils.substringBefore(bundleNumber, " ").trim();
				
				try {
					if (Integer.parseInt(bundleNumber) >= 0) {
						vivo2IdDomain.setInterfaceConexaoSwt("Bundle-Ether" + bundleNumber);
						return;
					}
				} catch (Exception e) {
					continue;
				}
			}
			
		} else {
			
			// ROTINA 1
			
			// BUSCANDO POR RDIST
			String rdistNumber = StringHelper.searchPattern(vivo2IdDomain.getRdist(), GlobalStrEnum.NUMBERS.toString());
			if(StringUtils.isNotBlank(rdistNumber)) {
				
				// pegando a interface de conexao
				String comando = "sh inter desc | in " + vivo2IdDomain.getRdist().toUpperCase();
				String retorno = redeIpFunctions.enviarComandoAvailableMore(comando, new String[] {hostnameEquipamento + "#", hostnameEquipamento + ">"}, new String[] {"-- more --", "--More--", "-- More --"}, SimpleTelnet.SPACE,
						RedeHelper.retornaInfoProcedimento(AtivacaoVivo2BrocadeFunctions.class.getName(), vivo2IdDomain.getIdTbs(), "iniciaBuscaInterface"));
				
				String[] linhasRetorno = retorno.split(GlobalStrEnum.BREAK_LINE.toString());
				for (int c1 = 0; c1 < linhasRetorno.length; c1++) {
					String linha = linhasRetorno[c1].trim();
				
					if (StringUtils.containsIgnoreCase(linha, "Po ")) {
						
						String portNumber = StringHelper.searchPattern(linha, GlobalStrEnum.NUMBERS.toString());
						if(StringUtils.isBlank(portNumber) 
								|| portNumber.length() != 3
								|| !StringUtils.startsWith(portNumber, rdistNumber))
							continue;
						
						comando = "show interface port-channel " + portNumber;
						retorno = redeIpFunctions.enviarComandoAvailableMore(comando, new String[] {hostnameEquipamento + "#", hostnameEquipamento + ">"}, new String[] {"-- more --", "--More--", "-- More --"}, SimpleTelnet.SPACE,
								RedeHelper.retornaInfoProcedimento(AtivacaoVivo2BrocadeFunctions.class.getName(), vivo2IdDomain.getIdTbs(), "iniciaBuscaInterface"));
						
						if(StringUtils.isBlank(retorno) || !StringUtils.containsIgnoreCase(retorno, "description"))
							continue;
						
						String description = StringHelper.searchLine(retorno, "Description");
						if(StringUtils.isBlank(description)) {
							description = StringHelper.searchLine(retorno, "description");
							if(StringUtils.isBlank(description))
								continue;
						}
						
						String bundleNumber = StringUtils.substringAfterLast(description, "BE");
						if(StringUtils.isBlank(bundleNumber)) {
							bundleNumber = StringUtils.substringAfterLast(description, "Bundle-Ether");
							if(StringUtils.isBlank(bundleNumber))
								continue;
						}
						
						if(StringUtils.contains(bundleNumber, " "))
							bundleNumber = StringUtils.substringBefore(bundleNumber, " ").trim();
						
						try {
							if (Integer.parseInt(bundleNumber) >= 0) {
								vivo2IdDomain.setInterfaceConexaoSwt("Bundle-Ether" + bundleNumber);
								return;
							}
						} catch (Exception e) {
							continue;
						}
					}
				}
			}
			
			// BUSCANDO POR 'RAI'
			String raiNumber = StringHelper.searchPattern(vivo2IdDomain.getBackbone(), GlobalStrEnum.NUMBERS.toString());
			if(StringUtils.isNotBlank(raiNumber)) {
				
				raiNumber = String.valueOf(Integer.parseInt(raiNumber));
				
				String comando = "sh inter desc | in " + vivo2IdDomain.getBackbone().toUpperCase();
				String retorno = redeIpFunctions.enviarComandoAvailableMore(comando, new String[] {hostnameEquipamento + "#", hostnameEquipamento + ">"}, new String[] {"-- more --", "--More--", "-- More --"}, SimpleTelnet.SPACE,
						RedeHelper.retornaInfoProcedimento(AtivacaoVivo2BrocadeFunctions.class.getName(), vivo2IdDomain.getIdTbs(), "iniciaBuscaInterface"));
				
				String[] linhasRetorno = retorno.split(GlobalStrEnum.BREAK_LINE.toString());
				for (int c1 = 0; c1 < linhasRetorno.length; c1++) {
					String linha = linhasRetorno[c1].trim();
				
					if (StringUtils.containsIgnoreCase(linha, "Po ")) {
						
						String portNumber = StringHelper.searchPattern(linha, GlobalStrEnum.NUMBERS.toString());
						if(StringUtils.isBlank(portNumber) 
								|| portNumber.length() != 3
								|| !StringUtils.startsWith(portNumber, raiNumber))
							continue;
						
						comando = "show interface port-channel " + portNumber;
						retorno = redeIpFunctions.enviarComandoAvailableMore(comando, new String[] {hostnameEquipamento + "#", hostnameEquipamento + ">"}, new String[] {"-- more --", "--More--", "-- More --"}, SimpleTelnet.SPACE,
								RedeHelper.retornaInfoProcedimento(AtivacaoVivo2BrocadeFunctions.class.getName(), vivo2IdDomain.getIdTbs(), "iniciaBuscaInterface"));
						
						if(StringUtils.isBlank(retorno) || !StringUtils.containsIgnoreCase(retorno, "description"))
							continue;
						
						String description = StringHelper.searchLine(retorno, "Description");
						if(StringUtils.isBlank(description)) {
							description = StringHelper.searchLine(retorno, "description");
							if(StringUtils.isBlank(description))
								continue;
						}
						
						String bundleNumber = StringUtils.substringAfterLast(description, "BE");
						if(StringUtils.isBlank(bundleNumber)) {
							bundleNumber = StringUtils.substringAfterLast(description, "Bundle-Ether");
							if(StringUtils.isBlank(bundleNumber))
								continue;
						}
						
						if(StringUtils.contains(bundleNumber, " "))
							bundleNumber = StringUtils.substringBefore(bundleNumber, " ").trim();
						
						try {
							if (Integer.parseInt(bundleNumber) >= 0) {
								vivo2IdDomain.setInterfaceConexaoSwt("Bundle-Ether" + bundleNumber);
								return;
							}
						} catch (Exception e) {
							continue;
						}
					}
				}
			}	
			
			// ROTINA 2
			
			// pegando a interface de conexao
			String comando = "show vlan " + vivo2IdDomain.getVlanRede();
			String retorno = redeIpFunctions.enviarComandoAvailableMore(comando, new String[] {hostnameEquipamento + "#", hostnameEquipamento + ">"}, new String[] {"-- more --", "--More--", "-- More --"}, SimpleTelnet.SPACE,
					RedeHelper.retornaInfoProcedimento(AtivacaoVivo2BrocadeFunctions.class.getName(), vivo2IdDomain.getIdTbs(), "iniciaBuscaInterface"));
			
			String[] linhasRetorno = retorno.split(GlobalStrEnum.BREAK_LINE.toString());
			for (int c1 = 0; c1 < linhasRetorno.length; c1++) {
				String linha = linhasRetorno[c1].trim();
			
				if (StringUtils.containsIgnoreCase(linha, "Po ")) {
					
					String portNumber = StringHelper.searchPattern(linha, GlobalStrEnum.NUMBERS.toString());
					if(StringUtils.isBlank(portNumber) 
							|| portNumber.length() != 3
							|| !StringUtils.startsWith(portNumber, rdistNumber))
						continue;
					
					comando = "show interface port-channel " + portNumber;
					retorno = redeIpFunctions.enviarComandoAvailableMore(comando, new String[] {hostnameEquipamento + "#", hostnameEquipamento + ">"}, new String[] {"-- more --", "--More--", "-- More --"}, SimpleTelnet.SPACE,
							RedeHelper.retornaInfoProcedimento(AtivacaoVivo2BrocadeFunctions.class.getName(), vivo2IdDomain.getIdTbs(), "iniciaBuscaInterface"));
					
					if(StringUtils.isBlank(retorno) || !StringUtils.containsIgnoreCase(retorno, "description"))
						continue;
					
					String description = StringHelper.searchLine(retorno, "Description");
					if(StringUtils.isBlank(description)) {
						description = StringHelper.searchLine(retorno, "description");
						if(StringUtils.isBlank(description))
							continue;
					}
					
					String bundleNumber = StringUtils.substringAfterLast(description, "BE");
					if(StringUtils.isBlank(bundleNumber)) {
						bundleNumber = StringUtils.substringAfterLast(description, "Bundle-Ether");
						if(StringUtils.isBlank(bundleNumber))
							continue;
					}
					
					if(StringUtils.contains(bundleNumber, " "))
						bundleNumber = StringUtils.substringBefore(bundleNumber, " ").trim();
					
					try {
						if (Integer.parseInt(bundleNumber) >= 0) {
							vivo2IdDomain.setInterfaceConexaoSwt("Bundle-Ether" + bundleNumber);
							return;
						}
					} catch (Exception e) {
						continue;
					}
				}
			}			
		}
		
		// Limpa Log
		redeIpFunctions.limpaLog(RedeHelper.retornaInfoProcedimento(AtivacaoVivo2BrocadeFunctions.class.getName(), 
				hostnameEquipamento, "iniciaBuscaInterface"));
		
		// Progress Bar
		vivo2IdDomain.setValorProgressBar("25");
		
	}
	
	/**
	 * getLogsVlan
	 * @param hostnameEquipamento
	 */
	public void getLogsVlan(String hostnameEquipamento) {
		
		if(StringUtils.isBlank(vivo2IdDomain.getVlanRede()))
			return;
		
		// comandos de validacao VLAN
		String comando = "show vlan " + vivo2IdDomain.getVlanRede();
		redeIpFunctions.enviarComandoAvailableMore(comando, new String[] {hostnameEquipamento + "#", hostnameEquipamento + ">"}, new String[] {"-- more --", "--More--", "-- More --"}, SimpleTelnet.SPACE,
				RedeHelper.retornaInfoProcedimento(AtivacaoVivo2BrocadeFunctions.class.getName(), vivo2IdDomain.getIdTbs(), "iniciaBuscaInterface"));
		
		comando = "show running-config int vlan " + vivo2IdDomain.getVlanRede();
		redeIpFunctions.enviarComandoAvailableMore(comando, new String[] {hostnameEquipamento + "#", hostnameEquipamento + ">"}, new String[] {"-- more --", "--More--", "-- More --"}, SimpleTelnet.SPACE,
				RedeHelper.retornaInfoProcedimento(AtivacaoVivo2BrocadeFunctions.class.getName(), vivo2IdDomain.getIdTbs(), "iniciaBuscaInterface"));
		
		// Limpa Log
		redeIpFunctions.limpaLog(RedeHelper.retornaInfoProcedimento(AtivacaoVivo2BrocadeFunctions.class.getName(), 
				hostnameEquipamento, "iniciaBuscaInterface"));
				
		// Progress Bar
		vivo2IdDomain.setValorProgressBar("25");
		
	}
	
	/**
	 * Metodos Gets and Sets 
	 */

	public RedeIpFunctions getRedeIpFunctions() {
		return redeIpFunctions;
	}

	public void setRedeIpFunctions(RedeIpFunctions redeIpFunctions) {
		this.redeIpFunctions = redeIpFunctions;
	}

	public Vivo2IdDomain getVivo2IdDomain() {
		return vivo2IdDomain;
	}

	public void setVivo2IdDomain(Vivo2IdDomain vivo2IdDomain) {
		this.vivo2IdDomain = vivo2IdDomain;
	}
	
}