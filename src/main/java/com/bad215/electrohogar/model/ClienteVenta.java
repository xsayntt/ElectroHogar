package com.bad215.electrohogar.model;

/**
 *
 * @author Say
 */
public class ClienteVenta {
    private int idCliente;
    private String nombreCliente;
    private String tipoCliente;
 
    public int getIdCliente() {
        return idCliente;
    }
 
    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
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
 
    @Override
    public String toString() {
        return idCliente + " - " + nombreCliente;
    }

}
