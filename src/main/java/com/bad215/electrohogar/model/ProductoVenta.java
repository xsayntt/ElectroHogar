package com.bad215.electrohogar.model;

/**
 *
 * @author Say
 */
public class ProductoVenta {
    private int idProducto;
    private String nombreProducto;
    private String nombreCategoria;
    private double precio;
    private double precioVigente;
    private int stock;

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

    public String getNombreCategoria() {
        return nombreCategoria;
    }

    public void setNombreCategoria(String nombreCategoria) {
        this.nombreCategoria = nombreCategoria;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public double getPrecioVigente() {
        return precioVigente;
    }

    public void setPrecioVigente(double precioVigente) {
        this.precioVigente = precioVigente;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}