package com.tuapp.stockapp.util;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import com.tuapp.stockapp.R;
import com.tuapp.stockapp.model.GrupoHistorial;
import java.util.List;

public class HistorialExpandableAdapter extends BaseExpandableListAdapter {
    private List<GrupoHistorial> grupos;

    public HistorialExpandableAdapter(List<GrupoHistorial> grupos) { this.grupos = grupos; }

    @Override
    public int getGroupCount() { return grupos.size(); }
    @Override
    public int getChildrenCount(int g) { return grupos.get(g).detallesDias.size(); }
    @Override
    public Object getGroup(int g) { return grupos.get(g); }
    @Override
    public Object getChild(int g, int c) { return grupos.get(g).detallesDias.get(c); }
    @Override
    public long getGroupId(int g) { return g; }
    @Override
    public long getChildId(int g, int c) { return c; }
    @Override
    public boolean hasStableIds() { return true; }

    @Override
    public View getGroupView(int g, boolean isExpanded, View v, ViewGroup p) {
        if (v == null) v = LayoutInflater.from(p.getContext()).inflate(android.R.layout.simple_expandable_list_item_1, p, false);
        TextView tv = v.findViewById(android.R.id.text1);
        GrupoHistorial grupo = grupos.get(g);
        tv.setText(grupo.titulo.toUpperCase() + "  |  Total: $" + String.format("%.2f", grupo.totalMensual));
        tv.setTextColor(p.getContext().getResources().getColor(R.color.primary_accent));
        v.setBackgroundColor(p.getContext().getResources().getColor(R.color.surface_dark));
        return v;
    }

    @Override
    public View getChildView(int g, int c, boolean isLast, View v, ViewGroup p) {
        if (v == null) v = LayoutInflater.from(p.getContext()).inflate(android.R.layout.simple_list_item_1, p, false);
        TextView tv = v.findViewById(android.R.id.text1);
        tv.setText(grupos.get(g).detallesDias.get(c));
        tv.setTextColor(p.getContext().getResources().getColor(R.color.text_medium_emphasis));
        tv.setPadding(80, 20, 20, 20);
        return v;
    }

    @Override
    public boolean isChildSelectable(int g, int c) { return true; }
}
