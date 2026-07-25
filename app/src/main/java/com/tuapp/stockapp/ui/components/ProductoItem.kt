package com.tuapp.stockapp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tuapp.stockapp.model.Producto
import com.tuapp.stockapp.ui.theme.*

@Composable
fun ProductoItem(
    producto: Producto,
    onVenderClick: (Producto) -> Unit,
    onBorrarClick: (Producto) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceCard
        ),
        border = BorderStroke(1.dp, BorderStroke),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 1. Nombre del Producto
            Text(
                text = producto.nombre ?: "Producto",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 2. Fila: Stock y Precio
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Stock
                Text(
                    text = "Stock: ${producto.stock}",
                    fontSize = 14.sp,
                    color = TextMuted,
                    fontWeight = FontWeight.Medium
                )

                // Precio
                Text(
                    text = "$ ${String.format("%.2f", producto.precio)}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryNavy
                )
            }

            // 3. Divisor fino y sobrio
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                thickness = 1.dp,
                color = BorderStroke
            )

            // 4. Botones de Acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Botón Eliminar
                OutlinedButton(
                    onClick = { onBorrarClick(producto) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, StatusDanger.copy(alpha = 0.5f)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = StatusDanger
                    )
                ) {
                    Text(
                        text = "ELIMINAR",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Botón Vender
                Button(
                    onClick = { onVenderClick(producto) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryNavy,
                        contentColor = SurfaceCard
                    )
                ) {
                    Text(
                        text = "VENDER",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}