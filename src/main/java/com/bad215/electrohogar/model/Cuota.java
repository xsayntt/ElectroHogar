package com.bad215.electrohogar.model;

import java.sql.Date;
/**
 *
 * @author Rene
 */
public class Cuota {
    private int numeroFactura;
    private int idCuota;
    private int numeroCuota;
    private int cantidadCuotas;
    private Date fechaVencimiento;
    private double montoCuota;
    private String dui;
    private String nit;
    private String estadoCalculado;

    public Cuota(int numeroFactura, int idCuota, int numeroCuota, int cantidadCuotas,
                 Date fechaVencimiento, double montoCuota, String dui, String nit, String estadoCalculado) {
        this.numeroFactura = numeroFactura;
        this.idCuota = idCuota;
        this.numeroCuota = numeroCuota;
        this.cantidadCuotas = cantidadCuotas;
        this.fechaVencimiento = fechaVencimiento;
        this.montoCuota = montoCuota;
        this.dui = dui;
        this.nit = nit;
        this.estadoCalculado = estadoCalculado;
    }

    // Getters
    public int getNumeroFactura() { return numeroFactura; }
    public int getIdCuota() { return idCuota; }
    public int getNumeroCuota() { return numeroCuota; }
    public int getCantidadCuotas() { return cantidadCuotas; }
    public Date getFechaVencimiento() { return fechaVencimiento; }
    public double getMontoCuota() { return montoCuota; }
    public String getDui() { return dui; }
    public String getNit() { return nit; }
    public String getEstadoCalculado() { return estadoCalculado; }
}