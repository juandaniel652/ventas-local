package com.tuapp.stockapp.model;

import java.util.ArrayList;
import java.util.List;

public class GrupoHistorial {
    public String titulo; // Ejemplo: "Abril 2026"
    public double totalMensual;
    public List<String> detallesDias = new ArrayList<>();

    public GrupoHistorial(String titulo) { this.titulo = titulo; }
}
