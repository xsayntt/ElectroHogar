/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bad215.electrohogar.dao;

import com.bad215.electrohogar.model.Supervisor;
import com.bad215.electrohogar.utils.Conexion;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Carlos
 */
public class SupervisorDao {
    
    public List<Supervisor> listarSupervisores(int idSuperior, Integer idUsuarioExcluir) throws SQLException {
        List<Supervisor> lista = new ArrayList<>();
        String sql = "{CALL sp_listar_supervisores(?, ?)}";

        try (Connection cn = Conexion.getConexion();
             CallableStatement cs = cn.prepareCall(sql)) {

            cs.setInt(1, idSuperior);
            if (idUsuarioExcluir == null) {
                cs.setNull(2, Types.INTEGER);
            } else {
                cs.setInt(2, idUsuarioExcluir);
            }

            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    Supervisor s = new Supervisor();
                    s.setIdEmpleado(rs.getInt("ID_EMPLEADO"));
                    s.setNombreCompleto(rs.getString("NOMBRE_COMPLETO"));
                    s.setRol(rs.getString("ROL"));
                    lista.add(s);
                }
            }
        }
        return lista;
    }
    
}
