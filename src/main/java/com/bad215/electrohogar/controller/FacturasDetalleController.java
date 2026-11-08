package com.bad215.electrohogar.controller;

import com.bad215.electrohogar.dao.FacturaDao;
import com.bad215.electrohogar.model.ClienteVenta;
import com.bad215.electrohogar.model.LineaVenta;
import com.bad215.electrohogar.model.MetodoPago;
import com.bad215.electrohogar.model.ProductoVenta;
import com.bad215.electrohogar.utils.Alertas;
import com.bad215.electrohogar.utils.Formulario;
import com.bad215.electrohogar.utils.Sesion;

import java.net.URL;
import java.sql.SQLException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/**
 *
 * @author Say
 */
public class FacturasDetalleController implements Initializable {
    private static final int MAX_TEXTO_BUSQUEDA = 50;
    private static final int MAX_CUOTAS = 100;
    private static final String SIN_PRODUCTO = "Ningún producto seleccionado";
    
    @FXML private TextField txtBuscarCliente;
    @FXML private ComboBox<ClienteVenta> cmbCliente;
    @FXML private ComboBox<MetodoPago> cmbMetodoPago;
    @FXML private TextField txtCuotas;
    @FXML private TextField txtSubtotal;
    @FXML private TextField txtTotal;

    @FXML private Label lblProductoSeleccionado;
    @FXML private TextField txtCantidad;
    
    @FXML private TextField txtBuscarProducto;
    @FXML private TableView<ProductoVenta> tblProductos;
    @FXML private TableColumn<ProductoVenta, Number> colProdId;
    @FXML private TableColumn<ProductoVenta, String> colProdNombre;
    @FXML private TableColumn<ProductoVenta, String> colProdCategoria;
    @FXML private TableColumn<ProductoVenta, String> colProdPrecio;
    @FXML private TableColumn<ProductoVenta, String> colProdPrecioVigente;
    @FXML private TableColumn<ProductoVenta, Number> colProdStock;
    
    @FXML private TableView<LineaVenta> tblAgregados;
    @FXML private TableColumn<LineaVenta, Number> colAgId;
    @FXML private TableColumn<LineaVenta, String> colAgNombre;
    @FXML private TableColumn<LineaVenta, Number> colAgCantidad;
    @FXML private TableColumn<LineaVenta, String> colAgPrecio;
    @FXML private TableColumn<LineaVenta, String> colAgSubtotal;
    @FXML private Button btnQuitarProducto;

    @FXML private Button btnCancelar;
    @FXML private Button btnProcederPago;
    
