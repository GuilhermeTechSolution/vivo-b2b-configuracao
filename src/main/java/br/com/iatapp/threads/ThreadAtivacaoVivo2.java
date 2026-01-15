package br.com.iatapp.threads;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import br.com.iatapp.config.IatConstants;
import br.com.iatapp.dao.SenhasDao;
import br.com.iatapp.domain.Vivo2IdDomain;
import br.com.iatapp.domain.Vivo2ResultadosDomain;
import br.com.iatapp.enums.CodigoServidoresEnum;
import br.com.iatapp.enums.ResultadosAtivacaoVivo2;
import br.com.iatapp.enums.ResultadosStatus;
import br.com.iatapp.enums.TiposTestesEnum;
import br.com.iatapp.helper.RedeHelper;
import br.com.iatapp.helper.StringHelper;
import br.com.iatapp.logger.ExceptionLogger;
import br.com.iatapp.logger.ResultadoLogger;
import br.com.iatapp.model.UsuarioModel;
import br.com.iatapp.rede.AtivacaoVivo2Functions;
import br.com.iatapp.rede.RedeIpFunctions;
import br.com.iatapp.repositories.Vivo2IdRepository;
import br.com.iatapp.repositories.Vivo2ResultadosRepository;
import br.com.iatapp.repositories.Vivo2ScriptRepository;

/**
 * 
 * @author ottap
 *
 */
public class ThreadAtivacaoVivo2 extends Thread {

	HttpSession session;
	Vivo2IdDomain vivo2IdDomain;
	Vivo2IdRepository vivo2IdRepository;
	Vivo2ResultadosRepository vivo2ResultadosRepository;
	Vivo2ScriptRepository vivo2ScriptRepository;
	
	public ThreadAtivacaoVivo2(
			HttpSession session, 
			Vivo2IdDomain vivo2IdDomain, 
			Vivo2IdRepository vivo2IdRepository, 
			Vivo2ResultadosRepository vivo2ResultadosRepository,
			Vivo2ScriptRepository vivo2ScriptRepository
			) {
		this.session = session;
		this.vivo2IdDomain = vivo2IdDomain;
		this.vivo2IdRepository = vivo2IdRepository;
		this.vivo2ResultadosRepository = vivo2ResultadosRepository;
		this.vivo2ScriptRepository = vivo2ScriptRepository;
	}
	
	public void run() {
		
		// Zerando a Progress Bar
		vivo2IdDomain.setValorProgressBar("5");
		// label etapa teste online
		vivo2IdDomain.setEtapaTesteOnline("Conectando no servidor");
		
		// Criando o Objeto da rede
		RedeIpFunctions redeIpFunctions = new RedeIpFunctions();
		vivo2IdDomain.setRedeIpFunctions(redeIpFunctions);
		
		// ------------------------------------------------------------
		// CRIANDO SESSAO TELNET
		// ------------------------------------------------------------
		
		try {
			if(!redeIpFunctions.abrirSessaoTelnet()) {
				ExceptionLogger.record("Telnet(Sem Exception)", 
						RedeHelper.retornaInfoProcedimento(ThreadAtivacaoVivo2.class.getName(), vivo2IdDomain.getIdTbs(), "Erro sem Exception ao abrir sessão TELNET no início do teste."));
				
				vivo2IdDomain.setValorProgressBar("100");
				// mensagem de erro ao conectar no servidor
				vivo2IdDomain.setMensagemErroConexaoServidor("Erro de conexão com o servidor. Execute o teste novamente.");
				
				// Metodo para limpar o objeto da sessao
				verificaObjetoSessao();
				
				return;
			}
		} catch (Exception e) {
			
			e.printStackTrace();
			
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e), 
					RedeHelper.retornaInfoProcedimento(ThreadAtivacaoVivo2.class.getName(), vivo2IdDomain.getIdTbs(), "Erro com Exception ao abrir sessão TELNET no início do teste."));
			
