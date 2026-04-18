package com.tuapp.stockapp.util;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import com.tuapp.stockapp.model.GrupoHistorial;
import com.tuapp.stockapp.R;

import java.util.List;

public class HistorialExpandableAdapter extends BaseExpandableListAdapter {
    private List<GrupoHistorial> grupos;

    public HistorialExpandableAdapter(List<GrupoHistorial> grupos) {
        this.grupos = grupos;
    }

    @Override
    public int getGroupCount() { return grupos.size(); }

    @Override
    public int getChildrenCount(int groupPosition) { 
        return grupos.get(groupPosition).detallesDias.size(); 
    }

    @Override
    public Object getGroup(int groupPosition) { return grupos.get(groupPosition); }

    @Override
    public Object getChild(int groupPosition, int childPosition) { 
        return grupos.get(groupPosition).detallesDias.get(childPosition); 
    }

    @Override
    public long getGroupId(int groupPosition) { return groupPosition; }

    @Override
    public long getChildId(int groupPosition, int childPosition) { return childPosition; }

    @Override
    public boolean hasStableIds() { return true; }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        TextView tv = new TextView(parent.getContext());
        GrupoHistorial g = grupos.get(groupPosition);
        tv.setText(g.titulo + " - Total: $" + String.format("%.2f", g.totalMensual));
        tv.setPadding(60, 40, 20, 40);
        tv.setTextSize(18);
        tv.setTextColor(parent.getContext().getResources().getColor(R.color.primary_accent));
        tv.setBackgroundColor(parent.getContext().getResources().getColor(R.color.surface_dark));
        return tv;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        TextView tv = new TextView(parent.getContext());
        tv.setText(grupos.get(groupPosition).detallesDias.get(childPosition));
        tv.setPadding(80, 30, 20, 30);
        tv.setTextSize(16);
        tv.setTextColor(parent.getContext().getResources().getColor(R.color.white));
        tv.setBackgroundColor(parent.getContext().getResources().getColor(R.color.bg_dark));
        return tv;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) { return true; }
}
