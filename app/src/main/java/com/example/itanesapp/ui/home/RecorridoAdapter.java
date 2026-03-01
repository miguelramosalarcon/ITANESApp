package com.example.itanesapp.ui.home;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.itanesapp.R;
import com.example.itanesapp.data.local.entity.RecorridoEntity;
import com.example.itanesapp.databinding.ItemRecorridoBinding;

/**
 * RecorridoAdapter — Adapter para el RecyclerView del HomeFragment.
 *
 * Extiende ListAdapter que internamente usa DiffUtil para
 * actualizar solo los items que cambiaron, no toda la lista.
 * Esto es más eficiente que un RecyclerView.Adapter tradicional.
 */
public class RecorridoAdapter extends
        ListAdapter<RecorridoEntity, RecorridoAdapter.RecorridoViewHolder> {

    // Callback para manejar clicks en cada card
    private final OnRecorridoClickListener listener;

    // --------------------------------------------------------
    // Interface para comunicar clicks al Fragment
    // --------------------------------------------------------
    public interface OnRecorridoClickListener {
        void onRecorridoClick(RecorridoEntity recorrido);
    }

    // --------------------------------------------------------
    // DiffUtil — compara items para actualizaciones eficientes
    // --------------------------------------------------------
    private static final DiffUtil.ItemCallback<RecorridoEntity> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<RecorridoEntity>() {

                @Override
                public boolean areItemsTheSame(@NonNull RecorridoEntity oldItem,
                                               @NonNull RecorridoEntity newItem) {
                    // Dos items son el mismo si tienen el mismo ID
                    return oldItem.id == newItem.id;
                }

                @Override
                public boolean areContentsTheSame(@NonNull RecorridoEntity oldItem,
                                                  @NonNull RecorridoEntity newItem) {
                    // El contenido es igual si nombre e imagen no cambiaron
                    return oldItem.nombre.equals(newItem.nombre) &&
                            oldItem.imagenUrl.equals(newItem.imagenUrl);
                }
            };

    // --------------------------------------------------------
    // Constructor
    // --------------------------------------------------------
    public RecorridoAdapter(OnRecorridoClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    // --------------------------------------------------------
    // Crear ViewHolder — infla el layout item_recorrido.xml
    // --------------------------------------------------------
    @NonNull
    @Override
    public RecorridoViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                  int viewType) {
        ItemRecorridoBinding binding = ItemRecorridoBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new RecorridoViewHolder(binding);
    }

    // --------------------------------------------------------
    // Vincular datos al ViewHolder
    // --------------------------------------------------------
    @Override
    public void onBindViewHolder(@NonNull RecorridoViewHolder holder,
                                 int position) {
        holder.bind(getItem(position));
    }

    // ============================================================
    // ViewHolder — contiene las vistas de cada item
    // ============================================================
    class RecorridoViewHolder extends RecyclerView.ViewHolder {

        private final ItemRecorridoBinding binding;

        public RecorridoViewHolder(@NonNull ItemRecorridoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        /**
         * Vincula un RecorridoEntity a las vistas del item.
         */
        public void bind(RecorridoEntity recorrido) {

            // Textos
            binding.tvNombre.setText(recorrido.nombre);
            binding.tvDescripcion.setText(recorrido.descripcion);

            // Puntos y duración
            binding.tvPuntos.setText(
                    binding.getRoot().getContext()
                            .getString(R.string.home_puntos, 5));

            binding.tvDuracion.setText(
                    binding.getRoot().getContext()
                            .getString(R.string.home_duracion, recorrido.duracionHoras));

            // Imagen con Glide
            Glide.with(binding.getRoot().getContext())
                    .load(recorrido.imagenUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .centerCrop()
                    .into(binding.ivPortada);

            // Click en la card
            binding.getRoot().setOnClickListener(v ->
                    listener.onRecorridoClick(recorrido));
        }
    }
}