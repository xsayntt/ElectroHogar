package com.bad215.electrohogar.controller;

import com.bad215.electrohogar.model.Cuota;
import com.bad215.electrohogar.utils.Alertas;
import com.bad215.electrohogar.utils.Conexion;
import com.bad215.electrohogar.utils.Sesion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.*;
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

    private int idUsuarioLogueado;
    private final ObservableList<Cuota> listaCuotas = FXCollections.observableArrayList();

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
        tblCuotas.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            btnPagar.setDisable(newSel == null || "Pagada".equalsIgnoreCase(newSel.getEstadoCalculado()));
        });
    }

    private void configurarTabla() {
        colFactura.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getNumeroFactura()).asObject());
        colIdCuota.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getIdCuota()).asObject());
        colNumCuota.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getNumeroCuota()).asObject());
        colCantCuotas.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getCantidadCuotas()).asObject());
        colVencimiento.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getFechaVencimiento()));
        colMonto.setCellValueFactory(cellData -> new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getMontoCuota()).asObject()); // <-- Uso de SimpleDoubleProperty
        colDui.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDui()));
        colNit.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNit()));
        colEstado.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEstadoCalculado()));

        tblCuotas.setItems(listaCuotas);
    }

    @FXML
    private void handleBuscar() {
        listaCuotas.clear();

        Integer idCliente = txtClienteId.getText().trim().isEmpty() ? null : Integer.parseInt(txtClienteId.getText().trim());
        Integer numFactura = txtNumeroFactura.getText().trim().isEmpty() ? null : Integer.parseInt(txtNumeroFactura.getText().trim());
        String dui = txtDui.getText().trim();
        String nit = txtNit.getText().trim();
        String estado = cmbEstado.getValue();

        String sql = "{CALL sp_listar_cuotas(?, ?, ?, ?, ?, ?)}";

        try (Connection conn = Conexion.getConexion();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, idUsuarioLogueado);
            stmt.setObject(2, idCliente, Types.INTEGER);
            stmt.setObject(3, numFactura, Types.INTEGER);
            stmt.setString(4, dui.isEmpty() ? null : dui);
            stmt.setString(5, nit.isEmpty() ? null : nit);
            stmt.setString(6, "Todos".equals(estado) ? null : estado);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                listaCuotas.add(new Cuota(
                        rs.getInt("NUMERO_FACTURA"),
                        rs.getInt("ID_CUOTA"),
                        rs.getInt("NUMERO_CUOTA"),
                        rs.getInt("CANTIDAD_CUOTAS"),
                        rs.getDate("FECHA_VENCIMIENTO"),
                        rs.getDouble("MONTO_CUOTA"),
                        rs.getString("DUI"),
                        rs.getString("NIT"),
                        rs.getString("ESTADO_CALCULADO")
                ));
            }

            if (listaCuotas.isEmpty()) {
                Alertas.advertencia("Búsqueda", "No se encontraron cuotas con los criterios ingresados.");
            }

        } catch (SQLException e) {
            Alertas.advertencia("Error al consultar cuotas", e.getMessage());
        }
    }

    @FXML
    private void handlePagar() {
        Cuota cuotaSeleccionada = tblCuotas.getSelectionModel().getSelectedItem();
        if (cuotaSeleccionada == null) return;

        int idMetodoPago = cmbMetodoPago.getSelectionModel().getSelectedIndex() + 1;

        String sql = "{CALL sp_pagar_cuota(?, ?, ?)}";

        try (Connection conn = Conexion.getConexion();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, idUsuarioLogueado);
            stmt.setInt(2, cuotaSeleccionada.getIdCuota());
            stmt.setInt(3, idMetodoPago);

            stmt.executeUpdate();

            Alertas.advertencia("Pago Exitoso", "La cuota N° " + cuotaSeleccionada.getNumeroCuota() + " fue pagada correctamente.");

            handleBuscar();

        } catch (SQLException e) {
            Alertas.advertencia("Error en procesamiento de pago", e.getMessage());
        }
    }
}