package com.bad215.electrohogar.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class ReporteFila {
    private final Map<String, String> valores = new LinkedHashMap<>();

    public void agregar(String columna, String valor) {
        valores.put(columna, valor == null ? "" : valor);
    }

    public String get(String columna) {
        return valores.getOrDefault(columna, "");
    }
}
