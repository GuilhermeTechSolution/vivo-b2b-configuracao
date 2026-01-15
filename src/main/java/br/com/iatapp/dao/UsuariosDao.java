package br.com.iatapp.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import br.com.iatapp.config.MysqlConfig;
import br.com.iatapp.helper.CriptografiaHelper;
import br.com.iatapp.helper.RedeHelper;
import br.com.iatapp.logger.ExceptionLogger;
import br.com.iatapp.model.ItemModel;
import br.com.iatapp.model.UsuarioModel;

public class UsuariosDao {
	
	private Connection conn;
	private PreparedStatement ps;
	private CallableStatement cs;
	private ResultSet rs;
	
	/**
	 * Construtor da Classe
	 * 
	 * @throws Exception
	 */
	public UsuariosDao() throws Exception {
		try {
            this.conn = MysqlConfig.getConnection();
        } catch (Exception e) {
        	ExceptionLogger.record(ExceptionUtils.getStackTrace(e),
        			RedeHelper.retornaInfoProcedimento(UsuariosDao.class.getName(), "Abrindo conexao com banco de dados."));
            throw new Exception("Erro: " + e.getMessage());
        }
	}
	
	/**
	 * Buscar usuarios cadastrados
	 * @param ativo
	 * @return
	 * @throws Exception
	 */
	public List<UsuarioModel> buscarUsuarios(int ativo) throws Exception {

        String SQL = " call sp_admin_buscar_usuarios(?); ";
        
        UsuarioModel aux = null;
        List<UsuarioModel> lista = null;
        try {
            ps = conn.prepareStatement(SQL);
            ps.setInt(1, ativo);
            rs = ps.executeQuery();
            
            lista = new ArrayList<>();
            
            while (rs.next()) {
            	aux = new UsuarioModel();
            	aux.setIdUsuario(rs.getInt("idUsuario"));
            	aux.setIdPerfil(rs.getInt("idPerfil"));
            	aux.setNomePerfil(rs.getString("nomePerfil"));
            	aux.setNome(rs.getString("nome"));
            	aux.setEmail(rs.getString("email"));
            	aux.setTelefone(rs.getString("telefone"));
            	aux.setNumeroRe(rs.getString("numeroRe"));
            	aux.setNomeUsuarioCadastro(rs.getString("nomeUsuarioCadastro"));
            	aux.setDataCadastro(rs.getString("dataCadastro"));
            	aux.setAtivo(rs.getInt("ativo"));
            	aux.setDataExpiracaoSenha(rs.getString("dataExpiracaoSenha"));
            	lista.add(aux);
            }
            
            return lista;

        } catch (SQLException sqle) {
        	ExceptionLogger.record(ExceptionUtils.getStackTrace(sqle),
        			RedeHelper.retornaInfoProcedimento(UsuariosDao.class.getName(), "buscarUsuarios"));
            throw new Exception(sqle);
        } finally {
        	MysqlConfig.closeConnection(conn, ps, rs);
        }
    }
	
	public UsuarioModel buscarUsuarioByNumeroRe(String numeroRe) throws Exception {

        String SQL = "SELECT t1.idUsuario, t1.idPerfil, t3.nomePerfil, " +
	        		" t1.nome, t1.email, t1.telefone, t1.numeroRe, " + 
	        		" t1.idUsuarioCadastro, t2.nome as nomeUsuarioCadastro, " +
	        		" t1.ativo " +
	        		" FROM iat_telefonica.tb_admin_usuarios t1 " +
	        		" inner join iat_telefonica.tb_admin_usuarios t2 on t1.idUsuarioCadastro = t2.idUsuario " +
	        		" inner join iat_telefonica.tb_admin_perfis t3 on t1.idPerfil = t3.idPerfil " +
	        		" left join iat_telefonica.tb_admin_empresas t4 on t1.idEmpresa = t4.idEmpresa " +
	        		" where t1.numeroRe = ? " +
	        		" and t1.ativo = 1 " +
	        		" limit 1;";
        
        UsuarioModel obj = null;
        try {
            ps = conn.prepareStatement(SQL);
            ps.setString(1, numeroRe);
            rs = ps.executeQuery();
            
            while (rs.next()) {
            	obj = new UsuarioModel();
            	obj.setIdUsuario(rs.getInt("idUsuario"));
            	obj.setNome(rs.getString("nome").toUpperCase());
            	obj.setEmail(rs.getString("email"));
            	obj.setTelefone(rs.getString("telefone"));
            	obj.setNumeroRe(rs.getString("numeroRe"));
            	obj.setNomePerfil(rs.getString("nomePerfil"));
            	obj.setIdPerfil(rs.getInt("idPerfil"));
            }
            
            return obj;

        } catch (SQLException sqle) {
        	ExceptionLogger.record(ExceptionUtils.getStackTrace(sqle),
        			RedeHelper.retornaInfoProcedimento(UsuariosDao.class.getName(), "buscarUsuarioByNumeroRe"));
            throw new Exception(sqle);
        } finally {
        	MysqlConfig.closeConnection(conn, ps, rs);
        }
    }
	
