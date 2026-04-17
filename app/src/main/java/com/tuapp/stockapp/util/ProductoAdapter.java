package com.tuapp.stockapp.util;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.tuapp.stockapp.R;
import com.tuapp.stockapp.model.Producto;
import java.util.List;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ViewHolder> {

    private List<Producto> lista;
    private OnProductoListener listener;

    public interface OnProductoListener {
        void onVenderClick(Producto p);
        void onBorrarClick(Producto p);
        void onEditarClick(Producto p); // Nuevo método para edición
    }

    public ProductoAdapter(List<Producto> lista, OnProductoListener listener) {
        this.lista = lista;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_producto, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Producto p = lista.get(position);
        holder.tvN.setText(p.getNombre());
        holder.tvS.setText("Stock: " + p.getStock());
        holder.tvP.setText("$" + p.getPrecio());

        holder.btnV.setOnClickListener(v -> listener.onVenderClick(p));
        holder.btnB.setOnClickListener(v -> listener.onBorrarClick(p));
        
        // Al tocar cualquier parte del texto, editamos
        holder.itemView.setOnClickListener(v -> listener.onEditarClick(p));
    }

    @Override
    public int getItemCount() { return lista.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvN, tvS, tvP;
        Button btnV, btnB;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvN = itemView.findViewById(R.id.tvNombre);
            tvS = itemView.findViewById(R.id.tvStock);
            tvP = itemView.findViewById(R.id.tvPrecio);
            btnV = itemView.findViewById(R.id.btnVender);
            btnB = itemView.findViewById(R.id.btnBorrar);
        }
    }
}
