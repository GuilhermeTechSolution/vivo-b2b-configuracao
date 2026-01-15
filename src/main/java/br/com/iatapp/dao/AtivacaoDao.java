package br.com.iatapp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONObject;

import br.com.iatapp.config.MysqlConfig;
import br.com.iatapp.helper.CriptografiaHelper;
import br.com.iatapp.helper.RedeHelper;
import br.com.iatapp.logger.ExceptionLogger;

public class AtivacaoDao {

	private Connection conn;
	private PreparedStatement ps;
	private ResultSet rs;
	
	public AtivacaoDao() throws Exception {
		try {
            this.conn = MysqlConfig.getConnection();
        } catch (Exception e) {
            throw new Exception("Erro: " + e.getMessage());
        }
	}
	
	/**
	 * buscarSenhaConfPe
	 * @return
	 * @throws Exception
	 */
	public JSONObject buscarSenhaConfPe() throws Exception {

        String SQL = " SELECT login, senha " + 
        		" FROM iat_telefonica.tb_senhas_conf_pe; ";
       
        try {
            ps = conn.prepareStatement(SQL);
            rs = ps.executeQuery();
            
            if (rs.next()) {
            	JSONObject jsonObj = new JSONObject();
            	jsonObj.put("login", rs.getString("login"));
            	jsonObj.put("senha", CriptografiaHelper.base64Decode(rs.getString("senha")));
            	return jsonObj;
            }            
            return null;

        } catch (SQLException sqle) {
        	ExceptionLogger.record(ExceptionUtils.getStackTrace(sqle),
        			RedeHelper.retornaInfoProcedimento(AtivacaoDao.class.getName(), "buscarSbcSip"));
            throw new Exception(sqle);
        } finally {
        	MysqlConfig.closeConnection(conn, ps, rs);
        }
    }
	
}
