package com.tuapp.stockapp.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabLayout tl = findViewById(R.id.tabLayout);
        ViewPager2 vp = findViewById(R.id.viewPager);
        vp.setAdapter(new ViewPagerAdapter(this));

        new TabLayoutMediator(tl, vp, (tab, position) -> {
            if (position == 0) tab.setText("STOCK");
            else if (position == 1) tab.setText("HOY");
            else tab.setText("HISTORIAL");
        }).attach();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_theme) {
            int mode = AppCompatDelegate.getDefaultNightMode();
            if (mode == AppCompatDelegate.MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
