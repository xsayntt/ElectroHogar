package com.bad215.electrohogar.controller;

import com.bad215.electrohogar.dao.CuotaDao;
import com.bad215.electrohogar.model.Cuota;
import com.bad215.electrohogar.utils.Alertas;
import com.bad215.electrohogar.utils.Conexion;
import com.bad215.electrohogar.utils.Sesion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.*;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
/**
 *
 * @author Rene
 */
public class CreditosController {
    
    @FXML private TextField txtClienteId;
    @FXML private TextField txtNumeroFactura;
    @FXML private TextField txtDui;
    @FXML private TextField txtNit;
    @FXML private ComboBox<String> cmbEstado;

    @FXML private TableView<Cuota> tblCuotas;
    @FXML private TableColumn<Cuota, Integer> colFactura;
    @FXML private TableColumn<Cuota, Integer> colIdCuota;
    @FXML private TableColumn<Cuota, Integer> colNumCuota;
    @FXML private TableColumn<Cuota, Integer> colCantCuotas;
    @FXML private TableColumn<Cuota, Date> colVencimiento;
    @FXML private TableColumn<Cuota, Double> colMonto;
    @FXML private TableColumn<Cuota, String> colDui;
    @FXML private TableColumn<Cuota, String> colNit;
    @FXML private TableColumn<Cuota, String> colEstado;

    @FXML private ComboBox<String> cmbMetodoPago;
    @FXML private Button btnPagar;

    private final CuotaDao cuotaDAO = new CuotaDao();
    private final ObservableList<Cuota> listaCuotas = FXCollections.observableArrayList();
    private int idUsuarioLogueado;

    public void initData(int idUsuario) {
        this.idUsuarioLogueado = idUsuario;
    }

    @FXML
    public void initialize() {
        this.idUsuarioLogueado = Sesion.getIdUsuario();

        configurarTabla();

        cmbEstado.setItems(FXCollections.observableArrayList("Todos", "Pendiente", "Pagada", "Vencida"));
        cmbEstado.getSelectionModel().select("Todos");

        cmbMetodoPago.setItems(FXCollections.observableArrayList("Efectivo", "Tarjeta"));
        cmbMetodoPago.getSelectionModel().selectFirst();

        btnPagar.setDisable(true);
        tblCuotas.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) ->
                btnPagar.setDisable(newSel == null || "Pagada".equalsIgnoreCase(newSel.getEstadoCalculado())));
    }

    private void configurarTabla() {
        colFactura.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getNumeroFactura()).asObject());
        colIdCuota.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getIdCuota()).asObject());
        colNumCuota.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getNumeroCuota()).asObject());
        colCantCuotas.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getCantidadCuotas()).asObject());
        colVencimiento.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getFechaVencimiento()));
        colMonto.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getMontoCuota()).asObject());
        colDui.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDui()));
        colNit.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNit()));
        colEstado.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEstadoCalculado()));

        tblCuotas.setItems(listaCuotas);
    }

    @FXML
    private void handleBuscar() {
        listaCuotas.clear();

        try {
            listaCuotas.addAll(cuotaDAO.listar(
                    idUsuarioLogueado,
                    txtClienteId.getText(),
                    txtNumeroFactura.getText(),
                    txtDui.getText(),
                    txtNit.getText(),
                    cmbEstado.getValue()
            ));

            if (listaCuotas.isEmpty()) {
                Alertas.advertencia("Búsqueda", "No se encontraron cuotas con los criterios ingresados.");
            }

        } catch (IllegalArgumentException e) {
            Alertas.advertencia("Datos inválidos", e.getMessage());
        } catch (SQLException e) {
            Alertas.advertencia("Error al consultar cuotas", e.getMessage());
        }
    }

    @FXML
    private void handlePagar() {
        Cuota cuota = tblCuotas.getSelectionModel().getSelectedItem();
        if (cuota == null) return;

        int idMetodoPago = cmbMetodoPago.getSelectionModel().getSelectedIndex() + 1;

        try {
            cuotaDAO.pagar(idUsuarioLogueado, cuota, idMetodoPago);

            Alertas.advertencia("Pago Exitoso",
                    "La cuota N° " + cuota.getNumeroCuota() + " fue pagada correctamente.");
            handleBuscar();

        } catch (IllegalArgumentException e) {
            Alertas.advertencia("Datos inválidos", e.getMessage());
        } catch (SQLException e) {
            Alertas.advertencia("Error en procesamiento de pago", e.getMessage());
        }
    }
}