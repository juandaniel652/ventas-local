package com.tuapp.stockapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tuapp.stockapp.model.Producto
import com.tuapp.stockapp.ui.components.ProductoItem
import com.tuapp.stockapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventarioScreen(
    productos: List<Producto>,
    onAgregarClick: () -> Unit,
    onVenderClick: (Producto) -> Unit,
    onBorrarClick: (Producto) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }

    // Filtrado en tiempo real según lo que ingrese el usuario
    val productosFiltrados = remember(searchQuery, productos) {
        if (searchQuery.isBlank()) {
            productos
        } else {
            productos.filter { 
                it.nombre?.contains(searchQuery, ignoreCase = true) == true 
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BackgroundCanvas,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAgregarClick,
                containerColor = PrimaryNavy,
                contentColor = SurfaceCard,
                shape = RoundedCornerShape(16.dp),
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 3.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar Producto"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header Superior
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Inventario de Productos",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "${productos.size} productos registrados",
                    fontSize = 13.sp,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Buscador Corporativo
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Buscar producto...", fontSize = 14.sp, color = TextMuted) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = TextMuted
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SurfaceCard,
                        unfocusedContainerColor = SurfaceCard,
                        focusedBorderColor = PrimaryNavy,
                        unfocusedBorderColor = BorderStroke
                    )
                )
            }

            // Lista o Estado Vacío
            if (productosFiltrados.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (searchQuery.isBlank()) "No hay productos en inventario." else "No se encontraron coincidencias.",
                        fontSize = 14.sp,
                        color = TextMuted,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                // Equivalente al RecyclerView (LazyColumn)
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp) // Espacio para no solapar el FAB
                ) {
                    items(
                        items = productosFiltrados,
                        key = { producto -> producto.id } // Garantiza reciclaje rápido de elementos
                    ) { producto ->
                        ProductoItem(
                            producto = producto,
                            onVenderClick = onVenderClick,
                            onBorrarClick = onBorrarClick
                        )
                    }
                }
            }
        }
    }
}