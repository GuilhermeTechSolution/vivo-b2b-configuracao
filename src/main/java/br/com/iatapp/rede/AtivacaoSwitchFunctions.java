package br.com.iatapp.rede;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import br.com.iatapp.domain.ConfigSwitchIdDomain;
import br.com.iatapp.domain.ConfigSwitchScriptDomain;
import br.com.iatapp.enums.ModelosEquipamentosEnum;
import br.com.iatapp.enums.ResultadosAtivacaoSwitch;
import br.com.iatapp.enums.ResultadosStatus;
import br.com.iatapp.enums.TiposTestesEnum;
import br.com.iatapp.helper.RedeHelper;
import br.com.iatapp.helper.StringHelper;
import br.com.iatapp.logger.GravaLogResultadoSwitch;
import br.com.iatapp.repositories.ConfigSwitchScriptRepository;

/**
 * 
 * @author ottap
 *
 */
public class AtivacaoSwitchFunctions {
	
	private RedeIpFunctions redeIpFunctions;
	private ConfigSwitchIdDomain configSwitchIdDomain;
	private StringBuilder log;
	private GravaLogResultadoSwitch gravaLogResultado;
	private ConfigSwitchScriptRepository configSwitchScriptRepository;
	
	public AtivacaoSwitchFunctions(RedeIpFunctions redeIpFunctions, ConfigSwitchIdDomain configSwitchIdDomain, ConfigSwitchScriptRepository configSwitchScriptRepository) {
		this.redeIpFunctions = redeIpFunctions;
		this.configSwitchIdDomain = configSwitchIdDomain;
		this.log = new StringBuilder();
		this.gravaLogResultado = configSwitchIdDomain.getGravaLogResultado();
		this.configSwitchScriptRepository = configSwitchScriptRepository;
	}
	
	/**
	 * iniciaProcedimentoConfiguracao
	 * @param equipamento
	 */
	public void iniciaProcedimentoConfiguracao() {
		
		// label etapa teste online
		configSwitchIdDomain.setEtapaTesteOnline("Executando Pré Check da Configuração");
		
		// Conectando no equipamento
		
		JSONObject jsonAux = new JSONObject();
		
		if(!redeIpFunctions.conectarEquipamentoSwitch(configSwitchIdDomain.getIpSwa(), configSwitchIdDomain.getUsuarioSenhas().getLoginRedeIp(), configSwitchIdDomain.getUsuarioSenhas().getSenhaRedeIp(),
				RedeHelper.retornaInfoProcedimento(AtivacaoSwitchFunctions.class.getName(), configSwitchIdDomain.getIdVantive(), "iniciaProcedimentoAtivacao"), jsonAux)) {
			
			// tentando conectar com as senhas fixas
			JSONArray array = redeIpFunctions.retornaListaSenhasSwitch();
			boolean conectou = false;
			for(int c = 0; c < array.length(); c++) {
				JSONObject json = array.getJSONObject(c);
				if(redeIpFunctions.conectarEquipamentoSwitch(configSwitchIdDomain.getIpSwa(), json.getString("login"), json.getString("senha"),
						RedeHelper.retornaInfoProcedimento(AtivacaoSwitchFunctions.class.getName(), configSwitchIdDomain.getIdVantive(), "iniciaProcedimentoAtivacao"), jsonAux)) {
					conectou = true;
					break;
				}
			}
			
			if(!conectou) {
				gravaLogResultado.record(TiposTestesEnum.SWITCH.getCodigo(), 
						ResultadosAtivacaoSwitch.ACESSOU_EQUIPAMENTO.getCodigo(), 
						ResultadosStatus.FALHOU.getCodigo(), 
						StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()),
						"Não foi possível conectar no equipamento = " + configSwitchIdDomain.getIpSwa(),
						ResultadosAtivacaoSwitch.ACESSOU_EQUIPAMENTO.getNome(),
						ResultadosAtivacaoSwitch.ACESSOU_EQUIPAMENTO.getIndex());
				
				// Setando a ProgressBar
				configSwitchIdDomain.setValorProgressBar("50");
				
				redeIpFunctions.limpaLog(RedeHelper.retornaInfoProcedimento(AtivacaoSwitchFunctions.class.getName(), configSwitchIdDomain.getIdVantive(), "iniciaProcedimentoAtivacao"));
				
				return;
			}
		}
		
