package br.com.iatapp.rede;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.concurrent.Semaphore;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.net.telnet.TelnetClient;
import org.apache.commons.net.telnet.WindowSizeOptionHandler;

import br.com.iatapp.config.IatConstants;
import br.com.iatapp.enums.CodigoServidoresEnum;
import br.com.iatapp.enums.CodigoTimeoutEnum;
import br.com.iatapp.enums.GlobalStrEnum;
import br.com.iatapp.helper.StringHelper;
import br.com.iatapp.logger.ExceptionLogger;
import br.com.iatapp.model.RetornoModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * 
 * @author ottap
 *
 */

@Configuration
@ConfigurationProperties("application")
public class SimpleTelnet {
	
	/**
	 * Constants
	 */
	
	// DSLAM
	public static String DSLAM_USER = "root";
	public static String DSLAM_SENHA = "br-hu4we1";	
	public static String DSLAM_USER_SIGRES = "sigres";
	public static String DSLAM_SENHA_SIGRES = "sergis1";
	// OLT
	public static String SERVIDOR_OLT_IP = "10.18.81.96";
	public static String SERVIDOR_OLT_USER = "oper";
	public static String SERVIDOR_OLT_SENHA = "oper@ultr@";
	public static String OLT_ALCATEL_ANTIGA_USER = "s_rbcosp02";
	public static String OLT_ALCATEL_ANTIGA_SENHA = "DS@f272K";
	
	// Rede
	public static int TELNET_CONNECTION_TIMEOUT = 80000;
	public static int TELNET_DEFAULT_TIMEOUT = 80000;
	public static int TELNET_PORTA = 23;
	public static int PE_CONNECTION_TIMEOUT = 55000;
	public static int CUSTOM_TIMEOUT = 20000; // Tempo mínimo para conectar no equipamento
	
	// CRTLC
	public static final String CTRLC = "CTRLC";
	// SPACE
	public static final String SPACE = "SPACE";
	// BACKSPACE
	public static final String BACKSPACE = "BACKSPACE";
	// TAB
	public static final String TAB = "TAB";
	
	// NUM TENTATIVAS TELNET
	public static final int NUM_TENTATIVAS_TELNET = 5;
	public static final int NUM_TENTATIVAS_EXCEPTION = 3;
	public final int TELNET_SLEEP_3000 = 3000;
	public final int TELNET_SLEEP_2000 = 2000;
	public final int TELNET_SLEEP_1000 = 1000;
	public final int TELNET_SLEEP_500  = 500;
	public final int TELNET_SLEEP_150  = 150;
	public final int TELNET_SLEEP_100  = 100;
	public final int TELNET_SLEEP_50   = 50;
	public final int TELNET_SLEEP_5    = 5;
	
	/**
	 * Atributos da classe
	 */
	private TelnetClient telnet;
	private InputStream in;
	private PrintStream out;
	private StringBuilder logTotal;
	private StringBuilder logOnline;
	private Semaphore semaphore;
	private static String ipTelnetServer;
	private static String telnetUser;
	private static String telnetPassword;
	private boolean enviarCtrlC;
	private boolean enviouCtrlC;
	private boolean socBloqueouUsuario;

	@Autowired
	Environment env;

	@Value("${jumpserver.galvao.ip}")
	public void setIpTelnetServer(final String ipTelnetServer){
		SimpleTelnet.ipTelnetServer = ipTelnetServer;
	}

	@Value("${jumpserver.galvao.user}")
	public void setTelnetUser(final String telnetUser){
		SimpleTelnet.telnetUser = telnetUser;
	}

	@Value("${jumpserver.galvao.password}")
	public void setIpTelnetPassword(final String telnetPassword){
		SimpleTelnet.telnetPassword = telnetPassword;
	}


	/**
	 * Construtor
	 */
	public SimpleTelnet() {
		// inicianlizando o TELNET cliente
		inicializaTelnet();
	}
	
	private void inicializaTelnet() {
		this.telnet = new TelnetClient();
		this.logTotal = new StringBuilder();
		this.logOnline = new StringBuilder();
		this.semaphore = new Semaphore(1);
	}

	/**
	 * Metodos de Rede
	 */
	
	/**
     * Servidor AWS
     * @return
	 * @throws IOException 
	 * @throws SocketException 
     * @throws InterruptedException
     */	
	public boolean abrirSessaoTelnet() throws Exception {
		
		int contAux = 0;
		
		for(int tentativasException = 0; tentativasException < NUM_TENTATIVAS_EXCEPTION; tentativasException++) {
			
			try {
				
				String infoProcedimento = SimpleTelnet.class.getName() + "<br/>Procedimento: abrirSessaoTelnet";
		        //Logger loggerTrace = LoggerFactory.getLogger("ottap-trace");
		        
		        for (int cont = 1; cont <= NUM_TENTATIVAS_TELNET; cont++) {
			        //loggerTrace.trace(String.format("Init telnet to connect (%d)", cont));
			           
			    	telnet.setConnectTimeout(TELNET_CONNECTION_TIMEOUT);
			        telnet.setDefaultTimeout(TELNET_DEFAULT_TIMEOUT);
			        
//		        	TERM (vt100)
//		        	terminal width, characters (80)
//		        	terminal height, rows (24)
//		        	terminal width, pixels (640)
//		        	terminal height, pixels (480)
//		        
//		        	width  = 1280 (160)
//		        	height = 1440 (72)
//		        	height = 1920 (94)
			        
			        WindowSizeOptionHandler optionHandler = new WindowSizeOptionHandler(160, 94, true, true, true, true);
			        telnet.addOptionHandler(optionHandler);
			        
			        telnet.connect(ipTelnetServer, TELNET_PORTA);
	
			        // esta linha deve estar apos a criacao da conexao
			        telnet.setSoTimeout(TELNET_CONNECTION_TIMEOUT);
			        
			        //loggerTrace.trace("Telnet connected");
			        
			        in = telnet.getInputStream();
			        out = new PrintStream(telnet.getOutputStream());
			        
			        //loggerTrace.trace("Telnet loaded resources");
			        
			        logTotal.append(readUntilAvailable("ogin:", infoProcedimento));
			        //String userAux = IatConstants.codigoServidor == CodigoServidoresEnum.SERVIDOR_PROD.getCodigoServidor() ? IatConstants.USER_SERVER_GALVAO : IatConstants.LOCAL_USER;
					String userAux = telnetUser;
					//loggerTrace.trace("Telnet write: " + userAux);
			        write(userAux);
			        
			        logTotal.append(readUntilAvailable("assword:", infoProcedimento));
			        //String passAux = IatConstants.codigoServidor == CodigoServidoresEnum.SERVIDOR_PROD.getCodigoServidor() ? IatConstants.PASS_SERVER_GALVAO : IatConstants.LOCAL_SENHA;
					String passAux = telnetPassword;
					//loggerTrace.trace("Telnet write: " + passAux);
			        write(passAux);
			        
			        // Esperar 2s, pois ocorreram alguns casos no Ubuntu de não retornar as infomações de login do sistema operacional.
			        // Por isso não logava.
			        Thread.sleep(TELNET_SLEEP_2000);
			        logTotal.append(readUntilAvailable(new String[] {"~]$",":~$", ":/]$"}, infoProcedimento));
			        
			        //loggerTrace.trace("### Init LogTotal ###");
			        //loggerTrace.trace(logTotal.toString());
			        //loggerTrace.trace("### End LogTotal ###");
			        
			        if (!telnet.isConnected()) {
			        	//loggerTrace.trace("Telnet not connected");
			        	Thread.sleep(TELNET_SLEEP_2000);
			        	telnet = new TelnetClient();
			        	logTotal = new StringBuilder();
			        	continue;
			        }
			        
			        if (logTotal.toString().contains(":~$") || logTotal.toString().contains("~]$") || logTotal.toString().contains(":/]$")) {
			        	//loggerTrace.trace("### LogTotal OK ###");
			        	setLogTotal(new StringBuilder());
			            return true;
			        } else {
			        	//loggerTrace.trace("### LogTotal NOK ###");
			        	fecharSessaoTelnet();
			        	Thread.sleep(TELNET_SLEEP_2000);
			        	telnet = new TelnetClient();
			        	logTotal = new StringBuilder();
			        	continue;
			        }
		        }
		        
		        return false;
				
			} catch (Exception e) {
				
				contAux++;
				if(contAux == NUM_TENTATIVAS_EXCEPTION) {
					throw new Exception(e);
				}
			}
		
			// aguardando o tempo para proxima tentantiva
			Thread.sleep(TELNET_SLEEP_3000);
			
			// inicianlizando o TELNET cliente
			inicializaTelnet();
		}
		
		return false;
	} 
	
    /**
     * Fechar Sessao Telnet
     * @throws IOException
     */
    public void fecharSessaoTelnet() throws IOException {    	
    	if(telnet != null && telnet.isAvailable()) {
    		telnet.disconnect();
    	}
    } 
    
    /**
     * Servidor Amazon
     * @return
     */
    public boolean conectarMaquinaAmazon() {
    	
    	String infoProcedimento = SimpleTelnet.class.getName() + "<br/>Procedimento: conectarMaquinaAmazon";
    	
    	try {
        	String comando = "ssh -i /home/ottap/aws/ottap-tecnologia-ltda-privatekey.pem " + IatConstants.AWS_HOST;
        	String retorno = enviarComandoAvailableSemAppendLogTotal(comando, new String[] {"assword:","(yes/no)?", ":~$", "~]$", ":/]$", "0$", "Para confirmar a leitura da notificacao"}, infoProcedimento);
        	
            if (StringUtils.isBlank(retorno)) {
            	return false;
            }
            
        	if(retorno.contains("(yes/no)?")) {
        		retorno = enviarComandoAvailableSemAppendLogTotal("yes", new String[] {"assword:","(yes/no)?"}, infoProcedimento);
            	if (StringUtils.isBlank(retorno))
                	return false;
        	}
            
            if(retorno.contains(":~$") || retorno.contains("~]$") || retorno.contains(":/]$")) {
            	return true;
        	}
            
        	return false;
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
			return false;
		}
    }
    
    /**
     * conectarMaquinaSalto
     * @return
     */
    public boolean conectarMaquinaSalto() {
    	
    	String infoProcedimento = SimpleTelnet.class.getName() + "<br/>Procedimento: conectarMaquinaSalto";
    	
    	try {
    		
    		String comando = "";
    		String retorno = "";
    		String ipSalto = "";
        	String portaSsh = "";
    		String login = "";
        	String senha = "";
        	

    		ipSalto = IatConstants.SALTO_IP_TATUAPE;
    		portaSsh = IatConstants.SALTO_SSH_PORT_TATUAPE;
    		login = IatConstants.SALTO_USER_TATUAPE;
    		senha = IatConstants.SALTO_SENHA_TATUAPE;

    		
    		for(int c  = 0; c < 2; c++) {
    			// Logica para enviar Ctrl+C caso o equipamento nao responda
	        	setEnviarCtrlC(true);
	        	setEnviouCtrlC(false);
	        	
	        	// Criando a Thread de monitoracao
	        	Thread threadMonitora = new Thread(new Runnable() {
	    			@Override
	    			public void run() {
	    				try {
	    					Thread.sleep(30000);
	    					if(isEnviarCtrlC()) {
	    						setEnviouCtrlC(true);
	    						write((char)3);
	    					}
	    				} catch (InterruptedException e) {
	    				}
	    					
	    			}
	    		});
	        	threadMonitora.start();	        	
	        	
	        	comando = "ssh " + login + "@" + ipSalto + " -p " + portaSsh;
	        	retorno = enviarComandoAvailableSemAppendLogTotal(comando, new String[] {"assword:","(yes/no)?", ":~$", "~]$", ":/]$", "0$", "Para confirmar a leitura da notificacao"}, infoProcedimento);
	        
	        	
	        	setEnviarCtrlC(false);
	        	if(threadMonitora.isAlive()) {
	        		threadMonitora.interrupt();
	        	} 
	        	
	        	if(isEnviouCtrlC()) {
	        		continue;
	        	}
	        	
	        	if (StringUtils.isBlank(retorno)) {
	            	return false;
	            } else {
	            	break;
	            }
    		}
    		
        	if(StringUtils.containsIgnoreCase(retorno, "host key verification failed")) {
        		comando = "ssh -o UserKnownHostsFile=/dev/null " + login + "@" + ipSalto;
        		retorno = enviarComandoAvailableSemAppendLogTotal(comando, new String[] {"assword:","(yes/no)?", ":~$", "~]$", ":/]$", "0$", "Para confirmar a leitura da notificacao"}, infoProcedimento);
            	if (StringUtils.isBlank(retorno))
                	return false;
        	}
            
            if(retorno.contains("(yes/no)?")) {
        		retorno = enviarComandoAvailableSemAppendLogTotal("yes", new String[] {"assword:","(yes/no)?"}, infoProcedimento);
            	if (StringUtils.isBlank(retorno))
                	return false;
        	}
            
            if(retorno.contains("assword:")) {
            	retorno = null;
            	retorno = enviarComandoAvailableSemAppendLogTotal(senha, new String[] {":~$","~]$", ":/]$"}, infoProcedimento);
            	if (StringUtils.isBlank(retorno)) {
                	return false;
                } else if(retorno.contains(":~$") || retorno.contains("~]$") || retorno.contains(":/]$")) {
                	String retornoAux = StringHelper.removeComando(retorno);
                	if(StringUtils.containsIgnoreCase(retornoAux, "techsolutio"))
                		return true;
                	else
                		return false;
            	}
            }
	        	
	        return false;
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
			return false;
		}
    }
    
