package com.example.itanesapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.itanesapp.data.local.entity.PuntoTuristicoEntity;
import com.example.itanesapp.data.local.entity.RecorridoEntity;
import com.example.itanesapp.data.repository.RecorridoRepository;

import java.util.List;
/**
 * RecorridoViewModel — ViewModel para RecorridoDetailFragment.
 *
 * Proporciona el recorrido seleccionado y sus 5 puntos turísticos.
 * Usa Transformations.switchMap para reaccionar cuando
 * cambia el ID del recorrido seleccionado.
 */
public class RecorridoViewModel extends AndroidViewModel {

    private final RecorridoRepository repository;

    // ID del recorrido actualmente seleccionado
    // MutableLiveData porque el Fragment puede cambiarlo
    private final MutableLiveData<Integer> recorridoIdLiveData = new MutableLiveData<>();

    // Recorrido actual — se actualiza automáticamente
    // cuando recorridoIdLiveData cambia
    public final LiveData<RecorridoEntity> recorrido;

    // Lista de 5 puntos — se actualiza automáticamente
    // cuando recorridoIdLiveData cambia
    public final LiveData<List<PuntoTuristicoEntity>> puntos;

    public RecorridoViewModel(@NonNull Application application) {
        super(application);
        repository = new RecorridoRepository(application);

        // switchMap: cada vez que recorridoIdLiveData emite un nuevo ID,
        // automáticamente consulta Room con ese nuevo ID
        recorrido = Transformations.switchMap(recorridoIdLiveData,
                id -> repository.getRecorridoById(id));

        puntos = Transformations.switchMap(recorridoIdLiveData,
                id -> repository.getPuntosByRecorrido(id));
    }

    /**
     * El Fragment llama este método al recibir el recorridoId
     * como argumento de navegación.
     */
    public void setRecorridoId(int recorridoId) {
        recorridoIdLiveData.setValue(recorridoId);
    }

    /**
     * Expone el ID actual — útil para consultas adicionales.
     */
    public int getRecorridoId() {
        Integer id = recorridoIdLiveData.getValue();
        return id != null ? id : -1;
    }
}
