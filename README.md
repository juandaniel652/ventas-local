# SimpleStock - Sistema de Gestión de Ventas Local

### 🚀 Descripción
Aplicación móvil ágil diseñada para digitalizar comercios pequeños. Elimina el registro manual en papel, permitiendo el control de stock y ventas diarias con persistencia de datos local.

### 🛠️ Stack Tecnológico
* **Lenguaje:** Java (Android Native)
* **IDE:** Visual Studio Code
* **Base de Datos:** SQLite (Persistencia local)
* **Depuración:** USB Debugging en dispositivo físico

### 📋 Requerimientos Funcionales
1. **Carga Única:** Los productos se registran una sola vez y quedan disponibles para la venta.
2. **Control de Stock:** El sistema descuenta unidades automáticamente con cada venta.
3. **Resiliencia de Datos:** Si la app se cierra inesperadamente, la venta en curso y el historial permanecen intactos.
4. **Historial de Ganancias:** Consulta de cuánto se vendió "Día por Día".

### 📂 Estructura de la Base de Datos (SQLite)
- **Tabla `productos`**: `id (PK)`, `nombre`, `precio`, `stock`.
- **Tabla `ventas`**: `id (PK)`, `producto_id (FK)`, `cantidad`, `subtotal`, `fecha`.

### 🛠️ Instalación y Pruebas
1. Clonar el repositorio.
2. Abrir con VS Code (con extensiones de Java/Android).
3. Conectar dispositivo Android vía USB.
4. Ejecutar `adb devices` para verificar conexión.
5. `Run and Debug` para desplegar.
