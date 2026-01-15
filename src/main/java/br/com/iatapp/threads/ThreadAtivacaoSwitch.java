package br.com.iatapp.threads;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import br.com.iatapp.config.IatConstants;
import br.com.iatapp.dao.SenhasDao;
import br.com.iatapp.domain.ConfigSwitchIdDomain;
import br.com.iatapp.domain.ConfigSwitchLogDomain;
import br.com.iatapp.domain.ConfigSwitchResultadosDomain;
import br.com.iatapp.enums.CodigoServidoresEnum;
import br.com.iatapp.enums.TiposTestesEnum;
import br.com.iatapp.helper.RedeHelper;
import br.com.iatapp.helper.StringHelper;
import br.com.iatapp.logger.ExceptionLogger;
import br.com.iatapp.logger.ResultadoLogger;
import br.com.iatapp.model.UsuarioModel;
import br.com.iatapp.rede.AtivacaoSwitchFunctions;
import br.com.iatapp.rede.RedeIpFunctions;
import br.com.iatapp.repositories.ConfigSwitchIdRepository;
import br.com.iatapp.repositories.ConfigSwitchLogRepository;
import br.com.iatapp.repositories.ConfigSwitchResultadosRepository;
import br.com.iatapp.repositories.ConfigSwitchScriptRepository;

/**
 * 
 * @author ottap
 *
 */
public class ThreadAtivacaoSwitch extends Thread {

	HttpSession session;
	ConfigSwitchIdDomain configSwitchIdDomain;
	ConfigSwitchIdRepository configSwitchIdRepository;
	ConfigSwitchLogRepository configSwitchLogRepository;
	ConfigSwitchResultadosRepository configSwitchresultadosRepository;
	ConfigSwitchScriptRepository configSwitchScriptRepository;
	
	public ThreadAtivacaoSwitch(
			HttpSession session, 
			ConfigSwitchIdDomain configSwitchIdDomain, 
			ConfigSwitchIdRepository configSwitchIdRepository, 
			ConfigSwitchLogRepository configSwitchLogRepository,
			ConfigSwitchResultadosRepository configSwitchresultadosRepository,
			ConfigSwitchScriptRepository configSwitchScriptRepository
			) {
		this.session = session;
		this.configSwitchIdDomain = configSwitchIdDomain;
		this.configSwitchIdRepository = configSwitchIdRepository;
		this.configSwitchLogRepository = configSwitchLogRepository;
		this.configSwitchresultadosRepository = configSwitchresultadosRepository;
		this.configSwitchScriptRepository = configSwitchScriptRepository;
	}
	
	public void run() {
		
		// Zerando a Progress Bar
		configSwitchIdDomain.setValorProgressBar("5");
		// label etapa teste online
		configSwitchIdDomain.setEtapaTesteOnline("Conectando no servidor");
		
		// Criando o Objeto da rede
		RedeIpFunctions redeIpFunctions = new RedeIpFunctions();
		configSwitchIdDomain.setRedeIpFunctions(redeIpFunctions);
		
		// ------------------------------------------------------------
		// CRIANDO SESSAO TELNET
		// ------------------------------------------------------------
		
		try {
			if(!redeIpFunctions.abrirSessaoTelnet()) {
				ExceptionLogger.record("Telnet(Sem Exception)", 
						RedeHelper.retornaInfoProcedimento(ThreadAtivacaoSwitch.class.getName(), configSwitchIdDomain.getIdVantive(), "Erro sem Exception ao abrir sessão TELNET no início do teste."));
				
				configSwitchIdDomain.setValorProgressBar("100");
				// mensagem de erro ao conectar no servidor
				configSwitchIdDomain.setMensagemErroConexaoServidor("Erro de conexão com o servidor. Execute o teste novamente.");
				
				// Metodo para limpar o objeto da sessao
				verificaObjetoSessao();
				
				return;
			}
		} catch (Exception e) {
			
			e.printStackTrace();
			
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e), 
					RedeHelper.retornaInfoProcedimento(ThreadAtivacaoSwitch.class.getName(), configSwitchIdDomain.getIdVantive(), "Erro com Exception ao abrir sessão TELNET no início do teste."));
			
			configSwitchIdDomain.setValorProgressBar("100");
			// mensagem de erro ao conectar no servidor
			configSwitchIdDomain.setMensagemErroConexaoServidor("Erro de conexão com o servidor. Execute o teste novamente.");
			
			// Metodo para limpar o objeto da sessao
			verificaObjetoSessao();
			
