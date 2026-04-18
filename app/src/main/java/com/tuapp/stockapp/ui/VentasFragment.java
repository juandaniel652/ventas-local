package com.tuapp.stockapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.LinearLayout;
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
        
        // Buscamos el contenedor principal del fragment para meter el ListView
        // Si el root de fragment_ventas es un LinearLayout o RelativeLayout, funcionará
        if (view instanceof ViewGroup) {
            lvVentas = new ListView(getContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 
                ViewGroup.LayoutParams.WRAP_CONTENT
            );
            lvVentas.setLayoutParams(lp);
            ((ViewGroup)view).addView(lvVentas);
        }
        
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        actualizarDatos();
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (menuVisible && isResumed()) {
            actualizarDatos();
        }
    }

    public void actualizarDatos() {
        if (dao == null && getContext() != null) dao = new ProductoDAO(getContext());
        if (dao != null && tvTotal != null) {
            double total = dao.obtenerTotalVentasHoy();
            tvTotal.setText("$ " + String.format("%.2f", total));
            
            List<String> lista = dao.obtenerResumenVentasHoy();
            if (getContext() != null && lvVentas != null) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, lista);
                lvVentas.setAdapter(adapter);
            }
        }
    }
}
