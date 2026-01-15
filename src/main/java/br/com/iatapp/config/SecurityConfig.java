package br.com.iatapp.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import br.com.iatapp.helper.CriptografiaHelper;

/**
 * 
 * @author ottap
 *
 */
public class SecurityConfig extends HandlerInterceptorAdapter {
	
//	@Override
//	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
//			throws Exception {
		
//		String uri = request.getRequestURI();
//		if(uri.endsWith("error") ||
//				uri.endsWith("login") ||
//				uri.endsWith("efetuarLogin") ||
//				uri.contains("resources") ||
//				uri.contains("imagesext") ||
//				uri.endsWith("unauthorized") ||
//				uri.endsWith("/api/login") ||
//				uri.endsWith("/api/status") ||
//				uri.endsWith("/api/sem-permissao") ||
//				uri.endsWith("/api/error")) {
//			return true;
//		}
//
//		// verificando requisicao da API
//		if (uri.contains("/api/")) {
//
//			// pegando os parametros header
//			String authorization = request.getHeader("Authorization");
//			if(StringUtils.isBlank(authorization)) {
//				response.sendRedirect(request.getContextPath() + "/api/unauthorized");
//				return false;
//			}
//
//			// separando o login e senha da String authorization
//			String[] credenciais = separaLoginPassword(authorization);
//			if(credenciais == null) {
//				response.sendRedirect(request.getContextPath() + "/api/unauthorized");
//				return false;
//			}
//
//			// verificando se é a senha de leitura do sensor
//			// Authorization: Ott@pT3cnologi@:VivoB2B@T3l3f0nica
//			// Authorization Base64: T3R0QHBUM2Nub2xvZ2lAOlZpdm9CMkJAVDNsM2YwbmljYQ==
//			if(credenciais[0].equals("Ott@pT3cnologi@") && credenciais[1].equals("VivoB2B@T3l3f0nica")) {
//				return true;
//			}
//
//			response.sendRedirect(request.getContextPath() + "/api/unauthorized");
//			return false;
//		}
//
//		if(request.getSession().getAttribute("usuarioLogado") != null) {
//			return true;
//		}
//
//		response.sendRedirect(request.getContextPath() + "/login");

//		if(!request.getRequestURI().contains("home")) {
//			response.sendRedirect(request.getContextPath() + "/");
//		}
//
//		return true;
//	}
	
	/**
	 * Metodo que decodifica a String Base64 e separa os valores de login e password
	 * @param authorization
	 * @return
	 */
	public String[] separaLoginPassword(String authorization) {
		
		// decodificando o Base64
		authorization = CriptografiaHelper.base64Decode(authorization);
		
		// verificando se contem o separador :
		if(!StringUtils.contains(authorization, ":")) {
			return null;
		}
		
		// verificando se o separado é o ultimo caracter
		if(authorization.indexOf(":") == (authorization.length() - 1)) {
			return null;
		}
		
		String[] credenciais = new String[2];
		credenciais[0] = authorization.substring(0, authorization.indexOf(":"));
		credenciais[1] = authorization.substring(authorization.indexOf(":") + 1);		
		return credenciais;
	}

}
