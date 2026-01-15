package br.com.iatapp.logger;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.iatapp.config.IatConstants;
import br.com.iatapp.helper.StringHelper;
import br.com.iatapp.service.SendEmailService;


public abstract class ExceptionLogger {
	
	private static final Logger loggerEx = LoggerFactory.getLogger(ExceptionLogger.class);
	//private static Semaphore semaphoreEx = new Semaphore(1);
	
	
	public static void record(String strException, String infoProcedimento) {
		
		if (StringUtils.isBlank(strException))
			return;
		
		try {
			//semaphoreEx.acquire();
			loggerEx.error(infoProcedimento + System.lineSeparator() + strException);
			//Thread.sleep(500);
			//semaphoreEx.release();
			
			Runnable task = () -> { 
				
				try {
					JSONObject objJson = new JSONObject();

					String strEx = StringHelper.readFileToString("/home/ottap-tools/templates/", "emailExTemplate.min.html");
					if (StringUtils.isBlank(strEx))
						strEx = infoProcedimento + "<br/><br/><br/>" + strException;
					else
						strEx = strEx.replace("<+++EXCEPTION--->", infoProcedimento + "<br/><br/><br/>" + strException);
					
					JSONArray jsonArrayEmail = new JSONArray();
					jsonArrayEmail.put("suporte@ottap.com.br");
					
					objJson.put("emails", jsonArrayEmail);
					objJson.put("htmlEmailTemplate", strEx);
					objJson.put("emailAssunto", IatConstants.EMAIL_SUBJECT);
					
					SendEmailService.enviarEmail(objJson.toString());					
				} catch (Exception e) {
					e.printStackTrace();
					Thread.currentThread().interrupt();
				}

			};
		 
			// start the thread
			new Thread(task).start();	
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void recordDisk(String strException, String infoProcedimento) {
		
		try {
			Thread.sleep(50);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		if (StringUtils.isBlank(strException))
			return;
		
		try {
			loggerEx.error(infoProcedimento + System.lineSeparator() + strException);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
