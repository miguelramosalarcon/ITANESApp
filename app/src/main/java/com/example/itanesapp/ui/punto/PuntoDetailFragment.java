package com.example.itanesapp.ui.punto;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import com.example.itanesapp.R;
import com.example.itanesapp.databinding.FragmentPuntoDetailBinding;
import com.example.itanesapp.viewmodel.PuntoViewModel;


/**
 * PuntoDetailFragment — Detalle completo de un punto turístico.
 *
 * Responsabilidades:
 * 1. Galería de fotos con ViewPager2
 * 2. Información del lugar (nombre, orden, descripción)
 * 3. Toggle favorito ♡
 * 4. Navegación anterior/siguiente entre puntos
 * 5. Botón compartir
 * 6. Botón ver ruta → RutaFragment
 */
public class PuntoDetailFragment extends Fragment {

    private FragmentPuntoDetailBinding binding;
    private PuntoViewModel viewModel;
    private FotoAdapter fotoAdapter;

    private int puntoId;
    private int recorridoId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPuntoDetailBinding.inflate(
                inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Obtener argumentos de navegación
        if (getArguments() != null) {
            puntoId = getArguments().getInt("puntoId", -1);
            recorridoId = getArguments().getInt("recorridoId", -1);
        }

        configurarViewModel();
        configurarViewPager();
        observarDatos();
        configurarBotones();
    }

    // --------------------------------------------------------
    // Configuración
    // --------------------------------------------------------

    private void configurarViewModel() {
        viewModel = new ViewModelProvider(this)
                .get(PuntoViewModel.class);
        viewModel.setPunto(puntoId, recorridoId);
    }

    private void configurarViewPager() {
        fotoAdapter = new FotoAdapter();
        binding.vpFotos.setAdapter(fotoAdapter);

        // Actualizar contador y dots al cambiar de foto
        binding.vpFotos.registerOnPageChangeCallback(
                new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        super.onPageSelected(position);
                        actualizarContadorFotos(position + 1,
                                fotoAdapter.getItemCount());
                        actualizarDots(position);
                    }
                });
    }

    // --------------------------------------------------------
    // Observadores LiveData
    // --------------------------------------------------------

    private void observarDatos() {

        // Datos del punto
        viewModel.punto.observe(getViewLifecycleOwner(), punto -> {
            if (punto == null) return;

            binding.tvNombre.setText(punto.nombre);
            binding.tvDescripcion.setText(punto.descripcion);
            binding.tvOrden.setText(
                    getString(R.string.home_puntos, punto.orden) +
                            " de 5 en el recorrido");

            // Mostrar/ocultar botón Anterior según orden
            binding.btnAnterior.setVisibility(
                    punto.orden > 1 ? View.VISIBLE : View.INVISIBLE);

            // Mostrar/ocultar botón Siguiente según orden
            binding.btnSiguiente.setVisibility(
                    punto.orden < 5 ? View.VISIBLE : View.INVISIBLE);
        });

        // Fotos del punto
        viewModel.fotos.observe(getViewLifecycleOwner(), fotos -> {
            if (fotos == null || fotos.isEmpty()) return;

            fotoAdapter.submitList(fotos);
            crearDots(fotos.size());
            actualizarContadorFotos(1, fotos.size());
        });

        // Estado favorito
        viewModel.esFavorito.observe(getViewLifecycleOwner(), esFav -> {
            boolean activo = esFav != null && esFav > 0;
            binding.btnFavorito.setImageResource(activo
                    ? R.drawable.ic_favorito_active
                    : R.drawable.ic_favorito_inactive);
        });
    }

    // --------------------------------------------------------
    // Botones
    // --------------------------------------------------------

    private void configurarBotones() {

        // Toggle favorito
        binding.btnFavorito.setOnClickListener(v ->
                viewModel.toggleFavorito());

        // Ver Ruta
        binding.btnVerRuta.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putInt("puntoId", puntoId);
            args.putInt("recorridoId", recorridoId);
            Navigation.findNavController(requireView())
                    .navigate(R.id.rutaFragment, args);
        });

        // Compartir
        binding.btnCompartir.setOnClickListener(v ->
                compartirPunto());

        // Anterior
        binding.btnAnterior.setOnClickListener(v -> {
            viewModel.punto.getValue();
            if (viewModel.punto.getValue() == null) return;
            int ordenActual = viewModel.punto.getValue().orden;

            viewModel.getAnteriorPunto(ordenActual)
                    .observe(getViewLifecycleOwner(), anterior -> {
                        if (anterior == null) return;
                        Bundle args = new Bundle();
                        args.putInt("puntoId", anterior.id);
                        args.putInt("recorridoId", recorridoId);
                        Navigation.findNavController(requireView())
                                .navigate(R.id.puntoDetailFragment, args);
                    });
        });

        // Siguiente
        binding.btnSiguiente.setOnClickListener(v -> {
            if (viewModel.punto.getValue() == null) return;
            int ordenActual = viewModel.punto.getValue().orden;

            viewModel.getSiguientePunto(ordenActual)
                    .observe(getViewLifecycleOwner(), siguiente -> {
                        if (siguiente == null) return;
                        Bundle args = new Bundle();
                        args.putInt("puntoId", siguiente.id);
                        args.putInt("recorridoId", recorridoId);
                        Navigation.findNavController(requireView())
                                .navigate(R.id.puntoDetailFragment, args);
                    });
        });
    }

    // --------------------------------------------------------
    // Helpers — dots y contador
    // --------------------------------------------------------

    /**
     * Crea los dots indicadores dinámicamente según
     * la cantidad de fotos disponibles.
     */
    private void crearDots(int cantidad) {
        binding.layoutDots.removeAllViews();
        for (int i = 0; i < cantidad; i++) {
            ImageView dot = new ImageView(requireContext());
            dot.setImageResource(i == 0
                    ? R.drawable.dot_active
                    : R.drawable.dot_inactive);
            int margen = (int) (4 * getResources()
                    .getDisplayMetrics().density);
            android.widget.LinearLayout.LayoutParams params =
                    new android.widget.LinearLayout.LayoutParams(
                            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(margen, 0, margen, 0);
            dot.setLayoutParams(params);
            binding.layoutDots.addView(dot);
        }
    }

    /**
     * Actualiza visualmente el dot activo.
     */
    private void actualizarDots(int posicionActiva) {
        for (int i = 0; i < binding.layoutDots.getChildCount(); i++) {
            ImageView dot = (ImageView) binding.layoutDots.getChildAt(i);
            dot.setImageResource(i == posicionActiva
                    ? R.drawable.dot_active
                    : R.drawable.dot_inactive);
        }
    }

    /**
     * Actualiza el texto del contador "1 / 3".
     */
    private void actualizarContadorFotos(int actual, int total) {
        binding.tvFotoCounter.setText(actual + " / " + total);
    }

    /**
     * Comparte el nombre del lugar usando Intent nativo de Android.
     * No requiere ninguna API externa.
     */
    private void compartirPunto() {
        if (viewModel.punto.getValue() == null) return;
        String nombre = viewModel.punto.getValue().nombre;
        String mensaje = getString(R.string.punto_compartir_mensaje, nombre);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, mensaje);
        startActivity(Intent.createChooser(intent,
                getString(R.string.punto_compartir)));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}