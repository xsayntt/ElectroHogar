/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bad215.electrohogar.model;

/**
 *
 * @author Carlos
 */
public class UsuarioResumen {
    
    private int idUsuario;
    private String nombreUsuario;
    private String rol;
    private boolean estadoUsuario;
    private boolean bloqueado;
    private int intentosFallidos;
    private Integer idEmpleado;      
    private String empleado;         
    private String duiEmpleado;
    private String emailEmpleado;
 
    public UsuarioResumen() {
    }
 
    public int getIdUsuario() {
        return idUsuario;
    }
 
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
 
    public String getNombreUsuario() {
        return nombreUsuario;
    }
 
    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }
 
    public String getRol() {
        return rol;
    }
 
    public void setRol(String rol) {
        this.rol = rol;
    }
 
    public boolean isEstadoUsuario() {
        return estadoUsuario;
    }
 
    public void setEstadoUsuario(boolean estadoUsuario) {
        this.estadoUsuario = estadoUsuario;
    }
 
    public boolean isBloqueado() {
        return bloqueado;
    }
 
    public void setBloqueado(boolean bloqueado) {
        this.bloqueado = bloqueado;
    }
 
    public int getIntentosFallidos() {
        return intentosFallidos;
    }
 
    public void setIntentosFallidos(int intentosFallidos) {
        this.intentosFallidos = intentosFallidos;
    }
 
    public Integer getIdEmpleado() {
        return idEmpleado;
    }
 
    public void setIdEmpleado(Integer idEmpleado) {
        this.idEmpleado = idEmpleado;
    }
 
    public String getEmpleado() {
        return empleado;
    }
 
    public void setEmpleado(String empleado) {
        this.empleado = empleado;
    }
 
    public String getDuiEmpleado() {
        return duiEmpleado;
    }
 
    public void setDuiEmpleado(String duiEmpleado) {
        this.duiEmpleado = duiEmpleado;
    }
 
    public String getEmailEmpleado() {
        return emailEmpleado;
    }
 
    public void setEmailEmpleado(String emailEmpleado) {
        this.emailEmpleado = emailEmpleado;
    }
    
}
