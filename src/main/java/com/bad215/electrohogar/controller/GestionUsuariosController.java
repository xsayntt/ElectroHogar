/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.bad215.electrohogar.controller;

import com.bad215.electrohogar.dao.SucursalDao;
import com.bad215.electrohogar.dao.UsuarioDao;
import com.bad215.electrohogar.model.Cajero;
import com.bad215.electrohogar.model.Empleado;
import com.bad215.electrohogar.model.Sucursal;
import com.bad215.electrohogar.model.TipoUsuario;
import com.bad215.electrohogar.model.Turno;
import com.bad215.electrohogar.model.Usuario;
import com.bad215.electrohogar.model.Vendedor;
import com.bad215.electrohogar.utils.Alertas;
import com.bad215.electrohogar.utils.Catalogos;
import com.bad215.electrohogar.utils.Formulario;
import com.bad215.electrohogar.utils.Contrasena;
import com.bad215.electrohogar.utils.Sesion;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author Carlos
 */
public class GestionUsuariosController implements Initializable {
    
    private static final String NINGUNO = "Ninguno";
    private static final String VENDEDOR = "Vendedor";
    private static final String CAJERO = "Cajero";
    
    // ----- Datos de cuenta -----
    @FXML private TextField txtNombreUsuario;
    @FXML private PasswordField txtClave;
    @FXML private ComboBox<TipoUsuario> cmbRol;

    // ----- Datos de empleado -----
    @FXML private ComboBox<Sucursal> cmbSucursal;
    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtDui;
    @FXML private TextField txtTelefono;
    @FXML private DatePicker dpFechaContratacion;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtEmail;
    @FXML private TextField txtIdSupervisor;      // opcional

    // ----- Subtipo (opcional) -----
    @FXML private ComboBox<String> cmbSubtipo;    // "Ninguno", "Vendedor", "Cajero"
    @FXML private VBox boxVendedor;               // contiene txtMeta y txtComision
    @FXML private TextField txtMeta;
    @FXML private TextField txtComision;
    @FXML private VBox boxCajero;                 // contiene cmbTurno y txtNumeroCaja
    @FXML private ComboBox<Turno> cmbTurno;
    @FXML private TextField txtNumeroCaja;

    private final UsuarioDao usuarioDao = new UsuarioDao();


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cmbRol.getItems().setAll(TipoUsuario.values());
        cmbTurno.getItems().setAll(Turno.values());
        cmbSubtipo.getItems().setAll("Ninguno", "Vendedor", "Cajero");
        cmbSubtipo.getSelectionModel().selectFirst();
        
        Catalogos.cargarSucursales(cmbSucursal);
        cmbSubtipo.valueProperty().addListener((obs, ant, nuevo) -> actualizarSubtipo(nuevo));
        actualizarSubtipo("Ninguno");
    }

    private void actualizarSubtipo(String subtipo) {
        boolean esVendedor = "Vendedor".equals(subtipo);
        boolean esCajero = "Cajero".equals(subtipo);
        boxVendedor.setVisible(esVendedor);
        boxVendedor.setManaged(esVendedor);
        boxCajero.setVisible(esCajero);
        boxCajero.setManaged(esCajero);
    }

        @FXML
    private void guardarUsuario() {
        if (!formularioValido()) {
            return;
        }
        try {
            Usuario usuario = construirUsuario();
            Empleado empleado = construirEmpleado();
            Vendedor vendedor = construirVendedor();
            Cajero cajero = construirCajero();

            usuarioDao.crear(Sesion.getIdUsuario(), usuario, empleado, vendedor, cajero);

            Alertas.info("Usuario creado", "Usuario creado correctamente (ID " + usuario.getIdUsuario() + ")");
            limpiarFormulario();

        } catch (NumberFormatException e) {
            Alertas.advertencia("Datos inválidos", "Revisa los campos numéricos (supervisor, meta, comisión o caja).");
        } catch (SQLException e) {
            if ("45000".equals(e.getSQLState())) {
                Alertas.advertencia("No permitido", e.getMessage());   // mensaje del procedure
            } else {
                e.printStackTrace();
                Alertas.error("Error", "Error de base de datos.");
            }
        }
    }

    // ---------- Validación ----------

    private boolean formularioValido() {
        String clave = txtClave.getText();
        if (clave == null || clave.length() < 8) {
            Alertas.advertencia("Contraseña", "La contraseña debe tener al menos 8 caracteres.");
            return false;
        }
        if (cmbRol.getValue() == null || cmbSucursal.getValue() == null
                || dpFechaContratacion.getValue() == null) {
            Alertas.advertencia("Campos requeridos", "Selecciona rol, sucursal y fecha de contratación.");
            return false;
        }
        if (CAJERO.equals(cmbSubtipo.getValue()) && cmbTurno.getValue() == null) {
            Alertas.advertencia("Campos requeridos", "Selecciona el turno del cajero.");
            return false;
        }
        return true;
    }

    // ---------- Construcción de objetos ----------

    private Usuario construirUsuario() {
        Usuario u = new Usuario();
        u.setIdTipo(cmbRol.getValue().getId());
        u.setNombreUsuario(txtNombreUsuario.getText().trim());
        u.setContrasena(Contrasena.hashear(txtClave.getText()));   // hash BCrypt de 60 caracteres
        return u;
    }

    private Empleado construirEmpleado() {
        Empleado e = new Empleado();
        e.setIdSucursal(cmbSucursal.getValue().getIdSucursal());
        e.setNombres(txtNombres.getText());
        e.setApellidos(txtApellidos.getText());
        e.setDuiEmpleado(txtDui.getText());
        e.setTelefonoEmpleado(txtTelefono.getText());
        e.setFechaContratacion(dpFechaContratacion.getValue());
        e.setDireccionPersonal(txtDireccion.getText());
        e.setEmailEmpleado(txtEmail.getText());
        e.setIdSupervisor(Formulario.aEnteroOpcional(txtIdSupervisor));
        return e;
    }

    private Vendedor construirVendedor() {
        if (!VENDEDOR.equals(cmbSubtipo.getValue())) {
            return null;
        }
        Vendedor v = new Vendedor();
        v.setMetaMensual(Formulario.aDecimal(txtMeta));
        v.setPorcentajeComision(Formulario.aDecimal(txtComision));
        return v;
    }

    private Cajero construirCajero() {
        if (!CAJERO.equals(cmbSubtipo.getValue())) {
            return null;
        }
        Cajero c = new Cajero();
        c.setIdTurno(cmbTurno.getValue().getIdTurno());
        c.setNumeroCaja(Formulario.aEntero(txtNumeroCaja));
        return c;
    }

    // ---------- Limpieza ----------

    private void limpiarFormulario() {
        Formulario.limpiar(txtNombreUsuario, txtClave, cmbRol, cmbSucursal, txtNombres,
                txtApellidos, txtDui, txtTelefono, dpFechaContratacion, txtDireccion,
                txtEmail, txtIdSupervisor, txtMeta, txtComision, cmbTurno, txtNumeroCaja);
        cmbSubtipo.getSelectionModel().selectFirst();
    }
}