    public boolean conectarTatuape() {
    	String infoProcedimento = SimpleTelnet.class.getName() + "<br/>Procedimento: conectarRedeIp";
    	
    	try {
    		
    		String comando = "";
    		String retorno = "";
    		
    		String login = "ottap-techsolutio";
    		String senha = "n9LLs72o*Lod";
    		
    		for(int c  = 0; c < 3; c++) {
    			// Logica para enviar Ctrl+C caso o equipamento nao responda
	        	setEnviarCtrlC(true);
	        	setEnviouCtrlC(false);
	        	
	        	// Criando a Thread de monitoracao
	        	Thread threadMonitora = new Thread(new Runnable() {
	    			@Override
	    			public void run() {
	    				try {
	    					Thread.sleep(20000);
	    					if(isEnviarCtrlC()) {
	    						setEnviouCtrlC(true);
	    						write((char)3);
	    					}
	    				} catch (InterruptedException e) {
	    				}
	    					
	    			}
	    		});
	        	threadMonitora.start();
	        	
	    		comando = "ssh -l ottap-techsolutio 186.200.67.62";    		
	        	retorno = enviarComandoAvailable(comando, new String[] {"assword:","(yes/no)?", ":~$", "~]$", ":/]$", "0$", "Para confirmar a leitura da notificacao"}, infoProcedimento);
	        	
	        	setEnviarCtrlC(false);
	        	if(threadMonitora.isAlive()) {
	        		threadMonitora.interrupt();
	        	}  
	        	
	        	if(isEnviouCtrlC()) {
	        		continue;
	        	}
	        	
	        	if (StringUtils.isBlank(retorno)) {
	            	return false;
	            } else {
	            	break;
	            }
    		}	
            
        	if(StringUtils.containsIgnoreCase(retorno, "host key verification failed")) {
        		comando = "ssh -o UserKnownHostsFile=/dev/null " + login + "@" + IatConstants.REDE_IP;
        		retorno = enviarComandoAvailable(comando, new String[] {"assword:","(yes/no)?", ":~$", "~]$", ":/]$", "0$", "Para confirmar a leitura da notificacao"}, infoProcedimento);
            	if (StringUtils.isBlank(retorno))
                	return false;
        	}
        	
        	if(retorno.contains("(yes/no)?")) {
        		retorno = enviarComandoAvailable("yes", new String[] {"assword:","(yes/no)?", ":~$", "~]$", ":/]$", "0$", "Para confirmar a leitura da notificacao"}, infoProcedimento);
            	if (StringUtils.isBlank(retorno))
                	return false;
        	}
        	
        	// verificando bloqueio do SOC
        	if(retorno.contains("Para confirmar a leitura da notificacao")) {
        		
        		// setando a flag de bloqueio do usuario no SOC
        		setSocBloqueouUsuario(true);
        		
        		write((char)3);
        		retorno = readUntilAvailable(new String[] {":~$","~]$", ":/]$", "0$"}, infoProcedimento);
            	if (StringUtils.isBlank(retorno))
                	return false;
            	
        		logTotal.append(retorno);
    	    	setarLogOnline(retorno);
            	return false;
        	}
        	
            if(retorno.contains("assword:")) {
            	retorno = enviarComandoAvailable(senha,  new String[] {"assword:","(yes/no)?", ":~$", "~]$", ":/]$", "0$", "Para confirmar a leitura da notificacao"}, infoProcedimento);
            	if (StringUtils.isBlank(retorno))
                	return false;
            	
            	// verificando bloqueio do SOC
            	if(retorno.contains("Para confirmar a leitura da notificacao")) {
            		
            		// setando a flag de bloqueio do usuario no SOC
            		setSocBloqueouUsuario(true);
            		
            		write((char)3);
            		retorno = readUntilAvailable(new String[] {":~$","~]$", ":/]$", "0$"}, infoProcedimento);
                	if (StringUtils.isBlank(retorno))
                    	return false;
                	
            		logTotal.append(retorno);
        	    	setarLogOnline(retorno);
                	return false;
            	}
            	
        		logTotal.append(retorno);
    	    	setarLogOnline(retorno);
            	
                if(retorno.contains("~]$") || retorno.contains(":~$") || retorno.contains(":/]$"))
                	return true;
            	
                if(retorno.contains("assword:")) {
            		write((char)3);
            		retorno = readUntilAvailable(new String[] {":~$","~]$", ":/]$", "0$"}, infoProcedimento);
                	if (StringUtils.isBlank(retorno))
                    	return false;
                	
            		logTotal.append(retorno);
        	    	setarLogOnline(retorno);
                	return false;
            	}
            }
            
        	return false;
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
			return false;
		}
    }
    
    /**
     * Servidor Rede IP
     * @return
     */
    public boolean conectarRedeIp(String login, String senha) {
    	
    	conectarTatuape();
    	
    	String infoProcedimento = SimpleTelnet.class.getName() + "<br/>Procedimento: conectarRedeIp";
    	
    	try {
    		
    		String comando = "";
    		String retorno = "";
    		
    		for(int c  = 0; c < 3; c++) {
    			// Logica para enviar Ctrl+C caso o equipamento nao responda
	        	setEnviarCtrlC(true);
	        	setEnviouCtrlC(false);
	        	
	        	// Criando a Thread de monitoracao
	        	Thread threadMonitora = new Thread(new Runnable() {
	    			@Override
	    			public void run() {
	    				try {
	    					Thread.sleep(20000);
	    					if(isEnviarCtrlC()) {
	    						setEnviouCtrlC(true);
	    						write((char)3);
	    					}
	    				} catch (InterruptedException e) {
	    				}
	    					
	    			}
	    		});
	        	threadMonitora.start();
	        	
	    		comando = "ssh " + login + "@" + IatConstants.REDE_IP;    		
	        	retorno = enviarComandoAvailable(comando, new String[] {"assword:","(yes/no)?", ":~$", "~]$", ":/]$", "0$", "Para confirmar a leitura da notificacao"}, infoProcedimento);
	        	
	        	setEnviarCtrlC(false);
	        	if(threadMonitora.isAlive()) {
	        		threadMonitora.interrupt();
	        	}  
	        	
	        	if(isEnviouCtrlC()) {
	        		continue;
	        	}
	        	
	        	if (StringUtils.isBlank(retorno)) {
	            	return false;
	            } else {
	            	break;
	            }
    		}	
            
        	if(StringUtils.containsIgnoreCase(retorno, "host key verification failed")) {
        		comando = "ssh -o UserKnownHostsFile=/dev/null " + login + "@" + IatConstants.REDE_IP;
        		retorno = enviarComandoAvailable(comando, new String[] {"assword:","(yes/no)?", ":~$", "~]$", ":/]$", "0$", "Para confirmar a leitura da notificacao"}, infoProcedimento);
            	if (StringUtils.isBlank(retorno))
                	return false;
        	}
        	
        	if(retorno.contains("(yes/no)?")) {
        		retorno = enviarComandoAvailable("yes", new String[] {"assword:","(yes/no)?", ":~$", "~]$", ":/]$", "0$", "Para confirmar a leitura da notificacao"}, infoProcedimento);
            	if (StringUtils.isBlank(retorno))
                	return false;
        	}
        	
        	// verificando bloqueio do SOC
        	if(retorno.contains("Para confirmar a leitura da notificacao")) {
        		
        		// setando a flag de bloqueio do usuario no SOC
        		setSocBloqueouUsuario(true);
        		
        		write((char)3);
        		retorno = readUntilAvailable(new String[] {":~$","~]$", ":/]$", "0$"}, infoProcedimento);
            	if (StringUtils.isBlank(retorno))
                	return false;
            	
        		logTotal.append(retorno);
    	    	setarLogOnline(retorno);
            	return false;
        	}
        	
            if(retorno.contains("assword:")) {
            	retorno = enviarComandoAvailable(senha,  new String[] {"assword:","(yes/no)?", ":~$", "~]$", ":/]$", "0$", "Para confirmar a leitura da notificacao"}, infoProcedimento);
            	if (StringUtils.isBlank(retorno))
                	return false;
            	
            	// verificando bloqueio do SOC
            	if(retorno.contains("Para confirmar a leitura da notificacao")) {
            		
            		// setando a flag de bloqueio do usuario no SOC
            		setSocBloqueouUsuario(true);
            		
            		write((char)3);
            		retorno = readUntilAvailable(new String[] {":~$","~]$", ":/]$", "0$"}, infoProcedimento);
                	if (StringUtils.isBlank(retorno))
                    	return false;
                	
            		logTotal.append(retorno);
        	    	setarLogOnline(retorno);
                	return false;
            	}
            	
        		logTotal.append(retorno);
    	    	setarLogOnline(retorno);
            	
                if(retorno.contains("~]$") || retorno.contains(":~$") || retorno.contains(":/]$"))
                	return true;
            	
                if(retorno.contains("assword:")) {
            		write((char)3);
            		retorno = readUntilAvailable(new String[] {":~$","~]$", ":/]$", "0$"}, infoProcedimento);
                	if (StringUtils.isBlank(retorno))
                    	return false;
                	
            		logTotal.append(retorno);
        	    	setarLogOnline(retorno);
                	return false;
            	}
            }
            
        	return false;
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
			return false;
		}
    }
    
