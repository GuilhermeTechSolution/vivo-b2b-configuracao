package br.com.iatapp.threads;

import java.io.IOException;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONObject;

import br.com.iatapp.domain.SipOneCoreProcessoIdDomain;
import br.com.iatapp.enums.JsonKeysEnum;
import br.com.iatapp.enums.TipoProcedimentoSipOneCoreEnum;
import br.com.iatapp.helper.RedeHelper;
import br.com.iatapp.helper.StringHelper;
import br.com.iatapp.logger.ExceptionLogger;
import br.com.iatapp.rede.RedeIpFunctions;
import br.com.iatapp.repositories.SipOneCoreProcessoIdRepository;

public class ThreadSipOneCoreAplicarScript extends Thread {

	HttpSession session;
	SipOneCoreProcessoIdDomain processoIdDomain;
	SipOneCoreProcessoIdRepository processoIdRepository;
	
	public ThreadSipOneCoreAplicarScript(HttpSession session, 
			SipOneCoreProcessoIdDomain processoIdDomain, 
			SipOneCoreProcessoIdRepository processoIdRepository) {
		this.session = session;
		this.processoIdDomain = processoIdDomain;
		this.processoIdRepository = processoIdRepository;
	}
	
	public void run() {
		
		// Zerando a Progress Bar
		processoIdDomain.setValorProgressBar("50");
		// label etapa teste online
		processoIdDomain.setEtapaTesteOnline("Conectando no servidor");
		
		// Criando o Objeto da rede
		RedeIpFunctions redeIpFunctions = new RedeIpFunctions();
		processoIdDomain.setRedeIpFunctions(redeIpFunctions);
		
		// ------------------------------------------------------------
		// CRIANDO SESSAO TELNET
		// ------------------------------------------------------------
		
		try {
			if(!redeIpFunctions.abrirSessaoTelnet()) {
				ExceptionLogger.record("Telnet(Sem Exception)", 
						RedeHelper.retornaInfoProcedimento(ThreadSipOneCoreAplicarScript.class.getName(), 
								processoIdDomain.getIdVantive(), "Erro sem Exception ao abrir sessão TELNET no início do teste."));
				
				processoIdDomain.setValorProgressBar("90");
				// mensagem de erro ao conectar no servidor
				processoIdDomain.setMensagemErroConexaoServidor("Erro de conexão com o servidor. Execute o teste novamente.");
				
				// Metodo para limpar o objeto da sessao
				//verificaObjetoSessao();
				
				return;
			}
		} catch (Exception e) {
			
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e), 
					RedeHelper.retornaInfoProcedimento(ThreadSipOneCoreAplicarScript.class.getName(), 
							processoIdDomain.getIdVantive(), "Erro com Exception ao abrir sessão TELNET no início do teste."));
			
			processoIdDomain.setValorProgressBar("90");
			// mensagem de erro ao conectar no servidor
			processoIdDomain.setMensagemErroConexaoServidor("Erro de conexão com o servidor. Execute o teste novamente.");
						
			// Metodo para limpar o objeto da sessao
			//verificaObjetoSessao();
			
