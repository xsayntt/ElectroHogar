/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bad215.electrohogar.utils;

import com.bad215.electrohogar.model.Usuario;

/**
 *
 * @author Carlos
 */
public class Sesion {
    private static Usuario usuario;   

    private Sesion() {}               

    public static void iniciar(Usuario u) { usuario = u; }
    public static void cerrar() { usuario = null; }
    public static Usuario getUsuario() { return usuario; }

    public static int getIdUsuario() {
        if (usuario == null) {
            throw new IllegalStateException("No hay sesión iniciada.");
        }
        return usuario.getIdUsuario();
    }
}
