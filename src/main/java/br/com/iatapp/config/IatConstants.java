package br.com.iatapp.config;

import br.com.iatapp.enums.CodigoServidoresEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("application")
public class IatConstants {

	// Servidor utilizado
	public static int codigoServidor;
	@Value("${environment}")
	public void setCodigoServidor(final String environment){
		IatConstants.codigoServidor =
				StringUtils.equalsIgnoreCase(environment , "PROD") ?
						CodigoServidoresEnum.SERVIDOR_PROD.getCodigoServidor() :
						CodigoServidoresEnum.SERVIDOR_DEV.getCodigoServidor();
	}
	public static String IP_SERVER_GALVAO;
	public static String USER_SERVER_GALVAO;
	public static String PASS_SERVER_GALVAO;

	@Value("${jumpserver.galvao.ip}")
	public void setIpServerGalvao(String ipServerGalvao) {
		IP_SERVER_GALVAO = ipServerGalvao;
	}

	@Value("${jumpserver.galvao.user}")
	public void setUserServerGalvao(String userServerGalvao) {
		USER_SERVER_GALVAO = userServerGalvao;
	}

	@Value("${jumpserver.galvao.password}")
	public void setPassServerGalvao(String passServerGalvao) {
		PASS_SERVER_GALVAO = passServerGalvao;
	}

	// Variavel para aparecer o Debug
	public static boolean DEBUG = true;

	// Variavel para setar se vai forcar o disconnect da sessao telnet quanto der Socket Timeout
	public static boolean DISCONNECT_TELNET_SOCKET = true;
	
//	public static String APPLICATION_USER = "techsolutio";
//	public static String APPLICATION_SENHA = "aWp@R3uyFqH5";
	
	//public static String LOCAL_USER = "techsolutio";
	//public static String LOCAL_SENHA = "aWp@R3uyFqH5";

	public static String SALTO_USER_TATUAPE;
	public static String SALTO_SENHA_TATUAPE;
	public static String SALTO_IP_TATUAPE;
	public static String SALTO_SSH_PORT_TATUAPE = "22";

	@Value("${jumpserver.tatuape.user}")
	public void setSaltoUserTatuape(String saltoUserTatuape) {
		SALTO_USER_TATUAPE = saltoUserTatuape;
	}

	@Value("${jumpserver.tatuape.password}")
	public void setSaltoSenhaTatuape(String saltoSenhaTatuape) {
		SALTO_SENHA_TATUAPE = saltoSenhaTatuape;
	}

	@Value("${jumpserver.tatuape.ip}")
	public void setSaltoIpTatuape(String saltoIpTatuape) {
		SALTO_IP_TATUAPE = saltoIpTatuape;
	}

	public static String SERVER_IP_SIP_IMS_SPO = "10.11.24.104";
	public static String SERVER_PORT_SIP_IMS_SPO = "3300";

	public static String MYSQL_HOST;
	public static String AWS_HOST = "ec2-user@ec2-3-211-200-8.compute-1.amazonaws.com";
	public static String MYSQL_DATABSE;


	@Value("${mysql.host}")
	public void setMysqlHost(String mysqlHost) {
		MYSQL_HOST = mysqlHost;
	}

	@Value("${mysql.database}")
	public void setMysqlDatabse(String mysqlDatabse) {
		MYSQL_DATABSE = mysqlDatabse;
	}

	public static String REDE_IP;

	@Value("${jumpserver.gesa.ip}")
	public void setRedeIp(String redeIp) {
		REDE_IP = redeIp;
	}
	
	public static String EMAIL_SUBJECT = "IaT Vivo B2B Configuracao | Exception";
	
	public static String IAT_VIVO_B2B_URL;

	@Value("${iat.url}")
	public void setIatVivoB2bUrl(String iatVivoB2bUrl) {
		IAT_VIVO_B2B_URL = iatVivoB2bUrl;
	}

	public static String SCRIPTS_PATH = "";

	@Value("${scripts.path}")
	public static void setScriptsPath(String scriptsPath) {
		SCRIPTS_PATH = scriptsPath;
	}

	public static String API_41_URL;
	public static String USUARIO_41;
	public static String SENHA_41;

	@Value("${fourtyone.url}")
	public void setApi41Url(String api41Url) {
		API_41_URL = api41Url;
	}

	@Value("${fourtyone.user}")
	public void setUsuario41(String usuario41) {
		USUARIO_41 = usuario41;
	}

	@Value("${fourtyone.password}")
	public void setSenha41(String senha41) {
		SENHA_41 = senha41;
	}


	/*static {
		if(codigoServidor == CodigoServidoresEnum.SERVIDOR_PROD.getCodigoServidor()) {
			DEBUG = false;
			SCRIPTS_PATH = "/opt/vivo-robots/vivo-sip-onecore/generated-scripts/";
		} else {
			DEBUG = true;
			SCRIPTS_PATH = "C:\\home\\ottap-tools\\scriptsSip\\";
		}
	}*/

}
