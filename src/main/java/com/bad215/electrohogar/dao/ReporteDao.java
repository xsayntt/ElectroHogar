package com.bad215.electrohogar.dao;

import com.bad215.electrohogar.model.EmpleadoReporte;
import com.bad215.electrohogar.model.ReporteFila;
import com.bad215.electrohogar.model.ReporteResultado;
import com.bad215.electrohogar.model.TipoReporteVentas;
import com.bad215.electrohogar.utils.Conexion;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReporteDao {

    public ReporteResultado ventas(int idUsuario, LocalDate desde, LocalDate hasta,
            TipoReporteVentas tipo) throws SQLException {
        String sql = "{CALL " + tipo.getProcedimiento() + "(?, ?, ?)}";
        try (Connection conn = Conexion.getConexion();
             CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setInt(1, idUsuario);
            stmt.setDate(2, Date.valueOf(desde));
            stmt.setDate(3, Date.valueOf(hasta));
            try (ResultSet rs = stmt.executeQuery()) {
                return mapearResultado(rs);
            }
        }
    }

    public ReporteResultado anulaciones(int idUsuario, LocalDate desde, LocalDate hasta,
            Integer idEmpleado) throws SQLException {
        try (Connection conn = Conexion.getConexion();
             CallableStatement stmt = conn.prepareCall("{CALL sp_historial_anulaciones_empleado(?, ?, ?, ?)}")) {
            stmt.setInt(1, idUsuario);
            stmt.setDate(2, Date.valueOf(desde));
            stmt.setDate(3, Date.valueOf(hasta));
            if (idEmpleado == null) {
                stmt.setNull(4, Types.INTEGER);
            } else {
                stmt.setInt(4, idEmpleado);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                return mapearResultado(rs);
            }
        }
    }

    public List<EmpleadoReporte> listarEmpleados(int idUsuario) throws SQLException {
        List<EmpleadoReporte> empleados = new ArrayList<>();
        try (Connection conn = Conexion.getConexion();
             CallableStatement stmt = conn.prepareCall("{CALL sp_listar_empleados_reportes(?)}")) {
            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    empleados.add(new EmpleadoReporte(
                            rs.getInt("ID_EMPLEADO"), rs.getString("NOMBRE_COMPLETO")));
                }
            }
        }
        return empleados;
    }

    private ReporteResultado mapearResultado(ResultSet rs) throws SQLException {
        ReporteResultado resultado = new ReporteResultado();
        ResultSetMetaData metadata = rs.getMetaData();
        int cantidadColumnas = metadata.getColumnCount();
        for (int i = 1; i <= cantidadColumnas; i++) {
            resultado.getColumnas().add(metadata.getColumnLabel(i));
        }
        while (rs.next()) {
            ReporteFila fila = new ReporteFila();
            for (int i = 1; i <= cantidadColumnas; i++) {
                Object valor = rs.getObject(i);
                fila.agregar(metadata.getColumnLabel(i), valor == null ? "" : valor.toString());
            }
            resultado.getFilas().add(fila);
        }
        return resultado;
    }
}
