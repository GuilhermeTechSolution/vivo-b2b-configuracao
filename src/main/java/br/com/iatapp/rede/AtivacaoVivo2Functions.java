package br.com.iatapp.rede;


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import br.com.iatapp.domain.Vivo2IdDomain;
import br.com.iatapp.domain.Vivo2ScriptDomain;
import br.com.iatapp.enums.ModelosEquipamentosEnum;
import br.com.iatapp.enums.ResultadosAtivacaoVivo2;
import br.com.iatapp.enums.ResultadosStatus;
import br.com.iatapp.enums.TiposTestesEnum;
import br.com.iatapp.helper.RedeHelper;
import br.com.iatapp.helper.StringHelper;
import br.com.iatapp.logger.GravaLogResultadoVivo2;
import br.com.iatapp.repositories.Vivo2ScriptRepository;

/**
 * 
 * @author ottap
 *
 */
public class AtivacaoVivo2Functions {
	
	private RedeIpFunctions redeIpFunctions;
	private Vivo2IdDomain vivo2IdDomain;
	private StringBuilder log;
	private GravaLogResultadoVivo2 gravaLogResultado;
	private Vivo2ScriptRepository vivo2ScriptRepository;
	
	public AtivacaoVivo2Functions(RedeIpFunctions redeIpFunctions, Vivo2IdDomain vivo2IdDomain, Vivo2ScriptRepository vivo2ScriptRepository) {
		this.redeIpFunctions = redeIpFunctions;
		this.vivo2IdDomain = vivo2IdDomain;
		this.log = new StringBuilder();
		this.gravaLogResultado = vivo2IdDomain.getGravaLogResultado();
		this.vivo2ScriptRepository = vivo2ScriptRepository;
	}
	
	/**
	 * iniciaProcedimentoConfiguracao
	 * @param equipamento
	 */
	public void iniciaProcedimentoConfiguracao() {
		
		// label etapa teste online
		vivo2IdDomain.setEtapaTesteOnline("Executando Pré Check da Configuração");
		
		// Conectando no equipamento
		
		// Conectando no PE Principal
		if(!redeIpFunctions.conectarEquipamentoForConfig(vivo2IdDomain.getBackbone().toLowerCase(),
				RedeHelper.retornaInfoProcedimento(AtivacaoVivo2Functions.class.getName(), vivo2IdDomain.getIdTbs() + " config", "iniciaProcedimentoConfiguracao"))) {
			
			gravaLogResultado.record(TiposTestesEnum.VIVO_2.getCodigo(), 
					ResultadosAtivacaoVivo2.ACESSOU_EQUIPAMENTO.getCodigo(), 
					ResultadosStatus.WARNING.getCodigo(), 
					StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()),
					"Não foi possível conectar no equipamento = " + vivo2IdDomain.getBackbone().toLowerCase(),
					ResultadosAtivacaoVivo2.ACESSOU_EQUIPAMENTO.getNome(),
					ResultadosAtivacaoVivo2.ACESSOU_EQUIPAMENTO.getIndex());
			
			// Setando a ProgressBar
			vivo2IdDomain.setValorProgressBar("50");
			
			redeIpFunctions.limpaLog(RedeHelper.retornaInfoProcedimento(AtivacaoVivo2Functions.class.getName(), vivo2IdDomain.getIdTbs(), "iniciaProcedimentoConfiguracao"));
			
			return;
		}
		
		// Setando a ProgressBar
		vivo2IdDomain.setValorProgressBar("10");
		
		// pegando o hostname do equipamento
		String hostnameEquipamento = redeIpFunctions.getHostnameEquipamento("");
		if(StringUtils.isBlank(hostnameEquipamento)) {
			gravaLogResultado.record(TiposTestesEnum.VIVO_2.getCodigo(), 
					ResultadosAtivacaoVivo2.ACESSOU_EQUIPAMENTO.getCodigo(), 
					ResultadosStatus.WARNING.getCodigo(), 
					StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()),
					"Não foi possível conectar no equipamento = " + vivo2IdDomain.getBackbone().toLowerCase(),
					ResultadosAtivacaoVivo2.ACESSOU_EQUIPAMENTO.getNome(),
					ResultadosAtivacaoVivo2.ACESSOU_EQUIPAMENTO.getIndex());
			
			// Setando a ProgressBar
			vivo2IdDomain.setValorProgressBar("50");
			
