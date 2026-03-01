package com.example.itanesapp.ui.punto;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.itanesapp.R;
import com.example.itanesapp.data.local.entity.FotoEntity;
import com.example.itanesapp.databinding.ItemFotoBinding;

/**
 * FotoAdapter — Adapter para el ViewPager2 de fotos.
 * Cada página muestra una foto del punto turístico.
 */
public class FotoAdapter extends
        ListAdapter<FotoEntity, FotoAdapter.FotoViewHolder> {

    private static final DiffUtil.ItemCallback<FotoEntity> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<FotoEntity>() {
                @Override
                public boolean areItemsTheSame(@NonNull FotoEntity oldItem,
                                               @NonNull FotoEntity newItem) {
                    return oldItem.id == newItem.id;
                }

                @Override
                public boolean areContentsTheSame(@NonNull FotoEntity oldItem,
                                                  @NonNull FotoEntity newItem) {
                    return oldItem.url.equals(newItem.url);
                }
            };

    public FotoAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public FotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                             int viewType) {
        ItemFotoBinding binding = ItemFotoBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new FotoViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FotoViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class FotoViewHolder extends RecyclerView.ViewHolder {

        private final ItemFotoBinding binding;

        public FotoViewHolder(@NonNull ItemFotoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(FotoEntity foto) {
            Glide.with(binding.getRoot().getContext())
                    .load(foto.url)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .centerCrop()
                    .into(binding.ivFoto);
        }
    }
}