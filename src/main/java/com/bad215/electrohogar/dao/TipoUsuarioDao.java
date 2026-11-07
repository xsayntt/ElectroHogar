/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bad215.electrohogar.dao;

import com.bad215.electrohogar.model.TipoUsuario;
import com.bad215.electrohogar.utils.Conexion;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Carlos
 */
public class TipoUsuarioDao {
    
        public List<TipoUsuario> buscarRol(int idSuperior, String texto) throws SQLException {

        List<TipoUsuario> lista = new ArrayList<>();
        String sql = "{CALL sp_buscar_rol(?, ?)}";

        try (Connection cn = Conexion.getConexion();
             CallableStatement cs = cn.prepareCall(sql)) {

            cs.setInt(1, idSuperior);
            cs.setString(2, texto);

            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    TipoUsuario tipo = new TipoUsuario();
                    tipo.setIdTipo(rs.getInt("ID_TIPO"));
                    tipo.setNombreTipo(rs.getString("NOMBRE_TIPO"));
                    tipo.setCantUsuarios(rs.getInt("USUARIOS"));
                    lista.add(tipo);
                }
            }
        }
        return lista;
    }
    
}
