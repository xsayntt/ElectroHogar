package com.bad215.electrohogar.model;
import java.time.LocalDateTime;
/**
 *
 * @author Say
 */
public class Factura {
    private int numeroFactura;
    private String nombreCliente;
    private String tipoCliente;
    private String empleadoRegistro;
    private LocalDateTime fechaFactura;
    private int estadoFactura;
    private String metodoPago;
    
    public int getNumeroFactura() {
        return numeroFactura;
    }
 
    public void setNumeroFactura(int numeroFactura) {
        this.numeroFactura = numeroFactura;
    }
 
    public String getNombreCliente() {
        return nombreCliente;
    }
 
    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }
 
    public String getTipoCliente() {
        return tipoCliente;
    }
 
    public void setTipoCliente(String tipoCliente) {
        this.tipoCliente = tipoCliente;
    }
 
    public String getEmpleadoRegistro() {
        return empleadoRegistro;
    }
 
    public void setEmpleadoRegistro(String empleadoRegistro) {
        this.empleadoRegistro = empleadoRegistro;
    }
 
    public LocalDateTime getFechaFactura() {
        return fechaFactura;
    }
 
    public void setFechaFactura(LocalDateTime fechaFactura) {
        this.fechaFactura = fechaFactura;
    }
 
    public int getEstadoFactura() {
        return estadoFactura;
    }
 
    public void setEstadoFactura(int estadoFactura) {
        this.estadoFactura = estadoFactura;
    }
 
    public String getMetodoPago() {
        return metodoPago;
    }
 
    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }
 
    public boolean isVigente() {
        return estadoFactura == 1;
    }

}
