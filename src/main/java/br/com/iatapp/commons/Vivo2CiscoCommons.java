package br.com.iatapp.commons;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONObject;

import br.com.iatapp.domain.Vivo2IdDomain;
import br.com.iatapp.domain.Vivo2ScriptDomain;
import br.com.iatapp.enums.GlobalStrEnum;
import br.com.iatapp.enums.JsonKeysEnum;
import br.com.iatapp.enums.ModelosEquipamentosEnum;
import br.com.iatapp.enums.ResultadosStatus;
import br.com.iatapp.helper.RedeHelper;
import br.com.iatapp.helper.StringHelper;
import br.com.iatapp.logger.ExceptionLogger;
import br.com.iatapp.model.RetornoModel;
import br.com.iatapp.rede.RedeIpFunctions;

public class Vivo2CiscoCommons {
	
	RedeIpFunctions redeIpFunctions;
	Vivo2IdDomain vivo2IdDomain;
	String hostnameEquipamento;
	
	public Vivo2CiscoCommons(RedeIpFunctions redeIpFunctions, Vivo2IdDomain vivo2IdDomain) {
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
		
		if(!vivo2IdDomain.getServico().equals("SIP")) {
			// Check IPV6 LAN
			retornoModel = checkIpv6Livre(vivo2IdDomain.getIpv6Lan(), vivo2IdDomain.getMascaraIpv6Lan(), "60");
			if(!retornoModel.isResultado()) {
				retornoModel.setRetorno("Error Check Ipv6 Lan");
				return retornoModel;
			}
			
			// Check IPV6 WAN
			retornoModel = checkIpv6Livre(vivo2IdDomain.getIpv6Wan(), vivo2IdDomain.getMascaraIpv6Wan(), "126");
			if(!retornoModel.isResultado()) {
				retornoModel.setRetorno("Error Check Ipv6 Wan");
				return retornoModel;
			}
		}	
		
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
			String comando = "";
			String retorno = "";
			
			// Check ipv4
			
			if(vivo2IdDomain.getServico().equals("SIP")) {
				switch (ModelosEquipamentosEnum.valueOf(vivo2IdDomain.getIdModeloEquipamento())) {
				
					case CISCO_XE:		
						comando = String.format("sh ip route vrf %s %s", vivo2IdDomain.getVrf(), ipv4);
						break;	
					case CISCO_XR:
						comando = String.format("sh route vrf %s %s", vivo2IdDomain.getVrf(), ipv4);
						break;				
					default:
						retornoModel = new RetornoModel();
						retornoModel.setCodigo(ResultadosStatus.FALHOU.getCodigo());
						retornoModel.setResultado(false);
						retornoModel.setRetorno("Modelo Equipamento não suportado");
						retornoModel.setLog(StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()));			
						return retornoModel;
				}
			} else {
				switch (ModelosEquipamentosEnum.valueOf(vivo2IdDomain.getIdModeloEquipamento())) {
				
					case CISCO_XE:		
					case CISCO_XR:				
						comando = "show route " + ipv4;
						break;				
					default:
						retornoModel = new RetornoModel();
						retornoModel.setCodigo(ResultadosStatus.FALHOU.getCodigo());
						retornoModel.setResultado(false);
						retornoModel.setRetorno("Modelo Equipamento não suportado");
						retornoModel.setLog(StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()));			
						return retornoModel;
				}
			}
			
			retorno = redeIpFunctions.enviarComandoAvailable(comando, new String[] {hostnameEquipamento + "#", hostnameEquipamento + ">"},
					RedeHelper.retornaInfoProcedimento(Vivo2CiscoCommons.class.getName(), vivo2IdDomain.getIdTbs(), "checkIpsLivres"));
			
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
			
			if(StringUtils.containsIgnoreCase(retorno, "% Network not in table")) {
				retornoModel = new RetornoModel();
				retornoModel.setCodigo(ResultadosStatus.OK.getCodigo());
				retornoModel.setResultado(true);
				retornoModel.setRetorno(comando);
				retornoModel.setLog(StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()));			
				return retornoModel;
			}
			
			if(StringUtils.containsIgnoreCase(retorno, "Routing entry for ")) {
				
				try {
					String linha = StringHelper.searchLine(retorno, "Routing entry for ");
					String mascara = StringUtils.substringAfterLast(linha, "/").trim();
					if(Integer.parseInt(mascara) > 24) {
						retornoModel = new RetornoModel();
						retornoModel.setCodigo(ResultadosStatus.FALHOU.getCodigo());
						retornoModel.setResultado(false);
						retornoModel.setRetorno(String.format("IP %s está duplicado", ipv4));
						retornoModel.setLog(StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()));			
						return retornoModel;
					}
					
				} catch (Exception e) {
					retornoModel = new RetornoModel();
					retornoModel.setCodigo(ResultadosStatus.FALHOU.getCodigo());
					retornoModel.setResultado(false);
					retornoModel.setRetorno("Erro");
					retornoModel.setLog(StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()));			
					return retornoModel;
				}
			}
			
			retornoModel = new RetornoModel();
			retornoModel.setCodigo(ResultadosStatus.OK.getCodigo());
			retornoModel.setResultado(true);
			retornoModel.setRetorno(comando);
			retornoModel.setLog(StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()));			
			return retornoModel;
			
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e),
					RedeHelper.retornaInfoProcedimento(Vivo2CiscoCommons.class.getName(), vivo2IdDomain.getIdTbs(), "checkIpsLivres"));
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
			String comando = "";
			String retorno = "";
			
			// Check ipv6 mascara1
			
			switch (ModelosEquipamentosEnum.valueOf(vivo2IdDomain.getIdModeloEquipamento())) {
				
				case CISCO_XE:
					comando = String.format("show route ipv6 unicast %s/%s", ipv6, mascara1);
					break;				
				case CISCO_XR:					
					comando = String.format("show route ipv6 unicast %s/%s", ipv6, mascara1);
					break;				
				default:
					retornoModel = new RetornoModel();
					retornoModel.setCodigo(ResultadosStatus.FALHOU.getCodigo());
					retornoModel.setResultado(false);
					retornoModel.setRetorno("Modelo Equipamento não suportado");
					retornoModel.setLog("");			
					return retornoModel;
			}
			
			retorno = redeIpFunctions.enviarComandoAvailable(comando, new String[] {hostnameEquipamento + "#", hostnameEquipamento + ">"},
					RedeHelper.retornaInfoProcedimento(Vivo2CiscoCommons.class.getName(), vivo2IdDomain.getIdTbs(), "checkIpsLivres"));
			
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
			
			if(!StringUtils.containsIgnoreCase(retorno, "% Network not in table")) {
				retornoModel = new RetornoModel();
				retornoModel.setCodigo(ResultadosStatus.FALHOU.getCodigo());
				retornoModel.setResultado(false);
				retornoModel.setRetorno(String.format("IP %s/%s está duplicado", ipv6, mascara1));
				retornoModel.setLog(StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()));			
				return retornoModel;
			}
			
			// Check ipv6 mascara2
			
			switch (ModelosEquipamentosEnum.valueOf(vivo2IdDomain.getIdModeloEquipamento())) {
				
				case CISCO_XE:
					comando = String.format("show route ipv6 unicast %s/%s", ipv6, mascara2);
					break;				
				case CISCO_XR:					
					comando = String.format("show route ipv6 unicast %s/%s", ipv6, mascara2);
					break;				
				default:
					retornoModel = new RetornoModel();
					retornoModel.setCodigo(ResultadosStatus.FALHOU.getCodigo());
					retornoModel.setResultado(false);
					retornoModel.setRetorno("Modelo Equipamento não suportado");
					retornoModel.setLog("");			
					return retornoModel;
			}
			
			retorno = redeIpFunctions.enviarComandoAvailable(comando, new String[] {hostnameEquipamento + "#", hostnameEquipamento + ">"},
					RedeHelper.retornaInfoProcedimento(Vivo2CiscoCommons.class.getName(), vivo2IdDomain.getIdTbs(), "checkIpsLivres"));
			
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
			
			if(!StringUtils.containsIgnoreCase(retorno, "% Network not in table")) {
				retornoModel = new RetornoModel();
				retornoModel.setCodigo(ResultadosStatus.FALHOU.getCodigo());
				retornoModel.setResultado(false);
				retornoModel.setRetorno(String.format("IP %s/%s está duplicado", ipv6, mascara2));
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
					RedeHelper.retornaInfoProcedimento(Vivo2CiscoCommons.class.getName(), vivo2IdDomain.getIdTbs(), "checkIpsLivres"));
			retornoModel = new RetornoModel();
			retornoModel.setCodigo(ResultadosStatus.FALHOU.getCodigo());
			retornoModel.setResultado(false);
			retornoModel.setRetorno("Erro");
			retornoModel.setLog("");			
			return retornoModel;
		}
	}
	
	/**
	 * checkIpv4Livre
	 * @return
	 */
	public RetornoModel checkVrfSipExiste() {
		
		RetornoModel retornoModel;
		
		try {
			// para produto SIP é necessário verificar se a VRF existe
				
			String cidade = vivo2IdDomain.getCidade().toUpperCase();
			String vrf = "";
			String comando = "";
			String retorno = "";
			boolean encontrouVrf = false;
			
			switch (ModelosEquipamentosEnum.valueOf(vivo2IdDomain.getIdModeloEquipamento())) {
			
				case CISCO_XE:
					comando = String.format("sh ip vrf | in EVOX");
					retorno = redeIpFunctions.enviarComandoAvailableMore(comando, new String[] {hostnameEquipamento + "#", hostnameEquipamento + ">", "(config)#"}, "-- more --", "",
							RedeHelper.retornaInfoProcedimento(Vivo2CiscoCommons.class.getName(), "", ""));
					if(StringUtils.isBlank(retorno)) {
						retornoModel = new RetornoModel();
						retornoModel.setCodigo(ResultadosStatus.FALHOU.getCodigo());
						retornoModel.setResultado(false);
						retornoModel.setRetorno("Error comando show vrf");
						retornoModel.setLog(StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()));
						return retornoModel;
					}
					
					redeIpFunctions.setLog(redeIpFunctions.getLog().append(retorno));
					
					retorno = StringHelper.removeComando(retorno);
					
					String[] linhasRetorno = retorno.split(GlobalStrEnum.BREAK_LINE.toString());
					for (int c1 = 0; c1 < linhasRetorno.length; c1++) {
						
						String linha = linhasRetorno[c1].trim();
						
						if(StringUtils.containsIgnoreCase(linha, "EVOX")) {
							vrf = StringUtils.substringBefore(linha, " ");
							
							// setando o nome da vrf
							vivo2IdDomain.setVrf(vrf);
							
							retornoModel = new RetornoModel();
							retornoModel.setCodigo(ResultadosStatus.OK.getCodigo());
							retornoModel.setResultado(true);
							retornoModel.setRetorno("Vrf Encontrada");
							retornoModel.setLog("");			
							return retornoModel;
						}
					}
					
					retornoModel = new RetornoModel();
					retornoModel.setCodigo(ResultadosStatus.FALHOU.getCodigo());
					retornoModel.setResultado(false);
					retornoModel.setRetorno("VRF do cliente não foi encontrada");
					retornoModel.setLog(StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()));
					return retornoModel;
					
				case CISCO_XR:					
					
					// primeira busca
					comando = String.format("sh vrf all | in EVOX");
					retorno = redeIpFunctions.enviarComandoAvailableMore(comando, new String[] {hostnameEquipamento + "#", hostnameEquipamento + ">", "(config)#"}, "-- more --", "",
							RedeHelper.retornaInfoProcedimento(Vivo2CiscoCommons.class.getName(), "", ""));
					if(StringUtils.isBlank(retorno)) {
						retornoModel = new RetornoModel();
						retornoModel.setCodigo(ResultadosStatus.FALHOU.getCodigo());
						retornoModel.setResultado(false);
						retornoModel.setRetorno("Error comando show vrf");
						retornoModel.setLog(StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()));
						return retornoModel;
					}
					
					redeIpFunctions.setLog(redeIpFunctions.getLog().append(retorno));
					
					retorno = StringHelper.removeComando(retorno);
					
					String linha = StringHelper.searchLine(retorno, "18881:");
					if(StringUtils.isBlank(linha)) 
						linha = StringHelper.searchLine(retorno, "10429:");
					
					if(StringUtils.isNotBlank(linha)) {
						
						vrf = StringUtils.substringBefore(linha.trim(), " ").trim();
						// setando o nome da vrf
						vivo2IdDomain.setVrf(vrf);
						
						retornoModel = new RetornoModel();
						retornoModel.setCodigo(ResultadosStatus.OK.getCodigo());
						retornoModel.setResultado(true);
						retornoModel.setRetorno("Vrf Encontrada");
						retornoModel.setLog("");			
						return retornoModel;
						
					}
					
					// segunda busca
					if(cidade.equals("JFA")
		                    || cidade.equals("IIG")
		                    || cidade.equals("DVL")
		                    || cidade.equals("VGA")
		                    || cidade.equals("BCA")) {
		                cidade = "BHE";
		            } else if(cidade.equals("NHO")
		                    || cidade.equals("SMA")
		                    || cidade.equals("PLT")
		                    || cidade.equals("IJI")
		                    || cidade.equals("LJO")
		                    || cidade.equals("ALG")
		                    || cidade.equals("AVA")
		                    || cidade.equals("BGE")
		                    || cidade.equals("BGV")
		                    || cidade.equals("CAN")
		                    || cidade.equals("CBM")
		                    || cidade.equals("CCR")
		                    || cidade.equals("CEN")
		                    || cidade.equals("CHN")
		                    || cidade.equals("CIO")
		                    || cidade.equals("CLB")
		                    || cidade.equals("CQU")
		                    || cidade.equals("CSL")
		                    || cidade.equals("DSR")
		                    || cidade.equals("EIO")
		                    || cidade.equals("ERE")
		                    || cidade.equals("EVA")
		                    || cidade.equals("FCA")
		                    || cidade.equals("FRL")
		                    || cidade.equals("GDO")
		                    || cidade.equals("GRD")
		                    || cidade.equals("GTI")
		                    || cidade.equals("GUB")
		                    || cidade.equals("IJH")
		                    || cidade.equals("IVI")
		                    || cidade.equals("KDK")
		                    || cidade.equals("LVH")
		                    || cidade.equals("MGO")
		                    || cidade.equals("NVP")
		                    || cidade.equals("OSR")
		                    || cidade.equals("PAE")
		                    || cidade.equals("PAS")
		                    || cidade.equals("PMM")
		                    || cidade.equals("RGR")
		                    || cidade.equals("SAN")
		                    || cidade.equals("SCR")
		                    || cidade.equals("SCS")
		                    || cidade.equals("SLE")
		                    || cidade.equals("SPG")
		                    || cidade.equals("SPS")
		                    || cidade.equals("SRD")
		                    || cidade.equals("SRO")
		                    || cidade.equals("TES")
		                    || cidade.equals("TMI")
		                    || cidade.equals("TQR")
		                    || cidade.equals("TRI")
		                    || cidade.equals("VAA")
		                    || cidade.equals("VAO")
		                    || cidade.equals("VNS")
		                    || cidade.equals("XNLA")) {
		                cidade = "PAE_2";
		            } else if(cidade.equals("CUA")) {
		                cidade = "SOO";
		            } else if(cidade.equals("MNI")) {
		                cidade = "GNA";
		            } else if(cidade.equals("SNO")) {
		                cidade = "CBA";
		            } else if(cidade.equals("URA")) {
		                cidade = "ULA";
		            } else if(cidade.equals("ACZ")
							|| cidade.equals("SMT")) {
		                cidade = "VTA";
		            }
					
					// busca de VRF
					// 5 Tentativas
					for(int index = 0; index < 5; index++) {
						
						switch (index) {
						case 0:
							vrf = "ACESSO_EVOX_" + cidade;
							break;
						case 1:
							vrf = "EVOX_ACESSO_" + cidade;
							break;
						case 2:
							vrf = "ACCESSO_EVOX_" + cidade;
							break;
						case 3:
							vrf = "EVOX_ACCESSO_" + cidade;
							break;
						case 4:
							vrf = "EVOX";
							break;
						default:
							break;
						}
						
						comando = String.format("show vrf %s", vrf);
						retorno = redeIpFunctions.enviarComandoAvailableMore(comando, new String[] {hostnameEquipamento + "#", hostnameEquipamento + ">", "(config)#"}, "-- more --", "",
								RedeHelper.retornaInfoProcedimento(Vivo2CiscoCommons.class.getName(), "", ""));
						if(StringUtils.isBlank(retorno)) {
							retornoModel = new RetornoModel();
							retornoModel.setCodigo(ResultadosStatus.FALHOU.getCodigo());
							retornoModel.setResultado(false);
							retornoModel.setRetorno("Error comando show vrf");
							retornoModel.setLog(StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()));
							return retornoModel;
						}
						
						redeIpFunctions.setLog(redeIpFunctions.getLog().append(retorno));
						
						retorno = StringHelper.removeComando(retorno);
						if(StringUtils.containsIgnoreCase(retorno, vrf)) {
							encontrouVrf = true;
							break;
						}
					}
					
					if(!encontrouVrf) {
						retornoModel = new RetornoModel();
						retornoModel.setCodigo(ResultadosStatus.FALHOU.getCodigo());
						retornoModel.setResultado(false);
						retornoModel.setRetorno("VRF do cliente não foi encontrada");
						retornoModel.setLog(StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()));
						return retornoModel;
					}
					
					// setando o nome da vrf
					vivo2IdDomain.setVrf(vrf);
					
					retornoModel = new RetornoModel();
					retornoModel.setCodigo(ResultadosStatus.OK.getCodigo());
					retornoModel.setResultado(true);
					retornoModel.setRetorno("Vrf Encontrada");
					retornoModel.setLog("");			
					return retornoModel;
					
				default:
					retornoModel = new RetornoModel();
					retornoModel.setCodigo(ResultadosStatus.FALHOU.getCodigo());
					retornoModel.setResultado(false);
					retornoModel.setRetorno("Modelo Equipamento não suportado");
					retornoModel.setLog("");			
					return retornoModel;
			}
			
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e),
					RedeHelper.retornaInfoProcedimento(Vivo2CiscoCommons.class.getName(), vivo2IdDomain.getIdTbs(), "checkIpsLivres"));
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
	public RetornoModel checkVlans() {
		
		RetornoModel retornoModel;
		
		try {
			String comando = "";
			String retorno = "";
			
			// Check ipv4
			
			switch (ModelosEquipamentosEnum.valueOf(vivo2IdDomain.getIdModeloEquipamento())) {
				
				case CISCO_XE:
					comando = "show vlan " + vivo2IdDomain.getVlanRede();
					break;				
				case CISCO_XR:					
					comando = "show vlan " + vivo2IdDomain.getVlanRede();
					break;				
				default:
					retornoModel = new RetornoModel();
					retornoModel.setCodigo(ResultadosStatus.FALHOU.getCodigo());
					retornoModel.setResultado(false);
					retornoModel.setRetorno("Modelo Equipamento não suportado");
					retornoModel.setLog("");			
					return retornoModel;
			}
			
			retorno = redeIpFunctions.enviarComandoAvailable(comando, new String[] {hostnameEquipamento + "#", hostnameEquipamento + ">"},
					RedeHelper.retornaInfoProcedimento(Vivo2CiscoCommons.class.getName(), vivo2IdDomain.getIdTbs(), "checkVlans"));
			
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
			
			retornoModel = new RetornoModel();
			retornoModel.setCodigo(ResultadosStatus.OK.getCodigo());
			retornoModel.setResultado(true);
			retornoModel.setRetorno(comando);
			retornoModel.setLog(StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()));			
			return retornoModel;
			
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e),
					RedeHelper.retornaInfoProcedimento(Vivo2CiscoCommons.class.getName(), vivo2IdDomain.getIdTbs(), "checkVlans"));
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
			String comando = "";
			String retorno = "";
			String[] linhasRetorno = null;
			String aux = "";
			
			// verificando se encontrou inteface no Switch
			if(StringUtils.isNotBlank(vivo2IdDomain.getInterfaceConexaoSwt())) {
				comando = "show running-config interface " + vivo2IdDomain.getInterfaceConexaoSwt();
				retorno = redeIpFunctions.enviarComandoAvailable(comando, new String[] {hostnameEquipamento + "#", hostnameEquipamento + ">"},
						RedeHelper.retornaInfoProcedimento(Vivo2CiscoCommons.class.getName(), vivo2IdDomain.getIdTbs(), "checkInterfaceConfiguracao"));
				if(StringUtils.containsIgnoreCase(retorno, "description")) {
					
					aux = vivo2IdDomain.getInterfaceConexaoSwt() + "." + vivo2IdDomain.getVlanRede() + vivo2IdDomain.getVlanUsuario();
					jsonDados.put(JsonKeysEnum.VIVO_2_INTERFACE_CONFIGURACAO.getCodigo(), aux);
					
					// retorno ok
					retornoModel = new RetornoModel();
					retornoModel.setCodigo(ResultadosStatus.OK.getCodigo());
					retornoModel.setResultado(true);
					retornoModel.setRetorno(String.format("Interface encontrada %s", jsonDados.getString(JsonKeysEnum.VIVO_2_INTERFACE_CONFIGURACAO.getCodigo())));
					retornoModel.setLog(StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()));			
					return retornoModel;
				}
			}
			
			switch (ModelosEquipamentosEnum.valueOf(vivo2IdDomain.getIdModeloEquipamento())) {
				
				case CISCO_XE:
					comando = "sh interface description | in " + vivo2IdDomain.getVlanRede();
					retorno = redeIpFunctions.enviarComandoAvailable(comando, new String[] {hostnameEquipamento + "#", hostnameEquipamento + ">"},
							RedeHelper.retornaInfoProcedimento(Vivo2CiscoCommons.class.getName(), vivo2IdDomain.getIdTbs(), "checkInterfaceConfiguracao"));
					
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
					
					linhasRetorno = retorno.split(GlobalStrEnum.BREAK_LINE.toString());
					for (int c1 = 0; c1 < linhasRetorno.length; c1++) {
						
						String linha = linhasRetorno[c1].trim();
						
						if(StringUtils.containsIgnoreCase(linha, "." +  vivo2IdDomain.getVlanRede()) 
								&& StringUtils.countMatches(linha, "up") > 1) {
							
							if(StringUtils.isBlank(jsonDados.optString(JsonKeysEnum.VIVO_2_INTERFACE_CONFIGURACAO.getCodigo()))) {
								aux = StringUtils.substringBefore(linha, "." +  vivo2IdDomain.getVlanRede()).trim();
								jsonDados.put(JsonKeysEnum.VIVO_2_INTERFACE_CONFIGURACAO.getCodigo(), aux);
								continue;
							}
							
							aux = jsonDados.getString(JsonKeysEnum.VIVO_2_INTERFACE_CONFIGURACAO.getCodigo());
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
					aux = jsonDados.getString(JsonKeysEnum.VIVO_2_INTERFACE_CONFIGURACAO.getCodigo());
					if(aux.startsWith("GE"))
						aux = aux.replace("GE", "GigabitEthernet");
					else if(aux.startsWith("Ge"))
						aux = aux.replace("Ge", "GigabitEthernet");
					else if(aux.startsWith("Gi"))
						aux = aux.replace("Gi", "GigabitEthernet");
					
					if(StringUtils.containsIgnoreCase(retorno, "." + vivo2IdDomain.getVlanRede() + vivo2IdDomain.getVlanUsuario() + " ")) {
						
						String linha = StringHelper.searchLine(retorno, "." + vivo2IdDomain.getVlanRede() + vivo2IdDomain.getVlanUsuario() + " ");
						if(StringUtils.isBlank(linha) || !StringUtils.containsIgnoreCase(linha, "deleted")) {
							retornoModel = new RetornoModel();
							retornoModel.setCodigo(ResultadosStatus.FALHOU.getCodigo());
							retornoModel.setResultado(false);
							retornoModel.setRetorno(String.format("Já existe uma interface configurada com a Vlan de Usuário"));
							retornoModel.setLog(StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()));			
							return retornoModel;
						}
					}
					
					jsonDados.put(JsonKeysEnum.VIVO_2_INTERFACE_CONFIGURACAO.getCodigo(), aux + "." + vivo2IdDomain.getVlanRede() + vivo2IdDomain.getVlanUsuario());
					
					// retorno ok
					retornoModel = new RetornoModel();
					retornoModel.setCodigo(ResultadosStatus.OK.getCodigo());
					retornoModel.setResultado(true);
					retornoModel.setRetorno(String.format("Interface encontrada %s", jsonDados.getString(JsonKeysEnum.VIVO_2_INTERFACE_CONFIGURACAO.getCodigo())));
					retornoModel.setLog(StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()));			
					return retornoModel;					
					
				case CISCO_XR:					
					comando = "show running-config formal | include " + vivo2IdDomain.getVlanRede();
					retorno = redeIpFunctions.enviarComandoAvailable(comando, new String[] {hostnameEquipamento + "#", hostnameEquipamento + ">"},
							RedeHelper.retornaInfoProcedimento(Vivo2CiscoCommons.class.getName(), vivo2IdDomain.getIdTbs(), "checkInterfaceConfiguracao"));
					
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
					
					linhasRetorno = retorno.split(GlobalStrEnum.BREAK_LINE.toString());
					int bundleNumber = 0;
					for (int c1 = 0; c1 < linhasRetorno.length; c1++) {
						String linha = linhasRetorno[c1].trim();
						if(StringUtils.startsWithIgnoreCase(linha, "interface ") 
								&& StringUtils.containsIgnoreCase(linha, String.format(" dot1q %s ", vivo2IdDomain.getVlanRede()))
								&& StringUtils.containsIgnoreCase(linha, ".")
								&& !StringUtils.containsIgnoreCase(linha, " rewrite ")
								&& (StringUtils.containsIgnoreCase(linha, "Bundle-Ether") || StringUtils.containsIgnoreCase(linha, "BE"))) {
							
							if(StringUtils.isBlank(jsonDados.optString(JsonKeysEnum.VIVO_2_INTERFACE_CONFIGURACAO.getCodigo()))) {
								aux = StringUtils.substringAfter(linha, " ").trim();
								aux = StringUtils.substringBefore(aux, " ").trim();
								if(!aux.contains("."))
									continue;
								
								jsonDados.put(JsonKeysEnum.VIVO_2_INTERFACE_CONFIGURACAO.getCodigo(), StringUtils.substringBefore(aux, "."));
		
								bundleNumber = Integer.parseInt(StringUtils.substringAfter(aux, "."));
								
								continue;
							}
						}
					}
					
					// verificando se apareceu 
					if(StringUtils.isBlank(jsonDados.optString(JsonKeysEnum.VIVO_2_INTERFACE_CONFIGURACAO.getCodigo()))
							|| bundleNumber == 0) {
						
						// procurando outros tipos de interfaces
						for (int c1 = 0; c1 < linhasRetorno.length; c1++) {
							
							String linha = linhasRetorno[c1].trim();
							if(StringUtils.startsWithIgnoreCase(linha, "interface ") 
									&& StringUtils.containsIgnoreCase(linha, String.format(" dot1q %s ", vivo2IdDomain.getVlanRede()))
									&& StringUtils.containsIgnoreCase(linha, ".")) {
								
								aux = StringUtils.substringBetween(linha, " ", ".").trim();
								aux += "." + vivo2IdDomain.getVlanRede() + vivo2IdDomain.getVlanUsuario();
								if(StringUtils.containsIgnoreCase(retorno, aux)) {
									retornoModel = new RetornoModel();
									retornoModel.setCodigo(ResultadosStatus.FALHOU.getCodigo());
									retornoModel.setResultado(false);
									retornoModel.setRetorno(String.format("Já existe uma interface configurada com a Vlan de Usuário"));
									retornoModel.setLog(StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()));			
									return retornoModel;
								}
								
								jsonDados.put(JsonKeysEnum.VIVO_2_INTERFACE_CONFIGURACAO.getCodigo(), aux);
								
								// retorno ok
								retornoModel = new RetornoModel();
								retornoModel.setCodigo(ResultadosStatus.OK.getCodigo());
								retornoModel.setResultado(true);
								retornoModel.setRetorno(String.format("Interface encontrada %s", jsonDados.getString(JsonKeysEnum.VIVO_2_INTERFACE_CONFIGURACAO.getCodigo())));
								retornoModel.setLog(StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()));			
								return retornoModel;
							}
						}
						
						retornoModel = new RetornoModel();
						retornoModel.setCodigo(ResultadosStatus.FALHOU.getCodigo());
						retornoModel.setResultado(false);
						retornoModel.setRetorno(String.format("Interface não encontrada"));
						retornoModel.setLog(StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()));			
						return retornoModel;
					}
					
					// incrementando a interface Bundle
					bundleNumber++;
					aux = jsonDados.getString(JsonKeysEnum.VIVO_2_INTERFACE_CONFIGURACAO.getCodigo());
					jsonDados.put(JsonKeysEnum.VIVO_2_INTERFACE_CONFIGURACAO.getCodigo(), aux + "." + vivo2IdDomain.getVlanRede() + vivo2IdDomain.getVlanUsuario());
					
					// retorno ok
					retornoModel = new RetornoModel();
					retornoModel.setCodigo(ResultadosStatus.OK.getCodigo());
					retornoModel.setResultado(true);
					retornoModel.setRetorno(String.format("Interface encontrada %s", jsonDados.getString(JsonKeysEnum.VIVO_2_INTERFACE_CONFIGURACAO.getCodigo())));
					retornoModel.setLog(StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()));			
					return retornoModel;
					
				default:
					retornoModel = new RetornoModel();
					retornoModel.setCodigo(ResultadosStatus.FALHOU.getCodigo());
					retornoModel.setResultado(false);
					retornoModel.setRetorno("Modelo Equipamento não suportado");
					retornoModel.setLog("");			
					return retornoModel;
			}	
			
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e),
					RedeHelper.retornaInfoProcedimento(Vivo2CiscoCommons.class.getName(), vivo2IdDomain.getIdTbs(), "checkInterfaceConfiguracao"));
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
		String comando = String.format("configure terminal");
		String retorno = redeIpFunctions.enviarComandoAvailableMore(comando, new String[] {hostnameEquipamento + "#", hostnameEquipamento + ">", "(config)#"}, "-- more --", "",
				RedeHelper.retornaInfoProcedimento(Vivo2CiscoCommons.class.getName(), "", ""));
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
		if(!StringUtils.containsIgnoreCase(retorno, "(config)#")) {
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
				
				retorno = redeIpFunctions.enviarComandoAvailableMore(linhasComando[c], new String[] {hostnameEquipamento + "#", hostnameEquipamento + ">", ")#", "]:"}, "-- more --", "",
						RedeHelper.retornaInfoProcedimento(Vivo2CiscoCommons.class.getName(), "", ""));
				
				redeIpFunctions.getLog().append(StringUtils.isNotBlank(retorno) ? retorno : "");
				
				if (StringUtils.isBlank(retorno) 
						|| StringUtils.containsIgnoreCase(retorno, "Invalid input detected")
						|| StringUtils.containsIgnoreCase(retorno, "syntax error:")
						|| StringUtils.containsIgnoreCase(retorno, "Failed to commit")) {
					configuracaoFalhou = true;
					break;
				}
				
				if(StringUtils.contains(retorno, "]:")) {
					retorno = redeIpFunctions.enviarComandoAvailableMore("yes", new String[] {hostnameEquipamento + "#", hostnameEquipamento + ">", ")#", "]:"}, "-- more --", "",
							RedeHelper.retornaInfoProcedimento(Vivo2CiscoCommons.class.getName(), "", ""));
					
					redeIpFunctions.getLog().append(StringUtils.isNotBlank(retorno) ? retorno : "");
					
					if (StringUtils.isBlank(retorno) 
							|| StringUtils.containsIgnoreCase(retorno, "Invalid input detected")
							|| StringUtils.containsIgnoreCase(retorno, "syntax error:")
							|| StringUtils.containsIgnoreCase(retorno, "Failed to commit")) {
						
						
						retorno = redeIpFunctions.enviarComandoAvailableMore("clear", new String[] {hostnameEquipamento + "#", hostnameEquipamento + ">", ")#", "]:"}, "-- more --", "",
								RedeHelper.retornaInfoProcedimento(Vivo2CiscoCommons.class.getName(), "", ""));
						
						redeIpFunctions.getLog().append(StringUtils.isNotBlank(retorno) ? retorno : "");
					
						configuracaoFalhou = true;
						break;
					}
				}
			}
		}
		
		// sair do modo de configuracao
		
		comando = String.format("exit");
		retorno = redeIpFunctions.enviarComandoAvailableMore(comando, new String[] {hostnameEquipamento + "#", hostnameEquipamento + ">", ")#", "]:"}, "-- more --", "",
				RedeHelper.retornaInfoProcedimento(Vivo2CiscoCommons.class.getName(), "", ""));
		
		redeIpFunctions.getLog().append(StringUtils.isNotBlank(retorno) ? retorno : "");
		
		if (StringUtils.isBlank(retorno)) {
			retornoModel = new RetornoModel();
			retornoModel.setCodigo(ResultadosStatus.FALHOU.getCodigo());
			retornoModel.setResultado(false);
			retornoModel.setRetorno("Erro no procedimento de configuração");
			retornoModel.setLog(StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()));
			return retornoModel;
		}
		
		if(StringUtils.contains(retorno, "]:")) {
			retorno = redeIpFunctions.enviarComandoAvailableMore("yes", new String[] {hostnameEquipamento + "#", hostnameEquipamento + ">", ")#", "]:"}, "-- more --", "",
					RedeHelper.retornaInfoProcedimento(Vivo2CiscoCommons.class.getName(), "", ""));
			
			redeIpFunctions.getLog().append(StringUtils.isNotBlank(retorno) ? retorno : "");
			
			if (StringUtils.isBlank(retorno) 
					|| StringUtils.containsIgnoreCase(retorno, "Invalid input detected")
					|| StringUtils.containsIgnoreCase(retorno, "syntax error:")
					|| StringUtils.containsIgnoreCase(retorno, "Failed to commit")) {
				
				
				retorno = redeIpFunctions.enviarComandoAvailableMore("clear", new String[] {hostnameEquipamento + "#", hostnameEquipamento + ">", ")#", "]:"}, "-- more --", "",
						RedeHelper.retornaInfoProcedimento(Vivo2CiscoCommons.class.getName(), "", ""));
				
				redeIpFunctions.getLog().append(StringUtils.isNotBlank(retorno) ? retorno : "");
				
				retorno = redeIpFunctions.enviarComandoAvailableMore("exit", new String[] {hostnameEquipamento + "#", hostnameEquipamento + ">", ")#", "]:"}, "-- more --", "",
						RedeHelper.retornaInfoProcedimento(Vivo2CiscoCommons.class.getName(), "", ""));
				
				redeIpFunctions.getLog().append(StringUtils.isNotBlank(retorno) ? retorno : "");
				
				retornoModel = new RetornoModel();
				retornoModel.setCodigo(ResultadosStatus.FALHOU.getCodigo());
				retornoModel.setResultado(false);
				retornoModel.setRetorno("Erro no procedimento de configuração");
				retornoModel.setLog(StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()));
				return retornoModel;
			}
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

}
