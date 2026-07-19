package com.tuapp.stockapp.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import androidx.core.content.FileProvider;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;



public class UpdateManager {
    // Reemplazá con la URL real de tu JSON en Firebase (Hosting o Storage)
    private static final String JSON_URL = "https://maxi-ventas-updates.web.app/version.json"; 
    private final Activity activity;

    public UpdateManager(Activity activity) {
        this.activity = activity;
    }

    public void verificarActualizaciones() {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                URL url = new URL(JSON_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String linea;
                while ((linea = reader.readLine()) != null) sb.append(linea);
                reader.close();

                JSONObject json = new JSONObject(sb.toString());
                int nuevoVersionCode = json.getInt("versionCode");
                String apkUrl = json.getString("apkUrl");

                PackageInfo pInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
                long versionActual = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P ? pInfo.getLongVersionCode() : pInfo.versionCode;

                if (nuevoVersionCode > versionActual) {
                    new Handler(Looper.getMainLooper()).post(() -> mostrarDialogoActualizacion(apkUrl));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void mostrarDialogoActualizacion(String apkUrl) {
        new MaterialAlertDialogBuilder(activity)
                .setTitle("¡Nueva versión disponible!")
                .setMessage("Hay una actualización crítica para mejorar el rendimiento de tus ventas. ¿Querés descargarla ahora?")
                .setCancelable(false)
                .setPositiveButton("Actualizar", (dialog, which) -> descargarEInstalarApk(apkUrl))
                .setNegativeButton("Más tarde", null)
                .show();
    }

    private void descargarEInstalarApk(String urlDescarga) {
        // Mostrar un feedback visual o diálogo de carga si se desea corporativo
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                URL url = new URL(urlDescarga);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                InputStream input = conn.getInputStream();

                File carpetaDescargas = activity.getExternalFilesDir("Download");
                if (carpetaDescargas != null && !carpetaDescargas.exists()) carpetaDescargas.mkdirs();

                File archivoApk = new File(carpetaDescargas, "update.apk");
                FileOutputStream output = new FileOutputStream(archivoApk);

                byte[] buffer = new byte[4096];
                int bytesLeidos;
                while ((bytesLeidos = input.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesLeidos);
                }

                output.close();
                input.close();

                new Handler(Looper.getMainLooper()).post(() -> lanzarInstalador(archivoApk));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void lanzarInstalador(File apkFile) {
        // En vez de application context, usamos la activity directamente para el FileProvider
        Uri apkUri = FileProvider.getUriForFile(activity, "com.tuapp.stockapp.fileprovider", apkFile);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        
        // FLAG_ACTIVITY_NEW_TASK a veces duplica procesos en Intents de instalación en la misma Activity; 
        // Usamos GRANT_READ_URI_PERMISSION de forma explícita
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        
        // Manejo específico para Android 8.0+ requiriendo permisos de orígenes desconocidos
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!activity.getPackageManager().canRequestPackageInstalls()) {
                activity.startActivity(new Intent(android.provider.Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, 
                        Uri.parse("package:" + activity.getPackageName())));
                return;
            }
        }
        
        activity.startActivity(intent);
    }
}