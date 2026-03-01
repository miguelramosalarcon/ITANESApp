package com.example.itanesapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.itanesapp.data.local.entity.FavoritoEntity;
import com.example.itanesapp.data.local.entity.PuntoTuristicoEntity;
import com.example.itanesapp.data.repository.FavoritoRepository;
import com.example.itanesapp.data.repository.RecorridoRepository;

import java.util.List;
/**
 * FavoritosViewModel — ViewModel para FavoritosFragment.
 *
 * Proporciona la lista de favoritos guardados por el usuario.
 * También permite obtener el detalle de cada punto favorito
 * para mostrarlo en el RecyclerView.
 */
public class FavoritosViewModel extends AndroidViewModel {

    private final FavoritoRepository favoritoRepository;
    private final RecorridoRepository recorridoRepository;

    // Lista de todos los favoritos guardados
    public final LiveData<List<FavoritoEntity>> favoritos;

    public FavoritosViewModel(@NonNull Application application) {
        super(application);
        favoritoRepository = new FavoritoRepository(application);
        recorridoRepository = new RecorridoRepository(application);

        // Room emite automáticamente cuando cambia la tabla favoritos
        favoritos = favoritoRepository.getAllFavoritos();
    }

    /**
     * Obtiene el detalle de un punto turístico por su ID.
     * El RecyclerView de favoritos lo usa para mostrar
     * nombre e imagen de cada punto guardado.
     */
    public LiveData<PuntoTuristicoEntity> getPuntoById(int puntoId) {
        return recorridoRepository.getPuntoById(puntoId);
    }

    /**
     * Elimina un punto de favoritos.
     * Llamado al hacer swipe o tocar el ícono de eliminar.
     */
    public void eliminarFavorito(int puntoId) {
        favoritoRepository.eliminarFavorito(puntoId);
    }
}