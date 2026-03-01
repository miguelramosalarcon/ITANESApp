package com.example.itanesapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.itanesapp.data.local.entity.FotoEntity;
import com.example.itanesapp.data.local.entity.PuntoTuristicoEntity;
import com.example.itanesapp.data.repository.FavoritoRepository;
import com.example.itanesapp.data.repository.RecorridoRepository;

import java.util.List;
/**
 * PuntoViewModel — ViewModel para PuntoDetailFragment.
 *
 * Proporciona:
 * - Detalle del punto turístico actual
 * - Fotos del punto para el ViewPager2
 * - Estado de favorito (♡) del punto
 * - Navegación anterior/siguiente entre puntos
 */
public class PuntoViewModel extends AndroidViewModel {

    private final RecorridoRepository recorridoRepository;
    private final FavoritoRepository favoritoRepository;

    // ID del punto actualmente visible
    private final MutableLiveData<Integer> puntoIdLiveData = new MutableLiveData<>();

    // ID del recorrido al que pertenece el punto
    // Necesario para navegar anterior/siguiente
    private final MutableLiveData<Integer> recorridoIdLiveData = new MutableLiveData<>();

    // Punto actual — se actualiza con switchMap
    public final LiveData<PuntoTuristicoEntity> punto;

    // Fotos del punto — para el ViewPager2
    public final LiveData<List<FotoEntity>> fotos;

    // Estado favorito: 1 = es favorito, 0 = no lo es
    public final LiveData<Integer> esFavorito;

    public PuntoViewModel(@NonNull Application application) {
        super(application);
        recorridoRepository = new RecorridoRepository(application);
        favoritoRepository = new FavoritoRepository(application);

        // Cada vez que puntoIdLiveData cambia, Room retorna
        // automáticamente los nuevos datos vía LiveData
        punto = Transformations.switchMap(puntoIdLiveData,
                id -> recorridoRepository.getPuntoById(id));

        fotos = Transformations.switchMap(puntoIdLiveData,
                id -> recorridoRepository.getFotosByPunto(id));

        esFavorito = Transformations.switchMap(puntoIdLiveData,
                id -> favoritoRepository.isFavorito(id));
    }

    /**
     * El Fragment llama este método al recibir los argumentos
     * de navegación (puntoId y recorridoId).
     */
    public void setPunto(int puntoId, int recorridoId) {
        puntoIdLiveData.setValue(puntoId);
        recorridoIdLiveData.setValue(recorridoId);
    }

    /**
     * Obtiene el siguiente punto en el recorrido.
     * @param ordenActual orden del punto visible (1-5)
     */
    public LiveData<PuntoTuristicoEntity> getSiguientePunto(int ordenActual) {
        Integer recorridoId = recorridoIdLiveData.getValue();
        if (recorridoId == null) return new MutableLiveData<>(null);
        return recorridoRepository.getSiguientePunto(recorridoId, ordenActual);
    }

    /**
     * Obtiene el punto anterior en el recorrido.
     * @param ordenActual orden del punto visible (1-5)
     */
    public LiveData<PuntoTuristicoEntity> getAnteriorPunto(int ordenActual) {
        Integer recorridoId = recorridoIdLiveData.getValue();
        if (recorridoId == null) return new MutableLiveData<>(null);
        return recorridoRepository.getAnteriorPunto(recorridoId, ordenActual);
    }

    /**
     * Toggle favorito — alterna entre guardar y quitar.
     * El Fragment lo llama cuando el usuario toca el botón ♡.
     */
    public void toggleFavorito() {
        Integer puntoId = puntoIdLiveData.getValue();
        Integer esFav = esFavorito.getValue();
        if (puntoId == null) return;

        // Si esFavorito es null o 0, no es favorito → agregar
        boolean activo = esFav != null && esFav > 0;
        favoritoRepository.toggleFavorito(puntoId, activo);
    }
}
