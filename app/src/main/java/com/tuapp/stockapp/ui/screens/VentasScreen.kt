package com.tuapp.stockapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tuapp.stockapp.model.GrupoHistorial
import com.tuapp.stockapp.ui.theme.*

@Composable
fun VentasScreen(
    totalHoy: Double,
    ventasDetalle: List<GrupoHistorial>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 1. Tarjeta KPI - Recaudación Principal
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            border = BorderStroke(1.dp, BorderStroke),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Recaudación de Hoy",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextMuted
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "$ ${String.format("%.2f", totalHoy)}",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryNavy
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 2. Título de Sección
        Text(
            text = "Detalle de ventas",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 3. Lista de Transacciones / Ventas
        if (ventasDetalle.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Aún no hay ventas registradas hoy.",
                    fontSize = 14.sp,
                    color = TextMuted
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(ventasDetalle) { venta ->
                    VentaItemRow(venta = venta)
                }
            }
        }
    }
}

@Composable
private fun VentaItemRow(venta: GrupoHistorial) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = BorderStroke(1.dp, BorderStroke)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = venta.nombreProducto ?: "Producto",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Text(
                    text = "Cantidad: ${venta.cantidad}",
                    fontSize = 13.sp,
                    color = TextMuted
                )
            }
            Text(
                text = "$ ${String.format("%.2f", venta.subtotal)}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = StatusSuccess
            )
        }
    }
}