package com.bad215.electrohogar.model;

/**
 *
 * @author Say
 */
public class MetodoPago {
    private int idMetodo;
    private String tipoPago;
 
    public int getIdMetodo() {
        return idMetodo;
    }
 
    public void setIdMetodo(int idMetodo) {
        this.idMetodo = idMetodo;
    }
 
    public String getTipoPago() {
        return tipoPago;
    }
 
    public void setTipoPago(String tipoPago) {
        this.tipoPago = tipoPago;
    }
 
    @Override
    public String toString() {
        return tipoPago;
    }

}
