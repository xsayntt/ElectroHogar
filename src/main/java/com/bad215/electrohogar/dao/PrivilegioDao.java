/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bad215.electrohogar.dao;

import com.bad215.electrohogar.utils.Conexion;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author Carlos
 */
public class PrivilegioDao {
    
        // Operaciones: ajusta las letras a lo que acepte fn_tiene_permiso
    public static final char CREAR = 'C';
    public static final char LEER = 'R';
    public static final char ACTUALIZAR = 'U';
    public static final char ELIMINAR = 'D';

    private static final String SQLSTATE_ACCESO_DENEGADO = "45000";


    public boolean tienePermiso(int idUsuario, String ruta, char operacion) throws SQLException {
        String sql = "{CALL sp_comprobar_permiso(?, ?, ?)}";

        try (Connection con = Conexion.getConexion();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, idUsuario);
            cs.setString(2, ruta);
            cs.setString(3, String.valueOf(operacion));
            cs.execute();
            return true;

        } catch (SQLException e) {
            if (SQLSTATE_ACCESO_DENEGADO.equals(e.getSQLState())) {
                return false;
            }
            throw e;
        }
    }

    public void comprobar(int idUsuario, String ruta, char operacion) throws SQLException {
        String sql = "{CALL sp_comprobar_permiso(?, ?, ?)}";

        try (Connection con = Conexion.getConexion();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, idUsuario);
            cs.setString(2, ruta);
            cs.setString(3, String.valueOf(operacion));
            cs.execute();

        } catch (SQLException e) {
            if (SQLSTATE_ACCESO_DENEGADO.equals(e.getSQLState())) {
                throw new SecurityException(e.getMessage(), e);
            }
            throw e;
        }
    }
    
}
