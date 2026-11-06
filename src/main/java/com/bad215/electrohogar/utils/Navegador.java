/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bad215.electrohogar.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class Navegador {

    private static final String BASE = "/com/bad215/electrohogar/";

    private Navegador() {}

    public static FXMLLoader loader(String fxml) throws IOException {
        String ruta = BASE + (fxml.endsWith(".fxml") ? fxml : fxml + ".fxml");
        URL url = Navegador.class.getResource(ruta);
        if (url == null) {
            throw new IOException("No existe el archivo " + ruta);
        }
        return new FXMLLoader(url);
    }

    public static void mostrar(Stage stage, Parent root, String titulo) {
        stage.setScene(new Scene(root));
        stage.setTitle(titulo);
        stage.centerOnScreen();
    }
}
