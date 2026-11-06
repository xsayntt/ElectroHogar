/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.bad215.electrohogar.controller;

import com.bad215.electrohogar.dao.MenuRutaDao;
import com.bad215.electrohogar.model.MenuRuta;
import com.bad215.electrohogar.model.Usuario;
import com.bad215.electrohogar.utils.Alertas;
import com.bad215.electrohogar.utils.Navegador;
import com.bad215.electrohogar.utils.Sesion;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author Carlos
 */
public class MenuPrincipalController implements Initializable {
    
     private static final String RUTA_VISTAS = "/com/bad215/electrohogar/";
    private static final int SIN_PADRE = 0;   // MEN_ID_MENU NULL en BD llega como 0

    @FXML private VBox menuContainer;
    @FXML private StackPane contentPane;
    @FXML private Label lblUsuario;
    @FXML private Label lblHora;
    
    private static final DateTimeFormatter FORMATO =
    DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM 'de' yyyy", Locale.forLanguageTag("es-SV"));
    private final MenuRutaDao menuRutaDao = new MenuRutaDao();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        mostrarUsuario();
        mostrarFecha();
        cargarMenu();
    }

    private void cargarMenu() {
        menuContainer.getChildren().clear();

        List<MenuRuta> opciones;
        try {
            opciones = menuRutaDao.menuPorUsuario(Sesion.getIdUsuario());
        } catch (SQLException e) {
            Alertas.error("No se pudo cargar el menú", e.getMessage());
            return;
        }

        if (opciones.isEmpty()) {
            Alertas.info("Sin acceso", "El usuario no tiene opciones de menú disponibles.");
            return;
        }

        // El SP ya viene ordenado por ORDEN y groupingBy conserva ese orden.
        // Las raíces quedan bajo la clave SIN_PADRE (0).
        Map<Integer, List<MenuRuta>> hijosPorPadre = opciones.stream()
                .collect(Collectors.groupingBy(MenuRuta::getMenIdMenu));

        for (MenuRuta raiz : hijosPorPadre.getOrDefault(SIN_PADRE, Collections.emptyList())) {
            List<MenuRuta> hijos = hijosPorPadre.getOrDefault(raiz.getIdMenu(), Collections.emptyList())
                    .stream()
                    .filter(h -> h.getPrivilegio().isPuedeLeer())
                    .collect(Collectors.toList());

            if (!hijos.isEmpty()) {
                menuContainer.getChildren().add(crearGrupo(raiz, hijos));
            } else if (raiz.getPrivilegio().isPuedeLeer() && raiz.tieneRuta()) {
                menuContainer.getChildren().add(crearBoton(raiz));
            }
        }
    }

    private TitledPane crearGrupo(MenuRuta padre, List<MenuRuta> hijos) {
        VBox contenido = new VBox(4);
        for (MenuRuta hijo : hijos) {
            contenido.getChildren().add(crearBoton(hijo));
        }
        TitledPane grupo = new TitledPane(padre.getNombreOpcion(), contenido);
        grupo.setExpanded(false);
        return grupo;
    }

    private Button crearBoton(MenuRuta opcion) {
        Button btn = new Button(opcion.getNombreOpcion());
        btn.getStyleClass().add("menu-button");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnAction(e -> abrirVista(opcion));
        return btn;
    }
    
    private void abrirVista(MenuRuta opcion) {
    if (!opcion.tieneRuta()) {
        return;
    }
    try {
        FXMLLoader loader = Navegador.loader(opcion.getRuta());
        Node vista = loader.load();
        contentPane.getChildren().setAll(vista);
    } catch (IOException e) {
        Alertas.error("No se pudo abrir " + opcion.getNombreOpcion(), e.getMessage());
    }
}
    
    private void mostrarUsuario() {
    Usuario u = Sesion.getUsuario();
    lblUsuario.setText(u != null ? u.getNombreUsuario() : "Sin sesión");
}
    
    private void mostrarFecha() {
    lblHora.setText(LocalDate.now().format(FORMATO));
}
}
