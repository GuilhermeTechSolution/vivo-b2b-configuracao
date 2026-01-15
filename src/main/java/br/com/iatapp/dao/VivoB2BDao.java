package br.com.iatapp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONObject;

import br.com.iatapp.config.MysqlConfig;
import br.com.iatapp.helper.RedeHelper;
import br.com.iatapp.logger.ExceptionLogger;
import br.com.iatapp.model.RdistModel;

public class VivoB2BDao {
	
	private Connection conn;
	private PreparedStatement ps;
	private ResultSet rs;
	
	/**
	 * Construtor da Classe
	 * @throws Exception
	 */
	public VivoB2BDao() throws Exception {
		try {
            this.conn = MysqlConfig.getConnection();
        } catch (Exception e) {
        	ExceptionLogger.record(ExceptionUtils.getStackTrace(e),
        			RedeHelper.retornaInfoProcedimento(VivoB2BDao.class.getName(), "Abrindo conexao com banco de dados."));
            throw new Exception("Erro: " + e.getMessage());
        }
	}	
	
	/**
	 * buscarSbcSip
	 * @param cnl
	 * @param at
	 * @return
	 * @throws Exception
	 */
	public String buscarSbcSip(String cnl, String at) throws Exception {

        String SQL = " select sbc "
        		+ " from iat_telefonica.tb_config_localidades_sbc "
        		+ " where cnl = ? and at = ?"
        		+ " limit 1; ";
       
        try {
            ps = conn.prepareStatement(SQL);
            ps.setString(1, cnl);
            ps.setString(2, at);
            rs = ps.executeQuery();
            
            if (rs.next()) {
            	return rs.getString("sbc");
            }            
            return "";

        } catch (SQLException sqle) {
        	ExceptionLogger.record(ExceptionUtils.getStackTrace(sqle),
        			RedeHelper.retornaInfoProcedimento(VivoB2BDao.class.getName(), "buscarSbcSip"));
            throw new Exception(sqle);
        } finally {
        	MysqlConfig.closeConnection(conn, ps, rs);
        }
    }
	
	/**
	 * buscarVrfSip
	 * @param sbc
	 * @return
	 * @throws Exception
	 */
	public JSONObject buscarVrfSip(String sbc) throws Exception {

        String SQL = " select vrf, comunityVoip "
        		+ " from iat_telefonica.tb_config_vrfs_sip "
        		+ " where sbc = ?"
        		+ " limit 1; ";
       
        try {
            ps = conn.prepareStatement(SQL);
            ps.setString(1, sbc);
            rs = ps.executeQuery();
            
            if (rs.next()) {
            	JSONObject jsonObj = new JSONObject();
            	jsonObj.put("vrfSip", rs.getString("vrf"));
            	jsonObj.put("comunityVoip", rs.getString("comunityVoip"));
            	return jsonObj;
            }            
            return null;

        } catch (SQLException sqle) {
        	ExceptionLogger.record(ExceptionUtils.getStackTrace(sqle),
        			RedeHelper.retornaInfoProcedimento(VivoB2BDao.class.getName(), "buscarSbcSip"));
            throw new Exception(sqle);
        } finally {
        	MysqlConfig.closeConnection(conn, ps, rs);
        }
    }
	
	/**
	 * buscarSbcSip
	 * @param cnl
	 * @param at
	 * @return
	 * @throws Exception
	 */
	public JSONObject buscarSbcIps(String cnl, String at) throws Exception {

        String SQL = " SELECT t2.sbcPri, t2.ipPrincipal, t2.ipRedundante, t2.sbcSec, parBbip " + 
        		"	FROM iat_telefonica.tb_config_localidades_sbc t1 " + 
        		"	inner join iat_telefonica.tb_config_sbc_ips t2 on t1.sbcPri = t2.sbcPri " + 
        		"	where t1.cnl = ? and t1.at = ? " + 
        		"	limit 1; ";
        
        JSONObject jsonObject = new JSONObject();
       
        try {
            ps = conn.prepareStatement(SQL);
            ps.setString(1, cnl);
            ps.setString(2, at);
            rs = ps.executeQuery();
            
            if (rs.next()) {
            	jsonObject.put("sbcPri", rs.getString("sbcPri"));
            	jsonObject.put("ipPrincipal", rs.getString("ipPrincipal"));
            	jsonObject.put("ipRedundante", rs.getString("ipRedundante"));
            	jsonObject.put("sbcSec", rs.getString("sbcSec"));
            	jsonObject.put("at", at);
            	jsonObject.put("cnl", cnl);
            	return jsonObject;
            }            
            return jsonObject;

        } catch (SQLException sqle) {
        	ExceptionLogger.record(ExceptionUtils.getStackTrace(sqle),
        			RedeHelper.retornaInfoProcedimento(VivoB2BDao.class.getName(), "buscarSbcSip"));
            throw new Exception(sqle);
        } finally {
        	MysqlConfig.closeConnection(conn, ps, rs);
        }
    }
	
