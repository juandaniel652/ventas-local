package com.tuapp.stockapp.util;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.tuapp.stockapp.R;
import com.tuapp.stockapp.model.Producto;
import java.util.List;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder> {

    private List<Producto> listaProductos;
    private OnProductoListener listener;

    public interface OnProductoListener {
        void onVenderClick(Producto producto);
        void onBorrarClick(Producto producto);
    }

    public ProductoAdapter(List<Producto> listaProductos, OnProductoListener listener) {
        this.listaProductos = listaProductos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_producto, parent, false);
        return new ProductoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position) {
        Producto p = listaProductos.get(position);
        holder.tvNombre.setText(p.getNombre());
        holder.tvStock.setText("Stock: " + p.getStock());
        holder.tvPrecio.setText("$ " + p.getPrecio());

        holder.btnVender.setOnClickListener(v -> listener.onVenderClick(p));
        holder.btnBorrar.setOnClickListener(v -> listener.onBorrarClick(p));
    }

    @Override
    public int getItemCount() { return listaProductos.size(); }

    public static class ProductoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvStock, tvPrecio;
        Button btnVender;
        ImageButton btnBorrar;

        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.itemNombre);
            tvStock = itemView.findViewById(R.id.itemStock);
            tvPrecio = itemView.findViewById(R.id.itemPrecio);
            btnVender = itemView.findViewById(R.id.btnVender);
            btnBorrar = itemView.findViewById(R.id.btnBorrar);
        }
    }
}