		// Setando a ProgressBar
		configSwitchIdDomain.setValorProgressBar("10");
		
		// pegando o hostname do equipamento
		String hostnameSwa = redeIpFunctions.getHostnameEquipamento("");
		if(StringUtils.isBlank(hostnameSwa)) {
			gravaLogResultado.record(TiposTestesEnum.SWITCH.getCodigo(), 
					ResultadosAtivacaoSwitch.ACESSOU_EQUIPAMENTO.getCodigo(), 
					ResultadosStatus.FALHOU.getCodigo(), 
					StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()),
					"Não foi possível conectar no equipamento = " + configSwitchIdDomain.getIpSwa(),
					ResultadosAtivacaoSwitch.ACESSOU_EQUIPAMENTO.getNome(),
					ResultadosAtivacaoSwitch.ACESSOU_EQUIPAMENTO.getIndex());
			
			// Setando a ProgressBar
			configSwitchIdDomain.setValorProgressBar("50");
			
			redeIpFunctions.limpaLog(RedeHelper.retornaInfoProcedimento(AtivacaoSwitchFunctions.class.getName(), configSwitchIdDomain.getIdVantive(), "iniciaProcedimentoAtivacao"));
			
			return;
		}
		
		configSwitchIdDomain.setHostnameSwa(hostnameSwa);
		
		// Descobrindo o modelo do equipamento
		int modeloEquipamento = jsonAux.optInt("idModelo", 0);		
		if(modeloEquipamento == 0) {
			modeloEquipamento = redeIpFunctions.retornaModeloEquipamentoSwitch(hostnameSwa,
					RedeHelper.retornaInfoProcedimento(AtivacaoSwitchFunctions.class.getName(), configSwitchIdDomain.getIdVantive(), "iniciaProcedimentoAtivacao"));
		}	
		
		// Seta o modelo do Equipamento
		configSwitchIdDomain.setIdModeloEquipamento(modeloEquipamento);
		configSwitchIdDomain.setNomeModeloEquipamento(ModelosEquipamentosEnum.valueOf(modeloEquipamento).getNome());

		configSwitchIdDomain.setTipoTeste(TiposTestesEnum.SWITCH);
		
		List<String> lstServicosCliente = new ArrayList<String>();
		lstServicosCliente.add(configSwitchIdDomain.getServicoCliente());
		
		switch (ModelosEquipamentosEnum.valueOf(modeloEquipamento)) {
		
			case DATACOM:
			case DATACOM_DM4050:
				
				// Acessou equipamento
				gravaLogResultado.record(TiposTestesEnum.SWITCH.getCodigo(), 
						ResultadosAtivacaoSwitch.ACESSOU_EQUIPAMENTO.getCodigo(), 
						ResultadosStatus.OK.getCodigo(), 
						StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()),
						"Conectou no equipamento " + hostnameSwa.toLowerCase(),
						ResultadosAtivacaoSwitch.ACESSOU_EQUIPAMENTO.getNome(),
						ResultadosAtivacaoSwitch.ACESSOU_EQUIPAMENTO.getIndex());		
				
				redeIpFunctions.limpaLog(RedeHelper.retornaInfoProcedimento(AtivacaoSwitchFunctions.class.getName(), configSwitchIdDomain.getIdVantive(), "iniciaProcedimentoAtivacao"));
				
				// lista de scripts de configuracao
				List<ConfigSwitchScriptDomain> lstScripts;
				if(modeloEquipamento == ModelosEquipamentosEnum.DATACOM.getCodigo())
					lstScripts = this.configSwitchScriptRepository.findByFabricanteAndServicosClienteIn("DATACOM", lstServicosCliente);
				else 
					lstScripts = this.configSwitchScriptRepository.findByFabricanteAndServicosClienteIn("DATACOM_DM4050", lstServicosCliente);
				
				// AtivacaoSwitchDatacomFunctions
				AtivacaoSwitchDatacomFunctions ativacaoSwitchDatacomFunctions = new AtivacaoSwitchDatacomFunctions(redeIpFunctions, configSwitchIdDomain, lstScripts);
				ativacaoSwitchDatacomFunctions.iniciaProcedimentoPreCheckConfiguracao();				
				break;
				
			case CORIANT:
				
				// Acessou equipamento
				gravaLogResultado.record(TiposTestesEnum.SWITCH.getCodigo(), 
						ResultadosAtivacaoSwitch.ACESSOU_EQUIPAMENTO.getCodigo(), 
						ResultadosStatus.OK.getCodigo(), 
						StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()),
						"Conectou no equipamento " + hostnameSwa.toLowerCase(),
						ResultadosAtivacaoSwitch.ACESSOU_EQUIPAMENTO.getNome(),
						ResultadosAtivacaoSwitch.ACESSOU_EQUIPAMENTO.getIndex());		
				
				redeIpFunctions.limpaLog(RedeHelper.retornaInfoProcedimento(AtivacaoSwitchFunctions.class.getName(), configSwitchIdDomain.getIdVantive(), "iniciaProcedimentoAtivacao"));
				
				// lista de scripts de configuracao
				List<ConfigSwitchScriptDomain> lstScriptsCoriant = this.configSwitchScriptRepository.findByFabricanteAndServicosClienteIn("CORIANT", lstServicosCliente);
				
				// AtivacaoSwitchCoriantFunctions
				AtivacaoSwitchCoriantFunctions ativacaoSwitchCoriantFunctions = new AtivacaoSwitchCoriantFunctions(redeIpFunctions, configSwitchIdDomain, lstScriptsCoriant);
				ativacaoSwitchCoriantFunctions.iniciaProcedimentoPreCheckConfiguracao();				
				break;
							
			case NAO_ENCONTRADO:
				
				// Acessou equipamento
				gravaLogResultado.record(TiposTestesEnum.SWITCH.getCodigo(), 
						ResultadosAtivacaoSwitch.ACESSOU_EQUIPAMENTO.getCodigo(), 
						ResultadosStatus.WARNING.getCodigo(), 
						StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()),
						"Conectou no equipamento " + hostnameSwa.toLowerCase() + " mas modelo não encontrado",
						ResultadosAtivacaoSwitch.ACESSOU_EQUIPAMENTO.getNome(),
						ResultadosAtivacaoSwitch.ACESSOU_EQUIPAMENTO.getIndex());		
				
				redeIpFunctions.limpaLog(RedeHelper.retornaInfoProcedimento(AtivacaoSwitchFunctions.class.getName(), configSwitchIdDomain.getIdVantive(), "iniciaProcedimentoAtivacao"));
				
				break;
			default:
				// Saindo do equipamento
				redeIpFunctions.fecharConexaoEquipamento(RedeHelper.retornaInfoProcedimento(AtivacaoSwitchFunctions.class.getName(), configSwitchIdDomain.getIdVantive(), "iniciaProcedimentoAtivacao"));
				redeIpFunctions.limpaLog(RedeHelper.retornaInfoProcedimento(AtivacaoSwitchFunctions.class.getName(), configSwitchIdDomain.getIdVantive(), "iniciaProcedimentoAtivacao"));
				return;
		}
		
		redeIpFunctions.limpaLog(RedeHelper.retornaInfoProcedimento(AtivacaoSwitchFunctions.class.getName(), configSwitchIdDomain.getIdVantive(), "iniciaProcedimentoAtivacaoPe -> limpaLogAux"));
		
		// Saindo do equipamento
		redeIpFunctions.fecharConexaoEquipamento(
				RedeHelper.retornaInfoProcedimento(AtivacaoSwitchFunctions.class.getName(), 
						configSwitchIdDomain.getIdVantive(), 
						"iniciaProcedimentoAtivacao"),
				modeloEquipamento);
		
		// Setando a ProgressBar
		configSwitchIdDomain.setValorProgressBar("90");
	}
	
	/**
	 * Metodos Sets and Gets 
	 */
	
	public RedeIpFunctions getRedeIpFunctions() {
		return redeIpFunctions;
	}

	public void setRedeIpFunctions(RedeIpFunctions redeIpFunctions) {
		this.redeIpFunctions = redeIpFunctions;
	}

	public StringBuilder getLog() {
		return log;
	}

	public void setLog(StringBuilder log) {
		this.log = log;
	}
	
}
