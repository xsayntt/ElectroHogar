/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bad215.electrohogar.utils;

import java.math.BigDecimal;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

/**
 *
 * @author Carlos
 */
public class Formulario {
    
        private Formulario() {}

    /** Limpia texto, selección de combos y fechas. */
    public static void limpiar(Node... campos) {
        for (Node n : campos) {
            if (n instanceof TextField) {
                ((TextField) n).clear();
            } else if (n instanceof ComboBox) {
                ((ComboBox<?>) n).getSelectionModel().clearSelection();
            } else if (n instanceof DatePicker) {
                ((DatePicker) n).setValue(null);
            }
        }
    }

    public static int aEntero(TextField campo) {
        return Integer.parseInt(campo.getText().trim());
    }

    /** Devuelve el valor de un campo opcional, o 0 si está vacío. */
    public static int aEnteroOpcional(TextField campo) {
        String t = campo.getText().trim();
        return t.isEmpty() ? 0 : Integer.parseInt(t);
    }

    public static BigDecimal aDecimal(TextField campo) {
        return new BigDecimal(campo.getText().trim());
    }
    
}
