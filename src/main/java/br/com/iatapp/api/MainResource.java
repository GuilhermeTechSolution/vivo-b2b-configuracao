package br.com.iatapp.api;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping(value="/api")
public class MainResource implements ErrorController {
	
	@GetMapping
	public RedirectView raiz() {
		return new RedirectView("status");
	}
	
	@GetMapping("/status")
	public ResponseEntity<String> status() {
		return ResponseEntity.status(HttpStatus.OK).body("IaT VIVO B2B Online");
	}
	
	@GetMapping("/unauthorized")
	public ResponseEntity<String> unauthorized() {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("As credencias de Autenticação foram recusadas pelo servidor ou não foram informadas no Header da requisição");
	}
	
	@GetMapping("/sem-permissao")
	public ResponseEntity<String> semPermissao() {
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Você não tem permissão para acessar este recurso");
	}

	@GetMapping(value = "/error")
    public ResponseEntity<String> error() {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Você está tentando acessar um recurso que não existe");
    }

	@Override
	public String getErrorPath() {
		return "error";
	}

}
