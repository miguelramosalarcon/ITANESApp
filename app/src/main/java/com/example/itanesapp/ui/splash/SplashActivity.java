package com.example.itanesapp.ui.splash;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.AlphaAnimation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.itanesapp.MainActivity;
import com.example.itanesapp.databinding.ActivitySplashBinding;

/**
 * SplashActivity — Pantalla de bienvenida de ITANES.
 *
 * Responsabilidades:
 * 1. Mostrar logo y slogan con animación de entrada
 * 2. Verificar conectividad a internet
 * 3. Solicitar permisos de ubicación para OSMDroid
 * 4. Redirigir a MainActivity tras 2 segundos
 */
@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;

    private static final int SPLASH_DELAY_MS = 2000;
    private static final int REQUEST_PERMISSIONS = 100;

    private static final String[] PERMISOS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Ocultar ActionBar en el Splash
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Iniciar animación de entrada
        iniciarAnimacion();

        // Verificar conectividad y mostrar estado
        verificarConectividad();

        // Solicitar permisos de ubicación para OSMDroid
        verificarPermisos();
    }

    /**
     * Animación fade-in en logo, nombre y slogan.
     */
    private void iniciarAnimacion() {
        AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
        fadeIn.setDuration(1000);
        fadeIn.setFillAfter(true);

        binding.ivLogo.startAnimation(fadeIn);
        binding.tvAppName.startAnimation(fadeIn);
        binding.tvSlogan.startAnimation(fadeIn);
    }

    /**
     * Verifica si hay conexión activa a internet.
     * Si no hay conexión, muestra aviso pero la app
     * igual inicia (funciona offline con Room).
     */
    private void verificarConectividad() {
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean hayConexion = false;

        if (cm != null) {
            NetworkCapabilities caps = cm.getNetworkCapabilities(
                    cm.getActiveNetwork());
            if (caps != null) {
                hayConexion = caps.hasCapability(
                        NetworkCapabilities.NET_CAPABILITY_INTERNET);
            }
        }

        if (!hayConexion) {
            binding.tvSinConexion.setVisibility(android.view.View.VISIBLE);
            binding.progressBar.setVisibility(android.view.View.GONE);
        }
    }

    /**
     * Verifica permisos de ubicación.
     * Si ya los tiene → navega después del delay.
     * Si no → solicita al usuario.
     */
    private void verificarPermisos() {
        boolean tienePermisos = true;

        for (String permiso : PERMISOS) {
            if (ContextCompat.checkSelfPermission(this, permiso)
                    != PackageManager.PERMISSION_GRANTED) {
                tienePermisos = false;
                break;
            }
        }

        if (tienePermisos) {
            navegarAMainConDelay();
        } else {
            ActivityCompat.requestPermissions(this, PERMISOS, REQUEST_PERMISSIONS);
        }
    }

    /**
     * Callback del diálogo de permisos.
     * Navega igual sin importar si acepta o rechaza —
     * el mapa funciona sin ubicación, solo no centra en el usuario.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            navegarAMainConDelay();
        }
    }

    /**
     * Navega a MainActivity después del delay.
     */
    private void navegarAMainConDelay() {
        new Handler(Looper.getMainLooper()).postDelayed(
                this::navegarAMain,
                SPLASH_DELAY_MS
        );
    }

    /**
     * Navega a MainActivity y cierra SplashActivity.
     */
    private void navegarAMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}