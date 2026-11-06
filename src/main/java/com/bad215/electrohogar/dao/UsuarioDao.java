/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bad215.electrohogar.dao;

import com.bad215.electrohogar.model.Cajero;
import com.bad215.electrohogar.model.Empleado;
import com.bad215.electrohogar.model.TipoUsuario;
import com.bad215.electrohogar.model.Usuario;
import com.bad215.electrohogar.model.Vendedor;
import com.bad215.electrohogar.utils.Conexion;
import com.bad215.electrohogar.utils.Contrasena;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 *
 * @author Carlos
 */
public class UsuarioDao {
    
     public Usuario autenticar(String nombreUsuario, String password) throws SQLException {
    String sql = "{CALL sp_obtener_login(?)}";

    try (Connection con = Conexion.getConexion();
         CallableStatement cs = con.prepareCall(sql)) {

        cs.setString(1, nombreUsuario);

        try (ResultSet rs = cs.executeQuery()) {
            if (!rs.next()) {
                return null; // el usuario no existe
            }

            String estado = rs.getString("ESTADO_LOGIN");
            if ("DADO_DE_BAJA".equals(estado)) {
                throw new IllegalStateException("Tu usuario fue dado de baja.");
            }
            if ("BLOQUEADO".equals(estado)) {
                throw new IllegalStateException("Tu usuario está bloqueado. Contacta al administrador");
            }

            String hash = rs.getString("CONTRASENA");
            int idUsuario = rs.getInt("ID_USUARIO");
            if (hash == null || !Contrasena.verificarContrasenia(password, hash)) {
                boolean quedoBloqueado = registrarLoginFallido(idUsuario);
                if (quedoBloqueado) {
                    throw new IllegalStateException("Tu usuario fue bloqueado por demasiados intentos fallidos.");
                }
                return null;
            }

            Usuario u = new Usuario();
            u.setIdUsuario(rs.getInt("ID_USUARIO"));
            u.setIdTipo(rs.getInt("ID_TIPO"));
            u.setNombreUsuario(rs.getString("NOMBRE_TIPO"));
            u.setNombreUsuario(nombreUsuario);
            
            registrarLoginExitoso(u.getIdUsuario());
            return u;  
        }
    }
}
     public void registrarLoginExitoso(int idUsuario) throws SQLException {
    String sql = "{CALL sp_login_exitoso(?)}";

    try (Connection con = Conexion.getConexion();
         CallableStatement cs = con.prepareCall(sql)) {

        cs.setInt(1, idUsuario);
        cs.executeUpdate();
    }
}
     
     public boolean registrarLoginFallido(int idUsuario) throws SQLException {
         String sql = "{CALL sp_login_fallido(?, ?)}";
         
         try (Connection con = Conexion.getConexion();
                 CallableStatement cs = con.prepareCall(sql)) {
             cs.setInt(1, idUsuario);
             cs.registerOutParameter(2, Types.TINYINT);
             cs.execute();
             
             return cs.getInt(2) == 1;   // true si este intento dejó al usuario bloqueado
         }
         
         
    }
     
    public void crear(int idCreador, Usuario usuario, Empleado empleado, Vendedor vendedor, Cajero cajero) throws SQLException {
 
        if (vendedor != null && cajero != null) {
            throw new IllegalArgumentException("Un empleado no puede ser vendedor y cajero a la vez");
        }

        try (Connection con = Conexion.getConexion();
             CallableStatement cs = con.prepareCall("{CALL sp_crear_usuario(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}")) {

            cs.setInt(1, idCreador);
            cs.setInt(2, usuario.getIdTipo());
            cs.setString(3, usuario.getNombreUsuario());
            cs.setString(4, usuario.getContrasena());
            cs.setInt(5, empleado.getIdSucursal());
            cs.setString(6, empleado.getNombres());
            cs.setString(7, empleado.getApellidos());
            cs.setString(8, empleado.getDuiEmpleado());
            cs.setString(9, empleado.getTelefonoEmpleado());
            cs.setDate(10, Date.valueOf(empleado.getFechaContratacion()));
            cs.setString(11, empleado.getDireccionPersonal());
            cs.setString(12, empleado.getEmailEmpleado());

            // supervisor opcional: 0 se interpreta como "sin supervisor"
            if (empleado.getIdSupervisor() > 0) {
                cs.setInt(13, empleado.getIdSupervisor());
            } else {
                cs.setNull(13, Types.INTEGER);
            }

            // subtipo, turno, caja, meta y comisión
            if (vendedor != null) {
                cs.setString(14, "V");
                cs.setNull(15, Types.INTEGER);
                cs.setNull(16, Types.INTEGER);
                cs.setBigDecimal(17, vendedor.getMetaMensual());
                cs.setBigDecimal(18, vendedor.getPorcentajeComision());
            } else if (cajero != null) {
                cs.setString(14, "C");
                cs.setInt(15, cajero.getIdTurno());
                cs.setInt(16, cajero.getNumeroCaja());
                cs.setNull(17, Types.DECIMAL);
                cs.setNull(18, Types.DECIMAL);
            } else {
                cs.setNull(14, Types.CHAR);
                cs.setNull(15, Types.INTEGER);
                cs.setNull(16, Types.INTEGER);
                cs.setNull(17, Types.DECIMAL);
                cs.setNull(18, Types.DECIMAL);
            }

            cs.registerOutParameter(19, Types.INTEGER);
            cs.registerOutParameter(20, Types.INTEGER);

            cs.execute();

            int idUsuario = cs.getInt(19);
            int idEmpleado = cs.getInt(20);

            usuario.setIdUsuario(idUsuario);
            empleado.setIdUsuario(idUsuario);
            empleado.setIdEmpleado(idEmpleado);
            if (vendedor != null) vendedor.setIdEmpleado(idEmpleado);
            if (cajero != null) cajero.setIdEmpleado(idEmpleado);
        }
    }
}

