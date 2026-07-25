package com.tuapp.stockapp.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.tuapp.stockapp.database.ProductoDAO
import com.tuapp.stockapp.model.Producto
import com.tuapp.stockapp.ui.dialogs.ProductoDialog
import com.tuapp.stockapp.ui.screens.InventarioScreen
import com.tuapp.stockapp.ui.screens.VentasScreen
import com.tuapp.stockapp.ui.theme.VentasMaxiTheme

class MainActivity : ComponentActivity() {

    private lateinit var productoDAO: ProductoDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        productoDAO = ProductoDAO(this)

        setContent {
            VentasMaxiTheme {
                var selectedTab by remember { mutableIntStateOf(0) }
                
                // Estados para la lista y el diálogo de producto
                var productosList by remember { mutableStateOf(obtenerProductos()) }
                var mostrarDialogo by remember { mutableStateOf(false) }

                fun recargarProductos() {
                    productosList = obtenerProductos()
                }

                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                selected = selectedTab == 0,
                                onClick = { selectedTab = 0 },
                                icon = { Icon(Icons.Default.List, contentDescription = "Inventario") },
                                label = { Text("Inventario") }
                            )
                            NavigationBarItem(
                                selected = selectedTab == 1,
                                onClick = { selectedTab = 1 },
                                icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Ventas") },
                                label = { Text("Ventas") }
                            )
                        }
                    }
                ) { padding ->
                    when (selectedTab) {
                        0 -> InventarioScreen(
                            productos = productosList,
                            onAgregarClick = { mostrarDialogo = true },
                            onVenderClick = { producto ->
                                if (producto.stock > 0) {
                                    productoDAO.registrarVenta(producto.id, 1)
                                    recargarProductos()
                                    Toast.makeText(this, "Venta registrada", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(this, "Sin stock disponible", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onBorrarClick = { producto ->
                                productoDAO.eliminarProducto(producto.id)
                                recargarProductos()
                                Toast.makeText(this, "Producto eliminado", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.padding(padding)
                        )
                        1 -> VentasScreen(
                            totalHoy = productoDAO.obtenerTotalHoy(),
                            ventasDetalle = productoDAO.obtenerHistorialHoy(),
                            modifier = Modifier.padding(padding)
                        )
                    }

                    // Diálogo para Agregar Producto
                    if (mostrarDialogo) {
                        ProductoDialog(
                            onDismissRequest = { mostrarDialogo = false },
                            onGuardarClick = { nombre, stock, precio ->
                                val nuevo = Producto(nombre, stock, precio)
                                productoDAO.insertarProducto(nuevo)
                                recargarProductos()
                                mostrarDialogo = false
                                Toast.makeText(this, "Producto agregado", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }

    private fun obtenerProductos(): List<Producto> {
        return productoDAO.obtenerTodosLosProductos() ?: emptyList()
    }
}