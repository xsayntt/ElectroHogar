/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bad215.electrohogar.model;

/**
 *
 * @author Carlos
 */
public class MenuRuta {
    private int idMenu;
    private int menIdMenu;
    private String nombreOpcion;
    private String ruta;
    private int orden;
    private Privilegio privilegio;

    
    
    public int getIdMenu() {
        return idMenu;
    }

    public void setIdMenu(int idMenu) {
        this.idMenu = idMenu;
    }

    public String getNombreOpcion() {
        return nombreOpcion;
    }

    public void setNombreOpcion(String nombreOpcion) {
        this.nombreOpcion = nombreOpcion;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public int getOrden() {
        return orden;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }

    public int getMenIdMenu() {
        return menIdMenu;
    }

    public void setMenIdMenu(int menIdMenu) {
        this.menIdMenu = menIdMenu;
    }

    public Privilegio getPrivilegio() {
        return privilegio;
    }

    public void setPrivilegio(Privilegio privilegio) {
        this.privilegio = privilegio;
    }
    
    public boolean tieneRuta() {
    return ruta != null && !ruta.isBlank();
    }  
}
