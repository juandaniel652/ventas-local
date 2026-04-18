package com.tuapp.stockapp.model;

import java.util.ArrayList;
import java.util.List;

public class GrupoHistorial {
    public String titulo;
    public double totalMensual;
    public List<String> detallesDias = new ArrayList<>();
    public List<String> fechasReales = new ArrayList<>();

    public GrupoHistorial(String titulo) {
        this.titulo = titulo;
        this.totalMensual = 0;
    }
}
