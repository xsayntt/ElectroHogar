/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.bad215.electrohogar.controller;

import com.bad215.electrohogar.dao.FacturaDao;
import com.bad215.electrohogar.model.ClienteVenta;
import com.bad215.electrohogar.utils.Alertas;
import com.bad215.electrohogar.utils.Sesion;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Carlos
 */
public class SeleccionarClienteController {

@FXML private TextField txtBuscar;
    @FXML private Button btnBuscar;
    @FXML private Button btnSeleccionar;
    @FXML private TableView<ClienteVenta> tblClientes;
    @FXML private TableColumn<ClienteVenta, Number> colId;
    @FXML private TableColumn<ClienteVenta, String> colNombre;
    @FXML private TableColumn<ClienteVenta, String> colTelefono;

    private ClienteVenta clienteElegido;
    private final FacturaDao facturaDao = new FacturaDao();
    
    
    @FXML
    private void initialize() {
        colId.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getIdCliente()));
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombreCliente()));
        colTelefono.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTipoCliente()));

        btnSeleccionar.disableProperty().bind(
                tblClientes.getSelectionModel().selectedItemProperty().isNull());

        // Doble clic elige directamente
        tblClientes.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2 && tblClientes.getSelectionModel().getSelectedItem() != null) {
                seleccionar();
            }
        });

        // Carga inicial (los primeros N, según el procedure)
        buscar();
    }
    
    @FXML
private void buscar() {
    try {
        List<ClienteVenta> resultado =
                facturaDao.buscarClientesEnVenta(Sesion.getIdUsuario(), txtBuscar.getText());
        tblClientes.setItems(FXCollections.observableArrayList(resultado));
    } catch (SQLException ex) {
        // 45000 = mensaje de negocio del procedure (ej. sin permisos)
        if ("45000".equals(ex.getSQLState())) {
            Alertas.advertencia("Clientes", ex.getMessage());
        } else {
            Alertas.error("Clientes", "No se pudo buscar los clientes.");
        }
    }
}

    @FXML
    private void seleccionar() {
        clienteElegido = tblClientes.getSelectionModel().getSelectedItem();
        cerrar();
    }

    @FXML
    private void cancelar() {
        clienteElegido = null;
        cerrar();
    }

    /** La pantalla de factura lo llama al cerrarse el diálogo. */
    public ClienteVenta getClienteElegido() {
        return clienteElegido;
    }

    private void cerrar() {
        ((Stage) btnBuscar.getScene().getWindow()).close();
    }

}
