package br.com.iatapp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang3.exception.ExceptionUtils;

import br.com.iatapp.config.MysqlConfig;
import br.com.iatapp.helper.CriptografiaHelper;
import br.com.iatapp.helper.RedeHelper;
import br.com.iatapp.logger.ExceptionLogger;
import br.com.iatapp.model.UsuarioModel;

public class LoginDao {

	private Connection conn;
	private PreparedStatement ps;
	private ResultSet rs;
	
	public LoginDao() throws Exception {
		try {
            this.conn = MysqlConfig.getConnection();
        } catch (Exception e) {
            throw new Exception("Erro: " + e.getMessage());
        }
	}
	
	/**
	 * 
	 * @param usuarioModel
	 * @return
	 * @throws Exception
	 */
	public UsuarioModel verificaLoginUsuario(UsuarioModel usuarioModel) throws Exception {        
		
		String SQL = " select t1.idUsuario, t1.nome, t1.email, t1.telefone, t1.numeroRe, t1.idPerfil, t1.dataExpiracaoSenha, t2.nomePerfil "
				+ " from tb_admin_usuarios t1 "
				+ " inner join tb_admin_perfis t2 on t1.idPerfil = t2.idPerfil "
				+ " where t1.numeroRe = ? and t1.senha = ? "
				+ " and t1.ativo = 1 and t2.ativo = 1 "
				+ " limit 1; ";
		
        try {
            ps = conn.prepareStatement(SQL);
            ps.setString(1, usuarioModel.getNumeroRe());
            ps.setString(2, CriptografiaHelper.converteStringToMD5(usuarioModel.getSenha()));
            rs = ps.executeQuery();
            
            if (rs.next()) {
            	usuarioModel.setIdUsuario(rs.getInt("idUsuario"));
            	usuarioModel.setEmail(rs.getString("email"));
            	usuarioModel.setNome(rs.getString("nome"));
            	usuarioModel.setTelefone(rs.getString("telefone"));
            	usuarioModel.setIdPerfil(rs.getInt("idPerfil"));
            	usuarioModel.setNomePerfil(rs.getString("nomePerfil"));
            }
            
            return usuarioModel;

        } catch (SQLException sqle) {
            throw new Exception(sqle);
        } finally {
        	MysqlConfig.closeConnection(conn, ps, rs);
        }
    }
	
	public String alterarSenha(UsuarioModel usuario) throws Exception {        
		
		String SQL = " call sp_admin_alterar_senha(?,?,?); ";
		String saida = null;
		
        try {
            ps = conn.prepareStatement(SQL);
            ps.setString(1, usuario.getNumeroRe());
            ps.setString(2, CriptografiaHelper.converteStringToMD5(usuario.getSenhaAntiga()));
            ps.setString(3, CriptografiaHelper.converteStringToMD5(usuario.getNovaSenha()));
            rs = ps.executeQuery();
            
            if (rs.next()) {
            	saida = rs.getString("saida");
            }
            
            return saida;

        } catch (SQLException sqle) {
        	ExceptionLogger.record(ExceptionUtils.getStackTrace(sqle),
        			RedeHelper.retornaInfoProcedimento(LoginDao.class.getName(), "alterarSenha"));
            throw new Exception(sqle);
        } finally {
        	MysqlConfig.closeConnection(conn, ps, rs);
        }
    }
	
}
