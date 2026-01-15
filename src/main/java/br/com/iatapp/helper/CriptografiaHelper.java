package br.com.iatapp.helper;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @author ottap
 *
 */
public class CriptografiaHelper {
	
	/**
	 * Metodo que realiza a conversão da String para MD5 
	 * @param texto
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static String converteStringToMD5(String texto) throws NoSuchAlgorithmException {
	    MessageDigest md = MessageDigest.getInstance("MD5");        
        BigInteger hash = new BigInteger(1, md.digest(texto.getBytes()));
        return String.format("%32x", hash);
    }
	
	/**
	 * Metodo que codifica a String Base64
	 * @param texto
	 * @return
	 */
	public static String base64Encode(String texto) {
		if(StringUtils.isBlank(texto)) {
			return "";
		}
		return Base64.getEncoder().encodeToString(texto.getBytes());
	}

	/**
	 * Metodo que decodifica a String Base64
	 * @param texto
	 * @return
	 */
	public static String base64Decode(String texto) {
		if(StringUtils.isBlank(texto)) {
			return "";
		}
		byte[] decodedBytes = Base64.getDecoder().decode(texto);		
	    return new String(decodedBytes);
	}
	
}
