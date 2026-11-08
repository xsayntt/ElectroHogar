package com.bad215.electrohogar.model;

/**
 *
 * @author Say
 */
public class LineaVenta {
    private int idProducto;
    private String nombreProducto;
    private int cantidad;
    private double precioUnitario;

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
    
    // multiplicación con redondeo a 2 decimales para evitar imprecisiones
    public double getSubtotal() {
        return Math.round(precioUnitario * cantidad * 100.0) / 100.0;
    }
}