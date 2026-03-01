package com.example.itanesapp.data.local;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.itanesapp.data.local.dao.FavoritoDao;
import com.example.itanesapp.data.local.dao.FotoDao;
import com.example.itanesapp.data.local.dao.PuntoDao;
import com.example.itanesapp.data.local.dao.RecorridoDao;
import com.example.itanesapp.data.local.entity.FavoritoEntity;
import com.example.itanesapp.data.local.entity.FotoEntity;
import com.example.itanesapp.data.local.entity.PuntoTuristicoEntity;
import com.example.itanesapp.data.local.entity.RecorridoEntity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ITANESDatabase — Base de datos principal de la app.
 *
 * Registra las 4 entidades (tablas) y expone los DAOs.
 * Implementa el patrón Singleton para evitar múltiples
 * instancias abiertas de la base de datos simultáneamente.
 *
 * version = 1 → si cambias entidades en el futuro,
 * debes incrementar este número y agregar una Migration.
 */
@Database(
        entities = {
                RecorridoEntity.class,
                PuntoTuristicoEntity.class,
                FotoEntity.class,
                FavoritoEntity.class
        },
        version = 1,
        exportSchema = false
)
public abstract class ITANESDatabase extends RoomDatabase {

    // --------------------------------------------------------
    // Nombre del archivo SQLite en el dispositivo
    // --------------------------------------------------------
    private static final String DATABASE_NAME = "itanes_database";

    // --------------------------------------------------------
    // Singleton — única instancia de la base de datos
    // volatile garantiza visibilidad entre hilos
    // --------------------------------------------------------
    private static volatile ITANESDatabase INSTANCE;

    // --------------------------------------------------------
    // Executor para operaciones de BD en hilo secundario
    // Room NO permite operaciones en el hilo principal (UI thread)
    // Usamos 4 hilos para operaciones paralelas
    // --------------------------------------------------------
    public static final ExecutorService databaseExecutor =
            Executors.newFixedThreadPool(4);

    // --------------------------------------------------------
    // DAOs — Room genera la implementación automáticamente
    // --------------------------------------------------------
    public abstract RecorridoDao recorridoDao();
    public abstract PuntoDao puntoDao();
    public abstract FotoDao fotoDao();
    public abstract FavoritoDao favoritoDao();

    // --------------------------------------------------------
    // getInstance — Patrón Singleton con doble verificación
    // --------------------------------------------------------
    public static ITANESDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (ITANESDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    ITANESDatabase.class,
                                    DATABASE_NAME
                            )
                            // Popula la BD con datos mock la primera vez
                            .addCallback(prepoblarCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // --------------------------------------------------------
    // Callback de prepoblado — se ejecuta una sola vez
    // cuando la BD se crea por primera vez en el dispositivo
    // --------------------------------------------------------
    private static final RoomDatabase.Callback prepoblarCallback =
            new RoomDatabase.Callback() {
                @Override
                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                    super.onCreate(db);

                    // Insertar datos en hilo secundario
                    databaseExecutor.execute(() -> {
                        if (INSTANCE != null) {
                            prepoblarDatos(INSTANCE);
                        }
                    });
                }
            };

