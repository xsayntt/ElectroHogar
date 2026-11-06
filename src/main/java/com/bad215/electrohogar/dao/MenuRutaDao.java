/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bad215.electrohogar.dao;

import com.bad215.electrohogar.model.MenuRuta;
import com.bad215.electrohogar.model.Privilegio;
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
public class MenuRutaDao {
    
     public List<MenuRuta> menuPorUsuario(int idUsuario) throws SQLException {
        List<MenuRuta> menu = new ArrayList<>();

        try (Connection cn = Conexion.getConexion();
             CallableStatement cs = cn.prepareCall("{CALL sp_menu_por_usuario(?)}")) {

            cs.setInt(1, idUsuario);

            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    menu.add(mapear(rs));
                }
            }
        }
        return menu;
    }

    private MenuRuta mapear(ResultSet rs) throws SQLException {
        MenuRuta m = new MenuRuta();
        m.setIdMenu(rs.getInt("ID_MENU"));

        
        m.setMenIdMenu(rs.getInt("MEN_ID_MENU"));

        m.setNombreOpcion(rs.getString("NOMBRE_OPCION"));
        m.setRuta(rs.getString("RUTA"));
        m.setOrden(rs.getInt("ORDEN"));

        Privilegio p = new Privilegio();
        p.setIdMenu(m.getIdMenu());
        p.setPuedeCrear(rs.getInt("PUEDE_CREAR") == 1);
        p.setPuedeLeer(rs.getInt("PUEDE_LEER") == 1);
        p.setPuedeActualizar(rs.getInt("PUEDE_ACTUALIZAR") == 1);
        p.setPuedeEliminar(rs.getInt("PUEDE_ELIMINAR") == 1);
        m.setPrivilegio(p);

        return m;
    }
    
}
