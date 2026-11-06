/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bad215.electrohogar.utils;

import com.bad215.electrohogar.dao.SucursalDao;
import com.bad215.electrohogar.model.Sucursal;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;

/**
 *
 * @author Carlos
 */
public class Catalogos {
        private static final SucursalDao sucursalDao = new SucursalDao();

    private Catalogos() {}

    /** Llena un ComboBox con las sucursales que el usuario logueado puede ver. */
    public static void cargarSucursales(ComboBox<Sucursal> combo) {
        System.out.println("ID en sesión: " + Sesion.getIdUsuario());
        try {
            combo.setItems(FXCollections.observableArrayList(
                    sucursalDao.listarSucursales(Sesion.getIdUsuario())));
        } catch (SQLException e) {
            if ("45000".equals(e.getSQLState())) {
                Alertas.advertencia("Sucursales", e.getMessage());   // acceso denegado
            } else {
                e.printStackTrace();
                Alertas.error("Sucursales", "No se pudieron cargar las sucursales.");
            }
        }
    }
}
