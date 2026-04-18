package com.tuapp.stockapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tuapp.stockapp.R;
import com.tuapp.stockapp.database.ProductoDAO;
import com.tuapp.stockapp.model.Producto;
import com.tuapp.stockapp.util.ProductoAdapter;
import java.util.List;

public class InventarioFragment extends Fragment {
    private ProductoDAO dao;
    private RecyclerView rv;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inventario, container, false);
        dao = new ProductoDAO(getContext());
        rv = view.findViewById(R.id.rvInventario);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        
        FloatingActionButton fab = view.findViewById(R.id.fabAgregar);
        if (fab != null) {
            fab.setOnClickListener(v -> mostrarDialogoProducto(null));
        }

        actualizarLista();
        return view;
    }

    private void actualizarLista() {
        List<Producto> lista = dao.obtenerTodos();
        rv.setAdapter(new ProductoAdapter(lista, new ProductoAdapter.OnProductoListener() {
            @Override
            public void onVenderClick(Producto p) {
                if (p.getStock() > 0) {
                    // Venta rápida: descuenta 1 y registra
                    dao.registrarVenta(p.getId(), 1, p.getPrecio());
                    actualizarLista();
                    Toast.makeText(getContext(), "Vendido: " + p.getNombre(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Sin stock de " + p.getNombre(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onBorrarClick(Producto p) {
                confirmarEliminacion(p);
            }

            @Override
            public void onEditarClick(Producto p) {
                mostrarDialogoProducto(p);
            }
        }));
    }

    private void mostrarDialogoProducto(@Nullable Producto p) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.dialog_producto, null);
        EditText etNom = v.findViewById(R.id.etNombre);
        EditText etStock = v.findViewById(R.id.etStock);
        EditText etPrecio = v.findViewById(R.id.etPrecio);

        if (p != null) {
            etNom.setText(p.getNombre());
            etStock.setText(String.valueOf(p.getStock()));
            etPrecio.setText(String.valueOf(p.getPrecio()));
        }

        new MaterialAlertDialogBuilder(requireContext())
            .setTitle(p == null ? "Nuevo Producto" : "Editar Producto")
            .setView(v)
            .setPositiveButton("Guardar", (d, w) -> {
                try {
                    String nombre = etNom.getText().toString();
                    int stock = Integer.parseInt(etStock.getText().toString());
                    double precio = Double.parseDouble(etPrecio.getText().toString());
                    if (p == null) dao.insertar(new Producto(0, nombre, stock, precio));
                    else dao.actualizar(new Producto(p.getId(), nombre, stock, precio));
                    actualizarLista();
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error en los datos", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Cancelar", null).show();
    }

    private void confirmarEliminacion(Producto p) {
        if (dao.tieneVentas(p.getId())) {
            new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Acción Protegida")
                .setMessage("El producto '" + p.getNombre() + "' tiene ventas registradas. No se puede eliminar para no romper el historial. Bajale el stock a 0 si ya no lo vendés.")
                .setPositiveButton("Entendido", null).show();
        } else {
            new MaterialAlertDialogBuilder(requireContext())
                .setTitle("¿Eliminar " + p.getNombre() + "?")
                .setPositiveButton("Eliminar", (d, w) -> {
                    dao.eliminar(p.getId());
                    actualizarLista();
                })
                .setNegativeButton("Cancelar", null).show();
        }
    }

    @Override
    public void onResume() { super.onResume(); actualizarLista(); }
}
