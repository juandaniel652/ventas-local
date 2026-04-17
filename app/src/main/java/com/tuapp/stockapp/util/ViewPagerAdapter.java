package com.tuapp.stockapp.util;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.tuapp.stockapp.ui.InventarioFragment;
import com.tuapp.stockapp.ui.VentasFragment;
import com.tuapp.stockapp.ui.HistorialFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fa) { super(fa); }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new InventarioFragment();
            case 1: return new VentasFragment();
            case 2: return new HistorialFragment();
            default: return new InventarioFragment();
        }
    }

    @Override
    public int getItemCount() { return 3; }
}
