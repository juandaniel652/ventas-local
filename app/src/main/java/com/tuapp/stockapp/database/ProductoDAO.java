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

    public void actualizar(Producto p) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("nombre", p.getNombre());
        v.put("stock", p.getStock());
        v.put("precio", p.getPrecio());
        db.update("productos", v, "id = ?", new String[]{String.valueOf(p.getId())});
    }

    public void registrarVenta(int productoId, int cantidad, double total) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("UPDATE productos SET stock = stock - " + cantidad + " WHERE id = " + productoId);
        db.execSQL("INSERT INTO ventas (producto_id, cantidad, total, fecha) VALUES (" + 
                   productoId + ", " + cantidad + ", " + total + ", datetime('now', 'localtime'))");
    }

    public boolean tieneVentas(int productoId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM ventas WHERE producto_id = ?", new String[]{String.valueOf(productoId)});
        boolean tiene = false;
        if (c.moveToFirst()) tiene = c.getInt(0) > 0;
        c.close();
        return tiene;
    }

    // Borrado lógico (Oculta el producto de las listas pero mantiene la integridad)
    public void eliminar(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("activo", 0);
        db.update("productos", v, "id = ?", new String[]{String.valueOf(id)});
    }

    // Borrado físico real (Solo usado si no tiene ventas asociadas)
    public void eliminarFisico(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("productos", "id = ?", new String[]{String.valueOf(id)});
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
        
        String query = "SELECT p.nombre, SUM(v.cantidad), SUM(v.total) " +
                       "FROM ventas v " +
                       "JOIN productos p ON v.producto_id = p.id " +
                       "WHERE date(v.fecha) = date('now', 'localtime') " +
                       "GROUP BY v.producto_id " +
                       "ORDER BY SUM(v.total) DESC";
                       
        Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            do {
                String nombreProducto = c.getString(0);
                int cantidadTotal = c.getInt(1);
                double recaudacionTotal = c.getDouble(2);
                
                ventas.add(nombreProducto + " (x" + cantidadTotal + ") - $" + String.format("%.2f", recaudacionTotal));
            } while (c.moveToNext());
        }
        c.close();
        return ventas;
    }

    public List<Producto> obtenerTodos() {
        List<Producto> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM productos WHERE activo = 1 ORDER BY nombre ASC", null);
        if (c.moveToFirst()) {
            do {
                lista.add(new Producto(c.getInt(0), c.getString(1), c.getInt(2), c.getDouble(3)));
            } while (c.moveToNext());
        }
        c.close();
        return lista;
    }
}