/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bad215.electrohogar.utils;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

/**
 *
 * @author Carlos
 */
public class Alertas {
    public static void error(String titulo, String mensaje) {
        mostrar(AlertType.ERROR, titulo, mensaje);
    }

    public static void advertencia(String titulo, String mensaje) {
        mostrar(AlertType.WARNING, titulo, mensaje);
    }

    public static void info(String titulo, String mensaje) {
        mostrar(AlertType.INFORMATION, titulo, mensaje);
    }

    public static boolean confirmar(String titulo, String mensaje) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        Optional<ButtonType> resultado = alert.showAndWait();
        return resultado.isPresent() && resultado.get() == ButtonType.OK;
    }

    private static void mostrar(AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);   // quita el encabezado, queda más limpio
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}

