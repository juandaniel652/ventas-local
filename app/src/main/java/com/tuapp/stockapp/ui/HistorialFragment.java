package com.tuapp.stockapp.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.tuapp.stockapp.R;
import com.tuapp.stockapp.database.DbHelper;
import com.tuapp.stockapp.model.GrupoHistorial;
import com.tuapp.stockapp.util.HistorialExpandableAdapter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HistorialFragment extends Fragment {
    private ExpandableListView elv;
    private final String[] meses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        elv = new ExpandableListView(getContext());
        elv.setBackgroundColor(getResources().getColor(R.color.bg_dark));
        elv.setDividerHeight(2);
        
        elv.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            GrupoHistorial grupo = (GrupoHistorial) elv.getExpandableListAdapter().getGroup(groupPosition);
            if (childPosition < grupo.fechasReales.size()) {
                abrirDetalleVentas(grupo.fechasReales.get(childPosition));
            }
            return true;
        });
        return elv;
    }

    private void abrirDetalleVentas(String fechaIso) {
        DbHelper db = new DbHelper(getContext());
        
        // Agrupamos por producto y consolidamos las ventas del día
        String sql = "SELECT group_concat(v.id), p.nombre, SUM(v.cantidad), SUM(v.total) " +
                     "FROM ventas v " +
                     "JOIN productos p ON v.producto_id = p.id " +
                     "WHERE date(v.fecha) = ? " +
                     "GROUP BY v.producto_id";
                     
        Cursor c = db.getReadableDatabase().rawQuery(sql, new String[]{fechaIso});
        List<String> labels = new ArrayList<>();
        List<String> concatenatedIds = new ArrayList<>();
        
        if (c.moveToFirst()) {
            do {
                concatenatedIds.add(c.getString(0)); // Lista de IDs separados por coma (ej: "1,4")
                labels.add(c.getString(1) + " (x" + c.getInt(2) + ") - $" + String.format("%.2f", c.getDouble(3)));
            } while (c.moveToNext());
        }
        c.close();

        if (concatenatedIds.isEmpty()) {
            cargarDatos();
            return;
        }

        // --- CONVERSIÓN DE FECHA AMIGABLE ---
        String fechaFormateada = fechaIso; // Fallback por si falla el parseo
        try {
            java.text.SimpleDateFormat formatoEntrada = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US);
            java.text.SimpleDateFormat formatoSalida = new java.text.SimpleDateFormat("d 'de' MMMM 'de' yyyy", new java.util.Locale("es", "AR"));
            java.util.Date fecha = formatoEntrada.parse(fechaIso);
            if (fecha != null) {
                fechaFormateada = formatoSalida.format(fecha);
                // Forzar la primera letra en mayúscula (ej: "29 de Junio...")
                if (fechaFormateada.contains(" de ")) {
                    String[] partes = fechaFormateada.split(" de ");
                    if (partes.length > 1) {
                        partes[1] = partes[1].substring(0, 1).toUpperCase() + partes[1].substring(1);
                        fechaFormateada = partes[0] + " de " + partes[1] + " de " + partes[2];
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // ------------------------------------

        new MaterialAlertDialogBuilder(requireContext())
            .setTitle("Ventas del " + fechaFormateada) // <-- Usamos la fecha limpia acá
            .setItems(labels.toArray(new String[0]), (dialog, which) -> {
                anularVenta(concatenatedIds.get(which), fechaIso); // Seguimos pasando fechaIso a anularVenta para que la query no se rompa
            })
            .setNegativeButton("Cerrar", null).show();
    }

    private void anularVenta(String idsVenta, String fechaIso) {
        new MaterialAlertDialogBuilder(requireContext())
            .setTitle("¿Anular estas ventas?")
            .setMessage("Se darán de baja los registros agrupados de este producto en el día.")
            .setPositiveButton("Anular", (d, w) -> {
                DbHelper db = new DbHelper(getContext());
                // Borramos todos los IDs que pertenecen a esa agrupación del día
                String sqlDelete = "DELETE FROM ventas WHERE id IN (" + idsVenta + ")";
                db.getWritableDatabase().execSQL(sqlDelete);
                
                // Refrescar lista mensual
                cargarDatos();
                
                // Refrescar pestaña de ventas de Hoy por si Maxi está parado ahí
                if (getActivity() != null) {
                    ViewPager2 vp = getActivity().findViewById(R.id.viewPager);
                    if (vp != null && vp.getAdapter() != null) {
                        vp.getAdapter().notifyDataSetChanged();
                    }
                }
                
                // Volver a abrir el diálogo actualizado
                abrirDetalleVentas(fechaIso);
                Toast.makeText(getContext(), "Venta eliminada", Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("Cancelar", null).show();
    }

    @Override
    public void onResume() { 
        super.onResume(); 
        cargarDatos(); 
    }

    private void cargarDatos() {
        Map<String, GrupoHistorial> mapa = new LinkedHashMap<>();
        String query = "SELECT strftime('%m', fecha), strftime('%Y', fecha), " +
                "CASE strftime('%w', fecha) WHEN '0' THEN 'Dom' WHEN '1' THEN 'Lun' WHEN '2' THEN 'Mar' WHEN '3' THEN 'Mié' WHEN '4' THEN 'Jue' WHEN '5' THEN 'Vie' WHEN '6' THEN 'Sáb' END " +
                "|| ' ' || strftime('%d-%m', fecha), SUM(total), date(fecha) " +
                "FROM ventas GROUP BY date(fecha) ORDER BY fecha DESC";

        try {
            Cursor c = new DbHelper(getContext()).getReadableDatabase().rawQuery(query, null);
            if (c.moveToFirst()) {
                do {
                    int mesIdx = Integer.parseInt(c.getString(0)) - 1;
                    String mesAnio = meses[mesIdx] + " " + c.getString(1);
                    if (!mapa.containsKey(mesAnio)) mapa.put(mesAnio, new GrupoHistorial(mesAnio));
                    GrupoHistorial g = mapa.get(mesAnio);
                    g.detallesDias.add(c.getString(2) + " -> $" + String.format("%.2f", c.getDouble(3)));
                    g.fechasReales.add(c.getString(4));
                    g.totalMensual += c.getDouble(3);
                } while (c.moveToNext());
            }
            c.close();
            if (elv != null) elv.setAdapter(new HistorialExpandableAdapter(new ArrayList<>(mapa.values())));
        } catch (Exception e) { }
    }
}