package com.example.itanesapp.ui.recorrido;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.itanesapp.R;
import com.example.itanesapp.databinding.FragmentRecorridoDetailBinding;
import com.example.itanesapp.viewmodel.RecorridoViewModel;

/**
 * RecorridoDetailFragment — Detalle de un recorrido turístico.
 *
 * Muestra imagen de portada, duración y la lista
 * de 5 puntos en formato timeline.
 * Al tocar "Iniciar Recorrido" navega al Punto 1.
 * Al tocar cualquier punto navega directamente a ese punto.
 */
public class RecorridoDetailFragment extends Fragment {

    private FragmentRecorridoDetailBinding binding;
    private RecorridoViewModel viewModel;
    private PuntoTimelineAdapter adapter;

    // ID del recorrido recibido como argumento de navegación
    private int recorridoId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentRecorridoDetailBinding.inflate(
                inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Obtener el recorridoId de los argumentos de navegación
        if (getArguments() != null) {
            recorridoId = getArguments().getInt("recorridoId", -1);
        }

        configurarViewModel();
        configurarRecyclerView();
        observarDatos();
    }

    private void configurarViewModel() {
        viewModel = new ViewModelProvider(this)
                .get(RecorridoViewModel.class);

        // Informar al ViewModel qué recorrido mostrar
        viewModel.setRecorridoId(recorridoId);
    }

    private void configurarRecyclerView() {
        adapter = new PuntoTimelineAdapter(punto -> {
            // Navegar al detalle del punto seleccionado
            Bundle args = new Bundle();
            args.putInt("puntoId", punto.id);
            args.putInt("recorridoId", recorridoId);
            Navigation.findNavController(requireView())
                    .navigate(R.id.puntoDetailFragment, args);
        });

        binding.rvPuntos.setLayoutManager(
                new LinearLayoutManager(requireContext()));
        binding.rvPuntos.setAdapter(adapter);
    }

    private void observarDatos() {

        // Observar datos del recorrido
        viewModel.recorrido.observe(getViewLifecycleOwner(), recorrido -> {
            if (recorrido == null) return;

            // Imagen de portada
            Glide.with(requireContext())
                    .load(recorrido.imagenUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .centerCrop()
                    .into(binding.ivPortada);

            // Nombre
            binding.tvNombre.setText(recorrido.nombre);

            // Duración
            binding.tvDuracion.setText(
                    getString(R.string.home_duracion, recorrido.duracionHoras));
        });

        // Observar lista de puntos
        viewModel.puntos.observe(getViewLifecycleOwner(), puntos -> {
            binding.progressBar.setVisibility(View.GONE);

            if (puntos == null || puntos.isEmpty()) return;

            adapter.submitList(puntos);

            // Botón Iniciar → navega al primer punto
            binding.btnIniciar.setOnClickListener(v -> {
                Bundle args = new Bundle();
                args.putInt("puntoId", puntos.get(0).id);
                args.putInt("recorridoId", recorridoId);
                Navigation.findNavController(requireView())
                        .navigate(R.id.puntoDetailFragment, args);
            });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}