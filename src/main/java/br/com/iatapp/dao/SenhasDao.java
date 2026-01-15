package br.com.iatapp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import br.com.iatapp.config.MysqlConfig;
import br.com.iatapp.helper.CriptografiaHelper;
import br.com.iatapp.model.UsuarioModel;

public class SenhasDao {

	private Connection conn;
	private PreparedStatement ps;
	private ResultSet rs;
	
	public SenhasDao() throws Exception {
		try {
            this.conn = MysqlConfig.getConnection();
        } catch (Exception e) {
            throw new Exception("Erro: " + e.getMessage());
        }
	}
	
	/**
	 * buscarSenhasUsuario
	 * @param idUsuario
	 * @return
	 * @throws Exception
	 */
	public UsuarioModel buscarSenhasUsuarioIaTRede() throws Exception {        
		String SQL = " call sp_testes_buscar_usuario_rede_iat(); ";
		
		UsuarioModel usuario = null;		
        
		try {
            ps = conn.prepareStatement(SQL);
            rs = ps.executeQuery();
            
            if (rs.next()) {
            	usuario =  new UsuarioModel();
            	usuario.setLoginCpe(rs.getString("loginCpe"));
            	usuario.setSenhaCpe(CriptografiaHelper.base64Decode(rs.getString("senhaCpe")));
            	usuario.setSenhaEnableCpe(CriptografiaHelper.base64Decode(rs.getString("senhaCpe")));
            	usuario.setLoginRedeIp(rs.getString("login"));
            	usuario.setSenhaRedeIp(CriptografiaHelper.base64Decode(rs.getString("senha")));
            	usuario.setLoginPe(rs.getString("login"));
            	usuario.setSenhaPe(CriptografiaHelper.base64Decode(rs.getString("senha")));
            }
            return usuario;

        } catch (SQLException sqle) {
            throw new Exception(sqle);
        } finally {
        	MysqlConfig.closeConnection(conn, ps, rs);
        }
    }
	
	public UsuarioModel buscarSenhasUsuarioIaTConfMaster() throws Exception {        
		String SQL = " call sp_testes_buscar_usuario_rede_conf_master(); ";
		
		UsuarioModel usuario = null;		
        
		try {
            ps = conn.prepareStatement(SQL);
            rs = ps.executeQuery();
            
            if (rs.next()) {
            	usuario =  new UsuarioModel();
            	usuario.setLoginCpe(rs.getString("loginCpe"));
            	usuario.setSenhaCpe(CriptografiaHelper.base64Decode(rs.getString("senhaCpe")));
            	usuario.setSenhaEnableCpe(CriptografiaHelper.base64Decode(rs.getString("senhaCpe")));
            	usuario.setLoginRedeIp(rs.getString("login"));
            	usuario.setSenhaRedeIp(CriptografiaHelper.base64Decode(rs.getString("senha")));
            	usuario.setLoginPe(rs.getString("login"));
            	usuario.setSenhaPe(CriptografiaHelper.base64Decode(rs.getString("senha")));
            }
            return usuario;

        } catch (SQLException sqle) {
            throw new Exception(sqle);
        } finally {
        	MysqlConfig.closeConnection(conn, ps, rs);
        }
    }
	
}