			vivo2IdDomain.setValorProgressBar("100");
			// mensagem de erro ao conectar no servidor
			vivo2IdDomain.setMensagemErroConexaoServidor("Erro de conexão com o servidor. Execute o teste novamente.");
			
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
							RedeHelper.retornaInfoProcedimento(ThreadAtivacaoVivo2.class.getName(), vivo2IdDomain.getIdTbs(), "Não conectou na máquina de Salto."));
				}

				vivo2IdDomain.setValorProgressBar("100");
				// mensagem de erro ao conectar no servidor
				vivo2IdDomain.setMensagemErroConexaoServidor("Erro de conexão com o servidor. Execute o teste novamente.");
				
				// Metodo para limpar o objeto da sessao
				verificaObjetoSessao();
				
				return;
			}		
		}
		
		// label etapa teste online
		vivo2IdDomain.setEtapaTesteOnline("Conectando Rede IP");
		
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
						RedeHelper.retornaInfoProcedimento(ThreadAtivacaoVivo2.class.getName(), vivo2IdDomain.getIdTbs(), "Não conectou na máquina de Salto."));
			}

			vivo2IdDomain.setValorProgressBar("100");
			// mensagem de erro ao conectar no servidor
			vivo2IdDomain.setMensagemErroConexaoServidor("Erro de conexão com o servidor. Execute o teste novamente.");
			
			// Metodo para limpar o objeto da sessao
			verificaObjetoSessao();
			
			return;
		}
		
		if(!redeIpFunctions.conectarRedeIp(usuarioSenhas.getLoginRedeIp(), usuarioSenhas.getSenhaRedeIp())) {
			
			// Fechando conexao
			redeIpFunctions.enviarComandoAvailable("exit", new String[] {"~]$",":~$", ":/]$"},
					RedeHelper.retornaInfoProcedimento(ThreadAtivacaoVivo2.class.getName(), vivo2IdDomain.getIdTbs(), "Erro ao conectar na Rede IP 200.204.1.4."));
			
			// Fechando a sessao telnet
			try {
				redeIpFunctions.fecharSessaoTelnet();
			} catch (IOException e) {
				ExceptionLogger.record(ExceptionUtils.getStackTrace(e),
						RedeHelper.retornaInfoProcedimento(ThreadAtivacaoVivo2.class.getName(), vivo2IdDomain.getIdTbs(), "Não conectou na Rede IP 200.204.1.4. Erro ao fechar a sessão TELNET."));
			}
			
			vivo2IdDomain.setValorProgressBar("100");
			// mensagem de erro ao conectar no servidor
			vivo2IdDomain.setMensagemErroConexaoServidor("Erro de conexão com o máquina de Gerência '200.204.1.4'.");
						
			// Metodo para limpar o objeto da sessao
			verificaObjetoSessao();
			
			return;
		}
		
		// Enter para aparecer o nome da Rede IP no inicio do Log
		redeIpFunctions.enviarComandoAvailable("", new String[] {"~]$",":~$", ":/]$"},
				RedeHelper.retornaInfoProcedimento(ThreadAtivacaoVivo2.class.getName(), vivo2IdDomain.getIdTbs(), "Erro ao enviar ENTER para aparecer o nome da Rede IP 200.204.1.4."));		
		
		// ------------------------------------------------------------
		// CONFIGURACAO SWITCH
		// ------------------------------------------------------------
		
		AtivacaoVivo2Functions ativacaoVivo2Functions = new AtivacaoVivo2Functions(redeIpFunctions, vivo2IdDomain, vivo2ScriptRepository);
		ativacaoVivo2Functions.iniciaProcedimentoBuscaInterfaceSwitch();
		ativacaoVivo2Functions.iniciaProcedimentoConfiguracao();
		
		// ------------------------------------------------------------
		// FECHANDO REDE IP
		// ------------------------------------------------------------
		
		// label etapa teste online
		vivo2IdDomain.setEtapaTesteOnline("Fechando conexão");
		
		redeIpFunctions.enviarComandoAvailableSemAppendLogTotal("exit", new String[] {"~]$",":~$",":/]$"},
				RedeHelper.retornaInfoProcedimento(ThreadAtivacaoVivo2.class.getName(), vivo2IdDomain.getIdTbs(), "Fechando a conexão com a Rede IP."));
			
		if(IatConstants.codigoServidor == CodigoServidoresEnum.SERVIDOR_DEV.getCodigoServidor()) {			
			// ------------------------------------------------------------
			// FECHANDO MAQUINA SALTO
			// ------------------------------------------------------------
			
			redeIpFunctions.enviarComandoAvailableSemAppendLogTotal("exit", new String[] {"~]$",":~$",":/]$"},
					RedeHelper.retornaInfoProcedimento(ThreadAtivacaoVivo2.class.getName(), vivo2IdDomain.getIdTbs(), "Fechando a conexão com a maquina de Salto."));	
		}
		
		// ------------------------------------------------------------
		// FECHANDO SESSAO TELNET
		// ------------------------------------------------------------
		
		redeIpFunctions.enviarComandoAvailableSemAppendLogTotal("exit", new String[] {"~]$",":~$",":/]$"},
				RedeHelper.retornaInfoProcedimento(ThreadAtivacaoVivo2.class.getName(), vivo2IdDomain.getIdTbs(), "Fechando a sessao telnet."));
		
		try {
			redeIpFunctions.fecharSessaoTelnet();
		} catch (IOException e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e),
					RedeHelper.retornaInfoProcedimento(ThreadAtivacaoVivo2.class.getName(), vivo2IdDomain.getIdTbs(), "Erro ao fechar sessao TELNET no final do teste."));
		}

		// label etapa teste online
		vivo2IdDomain.setEtapaTesteOnline("Salvando informações no Banco de Dados");
		// Setando a ProgressBar
		vivo2IdDomain.setValorProgressBar("95");
		
		// setaResultadoCertificacaoGeral
		setaResultadoCertificacaoGeral();
		
		// ------------------------------------------------------------
		// BANCO DE DADOS MONGODB
		// ------------------------------------------------------------		
			
		try {
			// principal id
			vivo2IdDomain.setDataFinal(new Date());
			vivo2IdDomain.setLogGeral(StringHelper.removerCodigoCores(redeIpFunctions.getLogTotal().toString()));
			this.vivo2IdRepository.save(vivo2IdDomain);

			// inserindo os resultados
			try {
				inserirResultadosTesteMongoDb(
						vivo2IdDomain.getId(),
						TiposTestesEnum.PE_PRINCIPAL.getCodigo(),
						vivo2IdDomain.getGravaLogResultado().getResultadoVivo2().getCodigosResultados(),
						vivo2IdDomain.getGravaLogResultado().getResultadoVivo2().getLogsResultados(),
						vivo2IdDomain.getGravaLogResultado().getResultadoVivo2().getRetornosDescricao());
			} catch (Exception e) {
				ExceptionLogger.record(ExceptionUtils.getStackTrace(e),
						RedeHelper.retornaInfoProcedimento(ThreadAtivacaoVivo2.class.getName(), vivo2IdDomain.getIdTbs(), "inserirResultadosTesteMongoDb"));
			}
			
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e),
					RedeHelper.retornaInfoProcedimento(ThreadAtivacaoVivo2.class.getName(), vivo2IdDomain.getIdTbs(), "inserirResultadosTesteMongoDb"));
		}
		
		// ------------------------------------------------------------
		// FIM DO TESTE
		// ------------------------------------------------------------
		
		// Setando ProgressBar
		vivo2IdDomain.setValorProgressBar("100");
		
		// Metodo para limpar o objeto da sessao
		verificaObjetoSessao();
	}
	
	/**
	 * setaResultadoCertificacaoGeral
	 */
	public void setaResultadoCertificacaoGeral() {
		
		if(vivo2IdDomain.getResultado(TiposTestesEnum.PE_PRINCIPAL.getCodigo(), ResultadosAtivacaoVivo2.ACESSOU_EQUIPAMENTO.getCodigo()) != ResultadosStatus.OK.getCodigo()) {
			vivo2IdDomain.setIdCertificado(false);
			vivo2IdDomain.setMensagemGeral(String.format("Não foi possível acessar o equipamento para execução do teste."));
			return;
		}
		
		if(vivo2IdDomain.getResultado(TiposTestesEnum.PE_PRINCIPAL.getCodigo(), ResultadosAtivacaoVivo2.CHECK_VRF_SIP.getCodigo()) == ResultadosStatus.FALHOU.getCodigo()) {
			vivo2IdDomain.setIdCertificado(false);
			vivo2IdDomain.setMensagemGeral(String.format("Error Check VRF SIP."));
			return;
		}
		
		if(vivo2IdDomain.getResultado(TiposTestesEnum.PE_PRINCIPAL.getCodigo(), ResultadosAtivacaoVivo2.CHECK_VALIDACAO_IPS_LIVRES.getCodigo()) == ResultadosStatus.FALHOU.getCodigo()) {
			vivo2IdDomain.setIdCertificado(false);
			vivo2IdDomain.setMensagemGeral(String.format("Error Check de Validação de IPs."));
			return;
		}
		
		if(vivo2IdDomain.getResultado(TiposTestesEnum.PE_PRINCIPAL.getCodigo(), ResultadosAtivacaoVivo2.CHECK_INTERFACE_CONFIGURACAO.getCodigo()) == ResultadosStatus.FALHOU.getCodigo()) {
			vivo2IdDomain.setIdCertificado(false);
			vivo2IdDomain.setMensagemGeral(String.format("Error Check de Interface."));
			return;
		}
		
		if(vivo2IdDomain.getResultado(TiposTestesEnum.PE_PRINCIPAL.getCodigo(), ResultadosAtivacaoVivo2.CONFIGURACAO_SCRIPT.getCodigo()) != ResultadosStatus.OK.getCodigo()) {
			vivo2IdDomain.setIdCertificado(false);
			vivo2IdDomain.setMensagemGeral(String.format("Error procedimento de configuração do equipamento"));
			return;
		}

		// ID Certificado
		vivo2IdDomain.setIdCertificado(true);
		vivo2IdDomain.setMensagemGeral(String.format("Atividade concluída com sucesso."));
		
		return;		
	}
		
	/**
	 * Metodo que insere os resultados no mongo db
	 * @param idTeste
	 * @param idTipoTeste
	 * @param codigosResultados
	 * @param logsResultados
	 * @param descricoesResultados
	 */
	public void inserirResultadosTesteMongoDb(String idConf, int idTipoTeste, int[] codigosResultados, String[] logsResultados, String[] descricoesResultados) {
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
			
			Vivo2ResultadosDomain vivo2ResultadosDomain =  new Vivo2ResultadosDomain(
					idConf,
					idTipoTeste,
					c,
					codigosResultados[c],
					StringUtils.isBlank(descricoesResultados[c]) ? "" : descricoesResultados[c],
					StringUtils.isBlank(logsResultados[c]) ? "" : logsResultados[c]
					);
			
			this.vivo2ResultadosRepository.save(vivo2ResultadosDomain);
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
		
		if(session != null && vivo2IdDomain != null && session.getAttribute("ativacaoModel_" + vivo2IdDomain.getToken()) != null) {				
			// Removendo o objeto de testes da sessao
			session.setAttribute("ativacaoModel_" + vivo2IdDomain.getToken(), null);
			session.removeAttribute("ativacaoModel_" + vivo2IdDomain.getToken());
		}
	}
	
}
