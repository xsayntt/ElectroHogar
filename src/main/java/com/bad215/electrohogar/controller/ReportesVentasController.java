package com.bad215.electrohogar.controller;

import com.bad215.electrohogar.dao.ReporteDao;
import com.bad215.electrohogar.model.EmpleadoReporte;
import com.bad215.electrohogar.model.ReporteFila;
import com.bad215.electrohogar.model.ReporteResultado;
import com.bad215.electrohogar.model.TipoReporteVentas;
import com.bad215.electrohogar.utils.Alertas;
import com.bad215.electrohogar.utils.Sesion;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ReportesVentasController implements Initializable {
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_ARCHIVO = DateTimeFormatter.ofPattern("yyyyMMdd");

    @FXML private ComboBox<String> cmbPeriodo;
    @FXML private DatePicker dpDesde;
    @FXML private DatePicker dpHasta;
    @FXML private ComboBox<TipoReporteVentas> cmbTipoVentas;
    @FXML private ComboBox<EmpleadoReporte> cmbEmpleado;
    @FXML private TableView<ReporteFila> tblVistaPrevia;
    @FXML private Label lblTituloVista;
    @FXML private Label lblEstado;
    @FXML private Button btnExportarVentas;
    @FXML private Button btnExportarAnulaciones;

    private final ReporteDao reporteDao = new ReporteDao();
    private ReporteResultado resultadoActual;
    private String tituloActual;
    private LocalDate desdeActual;
    private LocalDate hastaActual;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cmbPeriodo.setItems(FXCollections.observableArrayList(
                "Hoy", "Últimos 7 días", "Últimos 30 días", "Mes actual", "Personalizado"));
        cmbPeriodo.getSelectionModel().selectedItemProperty().addListener(
                (obs, anterior, nuevo) -> aplicarPeriodo(nuevo));
        cmbTipoVentas.setItems(FXCollections.observableArrayList(TipoReporteVentas.values()));
        cmbTipoVentas.getSelectionModel().select(TipoReporteVentas.SUCURSAL);
        cmbPeriodo.getSelectionModel().select("Mes actual");
        limpiarVista();
        cargarEmpleados();
    }

    private void aplicarPeriodo(String periodo) {
        if (periodo == null || dpDesde == null || dpHasta == null) {
            return;
        }
        LocalDate hoy = LocalDate.now();
        switch (periodo) {
            case "Hoy":
                dpDesde.setValue(hoy);
                dpHasta.setValue(hoy);
                break;
            case "Últimos 7 días":
                dpDesde.setValue(hoy.minusDays(6));
                dpHasta.setValue(hoy);
                break;
            case "Últimos 30 días":
                dpDesde.setValue(hoy.minusDays(29));
                dpHasta.setValue(hoy);
                break;
            case "Mes actual":
                dpDesde.setValue(hoy.withDayOfMonth(1));
                dpHasta.setValue(hoy);
                break;
            default:
                break;
        }
    }

    private void cargarEmpleados() {
        try {
            List<EmpleadoReporte> empleados = new ArrayList<>();
            empleados.add(new EmpleadoReporte(null, "Todos los empleados"));
            empleados.addAll(reporteDao.listarEmpleados(Sesion.getIdUsuario()));
            cmbEmpleado.setItems(FXCollections.observableArrayList(empleados));
            cmbEmpleado.getSelectionModel().selectFirst();
        } catch (SQLException e) {
            mostrarError("Reportes", "No se pudieron cargar los empleados.", e);
            btnExportarAnulaciones.setDisable(true);
        }
    }

    @FXML
    private void aplicarFiltros() {
        LocalDate[] periodo = obtenerPeriodoValido();
        if (periodo == null) {
            return;
        }
        TipoReporteVentas tipo = cmbTipoVentas.getValue();
        if (tipo == null) {
            Alertas.advertencia("Reportes", "Selecciona un tipo de reporte de ventas.");
            return;
        }
        try {
            resultadoActual = reporteDao.ventas(Sesion.getIdUsuario(), periodo[0], periodo[1], tipo);
            desdeActual = periodo[0];
            hastaActual = periodo[1];
            tituloActual = "Ventas por " + tipo.toString().toLowerCase(Locale.forLanguageTag("es-SV"));
            mostrarResultado(resultadoActual, tituloActual);
        } catch (SQLException e) {
            mostrarError("Reporte de ventas", "No se pudo cargar el reporte seleccionado.", e);
        }
    }

    @FXML
    private void vistaPreviaAnulaciones() {
        cargarAnulaciones(false);
    }

    @FXML
    private void exportarVentasPdf() {
        LocalDate[] periodo = obtenerPeriodoValido();
        if (periodo == null) {
            return;
        }
        TipoReporteVentas tipo = cmbTipoVentas.getValue();
        if (tipo == null) {
            Alertas.advertencia("Reportes", "Selecciona un tipo de reporte de ventas.");
            return;
        }
        try {
            resultadoActual = reporteDao.ventas(Sesion.getIdUsuario(), periodo[0], periodo[1], tipo);
            desdeActual = periodo[0];
            hastaActual = periodo[1];
            tituloActual = "Ventas por " + tipo.toString().toLowerCase(Locale.forLanguageTag("es-SV"));
            mostrarResultado(resultadoActual, tituloActual);
            guardarPdf("Reporte_ventas_" + tipo.name().toLowerCase(Locale.ROOT),
                    tituloActual, periodo[0], periodo[1]);
        } catch (SQLException e) {
            mostrarError("Reporte de ventas", "No se pudo generar el reporte seleccionado.", e);
        }
    }

    @FXML
    private void exportarAnulacionesPdf() {
        cargarAnulaciones(true);
    }

    private void cargarAnulaciones(boolean exportar) {
        LocalDate[] periodo = obtenerPeriodoValido();
        if (periodo == null) {
            return;
        }
        EmpleadoReporte empleado = cmbEmpleado.getValue();
        Integer idEmpleado = empleado == null ? null : empleado.getIdEmpleado();
        try {
            resultadoActual = reporteDao.anulaciones(Sesion.getIdUsuario(), periodo[0], periodo[1], idEmpleado);
            desdeActual = periodo[0];
            hastaActual = periodo[1];
            String nombreEmpleado = empleado == null ? "Todos los empleados" : empleado.toString();
            tituloActual = "Facturas anuladas - " + nombreEmpleado;
            mostrarResultado(resultadoActual, tituloActual);
            if (exportar) {
                guardarPdf("Reporte_facturas_anuladas", tituloActual, periodo[0], periodo[1]);
            }
        } catch (SQLException e) {
            mostrarError("Facturas anuladas", "No se pudo cargar el historial de anulaciones.", e);
        }
    }

    @FXML
    private void limpiarCampos() {
        cmbPeriodo.getSelectionModel().select("Mes actual");
        cmbTipoVentas.getSelectionModel().select(TipoReporteVentas.SUCURSAL);
        if (!cmbEmpleado.getItems().isEmpty()) {
            cmbEmpleado.getSelectionModel().selectFirst();
        }
        aplicarPeriodo("Mes actual");
        limpiarVista();
    }

    private LocalDate[] obtenerPeriodoValido() {
        LocalDate desde = dpDesde.getValue();
        LocalDate hasta = dpHasta.getValue();
        if (desde == null || hasta == null) {
            Alertas.advertencia("Fechas requeridas", "Selecciona las fechas Desde y Hasta.");
            return null;
        }
        if (desde.isAfter(hasta)) {
            Alertas.advertencia("Rango inválido", "La fecha Desde no puede ser posterior a Hasta.");
            return null;
        }
        return new LocalDate[]{desde, hasta};
    }

    private void mostrarResultado(ReporteResultado resultado, String titulo) {
        tblVistaPrevia.getColumns().clear();
        for (String clave : resultado.getColumnas()) {
            TableColumn<ReporteFila, String> columna = new TableColumn<>(etiquetaColumna(clave));
            columna.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().get(clave)));
            columna.setMinWidth(100);
            columna.setPrefWidth(140);
            tblVistaPrevia.getColumns().add(columna);
        }
        tblVistaPrevia.setItems(FXCollections.observableArrayList(resultado.getFilas()));
        lblTituloVista.setText(titulo);
        lblEstado.setText(resultado.getFilas().size() + " registro(s)");
        btnExportarVentas.setDisable(false);
        btnExportarAnulaciones.setDisable(false);
    }

    private void limpiarVista() {
        resultadoActual = null;
        tituloActual = null;
        desdeActual = null;
        hastaActual = null;
        tblVistaPrevia.getColumns().clear();
        tblVistaPrevia.getItems().clear();
        lblTituloVista.setText("Vista previa");
        lblEstado.setText("Selecciona un reporte y aplica los filtros.");
        btnExportarVentas.setDisable(false);
        btnExportarAnulaciones.setDisable(false);
    }

    private String etiquetaColumna(String nombre) {
        switch (nombre.toUpperCase(Locale.ROOT)) {
            case "ID_SUCURSAL": return "ID sucursal";
            case "NOMBRE_SUCURSAL": return "Sucursal";
            case "TOTAL_FACTURAS": return "Facturas";
            case "SUBTOTAL_VENTAS": return "Subtotal ventas";
            case "TOTAL_VENTAS": return "Total ventas";
            case "ID_EMPLEADO": return "ID empleado";
            case "NOMBRE_VENDEDOR": return "Vendedor";
            case "META_MENSUAL": return "Meta mensual";
            case "PORCENTAJE_COMISION": return "Comisión (%)";
            case "TOTAL_VENDIDO": return "Total vendido";
            case "ID_CATEGORIA": return "ID categoría";
            case "NOMBRE_CATEGORIA": return "Categoría";
            case "UNIDADES_VENDIDAS": return "Unidades vendidas";
            case "ID_PROVEEDOR": return "ID proveedor";
            case "NOMBRE_PROVEEDOR": return "Proveedor";
            case "ID_BIT_ANULACION": return "ID anulación";
            case "NUMERO_FACTURA": return "N° factura";
            case "FECHA_ANULACION": return "Fecha anulación";
            case "MOTIVO_ANULACION": return "Motivo";
            case "EMPLEADO_EMISOR": return "Empleado emisor";
            case "TOTAL_FACTURA_ANULADA": return "Total factura";
            default: return nombre.replace('_', ' ');
        }
    }

    private void guardarPdf(String nombreSugerido, String titulo, LocalDate desde, LocalDate hasta) {
        if (resultadoActual == null) {
            Alertas.advertencia("Exportar PDF", "Primero carga un reporte.");
            return;
        }
        FileChooser selector = new FileChooser();
        selector.setTitle("Guardar reporte como PDF");
        selector.setInitialFileName(nombreSugerido + "_" + FORMATO_ARCHIVO.format(desde)
                + "_" + FORMATO_ARCHIVO.format(hasta) + ".pdf");
        selector.getExtensionFilters().add(new FileChooser.ExtensionFilter("Documento PDF", "*.pdf"));
        File archivo = selector.showSaveDialog(tblVistaPrevia.getScene().getWindow());
        if (archivo == null) {
            return;
        }
        if (!archivo.getName().toLowerCase(Locale.ROOT).endsWith(".pdf")) {
            archivo = new File(archivo.getParentFile(), archivo.getName() + ".pdf");
        }

        Document documento = new Document(PageSize.A4.rotate(), 28, 28, 28, 28);
        boolean guardado = false;
        try (FileOutputStream salida = new FileOutputStream(archivo)) {
            PdfWriter.getInstance(documento, salida);
            documento.open();
            Font tituloPdf = new Font(Font.HELVETICA, 16, Font.BOLD);
            Font textoPdf = new Font(Font.HELVETICA, 9, Font.NORMAL);
            Font encabezadoPdf = new Font(Font.HELVETICA, 9, Font.BOLD);
            documento.add(new Paragraph("ElectroHogar - Reportes", tituloPdf));
            documento.add(new Paragraph(titulo, textoPdf));
            documento.add(new Paragraph("Período: " + FORMATO_FECHA.format(desde)
                    + " al " + FORMATO_FECHA.format(hasta), textoPdf));
            documento.add(new Paragraph(" "));

            int cantidadColumnas = resultadoActual.getColumnas().size();
            if (cantidadColumnas > 0) {
                PdfPTable tabla = new PdfPTable(cantidadColumnas);
                tabla.setWidthPercentage(100);
                for (String columna : resultadoActual.getColumnas()) {
                    PdfPCell celda = new PdfPCell(new Phrase(etiquetaColumna(columna), encabezadoPdf));
                    celda.setBackgroundColor(new java.awt.Color(221, 229, 238));
                    tabla.addCell(celda);
                }
                for (ReporteFila fila : resultadoActual.getFilas()) {
                    for (String columna : resultadoActual.getColumnas()) {
                        tabla.addCell(new Phrase(fila.get(columna), textoPdf));
                    }
                }
                documento.add(tabla);
            }
            documento.add(new Paragraph("Registros: " + resultadoActual.getFilas().size(), textoPdf));
            documento.close();
            guardado = true;
        } catch (DocumentException | IOException e) {
            Alertas.error("Exportar PDF", "No se pudo guardar el archivo: " + e.getMessage());
        } finally {
            if (documento.isOpen()) {
                documento.close();
            }
        }
        if (guardado) {
            Alertas.info("Exportación completada", "El PDF se guardó en:\n" + archivo.getAbsolutePath());
        }
    }

    private void mostrarError(String titulo, String mensaje, SQLException e) {
        if ("45000".equals(e.getSQLState())) {
            Alertas.advertencia("No permitido", e.getMessage());
        } else {
            e.printStackTrace();
            Alertas.error(titulo, mensaje + "\n" + e.getMessage());
        }
    }
}
