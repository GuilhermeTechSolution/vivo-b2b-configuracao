package br.com.iatapp.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;


public class HttpService {
	
    private final String user;
    private final String password;
    
    private static final Logger LOG = LoggerFactory.getLogger(HttpService.class);

    public HttpService(String user, String password) {
        this.user = user;
        this.password = password;
    }
    
    public String get(String url, boolean basicAuth) {
    	
    	String response = "";
		HttpGet request = new HttpGet(url);
		request.setHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		
		if(basicAuth) {
			request.setHeader(HttpHeaders.AUTHORIZATION, createBasicAuthHeaderValue());
		}
			
		try {
			response = executeRequest(request);	
		} catch (Exception e) {
			LOG.error("Erro ao executar request." + " - " + url);
			return "";
		}
			
		return response;		
    }
    
    public String post(String url, boolean basicAuth) {
    	
    	String response = "";
		HttpPost request = new HttpPost(url);
		request.setHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		
		if(basicAuth) {
			request.setHeader(HttpHeaders.AUTHORIZATION, createBasicAuthHeaderValue());
		}
			
		try {
			response = executeRequest(request);	
		} catch (Exception e) {
			LOG.error("Erro ao executar request." + " - " + url, e);
		}
			
		return response;		
    }
    
    public String put(String url, boolean basicAuth) {
    	
    	String response = "";
		HttpPut request = new HttpPut(url);
		request.setHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		
		if(basicAuth) {
			request.setHeader(HttpHeaders.AUTHORIZATION, createBasicAuthHeaderValue());
		}
			
		try {
			response = executeRequest(request);	
		} catch (Exception e) {
			LOG.error("Erro ao executar request." + " - " + url, e);
		}
			
		return response;		
    }
    
    public String delete(String url, boolean basicAuth) {
    	
    	String response = "";
		HttpDelete request = new HttpDelete(url);
		request.setHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		
		if(basicAuth) {
			request.setHeader(HttpHeaders.AUTHORIZATION, createBasicAuthHeaderValue());
		}
			
		try {
			response = executeRequest(request);	
		} catch (Exception e) {
			LOG.error("Erro ao executar request." + " - " + url, e);
		}
			
		return response;		
    }
    
    private String executeRequest(HttpRequestBase request) throws ClientProtocolException, IOException, ParseException, HttpException {  	
    	CloseableHttpClient client = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		response = client.execute(request);
		String json = getResponse(response);
		client.close();
		response.close();			
		return json;
    }
    
    private String getResponse(CloseableHttpResponse response) throws HttpException, ParseException, IOException {    	
		int httpStatus = response.getStatusLine().getStatusCode();
		if(httpStatus != HttpStatus.SC_OK) {
			throw new HttpException("Http status:" + httpStatus);
		}		
		HttpEntity responseEntity = response.getEntity();		
		return EntityUtils.toString(responseEntity);		
    }

    
    private String createBasicAuthHeaderValue() {
        String auth = user + ":" + password;
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.UTF_8));
        String authHeaderValue = "Basic " + new String(encodedAuth);
        return authHeaderValue;
    }

}
