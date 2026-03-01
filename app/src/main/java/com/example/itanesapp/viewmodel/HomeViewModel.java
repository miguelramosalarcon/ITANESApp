package com.example.itanesapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.itanesapp.data.local.entity.RecorridoEntity;
import com.example.itanesapp.data.repository.RecorridoRepository;

import java.util.List;
/**
 * HomeViewModel — ViewModel para HomeFragment.
 *
 * Proporciona la lista de recorridos a la UI.
 * Sobrevive rotaciones de pantalla sin perder datos.
 *
 * Extiende AndroidViewModel en lugar de ViewModel
 * porque necesita el Application para instanciar el Repository.
 */
public class HomeViewModel extends AndroidViewModel {

    private final RecorridoRepository repository;

    // LiveData con la lista de recorridos
    // La UI observa este campo directamente
    private final LiveData<List<RecorridoEntity>> recorridos;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        repository = new RecorridoRepository(application);

        // Se carga una sola vez al crear el ViewModel
        // Room actualiza automáticamente vía LiveData
        recorridos = repository.getAllRecorridos();
    }

    /**
     * Expone los recorridos al Fragment.
     * El Fragment observa este LiveData y actualiza el RecyclerView.
     */
    public LiveData<List<RecorridoEntity>> getRecorridos() {
        return recorridos;
    }
}
