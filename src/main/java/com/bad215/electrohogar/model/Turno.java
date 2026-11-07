/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bad215.electrohogar.model;

/**
 *
 * @author Carlos
 */
public enum Turno {
    MATUTINO(1, "Lunes a Viernes", "08:00 AM - 04:00 PM"),
    VESPERTINO(2, "Lunes a Viernes", "01:00 PM - 09:00 PM");

    private final int idTurno;
    private final String dias;
    private final String horario;

    Turno(int idTurno, String dias, String horario) {
        this.idTurno = idTurno;
        this.dias = dias;
        this.horario = horario;
    }

    public int getIdTurno() {
        return idTurno;
    }

    public String getDias() {
        return dias;
    }

    public String getHorario() {
        return horario;
    }
    
    public static Turno fromId(int id) {
    for (Turno turno : Turno.values()) {
        if (turno.getIdTurno() == id) {
            return turno;
        }
    }

    throw new IllegalArgumentException("ID de turno no válido: " + id);
}

}
