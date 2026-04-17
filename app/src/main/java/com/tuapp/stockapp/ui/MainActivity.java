package com.tuapp.stockapp.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.tuapp.stockapp.R;
import com.tuapp.stockapp.util.ViewPagerAdapter;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TabLayout tl = findViewById(R.id.tabLayout);
        ViewPager2 vp = findViewById(R.id.viewPager);

        vp.setAdapter(new ViewPagerAdapter(this));

        new TabLayoutMediator(tl, vp, (tab, position) -> {
            if (position == 0) tab.setText("STOCK");
            else if (position == 1) tab.setText("HOY");
            else tab.setText("HISTORIAL");
        }).attach();
    }
}
