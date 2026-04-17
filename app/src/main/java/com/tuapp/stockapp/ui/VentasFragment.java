package com.tuapp.stockapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.tuapp.stockapp.R;
import com.tuapp.stockapp.database.ProductoDAO;
import java.util.List;

public class VentasFragment extends Fragment {
    private ProductoDAO dao;
    private TextView tvTotal;
    private ListView lvVentas;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ventas, container, false);
        dao = new ProductoDAO(getContext());
        tvTotal = view.findViewById(R.id.tvTotalHoy);
        
        // Vamos a usar un ListView simple para el historial rápido de hoy
        lvVentas = new ListView(getContext());
        ((ViewGroup)view).addView(lvVentas); 
        
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        actualizarDatos();
    }

    public void actualizarDatos() {
        double total = dao.obtenerTotalVentasHoy();
        tvTotal.setText("$ " + String.format("%.2f", total));
        
        List<String> lista = dao.obtenerResumenVentasHoy();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, lista);
        lvVentas.setAdapter(adapter);
    }
}
