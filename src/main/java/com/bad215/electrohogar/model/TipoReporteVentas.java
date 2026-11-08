package com.bad215.electrohogar.model;

public enum TipoReporteVentas {
    SUCURSAL("Sucursal", "sp_reporte_ventas_sucursal"),
    VENDEDOR("Vendedor", "sp_reporte_ventas_vendedor"),
    CATEGORIA("Categoría", "sp_reporte_ventas_categoria"),
    PROVEEDOR("Proveedor", "sp_reporte_ventas_proveedor");

    private final String etiqueta;
    private final String procedimiento;

    TipoReporteVentas(String etiqueta, String procedimiento) {
        this.etiqueta = etiqueta;
        this.procedimiento = procedimiento;
    }

    public String getProcedimiento() {
        return procedimiento;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
