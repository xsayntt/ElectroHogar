package com.bad215.electrohogar.dao;
import com.bad215.electrohogar.model.Factura;
import com.bad215.electrohogar.utils.Conexion;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

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

}
