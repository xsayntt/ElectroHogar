/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bad215.electrohogar.dao;

import com.bad215.electrohogar.model.Turno;
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
public class TurnoDao {
    
        public List<Turno> listarTurnos(int idSuperior) throws SQLException {
        List<Turno> lista = new ArrayList<>();
        String sql = "{CALL sp_listar_turnos(?)}";

        try (Connection cn = Conexion.getConexion();
             CallableStatement cs = cn.prepareCall(sql)) {

            cs.setInt(1, idSuperior);

            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    Turno t = new Turno();
                    t.setIdTurno(rs.getInt("ID_TURNO"));
                    t.setTipoTurno(rs.getString("TIPO_TURNO"));
                    t.setDias(rs.getString("DIAS"));
                    t.setHorario(rs.getString("HORARIO"));
                    lista.add(t);
                }
            }
        }
        return lista;
    }
    
}
