package com.bad215.electrohogar.model;

public class EmpleadoReporte {
    private final Integer idEmpleado;
    private final String nombreCompleto;

    public EmpleadoReporte(Integer idEmpleado, String nombreCompleto) {
        this.idEmpleado = idEmpleado;
        this.nombreCompleto = nombreCompleto;
    }

    public Integer getIdEmpleado() {
        return idEmpleado;
    }

    @Override
    public String toString() {
        return nombreCompleto;
    }
}