    private final FacturaDao facturaDao = new FacturaDao();
    private final ObservableList<LineaVenta> lineas = FXCollections.observableArrayList();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTablaProductos();
        configurarTablaAgregados();
        configurarSelecciones();
        txtCantidad.setText("1");
        cargarDatosIniciales();
    }
    
    private void configurarTablaProductos() {
        colProdId.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getIdProducto()));
        colProdNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombreProducto()));
        colProdCategoria.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombreCategoria()));
        colProdPrecio.setCellValueFactory(c -> new SimpleStringProperty(formato(c.getValue().getPrecio())));
        colProdPrecioVigente.setCellValueFactory(c -> new SimpleStringProperty(formato(c.getValue().getPrecioVigente())));
        colProdStock.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getStock()));
    }

    private void configurarTablaAgregados() {
        colAgId.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getIdProducto()));
        colAgNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombreProducto()));
        colAgCantidad.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getCantidad()));
        colAgPrecio.setCellValueFactory(c -> new SimpleStringProperty(formato(c.getValue().getPrecioUnitario())));
        colAgSubtotal.setCellValueFactory(c -> new SimpleStringProperty(formato(c.getValue().getSubtotal())));
        tblAgregados.setItems(lineas);
    }

    private void configurarSelecciones() {
        tblProductos.getSelectionModel().selectedItemProperty().addListener((obs, anterior, nuevo) ->
                lblProductoSeleccionado.setText(nuevo == null ? SIN_PRODUCTO : nuevo.getNombreProducto()));
        btnQuitarProducto.disableProperty().bind(tblAgregados.getSelectionModel().selectedItemProperty().isNull());
    }

    private void cargarDatosIniciales() {
        try {
            int idUsuario = Sesion.getIdUsuario();
            cmbMetodoPago.setItems(FXCollections.observableArrayList(facturaDao.listarMetodosPago(idUsuario)));
            cmbCliente.setItems(FXCollections.observableArrayList(facturaDao.buscarClientesEnVenta(idUsuario, "")));
            tblProductos.setItems(FXCollections.observableArrayList(facturaDao.buscarProductosEnVenta(idUsuario, "")));
        } catch (SQLException e) {
            mostrarError("Nueva factura", "No se pudieron cargar los datos de la pantalla.", e);
        }
    }

    @FXML
    private void buscarCliente() {
        String texto = textoBusqueda(txtBuscarCliente);
        if (texto == null) {
            return;
        }
        try {
            List<ClienteVenta> clientes = facturaDao.buscarClientesEnVenta(Sesion.getIdUsuario(), texto);
            cmbCliente.setItems(FXCollections.observableArrayList(clientes));
            cmbCliente.getSelectionModel().clearSelection();
            if (clientes.isEmpty()) {
                Alertas.info("Clientes", "No se encontraron clientes con ese criterio.");
            } else {
                cmbCliente.show();
            }
        } catch (SQLException e) {
            mostrarError("Clientes", "No se pudieron buscar los clientes.", e);
        }
    }

    @FXML
    private void metodoPagoSeleccionado() {
        boolean credito = esCredito(cmbMetodoPago.getValue());
        txtCuotas.setDisable(!credito);
        if (!credito) {
            txtCuotas.clear();
        }
    }

    @FXML
    private void buscarProducto() {
        String texto = textoBusqueda(txtBuscarProducto);
        if (texto == null) {
            return;
        }
        try {
            List<ProductoVenta> productos = facturaDao.buscarProductosEnVenta(Sesion.getIdUsuario(), texto);
            tblProductos.setItems(FXCollections.observableArrayList(productos));
        } catch (SQLException e) {
            mostrarError("Productos", "No se pudieron buscar los productos.", e);
        }
    }

    @FXML
    private void agregarProducto() {
        ProductoVenta producto = tblProductos.getSelectionModel().getSelectedItem();
        if (producto == null) {
            Alertas.advertencia("Producto", "Selecciona un producto de la tabla.");
            return;
        }
 
        int cantidad;
        try {
            cantidad = Formulario.aEntero(txtCantidad);
        } catch (NumberFormatException e) {
            Alertas.advertencia("Cantidad", "Ingresa una cantidad válida (número entero).");
            return;
        }
        if (cantidad < 1) {
            Alertas.advertencia("Cantidad", "La cantidad debe ser mayor a cero.");
            return;
        }
 
        LineaVenta existente = buscarLinea(producto.getIdProducto());
        int acumulada = cantidad + (existente == null ? 0 : existente.getCantidad());
        if (acumulada > producto.getStock()) {
            Alertas.advertencia("Stock insuficiente", "Solo hay " + producto.getStock()
                    + " unidades de " + producto.getNombreProducto() + ".");
            return;
        }
 
        if (existente == null) {
            LineaVenta linea = new LineaVenta();
            linea.setIdProducto(producto.getIdProducto());
            linea.setNombreProducto(producto.getNombreProducto());
            linea.setPrecioUnitario(producto.getPrecioVigente());
            linea.setCantidad(cantidad);
            lineas.add(linea);
        } else {
            existente.setCantidad(acumulada);
            tblAgregados.refresh();
        }
 
        txtCantidad.setText("1");
        recalcularTotales();
    }

    @FXML
    private void quitarProducto() {
        LineaVenta seleccionada = tblAgregados.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            lineas.remove(seleccionada);
            recalcularTotales();
        }
    }

        @FXML
    private void procederPago() {
        ClienteVenta cliente = cmbCliente.getValue();
        MetodoPago metodo = cmbMetodoPago.getValue();
        if (cliente == null || metodo == null) {
            Alertas.advertencia("Campos requeridos", "Selecciona el cliente y el método de pago.");
            return;
        }
        if (lineas.isEmpty()) {
            Alertas.advertencia("Productos", "Agrega al menos un producto a la factura.");
            return;
        }
 
        int cuotas = 0;
        if (esCredito(metodo)) {
            try {
                cuotas = Formulario.aEntero(txtCuotas);
            } catch (NumberFormatException e) {
                Alertas.advertencia("Cuotas", "Ingresa la cantidad de cuotas (número entero).");
                return;
            }
            if (cuotas < 1 || cuotas > MAX_CUOTAS) {
                Alertas.advertencia("Cuotas", "Las cuotas deben estar entre 1 y " + MAX_CUOTAS + ".");
                return;
            }
        }
 
        try {
            int numeroFactura = facturaDao.registrarVenta(Sesion.getIdUsuario(), cliente.getIdCliente(),
                    metodo.getIdMetodo(), cuotas, new ArrayList<>(lineas));
            Alertas.info("Factura creada", "Factura N° " + numeroFactura + " registrada correctamente.");
            FacturasController.abrirEnContenido(btnProcederPago, "FrmFacturas");
        } catch (SQLException e) {
            mostrarError("Factura", "No se pudo registrar la factura.", e);
        }
    }

    @FXML
    private void cancelar() {
        if (!lineas.isEmpty() && !Alertas.confirmar("Cancelar factura",
                "Se perderán los productos agregados ¿continuar?")) {
            return;
        }
        FacturasController.abrirEnContenido(btnCancelar, "FrmFacturas");
    }

    private LineaVenta buscarLinea(int idProducto) {
        for (LineaVenta l : lineas) {
            if (l.getIdProducto() == idProducto) {
                return l;
            }
        }
        return null;
    }

    private void recalcularTotales() {
        double subtotal = lineas.stream().mapToDouble(LineaVenta::getSubtotal).sum();
        txtSubtotal.setText(formato(subtotal));
        txtTotal.setText(formato(subtotal));
    }

        private String textoBusqueda(TextField campo) {
        String texto = campo.getText() == null ? "" : campo.getText().trim();
        if (texto.length() > MAX_TEXTO_BUSQUEDA) {
            Alertas.advertencia("Búsqueda", "El texto de búsqueda no puede pasar de "
                    + MAX_TEXTO_BUSQUEDA + " caracteres.");
            return null;
        }
        return texto;
    }

    private boolean esCredito(MetodoPago metodo) {
        if (metodo == null || metodo.getTipoPago() == null) {
            return false;
        }
        String normalizado = Normalizer.normalize(metodo.getTipoPago(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase();
        return normalizado.contains("credito");
    }

        private String formato(double valor) {
        return String.format(Locale.US, "%.2f", valor);
    }
 
    private void mostrarError(String titulo, String mensaje, SQLException e) {
        if ("45000".equals(e.getSQLState())) {
            Alertas.advertencia("No permitido", e.getMessage());
        } else {
            e.printStackTrace();
            Alertas.error(titulo, mensaje);
        }
    }

}
