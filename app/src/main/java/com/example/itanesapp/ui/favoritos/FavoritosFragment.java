package com.example.itanesapp.ui.favoritos;

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

import com.google.android.material.snackbar.Snackbar;
import com.example.itanesapp.R;
import com.example.itanesapp.data.local.entity.FavoritoEntity;
import com.example.itanesapp.databinding.FragmentFavoritosBinding;
import com.example.itanesapp.viewmodel.FavoritosViewModel;


/**
 * FavoritosFragment — Lista de puntos guardados por el usuario.
 *
 * Responsabilidades:
 * 1. Mostrar lista de favoritos desde Room
 * 2. Permitir eliminar con confirmación via Snackbar
 * 3. Navegar al detalle del punto al tocar un item
 * 4. Mostrar estado vacío si no hay favoritos
 */
public class FavoritosFragment extends Fragment {

    private FragmentFavoritosBinding binding;
    private FavoritosViewModel viewModel;
    private FavoritoAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentFavoritosBinding.inflate(
                inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        configurarViewModel();
        configurarRecyclerView();
        observarFavoritos();
    }

    private void configurarViewModel() {
        viewModel = new ViewModelProvider(this)
                .get(FavoritosViewModel.class);
    }

    private void configurarRecyclerView() {

        adapter = new FavoritoAdapter(
                // OnFavoritoClickListener
                new FavoritoAdapter.OnFavoritoClickListener() {

                    @Override
                    public void onEliminarClick(FavoritoEntity favorito) {
                        // Eliminar con opción de deshacer via Snackbar
                        viewModel.eliminarFavorito(favorito.puntoId);
                        Snackbar.make(requireView(),
                                        R.string.favoritos_eliminar,
                                        Snackbar.LENGTH_LONG)
                                .setAction(R.string.favoritos_deshacer, v -> {
                                    // Re-agregar si el usuario deshace
                                    // TODO Fase 5: implementar undo completo
                                })
                                .show();
                    }

                    @Override
                    public void onItemClick(FavoritoEntity favorito) {
                        // Navegar al detalle del punto
                        Bundle args = new Bundle();
                        args.putInt("puntoId", favorito.puntoId);
                        args.putInt("recorridoId", -1);
                        Navigation.findNavController(requireView())
                                .navigate(R.id.puntoDetailFragment, args);
                    }
                },

                // OnImagenResolver — obtiene nombre e imagen por puntoId
                (puntoId, callback) -> {
                    viewModel.getPuntoById(puntoId)
                            .observe(getViewLifecycleOwner(), punto -> {
                                if (punto != null) {
                                    callback.onImagenResuelta(
                                            punto.nombre,
                                            punto.imagenUrl,
                                            null
                                    );
                                }
                            });
                }
        );

        binding.rvFavoritos.setLayoutManager(
                new LinearLayoutManager(requireContext()));
        binding.rvFavoritos.setAdapter(adapter);
    }

    private void observarFavoritos() {
        viewModel.favoritos.observe(getViewLifecycleOwner(), favoritos -> {

            if (favoritos == null || favoritos.isEmpty()) {
                binding.layoutEmpty.setVisibility(View.VISIBLE);
                binding.rvFavoritos.setVisibility(View.GONE);
            } else {
                binding.layoutEmpty.setVisibility(View.GONE);
                binding.rvFavoritos.setVisibility(View.VISIBLE);
                adapter.submitList(favoritos);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}