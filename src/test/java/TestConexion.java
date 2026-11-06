
import com.bad215.electrohogar.utils.Conexion;
import java.sql.Connection;
import java.sql.SQLException;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Carlos
 */
public class TestConexion {
    
    public static void main(String[] args) {
        try (Connection con = Conexion.getConexion()) {
    System.out.println("Conectado: " + !con.isClosed());
} catch (SQLException e) {
    e.printStackTrace();
}
    }
    
}
