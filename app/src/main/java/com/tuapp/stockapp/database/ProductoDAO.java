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

    public void eliminar(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("activo", 0);
        db.update("productos", v, "id = ?", new String[]{String.valueOf(id)});
    }

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

    // --- MÉTODOS DE ANULACIÓN/DESCUENTO DEL HISTORIAL ---

    // Resta 1 unidad de una venta (o borra la venta si era de 1 solo producto) y devuelve el stock
    public void restarUnaUnidadVenta(int ventaId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        // Obtener datos de la venta
        Cursor c = db.rawQuery("SELECT producto_id, cantidad, total FROM ventas WHERE id = ?", new String[]{String.valueOf(ventaId)});
        if (c.moveToFirst()) {
            int productoId = c.getInt(0);
            int cantidad = c.getInt(1);
            double total = c.getDouble(2);
            
            if (cantidad > 1) {
                double precioUnitario = total / cantidad;
                int nuevaCantidad = cantidad - 1;
                double nuevoTotal = total - precioUnitario;
                
                // Actualizar venta
                db.execSQL("UPDATE ventas SET cantidad = " + nuevaCantidad + ", total = " + nuevoTotal + " WHERE id = " + ventaId);
            } else {
                // Borrar venta si era la última unidad
                db.execSQL("DELETE FROM ventas WHERE id = " + ventaId);
            }
            
            // Devolver 1 al stock
            db.execSQL("UPDATE productos SET stock = stock + 1 WHERE id = " + productoId);
        }
        c.close();
    }

    // Elimina un grupo de ventas por IDs (ej: "1,4,8") y restaura el stock
    public void eliminarVentasAgrupadas(String idsVenta) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        // Devolver el stock correspondientes
        String sqlSelect = "SELECT producto_id, SUM(cantidad) FROM ventas WHERE id IN (" + idsVenta + ") GROUP BY producto_id";
        Cursor c = db.rawQuery(sqlSelect, null);
        if (c.moveToFirst()) {
            do {
                int productoId = c.getInt(0);
                int cantidad = c.getInt(1);
                db.execSQL("UPDATE productos SET stock = stock + " + cantidad + " WHERE id = " + productoId);
            } while (c.moveToNext());
        }
        c.close();
        
        // Borrar registros
        db.execSQL("DELETE FROM ventas WHERE id IN (" + idsVenta + ")");
    }
}