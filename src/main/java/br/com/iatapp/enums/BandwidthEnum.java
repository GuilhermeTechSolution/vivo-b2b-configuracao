package br.com.iatapp.enums;

import java.math.BigDecimal;

import org.apache.commons.lang3.exception.ExceptionUtils;

import br.com.iatapp.logger.ExceptionLogger;

public enum BandwidthEnum {
	
	BITSEC(1, "bits/sec", 0),
	KBPS(1000, "Kbps", 1),
	MBPS(1000000, "Mbps", 2),
	GBPS(1000000000l, "Gbps", 3);
	
	private final long codigo;
	private final String strTipo;
	private int idVelocidadeLink;
	
	/**
	 * Construtores
	 */

	BandwidthEnum(long codigo, String tipo, int idVelocidadeLink) {
		this.codigo = codigo;
		this.strTipo = tipo;
		this.idVelocidadeLink = idVelocidadeLink;
	}		
	
	public long getCodigo() {
		return codigo;
	}
	
	public String getTipo() {
		return strTipo;
	}	
	
	public int getIdVelocidadeLink() {
		return idVelocidadeLink;
	}
	
	/**
	 * Métodos
	 */
	
	public static BandwidthEnum valueOf(int idVelocidadeLink) {
		
		if (idVelocidadeLink == KBPS.getIdVelocidadeLink()) {
			return KBPS;
		}
		
		if (idVelocidadeLink == MBPS.getIdVelocidadeLink()) {
			return MBPS;	
		}
		
		if (idVelocidadeLink == GBPS.getIdVelocidadeLink()) {
			return GBPS;	
		}
		
		return BITSEC;
	}

	public static long retornaBps(int idVelocidadeLink, int velocidadeLink) {
		
		if (idVelocidadeLink == KBPS.getIdVelocidadeLink()) {
			return (long)KBPS.convertToBps(velocidadeLink);
		}
		
		if (idVelocidadeLink == MBPS.getIdVelocidadeLink()) {
			return (long)MBPS.convertToBps(velocidadeLink);
		}
		
		if (idVelocidadeLink == GBPS.getIdVelocidadeLink()) {
			return (long)GBPS.convertToBps(velocidadeLink);
		}
		
		return 0;
	}
	
	public static long retornaKbps(int idVelocidadeLink, int velocidadeLink) {
		
		if (idVelocidadeLink == KBPS.getIdVelocidadeLink()) {
			return (long) velocidadeLink;
		}
		
		if (idVelocidadeLink == MBPS.getIdVelocidadeLink()) {			
			if(velocidadeLink <= 4) {
				return (long) velocidadeLink * 1024;
			} else {
				return (long) velocidadeLink * 1000;
			}
		}
		
		if (idVelocidadeLink == GBPS.getIdVelocidadeLink()) {
			return (long) velocidadeLink * 1000000;
		}
		
		return 0;
	}
	
	public static String retornaVelocidadeStr(int idVelocidadeLink, int velocidadeLink) {
		
		if (idVelocidadeLink == KBPS.getIdVelocidadeLink()) {
			return velocidadeLink + KBPS.strTipo;
		}
		
		if (idVelocidadeLink == MBPS.getIdVelocidadeLink()) {
			return velocidadeLink + MBPS.strTipo;
		}
		
		if (idVelocidadeLink == GBPS.getIdVelocidadeLink()) {
			return velocidadeLink + GBPS.strTipo;
		}
		
		return "";
	}
	
	public static String retornaVelocidadeReduzidoStr(int idVelocidadeLink, String velocidadeLink) {
		
		if (idVelocidadeLink == KBPS.getIdVelocidadeLink()) {
			return velocidadeLink + "K";
		}
		
		if (idVelocidadeLink == MBPS.getIdVelocidadeLink()) {
			return velocidadeLink + "M";
		}
		
		if (idVelocidadeLink == GBPS.getIdVelocidadeLink()) {
			return velocidadeLink + "G";
		}
		
		return "";
	}
	
	private double doubleTruncated(double valor) {
		
		String infoProcedimento = BandwidthEnum.class.getName() + "\nProcedimento: doubleTruncated(double valor)";
		
		try {
			Double toBeTruncated = new Double(valor);
			return (new BigDecimal(toBeTruncated).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());			
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
			return valor;
		}
	}
	
	private double doubleTruncated(double valor, int precision) {
		
		String infoProcedimento = BandwidthEnum.class.getName() + "\nProcedimento: doubleTruncated(double valor, int precision)";
		
		try {
			Double toBeTruncated = new Double(valor);
			return (new BigDecimal(toBeTruncated).setScale(precision, BigDecimal.ROUND_HALF_UP).doubleValue());			
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
			return valor;
		}
	}		
	
	public double convertToBps(double valor) {		
		return doubleTruncated(valor * getCodigo());
	}
	
	public double convertBpsTo(double valor) {		
		return doubleTruncated(valor / getCodigo());
	}
	
	public double convertToBps(double valor, int precision) {		
		return doubleTruncated(valor * getCodigo(), precision);
	}
	
	public double convertBpsTo(double valor, int precision) {		
		return doubleTruncated(valor / getCodigo(), precision);
	}
	
	public String strConvertToBps(double valor) {		
		Double doubleValue = new Double(doubleTruncated(valor * getCodigo()));
		return (doubleValue.toString() + " " + getTipo());
	}
	
	public String strConvertBpsTo(double valor) {
		Double doubleValue = new Double(doubleTruncated(valor / getCodigo()));
		return (doubleValue.toString() + " " + getTipo());
	}
	
	public String strConvertToBps(double valor, int precision) {		
		Double doubleValue = new Double(doubleTruncated(valor * getCodigo(), precision));
		return (doubleValue.toString() + " " + getTipo());
	}
	
	public String strConvertBpsTo(double valor, int precision) {
		Double doubleValue = new Double(doubleTruncated(valor / getCodigo(), precision));
		return (doubleValue.toString() + " " + getTipo());
	}
	
}
