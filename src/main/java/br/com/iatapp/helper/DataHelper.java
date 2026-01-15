package br.com.iatapp.helper;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import br.com.iatapp.logger.ExceptionLogger;

public class DataHelper {
	
	public static String convertDateToStrPTBR(Date dataUSA) {		
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		dateFormat.setTimeZone(TimeZone.getTimeZone("America/Fortaleza"));
		return dateFormat.format(dataUSA);
	}	
	
	/**
	 * 
	 * @param data1
	 * @param data2
	 * @return
	 */
	public static long calcularDirefencaEmSegundos(Date data1, Date data2) {		
		if(data1 == null || data2 == null) {
			return 0;
		}
		return (data1.getTime() - data2.getTime())/1000;
	}
	
	/**
	 * 
	 * @return
	 */
	public static String getPrimeiroDiaMesAtualUSA() {
		return new SimpleDateFormat("yyyy-MM").format(new Date()) + "-01";
	}
	
	/**
	 * Metodo que retorna o data formato americano
	 * @return
	 */
	public static String getDataAtualUSA() {
		return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
	}
	
	public static String getDataAtualPTBR() {
		return new SimpleDateFormat("dd/MM/yyyy").format(new Date());
	}
	
	public static String getDataHoraAtualUSA() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}
	
	/**
	 * Metodo que retorna o ano atual
	 * @return
	 */
	public static String getAnoAtual() {
		return new SimpleDateFormat("yyyy").format(new Date());
	}
	
	public static String getCurrentYear() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy"));
	}
	
	/**
	 * Metodo que retorna o nome do mes atual
	 * @return
	 */
	public static String getNomeMesAtualUSA() {
		String mesAtual = new SimpleDateFormat("MM").format(new Date());
		switch (mesAtual) {
		case "01":
			return "Janeiro";
		case "02":
			return "Fevereiro";
		case "03":
			return "Março";
		case "04":
			return "Abril";
		case "05":
			return "Maio";
		case "06":
			return "Junho";
		case "07":
			return "Julho";
		case "08":
			return "Agosto";
		case "09":
			return "Setembro";
		case "10":
			return "Outubro";
		case "11":
			return "Novembro";
		case "12":
			return "Dezembro";
		}
		return null;
	}
	
	public static long getLongDateTimeStamp() {
		return Long.parseLong(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
	}
	
	public static String getCurrentLocalDateTimeStamp() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
	}
	
	public static String getCurrentLocalDateStamp() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
	}
	
	public static String convertDataPTBRToUSA(String data) {
		if(data.length() >= 10) {
			return data.substring(6, 10) + "-" + data.substring(3, 5) + "-" + data.substring(0, 2);
		}
		return "0000-00-00";
	}
	
	public static String somaDiasNaDataPTBR(String data, int qtdDias) {
		try {
			String dataUSA = convertDataPTBRToUSA(data);
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			Date dataDt = formatter.parse(dataUSA + " 00:00:00");
			
			Calendar c = Calendar.getInstance();
	        c.setTime(dataDt);			
			c.add(Calendar.DATE, 1);
			dataDt = c.getTime();
			formatter = new SimpleDateFormat("yyyy-MM-dd");			
			return formatter.format(dataDt);
		} catch (Exception e) {}
			
		return "0000-00-00";
	}
	
	
	// Cálculo para verificar a diferença de 2 datas
	public static boolean lessThan24Hours(String dateStart, String dateStop) {
		
		String infoProcedimento = DataHelper.class.getName() + "<br/>Procedimento: lessThan24Hours";
		
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			Date d1 = null;
			Date d2 = null;
			
			d1 = format.parse(dateStart);
			d2 = format.parse(dateStop);
			
			// in milliseconds
			long diff = d2.getTime() - d1.getTime();
			long diffDays = diff / (24 * 60 * 60 * 1000);
			
			if (diffDays == 0)
				return true;
			
			return false;
			
/*			long diffSeconds = diff / 1000 % 60;
			long diffMinutes = diff / (60 * 1000) % 60;
			long diffHours = diff / (60 * 60 * 1000) % 24;
			System.out.print(diffDays + " days, ");
			System.out.print(diffHours + " hours, ");
			System.out.print(diffMinutes + " minutes, ");
			System.out.print(diffSeconds + " seconds.");*/
			
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
			return false;
		}
	}
	
	public static String verificaDiferencaHorarioQuedas(String strDateStart, String strDateStop) {
		
		String infoProcedimento = DataHelper.class.getName() + "<br/>Procedimento: verificaDiferencaHorarioQuedas";
		
		try {
			// Verifica o mês
			if (!StringUtils.equalsIgnoreCase(StringUtils.substringBefore(strDateStart, " "), 
					StringUtils.substringBefore(strDateStop, " ")))
				return "";
			
			// Dec 20 07:35:22
			String timeStart = StringHelper.searchPattern(strDateStart, GlobalStrEnum.NUMBERS.toString()).trim() + ":" + StringHelper.searchPattern(strDateStart, GlobalStrEnum.CLOCK_FORMAT.toString());
			String timeStop = StringHelper.searchPattern(strDateStart, GlobalStrEnum.NUMBERS.toString()).trim() + ":" + StringHelper.searchPattern(strDateStop, GlobalStrEnum.CLOCK_FORMAT.toString());
			
			// Diferença
			SimpleDateFormat format = new SimpleDateFormat("dd:HH:mm:ss");
			Date dateStart = format.parse(timeStart);
			Date dateStop = format.parse(timeStop);
			long diff = (dateStop.getTime() - dateStart.getTime());
			
			long diffDays = diff / (24 * 60 * 60 * 1000);
			long diffHours = diff / (60 * 60 * 1000) % 24;
			long diffMinutes = diff / (60 * 1000) % 60;
			
			if (diffDays == 0)
				return (String.format("%d hora(s), %d minuto(s)", diffHours, diffMinutes));
			else
				return (String.format("%d dia(s), %d hora(s) e %d minuto(s)", diffDays, diffHours, diffMinutes));
			
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
			return "";
		}
	}
	
	public static boolean verificaHistoricoLog(String strDateStart, String strDateStop, int days) {
		
		String infoProcedimento = DataHelper.class.getName() + "<br/>Procedimento: verificaHistoricoLog";
		
		try {
			// Verifica o mês
			if (!StringUtils.equalsIgnoreCase(StringUtils.substringBefore(strDateStart, " "), 
					StringUtils.substringBefore(strDateStop, " ")))
				return false;
			
			// Dec 20 07:35:22
			String timeStart = StringHelper.searchPattern(strDateStart, GlobalStrEnum.NUMBERS.toString()).trim() + ":" + StringHelper.searchPattern(strDateStart, GlobalStrEnum.CLOCK_FORMAT.toString());
			String timeStop = StringHelper.searchPattern(strDateStop, GlobalStrEnum.NUMBERS.toString()).trim() + ":" + StringHelper.searchPattern(strDateStop, GlobalStrEnum.CLOCK_FORMAT.toString());
			
			// Diferença
			SimpleDateFormat format = new SimpleDateFormat("dd:HH:mm:ss");
			Date dateStart = format.parse(timeStart);
			Date dateStop = format.parse(timeStop);
			long diff = (dateStop.getTime() - dateStart.getTime());
			
			long diffDays = diff / (24 * 60 * 60 * 1000);
			//long diffHours = diff / (60 * 60 * 1000) % 24;
			//long diffMinutes = diff / (60 * 1000) % 60;
			
			if (diffDays <= days)
				return true;
			else
				return false;
			
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
			return false;
		}
	}
}
