package com.bad215.electrohogar.controller;
import com.bad215.electrohogar.dao.FacturaDao;
import com.bad215.electrohogar.model.Factura;
import com.bad215.electrohogar.model.Usuario;
import com.bad215.electrohogar.utils.Alertas;
import com.bad215.electrohogar.utils.Navegador;
import com.bad215.electrohogar.utils.Sesion;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

/**
 *
 * @author Say
 */
public class FacturasController implements Initializable {
    private static final int MAX_TEXTO_BUSQUEDA = 50;
    private static final DateTimeFormatter FORMATO_FECHA_LARGA =
            DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM 'de' yyyy", Locale.forLanguageTag("es-SV"));
    private static final DateTimeFormatter FORMATO_FECHA_FACTURA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML private Label lblFechaActual;
    @FXML private Label lblUsuarioActivo;
    @FXML private TextField txtBuscarFactura;
 
    @FXML private TableView<Factura> tblFacturas;
    @FXML private TableColumn<Factura, Number> colNumeroFactura;
    @FXML private TableColumn<Factura, String> colCliente;
    @FXML private TableColumn<Factura, String> colTipoCliente;
    @FXML private TableColumn<Factura, String> colEmpleado;
    @FXML private TableColumn<Factura, String> colFecha;
    @FXML private TableColumn<Factura, String> colEstado;
    @FXML private TableColumn<Factura, String> colMetodoPago;
 
    private final FacturaDao facturaDao = new FacturaDao();
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        mostrarDatosSesion();
        configurarColumnas();
        cargarFacturas("");
    }
    
    // esto funciona de encabezado
    private void mostrarDatosSesion() {
        lblFechaActual.setText("Fecha: " + LocalDate.now().format(FORMATO_FECHA_LARGA));
        Usuario u = Sesion.getUsuario();
        lblUsuarioActivo.setText("Usuario: " + (u != null ? u.getNombreUsuario() : "Sin sesión"));
    }
    
    private void configurarColumnas() {
        colNumeroFactura.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getNumeroFactura()));
        colCliente.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombreCliente()));
        colTipoCliente.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTipoCliente()));
        colEmpleado.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmpleadoRegistro()));
        colFecha.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getFechaFactura() == null
                        ? "" : c.getValue().getFechaFactura().format(FORMATO_FECHA_FACTURA)));
        colEstado.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().isVigente() ? "Vigente" : "Anulada"));
        colMetodoPago.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMetodoPago()));
    }

    private void cargarFacturas(String texto) {
        try {
            List<Factura> facturas = facturaDao.listarFacturas(Sesion.getIdUsuario(), texto);
            tblFacturas.setItems(FXCollections.observableArrayList(facturas));
        } catch (SQLException e) {
            if ("45000".equals(e.getSQLState())) {
                Alertas.advertencia("No permitido", e.getMessage());   // mensaje del procedure (permisos)
            } else {
                e.printStackTrace();
                Alertas.error("Facturas", "No se pudieron cargar las facturas.");
            }
        }
    }
    
    @FXML
    private void buscarFacturas() {
        String texto = txtBuscarFactura.getText() == null ? "" : txtBuscarFactura.getText().trim();
        if (texto.length() > MAX_TEXTO_BUSQUEDA) {
            Alertas.advertencia("Búsqueda", "El texto de búsqueda no puede pasar de "
                    + MAX_TEXTO_BUSQUEDA + " caracteres.");
            return;
        }
        cargarFacturas(texto);
    }
 
    @FXML
    private void crearFactura() {
        abrirEnContenido(tblFacturas, "FrmFacturasDetalle");
    }

        static void abrirEnContenido(Node origen, String vista) {
        Scene escena = origen.getScene();
        StackPane contenido = null;
 
        if (escena != null && escena.getRoot() instanceof BorderPane) {
            Node centro = ((BorderPane) escena.getRoot()).getCenter();
            if (centro instanceof StackPane) {
                contenido = (StackPane) centro;
            }
        }
        if (contenido == null) {
            Alertas.error("Navegación", "No se encontró el área de contenido del menú principal.");
            return;
        }
 
        try {
            FXMLLoader loader = Navegador.loader(vista);
            Node pantalla = loader.load();
            contenido.getChildren().setAll(pantalla);
        } catch (IOException e) {
            e.printStackTrace();
            Alertas.error("No se pudo abrir " + vista, e.getMessage());
        }
    }

}
