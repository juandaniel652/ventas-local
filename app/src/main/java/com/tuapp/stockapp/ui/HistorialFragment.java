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
        String sql = "SELECT v.id, p.nombre, v.cantidad, v.total FROM ventas v JOIN productos p ON v.producto_id = p.id WHERE date(v.fecha) = ?";
        Cursor c = db.getReadableDatabase().rawQuery(sql, new String[]{fechaIso});
        List<String> labels = new ArrayList<>();
        List<Integer> ids = new ArrayList<>();
        
        if (c.moveToFirst()) {
            do {
                ids.add(c.getInt(0));
                labels.add(c.getString(1) + " (x" + c.getInt(2) + ") - $" + String.format("%.2f", c.getDouble(3)));
            } while (c.moveToNext());
        }
        c.close();

        if (ids.isEmpty()) {
            cargarDatos();
            return;
        }

        new MaterialAlertDialogBuilder(requireContext())
            .setTitle("Ventas del " + fechaIso)
            .setItems(labels.toArray(new String[0]), (dialog, which) -> {
                anularVenta(ids.get(which), fechaIso);
            })
            .setNegativeButton("Cerrar", null).show();
    }

    private void anularVenta(int idVenta, String fechaIso) {
        new MaterialAlertDialogBuilder(requireContext())
            .setTitle("¿Anular esta venta?")
            .setPositiveButton("Anular", (d, w) -> {
                DbHelper db = new DbHelper(getContext());
                db.getWritableDatabase().delete("ventas", "id = ?", new String[]{String.valueOf(idVenta)});
                
                // REFRESCAR LOCAL (Historial)
                cargarDatos();
                
                // REFRESCAR GLOBAL (Pestaña HOY)
                // Buscamos el ViewPager en la Activity y notificamos que algo cambió
                if (getActivity() != null) {
                    ViewPager2 vp = getActivity().findViewById(R.id.viewPager);
                    if (vp != null && vp.getAdapter() != null) {
                        // Esto fuerza a que los fragmentos se revaliden al deslizar
                        vp.getAdapter().notifyDataSetChanged();
                    }
                }
                
                abrirDetalleVentas(fechaIso);
                Toast.makeText(getContext(), "Venta eliminada", Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("Cancelar", null).show();
    }

    @Override
    public void onResume() { super.onResume(); cargarDatos(); }

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