    // --------------------------------------------------------
    // Datos mock — 2 recorridos × 5 puntos turísticos
    // Coordenadas reales de Lima, Perú
    // --------------------------------------------------------
    private static void prepoblarDatos(ITANESDatabase db) {

        RecorridoDao recorridoDao = db.recorridoDao();
        PuntoDao puntoDao = db.puntoDao();
        FotoDao fotoDao = db.fotoDao();

        // Solo insertar si la BD está vacía
        if (recorridoDao.getCount() > 0) return;

        // ------------------------------------------------
        // RECORRIDO 1 — Centro Histórico de Lima
        // ------------------------------------------------
        RecorridoEntity r1 = new RecorridoEntity(
                "Centro Histórico de Lima",
                "Recorre los principales monumentos y plazas del corazón de Lima, Patrimonio de la Humanidad.",
                "https://upload.wikimedia.org/wikipedia/commons/d/d1/Plaza_Mayor_Lima.jpg",
                3.0
        );
        recorridoDao.insert(r1);

        // Necesitamos el ID generado — lo obtenemos con una query directa
        // Room autoincrementa desde 1
        int idR1 = 1;

        // 5 Puntos del Recorrido 1
        puntoDao.insertAll(java.util.Arrays.asList(
                new PuntoTuristicoEntity(idR1,
                        "Plaza Mayor de Lima",
                        "El corazón histórico de Lima. Rodeada por la Catedral, el Palacio de Gobierno y la Municipalidad.",
                        -12.0464, -77.0428, 1,
                        "https://upload.wikimedia.org/wikipedia/commons/d/d1/Plaza_Mayor_Lima.jpg"),

                new PuntoTuristicoEntity(idR1,
                        "Catedral de Lima",
                        "Imponente catedral construida en 1535. Alberga los restos de Francisco Pizarro.",
                        -12.0461, -77.0318, 2,
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/9/9e/Catedral_de_Lima.jpg/640px-Catedral_de_Lima.jpg"),

                new PuntoTuristicoEntity(idR1,
                        "Convento de San Francisco",
                        "Famoso por sus catacumbas subterráneas con osarios. Joya del arte barroco colonial.",
                        -12.0453, -77.0289, 3,
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3e/Convento_de_San_Francisco_Lima.jpg/640px-Convento_de_San_Francisco_Lima.jpg"),

                new PuntoTuristicoEntity(idR1,
                        "Palacio Torre Tagle",
                        "Magnífico palacio del siglo XVIII. Ejemplo perfecto de la arquitectura civil limeña.",
                        -12.0518, -77.0289, 4,
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b4/Torre_Tagle_Palace.jpg/640px-Torre_Tagle_Palace.jpg"),

                new PuntoTuristicoEntity(idR1,
                        "Puente de los Suspiros",
                        "Puente de madera centenario en Barranco. Cuenta la leyenda que quien lo cruza con los ojos cerrados, su amor se hará realidad.",
                        -12.1500, -77.0211, 5,
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a1/Puente_de_los_Suspiros.jpg/640px-Puente_de_los_Suspiros.jpg")
        ));

        // ------------------------------------------------
        // RECORRIDO 2 — Miraflores y Barranco
        // ------------------------------------------------
        RecorridoEntity r2 = new RecorridoEntity(
                "Miraflores y Barranco",
                "Descubre los distritos más modernos y bohemios de Lima, con vista al Océano Pacífico.",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/4/4e/Miraflores_Lima.jpg/1280px-Miraflores_Lima.jpg",
                4.0
        );
        recorridoDao.insert(r2);

        int idR2 = 2;

        // 5 Puntos del Recorrido 2
        puntoDao.insertAll(java.util.Arrays.asList(
                new PuntoTuristicoEntity(idR2,
                        "Parque Kennedy",
                        "Parque central de Miraflores, famoso por sus gatos residentes y rodeado de cafés y galerías.",
                        -12.1211, -77.0283, 1,
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e5/Parque_Kennedy_Miraflores.jpg/640px-Parque_Kennedy_Miraflores.jpg"),

                new PuntoTuristicoEntity(idR2,
                        "Parque del Amor",
                        "Mirador romántico frente al mar con la famosa escultura 'El Beso' de Victor Delfín.",
                        -12.1308, -77.0325, 2,
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/2/2e/Parque_del_Amor_Lima.jpg/640px-Parque_del_Amor_Lima.jpg"),

                new PuntoTuristicoEntity(idR2,
                        "Larcomar",
                        "Centro comercial y de entretenimiento construido sobre los acantilados de Miraflores con vista al Pacífico.",
                        -12.1319, -77.0350, 3,
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/1/1e/Larcomar_Miraflores.jpg/640px-Larcomar_Miraflores.jpg"),

                new PuntoTuristicoEntity(idR2,
                        "Bajada de Baños",
                        "Colorida bajada bohemia que conecta Barranco con la playa. Murales y arte urbano en cada esquina.",
                        -12.1489, -77.0211, 4,
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b2/Bajada_de_Banos_Barranco.jpg/640px-Bajada_de_Banos_Barranco.jpg"),

                new PuntoTuristicoEntity(idR2,
                        "Museo Mario Testino (MATE)",
                        "Museo dedicado al célebre fotógrafo peruano Mario Testino. Arte y moda en una casona colonial.",
                        -12.1478, -77.0228, 5,
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/m/ma/MATE_Museum_Barranco.jpg/640px-MATE_Museum_Barranco.jpg")
        ));

        // ------------------------------------------------
        // Fotos adicionales para el ViewPager2
        // 3 fotos por punto del Recorrido 1
        // ------------------------------------------------
        fotoDao.insertAll(java.util.Arrays.asList(
                // Fotos Plaza Mayor (punto 1)
                new FotoEntity(1, "https://upload.wikimedia.org/wikipedia/commons/thumb/5/5e/Lima_-_Plaza_Mayor.jpg/640px-Lima_-_Plaza_Mayor.jpg", "Vista frontal"),
                new FotoEntity(1, "https://upload.wikimedia.org/wikipedia/commons/thumb/5/5e/Lima_-_Plaza_Mayor.jpg/320px-Lima_-_Plaza_Mayor.jpg", "Detalle fuente"),
                new FotoEntity(1, "https://upload.wikimedia.org/wikipedia/commons/thumb/5/5e/Lima_-_Plaza_Mayor.jpg/480px-Lima_-_Plaza_Mayor.jpg", "Vista nocturna"),

                // Fotos Catedral (punto 2)
                new FotoEntity(2, "https://upload.wikimedia.org/wikipedia/commons/thumb/9/9e/Catedral_de_Lima.jpg/640px-Catedral_de_Lima.jpg", "Fachada principal"),
                new FotoEntity(2, "https://upload.wikimedia.org/wikipedia/commons/thumb/9/9e/Catedral_de_Lima.jpg/320px-Catedral_de_Lima.jpg", "Interior"),
                new FotoEntity(2, "https://upload.wikimedia.org/wikipedia/commons/thumb/9/9e/Catedral_de_Lima.jpg/480px-Catedral_de_Lima.jpg", "Torres")
        ));
    }
}