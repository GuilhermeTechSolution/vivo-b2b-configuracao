package br.com.iatapp.helper;

import org.json.JSONException;
import org.json.JSONObject;

public class ApiHelper {
	
	public static String apiResponse(String message) {
		
		if (message == null || message.length() == 0)
			return "{}";
		
		JSONObject jsonObj = new JSONObject();
		try {
			jsonObj.put("msg", message);
		} catch (JSONException e) {
			return "{}";
		}
		
		return jsonObj.toString();
	}
	
	public static String apiResponseToken(String token) {
		
		if (token == null || token.length() == 0)
			return "{}";
		
		JSONObject jsonObj = new JSONObject();
		try {
			jsonObj.put("token", token);
		} catch (JSONException e) {
			return "{}";
		}
		
		return jsonObj.toString();
	}
	
	public static String apiTestNotification(String token, int idTeste) {
		
		if (token == null || token.length() == 0)
			return "{}";
		
		JSONObject jsonObj = new JSONObject();
		try {
			jsonObj.put("token", token);
			jsonObj.put("idTeste", idTeste);
		} catch (JSONException e) {
			return "{}";
		}
		
		return jsonObj.toString();
	}
	
	public static String apiTestErrorNotification(String token, String msg) {
		
		if (token == null || token.length() == 0 || msg == null)
			return "{}";
		
		JSONObject jsonObj = new JSONObject();
		try {
			jsonObj.put("token", token);
			jsonObj.put("msg", msg);
		} catch (JSONException e) {
			return "{}";
		}
		
		return jsonObj.toString();
	}
	
	public String generateToken() {
		
		String[] abc = null;
		String  token = ""; 
		
		abc = "abcdefghijklmnopqrstuvwxyz1234567890".split("");
		for(int i=0; i < 32; i++) {
			token += abc[(int) Math.floor(Math.random()*abc.length)];
		}
		
		return token; // 32 bit "hash"
	}
}
