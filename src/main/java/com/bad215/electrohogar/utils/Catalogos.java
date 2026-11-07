/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bad215.electrohogar.utils;

import com.bad215.electrohogar.dao.SucursalDao;
import com.bad215.electrohogar.dao.SupervisorDao;
import com.bad215.electrohogar.dao.TipoUsuarioDao;
import com.bad215.electrohogar.dao.TurnoDao;
import com.bad215.electrohogar.model.Sucursal;
import com.bad215.electrohogar.model.Supervisor;
import com.bad215.electrohogar.model.TipoUsuario;
import com.bad215.electrohogar.model.Turno;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;

/**
 *
 * @author Carlos
 */
public class Catalogos {
        private static final SucursalDao sucursalDao = new SucursalDao();
        private static final TipoUsuarioDao tipoUsuarioDao = new TipoUsuarioDao();
        private static final TurnoDao turnoDao = new TurnoDao();
        private static final SupervisorDao supervisorDao = new SupervisorDao();


        

    private Catalogos() {}

    public static void cargarSucursales(ComboBox<Sucursal> combo) {
        System.out.println("ID en sesión: " + Sesion.getIdUsuario());
        try {
            combo.setItems(FXCollections.observableArrayList(sucursalDao.listarSucursales(Sesion.getIdUsuario())));
        } catch (SQLException e) {
            if ("45000".equals(e.getSQLState())) {
                Alertas.advertencia("Sucursales", e.getMessage());
            } else {
                e.printStackTrace();
                Alertas.error("Sucursales", "No se pudieron cargar las sucursales.");
            }
        }
    }
    
     public static void cargarRoles(ComboBox<TipoUsuario> combo) {
        try {
            combo.setItems(FXCollections.observableArrayList( tipoUsuarioDao.buscarRol(Sesion.getIdUsuario(), "")));
        } catch (SQLException e) {
            if ("45000".equals(e.getSQLState())) {
                Alertas.advertencia("Roles", e.getMessage());   // acceso denegado
            } else {
                e.printStackTrace();
                Alertas.error("Roles", "No se pudieron cargar los roles.");
            }
        }
}
     
     public static void cargarTurnos(ComboBox<Turno> combo) {
    try {
        combo.setItems(FXCollections.observableArrayList( turnoDao.listarTurnos(Sesion.getIdUsuario())));
    } catch (SQLException e) {
        if ("45000".equals(e.getSQLState())) {
            Alertas.advertencia("Turnos", e.getMessage());   // acceso denegado
        } else {
            e.printStackTrace();
            Alertas.error("Turnos", "No se pudieron cargar los turnos.");
        }
    }
}
     
     public static void cargarSupervisores(ComboBox<Supervisor> combo, Integer idUsuarioExcluir) {
    try {
        combo.setItems(FXCollections.observableArrayList(
                supervisorDao.listarSupervisores(Sesion.getIdUsuario(), idUsuarioExcluir)));
    } catch (SQLException e) {
        if ("45000".equals(e.getSQLState())) {
            Alertas.advertencia("Supervisores", e.getMessage());   // acceso denegado
        } else {
            e.printStackTrace();
            Alertas.error("Supervisores", "No se pudieron cargar los supervisores.");
        }
    }
}
}
