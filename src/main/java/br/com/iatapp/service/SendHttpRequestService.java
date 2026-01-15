package br.com.iatapp.service;

import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.http.MediaType;

import br.com.iatapp.config.IatConstants;
import br.com.iatapp.helper.RedeHelper;
import br.com.iatapp.logger.ExceptionLogger;


public class SendHttpRequestService {
	
	public String sendGetRequest(String url, String token, JSONObject jsonReqj) {
		try {			
			HttpGet httpGet = new HttpGet(url);
			httpGet.setHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
			httpGet.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
			httpGet.setHeader(HttpHeaders.AUTHORIZATION, "T3R0QHBUM2Nub2xvZ2lAOlZpdm9CMkJAVDNsM2YwbmljYQ==");
			
			if(StringUtils.isNotBlank(jsonReqj.optString("Set-Cookie", "")))
				httpGet.setHeader("Cookie", jsonReqj.getString("Set-Cookie"));
			
			HttpClient client = HttpClients.createDefault();
			
			HttpResponse response = client.execute(httpGet);
			
			if(IatConstants.DEBUG)
				System.out.println("Executing get request: " + httpGet.getRequestLine());
			
			if(StringUtils.isBlank(jsonReqj.optString("Set-Cookie", ""))) {
				String setCookie = (String) response.getFirstHeader("Set-Cookie").getValue();
				if(StringUtils.isNotBlank(setCookie))
					jsonReqj.put("Set-Cookie", setCookie);
			}
			
			HttpEntity responseEntity = response.getEntity();
			String json = null;
			try {
				json = EntityUtils.toString(responseEntity);
			} catch(Exception e){
				return "error";
			}
			
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				return json;
			}
			return "error";
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e),
					RedeHelper.retornaInfoProcedimento(SendHttpRequestService.class.getName(), "sendGetRequest"));
			return null;
		}
	}
	
	public String sendGetRequest(String url, String token, HttpSession session) {		
		try {			
			HttpGet request = new HttpGet(url);
			request.setHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
			request.setHeader(HttpHeaders.AUTHORIZATION, "T3R0QHBUM2Nub2xvZ2lAOlZpdm9CMkJAVDNsM2YwbmljYQ==");
			
			String setCookie = (String) session.getAttribute(token);
			if(StringUtils.isNotBlank(setCookie)) {
				request.setHeader("Cookie", setCookie);
			}
			
			HttpClient client = HttpClients.createDefault();
			
			HttpResponse response = client.execute(request);
			
			if(StringUtils.isBlank(setCookie)) {
				setCookie = (String) response.getFirstHeader("Set-Cookie").getValue();
				session.setAttribute(token, setCookie);
			}
			
			HttpEntity responseEntity = response.getEntity();
			
			String json = null;
			try {
				json = EntityUtils.toString(responseEntity);
			} catch(Exception e){
				return "error";
			}
			
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				return json;
			}
			return "error";
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e),
					RedeHelper.retornaInfoProcedimento(SendHttpRequestService.class.getName(), "sendGetRequest"));
			return null;
		}
	}
	
	public String sendPostRequest(String url, String token, String jsonDados, JSONObject jsonReq) {
		try {
			HttpPost httpPost  = new HttpPost(url);
			httpPost.setHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
			httpPost.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
			httpPost.setHeader(HttpHeaders.AUTHORIZATION, "T3R0QHBUM2Nub2xvZ2lAOlZpdm9CMkJAVDNsM2YwbmljYQ==");
			
			if(StringUtils.isNotBlank(jsonReq.optString("Set-Cookie", "")))
				httpPost.setHeader("Cookie", jsonReq.getString("Set-Cookie"));
			
			if (IatConstants.DEBUG) {
				System.out.println("\njsonDados");
				System.out.println(jsonDados);
			}
			
			StringEntity stringEntity = new StringEntity(jsonDados, StandardCharsets.UTF_8);
			httpPost.setEntity(stringEntity);
			
			if (IatConstants.DEBUG)
				System.out.println("Executing post request: " + httpPost.getRequestLine());
			
			CloseableHttpClient client = HttpClients.createDefault();
			CloseableHttpResponse response = client.execute(httpPost);
			
			if(StringUtils.isBlank(jsonReq.optString("Set-Cookie", ""))) {
				String setCookie = (String) response.getFirstHeader("Set-Cookie").getValue();
				if(StringUtils.isNotBlank(setCookie))
					jsonReq.put("Set-Cookie", setCookie);
			}
			
			HttpEntity responseEntity = response.getEntity();
			String json = null;
			try {
				json = EntityUtils.toString(responseEntity);
			} catch(Exception e){
				return "error";
			}
			
			int statusCode = response.getStatusLine().getStatusCode();
			client.close();
			
			if (statusCode == HttpStatus.SC_OK) {
				return json;
			}
			return "error";
			
		} catch (Exception e) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(e),
					RedeHelper.retornaInfoProcedimento(SendHttpRequestService.class.getName(), "sendPostRequest"));
			return null;
		}
	}
	
}
