package br.com.iatapp.threads;

import java.util.Date;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import br.com.iatapp.config.IatConstants;
import br.com.iatapp.domain.TestePrincipalDomain;
import br.com.iatapp.domain.TestePrincipalLogsDomain;
import br.com.iatapp.domain.TestePrincipalResultadosLogsDomain;
import br.com.iatapp.enums.ModulosTesteEnum;
import br.com.iatapp.enums.TiposTestesEnum;
import br.com.iatapp.helper.DataHelper;
import br.com.iatapp.helper.RedeHelper;
import br.com.iatapp.helper.StringHelper;
import br.com.iatapp.logger.ExceptionLogger;
import br.com.iatapp.logger.ResultadoLogger;
import br.com.iatapp.repositories.TestePrincipalLogsRepository;
import br.com.iatapp.repositories.TestePrincipalRepository;
import br.com.iatapp.repositories.TestePrincipalResultadosLogsRepository;
import br.com.iatapp.service.SendHttpRequestService;


public class ThreadSipOneCoreCheckVoip extends Thread {
	
	private HttpSession session;
	TestePrincipalDomain testeRedeModel;
	TestePrincipalDomain testeRedeModelRetorno;
	private TestePrincipalRepository testePrincipalRepository;
	private TestePrincipalLogsRepository testePrincipalLogsRepository;
	private TestePrincipalResultadosLogsRepository testePrincipalResultadosLogsRepository;
	
	public ThreadSipOneCoreCheckVoip(HttpSession session, 
			TestePrincipalDomain testeRedeModel,
			TestePrincipalRepository testePrincipalRepository,
			TestePrincipalLogsRepository testePrincipalLogsRepository,
			TestePrincipalResultadosLogsRepository testePrincipalResultadosLogsRepository) {
		this.session = session;
		this.testeRedeModel = testeRedeModel;
		this.testePrincipalRepository = testePrincipalRepository;
		this.testePrincipalLogsRepository = testePrincipalLogsRepository;
		this.testePrincipalResultadosLogsRepository = testePrincipalResultadosLogsRepository;
	}
	
