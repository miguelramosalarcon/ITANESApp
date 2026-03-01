package com.example.itanesapp.ui.favoritos;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.itanesapp.R;
import com.example.itanesapp.data.local.entity.FavoritoEntity;
import com.example.itanesapp.databinding.ItemFavoritoBinding;

/**
 * FavoritoAdapter — Adapter para el RecyclerView de favoritos.
 */
public class FavoritoAdapter extends
        ListAdapter<FavoritoEntity, FavoritoAdapter.FavoritoViewHolder> {

    // Callbacks para click e imagen
    private final OnFavoritoClickListener listener;
    private final OnImagenResolver imagenResolver;

    public interface OnFavoritoClickListener {
        void onEliminarClick(FavoritoEntity favorito);
        void onItemClick(FavoritoEntity favorito);
    }

    // Interfaz para obtener la URL de imagen por puntoId
    public interface OnImagenResolver {
        void resolverImagen(int puntoId, ImagenCallback callback);
        interface ImagenCallback {
            void onImagenResuelta(String nombre, String imagenUrl, String fecha);
        }
    }

    private static final DiffUtil.ItemCallback<FavoritoEntity> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<FavoritoEntity>() {
                @Override
                public boolean areItemsTheSame(@NonNull FavoritoEntity oldItem,
                                               @NonNull FavoritoEntity newItem) {
                    return oldItem.id == newItem.id;
                }

                @Override
                public boolean areContentsTheSame(@NonNull FavoritoEntity oldItem,
                                                  @NonNull FavoritoEntity newItem) {
                    return oldItem.puntoId == newItem.puntoId;
                }
            };

    public FavoritoAdapter(OnFavoritoClickListener listener,
                           OnImagenResolver imagenResolver) {
        super(DIFF_CALLBACK);
        this.listener = listener;
        this.imagenResolver = imagenResolver;
    }

    @NonNull
    @Override
    public FavoritoViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                 int viewType) {
        ItemFavoritoBinding binding = ItemFavoritoBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new FavoritoViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritoViewHolder holder,
                                 int position) {
        holder.bind(getItem(position));
    }

    class FavoritoViewHolder extends RecyclerView.ViewHolder {

        private final ItemFavoritoBinding binding;

        public FavoritoViewHolder(@NonNull ItemFavoritoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(FavoritoEntity favorito) {

            // Resolver nombre e imagen desde el puntoId
            imagenResolver.resolverImagen(favorito.puntoId, (nombre, imagenUrl, fecha) -> {

                binding.tvNombre.setText(nombre);

                // Formatear fecha — mostrar solo la parte de la fecha
                if (fecha != null && fecha.length() >= 10) {
                    binding.tvFecha.setText("Guardado: " + fecha.substring(0, 10));
                }

                // Cargar imagen con Glide
                Glide.with(binding.getRoot().getContext())
                        .load(imagenUrl)
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_background)
                        .centerCrop()
                        .into(binding.ivImagen);
            });

            // Click en el item → navegar al detalle del punto
            binding.getRoot().setOnClickListener(v ->
                    listener.onItemClick(favorito));

            // Click en eliminar
            binding.btnEliminar.setOnClickListener(v ->
                    listener.onEliminarClick(favorito));
        }
    }
}