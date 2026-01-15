package br.com.iatapp.api;

import java.util.Date;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import br.com.iatapp.dao.VivoB2BDao;
import br.com.iatapp.domain.SipOneCoreProcessoIdDomain;
import br.com.iatapp.helper.RedeHelper;
import br.com.iatapp.helper.StringHelper;
import br.com.iatapp.logger.ExceptionLogger;
import br.com.iatapp.rede.RedeIpFunctions;
import br.com.iatapp.repositories.SipOneCoreLogsRepository;
import br.com.iatapp.repositories.SipOneCoreProcessoIdRepository;
import br.com.iatapp.threads.ThreadSipOneCoreConfiguracao;

@RestController
@Scope("session")
@RequestMapping(value="/api/siponecore")
public class SipOneCoreResource {
	
	@Autowired
	private SipOneCoreProcessoIdRepository processoIdRepository;
	@Autowired
	private SipOneCoreLogsRepository logsRepository;
	
	@PostMapping(value = "/teste", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> executarTeste(HttpSession session, @RequestBody SipOneCoreProcessoIdDomain processoIdDomain) {
		
		try {
			if (StringUtils.isBlank(processoIdDomain.getIdVantive()) ||
				StringUtils.isBlank(processoIdDomain.getToken()))
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Id Vantive ou token não informado");
			
			processoIdDomain.setDataInicio(new Date());
			long idTeste = new VivoB2BDao().createIdConfigSipOneCore(processoIdDomain.getToken());
			if (idTeste == 0)return ResponseEntity.status(HttpStatus.OK).body("Não foi possível criar id do teste");
			processoIdDomain.setIdTeste(idTeste);
			
			// Preencher usuário
			processoIdDomain.setIdUsuario(2);
			processoIdDomain.setNomeUsuario("iatuser");
			
			this.processoIdRepository.save(processoIdDomain);
			
			// Adicionando o objeto na sessao concatenado com o token
			session.setAttribute("processoIdDomain_" + processoIdDomain.getToken(), processoIdDomain);
			
			ThreadSipOneCoreConfiguracao thread = new ThreadSipOneCoreConfiguracao(
					session, processoIdDomain, this.processoIdRepository, this.logsRepository);
			thread.start();
			
			return ResponseEntity.status(HttpStatus.OK).body("ok");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error");
		}
	}
	
	@GetMapping("/progress/{token}")
	public ResponseEntity<String> progress(HttpSession session, @PathVariable String token) {
		
		JSONObject json = new JSONObject();
		if(session == null) {
			json.put("progressBar", 5);
			json.put("logOnline", "");
			json.put("etapaTesteOnline", "");
			return ResponseEntity.status(HttpStatus.OK).body(json.toString());
		}
		
		SipOneCoreProcessoIdDomain processoIdDomain = (SipOneCoreProcessoIdDomain) session.getAttribute("processoIdDomain_" + token);
		if(processoIdDomain == null) {
			json.put("progressBar", 5);
			json.put("logOnline", "");
			json.put("etapaTesteOnline", "");
			return ResponseEntity.status(HttpStatus.OK).body(json.toString());
		}
		
		try {
			// long online
			String logOnline = "";
			RedeIpFunctions redeIpFunctions = processoIdDomain.getRedeIpFunctions();
			if(redeIpFunctions != null)
				logOnline = StringHelper.removerCodigoCores(redeIpFunctions.pegarLogOnline());
			
			json.put("progressBar", processoIdDomain.getValorProgressBar());
			json.put("logOnline", logOnline);
			json.put("etapaTesteOnline", processoIdDomain.getEtapaTesteOnline());
			return ResponseEntity.status(HttpStatus.OK).body(json.toString());
		} catch (Exception e) {
			json.put("progressBar", 5);
			json.put("logOnline", "");
			json.put("etapaTesteOnline", "");
			return ResponseEntity.status(HttpStatus.OK).body(json.toString());
		}
	}
	
	@GetMapping("/resultado/{token}")
	public ResponseEntity<Object> resultado(HttpSession session, @PathVariable String token) {
		
		try {
			if(session == null)
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			
			SipOneCoreProcessoIdDomain processoIdDomain = (SipOneCoreProcessoIdDomain) session.getAttribute("processoIdDomain_" + token);
			// Removendo o objeto de testes da sessao
			session.setAttribute("processoIdDomain_" + token, null);
			session.removeAttribute("processoIdDomain_" + token);
			
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
			String jsonStr = mapper.writeValueAsString(processoIdDomain);
			if(StringUtils.isBlank(jsonStr))
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Não foi possível converter o objeto");
			
			return ResponseEntity.status(HttpStatus.OK).body(jsonStr);
			
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e),
					RedeHelper.retornaInfoProcedimento(SipOneCoreResource.class.getName(), "resultado"));
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocorreu um erro inesperado");
		}
	}
	
}
