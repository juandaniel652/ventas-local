# Recordatorio: Sincronización de `version.json` (Local vs Hosting)

Para que el sistema de actualizaciones automáticas funcione correctamente, la versión declarada en tu entorno de desarrollo (VS Code) y la del archivo alojado en tu servidor/hosting deben coincidir exactamente una vez que subas una nueva versión.

## Flujo de Trabajo Paso a Paso

### 1. Modificación en Local (VS Code)
Cuando prepares una nueva actualización de la aplicación:
1. Abre el archivo `version.json` en la raíz de tu proyecto local.
2. Incrementa el número de versión (por ejemplo, de `"1.0.0"` a `"1.0.1"`).
3. Asegúrate de compilar o empaquetar tu aplicación si es necesario con esta nueva configuración.

### 2. Actualización en el Servidor (Hosting Online)
Para que la app detecte que existe una nueva versión:
1. Sube el nuevo ejecutable/paquete compilado a tu hosting.
2. Modifica el archivo `version.json` en tu hosting para que refleje exactamente el mismo número de versión que pusiste en local (`"1.0.1"`).
3. **Regla de oro:** 
   * Si `Versión Hosting` **>** `Versión Local instalada`: La app mostrará la ventana de "Actualización Disponible".
   * Si `Versión Hosting` **==** `Versión Local instalada`: La app considerará que está al día y no mostrará nada.

---

# Diagnóstico y Manejo de Errores al Actualizar

El problema de que la app **se cierre inesperadamente** o muestre el mensaje **"sigue fallando y no se actualiza"** al presionar el botón de actualizar se debe a un fallo crítico en el hilo de ejecución durante el proceso de descarga, reemplazo o permisos. 

Aquí tienes la estrategia arquitectónica y técnica de cómo debe manejarlo la app internamente:

## 1. ¿Por qué falla actualmente? (Causas Comunes)
* **Bloqueo de Archivos (File Lock):** La aplicación intenta descargarse y reemplazarse a sí misma mientras está en ejecución. Un sistema operativo (como Windows o Linux) no permite que un binario o script que se está ejecutando sea sobrescrito directamente.
* **Falta de Permisos:** El directorio donde está instalada la app requiere permisos de Administrador/Root para escribir el nuevo archivo.
* **Fallo de Red no controlado:** Si la descarga se interrumpe, el archivo queda corrupto y la app no puede reiniciarse.

## 2. Estrategia de Solución: El patrón "Updater Externo"
La forma profesional y robusta de manejar esto en frameworks de escritorio (como Kivy/Tkinter) o entornos móviles es delegar la actualización a un proceso secundario independiente.

### El Flujo Correcto en Código:
1. **Descarga en Segundo Plano (Bloque `try/except`):**
   La descarga del nuevo ejecutable/archivo debe hacerse en una carpeta temporal (ej. `temp/update.zip`) usando un hilo (`threading`) para no congelar la interfaz.
2. **Lanzar el Script Actualizador Externo:**
   En lugar de que la app se reemplace a sí misma, la app principal descarga un pequeño script o ejecutable llamado `updater.py` (o `.exe`) y lo ejecuta en un proceso independiente (`subprocess.Popen`).
3. **Cierre Controlado:**
   Inmediatamente después de lanzar el actualizador, la app principal se cierra de forma limpia (`sys.exit()`). Esto libera el bloqueo del archivo.
4. **El Actualizador toma el control:**
   El script `updater` espera un segundo a que la app principal muera por completo, copia los archivos nuevos desde la carpeta temporal a la carpeta raíz, y vuelve a iniciar la app principal ya actualizada.

## 3. Ejemplo de Manejo de Errores en la Ventana de Actualización
A nivel de código, debes envolver todo el proceso en un control de excepciones estricto para que la app no "explote" ni se cierre de golpe sin avisar al usuario:

```python
import subprocess
import sys
import os
import shutil

def ejecutar_actualizacion(self):
    try:
        # 1. Simulación de descarga del archivo desde el hosting
        # descargar_archivo(url_hosting, "temp/nueva_version.zip")
        
        # 2. Verificar que el archivo se descargó correctamente
        if not os.path.exists("temp/nueva_version.zip"):
            raise Exception("El archivo de actualización no se descargó por completo.")
            
        # 3. Lanzar el proceso independiente que reemplazará los archivos
        # Le pasamos el PID actual o simplemente dejamos que espere
        subprocess.Popen([sys.executable, "updater.py"])
        
        # 4. Cerrar la aplicación principal de forma limpia para liberar recursos
        sys.exit(0)
        
    except PermissionError:
        # Manejo de error si no hay permisos de escritura
        self.mostrar_alerta_interfaz(
            "Error de Permisos", 
            "No se pudo escribir la actualización. Por favor, ejecuta la aplicación como administrador."
        )
    except Exception as e:
        # Manejo de cualquier otro error (Red, archivo corrupto, etc.)
        self.log_error(str(e)) # Guardar en un archivo log para debug
        self.mostrar_alerta_interfaz(
            "Fallo en la Actualización", 
            f"El proceso falló de forma segura. Motivo: {str(e)}. Inténtalo de nuevo más tarde."
        )
```

### Resumen para el Desarrollo:
* **Nunca** sobrescribas la app desde la misma app activa; usa un proceso `updater` intermedio.
* **Siempre** usa bloques `try/except` envolviendo la descarga y la ejecución de archivos.
* Si algo falla en el `except`, muestra un mensaje elegante en la interfaz en lugar de permitir que el sistema operativo lance el aviso genérico de "la aplicación dejó de funcionar".
