package br.com.iatapp.rede;

import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;

import br.com.iatapp.commons.SwitchCoriantCommons;
import br.com.iatapp.domain.ConfigSwitchIdDomain;
import br.com.iatapp.domain.ConfigSwitchScriptDomain;
import br.com.iatapp.enums.ResultadosAtivacaoSwitch;
import br.com.iatapp.enums.ResultadosStatus;
import br.com.iatapp.helper.RedeHelper;
import br.com.iatapp.helper.StringHelper;
import br.com.iatapp.logger.ExceptionLogger;
import br.com.iatapp.logger.GravaLogResultadoSwitch;
import br.com.iatapp.model.RetornoModel;


public class AtivacaoSwitchCoriantFunctions {
	
	private RedeIpFunctions redeIpFunctions;
	private ConfigSwitchIdDomain configSwitchIdDomain;
	private GravaLogResultadoSwitch gravaLogResultado;
	private List<ConfigSwitchScriptDomain> lstScripts;
	
	/**
	 * 
	 * @param redeIpFunctions
	 * @param configSwitchIdDomain
	 */
	public AtivacaoSwitchCoriantFunctions(RedeIpFunctions redeIpFunctions, ConfigSwitchIdDomain configSwitchIdDomain, List<ConfigSwitchScriptDomain> lstScripts) {
		
		try {
			this.redeIpFunctions = redeIpFunctions;
			this.configSwitchIdDomain = configSwitchIdDomain;
			this.gravaLogResultado = configSwitchIdDomain.getGravaLogResultado();
			this.lstScripts = lstScripts;
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e),
					RedeHelper.retornaInfoProcedimento(AtivacaoSwitchCoriantFunctions.class.getName(), configSwitchIdDomain.getHostnameSwa(), "AtivacaoSwitchDatacomFunctions"));
		}
	}
	
	/**
	 * iniciaProcedimentoPreCheckConfiguracao
	 */
	public void iniciaProcedimentoPreCheckConfiguracao() {
		
		String hostname = configSwitchIdDomain.getHostnameSwa().toLowerCase();
		
		SwitchCoriantCommons switchCoriantCommons = new SwitchCoriantCommons(redeIpFunctions, configSwitchIdDomain);
		RetornoModel retornoModel;
		
		// FIND_USER_CONFIGURE
		
		retornoModel = switchCoriantCommons.findUserConfigure(hostname.toLowerCase(), configSwitchIdDomain.getIpSwa());
		if(!retornoModel.isResultado()) {
			gravaLogResultado.record(configSwitchIdDomain.getTipoTeste().getCodigo(), 
					ResultadosAtivacaoSwitch.ACESSOU_EQUIPAMENTO.getCodigo(), 
					ResultadosStatus.FALHOU.getCodigo(), 
					StringHelper.removerCodigoCores(redeIpFunctions.getLog().toString()),
					"Não foi possível conectar no equipamento = " + configSwitchIdDomain.getIpSwa(),
					ResultadosAtivacaoSwitch.ACESSOU_EQUIPAMENTO.getNome(),
					ResultadosAtivacaoSwitch.ACESSOU_EQUIPAMENTO.getIndex());
			
			// Setando a ProgressBar
			configSwitchIdDomain.setValorProgressBar("50");
			
			redeIpFunctions.limpaLog(RedeHelper.retornaInfoProcedimento(AtivacaoSwitchFunctions.class.getName(), configSwitchIdDomain.getIdVantive(), "findUserConfigure"));
			
			return;
		}
		
		// Limpa Log
		redeIpFunctions.limpaLog(RedeHelper.retornaInfoProcedimento(AtivacaoSwitchCoriantFunctions.class.getName(), 
				hostname, "FIND_USER_CONFIGURE"));
		
		// CHECK_STATUS_INTERFACE
		
		retornoModel = switchCoriantCommons.checkStatusInterface(hostname.toLowerCase(), configSwitchIdDomain.getTipoInterface(), configSwitchIdDomain.getPortaCliente());
		gravaLogResultado.record(configSwitchIdDomain.getTipoTeste().getCodigo(), 
				ResultadosAtivacaoSwitch.CHECK_STATUS_INTERFACE.getCodigo(), 
				retornoModel.getCodigo(), 
				retornoModel.getLog().trim(),
				retornoModel.getRetorno(),
				ResultadosAtivacaoSwitch.CHECK_STATUS_INTERFACE.getNome(),
				ResultadosAtivacaoSwitch.CHECK_STATUS_INTERFACE.getIndex());
		
		// Limpa Log
		redeIpFunctions.limpaLog(RedeHelper.retornaInfoProcedimento(AtivacaoSwitchCoriantFunctions.class.getName(), 
				hostname, "CHECK_VALIDACAO_VLAN_GERENCIA_SWA"));
		
		// Progress Bar
		configSwitchIdDomain.setValorProgressBar("25");		
		
		// label etapa teste online
		configSwitchIdDomain.setEtapaTesteOnline("Aplicando Script de Configuração");
		
		// CONFIGURACAO_SCRIPT
				
		if(retornoModel.isResultado()) {			
			retornoModel = switchCoriantCommons.iniciaProcedimentoConfiguracao(configSwitchIdDomain, hostname.toLowerCase(), lstScripts);
			gravaLogResultado.record(configSwitchIdDomain.getTipoTeste().getCodigo(), 
					ResultadosAtivacaoSwitch.CONFIGURACAO_SCRIPT.getCodigo(), 
					retornoModel.getCodigo(), 
					retornoModel.getLog().trim(),
					retornoModel.getRetorno(),
					ResultadosAtivacaoSwitch.CONFIGURACAO_SCRIPT.getNome(),
					ResultadosAtivacaoSwitch.CONFIGURACAO_SCRIPT.getIndex());
			
			// Limpa Log
			redeIpFunctions.limpaLog(RedeHelper.retornaInfoProcedimento(AtivacaoSwitchCoriantFunctions.class.getName(), 
					hostname, "CONFIGURACAO_SCRIPT"));
		} else {
			gravaLogResultado.record(configSwitchIdDomain.getTipoTeste().getCodigo(), 
					ResultadosAtivacaoSwitch.CONFIGURACAO_SCRIPT.getCodigo(), 
					ResultadosStatus.WARNING.getCodigo(), 
					"",
					"Script não foi aplicado porque o Check de Status da Interface Falhou",
					ResultadosAtivacaoSwitch.CONFIGURACAO_SCRIPT.getNome(),
					ResultadosAtivacaoSwitch.CONFIGURACAO_SCRIPT.getIndex());
		}
		
		// Limpa Log
		redeIpFunctions.limpaLog(RedeHelper.retornaInfoProcedimento(AtivacaoSwitchCoriantFunctions.class.getName(), 
				hostname, "CONFIGURACAO_SCRIPT"));

		// Progress Bar
		configSwitchIdDomain.setValorProgressBar("35");	
		
		// label etapa teste online
		configSwitchIdDomain.setEtapaTesteOnline("Finalizando Teste");
		
//		// CHECK_VALIDACAO_VLAN_GERENCIA
//		
//		retornoModel = switchCoriantCommons.checkVlanGerencia(hostname.toLowerCase(), configSwitchIdDomain.getVlanGerenciaSwa());
//		gravaLogResultado.record(configSwitchIdDomain.getTipoTeste().getCodigo(), 
//				ResultadosAtivacaoSwitch.CHECK_VALIDACAO_VLAN_GERENCIA_SWA.getCodigo(), 
//				retornoModel.getCodigo(), 
//				retornoModel.getLog().trim(),
//				retornoModel.getRetorno(),
//				ResultadosAtivacaoSwitch.CHECK_VALIDACAO_VLAN_GERENCIA_SWA.getNome(),
//				ResultadosAtivacaoSwitch.CHECK_VALIDACAO_VLAN_GERENCIA_SWA.getIndex());
//		
//		// Limpa Log
//		redeIpFunctions.limpaLog(RedeHelper.retornaInfoProcedimento(AtivacaoSwitchCoriantFunctions.class.getName(), 
//				hostname, "CHECK_VALIDACAO_VLAN_GERENCIA_SWA"));
//		
//		// Progress Bar
//		configSwitchIdDomain.setValorProgressBar("55");		
//		
//		// CHECK_VALIDACAO_VLAN_TUNEL_SERVICO
//		
//		retornoModel = switchCoriantCommons.checkVlanTunelServico(hostname.toLowerCase(), configSwitchIdDomain);
//		gravaLogResultado.record(configSwitchIdDomain.getTipoTeste().getCodigo(), 
//				ResultadosAtivacaoSwitch.CHECK_VALIDACAO_VLAN_TUNEL_SERVICO.getCodigo(), 
//				retornoModel.getCodigo(), 
//				retornoModel.getLog().trim(),
//				retornoModel.getRetorno(),
//				ResultadosAtivacaoSwitch.CHECK_VALIDACAO_VLAN_TUNEL_SERVICO.getNome(),
//				ResultadosAtivacaoSwitch.CHECK_VALIDACAO_VLAN_TUNEL_SERVICO.getIndex());
//		
//		// Limpa Log
//		redeIpFunctions.limpaLog(RedeHelper.retornaInfoProcedimento(AtivacaoSwitchCoriantFunctions.class.getName(), 
//				hostname, "CHECK_VALIDACAO_VLAN_TUNEL_SERVICO"));
//		
//		// Progress Bar
//		configSwitchIdDomain.setValorProgressBar("60");		
//		
//		// CHECK_CONFIGURACAO_INTERFACE
//		
//		retornoModel = switchCoriantCommons.checkConfiguracaoInterface(hostname.toLowerCase(), configSwitchIdDomain);
//		gravaLogResultado.record(configSwitchIdDomain.getTipoTeste().getCodigo(), 
//				ResultadosAtivacaoSwitch.CHECK_CONFIGURACAO_INTERFACE.getCodigo(), 
//				retornoModel.getCodigo(), 
//				retornoModel.getLog().trim(),
//				retornoModel.getRetorno(),
//				ResultadosAtivacaoSwitch.CHECK_CONFIGURACAO_INTERFACE.getNome(),
//				ResultadosAtivacaoSwitch.CHECK_CONFIGURACAO_INTERFACE.getIndex());
//		
//		// Limpa Log
//		redeIpFunctions.limpaLog(RedeHelper.retornaInfoProcedimento(AtivacaoSwitchCoriantFunctions.class.getName(), 
//				hostname, "CHECK_CONFIGURACAO_INTERFACE"));
//		
//		// Progress Bar
//		configSwitchIdDomain.setValorProgressBar("70");		
//		
//		// CHECK_VALIDACAO_VLAN_TABLE
//		
//		retornoModel = switchCoriantCommons.checkValidacaoVlanTable(hostname.toLowerCase(), configSwitchIdDomain);
//		gravaLogResultado.record(configSwitchIdDomain.getTipoTeste().getCodigo(), 
//				ResultadosAtivacaoSwitch.CHECK_VALIDACAO_VLAN_TABLE.getCodigo(), 
//				retornoModel.getCodigo(), 
//				retornoModel.getLog().trim(),
//				retornoModel.getRetorno(),
//				ResultadosAtivacaoSwitch.CHECK_VALIDACAO_VLAN_TABLE.getNome(),
//				ResultadosAtivacaoSwitch.CHECK_VALIDACAO_VLAN_TABLE.getIndex());
//		
//		// Limpa Log
//		redeIpFunctions.limpaLog(RedeHelper.retornaInfoProcedimento(AtivacaoSwitchCoriantFunctions.class.getName(), 
//				hostname, "CHECK_VALIDACAO_VLAN_TABLE"));
//		
//		// Progress Bar
//		configSwitchIdDomain.setValorProgressBar("85");		
	}	
	
	/**
	 * Metodos Gets and Sets 
	 */

	public RedeIpFunctions getRedeIpFunctions() {
		return redeIpFunctions;
	}

	public void setRedeIpFunctions(RedeIpFunctions redeIpFunctions) {
		this.redeIpFunctions = redeIpFunctions;
	}

	public ConfigSwitchIdDomain getConfigSwitchIdDomain() {
		return configSwitchIdDomain;
	}

	public void setConfigSwitchIdDomain(ConfigSwitchIdDomain configSwitchIdDomain) {
		this.configSwitchIdDomain = configSwitchIdDomain;
	}

	public GravaLogResultadoSwitch getGravaLogResultado() {
		return gravaLogResultado;
	}

	public void setGravaLogResultado(GravaLogResultadoSwitch gravaLogResultado) {
		this.gravaLogResultado = gravaLogResultado;
	}
	
}