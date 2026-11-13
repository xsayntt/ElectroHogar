/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bad215.electrohogar.dao;

import com.bad215.electrohogar.model.Cuota;
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
public class CuotaDao {
    
    public List<Cuota> listar(int idUsuario, String clienteId, String numeroFactura,
                              String dui, String nit, String estado)
            throws SQLException, IllegalArgumentException {

        Integer idCliente = parseEnteroOpcional(clienteId, "ID de cliente");
        Integer numFactura = parseEnteroOpcional(numeroFactura, "número de factura");

        String sql = "{CALL sp_listar_cuotas(?, ?, ?, ?, ?, ?)}";
        List<Cuota> cuota = new ArrayList<>();

        try (Connection conn = Conexion.getConexion();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, idUsuario);
            stmt.setObject(2, idCliente, Types.INTEGER);
            stmt.setObject(3, numFactura, Types.INTEGER);
            stmt.setString(4, vacioANull(dui));
            stmt.setString(5, vacioANull(nit));
            stmt.setString(6, (estado == null || "Todos".equals(estado)) ? null : estado);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    cuota.add(new Cuota(
                            rs.getInt("NUMERO_FACTURA"),
                            rs.getInt("ID_CUOTA"),
                            rs.getInt("NUMERO_CUOTA"),
                            rs.getInt("CANTIDAD_CUOTAS"),
                            rs.getDate("FECHA_VENCIMIENTO"),
                            rs.getDouble("MONTO_CUOTA"),
                            rs.getString("DUI"),
                            rs.getString("NIT"),
                            rs.getString("ESTADO_CALCULADO")
                    ));
                }
            }
        }
        return cuota;
    }

    public void pagar(int idUsuario, Cuota cuota, int idMetodoPago)
            throws SQLException, IllegalArgumentException {

        if (cuota == null) {
            throw new IllegalArgumentException("Debe seleccionar una cuota.");
        }
        if ("Pagada".equalsIgnoreCase(cuota.getEstadoCalculado())) {
            throw new IllegalArgumentException("La cuota seleccionada ya fue pagada.");
        }
        if (idMetodoPago <= 0) {
            throw new IllegalArgumentException("Debe seleccionar un método de pago.");
        }

        String sql = "{CALL sp_pagar_cuota(?, ?, ?)}";

        try (Connection conn = Conexion.getConexion();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, idUsuario);
            stmt.setInt(2, cuota.getIdCuota());
            stmt.setInt(3, idMetodoPago);
            stmt.executeUpdate();
        }
    }

    // ---- validaciones auxiliares ----

    private Integer parseEnteroOpcional(String valor, String campo) {
        if (valor == null || valor.isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El campo " + campo + " debe ser numérico.");
        }
    }

    private String vacioANull(String valor) {
        return (valor == null || valor.isEmpty()) ? null : valor;
    }
    
}