	/**
	 * Cadastro de usuario
	 * @param usuario
	 * @param idUsuario
	 * @return
	 * @throws Exception
	 */
	public String cadastrarUsuario(UsuarioModel usuario, int idUsuario) throws Exception {

        String SQL = " call sp_admin_cadastrar_usuario(?,?,?,?,?,?,?); ";
        
        String saida = null;
        
        try {
            ps = conn.prepareStatement(SQL);
            ps.setInt(1, usuario.getIdPerfil());
            ps.setString(2, usuario.getNome().trim());
            ps.setString(3, usuario.getEmail().trim());
            ps.setString(4, usuario.getTelefone().trim());
            ps.setString(5, usuario.getNumeroRe().trim());
            ps.setString(6, CriptografiaHelper.converteStringToMD5(usuario.getNumeroRe().trim()));
            ps.setInt(7, idUsuario);
            rs = ps.executeQuery();
            
            if (rs.next()) {
            	saida = rs.getString("saida");
            }
            
            return saida;

        } catch (SQLException sqle) {
        	ExceptionLogger.record(ExceptionUtils.getStackTrace(sqle),
        			RedeHelper.retornaInfoProcedimento(UsuariosDao.class.getName(), "cadastrarUsuario"));
            throw new Exception(sqle);
        } finally {
        	MysqlConfig.closeConnection(conn, ps, rs);
        }
    }
	
	/**
	 * Alteracao de usuario
	 * @param usuario
	 * @return
	 * @throws Exception
	 */
	public String editarUsuario(UsuarioModel usuario) throws Exception {

        String SQL = " call sp_admin_alterar_usuario(?,?,?,?,?,?,?); ";
        
        String saida = null;
        try {
            ps = conn.prepareStatement(SQL);
            ps.setInt(1, usuario.getIdUsuario());
            ps.setInt(2, usuario.getIdPerfil());
            ps.setString(3, usuario.getNome().trim());
            ps.setString(4, usuario.getEmail().trim());
            ps.setString(5, usuario.getTelefone().trim());
            ps.setString(6, usuario.getNumeroRe().trim());
            ps.setInt(7, usuario.getAtivo());
            rs = ps.executeQuery();
            
            if (rs.next()) {
            	saida = rs.getString("saida");
            }
            
            return saida;

        } catch (SQLException sqle) {
        	ExceptionLogger.record(ExceptionUtils.getStackTrace(sqle),
        			RedeHelper.retornaInfoProcedimento(UsuariosDao.class.getName(), "editarUsuario"));
            throw new Exception(sqle);
        } finally {
        	MysqlConfig.closeConnection(conn, ps, rs);
        }
    }

	/**
	 * Retorna a lista de perfis para o Select Html
	 * @return
	 * @throws Exception
	 */
	public List<ItemModel> retornaListaPerfis() throws Exception {

        String SQL = " select idPerfil, nomePerfil"
        		+ " from tb_admin_perfis"
        		+ " where ativo = 1"
        		+ " order by nomePerfil ";
        
        List<ItemModel> lista = null;
        ItemModel bean = null;
        
        try {
            ps = conn.prepareStatement(SQL);
            rs = ps.executeQuery();
            lista = new ArrayList<>();
            
            while (rs.next()) {
            	bean = new ItemModel();
            	bean.setId(rs.getString("idPerfil"));
            	bean.setDescricao(rs.getString("nomePerfil"));
            	lista.add(bean);            	
            }
            
            return lista;

        } catch (SQLException sqle) {
        	ExceptionLogger.record(ExceptionUtils.getStackTrace(sqle),
        			RedeHelper.retornaInfoProcedimento(UsuariosDao.class.getName(), "retornaListaPerfis"));
            throw new Exception(sqle);
        } finally {
        	MysqlConfig.closeConnection(conn, ps, rs);
        }
    }
	