			return;
		}
		
		if(IatConstants.codigoServidor == CodigoServidoresEnum.SERVIDOR_DEV.getCodigoServidor()) {
			
			// ------------------------------------------------------------
			// MAQUINA SALTO
			// ------------------------------------------------------------
			
			// Conectando na maquina de salto
			if(!redeIpFunctions.conectarMaquinaSalto()) {
				// Fechando a sessao telnet
				try {
					redeIpFunctions.fecharSessaoTelnet();
				} catch (IOException e) {
					ExceptionLogger.record(ExceptionUtils.getStackTrace(e), 
							RedeHelper.retornaInfoProcedimento(ThreadAtivacaoSwitch.class.getName(), configSwitchIdDomain.getIdVantive(), "Não conectou na máquina de Salto."));
				}

				configSwitchIdDomain.setValorProgressBar("100");
				// mensagem de erro ao conectar no servidor
				configSwitchIdDomain.setMensagemErroConexaoServidor("Erro de conexão com o servidor. Execute o teste novamente.");
				
				// Metodo para limpar o objeto da sessao
				verificaObjetoSessao();
				
				return;
			}		
		}
		
		
		
		// label etapa teste online
		configSwitchIdDomain.setEtapaTesteOnline("Conectando Rede IP");
		
		// ------------------------------------------------------------
		// CONECTANDO REDE IP
		// ------------------------------------------------------------
		
		// Buscando as sehas de CPE, RA, Rede IP
		UsuarioModel usuarioSenhas = null;
		try {
			usuarioSenhas = new SenhasDao().buscarSenhasUsuarioIaTRede();
		} catch (Exception e) {
			// Fechando a sessao telnet
			try {
				redeIpFunctions.fecharSessaoTelnet();
			} catch (IOException e1) {
				ExceptionLogger.record(ExceptionUtils.getStackTrace(e), 
						RedeHelper.retornaInfoProcedimento(ThreadAtivacaoSwitch.class.getName(), configSwitchIdDomain.getIdVantive(), "Não conectou na máquina de Salto."));
			}

			configSwitchIdDomain.setValorProgressBar("100");
			// mensagem de erro ao conectar no servidor
			configSwitchIdDomain.setMensagemErroConexaoServidor("Erro de conexão com o servidor. Execute o teste novamente.");
			
			// Metodo para limpar o objeto da sessao
			verificaObjetoSessao();
			
			return;
		}
		
		if(!redeIpFunctions.conectarRedeIp(usuarioSenhas.getLoginRedeIp(), usuarioSenhas.getSenhaRedeIp())) {
			
			// Fechando conexao
			redeIpFunctions.enviarComandoAvailable("exit", new String[] {"~]$",":~$", ":/]$"},
					RedeHelper.retornaInfoProcedimento(ThreadAtivacaoSwitch.class.getName(), configSwitchIdDomain.getIdVantive(), "Erro ao conectar na Rede IP 200.204.1.4."));
			
			// Fechando a sessao telnet
			try {
				redeIpFunctions.fecharSessaoTelnet();
			} catch (IOException e) {
				ExceptionLogger.record(ExceptionUtils.getStackTrace(e),
						RedeHelper.retornaInfoProcedimento(ThreadAtivacaoSwitch.class.getName(), configSwitchIdDomain.getIdVantive(), "Não conectou na Rede IP 200.204.1.4. Erro ao fechar a sessão TELNET."));
			}
			
			configSwitchIdDomain.setValorProgressBar("100");
			// mensagem de erro ao conectar no servidor
			configSwitchIdDomain.setMensagemErroConexaoServidor("Erro de conexão com o máquina de Gerência '200.204.1.4'.");
						
			// Metodo para limpar o objeto da sessao
			verificaObjetoSessao();
			
			return;
		}
		
		// Enter para aparecer o nome da Rede IP no inicio do Log
		redeIpFunctions.enviarComandoAvailable("", new String[] {"~]$",":~$", ":/]$"},
				RedeHelper.retornaInfoProcedimento(ThreadAtivacaoSwitch.class.getName(), configSwitchIdDomain.getIdVantive(), "Erro ao enviar ENTER para aparecer o nome da Rede IP 200.204.1.4."));		
		
		// ------------------------------------------------------------
		// CONFIGURACAO SWITCH
		// ------------------------------------------------------------
		
		AtivacaoSwitchFunctions ativacaoSwitchFunctions = new AtivacaoSwitchFunctions(redeIpFunctions, configSwitchIdDomain, configSwitchScriptRepository);
		ativacaoSwitchFunctions.iniciaProcedimentoConfiguracao();
		
		// ------------------------------------------------------------
		// FECHANDO REDE IP
		// ------------------------------------------------------------
		
		// label etapa teste online
		configSwitchIdDomain.setEtapaTesteOnline("Fechando conexão");
		
		redeIpFunctions.enviarComandoAvailableSemAppendLogTotal("exit", new String[] {"~]$",":~$",":/]$"},
				RedeHelper.retornaInfoProcedimento(ThreadAtivacaoSwitch.class.getName(), configSwitchIdDomain.getIdVantive(), "Fechando a conexão com a Rede IP."));
			
		// ------------------------------------------------------------
		// MAQUINA AMAZON
		// ------------------------------------------------------------
		
		if(IatConstants.codigoServidor == CodigoServidoresEnum.SERVIDOR_DEV.getCodigoServidor()) {			
			// ------------------------------------------------------------
			// FECHANDO MAQUINA SALTO
			// ------------------------------------------------------------
			
			redeIpFunctions.enviarComandoAvailableSemAppendLogTotal("exit", new String[] {"~]$",":~$",":/]$"},
					RedeHelper.retornaInfoProcedimento(ThreadAtivacaoSwitch.class.getName(), configSwitchIdDomain.getIdVantive(), "Fechando a conexão com a maquina de Salto."));
		}
		
		// ------------------------------------------------------------
		// FECHANDO SESSAO TELNET
		// ------------------------------------------------------------
		
		redeIpFunctions.enviarComandoAvailableSemAppendLogTotal("exit", new String[] {"~]$",":~$",":/]$"},
				RedeHelper.retornaInfoProcedimento(ThreadAtivacaoSwitch.class.getName(), configSwitchIdDomain.getIdVantive(), "Fechando a sessao telnet."));
		
		try {
			redeIpFunctions.fecharSessaoTelnet();
		} catch (IOException e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e),
					RedeHelper.retornaInfoProcedimento(ThreadAtivacaoSwitch.class.getName(), configSwitchIdDomain.getIdVantive(), "Erro ao fechar sessao TELNET no final do teste."));
		}

		// label etapa teste online
		configSwitchIdDomain.setEtapaTesteOnline("Salvando informações no Banco de Dados");
		// Setando a ProgressBar
		configSwitchIdDomain.setValorProgressBar("95");
		
		// ------------------------------------------------------------
		// BANCO DE DADOS MONGODB
		// ------------------------------------------------------------		
			
		try {
			// principal id
			configSwitchIdDomain.setDataFinal(new Date());
			this.configSwitchIdRepository.save(configSwitchIdDomain);
			
			// log geral
			ConfigSwitchLogDomain configSwitchLogDomain = new ConfigSwitchLogDomain(
					configSwitchIdDomain.getId(), 
					StringHelper.removerCodigoCores(redeIpFunctions.getLogTotal().toString()));			
			this.configSwitchLogRepository.save(configSwitchLogDomain);
			
			// inserindo os resultados
			try {
				inserirResultadosTesteMongoDb(
						configSwitchIdDomain.getId(),
						TiposTestesEnum.SWITCH.getCodigo(),
						configSwitchIdDomain.getGravaLogResultado().getResultadoSwitch().getCodigosResultados(),
						configSwitchIdDomain.getGravaLogResultado().getResultadoSwitch().getLogsResultados(),
						configSwitchIdDomain.getGravaLogResultado().getResultadoSwitch().getRetornosDescricao());
			} catch (Exception e) {
				ExceptionLogger.record(ExceptionUtils.getStackTrace(e),
						RedeHelper.retornaInfoProcedimento(ThreadAtivacaoSwitch.class.getName(), configSwitchIdDomain.getIdVantive(), "inserirResultadosTesteMongoDb"));
			}
			
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e),
					RedeHelper.retornaInfoProcedimento(ThreadAtivacaoSwitch.class.getName(), configSwitchIdDomain.getIdVantive(), "inserirResultadosTesteMongoDb"));
		}
		
		// ------------------------------------------------------------
		// FIM DO TESTE
		// ------------------------------------------------------------
		
		// Setando ProgressBar
		configSwitchIdDomain.setValorProgressBar("100");
		
		// Metodo para limpar o objeto da sessao
		verificaObjetoSessao();
	}
		
	/**
	 * Metodo que insere os resultados no mongo db
	 * @param idTeste
	 * @param idTipoTeste
	 * @param codigosResultados
	 * @param logsResultados
	 * @param descricoesResultados
	 */
	public void inserirResultadosTesteMongoDb(String idConfSwitch, int idTipoTeste, int[] codigosResultados, String[] logsResultados, String[] descricoesResultados) {
		// pegando o ultimo indice valido != -1
		int index = 0;
		for (int c = ResultadoLogger.NUMERO_PROCEDIMENTOS - 1; c > 0; c--) {
			if(codigosResultados[c] != -1) {
				index = c;
				break;
			}
		}
		
		for(int c = 0; c <= index; c++) {
			if(codigosResultados[c] == -1) {
				codigosResultados[c] = 0;
			}
			
			ConfigSwitchResultadosDomain configSwitchResultadosDomain =  new ConfigSwitchResultadosDomain(
					idConfSwitch,
					idTipoTeste,
					c,
					codigosResultados[c],
					StringUtils.isBlank(descricoesResultados[c]) ? "" : descricoesResultados[c],
					StringUtils.isBlank(logsResultados[c]) ? "" : logsResultados[c]
					);
			
			this.configSwitchresultadosRepository.save(configSwitchResultadosDomain);
		}
	}
	
	/**
	 * Metodo que verifica objeto na sessão 
	 */
	public void verificaObjetoSessao() {
		
		try {
			Thread.sleep(60000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if(session != null && configSwitchIdDomain != null && session.getAttribute("ativacaoModel_" + configSwitchIdDomain.getToken()) != null) {				
			// Removendo o objeto de testes da sessao
			session.setAttribute("ativacaoModel_" + configSwitchIdDomain.getToken(), null);
			session.removeAttribute("ativacaoModel_" + configSwitchIdDomain.getToken());
		}
	}
	
}
