package com.tuapp.stockapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SimpleStock.db";
    private static final int DATABASE_VERSION = 1;

    // SQL para crear tablas
    private static final String CREATE_TABLE_PRODUCTOS = 
        "CREATE TABLE productos (" +
        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
        "nombre TEXT, " +
        "precio REAL, " +
        "stock INTEGER)";

    private static final String CREATE_TABLE_VENTAS = 
        "CREATE TABLE ventas (" +
        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
        "fecha TEXT, " +
        "total REAL)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PRODUCTOS);
        db.execSQL(CREATE_TABLE_VENTAS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS productos");
        db.execSQL("DROP TABLE IF EXISTS ventas");
        onCreate(db);
    }
}