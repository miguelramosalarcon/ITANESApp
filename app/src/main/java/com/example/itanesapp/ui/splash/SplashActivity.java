package com.example.itanesapp.ui.splash;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import androidx.appcompat.app.AppCompatActivity;

import com.example.itanesapp.MainActivity;
import com.example.itanesapp.databinding.ActivitySplashBinding;

/**
 * SplashActivity — Pantalla de bienvenida de ITANES.
 *
 * Responsabilidades:
 * 1. Mostrar logo y slogan con animación de entrada
 * 2. Verificar conectividad a internet
 * 3. Redirigir a MainActivity tras 2 segundos
 *
 * Nota: @SuppressLint("CustomSplashScreen") es necesario porque
 * Android 12+ tiene su propio SplashScreen API, pero usamos
 * el nuestro para mayor control visual.
 */
@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;

    // Tiempo de espera antes de navegar a MainActivity
    private static final int SPLASH_DELAY_MS = 2000;

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

        // Navegar a MainActivity después del delay
        new Handler(Looper.getMainLooper()).postDelayed(
                this::navegarAMain,
                SPLASH_DELAY_MS
        );
    }

    /**
     * Animación fade-in en logo, nombre y slogan.
     * Da sensación de carga elegante.
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
            // Mostrar aviso — la app igual funciona con datos locales
            binding.tvSinConexion.setVisibility(
                    android.view.View.VISIBLE);
            binding.progressBar.setVisibility(
                    android.view.View.GONE);
        }
    }

    /**
     * Navega a MainActivity y cierra SplashActivity.
     * finish() evita que el usuario regrese al Splash
     * con el botón atrás.
     */
    private void navegarAMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Limpiar binding para evitar memory leaks
        binding = null;
    }
}