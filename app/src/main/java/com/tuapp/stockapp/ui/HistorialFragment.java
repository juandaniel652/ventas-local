package com.tuapp.stockapp.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
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
    private String[] meses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        elv = new ExpandableListView(getContext());
        elv.setBackgroundColor(getResources().getColor(R.color.bg_dark));
        
        elv.setOnChildClickListener((parent, v, groupPos, childPos, id) -> {
            GrupoHistorial grupo = (GrupoHistorial) elv.getExpandableListAdapter().getGroup(groupPos);
            String detalle = grupo.detallesDias.get(childPos); 
            
            // Extraemos la fecha y el monto de: "Lun 17-04 -> $4500.00"
            try {
                String fechaDiaMes = detalle.split(" -> ")[0].split(" ")[1]; // "17-04"
                String montoActual = detalle.split("\\$")[1]; // "4500.00"
                
                String anio = grupo.titulo.split(" ")[1];
                String nombreMes = grupo.titulo.split(" ")[0];
                String dia = fechaDiaMes.split("-")[0];
                
                int mesIdx = 0;
                for(int i=0; i<meses.length; i++) {
                    if(meses[i].equals(nombreMes)) {
                        mesIdx = i + 1;
                        break;
                    }
                }
                String fechaIso = anio + "-" + String.format("%02d", mesIdx) + "-" + dia;
                mostrarDialogoEdicionVenta(fechaIso, montoActual);
            } catch (Exception e) {
                Toast.makeText(getContext(), "Error al procesar datos", Toast.LENGTH_SHORT).show();
            }
            return true;
        });

        return elv;
    }

    private void mostrarDialogoEdicionVenta(String fecha, String totalActual) {
        EditText et = new EditText(getContext());
        et.setText(totalActual);
        et.setPadding(50, 40, 50, 40);
        et.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);

        new AlertDialog.Builder(getContext())
            .setTitle("Ajuste de Caja")
            .setMessage("Modificando total del día: " + fecha)
            .setView(et)
            .setPositiveButton("Guardar Cambio", (d, w) -> {
                try {
                    double nuevoTotal = Double.parseDouble(et.getText().toString());
                    DbHelper db = new DbHelper(getContext());
                    // Distribuimos el nuevo total entre las entradas de ese día
                    db.getWritableDatabase().execSQL("UPDATE ventas SET total = (" + nuevoTotal + " / (SELECT COUNT(*) FROM ventas WHERE date(fecha) = '" + fecha + "')) WHERE date(fecha) = '" + fecha + "'");
                    cargarHistorial();
                    Toast.makeText(getContext(), "Historial actualizado", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error: valor no numérico", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Cancelar", null)
            .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarHistorial();
    }

    private void cargarHistorial() {
        Map<String, GrupoHistorial> mapaGrupos = new LinkedHashMap<>();
        String query = "SELECT " +
                "strftime('%m', fecha) as mes, strftime('%Y', fecha) as anio, " +
                "CASE strftime('%w', fecha) " +
                "WHEN '0' THEN 'Dom' WHEN '1' THEN 'Lun' WHEN '2' THEN 'Mar' " +
                "WHEN '3' THEN 'Mié' WHEN '4' THEN 'Jue' WHEN '5' THEN 'Vie' " +
                "WHEN '6' THEN 'Sáb' END || ' ' || strftime('%d-%m', fecha) as dia_arg, " +
                "SUM(total) as diario " +
                "FROM ventas GROUP BY date(fecha) ORDER BY fecha DESC";

        DbHelper helper = new DbHelper(getContext());
        Cursor c = helper.getReadableDatabase().rawQuery(query, null);

        if (c.moveToFirst()) {
            do {
                int mesNum = Integer.parseInt(c.getString(0)) - 1;
                String anio = c.getString(1);
                String nombreGrupo = meses[mesNum] + " " + anio;

                if (!mapaGrupos.containsKey(nombreGrupo)) {
                    mapaGrupos.put(nombreGrupo, new GrupoHistorial(nombreGrupo));
                }
                GrupoHistorial g = mapaGrupos.get(nombreGrupo);
                g.detallesDias.add(c.getString(2) + " -> $" + String.format("%.2f", c.getDouble(3)));
                g.totalMensual += c.getDouble(3);
            } while (c.moveToNext());
        }
        c.close();
        elv.setAdapter(new HistorialExpandableAdapter(new ArrayList<>(mapaGrupos.values())));
    }
}
