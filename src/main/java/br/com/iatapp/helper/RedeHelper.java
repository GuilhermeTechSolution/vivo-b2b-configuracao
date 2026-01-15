package br.com.iatapp.helper;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.InetAddressValidator;

import br.com.iatapp.config.IatConstants;
import br.com.iatapp.enums.CodigoServidoresEnum;

public class RedeHelper {
	
	public static String retornaInfoProcedimento(String nomeClasse, String idVantive, String infoProcedimento) {
		return "<strong>Ambiente:</strong> " + (IatConstants.codigoServidor == CodigoServidoresEnum.SERVIDOR_PROD.getCodigoServidor() ? "Produção" : "Desenvolvimento Amazon") 
				+ "<br/><strong>Classe:</strong> " + nomeClasse 
				+ "<br/><strong>ID Vantive/ID Teste:</strong> " + idVantive
				+ "<br/><strong>Procedimento:</strong> " + infoProcedimento;
	}
		
	public static String retornaInfoProcedimento(String nomeClasse, String infoProcedimento) {
		return "<strong>Ambiente:</strong> " + (IatConstants.codigoServidor == CodigoServidoresEnum.SERVIDOR_PROD.getCodigoServidor() ? "Produção" : "Desenvolvimento Amazon") 
				+ "<br/><strong>Classe:</strong> " + nomeClasse 
				+ "<br/><strong>Procedimento:</strong> " + infoProcedimento;
	}
	
	public static boolean isValidIp(final String address) {
	    
		if (StringUtils.isBlank(address)) {
	        return false;
	    }
	    if (InetAddressValidator.getInstance().isValid(address) ) {
	        return true;
	    }
	    
	    return false;
	}
	
	/**
	 * Funcao para retornar Ip Wan Par
	 * @param ipWanImpar
	 * @return
	 */
	public static String retornaIpWanPar(String ipWanImpar) {
		
		String strIpv4 = ipWanImpar;
		int ipv4Increment = 0;
		
		String strIpv4Increment = strIpv4.substring(strIpv4.lastIndexOf('.') + 1);
		
		if(!StringUtils.isNumeric(strIpv4Increment))
			return null;

		// Incrementar o último octeto
		ipv4Increment = Integer.parseInt(strIpv4Increment) + 1;
		strIpv4Increment = strIpv4.substring(0, strIpv4.lastIndexOf('.')) + '.' + ipv4Increment;
	
		return strIpv4Increment;
	}
	
	public static String retornaIpv6WanPrimeiro(String ipv6WanImpar) {
		try {
			if(StringUtils.endsWith(ipv6WanImpar, ":"))
				return ipv6WanImpar + "1";
			
			String aux = StringUtils.substringAfterLast(ipv6WanImpar, ":");
			if(StringUtils.isBlank(aux))
				return ipv6WanImpar + "1";
		  
			int decimal = Integer.parseInt(aux, 16);
			decimal++;
			
			aux = StringUtils.substringBeforeLast(ipv6WanImpar, ":");
			String hex = Integer.toHexString(decimal);
			
			return aux + ":" + hex;
			
		} catch (Exception e) {
			return "";
		}
	}
	
	public static String retornaIpv6WanRemoto(String ipv6WanImpar) {
		try {
			if(StringUtils.endsWith(ipv6WanImpar, ":"))
				return ipv6WanImpar + "2";
			
			String aux = StringUtils.substringAfterLast(ipv6WanImpar, ":");
			if(StringUtils.isBlank(aux))
				return ipv6WanImpar + "2";
		  
			int decimal = Integer.parseInt(aux, 16);
			decimal = decimal + 2;
			
			aux = StringUtils.substringBeforeLast(ipv6WanImpar, ":");
			String hex = Integer.toHexString(decimal);
			
			return aux + ":" + hex;
			
		} catch (Exception e) {
			return "";
		}
	}
		
	public static boolean validaIpWanPe(String retorno, String ipWan) {
		
		if (StringUtils.isBlank(retorno) || StringUtils.isBlank(ipWan))
			return false;
		
		String strAux = StringHelper.removeComando(retorno);
		return StringUtils.containsIgnoreCase(strAux, ipWan);
	}
	
	public static String getMascaraInverse(String value) {
		
		switch (value) {
			case "255.255.0.0":
				return "16";
			case "255.255.240.0":
				return "20";
			case "255.255.248.0":
				return "21";
			case "255.255.252.0":
				return "22";
			case "255.255.254.0":
				return "23";
			case "255.255.255.0":
				return "24";
			case "255.255.255.128":
				return "25";
			case "255.255.255.192":
				return "26";
			case "255.255.255.224":
				return "27";
			case "255.255.255.240":
				return "28";
			case "255.255.255.248":
				return "29";
			case "255.255.255.252":
				return "30";
			case "255.255.255.254":
				return "31";
			case "255.255.255.255":
				return "32";
			default:
				break;
		}
		return "";
	}
	
	public static String getMascaraIp(String value) {
		
		switch (value) {
			case "16":
				return "255.255.0.0";
			case "20":
				return "255.255.240.0";
			case "21":
				return "255.255.248.0";
			case "22":
				return "255.255.252.0";
			case "23":
				return "255.255.254.0";
			case "24":
				return "255.255.255.0";
			case "25":
				return "255.255.255.128";
			case "26":
				return "255.255.255.192";
			case "27":
				return "255.255.255.224";
			case "28":
				return "255.255.255.240";
			case "29":
				return "255.255.255.248";
			case "30":
				return "255.255.255.252";
			case "31":
				return "255.255.255.254";
			case "32":
				return "255.255.255.255";
			default:
				break;
		}
		return "";
	}
	
}
