package br.com.iatapp.service;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SendEmailService {
	
	// Constants
	private static final String EMAIL_USUARIO = "app@ottap.com.br";
	private static final String EMAIL_SENHA = "@Area3Studio1984@";
	private static final String EMAIL_SMTP_SERVER = "smtp.gmail.com";
	private static final int EMAIL_SMTP_PORT = 465;	
	private static final String EMAIL_CHARSET = "utf-8";
	private static final String EMAIL_FROM_SUPORTE = "suporte@ottap.com.br";
	private static final String EMAIL_FROM_SUPORTE_ALIAS = "Suporte | Ottap";

	
	/**
	 * Metodo que envia o email com Remetente Suporte RasMap
	 * 
	 * @param vetorEmails
	 * @param htmlEmailTemplate
	 * @param assuntoEmail
	 * @throws EmailException
	 */
	private static void enviaEmailRemetenteSuporte(String[] vetorEmails, String htmlEmailTemplate, String assuntoEmail) throws EmailException {
		
		HtmlEmail htmlEmail = new HtmlEmail();
		htmlEmail.setHostName(EMAIL_SMTP_SERVER);
		htmlEmail.setSmtpPort(EMAIL_SMTP_PORT);
		htmlEmail.setDebug(true);
		htmlEmail.setAuthenticator(new DefaultAuthenticator(EMAIL_USUARIO, EMAIL_SENHA));
		htmlEmail.setSSLOnConnect(true);
		htmlEmail.addTo(vetorEmails);
		htmlEmail.setFrom(EMAIL_FROM_SUPORTE, EMAIL_FROM_SUPORTE_ALIAS);
		htmlEmail.setSubject(assuntoEmail);			
		htmlEmail.setHtmlMsg(htmlEmailTemplate);
		htmlEmail.setCharset(EMAIL_CHARSET);
		htmlEmail.send();
	}	
	
	
	/**
	 * Metodo que realiza o envio do email de acordo com o Json
	 * 
	 * @param jsonEmail
	 * @return
	 */
	public static void enviarEmail(String jsonEmail) {
		
		// Verificando se o objeto está nulo
		if(jsonEmail == null)
			return;
		
		JSONObject jsonObject = null;
		try {
			
			jsonObject = new JSONObject(jsonEmail);
			JSONArray jsonEmails = jsonObject.getJSONArray("emails");
			String[] vetorEmails = new String[jsonEmails.length()];
			for (int c = 0; c < jsonEmails.length(); c++) {
				vetorEmails[c] = jsonEmails.getString(c);
			}
			
			try {
				enviaEmailRemetenteSuporte(vetorEmails, jsonObject.getString("htmlEmailTemplate"), jsonObject.getString("emailAssunto"));
			} catch (EmailException e) {
				e.printStackTrace();
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
