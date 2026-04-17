package com.tuapp.stockapp.util;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.tuapp.stockapp.ui.InventarioFragment;
import com.tuapp.stockapp.ui.VentasFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return (position == 0) ? new InventarioFragment() : new VentasFragment();
    }

    @Override
    public int getItemCount() { return 2; }
}
