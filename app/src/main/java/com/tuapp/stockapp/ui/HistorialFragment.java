package com.tuapp.stockapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.tuapp.stockapp.database.ProductoDAO;
import java.util.ArrayList;
import java.util.List;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class HistorialFragment extends Fragment {
    private ListView lvHistorial;
    private ProductoDAO dao;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        lvHistorial = new ListView(getContext());
        dao = new ProductoDAO(getContext());
        return lvHistorial;
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarHistorial();
    }

    private void cargarHistorial() {
        List<String> datos = new ArrayList<>();
        // Consulta Pro: Agrupa ventas por día y suma el total
        String query = "SELECT date(fecha) as dia, SUM(total) FROM ventas GROUP BY dia ORDER BY dia DESC";
        
        // Usamos una consulta directa para el reporte rápido
        android.database.sqlite.SQLiteOpenHelper helper = new com.tuapp.stockapp.database.DbHelper(getContext());
        Cursor c = helper.getReadableDatabase().rawQuery(query, null);
        
        if (c.moveToFirst()) {
            do {
                datos.add("Día: " + c.getString(0) + "  |  Total: $" + String.format("%.2f", c.getDouble(1)));
            } while (c.moveToNext());
        }
        c.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, datos);
        lvHistorial.setAdapter(adapter);
    }
}
