package com.example.itanesapp.ui.home;

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

import com.example.itanesapp.R;
import com.example.itanesapp.databinding.FragmentHomeBinding;
import com.example.itanesapp.viewmodel.HomeViewModel;

/**
 * HomeFragment — Primera pantalla de la app.
 *
 * Muestra la lista de recorridos disponibles.
 * Al tocar una card navega a RecorridoDetailFragment
 * pasando el ID del recorrido seleccionado.
 */
public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;
    private RecorridoAdapter adapter;

    // --------------------------------------------------------
    // Ciclo de vida del Fragment
    // --------------------------------------------------------

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        configurarViewModel();
        configurarRecyclerView();
        observarRecorridos();
    }

    // --------------------------------------------------------
    // Configuración
    // --------------------------------------------------------

    /**
     * Inicializa el ViewModel usando ViewModelProvider.
     * El ViewModel sobrevive rotaciones de pantalla.
     */
    private void configurarViewModel() {
        viewModel = new ViewModelProvider(this)
                .get(HomeViewModel.class);
    }

    /**
     * Configura el RecyclerView con LinearLayoutManager
     * y el adapter. El adapter maneja los clicks.
     */
    private void configurarRecyclerView() {
        adapter = new RecorridoAdapter(recorrido -> {
            // Navegar a RecorridoDetailFragment con el ID del recorrido
            Bundle args = new Bundle();
            args.putInt("recorridoId", recorrido.id);
            Navigation.findNavController(requireView())
                    .navigate(R.id.recorridoDetailFragment, args);
        });

        binding.rvRecorridos.setLayoutManager(
                new LinearLayoutManager(requireContext()));
        binding.rvRecorridos.setAdapter(adapter);
    }

    /**
     * Observa el LiveData de recorridos desde el ViewModel.
     * Room actualiza automáticamente cuando cambian los datos.
     */
    private void observarRecorridos() {
        viewModel.getRecorridos().observe(getViewLifecycleOwner(), recorridos -> {

            // Ocultar ProgressBar
            binding.progressBar.setVisibility(View.GONE);

            if (recorridos == null || recorridos.isEmpty()) {
                // Mostrar estado vacío
                binding.layoutEmpty.setVisibility(View.VISIBLE);
                binding.rvRecorridos.setVisibility(View.GONE);
            } else {
                // Mostrar lista con los recorridos
                binding.layoutEmpty.setVisibility(View.GONE);
                binding.rvRecorridos.setVisibility(View.VISIBLE);
                adapter.submitList(recorridos);
            }
        });
    }

    // --------------------------------------------------------
    // Limpiar binding para evitar memory leaks
    // --------------------------------------------------------
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}