			redeIpFunctions.limpaLog(RedeHelper.retornaInfoProcedimento(AtivacaoVivo2Functions.class.getName(), vivo2IdDomain.getIdTbs(), "iniciaProcedimentoConfiguracao"));
			
			return;
		}
		
		vivo2IdDomain.setHostnameEquipamento(hostnameEquipamento);
		
		// Descobrindo o modelo do equipamento
		
		int modeloEquipamento = redeIpFunctions.retornaModeloEquipamento(hostnameEquipamento,
				RedeHelper.retornaInfoProcedimento(AtivacaoVivo2Functions.class.getName(), vivo2IdDomain.getIdTbs(), "iniciaProcedimentoConfiguracao"),
				StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()));
		
		// Seta o modelo do Equipamento
		vivo2IdDomain.setIdModeloEquipamento(modeloEquipamento);
		vivo2IdDomain.setNomeModeloEquipamento(ModelosEquipamentosEnum.valueOf(modeloEquipamento).getNome());

		vivo2IdDomain.setTipoTeste(TiposTestesEnum.VIVO_2);
		
		List<String> lstServicos = new ArrayList<String>();
		lstServicos.add(vivo2IdDomain.getServico());
		
		List<Vivo2ScriptDomain> lstScripts = null;
		
		switch (ModelosEquipamentosEnum.valueOf(modeloEquipamento)) {
		
			case CISCO_XE:
			case CISCO_XR:
				
				// Acessou equipamento
				gravaLogResultado.record(TiposTestesEnum.VIVO_2.getCodigo(), 
						ResultadosAtivacaoVivo2.ACESSOU_EQUIPAMENTO.getCodigo(), 
						ResultadosStatus.OK.getCodigo(), 
						StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()),
						"Conectou no equipamento = " + vivo2IdDomain.getBackbone().toLowerCase(),
						ResultadosAtivacaoVivo2.ACESSOU_EQUIPAMENTO.getNome(),
						ResultadosAtivacaoVivo2.ACESSOU_EQUIPAMENTO.getIndex());
				
				// lista de scripts de configuracao
				if(modeloEquipamento == ModelosEquipamentosEnum.CISCO_XR.getCodigo())
					lstScripts = this.vivo2ScriptRepository.findByFabricanteAndServicosClienteIn("CISCO_XR", lstServicos);
				else 
					lstScripts = this.vivo2ScriptRepository.findByFabricanteAndServicosClienteIn("CISCO_XE", lstServicos);
				
				// AtivacaoVivo2CiscoFunctions
				AtivacaoVivo2CiscoFunctions ativacaoVivo2CiscoFunctions = new AtivacaoVivo2CiscoFunctions(redeIpFunctions, vivo2IdDomain, lstScripts);
				ativacaoVivo2CiscoFunctions.iniciaProcedimento();
				
				break;
				
			case HUAWEI:
				
				// Acessou equipamento
				gravaLogResultado.record(TiposTestesEnum.VIVO_2.getCodigo(), 
						ResultadosAtivacaoVivo2.ACESSOU_EQUIPAMENTO.getCodigo(), 
						ResultadosStatus.OK.getCodigo(), 
						StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()),
						"Conectou no equipamento = " + vivo2IdDomain.getBackbone().toLowerCase(),
						ResultadosAtivacaoVivo2.ACESSOU_EQUIPAMENTO.getNome(),
						ResultadosAtivacaoVivo2.ACESSOU_EQUIPAMENTO.getIndex());
				
				// lista de scripts de configuracao
				lstScripts = this.vivo2ScriptRepository.findByFabricanteAndServicosClienteIn("HUAWEI", lstServicos);
				
				// AtivacaoVivo2HuaweiFunctions
				AtivacaoVivo2HuaweiFunctions ativacaoVivo2HuaweiFunctions = new AtivacaoVivo2HuaweiFunctions(redeIpFunctions, vivo2IdDomain, lstScripts);
				ativacaoVivo2HuaweiFunctions.iniciaProcedimento();
				
				break;
				
			case JUNIPER:
				
				// Acessou equipamento
				gravaLogResultado.record(TiposTestesEnum.VIVO_2.getCodigo(), 
						ResultadosAtivacaoVivo2.ACESSOU_EQUIPAMENTO.getCodigo(), 
						ResultadosStatus.OK.getCodigo(), 
						StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()),
						"Conectou no equipamento = " + vivo2IdDomain.getBackbone().toLowerCase(),
						ResultadosAtivacaoVivo2.ACESSOU_EQUIPAMENTO.getNome(),
						ResultadosAtivacaoVivo2.ACESSOU_EQUIPAMENTO.getIndex());
				
				// lista de scripts de configuracao
				lstScripts = this.vivo2ScriptRepository.findByFabricanteAndServicosClienteIn("JUNIPER", lstServicos);
				
				// AtivacaoVivo2JuniperFunctions
				AtivacaoVivo2JuniperFunctions ativacaoVivo2JuniperFunctions = new AtivacaoVivo2JuniperFunctions(redeIpFunctions, vivo2IdDomain, lstScripts);
				ativacaoVivo2JuniperFunctions.iniciaProcedimento();
				
				break;
				
			case NAO_ENCONTRADO:
				
				// Acessou equipamento
				gravaLogResultado.record(TiposTestesEnum.VIVO_2.getCodigo(), 
						ResultadosAtivacaoVivo2.ACESSOU_EQUIPAMENTO.getCodigo(), 
						ResultadosStatus.WARNING.getCodigo(), 
						StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()),
						"Conectou no equipamento " + hostnameEquipamento.toLowerCase() + " mas modelo não encontrado",
						ResultadosAtivacaoVivo2.ACESSOU_EQUIPAMENTO.getNome(),
						ResultadosAtivacaoVivo2.ACESSOU_EQUIPAMENTO.getIndex());
				
				redeIpFunctions.limpaLog(RedeHelper.retornaInfoProcedimento(AtivacaoVivo2Functions.class.getName(), vivo2IdDomain.getIdTbs(), "iniciaProcedimentoConfiguracao"));
				
				break;
			default:
				// Saindo do equipamento
				redeIpFunctions.fecharConexaoEquipamento(RedeHelper.retornaInfoProcedimento(AtivacaoVivo2Functions.class.getName(), vivo2IdDomain.getIdTbs(), "iniciaProcedimentoConfiguracao"));
				redeIpFunctions.limpaLog(RedeHelper.retornaInfoProcedimento(AtivacaoVivo2Functions.class.getName(), vivo2IdDomain.getIdTbs(), "iniciaProcedimentoConfiguracao"));
				return;
		}
		
		redeIpFunctions.limpaLog(RedeHelper.retornaInfoProcedimento(AtivacaoVivo2Functions.class.getName(), vivo2IdDomain.getIdTbs(), "iniciaProcedimentoConfiguracao"));
		
		// Saindo do equipamento
		redeIpFunctions.fecharConexaoEquipamento(
				RedeHelper.retornaInfoProcedimento(AtivacaoVivo2Functions.class.getName(), 
						vivo2IdDomain.getIdTbs(), 
						"iniciaProcedimentoConfiguracao"),
				modeloEquipamento);
		
		// Setando a ProgressBar
		vivo2IdDomain.setValorProgressBar("90");
	}
	
	/**
	 * iniciaProcedimentoBuscaSwitch
	 * @param equipamento
	 */
	public void iniciaProcedimentoBuscaInterfaceSwitch() {
		
		// label etapa teste online
		vivo2IdDomain.setEtapaTesteOnline("Buscando informação no Switch");
		
		// preparando o nome do hostname switch
		String[] values = vivo2IdDomain.getBackbone().split("-");
		if (values == null || values.length != 7)
			return;
			
		String hostnameSwitch = "l-" 
				+ values[1] + "-" 
				+ values[2]+ "-"  
				+ values[3]+ "-"  
				+ values[4]+ "-"  
				+ "scr-01";
		vivo2IdDomain.setHostnameSwitch(hostnameSwitch);
		
		// Conectando no equipamento
		
		boolean conectou = false;
		for (int c = 0; c < 5; c++) {
			// Conectando no PE Principal
			if(!redeIpFunctions.conectarEquipamentoForConfig(vivo2IdDomain.getHostnameSwitch().toLowerCase(),
					RedeHelper.retornaInfoProcedimento(AtivacaoVivo2Functions.class.getName(), vivo2IdDomain.getIdTbs() + " config", "iniciaProcedimentoConfiguracao"))) {
				try {Thread.sleep(3000);} catch (InterruptedException e) {e.printStackTrace();}
				continue;
			}
			conectou = true;
			break;
		}
			
		if (!conectou) {
		
			hostnameSwitch = "l-" 
					+ values[1] + "-" 
					+ values[2]+ "-"  
					+ values[3]+ "-"  
					+ values[4]+ "-"  
					+ "scr-02";
			vivo2IdDomain.setHostnameSwitch(hostnameSwitch);
			
			for (int c = 0; c < 3; c++) {
				// Conectando no PE Principal
				if(!redeIpFunctions.conectarEquipamentoForConfig(vivo2IdDomain.getHostnameSwitch().toLowerCase(),
						RedeHelper.retornaInfoProcedimento(AtivacaoVivo2Functions.class.getName(), vivo2IdDomain.getIdTbs() + " config", "iniciaProcedimentoConfiguracao"))) {
					try {Thread.sleep(3000);} catch (InterruptedException e) {e.printStackTrace();}
					continue;
				}
				conectou = true;
				break;
			}
			
			if (!conectou) {
				// Setando a ProgressBar
				vivo2IdDomain.setValorProgressBar("10");
				
				redeIpFunctions.limpaLog(RedeHelper.retornaInfoProcedimento(AtivacaoVivo2Functions.class.getName(), vivo2IdDomain.getIdTbs(), "iniciaProcedimentoConfiguracao"));
				
				return;
			}	
		}
		
		// Setando a ProgressBar
		vivo2IdDomain.setValorProgressBar("10");
		
		// pegando o hostname do equipamento
		String hostnameEquipamento = redeIpFunctions.getHostnameEquipamento("");
		if(StringUtils.isBlank(hostnameEquipamento)) {
			// Setando a ProgressBar
			vivo2IdDomain.setValorProgressBar("10");
			
			redeIpFunctions.limpaLog(RedeHelper.retornaInfoProcedimento(AtivacaoVivo2Functions.class.getName(), vivo2IdDomain.getIdTbs(), "iniciaProcedimentoConfiguracao"));
			
			return;
		}
		
		// Descobrindo o modelo do equipamento
		
		int modeloEquipamento = redeIpFunctions.retornaModeloEquipamento(hostnameEquipamento,
				RedeHelper.retornaInfoProcedimento(AtivacaoVivo2Functions.class.getName(), vivo2IdDomain.getIdTbs(), "iniciaProcedimentoConfiguracao"),
				StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()));
		
		// Seta o modelo do Equipamento
		vivo2IdDomain.setIdModeloSwitch(modeloEquipamento);
		vivo2IdDomain.setNomeModeloSwitch(ModelosEquipamentosEnum.valueOf(modeloEquipamento).getNome());

		vivo2IdDomain.setTipoTeste(TiposTestesEnum.VIVO_2);
		
		switch (ModelosEquipamentosEnum.valueOf(modeloEquipamento)) {
		
			case BROCADE:
				
				// AtivacaoVivo2BrocadeFunctions
				AtivacaoVivo2BrocadeFunctions ativacaoVivo2BrocadeFunctions = new AtivacaoVivo2BrocadeFunctions(redeIpFunctions, vivo2IdDomain);
				ativacaoVivo2BrocadeFunctions.iniciaBuscaInterface(hostnameEquipamento);	
				ativacaoVivo2BrocadeFunctions.getLogsVlan(hostnameEquipamento);						
				break;
				
			case NAO_ENCONTRADO:
				break;
			default:
				// Saindo do equipamento
				redeIpFunctions.fecharConexaoEquipamento(RedeHelper.retornaInfoProcedimento(AtivacaoVivo2Functions.class.getName(), vivo2IdDomain.getIdTbs(), "iniciaProcedimentoConfiguracao"));
				redeIpFunctions.limpaLog(RedeHelper.retornaInfoProcedimento(AtivacaoVivo2Functions.class.getName(), vivo2IdDomain.getIdTbs(), "iniciaProcedimentoConfiguracao"));
				return;
		}
		
		redeIpFunctions.limpaLog(RedeHelper.retornaInfoProcedimento(AtivacaoVivo2Functions.class.getName(), vivo2IdDomain.getIdTbs(), "iniciaProcedimentoConfiguracao"));
		
		// Saindo do equipamento
		redeIpFunctions.fecharConexaoEquipamento(
				RedeHelper.retornaInfoProcedimento(AtivacaoVivo2Functions.class.getName(), 
						vivo2IdDomain.getIdTbs(), 
						"iniciaProcedimentoConfiguracao"),
				modeloEquipamento);
		
		// Setando a ProgressBar
		vivo2IdDomain.setValorProgressBar("15");
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
