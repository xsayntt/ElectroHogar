/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bad215.electrohogar.model;

/**
 *
 * @author Carlos
 */
public enum TipoUsuario {
    Administrador(1),
    Cajero(2),
    Vendedor(3);

private final int id;

    TipoUsuario(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static TipoUsuario desdeId(int id) {
        for (TipoUsuario t : values()) {
            if (t.id == id) {
                return t;
            }
        }
        throw new IllegalArgumentException("Tipo de usuario desconocido: " + id);
    }
}


