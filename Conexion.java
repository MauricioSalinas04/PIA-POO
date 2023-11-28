/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import com.mysql.jdbc.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author nicol
 */
public class Conexion {
    
    public static Connection getConexion(){
        
        String url = "jdbc:mysql://localhost:3306/registros";
        String user = "root";
        String password = "";
        
        try{
            Connection con = (Connection) DriverManager.getConnection(url,user,password);
            return con;
        } catch(SQLException e){
            System.out.println(e.toString());
            return null;
        }
        
    }
}
