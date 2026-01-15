package br.com.iatapp.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.servlet.view.document.AbstractXlsView;

import br.com.iatapp.domain.ProcessoIdDomain;
import br.com.iatapp.domain.ProcessoIdRetiradaDomain;
import br.com.iatapp.domain.ProcessoIdVivo2Domain;
import br.com.iatapp.enums.ExportEnum;
import br.com.iatapp.helper.DataHelper;
import br.com.iatapp.helper.StringHelper;

public class ExportExcelService extends AbstractXlsView {

	@SuppressWarnings("unchecked")
	@Override
	protected Workbook createWorkbook(Map<String,Object> model, HttpServletRequest request) {

		try {
			Map<String, String> queryMap = (Map<String, String>) model.get("queryMap");
			boolean template = Boolean.parseBoolean(queryMap.get("template"));
			
			switch (ExportEnum.valueOf((int) model.get("procedimento"))) {
				
				case RELATORIO_CONFIGURACAO:
					if (template) {
						InputStream is = new URL(String.format("%s/files/iat-relatorio-configuracao-template.xlsx", StringHelper.getResourcesUrl(request))).openStream();
						return  new XSSFWorkbook(is);
					}
					break;
				case RELATORIO_SIP_ONECORE:
					break;
				default:
					break;
			}
			
			return new XSSFWorkbook();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return new XSSFWorkbook();
	}
	
	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		switch (ExportEnum.valueOf((int) model.get("procedimento"))) {
			
			case RELATORIO_CONFIGURACAO:
				response = relatorioConfiguracao(model, workbook, request, response);
				break;
			case RELATORIO_CONFIGURACAO_VIVO2:
				response = relatorioConfiguracaoVivo2(model, workbook, request, response);
				break;
			case RELATORIO_SIP_ONECORE:
				break;
			case RELATORIO_RETIRADA:
				response = relatorioRetirada(model, workbook, request, response);
				break;
			default:
				break;
		}
	}
	
	@SuppressWarnings("unchecked")
	private HttpServletResponse relatorioRetirada(Map<String, Object> model, Workbook workbook, HttpServletRequest request, HttpServletResponse response) {
		
		Map<String, String> queryMap = (Map<String, String>) model.get("queryMap");
		Map<String, CellStyle> styles = createStyles(workbook);
		String dataInicial = queryMap.get("datainicial");
		String dataFinal = queryMap.get("datafinal");
		
		String respHeader = null;
		respHeader = String.format("attachment;filename=\"iat-relatorio-retirada-%s.xlsx\"", DataHelper.getCurrentLocalDateTimeStamp());
		
		response.setHeader("Content-Disposition", respHeader);
			
		/*
		 * SHEET ANALÍTICO
		 */
		
		List<ProcessoIdRetiradaDomain> lst = (List<ProcessoIdRetiradaDomain>) model.get("listaAnalitico");
		Sheet sheet = workbook.createSheet("Analítico de Retirada");

		sheet.setColumnWidth(0, 4000);
		sheet.setColumnWidth(1, 5000);
		sheet.setColumnWidth(2, 16000);
		sheet.setColumnWidth(3, 8000);
		sheet.setColumnWidth(4, 8000);
		
		// Title 
		Row titleRow = sheet.createRow(0);
		titleRow.setHeightInPoints(20);
		Cell titleCell = titleRow.createCell(0);
		titleCell.setCellValue(String.format("Analítico de Retirada | %s - %s", dataInicial, dataFinal));
		titleCell.setCellStyle(styles.get("title"));
		sheet.addMergedRegion(CellRangeAddress.valueOf("$A$1:$D$1"));
		
		// Header 
		String[] titlesHeaderAnalitico = {"ID Vantive", "Produto", "Status", "Tarefa Star", "Data Execução"};
		Row headerRow = sheet.createRow(1);
		headerRow.setHeightInPoints(20);
		for (int i = 0; i < titlesHeaderAnalitico.length; i++) {
			Cell headerCell = headerRow.createCell(i);
			headerCell.setCellValue(titlesHeaderAnalitico[i]);
			headerCell.setCellStyle(styles.get("header"));
		}
		
		// Cell Results 
		int rowNum = 2;
		Cell resultCell = null;
		for(ProcessoIdRetiradaDomain el : lst) {
			Row row = sheet.createRow(rowNum++);
			resultCell = row.createCell(0);
			resultCell.setCellValue(el.getIdVantive());
			resultCell.setCellStyle(styles.get("cell"));
			resultCell = row.createCell(1);
			resultCell.setCellValue(el.getProduto());
			resultCell.setCellStyle(styles.get("cell"));
			resultCell = row.createCell(2);
			resultCell.setCellValue(el.getResultadoProcedimento());
			resultCell.setCellStyle(styles.get("cell"));
			resultCell = row.createCell(3);
			resultCell.setCellValue(el.getNomeTarefaStar());
			resultCell.setCellStyle(styles.get("cell"));
			resultCell = row.createCell(4);
			resultCell.setCellValue(el.getTzDataInicio());
			resultCell.setCellStyle(styles.get("cell"));
		}
		
		return response;
	}
	
