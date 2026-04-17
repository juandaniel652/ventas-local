package com.tuapp.stockapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tuapp.stockapp.R;
import com.tuapp.stockapp.database.ProductoDAO;
import com.tuapp.stockapp.model.Producto;
import com.tuapp.stockapp.util.ProductoAdapter;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ProductoDAO productoDAO;
    private RecyclerView rvProductos;
    private ProductoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        productoDAO = new ProductoDAO(this);
        productoDAO.abrir();

        rvProductos = findViewById(R.id.rvProductos);
        rvProductos.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fab = findViewById(R.id.fabAgregar);
        fab.setOnClickListener(v -> mostrarDialogoAgregar());

        actualizarLista();
    }

    private void mostrarDialogoAgregar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nuevo Producto");

        // Layout simple in-code o inflar uno rápido
        View view = LayoutInflater.from(this).inflate(android.R.layout.select_dialog_item, null); 
        // Para ir rápido, usamos inputs directos
        final EditText inputNombre = new EditText(this);
        inputNombre.setHint("Nombre del producto");
        
        builder.setView(inputNombre);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String nombre = inputNombre.getText().toString();
            if (!nombre.isEmpty()) {
                Producto p = new Producto(0, nombre, 100.0, 10); // Valores de ejemplo
                productoDAO.guardarProducto(p);
                actualizarLista();
            } else {
                Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void actualizarLista() {
        List<Producto> productos = productoDAO.obtenerTodos();
        adapter = new ProductoAdapter(productos);
        rvProductos.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        productoDAO.cerrar();
    }
}
