package com.tuapp.stockapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "stock_pro.db";
    private static final int DATABASE_VERSION = 2;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tabla de Productos (El Inventario)
        db.execSQL("CREATE TABLE productos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT," +
                "stock INTEGER," +
                "precio REAL)");

        // Tabla de Ventas (El Historial Diario)
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
        db.execSQL("DROP TABLE IF EXISTS ventas");
        db.execSQL("DROP TABLE IF EXISTS productos");
        onCreate(db);
    }
}
