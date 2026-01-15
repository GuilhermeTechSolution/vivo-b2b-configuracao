package br.com.iatapp.rede;

import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;

import br.com.iatapp.commons.Vivo2JuniperCommons;
import br.com.iatapp.domain.Vivo2IdDomain;
import br.com.iatapp.domain.Vivo2ScriptDomain;
import br.com.iatapp.enums.ResultadosAtivacaoVivo2;
import br.com.iatapp.enums.ResultadosStatus;
import br.com.iatapp.helper.RedeHelper;
import br.com.iatapp.logger.ExceptionLogger;
import br.com.iatapp.logger.GravaLogResultadoVivo2;
import br.com.iatapp.model.RetornoModel;

public class AtivacaoVivo2JuniperFunctions {
	
	private RedeIpFunctions redeIpFunctions;
	private Vivo2IdDomain vivo2IdDomain;
	private GravaLogResultadoVivo2 gravaLogResultado;
	private List<Vivo2ScriptDomain> lstScripts;
	
	public AtivacaoVivo2JuniperFunctions(RedeIpFunctions redeIpFunctions) {
		this.redeIpFunctions = redeIpFunctions;
	}
	
	/**
	 * AtivacaoVivo2JuniperFunctions
	 * @param redeIpFunctions
	 * @param vivo2IdDomain
	 */
	public AtivacaoVivo2JuniperFunctions(RedeIpFunctions redeIpFunctions, Vivo2IdDomain vivo2IdDomain, List<Vivo2ScriptDomain> lstScripts) {
		
		try {
			this.redeIpFunctions = redeIpFunctions;
			this.vivo2IdDomain = vivo2IdDomain;
			this.gravaLogResultado = vivo2IdDomain.getGravaLogResultado();
			this.lstScripts = lstScripts;
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e),
					RedeHelper.retornaInfoProcedimento(AtivacaoVivo2JuniperFunctions.class.getName(), vivo2IdDomain.getIdTbs(), "AtivacaoVivo2CiscoFunctions"));
		}
	}
	
	/**
	 * Método que gerencia o teste PE
	 */
	
	public void iniciaProcedimento() {
		
		String hostnameEquipamento = vivo2IdDomain.getHostnameEquipamento().toLowerCase();
		
		Vivo2JuniperCommons vivo2JuniperCommons = new Vivo2JuniperCommons(redeIpFunctions, vivo2IdDomain);
		RetornoModel retornoModel;
		
		// CHECK_VALIDACAO_IPS_LIVRES
		
		if(vivo2IdDomain.getServico().equals("SIP")) {
			retornoModel = vivo2JuniperCommons.checkVrfSipExiste();
			gravaLogResultado.record(vivo2IdDomain.getTipoTeste().getCodigo(), 
					ResultadosAtivacaoVivo2.CHECK_VRF_SIP.getCodigo(), 
					retornoModel.getCodigo(), 
					retornoModel.getLog().trim(),
					retornoModel.getRetorno(),
					ResultadosAtivacaoVivo2.CHECK_VRF_SIP.getNome(),
					ResultadosAtivacaoVivo2.CHECK_VRF_SIP.getIndex());
			
			if(!retornoModel.isResultado())
				return;
		}
		
		retornoModel = vivo2JuniperCommons.checkIpsLivres();
		gravaLogResultado.record(vivo2IdDomain.getTipoTeste().getCodigo(), 
				ResultadosAtivacaoVivo2.CHECK_VALIDACAO_IPS_LIVRES.getCodigo(), 
				retornoModel.getCodigo(), 
				retornoModel.getLog().trim(),
				retornoModel.getRetorno(),
				ResultadosAtivacaoVivo2.CHECK_VALIDACAO_IPS_LIVRES.getNome(),
				ResultadosAtivacaoVivo2.CHECK_VALIDACAO_IPS_LIVRES.getIndex());
		
		// Limpa Log
		redeIpFunctions.limpaLog(RedeHelper.retornaInfoProcedimento(AtivacaoVivo2JuniperFunctions.class.getName(), 
				hostnameEquipamento, "CHECK_VALIDACAO_IPS_LIVRES"));
		
		// Progress Bar
		vivo2IdDomain.setValorProgressBar("25");
		
		if(!retornoModel.isResultado())
			return;
		
		// CHECK_INTERFACE_CONFIGURACAO
		
		retornoModel = vivo2JuniperCommons.checkInterfaceConfiguracao();
		gravaLogResultado.record(vivo2IdDomain.getTipoTeste().getCodigo(), 
				ResultadosAtivacaoVivo2.CHECK_INTERFACE_CONFIGURACAO.getCodigo(), 
				retornoModel.getCodigo(), 
				retornoModel.getLog().trim(),
				retornoModel.getRetorno(),
				ResultadosAtivacaoVivo2.CHECK_INTERFACE_CONFIGURACAO.getNome(),
				ResultadosAtivacaoVivo2.CHECK_INTERFACE_CONFIGURACAO.getIndex());
		
		// Limpa Log
		redeIpFunctions.limpaLog(RedeHelper.retornaInfoProcedimento(AtivacaoVivo2JuniperFunctions.class.getName(), 
				hostnameEquipamento, "CHECK_VLANS"));
		
		// Progress Bar
		vivo2IdDomain.setValorProgressBar("30");
		
		// CONFIGURACAO_SCRIPT
		
		if(retornoModel.isResultado()) {			
			retornoModel = vivo2JuniperCommons.iniciaProcedimentoConfiguracao(lstScripts);
			gravaLogResultado.record(vivo2IdDomain.getTipoTeste().getCodigo(), 
					ResultadosAtivacaoVivo2.CONFIGURACAO_SCRIPT.getCodigo(), 
					retornoModel.getCodigo(), 
					retornoModel.getLog().trim(),
					retornoModel.getRetorno(),
					ResultadosAtivacaoVivo2.CONFIGURACAO_SCRIPT.getNome(),
					ResultadosAtivacaoVivo2.CONFIGURACAO_SCRIPT.getIndex());
			
		} else {
			gravaLogResultado.record(vivo2IdDomain.getTipoTeste().getCodigo(), 
					ResultadosAtivacaoVivo2.CONFIGURACAO_SCRIPT.getCodigo(), 
					ResultadosStatus.WARNING.getCodigo(), 
					"",
					"Script não foi aplicado porque o Check da Interface Falhou",
					ResultadosAtivacaoVivo2.CONFIGURACAO_SCRIPT.getNome(),
					ResultadosAtivacaoVivo2.CONFIGURACAO_SCRIPT.getIndex());
		}
		
		// Limpa Log
		redeIpFunctions.limpaLog(RedeHelper.retornaInfoProcedimento(AtivacaoVivo2JuniperFunctions.class.getName(), 
				hostnameEquipamento, "CONFIGURACAO_SCRIPT"));
		
		// CHECK_CONFIGURACAO_IPS
		
		retornoModel = vivo2JuniperCommons.checkConfiguracaoIps();
		gravaLogResultado.record(vivo2IdDomain.getTipoTeste().getCodigo(), 
				ResultadosAtivacaoVivo2.CHECK_CONFIGURACAO_IPS.getCodigo(), 
				retornoModel.getCodigo(), 
				retornoModel.getLog().trim(),
				retornoModel.getRetorno(),
				ResultadosAtivacaoVivo2.CHECK_CONFIGURACAO_IPS.getNome(),
				ResultadosAtivacaoVivo2.CHECK_CONFIGURACAO_IPS.getIndex());
		
		// Limpa Log
		redeIpFunctions.limpaLog(RedeHelper.retornaInfoProcedimento(AtivacaoVivo2JuniperFunctions.class.getName(), 
				hostnameEquipamento, "CHECK_CONFIGURACAO_IPS"));
		
		// Progress Bar
		vivo2IdDomain.setValorProgressBar("25");
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

	public Vivo2IdDomain getVivo2IdDomain() {
		return vivo2IdDomain;
	}

	public void setVivo2IdDomain(Vivo2IdDomain vivo2IdDomain) {
		this.vivo2IdDomain = vivo2IdDomain;
	}
	
}