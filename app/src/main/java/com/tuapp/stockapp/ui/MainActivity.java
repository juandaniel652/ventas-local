package com.tuapp.stockapp.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.tuapp.stockapp.R;
import com.tuapp.stockapp.database.DbHelper;
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
        // Agregamos una opción extra para limpiar todo
        menu.add(0, 999, 0, "LIMPIAR TODO (BORRAR VENTAS)");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_theme) {
            int mode = AppCompatDelegate.getDefaultNightMode();
            AppCompatDelegate.setDefaultNightMode(mode == AppCompatDelegate.MODE_NIGHT_YES ? 
                AppCompatDelegate.MODE_NIGHT_NO : AppCompatDelegate.MODE_NIGHT_YES);
            return true;
        }
        
        // BOTÓN DE PÁNICO PARA MAXI
        if (item.getItemId() == 999) {
            new MaterialAlertDialogBuilder(this)
                .setTitle("¿BORRAR TODAS LAS VENTAS?")
                .setMessage("Esto pondrá el marcador a $0 y borrará todo el historial. No afecta el stock.")
                .setPositiveButton("SÍ, BORRAR TODO", (d, w) -> {
                    DbHelper db = new DbHelper(this);
                    db.getWritableDatabase().execSQL("DELETE FROM ventas");
                    db.getWritableDatabase().execSQL("DELETE FROM sqlite_sequence WHERE name='ventas'");
                    Toast.makeText(this, "Base de datos de ventas limpiada", Toast.LENGTH_SHORT).show();
                    recreate(); // Reinicia la app para ver los cambios
                })
                .setNegativeButton("CANCELAR", null)
                .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
