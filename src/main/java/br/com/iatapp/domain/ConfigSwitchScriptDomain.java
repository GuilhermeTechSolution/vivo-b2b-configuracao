package br.com.iatapp.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection="configuracao_switch_scripts")
public class ConfigSwitchScriptDomain implements Serializable {

	private static final long serialVersionUID = 7742272810049195948L;

	@Id
	private String id;	

	@Indexed(name = "_fabricante_01")
	private String fabricante;
	
	private List<String> servicosCliente;
	private String script;
		
	public ConfigSwitchScriptDomain() {}
	
	@Transient
	public String replaceVariaveisScript(ConfigSwitchIdDomain configSwitchIdDomain) {
		
		String scriptConfiguracao = getScript();
		List<String> lstVariaveis = getListaVariaveis();
		
		if (StringUtils.isBlank(scriptConfiguracao)) return null;
		if(configSwitchIdDomain == null || lstVariaveis == null) return scriptConfiguracao;
		
		for(String variavelScript: lstVariaveis) {
			switch (variavelScript) {
			case "'VLAN_GERENCIA_SWA'":
				scriptConfiguracao = scriptConfiguracao.replaceAll(variavelScript, configSwitchIdDomain.getVlanGerenciaSwa());
				break;
			case "'TIPO_INTERFACE'":
				scriptConfiguracao = scriptConfiguracao.replaceAll(variavelScript, configSwitchIdDomain.getTipoInterface());
				break;
			case "'PORTA_CLIENTE'":
				scriptConfiguracao = scriptConfiguracao.replaceAll(variavelScript, configSwitchIdDomain.getPortaCliente());
				break;
			case "'PORTA_CLIENTE_HIFEN'":
				scriptConfiguracao = scriptConfiguracao.replaceAll(variavelScript, configSwitchIdDomain.getPortaCliente().replaceAll("/", "-"));
				break;
			case "'NOME_CENTRAL'":
				scriptConfiguracao = scriptConfiguracao.replaceAll(variavelScript, configSwitchIdDomain.getNomeCentral());
				break;
			case "'NUMERO_SWT'":
				scriptConfiguracao = scriptConfiguracao.replaceAll(variavelScript, configSwitchIdDomain.getNumeroSwitch());
				break;
			case "'IP_SWT'":
				scriptConfiguracao = scriptConfiguracao.replaceAll(variavelScript, configSwitchIdDomain.getIpSwt());
				break;
			case "'IP_SWT_UNDERLINE'":
				scriptConfiguracao = scriptConfiguracao.replaceAll(variavelScript, configSwitchIdDomain.getIpSwt().replaceAll("\\.", "_"));
				break;
			case "'S_VLAN'":
				scriptConfiguracao = scriptConfiguracao.replaceAll(variavelScript, configSwitchIdDomain.getsVlan());
				break;
			case "'C_VLAN'":
				scriptConfiguracao = scriptConfiguracao.replaceAll(variavelScript, configSwitchIdDomain.getcVlan());
				break;				
			case "'SERVICO_CLIENTE'":
				scriptConfiguracao = scriptConfiguracao.replaceAll(variavelScript, configSwitchIdDomain.getServicoCliente());
				break;
			case "'NOME_CLIENTE'":
				scriptConfiguracao = scriptConfiguracao.replaceAll(variavelScript, configSwitchIdDomain.getNomeCliente());
				break;
			case "'VELOCIDADE_LINK'":
				scriptConfiguracao = scriptConfiguracao.replaceAll(variavelScript, configSwitchIdDomain.getVelocidadeLink());
				break;
			case "'ATP'":
				scriptConfiguracao = scriptConfiguracao.replaceAll(variavelScript, configSwitchIdDomain.getAtp());
				break;
			case "'UPLINK_LAG'":
				scriptConfiguracao = scriptConfiguracao.replaceAll(variavelScript, configSwitchIdDomain.getUplinkLag());
				break;	
			default:
				break;
			}
		}
		
		return scriptConfiguracao;
	}
	
	@Transient
	public List<String> getListaVariaveis() {
		List<String> lstVariaveis = new ArrayList<>();
		lstVariaveis.add("'VLAN_GERENCIA_SWA'");
		lstVariaveis.add("'TIPO_INTERFACE'");
		lstVariaveis.add("'PORTA_CLIENTE'");
		lstVariaveis.add("'PORTA_CLIENTE_HIFEN'");
		lstVariaveis.add("'NOME_CENTRAL'");
		lstVariaveis.add("'NUMERO_SWT'");
		lstVariaveis.add("'IP_SWT'");
		lstVariaveis.add("'IP_SWT_UNDERLINE'");
		lstVariaveis.add("'S_VLAN'");
		lstVariaveis.add("'C_VLAN'");
		lstVariaveis.add("'SERVICO_CLIENTE'");
		lstVariaveis.add("'NOME_CLIENTE'");
		lstVariaveis.add("'VELOCIDADE_LINK'");
		lstVariaveis.add("'ATP'");
		lstVariaveis.add("'UPLINK_LAG'");
		return lstVariaveis;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFabricante() {
		return fabricante;
	}

	public void setFabricante(String fabricante) {
		this.fabricante = fabricante;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public List<String> getServicosCliente() {
		return servicosCliente;
	}

	public void setServicosCliente(List<String> servicosCliente) {
		this.servicosCliente = servicosCliente;
	}

}