	/**
	 * buscarSbcSip
	 * @param cnl
	 * @param at
	 * @return
	 * @throws Exception
	 */
	public JSONObject buscarSbcIpsPorHostname(String sbcPrimario) throws Exception {

        String SQL = " SELECT t1.ipPrincipal, t1.ipRedundante, t1.sbcPri, t1.sbcSec " + 
        		"	FROM iat_telefonica.tb_config_sbc_ips t1 " + 
        		"	where t1.sbcPri = ? " + 
        		"	limit 1; ";
        
        JSONObject jsonObject = new JSONObject();
       
        try {
            ps = conn.prepareStatement(SQL);
            ps.setString(1, sbcPrimario);
            rs = ps.executeQuery();
            
            if (rs.next()) {
            	jsonObject.put("ipPrincipal", rs.getString("ipPrincipal"));
            	jsonObject.put("ipRedundante", rs.getString("ipRedundante"));
            	jsonObject.put("sbcPri", rs.getString("sbcPri"));
            	jsonObject.put("sbcSec", rs.getString("sbcSec"));
            	return jsonObject;
            }            
            return jsonObject;

        } catch (SQLException sqle) {
        	ExceptionLogger.record(ExceptionUtils.getStackTrace(sqle),
        			RedeHelper.retornaInfoProcedimento(VivoB2BDao.class.getName(), "buscarSbcSip"));
            throw new Exception(sqle);
        } finally {
        	MysqlConfig.closeConnection(conn, ps, rs);
        }
    }
	
