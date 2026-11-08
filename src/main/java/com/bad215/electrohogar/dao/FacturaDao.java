package com.bad215.electrohogar.dao;

import com.bad215.electrohogar.model.Factura;
import com.bad215.electrohogar.utils.Conexion;
import com.bad215.electrohogar.model.ClienteVenta;
import com.bad215.electrohogar.model.LineaVenta;
import com.bad215.electrohogar.model.MetodoPago;
import com.bad215.electrohogar.model.ProductoVenta;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.sql.Types;

/**
 *
 * @author Say
 */
public class FacturaDao {
    
    // manda a llamar al sp_listar_facturas
        public List<Factura> listarFacturas(int idUsuario, String texto) throws SQLException {
        String sql = "{CALL sp_listar_facturas(?, ?)}";
        List<Factura> lista = new ArrayList<>();
 
        try (Connection con = Conexion.getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
 
            cs.setInt(1, idUsuario);
            cs.setString(2, texto);
 
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }
        
    // mapear sirve para llenar la tabla
    private Factura mapear(ResultSet rs) throws SQLException {
        Factura f = new Factura();
        f.setNumeroFactura(rs.getInt("NUMERO_FACTURA"));
        f.setNombreCliente(rs.getString("NOMBRE_CLIENTE"));
        f.setTipoCliente(rs.getString("TIPO_CLIENTE"));
        f.setEmpleadoRegistro(rs.getString("EMPLEADO_REGISTRO"));
 
        Timestamp fecha = rs.getTimestamp("FECHA_FACTURA");
        f.setFechaFactura(fecha != null ? fecha.toLocalDateTime() : null);
 
        f.setEstadoFactura(rs.getInt("ESTADO_FACTURA"));
        f.setMetodoPago(rs.getString("METODO_PAGO"));
        return f;
    }
    
    public List<ClienteVenta> buscarClientesEnVenta(int idUsuario, String texto) throws SQLException {
        String sql = "{CALL sp_buscar_cliente_en_venta(?, ?)}";
        List<ClienteVenta> lista = new ArrayList<>();
 
        try (Connection con = Conexion.getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
 
            cs.setInt(1, idUsuario);
            cs.setString(2, texto);
 
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    ClienteVenta c = new ClienteVenta();
                    c.setIdCliente(rs.getInt("ID_CLIENTE"));
                    c.setNombreCliente(rs.getString("NOMBRE_CLIENTE"));
                    c.setTipoCliente(rs.getString("TIPO_CLIENTE"));
                    lista.add(c);
                }
            }
        }
        return lista;
    }

    public List<MetodoPago> listarMetodosPago(int idUsuario) throws SQLException {
        String sql = "{CALL sp_listar_metodo_pagos_pago(?)}";
        List<MetodoPago> lista = new ArrayList<>();
 
        try (Connection con = Conexion.getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
 
            cs.setInt(1, idUsuario);
 
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    MetodoPago m = new MetodoPago();
                    m.setIdMetodo(rs.getInt("ID_METODO"));
                    m.setTipoPago(rs.getString("TIPO_PAGO"));
                    lista.add(m);
                }
            }
        }
        return lista;
    }
    
    public List<ProductoVenta> buscarProductosEnVenta(int idUsuario, String texto) throws SQLException {
        String sql = "{CALL sp_buscar_producto_en_venta(?, ?)}";
        List<ProductoVenta> lista = new ArrayList<>();
 
        try (Connection con = Conexion.getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
 
            cs.setInt(1, idUsuario);
            cs.setString(2, texto);
 
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    ProductoVenta p = new ProductoVenta();
                    p.setIdProducto(rs.getInt("ID_PRODUCTO"));
                    p.setNombreProducto(rs.getString("NOMBRE_PRODUCTO"));
                    p.setNombreCategoria(rs.getString("NOMBRE_CATEGORIA"));
                    p.setPrecio(rs.getDouble("PRECIO"));
                    p.setPrecioVigente(rs.getDouble("PRECIO_VIGENTE"));
                    p.setStock(rs.getInt("STOCK"));
                    lista.add(p);
                }
            }
        }
        return lista;
    }
    
        public int registrarVenta(int idUsuario, int idCliente, int idMetodoPago, int cuotas,
                              List<LineaVenta> lineas) throws SQLException {
        String sql = "{CALL sp_registrar_venta(?, ?, ?, ?, ?, ?)}";
 
        try (Connection con = Conexion.getConexion();
             CallableStatement cs = con.prepareCall(sql)) {
 
            cs.setInt(1, idUsuario);
            cs.setInt(2, idCliente);
            cs.setInt(3, idMetodoPago);
            cs.setInt(4, cuotas);
            cs.setString(5, aJson(lineas));
            cs.registerOutParameter(6, Types.INTEGER);
 
            cs.execute();
            return cs.getInt(6);
        }
    }

        private String aJson(List<LineaVenta> lineas) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < lineas.size(); i++) {
            if (i > 0) {
                json.append(',');
            }
            json.append("{\"id_producto\":").append(lineas.get(i).getIdProducto())
                .append(",\"cantidad\":").append(lineas.get(i).getCantidad())
                .append('}');
        }
        return json.append(']').toString();
    }

}
