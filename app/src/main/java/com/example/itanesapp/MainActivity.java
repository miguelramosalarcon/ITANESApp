package com.example.itanesapp;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.itanesapp.databinding.ActivityMainBinding;

/**
 * MainActivity — Contenedor principal de la app ITANES.
 *
 * Responsabilidades:
 * 1. Alojar el NavHostFragment
 * 2. Conectar Toolbar con Navigation (flecha atrás)
 * 3. Conectar BottomNavigationBar
 * 4. Ocultar Toolbar y BottomNav en pantallas específicas
 */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        configurarToolbar();
        configurarNavegacion();
        configurarBottomNav();
        configurarVisibilidad();
    }

    /**
     * Configura el Toolbar como ActionBar de la app.
     */
    private void configurarToolbar() {
        setSupportActionBar(binding.toolbar);
    }

    /**
     * Conecta NavController con el NavHostFragment.
     */
    private void configurarNavegacion() {
        NavHostFragment navHostFragment = (NavHostFragment)
                getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        // Destinos TOP LEVEL — no muestran flecha atrás
        // Home y Favoritos son destinos raíz
        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(
                        R.id.homeFragment,
                        R.id.favoritosFragment)
                        .build();

        // Conectar Toolbar con NavController
        // Maneja automáticamente flecha atrás y título
        NavigationUI.setupWithNavController(
                binding.toolbar,
                navController,
                appBarConfiguration);
    }

    /**
     * Conecta BottomNav con NavController.
     */
    private void configurarBottomNav() {
        NavigationUI.setupWithNavController(
                binding.bottomNav, navController);
    }

    /**
     * Oculta/muestra Toolbar y BottomNav según la pantalla.
     */
    private void configurarVisibilidad() {
        navController.addOnDestinationChangedListener(
                (controller, destination, arguments) -> {

                    int destId = destination.getId();

                    if (destId == R.id.homeFragment ||
                            destId == R.id.favoritosFragment) {
                        // Pantallas principales — mostrar ambos
                        binding.toolbar.setVisibility(View.VISIBLE);
                        binding.bottomNav.setVisibility(View.VISIBLE);
                    } else {
                        // Pantallas de detalle — solo Toolbar con flecha
                        binding.toolbar.setVisibility(View.VISIBLE);
                        binding.bottomNav.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() ||
                super.onSupportNavigateUp();
    }
}