	public long createIdConfigSipOneCore(String token) throws Exception {

		String SQL = " call vivo_b2b_configuracao.sp_insere_config_siponecore(?); ";
		
		try {
			ps = conn.prepareStatement(SQL);
			ps.setString(1, token);
			rs = ps.executeQuery();
			
			while (rs.next()) {
				return rs.getLong("idTeste");
			}
			
			return (long) 0;
			
		} catch (SQLException sqle) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(sqle),
					RedeHelper.retornaInfoProcedimento(VivoB2BDao.class.getName(), "createIdConfigSipOneCore"));
			throw new Exception(sqle);
		} finally {
			MysqlConfig.closeConnection(conn, ps, rs);
		}
	}
	
	public int createIdTesteRede(String token) throws Exception {

		String SQL = " call vivo_b2b_configuracao.sp_gerar_id_teste_rede(?); ";
		
		try {
			ps = conn.prepareStatement(SQL);
			ps.setString(1, token);
			rs = ps.executeQuery();
			
			while (rs.next()) {
				return rs.getInt("idTeste");
			}
			
			return 0;
			
		} catch (SQLException sqle) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(sqle),
					RedeHelper.retornaInfoProcedimento(VivoB2BDao.class.getName(), "createIdTesteRede"));
			throw new Exception(sqle);
		} finally {
			MysqlConfig.closeConnection(conn, ps, rs);
		}
	}
	
	public String findBackboneByRdist(String rdist) throws Exception {
		
		String SQL = " select backbone "
				+ " from iat_telefonica.tb_rdist_vivo2 "
				+ " where rdist = ?; ";
		
		try {
			ps = conn.prepareStatement(SQL);
			ps.setString(1, rdist);
			rs = ps.executeQuery();
			
			if(rs.next())
				return rs.getString("backbone").toLowerCase();
			
			
			return "";
			
		} catch (SQLException sqle) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(sqle),
					RedeHelper.retornaInfoProcedimento(VivoB2BDao.class.getName(), "createIdTesteRede"));
			throw new Exception(sqle);
		} finally {
			MysqlConfig.closeConnection(conn, ps, rs);
		}
		
	}
	
	public String findRdistByBackbone(String backbone) throws Exception {
		
		String SQL = " select rdist "
				+ " from iat_telefonica.tb_rdist_vivo2 "
				+ " where backbone = ?; ";
		
		try {
			ps = conn.prepareStatement(SQL);
			ps.setString(1, backbone);
			rs = ps.executeQuery();
			
			if(rs.next())
				return rs.getString("rdist").toLowerCase();
			
			
			return "";
			
		} catch (SQLException sqle) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(sqle),
					RedeHelper.retornaInfoProcedimento(VivoB2BDao.class.getName(), "createIdTesteRede"));
			throw new Exception(sqle);
		} finally {
			MysqlConfig.closeConnection(conn, ps, rs);
		}
		
	}
	
	/**
	 * findAllRdistAndBackbone
	 * @return
	 * @throws Exception
	 */
	public List<RdistModel> findAllRdistAndBackbone() throws Exception {

		String SQL = " select id, rdist, backbone"
				+ " from iat_telefonica.tb_rdist_vivo2; ";
       
		RdistModel aux = null;
        List<RdistModel> lista = null;
        try {
            ps = conn.prepareStatement(SQL);
            rs = ps.executeQuery();
            
            lista = new ArrayList<>();
            
            while (rs.next()) {
            	aux = new RdistModel();
            	aux.setId(rs.getInt("id"));
            	aux.setRdist(rs.getString("rdist"));
            	aux.setBackbone(rs.getString("backbone"));
            	lista.add(aux);
            }            

        	return lista;

        } catch (SQLException sqle) {
        	ExceptionLogger.record(ExceptionUtils.getStackTrace(sqle),
        			RedeHelper.retornaInfoProcedimento(VivoB2BDao.class.getName(), "buscarRdistBackbone"));
            throw new Exception(sqle);
        } finally {
        	MysqlConfig.closeConnection(conn, ps, rs);
        }
        
	}
	
	/**
	 * cadastrarRdist
	 * @param rdist
	 * @return
	 * @throws Exception
	 */
	public String cadastrarRdist(RdistModel rdist) throws Exception {

        String SQL = " call sp_insere_rdist_backbone(?,?); ";
        
        String saida = null;
        
        try {
            ps = conn.prepareStatement(SQL);
            ps.setString(1, rdist.getRdist().trim());
            ps.setString(2, rdist.getBackbone().trim());
            rs = ps.executeQuery();
            
            if (rs.next()) {
            	saida = rs.getString("saida");
            }
            
            return saida;

        } catch (SQLException sqle) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(sqle),
					RedeHelper.retornaInfoProcedimento(VivoB2BDao.class.getName(), "cadastrarRdist"));
            throw new Exception(sqle);
        } finally {
        	MysqlConfig.closeConnection(conn, ps, rs);
        }
    }
	
	/**
	 * alteraRdist
	 * @param rdist
	 * @return
	 * @throws Exception
	 */
	public String alteraRdist(RdistModel rdist) throws Exception {

        String SQL = " call sp_altera_rdist_backbone(?,?,?); ";
        
        String saida = null;
        
        try {
            ps = conn.prepareStatement(SQL);
            ps.setInt(1, rdist.getId());
            ps.setString(2, rdist.getRdist());
            ps.setString(3, rdist.getBackbone());
            rs = ps.executeQuery();
            
            if (rs.next()) {
            	saida = rs.getString("saida");
            }
            
            return saida;

        } catch (SQLException sqle) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(sqle),
					RedeHelper.retornaInfoProcedimento(VivoB2BDao.class.getName(), "alteraRdist"));
            throw new Exception(sqle);
        } finally {
        	MysqlConfig.closeConnection(conn, ps, rs);
        }
        
    }
	
	
	/**
	 * excluirRdist
	 * @param rdist
	 * @return
	 * @throws Exception
	 */
	public String excluirRdist(RdistModel rdist) throws Exception {

        String SQL = " call sp_excluir_rdist_backbone(?); ";
        
        String saida = null;
        
        try {
            ps = conn.prepareStatement(SQL);
            ps.setInt(1, rdist.getId());
            rs = ps.executeQuery();
            
            if (rs.next()) {
            	saida = rs.getString("saida");
            }
            
            return saida;

        } catch (SQLException sqle) {
			ExceptionLogger.record(ExceptionUtils.getStackTrace(sqle),
					RedeHelper.retornaInfoProcedimento(VivoB2BDao.class.getName(), "excluirRdist"));
            throw new Exception(sqle);
        } finally {
        	MysqlConfig.closeConnection(conn, ps, rs);
        }
        
    }
	
	
	public Map<Integer, Integer> retornaListaPermissoesPagina(RdistModel rdist) throws Exception {
			
		String SQL = " call sp_admin_buscar_permissoes_perfil(?, 0); ";
	     
		Map<Integer, Integer> lista = new HashMap<Integer, Integer>();
	    
	    try {
	        ps = conn.prepareStatement(SQL);
	        ps.setInt(1, rdist.getIdPerfil());
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