	/**
	 * Buscar perfis cadastrados
	 * @param ativo
	 * @return
	 * @throws Exception
	 */
	public List<UsuarioModel> buscarPerfis(int ativo) throws Exception {

        String SQL = " call sp_admin_buscar_perfis(?); ";        
        UsuarioModel aux = null;
        List<UsuarioModel> lista = null;
        try {
            ps = conn.prepareStatement(SQL);
            ps.setInt(1, ativo);
            rs = ps.executeQuery();
            
            lista = new ArrayList<>();
            
            while (rs.next()) {
            	aux = new UsuarioModel();
            	aux.setIdPerfil(rs.getInt("idPerfil"));
            	aux.setNomePerfil(rs.getString("nomePerfil"));
            	aux.setAtivo(rs.getInt("ativo"));
            	lista.add(aux);
            }
            
            return lista;

        } catch (SQLException sqle) {
        	ExceptionLogger.record(ExceptionUtils.getStackTrace(sqle),
        			RedeHelper.retornaInfoProcedimento(UsuariosDao.class.getName(), "buscarPerfis"));
            throw new Exception(sqle);
        } finally {
        	MysqlConfig.closeConnection(conn, ps, rs);
        }
    }
	
	/**
	 * Buscar permissoes dos perfis
	 * @param ativo
	 * @return
	 * @throws Exception
	 */
	public List<UsuarioModel> buscarPermissoesPerfil(int idPerfil, int idPagina) throws Exception {

        String SQL = " call sp_admin_buscar_permissoes_perfil(?,?); ";        
        UsuarioModel aux = null;
        List<UsuarioModel> lista = null;
        try {
            ps = conn.prepareStatement(SQL);
            ps.setInt(1, idPerfil);
            ps.setInt(2, idPagina);
            rs = ps.executeQuery();
            
            lista = new ArrayList<>();
            
            while (rs.next()) {
            	aux = new UsuarioModel();
            	aux.setIdPerfil(rs.getInt("idPerfil"));
            	aux.setNomePerfil(rs.getString("nomePerfil"));
            	aux.setIdPagina(rs.getInt("idPagina"));
            	aux.setNomePagina(rs.getString("nomePagina"));
            	aux.setIdPermissao(rs.getInt("idPermissao"));
            	lista.add(aux);
            }
            
            return lista;

        } catch (SQLException sqle) {
        	ExceptionLogger.record(ExceptionUtils.getStackTrace(sqle),
        			RedeHelper.retornaInfoProcedimento(UsuariosDao.class.getName(), "buscarPermissoesPerfil"));
            throw new Exception(sqle);
        } finally {
        	MysqlConfig.closeConnection(conn, ps, rs);
        }
    }
	
	/**
	 * Cadastro de perfil
	 * @param nomePerfil
	 * @return
	 * @throws Exception
	 */
	public String cadastrarPerfil(String nomePerfil) throws Exception {

        String SQL = " call sp_admin_cadastrar_perfil(?); ";        
        String saida = null;
        
        try {
            ps = conn.prepareStatement(SQL);
            ps.setString(1, nomePerfil.trim());
            rs = ps.executeQuery();
            
            if (rs.next()) {
            	saida = rs.getString("saida");
            }
            
            return saida;

        } catch (SQLException sqle) {
        	ExceptionLogger.record(ExceptionUtils.getStackTrace(sqle),
        			RedeHelper.retornaInfoProcedimento(UsuariosDao.class.getName(), "cadastrarPerfil"));
            throw new Exception(sqle);
        } finally {
        	MysqlConfig.closeConnection(conn, ps, rs);
        }
    }
	
