package br.com.iatapp.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import br.com.iatapp.config.IatConstants;
import br.com.iatapp.enums.CodigoServidoresEnum;

public class DownloadScriptsService {

	public void downloadFile (String tokenFolder, String nomeProcedimento, HttpServletResponse response) {
		
		FileOutputStream fos = null;
		ZipOutputStream zipOut = null;
		FileInputStream fis = null;
		
		if (tokenFolder == null || nomeProcedimento == null)
			return;
		
		try {
			String strZipFile = String.format("scripts_%s.zip", nomeProcedimento);
			boolean flag = false;
			File file = new File(tokenFolder);
			File[] files = file.listFiles();
			// Verificar se já existe zip file
			for(File input : files) {
				if (StringUtils.containsIgnoreCase(input.getName(), strZipFile)) {
					flag = true;
					break;
				}
			}
			
			if (IatConstants.codigoServidor == CodigoServidoresEnum.SERVIDOR_PROD.getCodigoServidor())
				tokenFolder += "/";
			else
				tokenFolder += "\\";
			
			if (!flag) {
				fos = new FileOutputStream(tokenFolder + strZipFile);
				zipOut = new ZipOutputStream(new BufferedOutputStream(fos));
				for(File input : files){
					fis = new FileInputStream(input);
					ZipEntry ze = new ZipEntry(input.getName());
					if(IatConstants.DEBUG)
						System.out.println("Zipping the file: "+ input.getName());
					zipOut.putNextEntry(ze);
					byte[] bytes = new byte[4*1024];
					int size = 0;
					while((size = fis.read(bytes)) != -1){
						zipOut.write(bytes, 0, size);
					}
					zipOut.flush();
					fis.close();
				}
				zipOut.close();
				if(IatConstants.DEBUG)
					System.out.println("Done... Zipped the files...");
			}
			
			File fileToDownload = new File(tokenFolder + strZipFile);
			InputStream inputStream = new FileInputStream(fileToDownload);
			response.setContentType("application/zip");
			response.setHeader("Content-Disposition", "attachment; filename=" + strZipFile); 
			IOUtils.copy(inputStream, response.getOutputStream());
			response.flushBuffer();
			inputStream.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			try {
				if(fos != null) fos.close();
			} catch(Exception ex){}
		}
	}
}
