package com.tuapp.stockapp.ui.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tuapp.stockapp.model.Producto
import com.tuapp.stockapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoDialog(
    productoExistente: Producto? = null,
    onDismissRequest: () -> Unit,
    onGuardarClick: (nombre: String, stock: Int, precio: Double) -> Unit
) {
    var nombre by remember { mutableStateOf(productoExistente?.nombre ?: "") }
    var stockText by remember { mutableStateOf(productoExistente?.stock?.toString() ?: "") }
    var precioText by remember { mutableStateOf(productoExistente?.precio?.toString() ?: "") }

    val esValido = nombre.isNotBlank() && stockText.toIntOrNull() != null && precioText.toDoubleOrNull() != null

    AlertDialog(
        onDismissRequest = onDismissRequest,
        shape = RoundedCornerShape(16.dp),
        containerColor = SurfaceCard,
        title = {
            Text(
                text = if (productoExistente == null) "NUEVO PRODUCTO" else "EDITAR PRODUCTO",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryNavy
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Input Nombre
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre del Producto") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryNavy,
                        unfocusedBorderColor = BorderStroke,
                        focusedLabelColor = PrimaryNavy
                    )
                )

                // Fila: Stock + Precio
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Input Stock
                    OutlinedTextField(
                        value = stockText,
                        onValueChange = { stockText = it },
                        label = { Text("Stock") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryNavy,
                            unfocusedBorderColor = BorderStroke,
                            focusedLabelColor = PrimaryNavy
                        )
                    )

                    // Input Precio
                    OutlinedTextField(
                        value = precioText,
                        onValueChange = { precioText = it },
                        label = { Text("Precio ($)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryNavy,
                            unfocusedBorderColor = BorderStroke,
                            focusedLabelColor = PrimaryNavy
                        )
                    )
                }
            }
        },
        confirmButton = {
            Button(
                enabled = esValido,
                onClick = {
                    val stock = stockText.toIntOrNull() ?: 0
                    val precio = precioText.toDoubleOrNull() ?: 0.0
                    onGuardarClick(nombre, stock, precio)
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryNavy,
                    contentColor = SurfaceCard
                )
            ) {
                Text("GUARDAR", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismissRequest,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, BorderStroke),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextMuted)
            ) {
                Text("CANCELAR")
            }
        }
    )
}