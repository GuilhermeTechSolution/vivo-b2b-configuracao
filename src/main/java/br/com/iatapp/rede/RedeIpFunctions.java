package br.com.iatapp.rede;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import br.com.iatapp.dao.AtivacaoDao;
import br.com.iatapp.enums.CodigoErroAcessoEquipamentoEnum;
import br.com.iatapp.enums.GlobalStrEnum;
import br.com.iatapp.enums.ModelosEquipamentosEnum;
import br.com.iatapp.helper.StringHelper;

/**
 * 
 * @author ottap
 *
 */
public class RedeIpFunctions extends SimpleTelnet {

	private StringBuilder log;
	private StringBuilder logExtra;
	private int codigoErroAcessoEquipamento;
	
	public RedeIpFunctions() {
		setLog(new StringBuilder());
	}
	
	/**
	 * Limpando o Log
	 */
	public void limpaLog(String infoProcedimento) {
		setLog(null);
		setLog(new StringBuilder());
		
		// Guardando a primeira linha no Log
		String[] patternParada = {"~]$", ":~$", "#", ">", ":/]$", "bash-3.00$"};
    	String comando = "";
    	String retorno = enviarComandoAvailable(comando, patternParada, infoProcedimento);
    	if(StringUtils.isBlank(retorno)) {
    		return;
    	}
		log.append(StringHelper.removerCodigoCores(retorno));
	}
	
	/**
	 * ENTER para separar comandos
	 */
	public void enter(String infoProcedimento) {
		
		log.append(StringHelper.lineSeparatorNew());
		// Guardando a primeira linha no Log
		String[] patternParada = {"~]$", ":~$", "#", ">", ":/]$", "bash-3.00$"};
    	String comando = "";
    	String retorno = enviarComandoAvailable(comando, patternParada, infoProcedimento);
    	if(StringUtils.isBlank(retorno)) {
    		return;
    	}
		log.append(StringHelper.removerCodigoCores(retorno));
	}
	
    public boolean conectarEquipamento(String equipamento, String usuario, String senha, String infoProcedimento) {
    	
    	setCodigoErroAcessoEquipamento(0);
    	
    	String comando = "";
    	boolean retornoConexao = false;
    		
		// zerando a flag
    	setCodigoErroAcessoEquipamento(0);
    	
    	// Tentando a conexao via SSH
    	//comando = "ssh " + equipamento;  
    	comando = "ssh " + usuario + "@" + equipamento;
    	retornoConexao = abrirConexaoEquipamento(comando, equipamento, usuario, senha, infoProcedimento);
        return retornoConexao;
    }
    