    /**
     * Servidor Batman
     * @return
     */
    public boolean conectarBatman(String login, String senha) {
    	
    	String infoProcedimento = SimpleTelnet.class.getName() + "<br/>Procedimento: conectarBatman";
    	
    	if (StringUtils.isBlank(login) || StringUtils.isBlank(senha))
    		return false;
    	
    	// Logica para enviar Ctrl+C caso o equipamento nao responda
    	setEnviarCtrlC(true);
    	// Criando a Thread de monitoracao
    	Thread threadMonitora = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(PE_CONNECTION_TIMEOUT);
					if(isEnviarCtrlC()) {
						write((char)3);
					}
				} catch (InterruptedException e) {
				}
					
			}
		});
    	threadMonitora.start();
    	
    	//String comando = "ssh " + login + "@10.120.45.249";
    	String comando = "ssh " + login + "@192.168.7.31";
    	String retorno = enviarComandoAvailable(comando, new String[] {"assword:","(yes/no)?", ":~$", "~]$",":/]$",":/#"}, infoProcedimento);
    	
    	setEnviarCtrlC(false);
    	if(threadMonitora.isAlive()) {
    		threadMonitora.interrupt();
    	}  
    	
    	if (StringUtils.isBlank(retorno)) {
        	return false;
        }
    	
    	try {
    		
        	if(StringUtils.containsIgnoreCase(retorno, "host key verification failed")) {
        		//comando = "ssh -o UserKnownHostsFile=/dev/null " + login + "@10.120.45.249";
        		comando = "ssh -o UserKnownHostsFile=/dev/null " + login + "@192.168.7.31";
        		retorno = enviarComandoAvailable(comando, new String[] {"assword:","(yes/no)?", ":~$", "~]$",":/]$",":/#"}, infoProcedimento);
            	if (StringUtils.isBlank(retorno))
                	return false;
        	}
            
        	if(retorno.contains("(yes/no)?")) {
        		retorno = enviarComandoAvailable("yes", new String[] {"assword:","(yes/no)?", ":~$", "~]$",":/]$",":/#"}, infoProcedimento);
            	if (StringUtils.isBlank(retorno))
                	return false;
        	}
        	
            if(retorno.contains("assword:")) {
            	retorno = enviarComandoAvailableMore(senha,  new String[] {":~$","~]$",":/]$",":~#",":/#","assword:"}, "Pressione Enter para confirmar", "", infoProcedimento);
            	if (StringUtils.isBlank(retorno))
                	return false;
            	
        		logTotal.append(retorno);
    	    	setarLogOnline(retorno);
    	    	
            	if(retorno.contains("assword:")) {
            		write((char)3);
            		retorno = readUntilAvailable(new String[] {":~$","~]$",":/]$",":~#",":/#"}, "");
                	if (StringUtils.isBlank(retorno))
                    	return false;
                	
            		logTotal.append(retorno);
        	    	setarLogOnline(retorno);
            		return false;
            	}
            	if(StringUtils.containsAny(retorno, new String[] {"@batman:~#", "@batman:/#","@batman:~$", "@batman:/]$"})) {
                	return true;
            	}
            }
            
        	return false;
        	
    	} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
			return false;
		}
    }
    
    /**
     * Servidor CCSO
     * @return
     */
    public boolean conectarCcso(String login, String senha) {
    	
    	String infoProcedimento = SimpleTelnet.class.getName() + "<br/>Procedimento: conectarBatman";
    	
    	if (StringUtils.isBlank(login) || StringUtils.isBlank(senha))
    		return false;
    	
    	// Logica para enviar Ctrl+C caso o equipamento nao responda
    	setEnviarCtrlC(true);
    	// Criando a Thread de monitoracao
    	Thread threadMonitora = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(20000);
					if(isEnviarCtrlC()) {
						write((char)3);
					}
				} catch (InterruptedException e) {
				}
					
			}
		});
    	threadMonitora.start();
    	
    	String comando = "ssh " + login + "@10.12.190.102";
    	String retorno = enviarComandoAvailable(comando, new String[] {"assword:","(yes/no)?", ":~$", "~]$",":/]$",":/#"}, infoProcedimento);
    	
    	setEnviarCtrlC(false);
    	if(threadMonitora.isAlive()) {
    		threadMonitora.interrupt();
    	}  
    	
    	if (StringUtils.isBlank(retorno)) {
        	return false;
        }
    	
    	try {    		
        	if(StringUtils.containsIgnoreCase(retorno, "host key verification failed")) {
        		comando = "ssh -o UserKnownHostsFile=/dev/null " + login + "@10.12.190.102";
        		retorno = enviarComandoAvailable(comando, new String[] {"assword:","(yes/no)?", ":~$", "~]$",":/]$",":/#"}, infoProcedimento);
            	if (StringUtils.isBlank(retorno))
                	return false;
        	}
            
        	if(retorno.contains("(yes/no)?")) {
        		retorno = enviarComandoAvailable("yes", new String[] {"assword:","(yes/no)?", ":~$", "~]$",":/]$",":/#"}, infoProcedimento);
            	if (StringUtils.isBlank(retorno))
                	return false;
        	}
        	
            if(retorno.contains("assword:")) {
            	retorno = enviarComandoAvailableMore(senha,  new String[] {":~$","~]$",":/]$",":~#",":/#","assword:"}, "Pressione Enter para confirmar", "", infoProcedimento);
            	if (StringUtils.isBlank(retorno))
                	return false;
            	
        		logTotal.append(retorno);
    	    	setarLogOnline(retorno);
    	    	
            	if(retorno.contains("assword:")) {
            		write((char)3);
            		retorno = readUntilAvailable(new String[] {":~$","~]$",":/]$",":~#",":/#"}, "");
                	if (StringUtils.isBlank(retorno))
                    	return false;
                	
            		logTotal.append(retorno);
        	    	setarLogOnline(retorno);
            		return false;
            	}
            	
            	if(StringUtils.containsIgnoreCase(retorno, "[ccso@srv-ra-02 ~]$")) {
                	return true;
            	}
            }
            
        	return false;
        	
    	} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
			return false;
		}
    }
    
    /**
	 * Metodo que envia o comando
	 * @param comando
	 * @return
	 */
	public String enviarComandoAvailable(char comando, String caracterParada, String infoProcedimento) {	
		
		write(comando);		
		String retorno = readUntilAvailable(caracterParada, infoProcedimento);		
		if(StringUtils.isNotBlank(retorno)) {
			logTotal.append(retorno);
	    	setarLogOnline(retorno);
		} else {
			write((char)3);
			readUntilAvailable(caracterParada, infoProcedimento);	
		}
		return retorno;
	}
	
	/**
	 * Metodo que envia o comando
	 * @param comando
	 * @return
	 */
	public String enviarComandoAvailable(char comando, String caracterParada[], String infoProcedimento) {	
		
		write(comando);
		String retorno = readUntilAvailable(caracterParada, infoProcedimento);
		if(StringUtils.isNotBlank(retorno)) {
			logTotal.append(retorno);
	    	setarLogOnline(retorno);
		} else {
			write((char)3);
			readUntilAvailable(caracterParada, infoProcedimento);	
		}
		return retorno;
	}
    
	/**
	 * Metodo que envia o comando
	 * @param comando
	 * @return
	 */
	public String enviarComandoAvailable(String comando, String caracterParada, String infoProcedimento) {			
		write(comando);		
		String retorno = readUntilAvailable(caracterParada, infoProcedimento);		
		if(StringUtils.isNotBlank(retorno)) {
			logTotal.append(retorno);
	    	setarLogOnline(retorno);
		} else {
			write((char)3);
			readUntilAvailable(caracterParada, infoProcedimento);	
		}
		return retorno;
	}
	
	/**
	 * Metodo que envia o comando
	 * @param comando
	 * @return
	 */
	public String enviarComandoAvailable(String comando, String caracterParada[], String infoProcedimento) {	
		
		write(comando);
		
		String retorno = readUntilAvailable(caracterParada, infoProcedimento);
		if(StringUtils.isNotBlank(retorno)) {
			logTotal.append(retorno);
			setarLogOnline(retorno);
		} else {
			write((char)3);
			readUntilAvailable(caracterParada, infoProcedimento);	
		}
		return retorno;
	}
	
	/**
	 * Metodo que envia o comando
	 * @param comando
	 * @return
	 */
	public String enviarComandoAvailableCoriant(String comando, String caracterParada[], String infoProcedimento) {	
		
		write(comando);
		
		String retorno = readUntilAvailable(caracterParada, infoProcedimento);
		if(StringUtils.isNotBlank(retorno)) {
			logTotal.append(StringHelper.addNewComand(comando, retorno));
			setarLogOnline(StringHelper.addNewComand(comando, retorno));
		} else {
			write((char)3);
			readUntilAvailable(caracterParada, infoProcedimento);	
		}
		return retorno;
	}
	
	/**
	 * Metodo que envia o comando
	 * @param comando
	 * @return
	 */
	public String enviarComandoBufferAvailable(String comando, String caracterParada[], String infoProcedimento) {	
		
		write(comando);
		
		String retorno = readUntilBufferAvailable(caracterParada, infoProcedimento);
		if(StringUtils.isNotBlank(retorno)) {
			logTotal.append(retorno);
			setarLogOnline(retorno);
		} else {
			write((char)3);
			readUntilAvailable(caracterParada, infoProcedimento);	
		}
		return retorno;
	}
	
	public String enviarComandoAvailableSemEnter(String comando, String caracterParada[], String infoProcedimento) {	
		
		writeSemEnter(comando);
		
		String retorno = readUntilAvailable(caracterParada, infoProcedimento);
		if(StringUtils.isNotBlank(retorno)) {
			logTotal.append(retorno);
	    	setarLogOnline(retorno);
		} else {
			write((char)3);
			readUntilAvailable(caracterParada, infoProcedimento);	
		}
		return retorno;
	}
	
	
	
	/**
	 * Metodo que envia o comando
	 * @param comando
	 * @return
	 */
	public String enviarComandoAvailableOltAlcatelNova(String comando, String caracterParada[], String infoProcedimento) {	
		
		write(comando);
		
		String retorno = readUntilAvailableOltAlcatelNova(caracterParada, infoProcedimento);
		if(StringUtils.isNotBlank(retorno)) {
			logTotal.append(retorno);
	    	setarLogOnline(retorno);
		} else {
			write((char)3);
			readUntilAvailable(caracterParada, infoProcedimento);	
		}
		return retorno;
	}
	
	/**
	 * Metodo que envia o comando
	 * @param comando
	 * @return
	 */
	public String enviarComandoAvailableOltAlcatelAntiga(String comando, String caracterParada[], String infoProcedimento) {	
		
		write(comando);
		
		String retorno = readUntilAvailableOltAlcatelAntiga(caracterParada, infoProcedimento);
		if(StringUtils.isNotBlank(retorno)) {
			logTotal.append(retorno);
	    	setarLogOnline(retorno);
		} else {
			write((char)3);
			readUntilAvailable(caracterParada, infoProcedimento);	
		}
		return retorno;
	}
	
	public String enviarComandoAvailableOltAlcatelSemEnter(String comando, String caracterParada[], String infoProcedimento) {	
		
		writeSemEnter(comando);
		
		String retorno = readUntilAvailableOltAlcatelNova(caracterParada, infoProcedimento);
		if(StringUtils.isNotBlank(retorno)) {
			logTotal.append(retorno);
	    	setarLogOnline(retorno);
		} else {
			write((char)3);
			readUntilAvailable(caracterParada, infoProcedimento);	
		}
		return retorno;
	}
	
	/**
	 * Metodo que envia o comando
	 * @param comando
	 * @return
	 */
	public RetornoModel enviarComandoAvailablePing(String comando, String caracterParada[], String infoProcedimento) {	
		
		write(comando);
		
		//RetornoModel retornoModel = readUntilAvailablePing(caracterParada, infoProcedimento);
		RetornoModel retornoModel = readUntilAvailablePing2(caracterParada, infoProcedimento);
		
		if(retornoModel.getCodigo() == CodigoTimeoutEnum.ERRO_INTERNO.getCodigo() || retornoModel.getCodigo() == CodigoTimeoutEnum.READ_UNTIL_TIMEOUT.getCodigo()) {
			write((char)3);
			String aux = readUntilAvailable(caracterParada, infoProcedimento);
			retornoModel.setRetorno(retornoModel.getRetorno() + aux);
			return retornoModel;
		}
		
		if(retornoModel.getCodigo() == CodigoTimeoutEnum.PING_PERDA_PACOTE.getCodigo()) {
			writePrint((char)3);
			try {
				Thread.sleep(TELNET_SLEEP_500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			String aux = readUntilAvailable(caracterParada, infoProcedimento);
			retornoModel.setRetorno(retornoModel.getRetorno() + aux);
		}
		
		return retornoModel;
	}
	
	/**
	 * Metodo que envia o comando
	 * @param comando
	 * @return
	 */
	public String enviarComandoAvailableSleepDeLeitura(String comando, String caracterParada[], String infoProcedimento) {	
		
		write(comando);
		
		try {
			Thread.sleep(TELNET_SLEEP_500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		String retorno = readUntilAvailableSleepDeLeitura(caracterParada, infoProcedimento);
		if(StringUtils.isNotBlank(retorno)) {
			logTotal.append(retorno);
	    	setarLogOnline(retorno);
		} else {
			write((char)3);
			readUntilAvailable(caracterParada, infoProcedimento);	
		}
		return retorno;
	}
	
	/**
	 * Metodo que envia o comando
	 * @param comando
	 * @return
	 */
	public String enviarComandoAvailable(String comando, String caracterParada[], String infoProcedimento, boolean enviarEmail, boolean appendComando) {	
		
		write(comando);
		if(appendComando) {
			logTotal.append(comando);
	    	setarLogOnline(comando);
		}
		    	
		String retorno = readUntilAvailable(caracterParada, infoProcedimento, enviarEmail);
		if(StringUtils.isNotBlank(retorno)) {
			logTotal.append(retorno);
	    	setarLogOnline(retorno);
		} else {
			write((char)3);
			readUntilAvailable(caracterParada, infoProcedimento);	
		}
		return retorno;
	}
	
	/**
	 * Metodo que envia o comando
	 * @param comando
	 * @return
	 */
	public String enviarComandoAvailableParadaRepetida(String comando, String[] caracterParada, int qtdVezesEncontrouParada, String[] patternErro, String infoProcedimento) {	
		
		write(comando);		
		String retorno = readUntilAvailableParadaRepetida(caracterParada, qtdVezesEncontrouParada, patternErro, infoProcedimento);		
		if(StringUtils.isNotBlank(retorno)) {
			logTotal.append(retorno);
	    	setarLogOnline(retorno);
		} else {
			write((char)3);
			readUntilAvailable(caracterParada, infoProcedimento);	
		}
		return retorno;
	}
	
	public String enviarComandoAvailableParadaRepetidaMore(String comando, String[] caracterParada, int qtdVezesEncontrouParada, String[] patternErro, String[] caracterMore, String comandoMore, String infoProcedimento) {	
		
		write(comando);		
		String retorno = readUntilAvailableParadaRepetidaMore(caracterParada, qtdVezesEncontrouParada, patternErro, infoProcedimento, caracterMore, comandoMore);		
		if(StringUtils.isNotBlank(retorno)) {
			logTotal.append(retorno);
	    	setarLogOnline(retorno);
		} else {
			write((char)3);
			readUntilAvailable(caracterParada, infoProcedimento);	
		}
		return retorno;
	}
	
	/**
	 * Metodo que envia o comando
	 * @param comando
	 * @return
	 */
	public String enviarComandoAvailableMore(String comando, String caracterParada[], String caracterMore, String comandoMore, String infoProcedimento) {	
		
		write(comando);
		String retorno = readUntilAvailableMore(caracterParada, caracterMore, comandoMore, infoProcedimento);
		if(StringUtils.isNotBlank(retorno)) {
			logTotal.append(retorno);
	    	setarLogOnline(retorno);
		} else {
			write((char)3);
			readUntilAvailable(caracterParada, infoProcedimento);	
		}
		return retorno;
	}
	
	/**
	 * Metodo que envia o comando
	 * @param comando
	 * @return
	 */
	public String enviarComandoAvailableMore(String comando, String caracterParada[], String caracterMore[], String comandoMore, String infoProcedimento) {	
		
		write(comando);
		String retorno = readUntilAvailableMore(caracterParada, caracterMore, comandoMore, infoProcedimento);
		if(StringUtils.isNotBlank(retorno)) {
			logTotal.append(retorno);
	    	setarLogOnline(retorno);
		} else {
			write((char)3);
			readUntilAvailable(caracterParada, infoProcedimento);	
		}
		return retorno;
	}
	
	/**
	 * Metodo que envia o comando
	 * @param comando
	 * @return
	 */
	public String enviarComandoAvailableMoreCoriant(String comando, String caracterParada[], String caracterMore[], String comandoMore, String infoProcedimento) {	
		
		write(comando);
		String retorno = readUntilAvailableMore(caracterParada, caracterMore, comandoMore, infoProcedimento);
		if(StringUtils.isNotBlank(retorno)) {
			logTotal.append(StringHelper.addNewComand(comando, retorno));
	    	setarLogOnline(StringHelper.addNewComand(comando, retorno));
		} else {
			write((char)3);
			readUntilAvailable(caracterParada, infoProcedimento);	
		}
		return retorno;
	}
	
	/**
	 * Metodo que envia o comando
	 * @param comando
	 * @return
	 */
	public String enviarComandoAvailableMoreOltHuawei(String comando, String caracterParada[], String caracterMore[], String comandoMore, String infoProcedimento) {	
		
		write(comando);
		String retorno = readUntilAvailableMoreOltHuawei(caracterParada, caracterMore, comandoMore, infoProcedimento);
		if(StringUtils.isNotBlank(retorno)) {
			logTotal.append(retorno);
	    	setarLogOnline(retorno);
		} else {
			write((char)3);
			readUntilAvailable(caracterParada, infoProcedimento);	
		}
		return retorno;
	}
	
	/**
	 * Metodo que envia o comando
	 * @param comando
	 * @return
	 */
	public String enviarComandoAvailableSemAppendLogTotal(String comando, String caracterParada, String infoProcedimento) {	
		write(comando);		
		String retorno = readUntilAvailable(caracterParada, infoProcedimento);
		if(StringUtils.isBlank(retorno)) {
			write((char)3);
			readUntilAvailable(caracterParada, infoProcedimento);	
		}
		return retorno;
	}
	
	/**
	 * Metodo que envia o comando
	 * @param comando
	 * @return
	 */
	public String enviarComandoAvailableSemAppendLogTotal(String comando, String[] caracterParada, String infoProcedimento) {	
		write(comando);		
		String retorno = readUntilAvailable(caracterParada, infoProcedimento);
		if(StringUtils.isBlank(retorno)) {
			write((char)3);
			readUntilAvailable(caracterParada, infoProcedimento);	
		}
		return retorno;
	}
	
	/**
	 * Metodo que envia o comando
	 * @param comando
	 * @return
	 */
	public String enviarComandoAvailableMoreSemAppendLogTotal(String comando, String caracterParada[], String caracterMore, String comandoMore, String infoProcedimento) {	
		
		write(comando);
		String retorno = readUntilAvailableMore(caracterParada, caracterMore, comandoMore, infoProcedimento);
		if(StringUtils.isBlank(retorno)) {
			write((char)3);
			readUntilAvailable(caracterParada, infoProcedimento);	
		}
		return retorno;
	}
	
	/**
	 * Metodo que envia o comando
	 * @param comando
	 * @return
	 */
	public String enviarComandoAvailableMoreSemAppendLogTotal(String comando, String caracterParada[], String caracterMore[], String comandoMore, String infoProcedimento) {	
		
		write(comando);
		String retorno = readUntilAvailableMore(caracterParada, caracterMore, comandoMore, infoProcedimento);
		if(StringUtils.isBlank(retorno)) {
			write((char)3);
			readUntilAvailable(caracterParada, infoProcedimento);	
		}
		return retorno;
	}
	
	/**
	 * Metodo que envia o comando
	 * @param comando
	 * @return
	 */
	public String enviarComandoAvailableWithSleep(String comando, String caracterParada[], String infoProcedimento) {	
		
		write(comando);
		
    	// Logica para enviar Ctrl+C caso o equipamento nao responda
    	setEnviarCtrlC(true);
    	// Criando a Thread de monitoracao
    	Thread threadMonitora = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(15000);
					if(isEnviarCtrlC()) {
						write((char)27); // ESC
					}
				} catch (InterruptedException e) {
				}
			}
		});
    	threadMonitora.start();
    	
		String retorno = readUntilAvailable(caracterParada, infoProcedimento);
    	
		setEnviarCtrlC(false);
    	if(threadMonitora.isAlive()) {
    		threadMonitora.interrupt();
    	} 
		
		if(StringUtils.isNotBlank(retorno)) {
			logTotal.append(retorno);
	    	setarLogOnline(retorno);
		} else {
			write((char)3);
			readUntilAvailable(caracterParada, infoProcedimento);	
		}
		return retorno;
	}
	
	/**
	 * Metodo append log
	 * @param comando
	 * @return
	 */
	public void appendLogTotal(String retorno) {	
		
		if(StringUtils.isNotBlank(retorno)) {
			logTotal.append(retorno);
	    	setarLogOnline(retorno);
		}
	}
	
	/**
	 * Escreve o comando na porta de Saída 
	 * @param value
	 */
	public void write(String value) {
    	
		if(telnet != null && telnet.isAvailable()) {
			try {
	            out.println(value);
	            out.flush();
	        } catch (Exception e) {
	        	ExceptionLogger.record(ExceptionUtils.getStackTrace(e), "");
	        }
		}	
    }
	
	/**
	 * Escreve o comando na porta de Saída 
	 * @param value
	 */
	public void writeSemEnter(String value) {
    	
		if(telnet != null && telnet.isAvailable()) {
			try {
	            out.print(value);
	            out.flush();
	        } catch (Exception e) {
	        	ExceptionLogger.record(ExceptionUtils.getStackTrace(e), "");
	        }
		}	
    }
    
    /**
	 * Escreve o comando na porta de Saída 
	 * @param value
	 */
    public void write(char value) {
    	
		if(telnet != null && telnet.isAvailable()) {
			try {
		        out.println(value);
		        out.flush();
		    } catch (Exception e) {
		    	ExceptionLogger.record(ExceptionUtils.getStackTrace(e), "");
		    }
		}	
    }
    
    /**
	 * Escreve o comando na porta de Saída 
	 * @param value
	 */
    public void writePrint(char value) {
    	
		if(telnet != null && telnet.isAvailable()) {
			try {
	            out.print(value);
	            out.flush();
	        } catch (Exception e) {
	        	ExceptionLogger.record(ExceptionUtils.getStackTrace(e), "");
	        }
		}	
    }
    
    /**
     * 
     * @param pattern
     * @return
     */
    public String readUntilAvailable(String pattern, String infoProcedimento) {
    	
    	if(telnet == null || !telnet.isAvailable()) {
    		ExceptionLogger.recordDisk("readUntilAvailable: Socket client disconnected ou null", infoProcedimento);
            return null;
    	}
    	
    	try {
            StringBuilder sb = new StringBuilder();
            int n = -1;
            char ch;

            try {
                n = in.read();
            } catch (SocketTimeoutException e) {
            	ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
            	
            	if(IatConstants.DISCONNECT_TELNET_SOCKET) {
            		fecharSessaoTelnet();
            	}
            	
                return null;
            }
            ch = (char) n;
            if(IatConstants.DEBUG) {
				System.out.print(ch);
			}

            while (n != -1) {
            	sb.append(ch);                	
        		if (sb.toString().endsWith(pattern)) {
        			Thread.sleep(TELNET_SLEEP_50);
        			while(in.available() > 0) {
        				Thread.sleep(TELNET_SLEEP_5);
        				ch = (char) in.read();
        				sb.append(ch);
        				
        				if(IatConstants.DEBUG) {
        					System.out.print(ch);
        				}
        			}
            		return sb.toString();
        		}   
             
                try {
                    n = in.read();
                } catch (SocketTimeoutException e) {
                	if(sb != null && !sb.toString().isEmpty()) {
                		infoProcedimento += "<br/><br/><br/><strong>SimpleTelnet -> readUntilAvailable(String pattern, String infoProcedimento) -> sb.toString()</strong><br/>" + sb.toString();
                	}
                	ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
                	
                	if(IatConstants.DISCONNECT_TELNET_SOCKET) {
                		fecharSessaoTelnet();
                	}
                	
                    return null;
                }
                ch = (char) n;
                if(IatConstants.DEBUG) {
					System.out.print(ch);
				}
            }
    	} catch (Exception e) {
    		ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
    		
    		if(IatConstants.DISCONNECT_TELNET_SOCKET) {
        		try {
					fecharSessaoTelnet();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
        	}
    		
            return null;
    	}
    	
    	return null;
    }
    
    /**
     * Funcao que recebe o retorno do comando ate que encontre a String de parada
     * @param pattern
     * @return
     */
    public String readUntilAvailable(String[] pattern, String infoProcedimento) {
    	
    	if(telnet == null || !telnet.isAvailable()) {
    		ExceptionLogger.recordDisk("readUntilAvailable: Socket client disconnected ou null", infoProcedimento);
            return null;
    	}
    	
    	try {
            StringBuilder sb = new StringBuilder();
            int n = -1;
            char ch;

            try {
                n = in.read();
            } catch (SocketTimeoutException e) {
            	ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
            	
            	if(IatConstants.DISCONNECT_TELNET_SOCKET) {
            		fecharSessaoTelnet();
            	}
            	
                return null;
            }
            ch = (char) n;
            if(IatConstants.DEBUG) {
				System.out.print(ch);
			}

            while (n != -1) {
            	
                sb.append(ch);                	
            	for(int c = 0; c < pattern.length; c++) {                		
            		if (sb.toString().endsWith(pattern[c])) {
        				for(int tentativa = 0; tentativa < 3; tentativa++) {
            				Thread.sleep(TELNET_SLEEP_150);
	            			while(in.available() > 0) {
	                            Thread.sleep(TELNET_SLEEP_5);
	            				ch = (char) in.read();
	            				if(IatConstants.DEBUG) {
	            					System.out.print(ch);
	            				}
	            				sb.append(ch);
	            			}
            			}
            			return sb.toString();
            		}
            	}    
             
                try {
                    n = in.read();
                } catch (SocketTimeoutException e) {
                	if(sb != null && !sb.toString().isEmpty()) {
                		infoProcedimento += "<br/><br/><br/><strong>SimpleTelnet -> readUntilAvailable(String[] pattern, String infoProcedimento) -> sb.toString()</strong><br/>" + sb.toString();
                	}
                	ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
                	
                	if(IatConstants.DISCONNECT_TELNET_SOCKET) {
                		fecharSessaoTelnet();
                	}
                	
                    return null;
                }
                ch = (char) n;
                if(IatConstants.DEBUG) {
					System.out.print(ch);
				}
            }
    	} catch (Exception e) {
    		ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
    		
    		if(IatConstants.DISCONNECT_TELNET_SOCKET) {
        		try {
					fecharSessaoTelnet();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
        	}
    		
            return null;
    	}

    	return null;
    }
    
    /**
     * Funcao que recebe o retorno do comando ate que encontre a String de parada
     * @param pattern
     * @return
     */
    public String readUntilBufferAvailable(String[] pattern, String infoProcedimento) {
    	
    	if(telnet == null || !telnet.isAvailable()) {
    		ExceptionLogger.recordDisk("readUntilBufferAvailable: Socket client disconnected ou null", infoProcedimento);
            return null;
    	}
    	
		try {
			int available = (int) (in.available() * 1.25);
			int lengthBytes = (int) Math.min(Integer.MAX_VALUE, available == 0 ? 1000024 : available) ;
			final byte[] buffer = new byte[lengthBytes];
			StringBuilder sb = new StringBuilder();
			byte[] contentsRead;
			int bytesRead = 0;
			
			try {
				bytesRead = Math.max(0, in.read(buffer));
				contentsRead = (bytesRead == lengthBytes ? buffer : Arrays.copyOf(buffer, bytesRead));
			} catch (SocketTimeoutException e) {
				ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
				if(IatConstants.DISCONNECT_TELNET_SOCKET) {
					fecharSessaoTelnet();
				}
				return null;
			}
			if (bytesRead != 0) {
				if(IatConstants.DEBUG) {
					System.out.print(new String(contentsRead, 0, bytesRead));
				}
			}
			
			ByteArrayOutputStream result = new ByteArrayOutputStream(buffer.length);
			while (bytesRead != 0) {
				
				result.write(contentsRead, 0, bytesRead);
				
				if (bytesRead > 1024) {
					sb = new StringBuilder(new String(contentsRead, 0, bytesRead));
				} else if (sb.length() > 1024) {
					// Pega da metade para frente e concatena com o novo retorno
					sb = new StringBuilder(StringUtils.substring(sb.toString(), 1024 / 2).concat(new String(contentsRead, 0, bytesRead)));
				} else {
					sb.append(new String(contentsRead, 0, bytesRead));
				}
				
				for(int c = 0; c < pattern.length; c++) {
					if (sb.toString().trim().endsWith(pattern[c])) {
						Thread.sleep(TELNET_SLEEP_50);
						while(in.available() > 0) {
							Thread.sleep(TELNET_SLEEP_5);
							try {
								bytesRead = Math.max(0, in.read(buffer));
								contentsRead = (bytesRead == lengthBytes ? buffer : Arrays.copyOf(buffer, bytesRead));
							} catch (SocketTimeoutException e) {
								ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
								if(IatConstants.DISCONNECT_TELNET_SOCKET) {
									fecharSessaoTelnet();
								}
								return null;
							}
							if (bytesRead != 0) {
								result.write(contentsRead, 0, bytesRead);
								if(IatConstants.DEBUG) {
									System.out.print(new String(contentsRead, 0, bytesRead));
								}
							}
						}
						result.flush();
						return result.toString();
					}
				}
				
				try {
					bytesRead = Math.max(0, in.read(buffer));
					contentsRead = (bytesRead == lengthBytes ? buffer : Arrays.copyOf(buffer, bytesRead));
				} catch (SocketTimeoutException e) {
					if(result != null && !result.toString().isEmpty()) {
						result.flush();
						infoProcedimento += "<br/><br/><br/><strong>SimpleTelnet -> readUntilBufferAvailableMoreEx(String[] pattern, String[] patternMore, String comandoMore, String infoProcedimento) -> sb.toString()</strong><br/>" + result.toString();
					}
					ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
					if(IatConstants.DISCONNECT_TELNET_SOCKET) {
						fecharSessaoTelnet();
					}
					return null;
				}
				if (bytesRead != 0) {
					if(IatConstants.DEBUG) {
						System.out.print(new String(contentsRead, 0, bytesRead));
					}
				}
			}
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
			if(IatConstants.DISCONNECT_TELNET_SOCKET) {
				try {
					fecharSessaoTelnet();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			return null;
		}
		return null;
    }
    
    /**
     * Funcao que recebe o retorno do comando ate que encontre a String de parada
     * @param pattern
     * @return
     */
    public String readUntilAvailableOltAlcatelNova(String[] pattern, String infoProcedimento) {
    	
    	if(telnet == null || !telnet.isAvailable()) {
    		ExceptionLogger.recordDisk("readUntilAvailableOltAlcatelNova: Socket client disconnected ou null", infoProcedimento);
            return null;
    	}
    	
    	try {
            StringBuilder sb = new StringBuilder();
            int n = -1;
            char ch;

            try {
                n = in.read();
            } catch (SocketTimeoutException e) {
            	ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
            	
            	if(IatConstants.DISCONNECT_TELNET_SOCKET) {
            		fecharSessaoTelnet();
            	}
            	
                return null;
            }
            ch = (char) n;
            if(IatConstants.DEBUG) {
				System.out.print(ch);
			}

            while (n != -1) {
            	
            	sb.append(ch);                	
            	for(int c = 0; c < pattern.length; c++) {                		
            		if (sb.toString().endsWith(pattern[c])) {            			
            			for(int tentativa = 0; tentativa < 3; tentativa++) {
            				Thread.sleep(TELNET_SLEEP_150);
	            			while(in.available() > 0) {
	                            Thread.sleep(TELNET_SLEEP_5);
	            				ch = (char) in.read();
	            				if(IatConstants.DEBUG) {
	            					System.out.print(ch);
	            				}
	            				sb.append(ch);
	            			}
            			}
            			return StringHelper.removerCodigoCores(sb.toString());
            		}
            	}    
             
                try {
                    n = in.read();
                } catch (SocketTimeoutException e) {
                	if(sb != null && !sb.toString().isEmpty()) {
                		infoProcedimento += "<br/><br/><br/><strong>SimpleTelnet -> readUntilAvailable(String[] pattern, String infoProcedimento) -> sb.toString()</strong><br/>" + sb.toString();
                	}
                	ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
                	
                	if(IatConstants.DISCONNECT_TELNET_SOCKET) {
                		fecharSessaoTelnet();
                	}
                	
                    return null;
                }
                ch = (char) n;
                if(IatConstants.DEBUG) {
					System.out.print(ch);
				}
            }
    	} catch (Exception e) {
    		ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
    		
    		if(IatConstants.DISCONNECT_TELNET_SOCKET) {
        		try {
					fecharSessaoTelnet();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
        	}
    		
            return null;
    	}

    	return null;
    }
    
    /**
     * Funcao que recebe o retorno do comando ate que encontre a String de parada
     * Quando encontra o caracter de parada espera até receber o caracter ';'
     * @param pattern
     * @return
     */
    public String readUntilAvailableOltAlcatelAntiga(String[] pattern, String infoProcedimento) {
    	
    	if(telnet == null || !telnet.isAvailable()) {
    		ExceptionLogger.recordDisk("readUntilAvailableOltAlcatelAntiga: Socket client disconnected ou null", infoProcedimento);
            return null;
    	}
    	
    	try {
            StringBuilder sb = new StringBuilder();
            int n = -1;
            char ch;

            try {
                n = in.read();
            } catch (SocketTimeoutException e) {
            	ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
            	
            	if(IatConstants.DISCONNECT_TELNET_SOCKET) {
            		fecharSessaoTelnet();
            	}
            	
                return null;
            }
            ch = (char) n;
            if(IatConstants.DEBUG) {
				System.out.print(ch);
			}

            while (n != -1) {
            	
            	sb.append(ch);                	
            	for(int c = 0; c < pattern.length; c++) {                		
            		if (sb.toString().endsWith(pattern[c])) {
            			
            			// lendo ate aparecer o caracter ';'
            			while (n != -1) {
            				n = in.read();
                            ch = (char) n;
        	                if(IatConstants.DEBUG) {
        						System.out.print(ch);
        					}
            				sb.append(ch);
            				if(sb.toString().endsWith(";")) {
            					break;
            				}
            			}
            			
            			Thread.sleep(TELNET_SLEEP_100);
            			while(in.available() > 0) {
                            Thread.sleep(TELNET_SLEEP_5);
            				ch = (char) in.read();
            				if(IatConstants.DEBUG) {
            					System.out.print(ch);
            				}
            				sb.append(ch);
            			}	
	            		
            			return StringHelper.removerCodigoCores(sb.toString());
                    }
            	}    
             
                try {
                    n = in.read();
                    ch = (char) n;
	                if(IatConstants.DEBUG) {
						System.out.print(ch);
					}
                } catch (SocketTimeoutException e) {
                	if(sb != null && !sb.toString().isEmpty()) {
                		infoProcedimento += "<br/><br/><br/><strong>SimpleTelnet -> readUntilAvailable(String[] pattern, String infoProcedimento) -> sb.toString()</strong><br/>" + sb.toString();
                	}
                	ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
                	
                	if(IatConstants.DISCONNECT_TELNET_SOCKET) {
                		fecharSessaoTelnet();
                	}
                	
                    return null;
                }
	                
            }
    	} catch (Exception e) {
    		ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
    		
    		if(IatConstants.DISCONNECT_TELNET_SOCKET) {
        		try {
					fecharSessaoTelnet();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
        	}
    		
            return null;
    	}

    	return null;
    }
    
    /**
     * Funcao que recebe o retorno do comando ate que encontre a String de parada
     * @param pattern
     * @return
     */
    public RetornoModel readUntilAvailablePing(String[] pattern, String infoProcedimento) {
    	
    	if(telnet == null || !telnet.isAvailable()) {
    		ExceptionLogger.recordDisk("readUntilAvailablePing: Socket client disconnected ou null", infoProcedimento);
    		
    		RetornoModel retorno = new RetornoModel();
        	retorno.setCodigo(CodigoTimeoutEnum.READ_UNTIL_TIMEOUT.getCodigo());
        	retorno.setRetorno("");
        	return retorno;
    	}
    	
    	try {
            StringBuilder sb = new StringBuilder();
            int n = -1;
            char ch;

            try {
                n = in.read();
            } catch (SocketTimeoutException e) {
            	ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
            	
            	if(IatConstants.DISCONNECT_TELNET_SOCKET) {
            		fecharSessaoTelnet();
            	}
            	
            	RetornoModel retorno = new RetornoModel();
            	retorno.setCodigo(CodigoTimeoutEnum.READ_UNTIL_TIMEOUT.getCodigo());
            	retorno.setRetorno(sb.toString());
            	return retorno;
            }
            ch = (char) n;
            if(IatConstants.DEBUG) {
				System.out.print(ch);
			}
            
            while (n != -1) {
            	
                sb.append(ch);
                
                // Verificar se teve perda de 5 pacotes seguidos
                if (StringUtils.countMatches(sb.toString().toLowerCase(), ".....") >= 1) {
                	RetornoModel retorno = new RetornoModel();
                	retorno.setCodigo(CodigoTimeoutEnum.PING_PERDA_PACOTE.getCodigo());
                	retorno.setRetorno(sb.toString());
                	return retorno;
                }
                
                // Verificar se teve perda de 5 pacotes seguidos
                if (StringUtils.countMatches(sb.toString().toLowerCase(), "uuuuu") >= 1) {
                	RetornoModel retorno = new RetornoModel();
                	retorno.setCodigo(CodigoTimeoutEnum.PING_PERDA_PACOTE.getCodigo());
                	retorno.setRetorno(sb.toString());
                	return retorno;
                }
                
                // Verificar se teve perda de 5 pacotes seguidos
                if(StringUtils.countMatches(sb.toString().toLowerCase(), "request time out") >= 5 ) {
                	
        			String[] linhasRetorno = sb.toString().toLowerCase().split("" + GlobalStrEnum.BREAK_LINE.toString() + "");
        			String linhaAux = "";
        			int contSeq = 0;
        	    	for (int c = 0; c < linhasRetorno.length - 1; c++) {
        	    		linhaAux = linhasRetorno[c].trim();
        	    		
        	    		if (linhaAux.contains("request time out")) {
        	    			contSeq++;
        	    			
        	    			// 5 perdas seguidas
        	    			if (contSeq >= 5) {
        	                	RetornoModel retorno = new RetornoModel();
        	                	retorno.setCodigo(CodigoTimeoutEnum.PING_PERDA_PACOTE.getCodigo());
        	                	retorno.setRetorno(sb.toString());
        	                	return retorno;
        	    			}
        	    		} else {
        	    			contSeq = 0;
        	    		}
        	    	}
                }
                
            	for(int c = 0; c < pattern.length; c++) {                		
            		if (sb.toString().endsWith(pattern[c])) {
            			Thread.sleep(TELNET_SLEEP_50);
            			while(in.available() > 0) {
                            Thread.sleep(TELNET_SLEEP_5);
            				ch = (char) in.read();
            				sb.append(ch);
            				if(IatConstants.DEBUG) {
            					System.out.print(ch);
            				}
            			}
            			RetornoModel retorno = new RetornoModel();
                    	retorno.setCodigo(CodigoTimeoutEnum.READ_UNTIL_OK.getCodigo());
                    	retorno.setRetorno(sb.toString());
                    	return retorno;
                    }
            	}    
             
                try {
                    n = in.read();
                } catch (SocketTimeoutException e) {
                	if(sb != null && !sb.toString().isEmpty()) {
                		infoProcedimento += "<br/><br/><br/><strong>SimpleTelnet -> readUntilAvailable(String[] pattern, String infoProcedimento) -> sb.toString()</strong><br/>" + sb.toString();
                	}
                	ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
                	
                	if(IatConstants.DISCONNECT_TELNET_SOCKET) {
                		fecharSessaoTelnet();
                	}
                	
                	RetornoModel retorno = new RetornoModel();
                	retorno.setCodigo(CodigoTimeoutEnum.READ_UNTIL_TIMEOUT.getCodigo());
                	retorno.setRetorno(sb.toString());
                	return retorno;
                }
                ch = (char) n;
                if(IatConstants.DEBUG) {
					System.out.print(ch);
				}
            }
    	} catch (Exception e) {
    		ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
    		
    		if(IatConstants.DISCONNECT_TELNET_SOCKET) {
        		try {
					fecharSessaoTelnet();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
        	}
    	}

    	RetornoModel retorno = new RetornoModel();
    	retorno.setCodigo(CodigoTimeoutEnum.ERRO_INTERNO.getCodigo());
    	retorno.setRetorno("");
    	return retorno;
    }
    
    /**
     * Funcao que recebe o retorno do comando ate que encontre a String de parada
     * @param pattern
     * @return
     */
    public RetornoModel readUntilAvailablePing2(String[] pattern, String infoProcedimento) {
    	
    	if(telnet == null || !telnet.isAvailable()) {
    		ExceptionLogger.recordDisk("readUntilAvailablePing2: Socket client disconnected ou null", infoProcedimento);
    		
    		RetornoModel retorno = new RetornoModel();
        	retorno.setCodigo(CodigoTimeoutEnum.READ_UNTIL_TIMEOUT.getCodigo());
        	retorno.setRetorno("");
        	return retorno;
    	}
    	
    	try {
            StringBuilder sb = new StringBuilder();
            StringBuilder sbAux = new StringBuilder();
            int n = -1;
            char ch;
            boolean enviouCTRLC = false;
            
            try {
                n = in.read();
            } catch (SocketTimeoutException e) {
            	ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
            	
            	if(IatConstants.DISCONNECT_TELNET_SOCKET) {
            		fecharSessaoTelnet();
            	}
            	
            	RetornoModel retorno = new RetornoModel();
            	retorno.setCodigo(CodigoTimeoutEnum.READ_UNTIL_TIMEOUT.getCodigo());
            	retorno.setRetorno(sb.toString());
            	return retorno;
            }
            ch = (char) n;
            if(IatConstants.DEBUG) {
				System.out.print(ch);
			}
            
            while (n != -1) {
            	
            	if(sb.toString().length() > 200) {
            		sb = null;
            		sb = new StringBuilder();
            	}
            	
                sb.append(ch);
                sbAux.append(ch);
                
                // Verificar se teve perda de 5 pacotes seguidos
                if (StringUtils.countMatches(sb.toString().toLowerCase(), ".....") >= 1) {
                	RetornoModel retorno = new RetornoModel();
                	retorno.setCodigo(CodigoTimeoutEnum.PING_PERDA_PACOTE.getCodigo());
                	retorno.setRetorno(sbAux.toString());
                	return retorno;
                }
                
                // Verificar se teve perda de 5 pacotes seguidos
                if (StringUtils.countMatches(sb.toString().toLowerCase(), "uuuuu") >= 1) {
                	RetornoModel retorno = new RetornoModel();
                	retorno.setCodigo(CodigoTimeoutEnum.PING_PERDA_PACOTE.getCodigo());
                	retorno.setRetorno(sbAux.toString());
                	return retorno;
                }
                
                // Verificar se teve perda de 5 pacotes seguidos
                if(StringUtils.countMatches(sb.toString().toLowerCase(), "request time out") >= 5 ) {
                	
        			String[] linhasRetorno = sb.toString().toLowerCase().split("" + GlobalStrEnum.BREAK_LINE.toString() + "");
        			String linhaAux = "";
        			int contSeq = 0;
        	    	for (int c = 0; c < linhasRetorno.length - 1; c++) {
        	    		linhaAux = linhasRetorno[c].trim();
        	    		
        	    		if (linhaAux.contains("request time out")) {
        	    			contSeq++;
        	    			
        	    			// 5 perdas seguidas
        	    			if (contSeq >= 5) {
        	                	RetornoModel retorno = new RetornoModel();
        	                	retorno.setCodigo(CodigoTimeoutEnum.PING_PERDA_PACOTE.getCodigo());
        	                	retorno.setRetorno(sbAux.toString());
        	                	return retorno;
        	    			}
        	    		} else {
        	    			contSeq = 0;
        	    		}
        	    	}
                }
                
                // verificando se o bloco atual possui mais de 100 caracteres
                if(sb.toString().length() > 50) {
                	for(int c = 0; c < pattern.length; c++) {                		
	            		if (sb.toString().endsWith(pattern[c])) {
	            			Thread.sleep(TELNET_SLEEP_50);
	            			while(in.available() > 0) {
	                            Thread.sleep(TELNET_SLEEP_5);
	            				ch = (char) in.read();
	            				sb.append(ch);
	            				sbAux.append(ch);
	            				if(IatConstants.DEBUG) {
	            					System.out.print(ch);
	            				}
	            			}
	            			
	            			RetornoModel retorno = new RetornoModel();
	            			retorno.setRetorno(sbAux.toString());
	            			if(enviouCTRLC) {
	            				retorno.setCodigo(CodigoTimeoutEnum.ERRO_IDLE_TIMEOUT.getCodigo());
	            			} else {
	            				retorno.setCodigo(CodigoTimeoutEnum.READ_UNTIL_OK.getCodigo());
	            			}
	                    	return retorno;
	                    }
	            	} 
                } else {
                	
                	for(int c = 0; c < pattern.length; c++) {                		
	            		if (sbAux.toString().endsWith(pattern[c])) {
	            			Thread.sleep(TELNET_SLEEP_50);
	            			while(in.available() > 0) {
	                            Thread.sleep(TELNET_SLEEP_5);
	            				ch = (char) in.read();
	            				sb.append(ch);
	            				sbAux.append(ch);
	            				if(IatConstants.DEBUG) {
	            					System.out.print(ch);
	            				}
	            			}
	            			
	            			RetornoModel retorno = new RetornoModel();
	            			retorno.setRetorno(sbAux.toString());
	            			if(enviouCTRLC) {
	            				retorno.setCodigo(CodigoTimeoutEnum.ERRO_IDLE_TIMEOUT.getCodigo());
	            			} else {
	            				retorno.setCodigo(CodigoTimeoutEnum.READ_UNTIL_OK.getCodigo());
	            			}
	                    	return retorno;
	                    }
	            	}
                }
                
                if(!enviouCTRLC && StringUtils.containsIgnoreCase(sbAux.toString(), "The idle timeout is soon to expire on this line")) {
                	write((char)3);
                	enviouCTRLC = true;
                }
                
                try {
                    n = in.read();
                } catch (SocketTimeoutException e) {
                	if(sb != null && !sb.toString().isEmpty()) {
                		infoProcedimento += "<br/><br/><br/><strong>SimpleTelnet -> readUntilAvailable(String[] pattern, String infoProcedimento) -> sb.toString()</strong><br/>" + sbAux.toString();
                	}
                	ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
                	
                	if(IatConstants.DISCONNECT_TELNET_SOCKET) {
                		fecharSessaoTelnet();
                	}
                	
                	RetornoModel retorno = new RetornoModel();
                	retorno.setCodigo(CodigoTimeoutEnum.READ_UNTIL_TIMEOUT.getCodigo());
                	retorno.setRetorno(sbAux.toString());
                	return retorno;
                }
                ch = (char) n;
                if(IatConstants.DEBUG) {
					System.out.print(ch);
				}
            }
    	} catch (Exception e) {
    		ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
    		
    		if(IatConstants.DISCONNECT_TELNET_SOCKET) {
        		try {
					fecharSessaoTelnet();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
        	}
    	}

    	RetornoModel retorno = new RetornoModel();
    	retorno.setCodigo(CodigoTimeoutEnum.ERRO_INTERNO.getCodigo());
    	retorno.setRetorno("");
    	return retorno;
    }
    
    
    /**
     * 
     * @param pattern
     * @param infoProcedimento
     * @return
     */
    public String readUntilAvailableSleepDeLeitura(String[] pattern, String infoProcedimento) {
    	
    	if(telnet == null || !telnet.isAvailable()) {
    		ExceptionLogger.recordDisk("readUntilAvailableSleepDeLeitura: Socket client disconnected ou null", infoProcedimento);
            return null;
    	}
    	
    	try {
            StringBuilder sb = new StringBuilder();
            int n = -1;
            char ch;

            try {
                n = in.read();
                Thread.sleep(TELNET_SLEEP_5);
            } catch (SocketTimeoutException e) {
            	ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
            	
            	if(IatConstants.DISCONNECT_TELNET_SOCKET) {
            		fecharSessaoTelnet();
            	}
            	
                return null;
            }
            ch = (char) n;
            if(IatConstants.DEBUG) {
				System.out.print(ch);
			}

            while (n != -1) {
            	
                sb.append(ch);                	
            	for(int c = 0; c < pattern.length; c++) {                		
            		if (sb.toString().endsWith(pattern[c])) {
            			Thread.sleep(TELNET_SLEEP_50);
            			while(in.available() > 0) {
                            Thread.sleep(TELNET_SLEEP_5);
            				ch = (char) in.read();
            				sb.append(ch);
            				if(IatConstants.DEBUG) {
            					System.out.print(ch);
            				}
            			}
            			return sb.toString();
                    }
            	}    
             
                try {
                    n = in.read();
                    Thread.sleep(TELNET_SLEEP_5);
                } catch (SocketTimeoutException e) {
                	if(sb != null && !sb.toString().isEmpty()) {
                		infoProcedimento += "<br/><br/><br/><strong>SimpleTelnet -> readUntilAvailable(String[] pattern, String infoProcedimento) -> sb.toString()</strong><br/>" + sb.toString();
                	}
                	ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
                	
                	if(IatConstants.DISCONNECT_TELNET_SOCKET) {
                		fecharSessaoTelnet();
                	}
                	
                    return null;
                }
                ch = (char) n;
                if(IatConstants.DEBUG) {
					System.out.print(ch);
				}
            }
    	} catch (Exception e) {
    		ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
    		
    		if(IatConstants.DISCONNECT_TELNET_SOCKET) {
        		try {
					fecharSessaoTelnet();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
        	}
    		
            return null;
    	}

    	return null;
    }
    
    /**
     * Funcao que recebe o retorno do comando ate que encontre a String de parada
     * @param pattern
     * @return
     */
    public String readUntilAvailable(String[] pattern, String infoProcedimento, boolean enviarEmail) {
    	
    	if(telnet == null || !telnet.isAvailable()) {
    		ExceptionLogger.recordDisk("readUntilAvailable: Socket client disconnected ou null", infoProcedimento);
            return null;
    	}
    	
    	try {
            StringBuilder sb = new StringBuilder();
            int n = -1;
            char ch;

            try {
                n = in.read();
            } catch (SocketTimeoutException e) {
            	if(enviarEmail) {
            		ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
                }
            	
            	if(IatConstants.DISCONNECT_TELNET_SOCKET) {
            		fecharSessaoTelnet();
            	}
            	
            	return null;
            }
            ch = (char) n;
            if(IatConstants.DEBUG) {
				System.out.print(ch);
			}

            while (n != -1) {
            	
                sb.append(ch);                	
            	for(int c = 0; c < pattern.length; c++) {                		
            		if (sb.toString().endsWith(pattern[c])) {
            			Thread.sleep(TELNET_SLEEP_50);
            			while(in.available() > 0) {
            				Thread.sleep(TELNET_SLEEP_5);
            				ch = (char) in.read();
            				sb.append(ch);
            				if(IatConstants.DEBUG) {
            					System.out.print(ch);
            				}
            			}
            			return sb.toString();
                    }
            	}    
             
                try {
                    n = in.read();
                } catch (SocketTimeoutException e) {
                	
                	if(sb != null && !sb.toString().isEmpty()) {
                		infoProcedimento += "<br/><br/><br/><strong>SimpleTelnet -> readUntilAvailable(String[] pattern, String infoProcedimento) -> sb.toString()</strong><br/>" + sb.toString();
                	}
                	
                	if(enviarEmail) {
                		ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
	                }
                	
                	if(IatConstants.DISCONNECT_TELNET_SOCKET) {
                		fecharSessaoTelnet();
                	}
                	
                	return null;                		
                }
                ch = (char) n;
                if(IatConstants.DEBUG) {
					System.out.print(ch);
				}
            }
    	} catch (Exception e) {
    		ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
    		
    		if(IatConstants.DISCONNECT_TELNET_SOCKET) {
        		try {
					fecharSessaoTelnet();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
        	}
    		
            return null;
    	}

    	return null;
    }
    
    /**
     * Funcao que recebe o retorno do comando
     * @param pattern
     * @return
     */
    public String readUntilAvailableMore(String[] pattern, String patternMore, String comandoMore, String infoProcedimento) {
    	
    	if(telnet == null || !telnet.isAvailable()) {
    		ExceptionLogger.recordDisk("readUntilAvailableMore: Socket client disconnected ou null", infoProcedimento);
            return null;
    	}
    	try {
            StringBuilder sb = new StringBuilder();
            int n = -1;
            char ch;

            try {
                n = in.read();
            } catch (SocketTimeoutException e) {
            	ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
            	
            	if(IatConstants.DISCONNECT_TELNET_SOCKET) {
            		fecharSessaoTelnet();
            	}
            	
                return null;
            }
            ch = (char) n;
            if(IatConstants.DEBUG) {
				System.out.print(ch);
			}

            while (n != -1) {            	
            	sb.append(ch);   
            	if(StringUtils.endsWithIgnoreCase(sb.toString(), patternMore)) {
            		if(comandoMore.equals(CTRLC)) {
            			write((char)3);
            		} else if(comandoMore.equals(SPACE)) {
            			write((char)32);
            		} else if(comandoMore.equals(BACKSPACE)) {
            			write((char)8);
            		} else if(comandoMore.equals(TAB)) {
            			write((char)9);
            		} else {
            			write(comandoMore);
            		}
        		} else {
	            	for(int c = 0; c < pattern.length; c++) {
	            		if (sb.toString().endsWith(pattern[c])) {
	            			Thread.sleep(TELNET_SLEEP_50);
	            			while(in.available() > 0) {
	            				Thread.sleep(TELNET_SLEEP_5);
	            				ch = (char) in.read();
	            	            sb.append(ch);
	            	            
	            	            if(IatConstants.DEBUG) {
	            					System.out.print(ch);
	            				}
	            	            
	            	            if(StringUtils.endsWithIgnoreCase(sb.toString(), patternMore)) {
	        	            		if(comandoMore.equals(CTRLC)) {
	        	            			write((char)3);
	        	            		} else if(comandoMore.equals(SPACE)) {
	        	            			write((char)32);
	        	            		} else if(comandoMore.equals(BACKSPACE)) {
	        	            			write((char)8);
	        	            		} else if(comandoMore.equals(TAB)) {
	        	            			write((char)9);
	        	            		} else {
	        	            			write(comandoMore);
	        	            		}
	        	            		Thread.sleep(TELNET_SLEEP_1000);
	        	        		}
	            			}
	            			
                			// Tratando o retorno
	            			String[] linhasRetorno = sb.toString().split("\\\n");
	            			sb = new StringBuilder();
	            			for (int i = 0; i < linhasRetorno.length; i++) {
	            				if(!StringUtils.containsIgnoreCase(linhasRetorno[i], patternMore)) {
	            					sb.append(linhasRetorno[i]);
	            				}
	            			}
	            			return sb.toString();
	                    }
	            	}    
        		}
            	
                try {
                    n = in.read();
                } catch (SocketTimeoutException e) {
                	if(sb != null && !sb.toString().isEmpty()) {
                		infoProcedimento += "<br/><br/><br/><strong>SimpleTelnet -> readUntilAvailableMore(String[] pattern, String patternMore, String comandoMore, String infoProcedimento) -> sb.toString()</strong><br/>" + sb.toString();
                	}
                	ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
                	
                	if(IatConstants.DISCONNECT_TELNET_SOCKET) {
                		fecharSessaoTelnet();
                	}
                	
                    return null;
                }
                ch = (char) n;
                if(IatConstants.DEBUG) {
					System.out.print(ch);
				}
            }
    	} catch (Exception e) {
    		ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
    		
    		if(IatConstants.DISCONNECT_TELNET_SOCKET) {
        		try {
					fecharSessaoTelnet();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
        	}
    		
            return null;
    	}

    	return null;
    }
    
    /**
     * Funcao que recebe o retorno do comando
     * @param pattern
     * @return
     */
    public String readUntilAvailableMore(String[] pattern, String[] patternMore, String comandoMore, String infoProcedimento) {
    	
    	if(telnet == null || !telnet.isAvailable()) {
    		ExceptionLogger.recordDisk("readUntilAvailableMore: Socket client disconnected ou null", infoProcedimento);
            return null;
    	}
    	
    	try {
            StringBuilder sb = new StringBuilder();
            int n = -1;
            char ch;

            try {
                n = in.read();
            } catch (SocketTimeoutException e) {
            	ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
            	
            	if(IatConstants.DISCONNECT_TELNET_SOCKET) {
            		fecharSessaoTelnet();
            	}
            	
                return null;
            }
            ch = (char) n;
            if(IatConstants.DEBUG) {
				System.out.print(ch);
			}

            while (n != -1) {            	
            	sb.append(ch);
            	int habilita = 0;
            	for (int c = 0; c < patternMore.length; c++) {
            		if(StringUtils.endsWithIgnoreCase(sb.toString(), patternMore[c])) {
            			habilita = 1;
            			break;
            		}
            	}
            	if(habilita == 1) {
            		if(comandoMore.equals(CTRLC)) {
            			write((char)3);
            		} else if(comandoMore.equals(SPACE)) {
            			write((char)32);
            		} else if(comandoMore.equals(BACKSPACE)) {
            			write((char)8);
            		} else if(comandoMore.equals(TAB)) {
            			write((char)9);
            		} else {
            			write(comandoMore);
            		}
        		} else {
	            	for(int c = 0; c < pattern.length; c++) {
	            		if (sb.toString().endsWith(pattern[c])) {
	            			Thread.sleep(TELNET_SLEEP_50);
	            			while(in.available() > 0) {
	            				Thread.sleep(TELNET_SLEEP_5);
	            				ch = (char) in.read();
	        	            	sb.append(ch);
	        	            	if(IatConstants.DEBUG) {
	            					System.out.print(ch);
	            				}
	            			}
	            			// Tratando o retorno
	            			String[] linhasRetorno = sb.toString().split("\\\n");
	            			sb = new StringBuilder();
	            			for (int i = 0; i < linhasRetorno.length; i++) {
	            				for (int c1 = 0; c1 < patternMore.length; c1++) {
	            					if(!StringUtils.containsIgnoreCase(linhasRetorno[i], patternMore[c1])) {
		            					sb.append(linhasRetorno[i]);
		            					break;
		            				}
	            				}	
	            			}
	            			return sb.toString();
	                    }
	            	}    
        		}
            	
                try {
                    n = in.read();
                } catch (SocketTimeoutException e) {
                	if(sb != null && !sb.toString().isEmpty()) {
                		infoProcedimento += "<br/><br/><br/><strong>SimpleTelnet -> readUntilAvailableMore(String[] pattern, String patternMore, String comandoMore, String infoProcedimento) -> sb.toString()</strong><br/>" + sb.toString();
                	}
                	ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
                	
                	if(IatConstants.DISCONNECT_TELNET_SOCKET) {
                		fecharSessaoTelnet();
                	}
                	
                    return null;
                }
                ch = (char) n;
                if(IatConstants.DEBUG) {
					System.out.print(ch);
				}
            }
    	} catch (Exception e) {
    		ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
    		
    		if(IatConstants.DISCONNECT_TELNET_SOCKET) {
        		try {
					fecharSessaoTelnet();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
        	}
    		
            return null;
    	}

    	return null;
    }
    
    /**
     * Funcao que recebe o retorno do comando
     * @param pattern
     * @return
     */
    public String readUntilAvailableMoreOltHuawei(String[] pattern, String[] patternMore, String comandoMore, String infoProcedimento) {
    	
    	if(telnet == null || !telnet.isAvailable()) {
    		ExceptionLogger.recordDisk("readUntilAvailableMoreOltHuawei: Socket client disconnected ou null", infoProcedimento);
            return null;
    	}
    	
    	try {
            StringBuilder sb = new StringBuilder();
            int n = -1;
            char ch;

            try {
                n = in.read();
            } catch (SocketTimeoutException e) {
            	ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
            	
            	if(IatConstants.DISCONNECT_TELNET_SOCKET) {
            		fecharSessaoTelnet();
            	}
            	
                return null;
            }
            ch = (char) n;
            if(IatConstants.DEBUG) {
				System.out.print(ch);
			}

            while (n != -1) {            	
            	sb.append(ch);
            	int habilita = 0;
            	for (int c = 0; c < patternMore.length; c++) {
            		if(StringUtils.endsWithIgnoreCase(sb.toString(), patternMore[c])) {
            			habilita = 1;
            			break;
            		}
            	}
            	if(habilita == 1) {
            		if(comandoMore.equals(CTRLC)) {
            			write((char)3);
            		} else if(comandoMore.equals(SPACE)) {
            			write((char)32);
            		} else if(comandoMore.equals(BACKSPACE)) {
            			write((char)8);
            		} else if(comandoMore.equals(TAB)) {
            			write((char)9);
            		} else {
            			write(comandoMore);
            		}
        		} else {
	            	for(int c = 0; c < pattern.length; c++) {
	            		if (sb.toString().endsWith(pattern[c])) {
	            			Thread.sleep(TELNET_SLEEP_50);
	            			while(in.available() > 0) {
	            				Thread.sleep(TELNET_SLEEP_5);
	            				ch = (char) in.read();
	        	            	sb.append(ch);
	        	            	if(IatConstants.DEBUG) {
	            					System.out.print(ch);
	            				}
	            			}
	            			// Tratando o retorno
	            			String[] linhasRetorno = sb.toString().split("\\\n");
	            			sb = new StringBuilder();
	            			for (int i = 0; i < linhasRetorno.length; i++) {
	            				for (int c1 = 0; c1 < patternMore.length; c1++) {
	            					if(StringUtils.containsIgnoreCase(linhasRetorno[i], patternMore[c1])) {
	            						String aux = StringUtils.replaceIgnoreCase(linhasRetorno[i], patternMore[c1], "");
	            						aux = StringHelper.removerCodigoCores(aux);
	            						aux = "\n  " + aux.trim();
	            						sb.append(aux);
		            					break;
		            				} else {
		            					sb.append(linhasRetorno[i]);
		            				}
	            				}	
	            			}
	            			return sb.toString();
	                    }
	            	}    
        		}
            	
                try {
                    n = in.read();
                } catch (SocketTimeoutException e) {
                	if(sb != null && !sb.toString().isEmpty()) {
                		infoProcedimento += "<br/><br/><br/><strong>SimpleTelnet -> readUntilAvailableMore(String[] pattern, String patternMore, String comandoMore, String infoProcedimento) -> sb.toString()</strong><br/>" + sb.toString();
                	}
                	ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
                	
                	if(IatConstants.DISCONNECT_TELNET_SOCKET) {
                		fecharSessaoTelnet();
                	}
                	
                    return null;
                }
                ch = (char) n;
                if(IatConstants.DEBUG) {
					System.out.print(ch);
				}
            }
    	} catch (Exception e) {
    		ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
    		
    		if(IatConstants.DISCONNECT_TELNET_SOCKET) {
        		try {
					fecharSessaoTelnet();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
        	}
    		
            return null;
    	}

    	return null;
    }
    
    /**
     * Funcao que recebe o retorno do comando ate que encontre a String de parada
     * @param pattern
     * @return
     */
    public String readUntilAvailableParadaRepetida(String[] pattern, int qtdVezesEncontrouParada, String[] patternErro, String infoProcedimento) {
    	
    	if(telnet == null || !telnet.isAvailable()) {
    		ExceptionLogger.recordDisk("readUntilAvailableParadaRepetida: Socket client disconnected ou null", infoProcedimento);
            return null;
    	}
    	
    	try {
            StringBuilder sb = new StringBuilder();
            int n = -1, cont = 0;
            char ch;

            try {
                n = in.read();
            } catch (SocketTimeoutException e) {
            	ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
            	
            	if(IatConstants.DISCONNECT_TELNET_SOCKET) {
            		fecharSessaoTelnet();
            	}
            	
                return null;
            }
            ch = (char) n;
            if(IatConstants.DEBUG) {
				System.out.print(ch);
			}

            while (n != -1) {            	
                sb.append(ch);  	
            	// Buscando os caracteres de parada
                for(int c = 0; c < pattern.length; c++) {                		
            		if (sb.toString().endsWith(pattern[c])) {
            			cont++;
            			if(cont == qtdVezesEncontrouParada) {
            				Thread.sleep(TELNET_SLEEP_50);
	            			while(in.available() > 0) {
	            				Thread.sleep(TELNET_SLEEP_5);
	            				ch = (char) in.read();
	            				sb.append(ch);
	            				if(IatConstants.DEBUG) {
	            					System.out.print(ch);
	            				}
	            			}
	            			return sb.toString();
            			} else {
            				
            				Thread.sleep(TELNET_SLEEP_1000);
            				if(in.available() > 0) {
            					break;
            				} else {
            					return sb.toString();
            				}
            			}
                    }
            	}            	
            	// Verificando se apareceu o Pattern erro
    			for(int c1 = 0; c1 < patternErro.length; c1++) {
					if (sb.toString().contains(patternErro[c1])) {
						Thread.sleep(TELNET_SLEEP_50);
            			while(in.available() > 0) {
            				Thread.sleep(TELNET_SLEEP_5);
            				ch = (char) in.read();
            				sb.append(ch);
            				if(IatConstants.DEBUG) {
            					System.out.print(ch);
            				}
            			}
            			return sb.toString();
					}
				}
             
                try {
                    n = in.read();
                } catch (SocketTimeoutException e) {
                	
                	if(sb != null && !sb.toString().isEmpty()) {
                		return sb.toString();
                	}
                	
                	if(sb != null && !sb.toString().isEmpty()) {
                		infoProcedimento += "<br/><br/><br/><strong>SimpleTelnet -> readUntilAvailableParadaRepetida(String[] pattern, int qtdVezesEncontrouParada, String[] patternErro, String infoProcedimento) -> sb.toString()</strong><br/>" + sb.toString();
                	}
                	ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
                	
                	if(IatConstants.DISCONNECT_TELNET_SOCKET) {
                		fecharSessaoTelnet();
                	}
                	
                    return null;
                }
                ch = (char) n;
                if(IatConstants.DEBUG) {
					System.out.print(ch);
				}
            }
    	} catch (Exception e) {
    		ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
    		
    		if(IatConstants.DISCONNECT_TELNET_SOCKET) {
        		try {
					fecharSessaoTelnet();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
        	}
    		
            return null;
    	}

    	return null;
    }
    
    /**
     * Funcao que recebe o retorno do comando ate que encontre a String de parada
     * @param pattern
     * @return
     */
    public String readUntilAvailableParadaRepetidaMore(String[] pattern, int qtdVezesEncontrouParada, String[] patternErro, String infoProcedimento, String[] patternMore, String comandoMore) {
    	
    	if(telnet == null || !telnet.isAvailable()) {
    		ExceptionLogger.recordDisk("readUntilAvailableParadaRepetidaMore: Socket client disconnected ou null", infoProcedimento);
            return null;
    	}
    	
    	try {
            StringBuilder sb = new StringBuilder();
            int n = -1, cont = 0;
            char ch;

            try {
                n = in.read();
            } catch (SocketTimeoutException e) {
            	ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
            	
            	if(IatConstants.DISCONNECT_TELNET_SOCKET) {
            		fecharSessaoTelnet();
            	}
            	
                return null;
            }
            ch = (char) n;
            if(IatConstants.DEBUG) {
				System.out.print(ch);
			}

            while (n != -1) {            	
                sb.append(ch);
                
                int habilita = 0;
            	for (int c = 0; c < patternMore.length; c++) {
            		if(StringUtils.endsWithIgnoreCase(sb.toString(), patternMore[c])) {
            			habilita = 1;
            			break;
            		}
            	}
            	if(habilita == 1) {
            		if(comandoMore.equals(CTRLC)) {
            			write((char)3);
            		} else if(comandoMore.equals(SPACE)) {
            			write((char)32);
            		} else if(comandoMore.equals(BACKSPACE)) {
            			write((char)8);
            		} else if(comandoMore.equals(TAB)) {
            			write((char)9);
            		} else {
            			write(comandoMore);
            		}
        		} else {
        			// Buscando os caracteres de parada
	                for(int c = 0; c < pattern.length; c++) {                		
	            		if (sb.toString().endsWith(pattern[c])) {
	            			cont++;
	            			if(cont == qtdVezesEncontrouParada) {
	            				Thread.sleep(TELNET_SLEEP_50);
		            			while(in.available() > 0) {
		            				Thread.sleep(TELNET_SLEEP_5);
		            				ch = (char) in.read();
		            				sb.append(ch);
		            				if(IatConstants.DEBUG) {
		            					System.out.print(ch);
		            				}
		            			}
		            			return sb.toString();
	            			} else {
	            				
	            				Thread.sleep(TELNET_SLEEP_1000);
	            				if(in.available() > 0) {
	            					break;
	            				} else {
	            					return sb.toString();
	            				}
	            			}
	                    }
	            	}            	
	            	// Verificando se apareceu o Pattern erro
	    			for(int c1 = 0; c1 < patternErro.length; c1++) {
						if (sb.toString().contains(patternErro[c1])) {
							Thread.sleep(TELNET_SLEEP_50);
	            			while(in.available() > 0) {
	            				Thread.sleep(TELNET_SLEEP_5);
	            				ch = (char) in.read();
	            				sb.append(ch);
	            				if(IatConstants.DEBUG) {
	            					System.out.print(ch);
	            				}
	            			}
	            			return sb.toString();
						}
					}
        		}
             
                try {
                    n = in.read();
                } catch (SocketTimeoutException e) {
                	
                	if(sb != null && !sb.toString().isEmpty()) {
                		return sb.toString();
                	}
                	
                	if(sb != null && !sb.toString().isEmpty()) {
                		infoProcedimento += "<br/><br/><br/><strong>SimpleTelnet -> readUntilAvailableParadaRepetida(String[] pattern, int qtdVezesEncontrouParada, String[] patternErro, String infoProcedimento) -> sb.toString()</strong><br/>" + sb.toString();
                	}
                	ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
                	
                	if(IatConstants.DISCONNECT_TELNET_SOCKET) {
                		fecharSessaoTelnet();
                	}
                	
                    return null;
                }
                ch = (char) n;
                if(IatConstants.DEBUG) {
					System.out.print(ch);
				}
            }
    	} catch (Exception e) {
    		ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
    		
    		if(IatConstants.DISCONNECT_TELNET_SOCKET) {
        		try {
					fecharSessaoTelnet();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
        	}
    		
            return null;
    	}

    	return null;
    }
    
    public String pegarLogOnline() {

    	String infoProcedimento = SimpleTelnet.class.getName() + "<br/>Procedimento: pegarLogOnline";
    	
    	try {
    		String log = "";
			// Pega semáforo
    		semaphore.acquire();
    		if (logOnline != null) {
		    	log = logOnline.toString();
		    	// Limpar logOnline
		        // Set the StringBuilder length to zero, so it will remove all its content
		    	logOnline.setLength(0);
		    	logOnline.trimToSize(); // Clear the storage by deleting old buffer
    		}
		    semaphore.release();
	    	// libera semáforo
	    	
	    	return log;
	    	
		} catch (InterruptedException e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
			return "";
		}
    }
    
    public void setarLogOnline(String log) {

    	String infoProcedimento = SimpleTelnet.class.getName() + "<br/>Procedimento: setarLogOnline";
    	
    	try {
			// Pega semáforo
    		semaphore.acquire();
	    	logOnline.append(log);
	    	semaphore.release();
	    	// libera semáforo
	    	
		} catch (InterruptedException e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e), infoProcedimento);
		}
    }    
	
	/**
	 * Metodos Sets and Gets 
	 */

	public StringBuilder getLogTotal() {
		return logTotal;
	}

	public void setLogTotal(StringBuilder logTotal) {
		this.logTotal = logTotal;
	}

	public boolean isEnviarCtrlC() {
		return enviarCtrlC;
	}

	public void setEnviarCtrlC(boolean enviarCtrlC) {
		this.enviarCtrlC = enviarCtrlC;
	}

	public StringBuilder getLogOnline() {
		return logOnline;
	}

	public void setLogOnline(StringBuilder logOnline) {
		this.logOnline = logOnline;
	}

	public Semaphore getSemaphore() {
		return semaphore;
	}

	public void setSemaphore(Semaphore semaphore) {
		this.semaphore = semaphore;
	}

	public TelnetClient getTelnet() {
		return telnet;
	}

	public void setTelnet(TelnetClient telnet) {
		this.telnet = telnet;
	}

	public boolean isSocBloqueouUsuario() {
		return socBloqueouUsuario;
	}

	public void setSocBloqueouUsuario(boolean socBloqueouUsuario) {
		this.socBloqueouUsuario = socBloqueouUsuario;
	}

	public boolean isEnviouCtrlC() {
		return enviouCtrlC;
	}

	public void setEnviouCtrlC(boolean enviouCtrlC) {
		this.enviouCtrlC = enviouCtrlC;
	}
	
}
