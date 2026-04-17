package com.tuapp.stockapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.tuapp.stockapp.model.Producto;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {
    private DbHelper dbHelper;

    public ProductoDAO(Context context) {
        dbHelper = new DbHelper(context);
    }

    public long insertar(Producto p) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("nombre", p.getNombre());
        v.put("stock", p.getStock());
        v.put("precio", p.getPrecio());
        return db.insert("productos", null, v);
    }

    public void registrarVenta(int productoId, int cantidad, double total) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("UPDATE productos SET stock = stock - " + cantidad + " WHERE id = " + productoId);
        ContentValues v = new ContentValues();
        v.put("producto_id", productoId);
        v.put("cantidad", cantidad);
        v.put("total", total);
        db.insert("ventas", null, v);
    }

    public double obtenerTotalVentasHoy() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT SUM(total) FROM ventas WHERE date(fecha) = date('now', 'localtime')", null);
        double total = 0;
        if (c.moveToFirst()) total = c.getDouble(0);
        c.close();
        return total;
    }

    public List<String> obtenerResumenVentasHoy() {
        List<String> ventas = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT p.nombre, v.cantidad, v.total " +
                       "FROM ventas v JOIN productos p ON v.producto_id = p.id " +
                       "WHERE date(v.fecha) = date('now', 'localtime')";
        Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            do {
                ventas.add(c.getString(0) + " (x" + c.getInt(1) + ") - $" + c.getDouble(2));
            } while (c.moveToNext());
        }
        c.close();
        return ventas;
    }

    public void eliminar(int id) {
        dbHelper.getWritableDatabase().delete("productos", "id = ?", new String[]{String.valueOf(id)});
    }

    public List<Producto> obtenerTodos() {
        List<Producto> lista = new ArrayList<>();
        Cursor c = dbHelper.getReadableDatabase().rawQuery("SELECT * FROM productos", null);
        if (c.moveToFirst()) {
            do {
                lista.add(new Producto(c.getInt(0), c.getString(1), c.getInt(2), c.getDouble(3)));
            } while (c.moveToNext());
        }
        c.close();
        return lista;
    }
}
