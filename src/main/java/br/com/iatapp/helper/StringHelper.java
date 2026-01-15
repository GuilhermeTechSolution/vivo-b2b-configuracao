package br.com.iatapp.helper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.com.iatapp.enums.GlobalStrEnum;
import br.com.iatapp.logger.ExceptionLogger;



/**
 * 
 * @author ottap
 *
 */
public class StringHelper {
	
	public static String removeCaracteresEspeciais(String cliente) {
		
		cliente = cliente.replaceAll("/", "");
		cliente = cliente.replaceAll("\\.", "");
		cliente = cliente.replaceAll("'", "");
		cliente = cliente.replaceAll("& ", "");
		cliente = cliente.replaceAll("&", "");
		cliente = cliente.replaceAll("\\+ ", "");
		cliente = cliente.replaceAll("\\+", "");
		cliente = cliente.replaceAll("- ", "");
		cliente = cliente.replaceAll("-", "");
		cliente = cliente.replaceAll(",", "");
		
		return Normalizer.normalize(cliente, Normalizer.Form.NFKD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
	}
	
	/**
	 * 
	 * @param texto
	 * @return
	 */
	public static String removerCodigoCores(String texto) {
		
		// caracter ESC
		// UNICODE = \u001B
		// HEX = \x1B
		// OCTAL = \033
		
		if(StringUtils.isNotBlank(texto)) {
			
			// 7-bit and 8-bit C1 ANSI sequences
			texto = texto.replaceAll("(?:\\x1B[@-Z\\\\-_]|[\\x80-\\x9A\\x9C-\\x9F]|(?:\\x1B\\[|\\x9B)[0-?]*[ -/]*[@-~])", "");
			//texto = texto.replaceAll("\u001B\\[[;\\d]*m", ""); // tratativa de cores
			texto = texto.replaceAll("(\\u001B\\[[\\d;]*[^\\d;])",""); // tratativa de cores (completa)
			texto = texto.replaceAll("(\\x1B\\[A|\\x1B\\[C|\\x1B\\[K|\\x1B\\[1D)", ""); // ANSI escape sequences -> (Ex: 'ESC[A' = '\033[A'  || 'ESC[A' = '\x1B[A')
			texto = texto.replaceAll("(\\x1B7|\\x1B\7|\\x1B8|\\x1B7\\x1B|\\x1B8\\x1B)", ""); // Para modem rad antigo
			texto = texto.replaceAll("(\u00ff)", ""); // Custom Extended ASCII Code
			texto = texto.replaceAll("(\\x07|\\x08|\\x1B)", ""); // código não imprimível
			texto = texto.replaceAll("(-\\\\\\|\\/)", ""); // Caracter loading "-\|/"para OLT Alcatel
			texto = texto.replaceAll("(-\\\\\\|)", ""); // Caracter loading "-\|/"para OLT Alcatel
			return texto;
		}
		return texto;
	}
	
	/**
	 * 
	 * @param texto
	 * @return
	 */
	public static String removerAcentuacao(String texto) {
		if(StringUtils.isNotBlank(texto))
			return Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
		
		return "";
	}
	
	// Substring com os separadores
	public static String substringBetweenWithSeparator(final String str, final String open, final String close, final int ordinalInit, final int ordinalFinal) {
		
		String infoProcedimento = StringHelper.class.getName() + "\nProcedimento: substringBetweenWithSeparator(...)";
		
		try {
			if (str == null || open == null || close == null) {
				return null;
			}
			final int start = StringUtils.ordinalIndexOf(str, open, ordinalInit);
			if (start != -1) {
				final int end =StringUtils.ordinalIndexOf(str, close, ordinalFinal);
				if (end != -1) {
					return str.substring(start, end + close.length());
				}
			}
			return null;
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
			return null;
		}
	}
	
	// Substring: buscando os separadores e depois pegando a linha inteira. Incluído as linhas dos separadores
	public static String substringBetweenWithSeparatorBreakLine(final String str, final String open, final String close, final int ordinalInit, final int ordinalFinal, final int extraIndex) {
		
		String infoProcedimento = StringHelper.class.getName() + "\nProcedimento: substringBetweenWithSeparatorBreakLine(...)";
		
		try {
			if (str == null || open == null || close == null) {
				return null;
			}
			final int start = StringUtils.ordinalIndexOf(str, open, ordinalInit);
			if (start != -1) {
				final int end = StringUtils.ordinalIndexOf(str, close, ordinalFinal);
				if (end != -1) {
					//int endBreakLine = StringUtils.indexOf(str.substring(end), System.lineSeparator());
					int endBreakLine = indexOf(str.substring(end), Pattern.compile("" + GlobalStrEnum.BREAK_LINE.toString() + ""));
					return str.substring(start, end + endBreakLine + System.lineSeparator().length() + extraIndex);
				}
			}
			return null;
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
			return null;
		}
	}
	
	// Substring: buscando os separadores e depois pegando a linha inteira
	public static String substringBetweenBreakLine(final String str, final String open, final String close, final int ordinalInit, final int ordinalFinal) {
		
		String infoProcedimento = StringHelper.class.getName() + "\nProcedimento: substringBetweenBreakLine(...)";
		
		try {
			if (str == null || open == null || close == null) {
				return null;
			}
			final int start = StringUtils.ordinalIndexOf(str, open, ordinalInit);
			if (start != -1) {
				//int startBreakLine = StringUtils.indexOf(str.substring(start), System.lineSeparator());
				int startBreakLine = indexOf(str.substring(start), Pattern.compile("" + GlobalStrEnum.BREAK_LINE.toString() + ""));
				final int end = close.equals(GlobalStrEnum.END_OF_STRING.toString()) ? str.length() - 1 : StringUtils.ordinalIndexOf(str, close, ordinalFinal);
				if (end != -1) {
					return str.substring(start + startBreakLine, end);
				}
			}
			return null;
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
			return null;
		}
	}
	
	public static String substringAfterIgnoreCase(final String str, final String separator) {
		
		String infoProcedimento = StringHelper.class.getName() + "\nProcedimento: substringAfterIgnoreCase()";
		
		try {
			if (StringUtils.isEmpty(str)) {
				return "";
			}
			if (separator == null) {
				return "";
			}
			final int pos = StringUtils.indexOfIgnoreCase(str, separator);
			if (pos == -1) {
				return "";
			}
			return str.substring(pos + separator.length());
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
			return "";
		}
	}
	
	// Substring: Retorna primeira linha da string
	public static String KeepFirstLine(final String str) {
		
		String infoProcedimento = StringHelper.class.getName() + "\nProcedimento: KeepFirstLine(...)";
		
		try {
			
			String[] linhas = str.split("" + GlobalStrEnum.BREAK_LINE.toString() + "");
			if (linhas.length > 0) {
				String linhaAux = null;
				for (int c = 0; c < linhas.length - 1; c++) {
					linhaAux = linhas[c];
					if (linhaAux.length() > 0)
						return linhaAux;
				}
			}
			return str;
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
			return str;
		}
	}
	
	// Incluir string entre os separadores
	public static String includeStrBetweenSeparator(final String strMain, final String strInclude, final String open, final String close, final int ordinalInit, final int ordinalFinal) {
		
		String infoProcedimento = StringHelper.class.getName() + "\nProcedimento: includeStrBetweenSeparator(...)";
		
		try {
			final int end =StringUtils.ordinalIndexOf(strMain, close, ordinalFinal);
			if (end != -1 && end != 0) {
				
				String strAux = strMain.substring(0, end);
				StringBuilder strBuilder = new StringBuilder();
				strBuilder.append(strAux);
				strBuilder.append(strInclude);
				strBuilder.append(System.lineSeparator());
				strAux = strMain.substring(end);
				strBuilder.append(strAux);
				
				return strBuilder.toString();
			}
			return strMain;
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
			return strMain;
		}
	}
	
	// Retorna a linha da string onde contém o separador
	public static String searchLine(String str, String separator) {
		
		String infoProcedimento = StringHelper.class.getName() + "\nProcedimento: searchLine(String str, String separator)";
		
		try {
			String linhasRetorno[] = str.split("" + GlobalStrEnum.BREAK_LINE.toString() + "");
			for (int c = 0; c < linhasRetorno.length; c++) {
				
				String linhaAux = linhasRetorno[c].trim();
				if(linhaAux != null && StringUtils.containsIgnoreCase(linhaAux, separator)) {
					return linhaAux;
				}
			}
			return null;
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
			return null;
		}
	}
	
	 // Retorna a linha da string onde contém o separador
	public static String searchLineWithRegex(String str, String regex) {

		String infoProcedimento = StringHelper.class.getName() + "\nProcedimento: searchLineWithRegex(String str, String regex)";
	
		try {
			String linhasRetorno[] = str.split("" + GlobalStrEnum.BREAK_LINE.toString() + "");
			for (int c = 0; c < linhasRetorno.length; c++) {
	
				String linhaAux = linhasRetorno[c].trim();
				if(linhaAux != null && StringUtils.isNotBlank(searchPattern(linhaAux, regex))) {
					return linhaAux;
				}
			}
			return "";
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
			return "";
		}
	}
	
	// Retorna todas as linhas com a ocorrencia
	public static String[] searchLines(String str, String regex) {
		
		String infoProcedimento = StringHelper.class.getName() + "\nProcedimento: searchLineWithRegex(String str, String regex)";
		List<String> listaLinhas = new ArrayList<String>();
		try {
			String linhasRetorno[] = str.split("" + GlobalStrEnum.BREAK_LINE.toString() + "");
			int index = 0;
			for (int c = 0; c < linhasRetorno.length; c++) {	
				String linhaAux = linhasRetorno[c].trim();
				if(linhaAux != null && StringUtils.isNotBlank(searchPattern(linhaAux, regex))) {
					listaLinhas.add(index, linhaAux);
					index++;
				}
			}
			return convertListStringToArrayString(listaLinhas);
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
			return convertListStringToArrayString(listaLinhas);
		}
	}
	
	// convertListStringToArrayString
    public static String[] convertListStringToArrayString(List<String> listaLinhas) {
    	Object[] objArr = listaLinhas.toArray(); 
        String[] arrayStr = Arrays.copyOf(objArr, objArr.length, String[].class);  
        return arrayStr; 
    }
	
	// Retorna a linha da string onde contém o separador, sem trim
	public static String searchLineWithoutTrim(String str, String separator) {
		
		String infoProcedimento = StringHelper.class.getName() + "\nProcedimento: searchLineWithoutTrim(String str, String separator)";
		
		try {
			String linhasRetorno[] = str.split("" + GlobalStrEnum.BREAK_LINE.toString() + "");
			for (int c = 0; c < linhasRetorno.length - 1; c++) {
				
				String linhaAux = linhasRetorno[c];
				if(linhaAux != null && StringUtils.containsIgnoreCase(linhaAux, separator)) {
					return linhaAux;
				}
			}
			return null;
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
			return null;
		}
	}
	
	// Retorna a linha da string onde contém o separador
	public static String searchLineAny(String str, String[] separator) {
		
		String infoProcedimento = StringHelper.class.getName() + "\nProcedimento: searchLineAny(String str, String[] separator)";
		
		// Tudo minúsculo
		for (int c = 0; c < separator.length; c++) {
			String strAux = separator[c].trim();
			if(strAux != null) {
				separator[c] = strAux.toLowerCase();
			}
		}
		
		try {
			String linhasRetorno[] = str.split("" + GlobalStrEnum.BREAK_LINE.toString() + "");
			for (int c = 0; c < linhasRetorno.length - 1; c++) {
				
				String linhaAux = linhasRetorno[c].trim();
				if(linhaAux != null && StringUtils.containsAny(linhaAux.toLowerCase(), separator)) {
					return linhaAux;
				}
			}
			return null;
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
			return null;
		}
	}
	
	// Retorna a linha da string onde contém todos os separadores
	public static String searchLineAll(String str, String[] separator) {
		
		String infoProcedimento = StringHelper.class.getName() + "\nProcedimento: searchLineAll(String str, String[] separator)";
		
		// Tudo minúsculo
		for (int c = 0; c < separator.length; c++) {
			String strAux = separator[c].trim();
			if(strAux != null) {
				separator[c] = strAux.toLowerCase();
			}
		}
		
		try {
			String linhasRetorno[] = str.split("" + GlobalStrEnum.BREAK_LINE.toString() + "");
			for (int c = 0; c < linhasRetorno.length - 1; c++) {
				
				String linhaAux = linhasRetorno[c].trim();
				if(linhaAux != null && containsAll(linhaAux.toLowerCase(), separator)) {
					return linhaAux;
				}
			}
			return null;
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
			return null;
		}
	}
	
	// Verifica todas as ocorrências
	public static boolean containsAll(CharSequence cs, CharSequence... searchCharSequences) {
		
		String infoProcedimento = StringHelper.class.getName() + "\nProcedimento: containsAll(CharSequence cs, CharSequence... searchCharSequences)";
		
		try {
			if (StringUtils.isEmpty(cs) || ArrayUtils.isEmpty(searchCharSequences)) {
				return false;
			}
			for (CharSequence searchCharSequence : searchCharSequences) {
				if (!StringUtils.contains(cs, searchCharSequence)) {
					return false;
				}
			}
			return true;
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
			return false;
		}
	}
	
	// Retorna a linha da string onde contém o separador e por ordinalIndexOf
	public static String searchLineWithOccurance(String str, String separator, int occurance) {
		
		String infoProcedimento = StringHelper.class.getName() + "\nProcedimento: searchLineWithIndex(String str, String separator, int index)";
		
		try {
			
			int quantity = StringUtils.countMatches(str.toLowerCase(), separator);
			int count = 0;
			
			String linhasRetorno[] = str.split("" + GlobalStrEnum.BREAK_LINE.toString() + "");
			for (int c = 0; c < linhasRetorno.length - 1; c++) {
				
				String linhaAux = linhasRetorno[c].trim();
				if(linhaAux != null && StringUtils.containsIgnoreCase(linhaAux, separator)) {
					
					// Verifica quantas ocorrencias possui a linha
					count += StringUtils.countMatches(linhaAux.toLowerCase(), separator);
					
					// Se é -1, quer dizer que é a última ocorrência do separador
					if ((occurance == -1) && (count == quantity)) {
						return linhaAux;
					} else if (occurance != -1 && count >= occurance) { // Pode ser que na mesma linha tenha mais de uma ocorrência, por isso '>='
						return linhaAux;
					}
				}
			}
			return null;
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
			return null;
		}
	}
	
	// IndexOf com regex
	public static int indexOf(String s, Pattern pattern) {
		
		String infoProcedimento = StringHelper.class.getName() + "\nProcedimento: indexOf(String s, Pattern pattern)";
		
		try {
			Matcher matcher = pattern.matcher(s);
			return matcher.find() ? matcher.start() : -1;
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
			return -1;
		}
	}
	
	// Reading file into a string
	public static String readFileToString(String path, String file) {
		try {
			return new String(Files.readAllBytes(Paths.get(path, file)));
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	// Encontra a primeira ocorrência
	public static String searchPattern(String str, String regex) {
		
		String infoProcedimento = StringHelper.class.getName() + "\nProcedimento: searchPattern(String str, String regex)";
		
		try {
			if (StringUtils.isBlank(str))
				return "";
			
			Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(str);
		    if(matcher.find()) {
		    	return matcher.group();
		    }
		    return "";
		    
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
			return "";
		}
	}
	
	// Encontra a primeira ocorrência
	public static String searchPatternCaseSensitive(String str, String regex) {
		
		String infoProcedimento = StringHelper.class.getName() + "\nProcedimento: searchPatternCaseSensitive(String str, String regex)";
		
		try {
			if (StringUtils.isBlank(str))
				return "";
			
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(str);
		    if(matcher.find()) {
		    	return matcher.group();
		    }
		    return "";
		    
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
			return "";
		}
	}
	
	// Retorna todas as ocorrências com separador ( usar split )
	public static String searchPattern(String str, String regex, String separator) {
		
		String infoProcedimento = StringHelper.class.getName() + "\nProcedimento: searchPattern(String str, String regex, String separator)";
		
		try {
			Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(str);
			StringBuilder strBuilder = new StringBuilder();
		    while(matcher.find()) {
		    	strBuilder.append(matcher.group());
		    	strBuilder.append(separator);
		    }
		    return StringUtils.removeEnd(strBuilder.toString(), separator);
		    
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
			return "";
		}
	}
	
	/* 
	 * Conta quantas ocorrências de uma string pattern regex
	 * */
	public static int countMatchesRegex(String str, String regex) {
		
		String infoProcedimento = StringHelper.class.getName() + "\nProcedimento: countMatchesRegex";
		
		if (StringUtils.isBlank(str))
			return 0;
		
		try {
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(str);
			int count = 0;
			while(matcher.find())
				count++;
			return count;
		    
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
			return 0;
		}
	}
	
	/* 
	 * Verifica se a string pattern regex está no início da linha
	 * */
	public static boolean startsWithRegex(String str, String regex) {
		
		String infoProcedimento = StringHelper.class.getName() + "\nProcedimento: startsWithRegex";
		
		try {
			Pattern pattern = Pattern.compile("^" + regex);
			Matcher matcher = pattern.matcher(str);
			return matcher.find();
		    
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
			return false;
		}
	}	
	
	// Remove comando
	public static String removeComando(String str) {
		
		String infoProcedimento = StringHelper.class.getName() + "\nProcedimento: removeComando";
		
		if (StringUtils.isBlank(str))
			return "";
		
		try {
			String[] linhasRetorno = str.split("" + GlobalStrEnum.BREAK_LINE.toString() + "");
			if (linhasRetorno == null)
				return null;
			if (linhasRetorno.length <= 1 )
				return str;
			
			// Remover linhas em branco antes do comando 
			int index = 0;
			for (int cont = 0; cont < linhasRetorno.length; cont++) {
				if (StringUtils.isBlank(linhasRetorno[cont]))
					index++;
				else
					break;
			}
			
			return StringUtils.join(linhasRetorno, "\n", index + 1, linhasRetorno.length);
		    
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
			return "";
		}
	}
	
	// Remove linha do limpa log
	public static String removeLimpaLog(String str) {
		
		String infoProcedimento = StringHelper.class.getName() + "\nProcedimento: removeLimpaLog";
		StringBuilder strFormatted = new StringBuilder();
		try {
			// Guardando a primeira linha no Log
			String[] pattern = {"~]$",":~$","#",">", ":/]$","~]$ ",":~$ ","# ","> ", ":/]$ "};
			String linhasRetorno[] = str.split("" + GlobalStrEnum.BREAK_LINE.toString() + "");
			List<String> lst = Arrays.asList(linhasRetorno);
			for (String linhaAux : lst)
			{
				boolean found = false;
				for(int c = 0; c < pattern.length; c++) {
					// Verifica se existe o pattern. Linha do limpalog
					if(linhaAux != null && linhaAux.endsWith(pattern[c])) {
						found = true;
						break;
					}
				}
				
				if (!found) {
					strFormatted.append(linhaAux);
					strFormatted.append(System.lineSeparator());
				}
			}
			
			return strFormatted.toString();
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
			return null;
		}
	}
	
	// Remove linha do retorno do comando
	public static String removeLine(String str, String regex) {
		
		String infoProcedimento = StringHelper.class.getName() + "\nProcedimento: removeLine";
		
		if (str == null || regex == null)
			return "";
		
		try {
			StringBuilderPlus sb = new StringBuilderPlus();
			String[] linhasRetorno = str.split("" + GlobalStrEnum.BREAK_LINE.toString() + "");
			for (int c = 0; c < linhasRetorno.length; c++) {
				String linhaAux = linhasRetorno[c];
				if (linhaAux != null && searchPatternCaseSensitive(linhaAux, regex).isEmpty()) {
					if (sb.toString().isEmpty())
						sb.append(linhaAux);
					sb.appendLine(linhaAux);
				}
			}
			if (sb.toString().isEmpty())
				return str;
			else
				return sb.toString();
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
			return str;
		}
	}
	
	// Formata o retorno do comando ping, deixando apenas a primeira linha do ping
	public static String formatKeepFirstLineOfPing(String str, String regex) {
		
		String infoProcedimento = StringHelper.class.getName() + "\nProcedimento: formatKeepFirstLineOfPing";
		
		if (str == null || regex == null)
			return "";
		
		try {
			StringBuilderPlus sb = new StringBuilderPlus();
			String[] linhasRetorno = str.split("" + GlobalStrEnum.BREAK_LINE.toString() + "");
			boolean flag = false;
			for (int c = 0; c < linhasRetorno.length; c++) {
				String linhaAux = linhasRetorno[c];
				if (linhaAux != null && !searchPatternCaseSensitive(linhaAux, regex).isEmpty() && !flag) {
					if (sb.toString().isEmpty()) {
						// Verifica se o ping completo está em uma única linha
						if (linhaAux.length() > 75)
							sb.append(linhaAux.substring(0, 70));
						else	
							sb.append(linhaAux);
					} else {
						// Verifica se o ping completo está em uma única linha
						if (linhaAux.length() > 75)
							sb.appendLine(linhaAux.substring(0, 70));
						else	
							sb.appendLine(linhaAux);
					}
					flag = true;
				} else if (linhaAux != null && searchPatternCaseSensitive(linhaAux, regex).isEmpty()) {
					if (sb.toString().isEmpty())
						sb.append(linhaAux);
					sb.appendLine(linhaAux);
				}
			}
			if (sb.toString().isEmpty())
				return str;
			else
				return sb.toString();
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
			return str;
		}
	}
	
	// removes all whitespaces and non-visible characters
	public static String removeBlankChar(String str) {
		return str.replaceAll("\\s+","");
	}
	
	// Remove multiple spaces to single space
	public static String multipleSpacestoSingleSpace(String str) {
		
		String infoProcedimento = StringHelper.class.getName() + "\nProcedimento: multipleSpacestoSingleSpace(String str)";
		
		/*			^_+ : any sequence of spaces at the beginning of the string
		Match and replace with $1, which captures the empty string
		_+$ : any sequence of spaces at the end of the string
		Match and replace with $1, which captures the empty string
		(_)+ : any sequence of spaces that matches none of the above, meaning it's in the middle
		Match and replace with, which captures a single space*/
		
		try {
			String regex = "^ +| +$|( )+";
			return  str.trim().replaceAll(regex, " ");
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
			return str;
		}
	}
	
	// Concatenar String value no HashMap
	public static String concatHashMapValue(Map<String, String> mapStr, String key, String value) {

		String infoProcedimento = StringHelper.class.getName() + "\nProcedimento: concatHashMapValue";
		
		try {
			
			String existing = mapStr.get(key);
			String extraContent = value;
			return existing == null ? extraContent : existing + "<br>" + extraContent;
			
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
			return "";
		}
	}
	
	// ASCII to HTML
	public static String asciiToHtml(String value) {
		
		String infoProcedimento = StringHelper.class.getName() + "\nProcedimento: asciiToHtml";
		
		try {
			
			if(StringUtils.isNotBlank(value)) {
				return StringUtils.replaceEach(value, new String[]{"&", "<", ">", "\"", "'", "/"}, new String[]{"&amp;", "&lt;", "&gt;", "&quot;", "&#x27;", "&#x2F;"});
			}
			
			return "";
			
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
			return "";
		}
	}
	
	// Return app url path
	public static String getAppUrl(HttpServletRequest request) {
		
		String reqUrl = request.getRequestURL().toString();
		String appPath = request.getContextPath().toString();
		return String.format("%s%s", StringUtils.substringBefore(reqUrl, appPath).trim(), appPath);
	}
	
	// Return resources url path
	public static String getResourcesUrl(HttpServletRequest request) {
		
		String reqUrl = request.getRequestURL().toString();
		String appPath = request.getContextPath().toString();
		return String.format("%s%s/resources", StringUtils.substringBefore(reqUrl, appPath).trim(), appPath);
	}
	
	// Return Line Separator
	public static String lineSeparator() {
		return System.lineSeparator() + " ";
	}
	
	public static String lineSeparatorNew() {
		return System.lineSeparator() + " " + System.lineSeparator();
	}
	
	public static boolean isInteger(String caracter) {
		try {
			Integer.parseInt(caracter);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static JSONObject mergeJSONObjects(JSONObject json1, JSONObject json2) {
		
		String infoProcedimento = StringHelper.class.getName() + "\nProcedimento: mergeJSONObjects(...)";
		
		JSONObject mergedJSON = new JSONObject();
		try {
			mergedJSON = new JSONObject(json1, JSONObject.getNames(json1));
			for (String key : JSONObject.getNames(json2)) {
				mergedJSON.put(key, json2.get(key));
			}
		} catch (JSONException e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
			return new JSONObject();
		}
		
		return mergedJSON;
	}
	
	public String generateToken() {
		String[] abc = null;
		String  token = ""; 
		abc = "abcdefghijklmnopqrstuvwxyz1234567890".split("");
		for(int i=0; i < 32; i++) {
			token += abc[(int) Math.floor(Math.random()*abc.length)];
		}
		return token; // 32 bit "hash"
	}
	
	public static String readTxtLineByLine(String filePath) {
		
		StringBuilder contentBuilder = new StringBuilder();
		try (Stream<String> stream = Files.lines( Paths.get(filePath), StandardCharsets.UTF_8))
		{
			stream.forEach(s -> contentBuilder.append(s).append("\n"));
		}
		catch (IOException e) {
			e.printStackTrace();
			return "";
		}
		return contentBuilder.toString();
	}
	
	public static String getMotivoTeste(String mensagemGeral, String separator) {
		
		String infoProcedimento = StringHelper.class.getName() + "\nProcedimento: getMotivoTeste: " + mensagemGeral;
		
		try {
			StringBuilder sb = new StringBuilder();
			if (StringUtils.isNotBlank(mensagemGeral)) {
				String[] linhasRetorno = mensagemGeral.split(separator);
				if (linhasRetorno != null && linhasRetorno.length >= 3) {
					for (int c = 0; c < linhasRetorno.length; c++) {
						String linhaAux = linhasRetorno[c].trim();
						if(StringUtils.containsIgnoreCase(linhaAux, "ID Atividade:"))
							break;
						sb.append(linhaAux);
					}
				}
			} else {
				return "";
			}
			return sb.toString();
			
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
			return "";
		}
	}
	
	// Remove comando antigo e insere um novo comando
	public static String addNewComand(String comando, String retorno) {
		
		String infoProcedimento = StringHelper.class.getName() + "\nProcedimento: addNewComand";
		
		if (StringUtils.isBlank(comando) || StringUtils.isBlank(retorno))
			return retorno;
		
		try {
			String[] linhasRetorno = retorno.split("" + GlobalStrEnum.BREAK_LINE.toString() + "");
			if (linhasRetorno == null)
				return retorno;
			if (linhasRetorno.length <= 1 )
				return retorno;
			
			// Remover linhas em branco antes do comando 
			int index = 0;
			for (int cont = 0; cont < linhasRetorno.length; cont++) {
				if (StringUtils.isBlank(linhasRetorno[cont]))
					index++;
				else
					break;
			}
			
			// Comando completo
			String strAux = StringUtils.join(linhasRetorno, "\n", index, linhasRetorno.length);
			linhasRetorno = strAux.split("" + GlobalStrEnum.BREAK_LINE.toString() + "");
			
			// substituir comando
			linhasRetorno[0] = comando;
			
			//strAux = StringUtils.join(linhasRetorno, "\n", 0, linhasRetorno.length);
			strAux = String.join("\n", linhasRetorno);
			
			return strAux;
			
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
			return "";
		}
	}
	
	public static boolean isJsonValid(String jsonData) {
		
		if (jsonData == null || jsonData.length() == 0)
			return false;
		
		try {
			new JSONObject(jsonData);
		} catch (JSONException e) {
			try {
				new JSONArray(jsonData);
			} catch (JSONException ex) {
				return false;
			}
		}
		
		return true;
	}
	
}