	/**
	 * 
	 * @param usuario
	 * @return
	 * @throws Exception
	 */
	public String editarPerfil(UsuarioModel usuario) throws Exception {

        String SQL = " call sp_admin_alterar_perfil(?,?); ";        
        String saida = null;
        
        try {
            ps = conn.prepareStatement(SQL);
            ps.setInt(1, usuario.getIdPerfil());
            ps.setString(2, usuario.getNomePerfil().trim());
            rs = ps.executeQuery();
            
            if (rs.next()) {
            	saida = rs.getString("saida");
            }
            
            return saida;

        } catch (SQLException sqle) {
        	ExceptionLogger.record(ExceptionUtils.getStackTrace(sqle),
        			RedeHelper.retornaInfoProcedimento(UsuariosDao.class.getName(), "editarPerfil"));
            throw new Exception(sqle);
        } finally {
        	MysqlConfig.closeConnection(conn, ps, rs);
        }
    }
	
	/**
	 * Reset de Senha
	 * @param idUsuario
	 * @return
	 * @throws Exception
	 */
	public String resetarSenha(UsuarioModel usuario) throws Exception {

        String SQL = " call sp_admin_resetar_senha(?,?); ";
        
        String saida = null;
        
        try {
            ps = conn.prepareStatement(SQL);
            ps.setInt(1, usuario.getIdUsuario());
            ps.setString(2, CriptografiaHelper.converteStringToMD5(usuario.getNumeroRe().trim()));
            rs = ps.executeQuery();
            
            if (rs.next()) {
            	saida = rs.getString("saida");
            }
            
            return saida;

        } catch (SQLException sqle) {
        	ExceptionLogger.record(ExceptionUtils.getStackTrace(sqle),
        			RedeHelper.retornaInfoProcedimento(UsuariosDao.class.getName(), "resetarSenha"));
            throw new Exception(sqle);
        } finally {
        	MysqlConfig.closeConnection(conn, ps, rs);
        }
    }

	public void editarPermissoes(String jsonArr) throws Exception {
		
		JSONArray jsonArray = new JSONArray(jsonArr);
		if(jsonArray != null && jsonArray.length() > 0) {
			for (int c = 0; c < jsonArray.length(); c++) {
				
				JSONObject jsonObject = jsonArray.getJSONObject(c);
				
				String SQL = " update tb_admin_permissoes "
						+ " set idPermissao = ? "
						+ " where idPerfil = ? and idPagina = ? ;";
				
		        try {
		        	cs = conn.prepareCall(SQL);
		        	cs.setString(1, jsonObject.getString("idPermissao"));
		        	cs.setString(2, jsonObject.getString("idPerfil"));
		        	cs.setString(3, jsonObject.getString("idPagina"));
		        	cs.execute();
		        } catch (SQLException sqle) {
		        	ExceptionLogger.record(ExceptionUtils.getStackTrace(sqle),
		        			RedeHelper.retornaInfoProcedimento(UsuariosDao.class.getName(), "editarPermissoes"));
					MysqlConfig.closeConnection(conn, cs);
		            throw new Exception(sqle);
		        }
			}
			
			MysqlConfig.closeConnection(conn, cs);
		}
	}
	
	public Map<Integer, Integer> retornaListaPermissoesPagina(UsuarioModel usuario) throws Exception {
		
		String SQL = " call sp_admin_buscar_permissoes_perfil(?, 0); ";
         
		Map<Integer, Integer> lista = new HashMap<Integer, Integer>();
        
        try {
            ps = conn.prepareStatement(SQL);
            ps.setInt(1, usuario.getIdPerfil());
            rs = ps.executeQuery();
            
            while (rs.next()) {
            	lista.put(rs.getInt("idPagina"), rs.getInt("idPermissao"));
            }
            
            return lista;

        } catch (SQLException sqle) {
        	ExceptionLogger.record(ExceptionUtils.getStackTrace(sqle),
        			RedeHelper.retornaInfoProcedimento(UsuariosDao.class.getName(), "retornaListaPermissoesPagina"));
            throw new Exception(sqle);
        } finally {
        	MysqlConfig.closeConnection(conn, ps, rs);
        }
	}

}
