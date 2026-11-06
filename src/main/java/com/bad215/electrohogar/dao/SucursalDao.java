/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bad215.electrohogar.dao;

import com.bad215.electrohogar.model.Sucursal;
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
public class SucursalDao {
    
    public List<Sucursal> listarSucursales(int idSuperior) throws SQLException {
        String sql = "{CALL sp_listar_sucursales(?)}";
        List<Sucursal> lista = new ArrayList<>();

        try (Connection con = new Conexion().getConexion();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, idSuperior);

            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    Sucursal s = new Sucursal();
                    s.setIdSucursal(rs.getInt("ID_SUCURSAL"));
                    s.setNombreSucursal(rs.getString("NOMBRE_SUCURSAL"));
                    lista.add(s);
                }
            }
        }
        return lista;
    }
}

