/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.bad215.electrohogar.controller;

import com.bad215.electrohogar.dao.UsuarioDao;
import com.bad215.electrohogar.model.Usuario;
import com.bad215.electrohogar.utils.Alertas;
import com.bad215.electrohogar.utils.Navegador;
import com.bad215.electrohogar.utils.Sesion;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Carlos
 */
public class LoginController implements Initializable {
       @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtContrasena;

    private final UsuarioDao usuarioDao = new UsuarioDao();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // aquí va lo que quieras preparar al abrir la pantalla (puede quedar vacío)
    }

    @FXML
    private void onIngresar() {
        String usuario = txtUsuario.getText().trim();
        String contrasena = txtContrasena.getText();

        if (usuario.isEmpty() || contrasena.isEmpty()) {
            Alertas.advertencia("Campos vacíos", "Ingresa tu usuario y contraseña.");
            return;
        }

        try {
            Usuario u = usuarioDao.autenticar(usuario, contrasena);
            if (u != null) {
                Sesion.iniciar(u);
                abrirMenuPrincipal();
              
            } else {
                Alertas.error("Error de acceso", "Usuario o contraseña incorrectos.");
            }
        } catch (IllegalStateException e) {
            Alertas.advertencia("Acceso denegado", e.getMessage()); // bloqueado o dado de baja
        } catch (SQLException e) {
            Alertas.error("Error de conexión", "No se pudo conectar con la base de datos.");
            e.printStackTrace();
        }
    }

private void abrirMenuPrincipal() {
    try {
        FXMLLoader loader = Navegador.loader("menuPrincipal");
        Parent root = loader.load();

        Stage stage = (Stage) txtUsuario.getScene().getWindow();
        Navegador.mostrar(stage, root, "ElectroHogar");
    } catch (IOException e) {
        Alertas.error("Error", "No se pudo abrir el menú principal: " + e.getMessage());
    }
}
}

