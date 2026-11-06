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
public class Vendedor {
    private int idEmpleado;
    private BigDecimal metaMensual;
    private BigDecimal porcentajeComision;

    public int getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public BigDecimal getMetaMensual() {
        return metaMensual;
    }

    public void setMetaMensual(BigDecimal metaMensual) {
        this.metaMensual = metaMensual;
    }

    public BigDecimal getPorcentajeComision() {
        return porcentajeComision;
    }

    public void setPorcentajeComision(BigDecimal porcentajeComision) {
        this.porcentajeComision = porcentajeComision;
    }
    
    
}
