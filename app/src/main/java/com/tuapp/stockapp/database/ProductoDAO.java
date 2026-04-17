package com.tuapp.stockapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.tuapp.stockapp.model.Producto;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {
    private SQLiteDatabase db;
    private DatabaseHelper dbHelper;

    public ProductoDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void abrir() {
        db = dbHelper.getWritableDatabase();
    }

    public void cerrar() {
        dbHelper.close();
    }

    // INSERTAR O ACTUALIZAR (Para no perder datos)
    public long guardarProducto(Producto producto) {
        ContentValues values = new ContentValues();
        values.put("nombre", producto.getNombre());
        values.put("precio", producto.getPrecio());
        values.put("stock", producto.getStock());

        if (producto.getId() > 0) {
            return db.update("productos", values, "id = ?", new String[]{String.valueOf(producto.getId())});
        } else {
            return db.insert("productos", null, values);
        }
    }

    // LEER TODOS
    public List<Producto> obtenerTodos() {
        List<Producto> lista = new ArrayList<>();
        Cursor cursor = db.query("productos", null, null, null, null, null, "nombre ASC");

        if (cursor.moveToFirst()) {
            do {
                Producto p = new Producto(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getDouble(2),
                    cursor.getInt(3)
                );
                lista.add(p);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }

    // ELIMINAR
    public void eliminar(int id) {
        db.delete("productos", "id = ?", new String[]{String.valueOf(id)});
    }
}