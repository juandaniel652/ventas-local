package com.tuapp.stockapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tuapp.stockapp.R;
import com.tuapp.stockapp.database.ProductoDAO;
import com.tuapp.stockapp.model.Producto;
import com.tuapp.stockapp.util.ProductoAdapter;
import java.util.List;

public class InventarioFragment extends Fragment implements ProductoAdapter.OnProductoListener {

    private ProductoDAO dao;
    private ProductoAdapter adapter;
    private RecyclerView rv;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inventario, container, false);
        dao = new ProductoDAO(getContext());
        rv = view.findViewById(R.id.rvInventario);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        
        view.findViewById(R.id.fabAgregar).setOnClickListener(v -> mostrarDialogo(null));

        actualizarLista();
        return view;
    }

    public void actualizarLista() {
        adapter = new ProductoAdapter(dao.obtenerTodos(), this);
        rv.setAdapter(adapter);
    }

    private void mostrarDialogo(@Nullable Producto p) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.dialog_producto, null);
        EditText etN = v.findViewById(R.id.etNombre);
        EditText etS = v.findViewById(R.id.etStock);
        EditText etP = v.findViewById(R.id.etPrecio);

        if (p != null) {
            etN.setText(p.getNombre());
            etS.setText(String.valueOf(p.getStock()));
            etP.setText(String.valueOf(p.getPrecio()));
        }

        new AlertDialog.Builder(getContext())
            .setTitle(p == null ? "Nuevo Producto" : "Editar Producto")
            .setView(v)
            .setPositiveButton("Guardar", (d, w) -> {
                if (p == null) {
                    dao.insertar(new Producto(0, etN.getText().toString(), Integer.parseInt(etS.getText().toString()), Double.parseDouble(etP.getText().toString())));
                } else {
                    // Aquí podrías agregar dao.actualizar() si lo necesitas, pero por ahora vamos con la venta
                }
                actualizarLista();
            }).show();
    }

    @Override
    public void onVenderClick(Producto p) {
        if (p.getStock() > 0) {
            dao.registrarVenta(p.getId(), 1, p.getPrecio());
            actualizarLista();
            Toast.makeText(getContext(), "Venta registrada: " + p.getNombre(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Sin stock!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBorrarClick(Producto p) {
        dao.eliminar(p.getId());
        actualizarLista();
    }
}
