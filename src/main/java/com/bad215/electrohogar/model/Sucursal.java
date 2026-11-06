/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bad215.electrohogar.model;

import java.math.BigDecimal;

/**
 *
 * @author Carlos
 */
public class Sucursal {
    private int idSucursal;
    private String nombreSucursal;
    private String direccionSucursal;
    private BigDecimal presupuesto;
    private BigDecimal limiteCreditoPersonal;

    public int getIdSucursal() {
        return idSucursal;
    }

    public void setIdSucursal(int idSucursal) {
        this.idSucursal = idSucursal;
    }

    public String getNombreSucursal() {
        return nombreSucursal;
    }

    public void setNombreSucursal(String nombreSucursal) {
        this.nombreSucursal = nombreSucursal;
    }

    public String getDireccionSucursal() {
        return direccionSucursal;
    }

    public void setDireccionSucursal(String direccionSucursal) {
        this.direccionSucursal = direccionSucursal;
    }

    public BigDecimal getPresupuesto() {
        return presupuesto;
    }

    public void setPresupuesto(BigDecimal presupuesto) {
        this.presupuesto = presupuesto;
    }

    public BigDecimal getLimiteCreditoPersonal() {
        return limiteCreditoPersonal;
    }

    public void setLimiteCreditoPersonal(BigDecimal limiteCreditoPersonal) {
        this.limiteCreditoPersonal = limiteCreditoPersonal;
    }
    
    
    
}
