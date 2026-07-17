package com.tuapp.stockapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "stock_pro.db";
    // Subimos la versión a 3 para forzar la actualización de la estructura
    private static final int DATABASE_VERSION = 3; 

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Añadimos "activo INTEGER DEFAULT 1" al final
        db.execSQL("CREATE TABLE productos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT," +
                "stock INTEGER," +
                "precio REAL," +
                "activo INTEGER DEFAULT 1)"); // 1 = Visible, 0 = Eliminado lógico

        db.execSQL("CREATE TABLE ventas (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "producto_id INTEGER," +
                "cantidad INTEGER," +
                "fecha DATE DEFAULT (datetime('now','localtime'))," +
                "total REAL," +
                "FOREIGN KEY(producto_id) REFERENCES productos(id))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) {
            // Migración limpia: si Maxi ya tiene la v2, le agrega la columna sin borrarle sus productos actuales
            db.execSQL("ALTER TABLE productos ADD COLUMN activo INTEGER DEFAULT 1");
        }
    }
}