			return;
		}
		
		processoIdDomain.setValorProgressBar("52");
		
		// ------------------------------------------------------------
		// MAQUINA SALTO (TATUAPÉ)
		// ------------------------------------------------------------
		
		processoIdDomain.setValorProgressBar("55");
		
		// Conectando na maquina de salto
		if(!redeIpFunctions.conectarMaquinaSalto()) {
			// Fechando a sessao telnet
			try {
				redeIpFunctions.fecharSessaoTelnet();
			} catch (IOException e) {
				ExceptionLogger.record(ExceptionUtils.getStackTrace(e), 
						RedeHelper.retornaInfoProcedimento(ThreadSipOneCoreAplicarScript.class.getName(), 
								processoIdDomain.getIdVantive(), "Não conectou na máquina de Salto."));
			}
			processoIdDomain.setValorProgressBar("90");
			// mensagem de erro ao conectar no servidor
			processoIdDomain.setMensagemErroConexaoServidor("Erro de conexão com o servidor. Execute o teste novamente.");
						
			// Metodo para limpar o objeto da sessao
			//verificaObjetoSessao();
			
			return;
		}		
		
		// ------------------------------------------------------------
		// Scripts de remocao para os casos de SBC Antigo Migracao
		// ------------------------------------------------------------
		
		if (processoIdDomain.getTipoProcedimento() == TipoProcedimentoSipOneCoreEnum.SCRIPTS_ALTA_FRESH.getCodigo()) {
			
			JSONObject jsonTxtStart = new JSONObject(processoIdDomain.getDadosTxtStar());
			if(jsonTxtStart != null 
					&& StringUtils.isNotBlank(jsonTxtStart.optString("sbcAtualPrimario", "")) 
					&& StringUtils.isNotBlank(jsonTxtStart.optString("sbcAtualSecundario", "")) 
					&& StringUtils.isNotBlank(jsonTxtStart.optString("scriptDeletePriLine1", "")) 
					&& StringUtils.isNotBlank(jsonTxtStart.optString("scriptDeletePriLine2", "")) 
					&& StringUtils.isNotBlank(jsonTxtStart.optString("scriptDeleteSecLine1", "")) 
					&& StringUtils.isNotBlank(jsonTxtStart.optString("scriptDeleteSecLine2", ""))) {
			
				
				AplicarScriptsSbcFunctions sipSbcFunctions = new AplicarScriptsSbcFunctions(redeIpFunctions, processoIdDomain);
				sipSbcFunctions.iniciaProcedimentoRemocaoSbcAtual(jsonTxtStart);				
			}
		}
		
		processoIdDomain.setValorProgressBar("60");
		
		if (processoIdDomain.getTipoProcedimento() == TipoProcedimentoSipOneCoreEnum.SCRIPTS_ALTA_FRESH.getCodigo() ||
			processoIdDomain.getTipoProcedimento() == TipoProcedimentoSipOneCoreEnum.SCRIPTS_AP_PORTABILIDADE.getCodigo() ||
			processoIdDomain.getTipoProcedimento() == TipoProcedimentoSipOneCoreEnum.ROLLBACK_ALTA_FRESH.getCodigo() || 
			processoIdDomain.getTipoProcedimento() == TipoProcedimentoSipOneCoreEnum.ROLLBACK_AP_PORTABILIDADE.getCodigo()) {
			// APLICAR SCRIPT U2000
			AplicarScriptsU2000Functions sipU2000Functions = new AplicarScriptsU2000Functions(redeIpFunctions, processoIdDomain);
			sipU2000Functions.iniciaProcedimento();
		}
		
		processoIdDomain.setValorProgressBar("70");
		
		if (!processoIdDomain.getJsonDados().has(JsonKeysEnum.SIPONECORE_LOGIN_U2000.getCodigo())) {
			
			// APLICAR SCRIPT IMS
			AplicarScriptsImsFunctions sipImsFunctions = new AplicarScriptsImsFunctions(redeIpFunctions, processoIdDomain);
			sipImsFunctions.iniciaProcedimento();
			
			processoIdDomain.setValorProgressBar("80");
			
			if (processoIdDomain.getTipoProcedimento() == TipoProcedimentoSipOneCoreEnum.SCRIPTS_ALTA_FRESH.getCodigo() ||
				processoIdDomain.getTipoProcedimento() == TipoProcedimentoSipOneCoreEnum.SCRIPTS_AP_PORTABILIDADE.getCodigo() ||
				processoIdDomain.getTipoProcedimento() == TipoProcedimentoSipOneCoreEnum.ROLLBACK_ALTA_FRESH.getCodigo() || 
				processoIdDomain.getTipoProcedimento() == TipoProcedimentoSipOneCoreEnum.ROLLBACK_AP_PORTABILIDADE.getCodigo()) {
				// APLICAR SCRIPT SBC
				AplicarScriptsSbcFunctions sipSbcFunctions = new AplicarScriptsSbcFunctions(redeIpFunctions, processoIdDomain);
				sipSbcFunctions.iniciaProcedimento();
			}
			
			processoIdDomain.setValorProgressBar("85");
		}
				
		// ------------------------------------------------------------
		// FECHANDO MAQUINA SALTO
		// ------------------------------------------------------------
		
		redeIpFunctions.enviarComandoAvailableSemAppendLogTotal("exit", new String[] {"~]$",":~$",":/]$"},
				RedeHelper.retornaInfoProcedimento(ThreadSipOneCoreAplicarScript.class.getName(), 
						processoIdDomain.getIdVantive(), "Fechando a conexão com a maquina de Salto."));
		
		// ------------------------------------------------------------
		// FECHANDO SESSAO TELNET
		// ------------------------------------------------------------
		
		redeIpFunctions.enviarComandoAvailableSemAppendLogTotal("exit", new String[] {"~]$",":~$",":/]$"},
				RedeHelper.retornaInfoProcedimento(ThreadSipOneCoreAplicarScript.class.getName(), 
						processoIdDomain.getIdVantive(), "Fechando a sessao telnet."));
		
		try {
			redeIpFunctions.fecharSessaoTelnet();
		} catch (IOException e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e),
					RedeHelper.retornaInfoProcedimento(ThreadSipOneCoreAplicarScript.class.getName(), 
							processoIdDomain.getIdVantive(), "Erro ao fechar sessao TELNET no final do teste."));
		}

		// Setando o log de execucao
		processoIdDomain.setLogExecucao(StringHelper.removerCodigoCores(redeIpFunctions.getLogTotal().toString()));
		
		// label etapa teste online
		//processoIdDomain.setEtapaTesteOnline("Salvando informações no Banco de Dados");
		
		// ------------------------------------------------------------
		// FIM DO TESTE
		// ------------------------------------------------------------
		
		// Setando ProgressBar
		processoIdDomain.setValorProgressBar("90");
		
//		// Metodo para limpar o objeto da sessao
//		verificaObjetoSessao();
	}
	
//	/**
//	 * Metodo que verifica objeto na sessão 
//	 */
//	public void verificaObjetoSessao() {
//		
//		try {
//			Thread.sleep(60000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		
//		if(session != null && processoIdDomain != null && session.getAttribute("testeRede_" + processoIdDomain.getToken()) != null) {
//			// Removendo o objeto de testes da sessao
//			session.setAttribute("testeRede_" + processoIdDomain.getToken(), null);
//			session.removeAttribute("testeRede_" + processoIdDomain.getToken());
//		}
//	}
}
