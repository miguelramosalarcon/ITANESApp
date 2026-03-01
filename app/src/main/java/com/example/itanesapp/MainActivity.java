package com.example.itanesapp;

// ============================================================
// IMPORTS — Solo los necesarios, sin duplicados
// ============================================================
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.itanesapp.databinding.ActivityMainBinding;

/**
 * MainActivity — Actividad principal y única de la app ITANES.
 * Actúa como contenedor del NavHostFragment.
 * Toda la navegación ocurre entre Fragments dentro de esta Activity.
 */
public class MainActivity extends AppCompatActivity {

    // ViewBinding — acceso a las vistas sin findViewById()
    private ActivityMainBinding binding;

    // NavController — maneja la navegación entre Fragments
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflar el layout usando ViewBinding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtener el NavHostFragment definido en activity_main.xml
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        // Obtener el NavController desde el NavHostFragment
        navController = navHostFragment.getNavController();

        // Conectar el ActionBar (flecha atrás) con el NavController
        NavigationUI.setupActionBarWithNavController(this, navController);
    }

    /**
     * Permite que la flecha "atrás" del ActionBar
     * navegue entre Fragments correctamente.
     */
    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}