	@Override
	public void run() {
		
		try {
		
			System.out.println(String.format("Starting Check Voip ID:%s | Vantive:%s", testeRedeModel.getIdTeste(), testeRedeModel.getIdVantive()));
			
			testeRedeModel.setValorProgressBar("5");
			testeRedeModel.setEtapaTesteOnline("Iniciando Check VOIP");
			
			//ProcessHelper processHelper = new ProcessHelper();
			
			// ----------------------------------------------------------------
			// -------------------------- STAR --------------------------------
			// ----------------------------------------------------------------
			
			sleepMs(2000);
			testeRedeModel.setEtapaTesteOnline("Buscando informações no STAR");
			
			// buscando os dados no star
			JSONObject jsonDados = null;
			//jsonDados = processHelper.buscaDadosStar(testeRedeModel.getIdVantive());
			if(jsonDados == null) {
				testeRedeModel.setDataFimTeste(new Date());
				testeRedeModel.setTimestampFimTeste(DataHelper.getLongDateTimeStamp());
				testeRedeModel.setValorProgressBar("100");
				testePrincipalRepository.save(testeRedeModel);
				verificaObjetoSessao();
				return;
			}
			
			// ----------------------------------------------------------------
			// ----------------------- TESTE REDE -----------------------------
			// ----------------------------------------------------------------
			boolean testeOk = false;
			JSONObject jsonReq = new JSONObject();
			jsonDados.put("token", testeRedeModel.getToken());
			SendHttpRequestService sendHttpRequestService = new SendHttpRequestService();
			String retorno = sendHttpRequestService.sendPostRequest(String.format("%s/api/config/checkBackboneRedeIp", 
					IatConstants.IAT_VIVO_B2B_URL), testeRedeModel.getToken(), jsonDados.toString(), jsonReq);
			if (StringUtils.isBlank(retorno)) {
				testeRedeModel.setDataFimTeste(new Date());
				testeRedeModel.setTimestampFimTeste(DataHelper.getLongDateTimeStamp());
				testeRedeModel.setValorProgressBar("100");
				testePrincipalRepository.save(testeRedeModel);
				verificaObjetoSessao();
				return;
			} else if (retorno.equals("tarefa_iniciada")) {
				
				//Start thread progress
				int c = 0;
					
				while (c < 360) { // 30min
					
					// sleep
					try { Thread.sleep(5 * 1000); } catch (Exception e) {}
					
					retorno = sendHttpRequestService.sendGetRequest(String.format("%s/api/config/progressCheckBackboneRedeIp/%s", 
							IatConstants.IAT_VIVO_B2B_URL, testeRedeModel.getToken()), testeRedeModel.getToken(), jsonReq);
					c++;
					
					if(IatConstants.DEBUG)
						System.out.println(retorno);
					
					if(StringUtils.isBlank(retorno))
						continue;
					
					if (!StringHelper.isJsonValid(retorno))
						continue;
					
					JSONObject jsonProgress = null;
					try {
						jsonProgress = new JSONObject(retorno);
					} catch (Exception e) {}
					
					if(jsonProgress.has("progressBar")) {
						if (jsonProgress.getInt("progressBar") == 100) {
							testeOk = true;
							break;
						} else {
							testeRedeModel.setValorProgressBar(Integer.toString(jsonProgress.getInt("progressBar")));
							if (jsonProgress.has("logOnline"))
								testeRedeModel.setLogOnline(jsonProgress.getString("logOnline"));
							if (jsonProgress.has("etapaTesteOnline"))
								testeRedeModel.setEtapaTesteOnline(jsonProgress.getString("etapaTesteOnline"));
						}
					}
					continue;
				}
				
				if (testeOk) {
					
					retorno = sendHttpRequestService.sendGetRequest(String.format("%s/api/config/resultadosCheckBackboneRedeIp/%s", 
							IatConstants.IAT_VIVO_B2B_URL, testeRedeModel.getToken()), testeRedeModel.getToken(), jsonReq);
						
					if (StringUtils.isNotBlank(retorno)) {
						ObjectMapper objectMapper = new ObjectMapper();
						objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
						testeRedeModelRetorno = objectMapper.readValue(retorno, TestePrincipalDomain.class);
						
						if (testeRedeModelRetorno == null)
							testeOk = false;
					} else {
						testeOk = false;
					}
				}
			}
			
			if (testeOk) {
				
				// Atualizar os dados
				testeRedeModelRetorno.setId(testeRedeModel.getId());
				testeRedeModelRetorno.setIdTeste(testeRedeModel.getIdTeste());
				// setando o usuario do teste
				testeRedeModelRetorno.setIdUsuario(testeRedeModel.getIdUsuario());
				testeRedeModelRetorno.setNomeUsuario(testeRedeModel.getNomeUsuario());
				// modulo do teste
				testeRedeModelRetorno.setIdModulo(ModulosTesteEnum.CHECK_VOIP.getCodigo());
				testeRedeModelRetorno.setNomeModulo(ModulosTesteEnum.CHECK_VOIP.getDescricao());
				// data inicio
				testeRedeModelRetorno.setDataInicioTeste(testeRedeModel.getDataInicioTeste());
				testeRedeModelRetorno.setTimestampInicioTeste(testeRedeModel.getTimestampInicioTeste());
				
				testeRedeModel = testeRedeModelRetorno;
				session.setAttribute("checkVoip_" + testeRedeModel.getToken(), testeRedeModel);
				
				testeRedeModel.setValorProgressBar("95");
				testeRedeModel.setEtapaTesteOnline("Salvando informações no Banco de Dados");
				
				// ------------------------------------------------------------
				// BANCO DE DADOS MONGODB
				// ------------------------------------------------------------
				
				try {
					// salvando no MongoDB
					testeRedeModel.setDataFimTeste(new Date());
					testeRedeModel.setTimestampFimTeste(DataHelper.getLongDateTimeStamp());
					testePrincipalRepository.save(testeRedeModel);
				} catch (Exception e) {
					ExceptionLogger.record(ExceptionUtils.getStackTrace(e),
							RedeHelper.retornaInfoProcedimento(ThreadSipOneCoreCheckVoip.class.getName(), 
									String.valueOf(testeRedeModel.getIdTeste()), "insereTesteBancoDados MONGODB"));
				}
				
				try {
					// inserindo log principal Mongodb
					TestePrincipalLogsDomain testesPrincipalLogs = new TestePrincipalLogsDomain(
							testeRedeModel.getIdTeste(), 
							testeRedeModel.getLogExecucao(), 
							testeRedeModel.getLogFormatado().toString());
					this.testePrincipalLogsRepository.save(testesPrincipalLogs);
				} catch (Exception e) {
					ExceptionLogger.record(ExceptionUtils.getStackTrace(e),
							RedeHelper.retornaInfoProcedimento(ThreadSipOneCoreCheckVoip.class.getName(), 
									String.valueOf(testeRedeModel.getIdTeste()), "insereTesteBancoDados MONGODB"));
				}
				
				// inserindo os resultados
				try {
					// PE
					inserirResultadosTesteMongoDb(
							testeRedeModel.getIdTeste(),
							TiposTestesEnum.PE_PRINCIPAL.getCodigo(),
							testeRedeModel.getGravaLogResultado().getResultadoPePrincipal().getCodigosResultados(),
							testeRedeModel.getGravaLogResultado().getResultadoPePrincipal().getLogsResultados(),
							testeRedeModel.getGravaLogResultado().getResultadoPePrincipal().getRetornosDescricao());
					// PE Backup
					inserirResultadosTesteMongoDb(
							testeRedeModel.getIdTeste(),
							TiposTestesEnum.PE_BACKUP.getCodigo(),
							testeRedeModel.getGravaLogResultado().getResultadoPeBackup().getCodigosResultados(),
							testeRedeModel.getGravaLogResultado().getResultadoPeBackup().getLogsResultados(),
							testeRedeModel.getGravaLogResultado().getResultadoPeBackup().getRetornosDescricao());
				} catch (Exception e) {
					ExceptionLogger.record(ExceptionUtils.getStackTrace(e),
							RedeHelper.retornaInfoProcedimento(ThreadSipOneCoreCheckVoip.class.getName(), 
									String.valueOf(testeRedeModel.getIdTeste()), "insereTesteBancoDados -> inserirResultadosTeste"));
				}
				
			} else {
				testeRedeModel.setDataFimTeste(new Date());
				testeRedeModel.setTimestampFimTeste(DataHelper.getLongDateTimeStamp());
				testePrincipalRepository.save(testeRedeModel);
			}
			
			testeRedeModel.setValorProgressBar("100");
			testeRedeModel.setEtapaTesteOnline("Check Voip Finalizado");
			
			verificaObjetoSessao();
			
			return;
			
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e),
					RedeHelper.retornaInfoProcedimento(ThreadSipOneCoreCheckVoip.class.getName(), testeRedeModel.getIdVantive(), "ThreadSipOneCoreCheckVoip"));
		}
	}
	
	/**
	 * Metodo que insere os resultados no mongo db
	 * @param idTeste
	 * @param idTipoTeste
	 * @param codigosResultados
	 * @param logsResultados
	 * @param descricoesResultados
	 */
	public void inserirResultadosTesteMongoDb(int idTeste, int idTipoTeste, int[] codigosResultados, String[] logsResultados, String[] descricoesResultados) {
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
			
			TestePrincipalResultadosLogsDomain testesPrincipalResultadosLogs =  new TestePrincipalResultadosLogsDomain(
					idTeste,
					idTipoTeste,
					c,
					codigosResultados[c],
					StringUtils.isBlank(descricoesResultados[c]) ? "" : descricoesResultados[c],
					StringUtils.isBlank(logsResultados[c]) ? "" : logsResultados[c]
					);
			
			testePrincipalResultadosLogsRepository.save(testesPrincipalResultadosLogs);
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
		
		if(session != null && testeRedeModel != null && session.getAttribute("checkVoip_" + testeRedeModel.getToken()) != null) {
			// Removendo o objeto de testes da sessao
			session.setAttribute("checkVoip_" + testeRedeModel.getToken(), null);
			session.removeAttribute("checkVoip_" + testeRedeModel.getToken());
		}
	}
	
	public void sleepMs(long delayMs) {
		try {
			Thread.sleep(delayMs);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