    public boolean conectarEquipamentoForConfig(String equipamento, String infoProcedimento) {
		
		// zerando a flag
		setCodigoErroAcessoEquipamento(0);
		
//		String usuario = "s_roboconfig_iat_cf";
//		String senha = "aWp@R3uyFqH5";
//		String usuario = "80717224";
//		String senha = "Gonc@lve$2020";		
		
		try {
			// pegando o usuario e senha no banco
			JSONObject jsonSenha = new AtivacaoDao().buscarSenhaConfPe();
			String usuario = jsonSenha.getString("login");
			String senha = jsonSenha.getString("senha");
			
			// Tentando a conexao via SSH
			String comando = "ssh " + usuario + "@" + equipamento;
			boolean retornoConexao = abrirConexaoEquipamento(comando, equipamento, usuario, senha, infoProcedimento);
			return retornoConexao;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
    
    public boolean conectarEquipamentoSwitch(String equipamento, String usuario, String senha, String infoProcedimento, JSONObject jsonAux) {
    	
    	setCodigoErroAcessoEquipamento(0);
    	
    	// Tentando a conexao via Telnet
    	String comando = "telnet " + equipamento;
    	
    	boolean retornoConexao = abrirConexaoEquipamentoSwitch(comando, equipamento, usuario, senha, infoProcedimento, jsonAux);
    	if(retornoConexao) 
    		return true;
    	
    	// zerando a flag
    	setCodigoErroAcessoEquipamento(0);
    	
    	// Tentando a conexao via SSH
    	
    	//comando = "ssh " + equipamento;  
    	comando = "ssh " + usuario + "@" + equipamento;
    	retornoConexao = abrirConexaoEquipamentoSwitch(comando, equipamento, usuario, senha, infoProcedimento, jsonAux);
        return retornoConexao;      	
    }
    
    public JSONArray retornaListaSenhasSwitch() {
    	
    	int index = 0;
    	JSONArray array = new JSONArray();
    	
    	JSONObject json = new JSONObject();
    	json.put("login", "suporte");
    	json.put("senha", "sup0rt3_m3tr0");
    	array.put(index, json);
    	index++;
    	
    	json = new JSONObject();
    	json.put("login", "admin");
    	json.put("senha", "d4t4c0m#$%");
    	array.put(index, json);
    	index++;
    	
    	json = new JSONObject();
    	json.put("login", "suporte");
    	json.put("senha", "sup0rt3_osoe");
    	array.put(index, json);
    	index++;
    	
    	json = new JSONObject();
    	json.put("login", "admin001");
    	json.put("senha", "c0r1@nt#$%");
    	array.put(index, json);
    	index++;
    	
    	json = new JSONObject();
    	json.put("login", "admin");
    	json.put("senha", "dtc123!@#");
    	array.put(index, json);
    	index++;
    	    	
    	json = new JSONObject();
    	json.put("login", "admin");
    	json.put("senha", "admin");
    	array.put(index, json);
    	index++;
    	
    	json = new JSONObject();
    	json.put("login", "admin");
    	json.put("senha", "cisco");
    	array.put(index, json);
    	index++;
    	
    	json = new JSONObject();
    	json.put("login", "admin");
    	json.put("senha", "osoe123!@#");
    	array.put(index, json);
    	index++;
    	
    	json = new JSONObject();
    	json.put("login", "admin");
    	json.put("senha", "dtcoes#@!");
    	array.put(index, json);
    	index++;
    	
    	json = new JSONObject();
    	json.put("login", "admin");
    	json.put("senha", "Tnx12014");
    	array.put(index, json);
    	index++;
    	
    	return array;
    }
    
    public JSONArray retornaListaSenhasSwitchCoriant() {
    	
    	int index = 0;
    	JSONArray array = new JSONArray();
    	
    	JSONObject json = new JSONObject();
    	json.put("login", "root");
    	json.put("senha", "sup0rt3_m3tr0");
    	array.put(index, json);
    	index++;
    	
    	json = new JSONObject();
    	json.put("login", "user_root");
    	json.put("senha", "sup0rt3_m3tr0");
    	array.put(index, json);
    	index++;
    	
    	json = new JSONObject();
    	json.put("login", "root");
    	json.put("senha", "sup0rt3_osoe");
    	array.put(index, json);
    	index++;
    	
    	json = new JSONObject();
    	json.put("login", "admin001");
    	json.put("senha", "c0r1@nt#$%");
    	array.put(index, json);
    	index++;
    	
    	return array;
    }
    
    public boolean abrirConexaoEquipamento(String comando, String equipamento, String usuario, String senha, String infoProcedimento) {
    	
    	// Logica para enviar Ctrl+C caso o equipamento nao responda
    	setEnviarCtrlC(true);
    	// Criando a Thread de monitoracao
    	Thread threadMonitora = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(CUSTOM_TIMEOUT);
					if(isEnviarCtrlC()) {
						write((char)3);
						// Setando o codigo do erro
						setCodigoErroAcessoEquipamento(CodigoErroAcessoEquipamentoEnum.EQUIPAMENTO_NAO_RESPONDE.getCodigo());
					}
				} catch (InterruptedException e) {
				}
					
			}
		});
    	threadMonitora.start();
    	
    	String retorno = enviarComandoAvailable(comando, new String[] {"assword:", "sername:", "ogin:", "~]$", ":~$", ":/]$", "0$", "(yes/no)?", "(yes/no/[fingerprint])? ", "(yes/no/[fingerprint])?"}, infoProcedimento);
    	setEnviarCtrlC(false);
    	if(threadMonitora.isAlive()) {
    		threadMonitora.interrupt();
    	}    	
        if (StringUtils.isBlank(retorno)) {
        	// Setando o codigo do erro
			setCodigoErroAcessoEquipamento(CodigoErroAcessoEquipamentoEnum.EQUIPAMENTO_NAO_RESPONDE.getCodigo());
        	return false;
        } 
        // Seta o Log
        log.append(StringHelper.removerCodigoCores(retorno));
        
        if(StringUtils.containsIgnoreCase(retorno, "known_hosts")) {
    		comando = "ssh -o UserKnownHostsFile=/dev/null " + usuario + "@" + equipamento;
    		retorno = enviarComandoAvailable(comando, new String[] {"assword:", "sername:", "ogin:", "~]$", ":~$", ":/]$", "(yes/no)?", "(yes/no/[fingerprint])? ", "(yes/no/[fingerprint])?", "0$"}, infoProcedimento);
        	if (StringUtils.isBlank(retorno))
            	return false;        	
        	// Seta o Log
            log.append(StringHelper.removerCodigoCores(retorno));
            
    	}
        
        if(retorno.contains("(yes/no)?") || retorno.contains("(yes/no/[fingerprint])?")) {
    		retorno = enviarComandoAvailable("yes", new String[] {"assword:", "sername:", "ogin:", "~]$", ":~$", ":/]$", "(yes/no)?", "(yes/no/[fingerprint])? ", "(yes/no/[fingerprint])?", "0$"}, infoProcedimento);
        	if (StringUtils.isBlank(retorno))
            	return false;
        	
        	// Seta o Log
            log.append(StringHelper.removerCodigoCores(retorno));
    	}
        
        
        // Verificando se o deu Connection refused
        if(StringUtils.containsIgnoreCase(retorno, "Connection refused")) {
        	// Setando o codigo do erro
			setCodigoErroAcessoEquipamento(CodigoErroAcessoEquipamentoEnum.EQUIPAMENTO_NAO_RESPONDE.getCodigo());
        	return false;
        }
        
        // Verificando se o hostname esta errado
        if(StringUtils.containsIgnoreCase(retorno, "Connection closed")) {
        	// Setando o codigo do erro
			setCodigoErroAcessoEquipamento(CodigoErroAcessoEquipamentoEnum.EQUIPAMENTO_NAO_RESPONDE.getCodigo());
        	return false;
        }
        
        // Verificando se o hostname esta errado
        if(StringUtils.containsIgnoreCase(retorno, "Temporary failure in name resolution")) {
        	// Setando o codigo do erro
			setCodigoErroAcessoEquipamento(CodigoErroAcessoEquipamentoEnum.IP_HOSTNAME_INVALIDO.getCodigo());
        	return false;
        }
        
        if(retorno.contains("(yes/no)?")) {
    		retorno = enviarComandoAvailable("yes", new String[] {"assword:", "sername:", "ogin:", "~]$", ":~$", ":/]$", "(yes/no)?", "0$"}, infoProcedimento);
        	if (StringUtils.isBlank(retorno))
            	return false;
        	// Seta o Log
	        log.append(StringHelper.removerCodigoCores(retorno));
    	}	        
        
        // Verificando se pediu usuario
        if(StringUtils.containsIgnoreCase(retorno, "sername:") || StringUtils.containsIgnoreCase(retorno, "ogin:")) {       
        	// Escrevendo o usuário
        	retorno = enviarComandoAvailable(usuario, new String[] {"assword:", "sername:", "ogin:", "~]$", ":~$", ":/]$", "0$"}, infoProcedimento);
        	if (StringUtils.isBlank(retorno)) {
        		// Setando o codigo do erro
    			setCodigoErroAcessoEquipamento(CodigoErroAcessoEquipamentoEnum.LOGIN_SENHA_INVALIDO.getCodigo());
            	return false;
            }
        	// Seta o Log
            log.append(StringHelper.removerCodigoCores(retorno));
            
            // Verificando se o hostname esta errado
            if(StringUtils.containsIgnoreCase(retorno, "Connection closed")) {
            	// Setando o codigo do erro
    			setCodigoErroAcessoEquipamento(CodigoErroAcessoEquipamentoEnum.EQUIPAMENTO_NAO_RESPONDE.getCodigo());
            	return false;
            }
            
        }
        
        // Verificando se pediu senha
        if(StringUtils.containsIgnoreCase(retorno, "assword:")) {
        	// Escrevendo a senha
        	retorno = enviarComandoAvailable(senha, new String[] {"assword:", "sername:", "ogin:", equipamento+"#", equipamento+">", "#", ">", "]", "0$"}, infoProcedimento);
        	if (StringUtils.isBlank(retorno)) {
        		// Setando o codigo do erro
    			setCodigoErroAcessoEquipamento(CodigoErroAcessoEquipamentoEnum.LOGIN_SENHA_INVALIDO.getCodigo());
            	return false;
            }
        	// Seta o Log
            log.append(StringHelper.removerCodigoCores(retorno));
            
        	// Tratando o retorno
        	if(StringUtils.containsIgnoreCase(retorno, "sername:") || 
        			StringUtils.containsIgnoreCase(retorno, "assword:") || 
        			(StringUtils.containsIgnoreCase(retorno, "ogin:") && !StringUtils.containsIgnoreCase(retorno, "Last login:")) || 
        			StringUtils.containsIgnoreCase(retorno, "incorrect") ||
        			StringUtils.containsIgnoreCase(retorno, "Authentication Failed") ||
        			StringUtils.containsIgnoreCase(retorno, "Login failed") ||
        			StringUtils.containsIgnoreCase(retorno, "Login invalid")) {
        		// Setando o codigo do erro
        		if(StringUtils.containsIgnoreCase(retorno, "Authentication Failed")) {
        			setCodigoErroAcessoEquipamento(CodigoErroAcessoEquipamentoEnum.FALHA_AUTENTICACAO.getCodigo());
        		} else {
        			setCodigoErroAcessoEquipamento(CodigoErroAcessoEquipamentoEnum.LOGIN_SENHA_INVALIDO.getCodigo());
        		}
    			        		
    			int c = 0;
    			while (!StringUtils.containsIgnoreCase(retorno, "~]$") && 
    					!StringUtils.containsIgnoreCase(retorno, ":~$") && 
    					!StringUtils.containsIgnoreCase(retorno, ":/]$") && 
    					!StringUtils.containsIgnoreCase(retorno, "0$") &&
    					c < 7) {
    				// Escrevendo a senha
    	        	retorno = enviarComandoAvailable("a", new String[] {"assword:", "name:", "ogin:", "~]$",":~$", ":/]$", "0$"}, infoProcedimento);
    	        	if (StringUtils.isBlank(retorno)) {
    	            	return false;
    	            }
    	        	// Adicionando o retorno no Log
    	        	log.append(StringHelper.removerCodigoCores(retorno));
    	        	c++;
    			}
    			
    			return false;
        	}        	
        	
        	if(retorno.contains(">") || retorno.contains("#") || retorno.contains(equipamento)) { 
        		return true;
        	}  
        	
        	// Setando o codigo do erro
			setCodigoErroAcessoEquipamento(CodigoErroAcessoEquipamentoEnum.LOGIN_SENHA_INVALIDO.getCodigo());
        	return false;        	
        }
        
        // Setando o codigo do erro
		setCodigoErroAcessoEquipamento(CodigoErroAcessoEquipamentoEnum.EQUIPAMENTO_NAO_RESPONDE.getCodigo());
		
    	return false;    
    }
    
    public boolean abrirConexaoEquipamentoSwitch(String comando, String equipamento, String usuario, String senha, String infoProcedimento, JSONObject jsonAux) {
    	
    	// Logica para enviar Ctrl+C caso o equipamento nao responda
    	setEnviarCtrlC(true);
    	// Criando a Thread de monitoracao
    	Thread threadMonitora = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(CUSTOM_TIMEOUT);
					if(isEnviarCtrlC()) {
						write((char)3);
						// Setando o codigo do erro
						setCodigoErroAcessoEquipamento(CodigoErroAcessoEquipamentoEnum.EQUIPAMENTO_NAO_RESPONDE.getCodigo());
					}
				} catch (InterruptedException e) {
				}
					
			}
		});
    	threadMonitora.start();
    	
    	String retorno = enviarComandoAvailable(comando, new String[] {"assword:", "name:", "ogin:", "~]$", ":~$", ":/]$", "0$", "(yes/no)?", "y(es)/n(o)", "(yes/no/[fingerprint])? ", "(yes/no/[fingerprint])?"}, infoProcedimento);
    	setEnviarCtrlC(false);
    	if(threadMonitora.isAlive()) {
    		threadMonitora.interrupt();
    	}    	
        if (StringUtils.isBlank(retorno)) {
        	// Setando o codigo do erro
			setCodigoErroAcessoEquipamento(CodigoErroAcessoEquipamentoEnum.EQUIPAMENTO_NAO_RESPONDE.getCodigo());
        	return false;
        } 
        // Seta o Log
        log.append(StringHelper.removerCodigoCores(retorno));
        
        if(StringUtils.containsIgnoreCase(retorno, "known_hosts")) {
    		comando = "ssh -o UserKnownHostsFile=/dev/null " + usuario + "@" + equipamento;
    		retorno = enviarComandoAvailable(comando, new String[] {"assword:", "name:", "ogin:", "~]$", ":~$", ":/]$", "(yes/no)?", "0$"}, infoProcedimento);
        	if (StringUtils.isBlank(retorno))
            	return false;        	
        	// Seta o Log
            log.append(StringHelper.removerCodigoCores(retorno));
            
    	}
        
        if(retorno.contains("(yes/no)?") || retorno.contains("(yes/no/[fingerprint])?")) {
    		retorno = enviarComandoAvailable("yes", new String[] {"assword:", "name:", "ogin:", "~]$", ":~$", ":/]$", "(yes/no)?", "(yes/no/[fingerprint])? ", "(yes/no/[fingerprint])?", "0$"}, infoProcedimento);
        	if (StringUtils.isBlank(retorno))
            	return false;
        	
        	// Seta o Log
            log.append(StringHelper.removerCodigoCores(retorno));
    	}
        
        
        // Verificando se o deu Connection refused
        if(StringUtils.containsIgnoreCase(retorno, "Connection refused")) {
        	// Setando o codigo do erro
			setCodigoErroAcessoEquipamento(CodigoErroAcessoEquipamentoEnum.EQUIPAMENTO_NAO_RESPONDE.getCodigo());
        	return false;
        }
        
        // Verificando se o hostname esta errado
        if(StringUtils.containsIgnoreCase(retorno, "Connection closed")) {
        	// Setando o codigo do erro
			setCodigoErroAcessoEquipamento(CodigoErroAcessoEquipamentoEnum.EQUIPAMENTO_NAO_RESPONDE.getCodigo());
        	return false;
        }
        
        // Verificando se o hostname esta errado
        if(StringUtils.containsIgnoreCase(retorno, "Temporary failure in name resolution")) {
        	// Setando o codigo do erro
			setCodigoErroAcessoEquipamento(CodigoErroAcessoEquipamentoEnum.IP_HOSTNAME_INVALIDO.getCodigo());
        	return false;
        }
        
        if(retorno.contains("(yes/no)?")) {
    		retorno = enviarComandoAvailable("yes", new String[] {"assword:", "name:", "ogin:", "~]$", ":~$", ":/]$", "(yes/no)?", "0$"}, infoProcedimento);
        	if (StringUtils.isBlank(retorno))
            	return false;
        	// Seta o Log
	        log.append(StringHelper.removerCodigoCores(retorno));
    	}
        
        if(retorno.contains("y(es)/n(o)")) {
    		retorno = enviarComandoAvailableSemEnter("y", new String[] {"assword:", "name:", "ogin:", "~]$", ":~$", ":/]$", "(yes/no)?", "0$"}, infoProcedimento);
        	if (StringUtils.isBlank(retorno))
            	return false;
        	// Seta o Log
	        log.append(StringHelper.removerCodigoCores(retorno));
	        
	        if(StringUtils.containsIgnoreCase(retorno, "Coriant"))
	        	jsonAux.put("idModelo", ModelosEquipamentosEnum.CORIANT.getCodigo());
    	}
        
        // Verificando se pediu usuario
        if(StringUtils.containsIgnoreCase(retorno, "name:") || StringUtils.containsIgnoreCase(retorno, "ogin:")) {       
        	// Escrevendo o usuário
        	retorno = enviarComandoAvailable(usuario, new String[] {"assword:", "name:", "ogin:", "~]$", ":~$", ":/]$", "0$"}, infoProcedimento);
        	if (StringUtils.isBlank(retorno)) {
        		// Setando o codigo do erro
    			setCodigoErroAcessoEquipamento(CodigoErroAcessoEquipamentoEnum.LOGIN_SENHA_INVALIDO.getCodigo());
            	return false;
            }
        	// Seta o Log
            log.append(StringHelper.removerCodigoCores(retorno));
            
            // Verificando se o hostname esta errado
            if(StringUtils.containsIgnoreCase(retorno, "Connection closed")) {
            	// Setando o codigo do erro
    			setCodigoErroAcessoEquipamento(CodigoErroAcessoEquipamentoEnum.EQUIPAMENTO_NAO_RESPONDE.getCodigo());
            	return false;
            }
            
        }
        
        // Verificando se pediu senha
        if(StringUtils.containsIgnoreCase(retorno, "assword:")) {
        	// Escrevendo a senha
        	retorno = enviarComandoAvailable(senha, new String[] {"assword:", "name:", "ogin:", equipamento+"#", equipamento+">", "#", ">", "]", "0$"}, infoProcedimento);
        	if (StringUtils.isBlank(retorno)) {
        		// Setando o codigo do erro
    			setCodigoErroAcessoEquipamento(CodigoErroAcessoEquipamentoEnum.LOGIN_SENHA_INVALIDO.getCodigo());
            	return false;
            }
        	// Seta o Log
            log.append(StringHelper.removerCodigoCores(retorno));
            
        	// Tratando o retorno
        	if(StringUtils.containsIgnoreCase(retorno, "name:") || 
        			StringUtils.containsIgnoreCase(retorno, "assword:") || 
        			(StringUtils.containsIgnoreCase(retorno, "ogin:") && !StringUtils.containsIgnoreCase(retorno, "Last login:")) || 
        			StringUtils.containsIgnoreCase(retorno, "incorrect") ||
        			StringUtils.containsIgnoreCase(retorno, "Authentication Failed") ||
        			StringUtils.containsIgnoreCase(retorno, "Login failed") ||
        			StringUtils.containsIgnoreCase(retorno, "Login invalid")) {
        		// Setando o codigo do erro
        		if(StringUtils.containsIgnoreCase(retorno, "Authentication Failed")) {
        			setCodigoErroAcessoEquipamento(CodigoErroAcessoEquipamentoEnum.FALHA_AUTENTICACAO.getCodigo());
        		} else {
        			setCodigoErroAcessoEquipamento(CodigoErroAcessoEquipamentoEnum.LOGIN_SENHA_INVALIDO.getCodigo());
        		}
    			        		
    			int c = 0;
    			while (!StringUtils.containsIgnoreCase(retorno, "~]$") && 
    					!StringUtils.containsIgnoreCase(retorno, ":~$") && 
    					!StringUtils.containsIgnoreCase(retorno, ":/]$") && 
    					!StringUtils.containsIgnoreCase(retorno, "0$") &&
    					c < 7) {
    				// Escrevendo a senha
    	        	retorno = enviarComandoAvailable("a", new String[] {"assword:", "name:", "ogin:", "~]$",":~$", ":/]$", "0$"}, infoProcedimento);
    	        	if (StringUtils.isBlank(retorno)) {
    	            	return false;
    	            }
    	        	// Adicionando o retorno no Log
    	        	log.append(StringHelper.removerCodigoCores(retorno));
    	        	c++;
    			}
    			
    			return false;
        	}        	
        	
        	if(retorno.contains(">") || retorno.contains("#") || retorno.contains(equipamento)) { 
        		return true;
        	}  
        	
        	// Setando o codigo do erro
			setCodigoErroAcessoEquipamento(CodigoErroAcessoEquipamentoEnum.LOGIN_SENHA_INVALIDO.getCodigo());
        	return false;        	
        }
        
        // Setando o codigo do erro
		setCodigoErroAcessoEquipamento(CodigoErroAcessoEquipamentoEnum.EQUIPAMENTO_NAO_RESPONDE.getCodigo());
		
    	return false;    
    }
	
    /**
     * 
     * @param patternParada
     * @return
     */
    public boolean fecharConexaoEquipamento(String infoProcedimento) {
    	
    	String[] patternParada = {"~]$",":~$","#",">", ":/]$", "0$"};
    	
    	// quit
    	String retorno = enviarComandoAvailable("quit", patternParada, infoProcedimento);
    	if(StringUtils.isBlank(retorno)) {
    		return false;
    	}
    	// Seta log
    	log.append(retorno);
    	
    	if(!StringUtils.containsIgnoreCase(retorno, "error") &&
    			!StringUtils.containsIgnoreCase(retorno, "invalid") &&
    			!StringUtils.containsIgnoreCase(retorno, "incorrect") &&
    			!StringUtils.containsIgnoreCase(retorno, "bad ") &&
    			!StringUtils.containsIgnoreCase(retorno, "Authentication Failed") &&
    			!StringUtils.containsIgnoreCase(retorno, "Login failed") &&
    			!StringUtils.containsIgnoreCase(retorno, "Login invalid")) {
    		return true;
    	}
    	
    	// logout
    	retorno = enviarComandoAvailable("logout", patternParada, infoProcedimento);
    	if(StringUtils.isBlank(retorno)) {
    		return false;
    	}
    	// Seta log
    	log.append(retorno);
    	
    	if(!StringUtils.containsIgnoreCase(retorno, "error") &&
    			!StringUtils.containsIgnoreCase(retorno, "invalid") &&
    			!StringUtils.containsIgnoreCase(retorno, "incorrect") &&
    			!StringUtils.containsIgnoreCase(retorno, "bad ") &&
    			!StringUtils.containsIgnoreCase(retorno, "Authentication Failed") &&
    			!StringUtils.containsIgnoreCase(retorno, "Login failed") &&
    			!StringUtils.containsIgnoreCase(retorno, "Login invalid")) {
    		return true;
    	}
    	
    	// exit
    	retorno = enviarComandoAvailable("exit", patternParada, infoProcedimento);
    	if(StringUtils.isBlank(retorno)) {
    		return false;
    	}
    	// Seta log
    	log.append(retorno);
    	
    	if(!StringUtils.containsIgnoreCase(retorno, "error") &&
    			!StringUtils.containsIgnoreCase(retorno, "invalid") &&
    			!StringUtils.containsIgnoreCase(retorno, "incorrect") &&
    			!StringUtils.containsIgnoreCase(retorno, "bad ") &&
    			!StringUtils.containsIgnoreCase(retorno, "Authentication Failed") &&
    			!StringUtils.containsIgnoreCase(retorno, "Login failed") &&
    			!StringUtils.containsIgnoreCase(retorno, "Login invalid")) {
    		return true;
    	}
    	
    	return false;
    }
    
    /**
     * 
     * @param patternParada
     * @return
     */
    public boolean fecharConexaoEquipamento(String infoProcedimento, int idModeloEquipamento) {
    	
    	if(idModeloEquipamento == ModelosEquipamentosEnum.CORIANT.getCodigo()) {
    		String[] patternParada = {"~]$",":~$","#",">", ":/]$", "0$"};
	    	
	    	// quit
	    	String retorno = enviarComandoAvailableCoriant("quit", patternParada, infoProcedimento);
	    	if(StringUtils.isBlank(retorno)) {
	    		return false;
	    	}
	    	// Seta log
	    	log.append(StringHelper.addNewComand("quit", retorno));
	    	
	    	if(!StringUtils.containsIgnoreCase(retorno, "error") &&
	    			!StringUtils.containsIgnoreCase(retorno, "invalid") &&
	    			!StringUtils.containsIgnoreCase(retorno, "incorrect") &&
	    			!StringUtils.containsIgnoreCase(retorno, "bad ") &&
	    			!StringUtils.containsIgnoreCase(retorno, "Authentication Failed") &&
	    			!StringUtils.containsIgnoreCase(retorno, "Login failed") &&
	    			!StringUtils.containsIgnoreCase(retorno, "Login invalid")) {
	    		return true;
	    	}
	    	
	    	// logout
	    	retorno = enviarComandoAvailableCoriant("logout", patternParada, infoProcedimento);
	    	if(StringUtils.isBlank(retorno)) {
	    		return false;
	    	}
	    	// Seta log
	    	log.append(StringHelper.addNewComand("logout", retorno));
	    	
	    	if(!StringUtils.containsIgnoreCase(retorno, "error") &&
	    			!StringUtils.containsIgnoreCase(retorno, "invalid") &&
	    			!StringUtils.containsIgnoreCase(retorno, "incorrect") &&
	    			!StringUtils.containsIgnoreCase(retorno, "bad ") &&
	    			!StringUtils.containsIgnoreCase(retorno, "Authentication Failed") &&
	    			!StringUtils.containsIgnoreCase(retorno, "Login failed") &&
	    			!StringUtils.containsIgnoreCase(retorno, "Login invalid")) {
	    		return true;
	    	}
	    	
	    	// exit
	    	retorno = enviarComandoAvailableCoriant("exit", patternParada, infoProcedimento);
	    	if(StringUtils.isBlank(retorno)) {
	    		return false;
	    	}
	    	// Seta log
	    	log.append(StringHelper.addNewComand("exit", retorno));
	    	
	    	if(!StringUtils.containsIgnoreCase(retorno, "error") &&
	    			!StringUtils.containsIgnoreCase(retorno, "invalid") &&
	    			!StringUtils.containsIgnoreCase(retorno, "incorrect") &&
	    			!StringUtils.containsIgnoreCase(retorno, "bad ") &&
	    			!StringUtils.containsIgnoreCase(retorno, "Authentication Failed") &&
	    			!StringUtils.containsIgnoreCase(retorno, "Login failed") &&
	    			!StringUtils.containsIgnoreCase(retorno, "Login invalid")) {
	    		return true;
	    	}
	    	
	    	return false;
    	} else {
    		String[] patternParada = {"~]$",":~$","#",">", ":/]$", "0$"};
	    	
	    	// quit
	    	String retorno = enviarComandoAvailable("quit", patternParada, infoProcedimento);
	    	if(StringUtils.isBlank(retorno)) {
	    		return false;
	    	}
	    	// Seta log
	    	log.append(retorno);
	    	
	    	if(!StringUtils.containsIgnoreCase(retorno, "error") &&
	    			!StringUtils.containsIgnoreCase(retorno, "invalid") &&
	    			!StringUtils.containsIgnoreCase(retorno, "incorrect") &&
	    			!StringUtils.containsIgnoreCase(retorno, "bad ") &&
	    			!StringUtils.containsIgnoreCase(retorno, "Authentication Failed") &&
	    			!StringUtils.containsIgnoreCase(retorno, "Login failed") &&
	    			!StringUtils.containsIgnoreCase(retorno, "Login invalid")) {
	    		return true;
	    	}
	    	
	    	// logout
	    	retorno = enviarComandoAvailable("logout", patternParada, infoProcedimento);
	    	if(StringUtils.isBlank(retorno)) {
	    		return false;
	    	}
	    	// Seta log
	    	log.append(retorno);
	    	
	    	if(!StringUtils.containsIgnoreCase(retorno, "error") &&
	    			!StringUtils.containsIgnoreCase(retorno, "invalid") &&
	    			!StringUtils.containsIgnoreCase(retorno, "incorrect") &&
	    			!StringUtils.containsIgnoreCase(retorno, "bad ") &&
	    			!StringUtils.containsIgnoreCase(retorno, "Authentication Failed") &&
	    			!StringUtils.containsIgnoreCase(retorno, "Login failed") &&
	    			!StringUtils.containsIgnoreCase(retorno, "Login invalid")) {
	    		return true;
	    	}
	    	
	    	// exit
	    	retorno = enviarComandoAvailable("exit", patternParada, infoProcedimento);
	    	if(StringUtils.isBlank(retorno)) {
	    		return false;
	    	}
	    	// Seta log
	    	log.append(retorno);
	    	
	    	if(!StringUtils.containsIgnoreCase(retorno, "error") &&
	    			!StringUtils.containsIgnoreCase(retorno, "invalid") &&
	    			!StringUtils.containsIgnoreCase(retorno, "incorrect") &&
	    			!StringUtils.containsIgnoreCase(retorno, "bad ") &&
	    			!StringUtils.containsIgnoreCase(retorno, "Authentication Failed") &&
	    			!StringUtils.containsIgnoreCase(retorno, "Login failed") &&
	    			!StringUtils.containsIgnoreCase(retorno, "Login invalid")) {
	    		return true;
	    	}
	    	
	    	return false;
    	}
    }

	/**
	 * 
	 */
	public void retornaRedeIp(String infoProcedimento) {
		String[] patternParada = {"~]$",":~$","#",">", ":/]$"};
    	String comando = "";
    	String retorno = enviarComandoAvailable(comando, patternParada, infoProcedimento);
    	if(StringUtils.isBlank(retorno)) {
    		return;
    	}
    	if(!retorno.contains("$")) {
    		fecharConexaoEquipamento(infoProcedimento);
    	}
	}
	
	/**
	 * Limpando o Log Extra
	 */
	public void limpaLogExtra(String infoProcedimento) {
		setLogExtra(null);
		setLogExtra(new StringBuilder());
		
		// Guardando a primeira linha no Log
		String[] patternParada = {"~]$",":~$","#",">", ":/]$"};
		String retorno = enviarComandoAvailable("", patternParada, infoProcedimento);
		if(StringUtils.isBlank(retorno)) {
			return;
		}
		logExtra.append(StringHelper.removerCodigoCores(retorno));
	}
	
	
	/**
	 * BreakLine / Enter
	 */
	public String breakLine(String infoProcedimento) {
		
		// Guardando a primeira linha no Log
		String[] patternParada = {"~]$",":~$","#",">", ":/]$"};
		String retorno = enviarComandoAvailable("", patternParada, infoProcedimento);
		if(StringUtils.isBlank(retorno)) {
			return "";
		}
		return StringHelper.removerCodigoCores(retorno);
	}
	
	/**
     * Metodo que retorna o modelo do PE
     * 	1- Cisco XE
     * 	2- Cisco XR
     * 	3- Juniper
     * 	4- Huawei
     * @return
     */
    public int retornaModeloEquipamento(String equipamento, String infoProcedimento, String logAcessouEquipamento) {
    	
    	String comando = "", retorno = "";
    	// Mandando um 'Enter' para saber se é Juniper
    	retorno = enviarComandoAvailable(comando, new String[] {equipamento+"#", equipamento+">"}, infoProcedimento);
    	if(StringUtils.isBlank(retorno)) {
    		return 0;
    	} 
    	// Seta Log
    	log.append(retorno);
    	
    	if(retorno.contains("{master}") || (StringUtils.isNotBlank(logAcessouEquipamento) && StringUtils.containsIgnoreCase(logAcessouEquipamento, " JUNOS "))) {
    		// PE Juniper
    		// Enviando o comando para não precisar enviar 'Enter' se a resposta do equipamento for muito longa
    		comando = "set cli screen-length 0";
        	retorno = enviarComandoAvailable(comando, new String[] {equipamento+"#", equipamento+">"}, infoProcedimento);
        	if(StringUtils.isBlank(retorno)) {
        		return 0;
        	} 
        	// Seta Log
        	log.append(retorno);
        	
        	// Enviando o 'show version' para salvar no Log
        	comando = "show version | match software";
        	retorno = enviarComandoAvailableMore(comando, new String[] {equipamento+"#", equipamento+">"}, new String[] {"-- more --", "--More--", "-- More --"}, "", infoProcedimento);
        	if(StringUtils.isBlank(retorno)) {
        		return 0;
        	} 
        	// Seta Log
        	log.append(retorno);
        	
        	return ModelosEquipamentosEnum.JUNIPER.getCodigo();
    	}
    	    	
    	// Enviando o primeiro comando para tentar descobrir o equipamento
    	comando = "screen-length disable";
    	retorno = enviarComandoAvailable(comando, new String[] {equipamento+"#", equipamento+">"}, infoProcedimento);
    	// Tratando o retorno
    	if(StringUtils.isBlank(retorno)) {
    		return 0;
    	}  
    	// Seta Log
    	log.append(retorno);
    	
    	// Brocade
    	if(StringUtils.containsIgnoreCase(retorno, "syntax error: ")) {
    		comando = "show version";
	    	retorno = enviarComandoAvailable(comando, new String[] {equipamento+"#", equipamento+">"}, infoProcedimento);
	    	if(StringUtils.isBlank(retorno)) {
	    		return 0;
	    	}
	    	// Seta Log
	    	log.append(retorno);
	    	
	    	if(StringUtils.containsIgnoreCase(retorno, "Brocade"))
	    		return ModelosEquipamentosEnum.BROCADE.getCodigo();
	    }
    	
    	// Versão antiga Huawei
    	if(StringUtils.containsIgnoreCase(retorno, "Error: Wrong parameter found") ||
    			StringUtils.containsIgnoreCase(retorno, "Unrecognized command found")) {
    		comando = "screen-length 0 temporary";
	    	retorno = enviarComandoAvailable(comando, new String[] {equipamento+"#", equipamento+">"}, infoProcedimento);
	    	if(StringUtils.isBlank(retorno)) {
	    		return 0;
	    	}
	    	// Seta Log
	    	log.append(retorno);
    		// Huawei
	    	return verificaEquipamentoHuawei(equipamento, infoProcedimento);
	    }
    	
    	// CISCO
    	if(StringUtils.containsIgnoreCase(retorno, "Invalid input detected")) {
    		configForCiscoEscapeCharacterToCtrlC(equipamento, infoProcedimento);
    		return verificaEquimamentoCisco(equipamento, infoProcedimento);
    	} 
    	
    	// ALCATEL
    	if(StringUtils.containsIgnoreCase(retorno, "Error: Bad command")) {
    		
    		comando = "environment no more";
        	retorno = enviarComandoAvailableMore(comando, new String[] {equipamento+"#", equipamento+">"}, new String[] {"-- more --", "--More--", "-- More --"}, "", infoProcedimento);
        	if(StringUtils.isBlank(retorno)) {
        		return 0;
        	}
        	// Seta Log
        	log.append(retorno);
    		
    		comando = "show version";
        	retorno = enviarComandoAvailableMore(comando, new String[] {equipamento+"#", equipamento+">"}, new String[] {"-- more --", "--More--", "-- More --"}, "", infoProcedimento);
        	if(StringUtils.isBlank(retorno)) {
        		return 0;
        	}
        	// Seta Log
        	log.append(retorno);
        	
        	if(StringUtils.containsIgnoreCase(retorno, "ALCATEL")) {
        		return ModelosEquipamentosEnum.ALCATEL.getCodigo();
        	} if(StringUtils.containsIgnoreCase(retorno, "Nokia")) {
        		return ModelosEquipamentosEnum.NOKIA.getCodigo();
        	}
    	} else {
    		// Huawei
	    	return verificaEquipamentoHuawei(equipamento, infoProcedimento);
    	}
    	return 0;
    }
    
    public int retornaModeloEquipamentoSwitch(String equipamento, String infoProcedimento) {
    	
    	String comando = "", retorno = "";
    	// Mandando um 'Enter' para saber se é Juniper
    	retorno = enviarComandoAvailable(comando, new String[] {equipamento+"#", equipamento+">"}, infoProcedimento);
    	if(StringUtils.isBlank(retorno)) {
    		return 0;
    	} 
    	// Seta Log
    	log.append(retorno);
    	
    	if(retorno.contains("{master}")) {
    		// PE Juniper
    		// Enviando o comando para não precisar enviar 'Enter' se a resposta do equipamento for muito longa
    		comando = "set cli screen-length 0";
        	retorno = enviarComandoAvailable(comando, new String[] {equipamento+"#", equipamento+">"}, infoProcedimento);
        	if(StringUtils.isBlank(retorno)) {
        		return 0;
        	} 
        	// Seta Log
        	log.append(retorno);
        	
        	// Enviando o 'show version' para salvar no Log
        	comando = "show version | match software";
        	retorno = enviarComandoAvailableMore(comando, new String[] {equipamento+"#", equipamento+">"}, new String[] {"-- more --", "--More--", "-- More --"}, "", infoProcedimento);
        	if(StringUtils.isBlank(retorno)) {
        		return 0;
        	} 
        	// Seta Log
        	log.append(retorno);
        	
        	return ModelosEquipamentosEnum.JUNIPER.getCodigo();
    	}
    	
    	// Enviando o primeiro comando para tentar descobrir o equipamento
    	comando = "show system";
    	retorno = enviarComandoAvailable(comando, new String[] {equipamento+"#", equipamento+">"}, infoProcedimento);
    	// Tratando o retorno
    	if(StringUtils.isBlank(retorno))
    		return 0;
    	// Seta Log
    	log.append(retorno);
    	
    	if(StringUtils.containsIgnoreCase(retorno, "Model:")) 
    		return ModelosEquipamentosEnum.DATACOM.getCodigo();
    	else {
    		
    		comando = "show platform";
        	retorno = enviarComandoAvailable(comando, new String[] {equipamento+"#", equipamento+">"}, infoProcedimento);
        	// Tratando o retorno
        	if(StringUtils.isBlank(retorno))
        		return 0;
        	// Seta Log
        	log.append(retorno);
        	
    		if(StringUtils.containsIgnoreCase(retorno, "DM4050")) 
    			return ModelosEquipamentosEnum.DATACOM_DM4050.getCodigo();
    	}
    	
    	
    	// Enviando o primeiro comando para tentar descobrir o equipamento
    	comando = "show sw-version";
    	retorno = enviarComandoAvailable(comando, new String[] {equipamento+"#", equipamento+">"}, infoProcedimento);
    	// Tratando o retorno
    	if(StringUtils.isBlank(retorno)) {
    		return 0;
    	}  
    	// Seta Log
    	log.append(retorno);
    	
    	if(StringUtils.containsIgnoreCase(retorno, "system up-time"))
    		return ModelosEquipamentosEnum.CORIANT.getCodigo();
    	
    	// Enviando o primeiro comando para tentar descobrir o equipamento
    	comando = "screen-length disable";
    	retorno = enviarComandoAvailable(comando, new String[] {equipamento+"#", equipamento+">"}, infoProcedimento);
    	// Tratando o retorno
    	if(StringUtils.isBlank(retorno)) {
    		return 0;
    	}  
    	// Seta Log
    	log.append(retorno);
    	
    	// Versão antiga Huawei
    	if(StringUtils.containsIgnoreCase(retorno, "Error: Wrong parameter found") 
    			|| StringUtils.containsIgnoreCase(retorno, "Unrecognized command found")) {
    		comando = "screen-length 0 temporary";
	    	retorno = enviarComandoAvailable(comando, new String[] {equipamento+"#", equipamento+">"}, infoProcedimento);
	    	if(StringUtils.isBlank(retorno)) {
	    		return 0;
	    	}
	    	// Seta Log
	    	log.append(retorno);
    		// Huawei
	    	return verificaEquipamentoHuawei(equipamento, infoProcedimento);
	    }
    	
    	// CISCO
    	if(StringUtils.containsIgnoreCase(retorno, "Invalid input detected")) {
    		configForCiscoEscapeCharacterToCtrlC(equipamento, infoProcedimento);
    		return verificaEquimamentoCisco(equipamento, infoProcedimento);
    	} 
    	
    	// ALCATEL
    	if(StringUtils.containsIgnoreCase(retorno, "Error: Bad command")) {
    		
    		comando = "environment no more";
        	retorno = enviarComandoAvailableMore(comando, new String[] {equipamento+"#", equipamento+">"}, new String[] {"-- more --", "--More--", "-- More --"}, "", infoProcedimento);
        	if(StringUtils.isBlank(retorno)) {
        		return 0;
        	}
        	// Seta Log
        	log.append(retorno);
    		
    		comando = "show version";
        	retorno = enviarComandoAvailableMore(comando, new String[] {equipamento+"#", equipamento+">"}, new String[] {"-- more --", "--More--", "-- More --"}, "", infoProcedimento);
        	if(StringUtils.isBlank(retorno)) {
        		return 0;
        	}
        	// Seta Log
        	log.append(retorno);
        	
        	if(StringUtils.containsIgnoreCase(retorno, "ALCATEL")) {
        		return ModelosEquipamentosEnum.ALCATEL.getCodigo();
        	} if(StringUtils.containsIgnoreCase(retorno, "Nokia")) {
        		return ModelosEquipamentosEnum.NOKIA.getCodigo();
        	}
    	} else {
    		// Huawei
	    	return verificaEquipamentoHuawei(equipamento, infoProcedimento);
    	}
    	return 0;
    }
    
    /**
     * 
     * @return
     */
    private int verificaEquipamentoHuawei(String equipamento, String infoProcedimento) {
    	// Comando resumido
    	String comando = "display version | include Software";
    	String retorno = enviarComandoAvailableMore(comando, new String[] {equipamento+"#", equipamento+">"}, new String[] {"-- more --", "--More--", "-- More --"}, "", infoProcedimento);
    	if(StringUtils.isBlank(retorno)) {
    		return 0;
    	}
    	// Seta Log
    	log.append(retorno);
    	
    	if(StringUtils.containsIgnoreCase(retorno, "Huawei") ||
    			StringUtils.containsIgnoreCase(retorno, "H3C") || 
    			StringUtils.containsIgnoreCase(retorno, "3com") || 
    			StringUtils.containsIgnoreCase(retorno, "HP Comware Platform") ||
    			StringUtils.containsIgnoreCase(retorno, "HP Comware Software") ||
    			StringUtils.containsIgnoreCase(retorno, "HPE Comware Software") ||
    			StringUtils.containsIgnoreCase(retorno, "HPE Comware Platform")) {
    		return ModelosEquipamentosEnum.HUAWEI.getCodigo();
    	}
    	
    	// Comando geral
    	comando = "display version";
    	retorno = enviarComandoAvailableMore(comando, new String[] {equipamento+"#", equipamento+">"}, new String[] {"-- more --", "--More--", "-- More --"}, "", infoProcedimento);
    	if(StringUtils.isBlank(retorno)) {
    		return 0;
    	}
    	// Seta Log
    	log.append(retorno);
    	
    	if(StringUtils.containsIgnoreCase(retorno, "Huawei") ||
    			StringUtils.containsIgnoreCase(retorno, "H3C") ||
    			StringUtils.containsIgnoreCase(retorno, "3com") ||
    			StringUtils.containsIgnoreCase(retorno, "HP Comware Platform") ||
    			StringUtils.containsIgnoreCase(retorno, "HP Comware Software") ||
    			StringUtils.containsIgnoreCase(retorno, "HPE Comware Software") ||
    			StringUtils.containsIgnoreCase(retorno, "HPE Comware Platform")) {
    		return ModelosEquipamentosEnum.HUAWEI.getCodigo();
    	}    	
		return 0;
	}
    
    /**
     * 
     * @return
     */
    public int verificaEquimamentoCisco(String equimamento, String infoProcedimento) {
    	// Enviando o comando para nao pedir pra digitar 'Enter'

    	String comando = "terminal length 0";
    	String retorno = enviarComandoAvailable(comando, new String[] {equimamento+"#", equimamento+">"}, infoProcedimento);
    	if(StringUtils.isBlank(retorno)) {
    		return 0;
    	} 
    	// Seta Log
    	log.append(retorno);
    	
    	// Aumentando o width do terminal
    	comando = "terminal width 20";
    	retorno = enviarComandoAvailable(comando, new String[] {equimamento+"#", equimamento+">"}, infoProcedimento);
    	if(StringUtils.isBlank(retorno)) {
    		return 0;
    	} 
    	// Seta Log
    	log.append(retorno);
    	
    	if(!StringUtils.containsIgnoreCase(retorno, "Error:")) {
    		// Enviando o 'show version brief' para salvar no Log
        	comando = "show version brief";
        	retorno = enviarComandoAvailableMore(comando, new String[] {equimamento+"#",equimamento+">"}, new String[] {"-- more --", "--More--", "-- More --"}, "", infoProcedimento);
        	if(StringUtils.isBlank(retorno)) {
        		return 0;
        	} 
        	// Seta Log
        	log.append(retorno);
        	
        	// Enviando o 'show version' para salvar no Log
        	if(StringUtils.containsIgnoreCase(retorno, "Invalid input detected")) {
        		comando = "show version";
            	retorno = enviarComandoAvailableMore(comando, new String[] {equimamento+"#",equimamento+">"}, new String[] {"-- more --", "--More--", "-- More --"}, "", infoProcedimento);
            	if(StringUtils.isBlank(retorno)) {
            		return 0;
            	}
            	// Seta Log
            	log.append(retorno);
        	}
        	// Tratando o retorno
        	if(StringUtils.containsIgnoreCase(retorno, "Cisco IOS XR Software")) {
        		return ModelosEquipamentosEnum.CISCO_XR.getCodigo();
        	} else if(StringUtils.containsIgnoreCase(retorno, "Cisco")){
        		return ModelosEquipamentosEnum.CISCO_XE.getCodigo();
        	}
    	}    	
    	return 0;
    }
    
    public void configForCiscoEscapeCharacterToCtrlC(String equimamento, String infoProcedimento) {
    	
    	// Para configurar o escape-character (break) para CTRL+C da sessão ssh/telnet
    	String comando = "terminal escape-character 3";
    	enviarComandoAvailableSemAppendLogTotal(comando, new String[] {equimamento+"#", equimamento+">"}, infoProcedimento);
    }
    
    public void initialConfigForCisco(String equimamento, String infoProcedimento) {
    	
    	// Para configurar o escape-character (break) para CTRL+C da sessão ssh/telnet
    	String comando = "terminal escape-character 3";
    	enviarComandoAvailableSemAppendLogTotal(comando, new String[] {equimamento+"#", equimamento+">"}, infoProcedimento);
    	
    	// terminal length 0
		String retorno = enviarComandoAvailable("terminal length 0", new String[] {equimamento+"#", equimamento+">"}, infoProcedimento);		
		getLog().append(StringUtils.isNotBlank(retorno) ? retorno : "");
		
    	// terminal width 20
		retorno = enviarComandoAvailable("terminal width 20", new String[] {equimamento+"#", equimamento+">"}, infoProcedimento);		
		getLog().append(StringUtils.isNotBlank(retorno) ? retorno : "");
    }
    
    public void initialConfigForHuawei(String equimamento, String infoProcedimento) {    	
    	String retorno = enviarComandoAvailableSemAppendLogTotal("screen-length disable", new String[] {equimamento+"#", equimamento+">"}, infoProcedimento);
    	getLog().append(StringUtils.isNotBlank(retorno) ? retorno : "");
    	
    	retorno = enviarComandoAvailable("screen-length 0 temporary", new String[] {equimamento+"#", equimamento+">"}, infoProcedimento);		
		getLog().append(StringUtils.isNotBlank(retorno) ? retorno : "");
	}
    
    /**
	 * 
	 * @return
	 */
	public String getHostnameEquipamento(String infoProcedimento) {		
		// Pegando o hostname
		String retorno = enviarComandoAvailable("", new String[] {">","#","]"}, infoProcedimento);
		if(StringUtils.isBlank(retorno)) {
			return null;
		}
		
		String hostname = StringHelper.searchPattern(retorno, GlobalStrEnum.HOSTNAME_PATTERN.toString());
		if(StringUtils.isNotBlank(hostname)) 
			return hostname;		
		
		// Tratando o retorno
		String[] linhasRetorno = retorno.split("" + GlobalStrEnum.BREAK_LINE.toString() + "");
    	for (int c = 0; c < linhasRetorno.length; c++) {
    		if(linhasRetorno[c] != null && (linhasRetorno[c].contains(">") || linhasRetorno[c].contains("#")) ) {
    			hostname = linhasRetorno[c].trim().substring(0, linhasRetorno[c].trim().length()-1);
    			if(StringUtils.containsIgnoreCase(hostname, ":")) 
    				hostname = StringUtils.substringAfter(hostname, ":").trim();
    			
    			if(StringUtils.startsWith(hostname, "<")) 
    				hostname = hostname.replace("<", "");
    			
    			return hostname;
    		}
    	}
		return null;
	}
	
	public StringBuilder getLogExtra() {
		return logExtra;
	}

	public void setLogExtra(StringBuilder logExtra) {
		this.logExtra = logExtra;
	}

	public int getCodigoErroAcessoEquipamento() {
		return codigoErroAcessoEquipamento;
	}

	public void setCodigoErroAcessoEquipamento(int codigoErroAcessoEquipamento) {
		this.codigoErroAcessoEquipamento = codigoErroAcessoEquipamento;
	}

	public StringBuilder getLog() {
		return log;
	}

	public void setLog(StringBuilder log) {
		this.log = log;
	}

}
