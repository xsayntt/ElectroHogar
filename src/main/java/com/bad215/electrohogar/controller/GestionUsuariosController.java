/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.bad215.electrohogar.controller;

import com.bad215.electrohogar.dao.SucursalDao;
import com.bad215.electrohogar.dao.TipoUsuarioDao;
import com.bad215.electrohogar.dao.UsuarioDao;
import com.bad215.electrohogar.model.Cajero;
import com.bad215.electrohogar.model.Empleado;
import com.bad215.electrohogar.model.Sucursal;
import com.bad215.electrohogar.model.Supervisor;
import com.bad215.electrohogar.model.TipoUsuario;
import com.bad215.electrohogar.model.Turno;
import com.bad215.electrohogar.model.Usuario;
import com.bad215.electrohogar.model.UsuarioResumen;
import com.bad215.electrohogar.model.Vendedor;
import com.bad215.electrohogar.utils.Alertas;
import com.bad215.electrohogar.utils.Catalogos;
import com.bad215.electrohogar.utils.Formulario;
import com.bad215.electrohogar.utils.Contrasena;
import com.bad215.electrohogar.utils.Sesion;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
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
    
    
    @FXML private TextField txtBuscar;
    @FXML private Button btnBuscar;
    @FXML private Label lblResultados;
 
    @FXML private TableView<UsuarioResumen> tblUsuarios;
    @FXML private TableColumn<UsuarioResumen, String> colUsuario;
    @FXML private TableColumn<UsuarioResumen, String> colRol;
    @FXML private TableColumn<UsuarioResumen, String> colEstado;
    @FXML private TableColumn<UsuarioResumen, String> colBloqueado;
    @FXML private TableColumn<UsuarioResumen, Number> colIntentos;
    @FXML private TableColumn<UsuarioResumen, String> colEmpleado;
    @FXML private TableColumn<UsuarioResumen, String> colDui;
    @FXML private TableColumn<UsuarioResumen, String> colEmail;

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
    @FXML private ComboBox<Supervisor> cmbSupervisor;      

    // ----- Subtipo (opcional) -----
    @FXML private ComboBox<String> cmbSubtipo;    // "Ninguno", "Vendedor", "Cajero"
    @FXML private VBox boxVendedor;               // contiene txtMeta y txtComision
    @FXML private TextField txtMeta;
    @FXML private TextField txtComision;
    @FXML private VBox boxCajero;                 // contiene cmbTurno y txtNumeroCaja
    @FXML private ComboBox<Turno> cmbTurno;
    @FXML private TextField txtNumeroCaja;

    private final UsuarioDao usuarioDao = new UsuarioDao();
    private String ultimoTexto = "";
 
    @FunctionalInterface
    private interface AccionDAO {
        void ejecutar() throws SQLException;
    }


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Catalogos.cargarRoles(cmbRol);
        Catalogos.cargarSucursales(cmbSucursal);
        Catalogos.cargarTurnos(cmbTurno);
        Catalogos.cargarSupervisores(cmbSupervisor, null); 
        cmbSubtipo.getItems().setAll("Ninguno", "Vendedor", "Cajero");
        cmbSubtipo.getSelectionModel().selectFirst();
        
        Catalogos.cargarSucursales(cmbSucursal);
        cmbSubtipo.valueProperty().addListener((obs, ant, nuevo) -> actualizarSubtipo(nuevo));
        actualizarSubtipo("Ninguno");
        
        colEmpleado.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmpleado()));
        colUsuario.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombreUsuario()));
        colRol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRol()));
        colEstado.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isEstadoUsuario() ? "Activo" : "Inactivo"));
        colBloqueado.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isBloqueado() ? "Sí" : "No"));
        colIntentos.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getIntentosFallidos()));
        colDui.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDuiEmpleado()));
        colEmail.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmailEmpleado()));
 
        configurarColumnaBloqueado();
        configurarColumnaEstado();
        tblUsuarios.setPlaceholder(new Label("Sin resultados"));
 
        // Enter dentro del campo de texto también busca
        txtBuscar.setOnAction(e -> onBuscar());
 
        // Carga inicial: texto vacío trae todos los usuarios
        onBuscar();
    }
    
    private void configurarColumnaBloqueado() {
        colBloqueado.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("🔒");
 
            {
                btn.setTooltip(new Tooltip("Desbloquear usuario"));
                btn.setOnAction(e -> {
                    UsuarioResumen u = getTableRow().getItem();
                    if (u != null) {
                        desbloquear(u);
                    }
                });
            }
 
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                UsuarioResumen u = (empty || getTableRow() == null) ? null : getTableRow().getItem();
                if (u == null) {
                    setGraphic(null);
                    setText(null);
                } else if (u.isBloqueado()) {
                    setText(null);
                    setGraphic(btn);
                } else {
                    setGraphic(null);
                    setText("🔓");
                }
            }
        });
    }
 
    /** Estado: botón que da de baja (si está activo) o reactiva (si está inactivo). */
    private void configurarColumnaEstado() {
        colEstado.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button();
 
            {
                btn.setOnAction(e -> {
                    UsuarioResumen u = getTableRow().getItem();
                    if (u != null) {
                        alternarEstado(u);
                    }
                });
            }
 
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                UsuarioResumen u = (empty || getTableRow() == null) ? null : getTableRow().getItem();
                if (u == null) {
                    setGraphic(null);
                    setText(null);
                    return;
                }
                setText(null);
                if (u.isEstadoUsuario()) {
                    btn.setText("🟢 Activo");
                    btn.setTooltip(new Tooltip("Dar de baja"));
                    // sp_dar_de_baja no permite que alguien se dé de baja a sí mismo
                    btn.setDisable(u.getIdUsuario() == Sesion.getIdUsuario());
                } else {
                    btn.setText("🔴 Inactivo");
                    btn.setTooltip(new Tooltip("Reactivar usuario"));
                    btn.setDisable(false);
                }
                setGraphic(btn);
            }
        });
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
    private void onBuscar() {
        String texto = txtBuscar.getText() == null ? "" : txtBuscar.getText().trim();
        buscar(texto);
    }
 
    private void buscar(String texto) {
        ultimoTexto = texto;
 
        Task<List<UsuarioResumen>> tarea = new Task<>() {
            @Override
            protected List<UsuarioResumen> call() throws Exception {
                return usuarioDao.buscar(Sesion.getIdUsuario(), texto);
            }
        };
 
        tarea.setOnRunning(e -> btnBuscar.setDisable(true));
 
        tarea.setOnSucceeded(e -> {
            List<UsuarioResumen> resultado = tarea.getValue();
            tblUsuarios.setItems(FXCollections.observableArrayList(resultado));
            lblResultados.setText(resultado.size() + " resultado(s)");
            btnBuscar.setDisable(false);
            tblUsuarios.setDisable(false);
        });
 
        tarea.setOnFailed(e -> {
            btnBuscar.setDisable(false);
            tblUsuarios.setDisable(false);
            tblUsuarios.getItems().clear();
            lblResultados.setText("");
            Alertas.info("", tarea.getException().getMessage());
        });
 
        lanzar(tarea, "buscar-usuarios");
    }
    
    private void desbloquear(UsuarioResumen u) {
        if (!confirmar("¿Desbloquear al usuario \"" + u.getNombreUsuario() + "\"?")) {
            return;
        }
        ejecutarAccion(() -> usuarioDao.desbloquear(Sesion.getIdUsuario(), u.getIdUsuario()));
    }
 
    private void alternarEstado(UsuarioResumen u) {
        if (u.isEstadoUsuario()) {
            if (confirmar("¿Dar de baja al usuario \"" + u.getNombreUsuario() + "\"?")) {
                ejecutarAccion(() -> usuarioDao.darDeBaja(Sesion.getIdUsuario(), u.getIdUsuario()));
            }
        } else {
            if (confirmar("¿Reactivar al usuario \"" + u.getNombreUsuario() + "\"?")) {
                ejecutarAccion(() -> usuarioDao.reactivar(Sesion.getIdUsuario(), u.getIdUsuario()));
            }
        }
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
    
        private void ejecutarAccion(AccionDAO accion) {
        Task<Void> tarea = new Task<>() {
            @Override
            protected Void call() throws Exception {
                accion.ejecutar();
                return null;
            }
        };
 
        tarea.setOnRunning(e -> tblUsuarios.setDisable(true));
 
        tarea.setOnSucceeded(e -> buscar(ultimoTexto));
 
        tarea.setOnFailed(e -> {
            Alertas.info("",tarea.getException().getMessage());
            // Se recarga igual: el estado del usuario pudo haber cambiado mientras tanto
            buscar(ultimoTexto);
        });
 
        lanzar(tarea, "accion-usuario");
    }
        
        private void lanzar(Task<?> tarea, String nombre) {
        Thread hilo = new Thread(tarea, nombre);
        hilo.setDaemon(true);
        hilo.start();
    }
    // ---------- Construcción de objetos ----------

    private Usuario construirUsuario() {
        TipoUsuario rol = cmbRol.getValue();
    Usuario u = new Usuario();
    u.setIdTipo(rol == null ? 0 : rol.getIdTipo());
    u.setNombreUsuario(txtNombreUsuario.getText());
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
        Supervisor sup = cmbSupervisor.getValue();
        e.setIdSupervisor(sup == null ? null : sup.getIdEmpleado());
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
        private boolean confirmar(String mensaje) {
        return Alertas.confirmar("Usuarios", mensaje);
    }
    
    // ---------- Limpieza ----------

    private void limpiarFormulario() {
        Formulario.limpiar(txtNombreUsuario, txtClave, cmbRol, cmbSucursal, txtNombres,
                txtApellidos, txtDui, txtTelefono, dpFechaContratacion, txtDireccion,
                txtEmail, cmbSupervisor, txtMeta, txtComision, cmbTurno, txtNumeroCaja);
        cmbSubtipo.getSelectionModel().selectFirst();
    }
}
