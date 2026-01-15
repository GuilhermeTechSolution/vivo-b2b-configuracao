package br.com.iatapp.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * 
 * @author ottap
 *
 */
public class MysqlConfig {
	
	/**
	 * Metodo realiza a conexão com o Banco de Dados
	 * @return
	 * @throws Exception
	 */
	public static Connection getConnection() throws Exception {
		
		String mysqlClass = "com.mysql.cj.jdbc.Driver";
		String mysqlUser = "devops";
		String mysqlPass = "zt1&H%^FE*J&";
	    String mysqUrl = "jdbc:mysql://"+ IatConstants.MYSQL_HOST + ":33106/ " + IatConstants.MYSQL_DATABSE + "?useTimezone=true&serverTimezone=UTC&useSSL=false";
		
		Class.forName(mysqlClass);
        return DriverManager.getConnection(mysqUrl, mysqlUser, mysqlPass);
    }

	/**
	 * 
	 * @param conn
	 * @param stmt
	 * @param rs
	 * @throws Exception
	 */
    public static void closeConnection(Connection conn, Statement stmt, ResultSet rs) throws Exception {
        close(conn, stmt, rs);
    }

    /**
     * 
     * @param conn
     * @param stmt
     * @throws Exception
     */
    public static void closeConnection(Connection conn, Statement stmt) throws Exception {
        close(conn, stmt, null);
    }

    /**
     * 
     * @param conn
     * @throws Exception
     */
    public static void closeConnection(Connection conn) throws Exception {
        close(conn, null, null);
    }

    /**
     * 
     * @param conn
     * @param stmt
     * @param rs
     * @throws Exception
     */
    private static void close(Connection conn, Statement stmt, ResultSet rs) throws Exception {        
    	
    	if (rs != null) {
    		rs.close( );
    	}
    	
        if (stmt != null) {
        	stmt.close( );
        }
        
        if (conn != null) {
        	conn.close( );
        }        
    }

}
