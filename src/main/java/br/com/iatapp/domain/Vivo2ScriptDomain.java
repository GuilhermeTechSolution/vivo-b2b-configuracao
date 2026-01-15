package br.com.iatapp.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.googlecode.ipv6.IPv6Address;

import br.com.iatapp.enums.BandwidthEnum;
import br.com.iatapp.enums.GlobalStrEnum;
import br.com.iatapp.enums.JsonKeysEnum;
import br.com.iatapp.helper.RedeHelper;
import br.com.iatapp.helper.StringHelper;


@Document(collection="vivo_2_scripts")
public class Vivo2ScriptDomain implements Serializable {

	private static final long serialVersionUID = 7742272810049195948L;

	@Id
	private String id;	

	@Indexed(name = "_fabricante_01")
	private String fabricante;
	
	private List<String> servicosCliente;
	private String script;
		
	public Vivo2ScriptDomain() {}
	
	@Transient
	public String replaceVariaveisScript(Vivo2IdDomain vivo2IdDomain) {
		
		String scriptConfiguracao = getScript();
		List<String> lstVariaveis = getListaVariaveis();
		
		if (StringUtils.isBlank(scriptConfiguracao)) return null;
		if(vivo2IdDomain == null || lstVariaveis == null) return scriptConfiguracao;
		
		JSONObject jsonDados = vivo2IdDomain.getJsonDados();
		
		String aux = "";
		for(String variavelScript: lstVariaveis) {
			switch (variavelScript) {
			case "'INTERFACE_CONFIGURACAO'":
				scriptConfiguracao = scriptConfiguracao.replaceAll(variavelScript, jsonDados.getString(JsonKeysEnum.VIVO_2_INTERFACE_CONFIGURACAO.getCodigo()));
				break;
			case "'NOME_CLIENTE'":
				String cliente = vivo2IdDomain.getCliente();
				if (cliente.length() >= 12)
					cliente = cliente.substring(0, 12);
				scriptConfiguracao = scriptConfiguracao.replaceAll(variavelScript, cliente);
				break;
			case "'ID_TBS'":
				scriptConfiguracao = scriptConfiguracao.replaceAll(variavelScript, vivo2IdDomain.getIdTbs());
				break;
			case "'VELOCIDADE_CLIENTE'":
				scriptConfiguracao = scriptConfiguracao.replaceAll(variavelScript, BandwidthEnum.retornaVelocidadeReduzidoStr(vivo2IdDomain.getIdUnidadeVelocidadeLink(), vivo2IdDomain.getVelocidadeLink()));
				break;
			case "'VLAN_REDE'":
				scriptConfiguracao = scriptConfiguracao.replaceAll(variavelScript, vivo2IdDomain.getVlanRede());
				break;
			case "'VLAN_USUARIO'":
				scriptConfiguracao = scriptConfiguracao.replaceAll(variavelScript, vivo2IdDomain.getVlanUsuario());
				break;
			case "'DESIGNADOR'":
				scriptConfiguracao = scriptConfiguracao.replaceAll(variavelScript, vivo2IdDomain.getDesignador());
				break;
			case "'CIDADE'":
				scriptConfiguracao = scriptConfiguracao.replaceAll(variavelScript, vivo2IdDomain.getCidade());
				break;				
			case "'VRF'":
				scriptConfiguracao = scriptConfiguracao.replaceAll(variavelScript, vivo2IdDomain.getVrf());
				break;
				
			case "'IPV4_WAN'":
				
				String ipv4Wan = vivo2IdDomain.getIpWan();
				if(vivo2IdDomain.getMascaraWan().equals("30") || vivo2IdDomain.getMascaraWan().equals("255.255.255.252"))
					ipv4Wan = RedeHelper.retornaIpWanPar(ipv4Wan);
				
				scriptConfiguracao = scriptConfiguracao.replaceAll(variavelScript, ipv4Wan);
				break;
				
			case "'MASCARA_IPV4_WAN_IP'":
				aux = StringHelper.searchPattern(vivo2IdDomain.getMascaraWan(), GlobalStrEnum.IPADDRESS_PATTERN.toString());
				if(StringUtils.isBlank(aux))
					aux = RedeHelper.getMascaraIp(vivo2IdDomain.getMascaraWan());
				
				if(StringUtils.isBlank(aux))
					aux = vivo2IdDomain.getMascaraWan();
				else
					vivo2IdDomain.setMascaraWan(aux);
				
				scriptConfiguracao = scriptConfiguracao.replaceAll(variavelScript, aux);
				break;
				
			case "'MASCARA_IPV4_WAN_NUMERO'":
				aux = StringHelper.searchPattern(vivo2IdDomain.getMascaraWan(), GlobalStrEnum.IPADDRESS_PATTERN.toString());
				if(StringUtils.isNotBlank(aux))
					aux = RedeHelper.getMascaraInverse(aux);
				
				scriptConfiguracao = scriptConfiguracao.replaceAll(variavelScript, aux);
				break;
				
			case "'IPV6_WAN'":
				scriptConfiguracao = scriptConfiguracao.replaceAll(variavelScript, vivo2IdDomain.getIpv6Wan());
				break;
				
			case "'MASCARA_IPV6_WAN'":
				scriptConfiguracao = scriptConfiguracao.replaceAll(variavelScript, vivo2IdDomain.getMascaraIpv6Wan());
				break;
			
			case "'IPV4_LAN'":
				scriptConfiguracao = scriptConfiguracao.replaceAll(variavelScript, vivo2IdDomain.getIpLan());
				break;
				
			case "'MASCARA_IPV4_LAN'":
				aux = StringHelper.searchPattern(vivo2IdDomain.getMascaraLan(), GlobalStrEnum.IPADDRESS_PATTERN.toString());
				if(StringUtils.isNotBlank(aux))
					aux = RedeHelper.getMascaraInverse(vivo2IdDomain.getMascaraLan());
				
				if(StringUtils.isBlank(aux))
					aux = vivo2IdDomain.getMascaraLan();
				else
					vivo2IdDomain.setMascaraLan(aux);
				
				scriptConfiguracao = scriptConfiguracao.replaceAll(variavelScript, aux);
				break;
				
			case "'MASCARA_IPV4_LAN_NUMERO'":
				aux = StringHelper.searchPattern(vivo2IdDomain.getMascaraLan(), GlobalStrEnum.IPADDRESS_PATTERN.toString());
				if(StringUtils.isNotBlank(aux))
					aux = RedeHelper.getMascaraInverse(vivo2IdDomain.getMascaraLan());
				
				scriptConfiguracao = scriptConfiguracao.replaceAll(variavelScript, aux);
				break;
				
			case "'MASCARA_IPV4_LAN_IP'":
				aux = StringHelper.searchPattern(vivo2IdDomain.getMascaraLan(), GlobalStrEnum.IPADDRESS_PATTERN.toString());
				if(StringUtils.isBlank(aux))
					aux = RedeHelper.getMascaraIp(vivo2IdDomain.getMascaraLan());
				
				if(StringUtils.isBlank(aux))
					aux = vivo2IdDomain.getMascaraLan();
				else
					vivo2IdDomain.setMascaraLan(aux);
				
				scriptConfiguracao = scriptConfiguracao.replaceAll(variavelScript, aux);
				break;
				
			case "'IPV4_WAN_CPE'":				
				String ipv4WanCpe = RedeHelper.retornaIpWanPar(vivo2IdDomain.getIpWan());
				if(vivo2IdDomain.getMascaraWan().equals("30") || vivo2IdDomain.getMascaraWan().equals("255.255.255.252"))
					ipv4WanCpe = RedeHelper.retornaIpWanPar(ipv4WanCpe);
				
				scriptConfiguracao = scriptConfiguracao.replaceAll(variavelScript, ipv4WanCpe);
				break;
				
			case "'IPV6_LAN'":
				scriptConfiguracao = scriptConfiguracao.replaceAll(variavelScript, vivo2IdDomain.getIpv6Lan());
				break;
				
			case "'MASCARA_IPV6_LAN'":
				scriptConfiguracao = scriptConfiguracao.replaceAll(variavelScript, vivo2IdDomain.getMascaraIpv6Lan());
				break;
				
			case "'IPV6_WAN_CPE'":
				scriptConfiguracao = scriptConfiguracao.replaceAll(variavelScript, IPv6Address.fromString(vivo2IdDomain.getIpv6Wan())
																							  .add(1)
																							  .toString());
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
		lstVariaveis.add("'INTERFACE_CONFIGURACAO'");
		lstVariaveis.add("'NOME_CLIENTE'");
		lstVariaveis.add("'ID_TBS'");
		lstVariaveis.add("'VELOCIDADE_CLIENTE'");
		lstVariaveis.add("'IPV4_WAN'");
		lstVariaveis.add("'MASCARA_IPV4_WAN_IP'");
		lstVariaveis.add("'MASCARA_IPV4_WAN_NUMERO'");
		lstVariaveis.add("'IPV6_WAN'");
		lstVariaveis.add("'MASCARA_IPV6_WAN'");
		lstVariaveis.add("'VLAN_REDE'");
		lstVariaveis.add("'VLAN_USUARIO'");
		lstVariaveis.add("'IPV4_LAN'");
		lstVariaveis.add("'MASCARA_IPV4_LAN'");
		lstVariaveis.add("'MASCARA_IPV4_LAN_IP'");
		lstVariaveis.add("'MASCARA_IPV4_LAN_NUMERO'");
		lstVariaveis.add("'IPV4_WAN_CPE'");
		lstVariaveis.add("'IPV6_LAN'");
		lstVariaveis.add("'MASCARA_IPV6_LAN'");
		lstVariaveis.add("'IPV6_WAN_CPE'");
		lstVariaveis.add("'DESIGNADOR'");
		lstVariaveis.add("'CIDADE'");
		lstVariaveis.add("'VRF'");
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