	/**
	 * Relatorio Configuracao
	 */
	@SuppressWarnings("unchecked")
	private HttpServletResponse relatorioConfiguracao(Map<String, Object> model, Workbook workbook, HttpServletRequest request, HttpServletResponse response) {
		
		Map<String, String> queryMap = (Map<String, String>) model.get("queryMap");
		Map<String, CellStyle> styles = createStyles(workbook);
		boolean template = Boolean.parseBoolean(queryMap.get("template"));
		String dataInicial = queryMap.get("datainicial");
		String dataFinal = queryMap.get("datafinal");
		String produto = queryMap.get("produto");
		
		String respHeader = null;
		if (StringUtils.isBlank(produto))
			respHeader = String.format("attachment;filename=\"iat-relatorio-configuracao-%s.xlsx\"", DataHelper.getCurrentLocalDateTimeStamp());
		else {
			produto = produto.replace(" ", "_").toLowerCase();
			respHeader = String.format("attachment;filename=\"iat-relatorio-configuracao-%s-%s.xlsx\"", produto, DataHelper.getCurrentLocalDateTimeStamp());
		}
		
		response.setHeader("Content-Disposition", respHeader);
		
		if (template) {
			
			try {
				
//				/*
//				 * SHEET RESUMO 
//				 */
//				List<ProcessoIdDomain> lst = (List<ProcessoIdDomain>) model.get("listaResumo");
//				Sheet sheet = workbook.getSheetAt(0);
//				
//				// Title 
//				sheet.getRow(0).getCell(0).setCellValue(String.format("Resumo por Funcionário | %s - %s", dataInicial, dataFinal));
//				
//				// Cell Results 
//				int rowNum = 2;
//				Cell resultCell;
//				for(ProcessoIdDomain el : lst) {
//					Row row = sheet.createRow(rowNum++);
//					resultCell = row.createCell(0);
//					resultCell.setCellValue(el.getNomeUsuario());
//					resultCell.setCellStyle(styles.get("cell"));
//					resultCell = row.createCell(1);
//					resultCell.setCellValue(el.getNomeEmpresa());
//					resultCell.setCellStyle(styles.get("cell"));
//					resultCell = row.createCell(2);
//					resultCell.setCellValue(el.getNomeArea());
//					resultCell.setCellStyle(styles.get("cell"));
//					resultCell = row.createCell(3);
//					resultCell.setCellValue(el.getQtdTestes());
//					resultCell.setCellStyle(styles.get("cell"));
//				}
//				
//				/*
//				 * SHEET ANALÍTICO
//				 */
//				lst = (List<ProcessoIdDomain>) model.get("listaAnalitico");
//				sheet = workbook.getSheetAt(1);
//				
//				// Title
//				sheet.getRow(0).getCell(0).setCellValue(String.format("Analítico de Testes | %s - %s", dataInicial, dataFinal));
//				
//				// Cell Results 
//				rowNum = 2;
//				for(ProcessoIdDomain el : lst) {
//					Row row = sheet.createRow(rowNum++);
//					resultCell = row.createCell(0);
//					resultCell.setCellValue(el.getIdTeste());
//					resultCell.setCellStyle(styles.get("cell"));
//					resultCell = row.createCell(1);
//					resultCell.setCellValue(el.getIdVantive());
//					resultCell.setCellStyle(styles.get("cell"));
//					resultCell = row.createCell(2);
//					resultCell.setCellValue(el.getNomeEmpresa());
//					resultCell.setCellStyle(styles.get("cell"));
//					resultCell = row.createCell(3);
//					resultCell.setCellValue(el.getNomeArea());
//					resultCell.setCellStyle(styles.get("cell"));
//					resultCell = row.createCell(4);
//					resultCell.setCellValue(el.getNomeUsuario());
//					resultCell.setCellStyle(styles.get("cell"));
//					resultCell = row.createCell(5);
//					resultCell.setCellValue(el.getCliente());
//					resultCell.setCellStyle(styles.get("cell"));
//					resultCell = row.createCell(6);
//					resultCell.setCellValue(el.getNomeTecnologia());
//					resultCell.setCellStyle(styles.get("cell"));
//					resultCell = row.createCell(7);
//					resultCell.setCellValue(el.getNomeServico());
//					resultCell.setCellStyle(styles.get("cell"));
//					resultCell = row.createCell(8);
//					resultCell.setCellValue(el.getNomeModulo());
//					resultCell.setCellStyle(styles.get("cell"));
//					resultCell = row.createCell(9);
//					resultCell.setCellValue(el.getDataInicio());
//					resultCell.setCellStyle(styles.get("cell"));
//					resultCell = row.createCell(10);
//					resultCell.setCellValue(el.getDuracaoTeste());
//					resultCell.setCellStyle(styles.get("cell"));
//					resultCell = row.createCell(11);
//					resultCell.setCellValue(el.isIdCertificado() ? "id_certificado" : "id_nao_certificado");
//					resultCell.setCellStyle(styles.get("cell"));
//					resultCell = row.createCell(12);
//					resultCell.setCellValue(el.getMensagemGeral());
//					resultCell.setCellStyle(styles.get("cell"));
//					resultCell = row.createCell(13);
//					resultCell.setCellValue(el.getNomeFabricanteCpe());
//					resultCell.setCellStyle(styles.get("cell"));
//					resultCell = row.createCell(14);
//					resultCell.setCellValue(el.getNomeModeloCpe());
//					resultCell.setCellStyle(styles.get("cell"));
//					resultCell = row.createCell(15);
//					resultCell.setCellValue(el.getSerialCpe());
//					resultCell.setCellStyle(styles.get("cell"));
//					resultCell = row.createCell(16);
//					resultCell.setCellValue(el.getVersaoCpe());
//					resultCell.setCellStyle(styles.get("cell"));
//				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		} else {
			
			/*
			 * SHEET RESUMO 
			 */
			List<ProcessoIdDomain> lst = (List<ProcessoIdDomain>) model.get("listaResumo");
			Sheet sheet = workbook.createSheet("Resumo por Status");
			
			sheet.setColumnWidth(0, 15000);
			sheet.setColumnWidth(1, 4000);
			
			// Title 
			Row titleRow = sheet.createRow(0);
			titleRow.setHeightInPoints(20);
			Cell titleCell = titleRow.createCell(0);
			if (StringUtils.isBlank(produto))
				titleCell.setCellValue(String.format("Resumo por Status | %s - %s", dataInicial, dataFinal));
			else
				titleCell.setCellValue(String.format("Resumo por Status | %s - %s | %s", dataInicial, dataFinal, produto));
			
			titleCell.setCellStyle(styles.get("title"));
			sheet.addMergedRegion(CellRangeAddress.valueOf("$A$1:$B$1"));
			
			// Header 
			String[] titlesHeaderResumo = {"Status", "Qtd"};
			Row headerRow = sheet.createRow(1);
			headerRow.setHeightInPoints(20);
			Cell headerCell;
			for (int i = 0; i < titlesHeaderResumo.length; i++) {
				headerCell = headerRow.createCell(i);
				headerCell.setCellValue(titlesHeaderResumo[i]);
				headerCell.setCellStyle(styles.get("header"));
			}
			
			// Cell Results 
			int rowNum = 2;
			Cell resultCell;
			for(ProcessoIdDomain el : lst) {
				Row row = sheet.createRow(rowNum++);
				resultCell = row.createCell(0);
				resultCell.setCellValue(el.getResultadoProcedimento());
				resultCell.setCellStyle(styles.get("cell"));
				resultCell = row.createCell(1);
				resultCell.setCellValue(el.getQtd());
				resultCell.setCellStyle(styles.get("cell"));
			}
			
			/*
			 * SHEET ANALÍTICO
			 */
			lst = (List<ProcessoIdDomain>) model.get("listaAnalitico");
			sheet = workbook.createSheet("Analítico de Testes");

			sheet.setColumnWidth(0, 4000);
			sheet.setColumnWidth(1, 5000);
			sheet.setColumnWidth(2, 16000);
			sheet.setColumnWidth(3, 8000);
			sheet.setColumnWidth(4, 8000);
			sheet.setColumnWidth(5, 8000);
			sheet.setColumnWidth(6, 8000);
			sheet.setColumnWidth(7, 8000);
			sheet.setColumnWidth(8, 8000);
			sheet.setColumnWidth(9, 8000);
			sheet.setColumnWidth(10, 8000);
			sheet.setColumnWidth(11, 8000);
			sheet.setColumnWidth(12, 16000);
			sheet.setColumnWidth(13, 30000);
			sheet.setColumnWidth(14, 30000);
			sheet.setColumnWidth(15, 8000);
			
			// Title 
			titleRow = sheet.createRow(0);
			titleRow.setHeightInPoints(20);
			titleCell = titleRow.createCell(0);
			if (StringUtils.isBlank(produto))
				titleCell.setCellValue(String.format("Analítico de Testes | %s - %s", dataInicial, dataFinal));
			else
				titleCell.setCellValue(String.format("Analítico de Testes | %s - %s | %s", dataInicial, dataFinal, produto));
			titleCell.setCellStyle(styles.get("title"));
			sheet.addMergedRegion(CellRangeAddress.valueOf("$A$1:$D$1"));
			
			// Header 
			String[] titlesHeaderAnalitico = {"ID Vantive", "Produto", "Status", "Tecnologia", "Tipo Venda", "DSLAM/OLT", "RA Principal", "RA Backup", "IP LAN", "IP Loopback", "IP Wan Pe", "Interface PE Principal", "Resultado Config", "Alerta Config", "Resultado Check Configuração", "Data Execução"};
			headerRow = sheet.createRow(1);
			headerRow.setHeightInPoints(20);
			for (int i = 0; i < titlesHeaderAnalitico.length; i++) {
				headerCell = headerRow.createCell(i);
				headerCell.setCellValue(titlesHeaderAnalitico[i]);
				headerCell.setCellStyle(styles.get("header"));
			}
			
			// Cell Results 
			rowNum = 2;
			for(ProcessoIdDomain el : lst) {
				Row row = sheet.createRow(rowNum++);
				resultCell = row.createCell(0);
				resultCell.setCellValue(el.getIdVantive());
				resultCell.setCellStyle(styles.get("cell"));
				resultCell = row.createCell(1);
				resultCell.setCellValue(el.getProduto());
				resultCell.setCellStyle(styles.get("cell"));
				resultCell = row.createCell(2);
				resultCell.setCellValue(el.getResultadoProcedimento());
				resultCell.setCellStyle(styles.get("cell"));
				resultCell = row.createCell(3);
				resultCell.setCellValue(el.getTecnologiaStr());
				resultCell.setCellStyle(styles.get("cell"));
				resultCell = row.createCell(4);
				resultCell.setCellValue(el.getTipoVendaStr());
				resultCell.setCellStyle(styles.get("cell"));
				resultCell = row.createCell(5);
				resultCell.setCellValue(el.getDslamOltInfo());
				resultCell.setCellStyle(styles.get("cell"));
				resultCell = row.createCell(6);
				resultCell.setCellValue(el.getRaPrincipalStr());
				resultCell.setCellStyle(styles.get("cell"));
				resultCell = row.createCell(7);
				resultCell.setCellValue(el.getRaBackupStr());
				resultCell.setCellStyle(styles.get("cell"));
				resultCell = row.createCell(8);
				resultCell.setCellValue(el.getIpLanStr());
				resultCell.setCellStyle(styles.get("cell"));
				resultCell = row.createCell(9);
				resultCell.setCellValue(el.getIpLoopbackStr());
				resultCell.setCellStyle(styles.get("cell"));
				resultCell = row.createCell(10);
				resultCell.setCellValue(el.getIpWanPeStr());
				resultCell.setCellStyle(styles.get("cell"));
				resultCell = row.createCell(11);
				resultCell.setCellValue(el.getInterfacePePrincipalStr());
				resultCell.setCellStyle(styles.get("cell"));
				resultCell = row.createCell(12);
				resultCell.setCellValue(el.getResultadoConfig());
				resultCell.setCellStyle(styles.get("cell"));
				resultCell = row.createCell(13);
				resultCell.setCellValue(el.getAlertaConfig());
				resultCell.setCellStyle(styles.get("cell"));
				resultCell = row.createCell(14);
				resultCell.setCellValue(el.getMotivoStr());
				resultCell.setCellStyle(styles.get("cell"));
				resultCell = row.createCell(15);
				resultCell.setCellValue(el.getTzDataInicio());
				resultCell.setCellStyle(styles.get("cell"));
			}
		}
		
		return response;
	}
	
	@SuppressWarnings("unchecked")
	private HttpServletResponse relatorioConfiguracaoVivo2(Map<String, Object> model, Workbook workbook, HttpServletRequest request, HttpServletResponse response) {
		
		Map<String, String> queryMap = (Map<String, String>) model.get("queryMap");
		Map<String, CellStyle> styles = createStyles(workbook);
		String dataInicial = queryMap.get("datainicial");
		String dataFinal = queryMap.get("datafinal");
		String produto = queryMap.get("servico");
		
		String respHeader = null;
		if (StringUtils.isBlank(produto))
			respHeader = String.format("attachment;filename=\"iat-relatorio-configuracao-vivo2-%s.xlsx\"", DataHelper.getCurrentLocalDateTimeStamp());
		else {
			produto = produto.replace(" ", "_").toLowerCase();
			respHeader = String.format("attachment;filename=\"iat-relatorio-configuracao-vivo2-%s-%s.xlsx\"", produto, DataHelper.getCurrentLocalDateTimeStamp());
		}
		
		response.setHeader("Content-Disposition", respHeader);
		
		/*
		 * SHEET ANALÍTICO QIP
		 */
		
		List<ProcessoIdVivo2Domain> lst = (List<ProcessoIdVivo2Domain>) model.get("listaAnaliticoQip");
		Sheet sheet = workbook.createSheet("Analitico QIP");
		
		sheet.setColumnWidth(0, 8000);
		sheet.setColumnWidth(1, 8000);
		sheet.setColumnWidth(2, 8000);
		sheet.setColumnWidth(3, 8000);
		sheet.setColumnWidth(4, 8000);
		sheet.setColumnWidth(5, 8000);
		sheet.setColumnWidth(6, 8000);
		sheet.setColumnWidth(7, 8000);
		sheet.setColumnWidth(8, 8000);
		sheet.setColumnWidth(9, 8000);
		sheet.setColumnWidth(10, 8000);
		sheet.setColumnWidth(11, 8000);
		
		// Title 
		Row titleRow = sheet.createRow(0);
		titleRow.setHeightInPoints(20);
		Cell titleCell = titleRow.createCell(0);
		if (StringUtils.isBlank(produto))
			titleCell.setCellValue(String.format("Analitico QIP | %s - %s", dataInicial, dataFinal));
		else
			titleCell.setCellValue(String.format("Analitico QIP | %s - %s | %s", dataInicial, dataFinal, produto));
		
		titleCell.setCellStyle(styles.get("title"));
		sheet.addMergedRegion(CellRangeAddress.valueOf("$A$1:$B$1"));
		
		// Header 
		String[] titlesHeaderQip = {
				"OS TBS"
				, "Fila"
				, "Serviço"
				, "Cliente"
				, "Resultado"
				, "IPv4 Wan"
				, "IPv4 Lan"
				, "IPv6 Wan"
				, "IPv6 Lan"
				, "Subnet IPv6 Wan"
				, "Subnet IPv6 Lan"
				, "Data Execução"
				};

		Row headerRow = sheet.createRow(1);
		headerRow.setHeightInPoints(20);
		Cell headerCell;
		for (int i = 0; i < titlesHeaderQip.length; i++) {
			headerCell = headerRow.createCell(i);
			headerCell.setCellValue(titlesHeaderQip[i]);
			headerCell.setCellStyle(styles.get("header"));
		}
		
		// Cell Results 
		int rowNum = 2;
		Cell resultCell;
		for(ProcessoIdVivo2Domain el : lst) {
			Row row = sheet.createRow(rowNum++);
			resultCell = row.createCell(0);
			resultCell.setCellValue(el.getIdTbs());
			resultCell.setCellStyle(styles.get("cell"));
			resultCell = row.createCell(1);
			resultCell.setCellValue(el.getFila());
			resultCell.setCellStyle(styles.get("cell"));
			resultCell = row.createCell(2);
			resultCell.setCellValue(el.getServico());
			resultCell.setCellStyle(styles.get("cell"));
			resultCell = row.createCell(3);
			resultCell.setCellValue(el.getCliente());
			resultCell.setCellStyle(styles.get("cell"));
			resultCell = row.createCell(4);
			resultCell.setCellValue(el.getResultadoProcedimento());
			resultCell.setCellStyle(styles.get("cell"));
			resultCell = row.createCell(5);
			resultCell.setCellValue(el.getIpv4WanBlock());
			resultCell.setCellStyle(styles.get("cell"));
			resultCell = row.createCell(6);
			resultCell.setCellValue(el.getIpv4LanBlock());
			resultCell.setCellStyle(styles.get("cell"));
			resultCell = row.createCell(7);
			resultCell.setCellValue(el.getIpv6WanBlock());
			resultCell.setCellStyle(styles.get("cell"));
			resultCell = row.createCell(8);
			resultCell.setCellValue(el.getIpv6LanBlock());
			resultCell.setCellStyle(styles.get("cell"));
			resultCell = row.createCell(9);
			resultCell.setCellValue(el.getIpv6WanSubnet());
			resultCell.setCellStyle(styles.get("cell"));
			resultCell = row.createCell(10);
			resultCell.setCellValue(el.getIpv6LanSubnet());
			resultCell.setCellStyle(styles.get("cell"));
			resultCell = row.createCell(11);
			resultCell.setCellValue(el.getTzDataInicio());
			resultCell.setCellStyle(styles.get("cell"));
		}
		
		/*
		 * SHEET ANALÍTICO CONFIGURACAO
		 */
		lst = (List<ProcessoIdVivo2Domain>) model.get("listaAnaliticoConfiguracao");
		sheet = workbook.createSheet("Analítico Configuração");

		sheet.setColumnWidth(0, 8000);
		sheet.setColumnWidth(1, 8000);
		sheet.setColumnWidth(2, 8000);
		sheet.setColumnWidth(3, 8000);
		sheet.setColumnWidth(4, 8000);
		sheet.setColumnWidth(5, 8000);
		sheet.setColumnWidth(6, 8000);
		
		// Title 
		titleRow = sheet.createRow(0);
		titleRow.setHeightInPoints(20);
		titleCell = titleRow.createCell(0);
		if (StringUtils.isBlank(produto))
			titleCell.setCellValue(String.format("Analítico Configuração | %s - %s", dataInicial, dataFinal));
		else
			titleCell.setCellValue(String.format("Analítico Configuração | %s - %s | %s", dataInicial, dataFinal, produto));
		titleCell.setCellStyle(styles.get("title"));
		sheet.addMergedRegion(CellRangeAddress.valueOf("$A$1:$D$1"));
		
		// Header 
		String[] titlesHeaderConfiguracao = {
				"OS TBS", 
				"Fila", 
				"Serviço", 
				"Cliente", 
				"Resultado", 
				"Motivo", 
				"Data Execução"
				};
		
		headerRow = sheet.createRow(1);
		headerRow.setHeightInPoints(20);
		for (int i = 0; i < titlesHeaderConfiguracao.length; i++) {
			headerCell = headerRow.createCell(i);
			headerCell.setCellValue(titlesHeaderConfiguracao[i]);
			headerCell.setCellStyle(styles.get("header"));
		}
		
		// Cell Results 
		rowNum = 2;
		for(ProcessoIdVivo2Domain el : lst) {
			Row row = sheet.createRow(rowNum++);
			resultCell = row.createCell(0);
			resultCell.setCellValue(el.getIdTbs());
			resultCell.setCellStyle(styles.get("cell"));
			resultCell = row.createCell(1);
			resultCell.setCellValue(el.getFila());
			resultCell.setCellStyle(styles.get("cell"));
			resultCell = row.createCell(2);
			resultCell.setCellValue(el.getServico());
			resultCell.setCellStyle(styles.get("cell"));
			resultCell = row.createCell(3);
			resultCell.setCellValue(el.getCliente());
			resultCell.setCellStyle(styles.get("cell"));
			resultCell = row.createCell(4);
			resultCell.setCellValue(el.getResultadoProcedimento());
			resultCell.setCellStyle(styles.get("cell"));
			resultCell = row.createCell(5);
			resultCell.setCellValue(el.getMotivo());
			resultCell.setCellStyle(styles.get("cell"));
			resultCell = row.createCell(6);
			resultCell.setCellValue(el.getTzDataInicio());
			resultCell.setCellStyle(styles.get("cell"));
		}
		
		return response;
	}
	
	/**
	 * Styles
	 */
	private static Map<String, CellStyle> createStyles(Workbook wb) {
		
		Map<String, CellStyle> styles = new HashMap<String, CellStyle>();
		CellStyle style;
		Font titleFont = wb.createFont();
		
		/* Título */
		titleFont.setFontHeightInPoints((short)12);
		titleFont.setBold(true);
		style = wb.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setFont(titleFont);
		styles.put("title", style);
		
		/* Header */
		Font headerFont = wb.createFont();
		headerFont.setFontHeightInPoints((short)10);
		headerFont.setBold(true);
		headerFont.setColor(IndexedColors.WHITE.getIndex());
		style = wb.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setFont(headerFont);
		styles.put("header", style);
		
		/* Cell */
		style = wb.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setWrapText(true);
		style.setFillForegroundColor(IndexedColors.AUTOMATIC.getIndex());
		style.setFillPattern(FillPatternType.NO_FILL);
		style.setBorderRight(BorderStyle.THIN);
		style.setRightBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderLeft(BorderStyle.THIN);
		style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderTop(BorderStyle.THIN);
		style.setTopBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderBottom(BorderStyle.THIN);
		style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		styles.put("cell", style);
		
		/* Cell Ok */
		style = wb.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setWrapText(true);
		style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setBorderRight(BorderStyle.THIN);
		style.setRightBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderLeft(BorderStyle.THIN);
		style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderTop(BorderStyle.THIN);
		style.setTopBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderBottom(BorderStyle.THIN);
		style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		styles.put("cell-ok", style);
		
		/* Cell NOK */
		style = wb.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setWrapText(true);
		style.setFillForegroundColor(IndexedColors.RED.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setBorderRight(BorderStyle.THIN);
		style.setRightBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderLeft(BorderStyle.THIN);
		style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderTop(BorderStyle.THIN);
		style.setTopBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderBottom(BorderStyle.THIN);
		style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		styles.put("cell-nok", style);
		
		/* Row OK */
		style = wb.createCellStyle();
		style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		styles.put("row-ok", style);
		
		/* Row NOK */
		style = wb.createCellStyle();
		style.setFillForegroundColor(IndexedColors.RED.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		styles.put("row-nok", style);
		
		return styles;
	}
}
