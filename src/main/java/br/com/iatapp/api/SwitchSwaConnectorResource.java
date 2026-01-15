package br.com.iatapp.api;

import br.com.iatapp.dao.SenhasDao;
import br.com.iatapp.dao.UsuariosDao;
import br.com.iatapp.domain.ConfigSwitchIdDomain;
import br.com.iatapp.helper.StringHelper;
import br.com.iatapp.model.UsuarioModel;
import br.com.iatapp.repositories.*;
import br.com.iatapp.threads.ThreadAtivacaoSwitch;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping(value="/api/config-switch")
public class SwitchSwaConnectorResource {

	@Autowired
	private ConfigSwitchIdRepository configSwitchIdRepository;
	@Autowired
	private ConfigSwitchLogRepository configSwitchLogRepository;
	@Autowired
	private ConfigSwitchResultadosRepository configSwitchResultadosRepository;
	@Autowired
	private ConfigSwitchScriptRepository configSwitchScriptRepository;

	@PostMapping(value = "/teste", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> executarTeste(@RequestHeader("name") String name,
												@RequestBody ConfigSwitchIdDomain configSwitchIdDomain) {

		// Validando os campos obrigatorios
		if(configSwitchIdDomain == null ||
				StringUtils.isBlank(configSwitchIdDomain.getIdVantive()) ||
				StringUtils.isBlank(configSwitchIdDomain.getIpSwt())||
				StringUtils.isBlank(configSwitchIdDomain.getNomeCliente())) {
			return ResponseEntity
					.status(HttpStatus.BAD_REQUEST)
					.body("campos_obrigatorios_vazios");
		}

		// tratando o nome do cliente
		configSwitchIdDomain.setNomeCliente(getNomeCliente(configSwitchIdDomain.getNomeCliente()));

//		// Pegando o usuario logado
//		UsuarioModel loggedUser;
//		try {
//			loggedUser = new UsuariosDao().buscarUsuarioByNumeroRe(username);
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
//		// setando o usuario do teste
		configSwitchIdDomain.setNomeUsuario(name);
		configSwitchIdDomain.setIdUsuario(2600); //Usuário sistêmico vanUser

		// Buscando as senhas de CPE, RA, Rede IP
		UsuarioModel usuarioSenhas;
		try {
			usuarioSenhas = new SenhasDao().buscarSenhasUsuarioIaTConfMaster();
		} catch (Exception e) {
			usuarioSenhas = null;
			e.printStackTrace();
		}

		if(usuarioSenhas == null ||
				StringUtils.isBlank(usuarioSenhas.getLoginPe()) ||
				StringUtils.isBlank(usuarioSenhas.getSenhaPe()) ||
				StringUtils.isBlank(usuarioSenhas.getLoginRedeIp()) ||
				StringUtils.isBlank(usuarioSenhas.getSenhaRedeIp())) {
			// Nao possui senhas cadastradas
			return ResponseEntity
					.status(HttpStatus.NO_CONTENT)
					.body("sem_senhas_cadastradas");
		}

		configSwitchIdDomain.setUsuarioSenhas(usuarioSenhas);
		configSwitchIdDomain.setDataInicio(new Date());

		// salvando no MongoDB
		this.configSwitchIdRepository.save(configSwitchIdDomain);

//		 Iniciando a Thread do Teste
		ThreadAtivacaoSwitch threadAtivacaoSwitch = new ThreadAtivacaoSwitch(
				null,
				configSwitchIdDomain,
				this.configSwitchIdRepository,
				this.configSwitchLogRepository,
				this.configSwitchResultadosRepository,
				this.configSwitchScriptRepository
		);
		threadAtivacaoSwitch.start();

		return ResponseEntity
				.ok()
				.body(configSwitchIdDomain.getId());
	}

	private String getNomeCliente(String nomeCliente) {
		nomeCliente = nomeCliente.toUpperCase();
		String cliente = StringHelper.removeCaracteresEspeciais(nomeCliente);
		String[] clienteArray = cliente.split(" ");

		if(clienteArray.length >= 3)
			return clienteArray[0] + "_" + clienteArray[1] + "_" + clienteArray[2];
		else if(clienteArray.length >= 2)
			return clienteArray[0] + "_" + clienteArray[1];
		else
			return cliente;
	}
}
