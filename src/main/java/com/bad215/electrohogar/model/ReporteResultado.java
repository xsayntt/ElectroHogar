package com.bad215.electrohogar.model;

import java.util.ArrayList;
import java.util.List;

public class ReporteResultado {
    private final List<String> columnas = new ArrayList<>();
    private final List<ReporteFila> filas = new ArrayList<>();

    public List<String> getColumnas() {
        return columnas;
    }

    public List<ReporteFila> getFilas() {
        return filas;
    }
}
