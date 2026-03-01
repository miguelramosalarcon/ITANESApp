package com.example.itanesapp.ui.recorrido;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.itanesapp.R;
import com.example.itanesapp.data.local.entity.PuntoTuristicoEntity;
import com.example.itanesapp.databinding.ItemPuntoTimelineBinding;

/**
 * PuntoTimelineAdapter — Adapter para la lista de 5 puntos
 * del recorrido en formato timeline.
 */
public class PuntoTimelineAdapter extends
        ListAdapter<PuntoTuristicoEntity, PuntoTimelineAdapter.PuntoViewHolder> {

    private final OnPuntoClickListener listener;

    public interface OnPuntoClickListener {
        void onPuntoClick(PuntoTuristicoEntity punto);
    }

    private static final DiffUtil.ItemCallback<PuntoTuristicoEntity> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<PuntoTuristicoEntity>() {
                @Override
                public boolean areItemsTheSame(@NonNull PuntoTuristicoEntity oldItem,
                                               @NonNull PuntoTuristicoEntity newItem) {
                    return oldItem.id == newItem.id;
                }

                @Override
                public boolean areContentsTheSame(@NonNull PuntoTuristicoEntity oldItem,
                                                  @NonNull PuntoTuristicoEntity newItem) {
                    return oldItem.nombre.equals(newItem.nombre) &&
                            oldItem.orden == newItem.orden;
                }
            };

    public PuntoTimelineAdapter(OnPuntoClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public PuntoViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                              int viewType) {
        ItemPuntoTimelineBinding binding = ItemPuntoTimelineBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new PuntoViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PuntoViewHolder holder, int position) {
        holder.bind(getItem(position), position == getItemCount() - 1);
    }

    class PuntoViewHolder extends RecyclerView.ViewHolder {

        private final ItemPuntoTimelineBinding binding;

        public PuntoViewHolder(@NonNull ItemPuntoTimelineBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        /**
         * @param punto         datos del punto turístico
         * @param esUltimo      si es el último punto, oculta la línea conectora
         */
        public void bind(PuntoTuristicoEntity punto, boolean esUltimo) {

            // Número de orden en el círculo verde
            binding.tvOrden.setText(String.valueOf(punto.orden));

            // Nombre y descripción
            binding.tvNombre.setText(punto.nombre);
            binding.tvDescripcionCorta.setText(punto.descripcion);

            // Ocultar línea conectora en el último punto
            binding.viewLinea.setVisibility(esUltimo ? View.INVISIBLE : View.VISIBLE);

            // Imagen miniatura con Glide
            Glide.with(binding.getRoot().getContext())
                    .load(punto.imagenUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .centerCrop()
                    .into(binding.ivMiniatura);

            // Click navega al detalle del punto
            binding.getRoot().setOnClickListener(v ->
                    listener.onPuntoClick(punto));
        }
    }
}