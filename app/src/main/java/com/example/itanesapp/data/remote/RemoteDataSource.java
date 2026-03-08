package com.example.itanesapp.data.remote;

import android.util.Log;

import com.example.itanesapp.data.local.dao.FotoDao;
import com.example.itanesapp.data.local.dao.PuntoDao;
import com.example.itanesapp.data.local.dao.RecorridoDao;
import com.example.itanesapp.data.local.entity.FotoEntity;
import com.example.itanesapp.data.local.entity.PuntoTuristicoEntity;
import com.example.itanesapp.data.local.entity.RecorridoEntity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * RemoteDataSource — Consume MockAPI y sincroniza
 * los datos con Room.
 *
 * Flujo:
 * 1. Llama GET /recorridos → guarda en Room
 * 2. Llama GET /puntos → guarda puntos y fotos en Room
 * 3. LiveData notifica la UI automáticamente
 */
public class RemoteDataSource {

    private static final String TAG = "RemoteDataSource";

    private final RecorridoDao recorridoDao;
    private final PuntoDao puntoDao;
    private final FotoDao fotoDao;

    public RemoteDataSource(RecorridoDao recorridoDao,
                            PuntoDao puntoDao,
                            FotoDao fotoDao) {
        this.recorridoDao = recorridoDao;
        this.puntoDao = puntoDao;
        this.fotoDao = fotoDao;
    }

    /**
     * Sincroniza recorridos desde MockAPI a Room.
     */
    public void sincronizarRecorridos() {
        MockApiService service = RetrofitClient.getMockService();

        service.getRecorridos().enqueue(new Callback<List<RecorridoRemote>>() {

            @Override
            public void onResponse(Call<List<RecorridoRemote>> call,
                                   Response<List<RecorridoRemote>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Log.e(TAG, "Error recorridos: " + response.code());
                    return;
                }

                List<RecorridoEntity> entities = new ArrayList<>();
                for (RecorridoRemote remote : response.body()) {
                    entities.add(convertirRecorrido(remote));
                }

                // Insertar en Room en background
                new Thread(() -> {
                    recorridoDao.deleteAll();
                    recorridoDao.insertAll(entities);
                    Log.d(TAG, entities.size() + " recorridos sincronizados");
                }).start();
            }

            @Override
            public void onFailure(Call<List<RecorridoRemote>> call,
                                  Throwable t) {
                Log.e(TAG, "Fallo recorridos: " + t.getMessage());
            }
        });
    }

    /**
     * Sincroniza puntos y fotos desde MockAPI a Room.
     */
    public void sincronizarPuntos() {
        MockApiService service = RetrofitClient.getMockService();

        service.getPuntos().enqueue(new Callback<List<PuntoRemote>>() {

            @Override
            public void onResponse(Call<List<PuntoRemote>> call,
                                   Response<List<PuntoRemote>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Log.e(TAG, "Error puntos: " + response.code());
                    return;
                }

                List<PuntoTuristicoEntity> puntoEntities = new ArrayList<>();
                List<FotoEntity> fotoEntities = new ArrayList<>();

                for (PuntoRemote remote : response.body()) {
                    puntoEntities.add(convertirPunto(remote));

                    // Extraer fotos embebidas
                    if (remote.fotos != null) {
                        for (FotoRemote foto : remote.fotos) {
                            fotoEntities.add(convertirFoto(
                                    foto,
                                    Integer.parseInt(remote.id)
                            ));
                        }
                    }
                }

                // Insertar en Room en background
                new Thread(() -> {
                    puntoDao.deleteAll();
                    fotoDao.deleteAll();
                    puntoDao.insertAll(puntoEntities);
                    fotoDao.insertAll(fotoEntities);
                    Log.d(TAG, puntoEntities.size()
                            + " puntos y "
                            + fotoEntities.size()
                            + " fotos sincronizados");
                }).start();
            }

            @Override
            public void onFailure(Call<List<PuntoRemote>> call,
                                  Throwable t) {
                Log.e(TAG, "Fallo puntos: " + t.getMessage());
            }
        });
    }

    // -------------------------------------------------------
    // Conversores Remote → Entity
    // -------------------------------------------------------

    private RecorridoEntity convertirRecorrido(RecorridoRemote r) {
        RecorridoEntity entity = new RecorridoEntity(
                r.nombre,
                r.descripcion,
                r.imagenUrl,
                r.duracionHoras
        );
        entity.id = Integer.parseInt(r.id);
        return entity;
    }

    private PuntoTuristicoEntity convertirPunto(PuntoRemote p) {
        PuntoTuristicoEntity entity = new PuntoTuristicoEntity(
                Integer.parseInt(p.recorridoId),
                p.nombre,
                p.descripcion,
                p.latitud,
                p.longitud,
                p.orden,
                p.imagenUrl
        );
        entity.id = Integer.parseInt(p.id);
        return entity;
    }

    private FotoEntity convertirFoto(FotoRemote f, int puntoId) {
        return new FotoEntity(puntoId, f.url, f.caption);
    }
}