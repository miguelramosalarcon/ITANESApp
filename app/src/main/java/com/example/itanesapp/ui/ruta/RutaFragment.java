package com.example.itanesapp.ui.ruta;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

//import com.example.itanesapp.BuildConfig;
import com.example.itanesapp.data.local.entity.PuntoTuristicoEntity;
import com.example.itanesapp.data.remote.RetrofitClient;
import com.example.itanesapp.data.remote.RouteRequestBody;
import com.example.itanesapp.data.remote.RouteResponse;
import com.example.itanesapp.databinding.FragmentRutaBinding;
import com.example.itanesapp.utils.OsmConfig;
import com.example.itanesapp.viewmodel.RecorridoViewModel;

import org.osmdroid.api.IMapController;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RutaFragment extends Fragment {

    private static final String TAG = "RutaFragment";

    // ⚠️ REEMPLAZA CON TU API KEY DE OPENROUTESERVICE
    private static final String ORS_API_KEY = "eyJvcmciOiI1YjNjZTM1OTc4NTExMTAwMDFjZjYyNDgiLCJpZCI6IjU3YTBiMWEzNWE4ZDQ2ZjU5N2RkYzY0NzFiZTA4ODg3IiwiaCI6Im11cm11cjY0In0=";

    private FragmentRutaBinding binding;
    private RecorridoViewModel viewModel;
    private int recorridoId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentRutaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            recorridoId = getArguments().getInt("recorridoId", -1);
        }

        configurarMapa();
        configurarViewModel();
        observarDatos();
    }

    private void configurarMapa() {
        OsmConfig.configurarMapa(binding.mapa);
    }

    private void configurarViewModel() {
        viewModel = new ViewModelProvider(this)
                .get(RecorridoViewModel.class);
        viewModel.setRecorridoId(recorridoId);
    }

    private void observarDatos() {

        viewModel.recorrido.observe(getViewLifecycleOwner(), recorrido -> {
            if (recorrido == null) return;
            binding.tvNombreRecorrido.setText(recorrido.nombre);
        });

        viewModel.puntos.observe(getViewLifecycleOwner(), puntos -> {
            if (puntos == null || puntos.isEmpty()) return;

            // Limpiar overlays previos
            binding.mapa.getOverlays().clear();

            // Colocar marcadores en el mapa
            List<GeoPoint> geoPoints = colocarMarcadores(puntos);

            // Centrar mapa
            IMapController controller = binding.mapa.getController();
            controller.setZoom(14.0);
            controller.setCenter(geoPoints.get(0));
            binding.mapa.invalidate();

            // Solicitar ruta real a ORS
            solicitarRuta(puntos);

            // Botón iniciar navegación
            binding.btnIniciarRuta.setOnClickListener(v ->
                    Toast.makeText(requireContext(),
                            "Navegación en Fase 5", Toast.LENGTH_SHORT).show()
            );
        });
    }

    /**
     * Coloca marcadores numerados en el mapa.
     */
    private List<GeoPoint> colocarMarcadores(
            List<PuntoTuristicoEntity> puntos) {

        List<GeoPoint> geoPoints = new ArrayList<>();

        for (PuntoTuristicoEntity punto : puntos) {
            GeoPoint geoPoint = new GeoPoint(
                    punto.latitud, punto.longitud);
            geoPoints.add(geoPoint);

            Marker marcador = new Marker(binding.mapa);
            marcador.setPosition(geoPoint);
            marcador.setTitle(punto.nombre);
            marcador.setSnippet("Punto " + punto.orden
                    + " — Toca para más info");
            marcador.setAnchor(
                    Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

            binding.mapa.getOverlays().add(marcador);
        }

        return geoPoints;
    }

    /**
     * Solicita la ruta real a OpenRouteService.
     * Convierte coordenadas a formato GeoJSON [lon, lat].
     */
    private void solicitarRuta(List<PuntoTuristicoEntity> puntos) {
        binding.progressRuta.setVisibility(View.VISIBLE);

        // ORS usa [longitud, latitud] — orden invertido vs OSMDroid
        List<List<Double>> coordenadas = new ArrayList<>();
        for (PuntoTuristicoEntity punto : puntos) {
            coordenadas.add(Arrays.asList(punto.longitud, punto.latitud));
        }

        RouteRequestBody body = new RouteRequestBody(coordenadas);

        RetrofitClient.getRouteService()
                .getRoute(ORS_API_KEY, "driving-car", body)
                .enqueue(new Callback<RouteResponse>() {

                    @Override
                    public void onResponse(@NonNull Call<RouteResponse> call,
                                           @NonNull Response<RouteResponse> response) {
                        binding.progressRuta.setVisibility(View.GONE);

                        if (!response.isSuccessful()
                                || response.body() == null
                                || response.body().features == null
                                || response.body().features.isEmpty()) {
                            Log.e(TAG, "Respuesta vacía o error: "
                                    + response.code());
                            dibujarLineaSimple(puntos);
                            return;
                        }

                        // Extraer coordenadas de la ruta
                        List<List<Double>> coords = response.body()
                                .features.get(0).geometry.coordinates;

                        // Extraer distancia y duración
                        RouteResponse.Segment seg = response.body()
                                .features.get(0).properties.segments.get(0);

                        dibujarRuta(coords, seg);
                    }

                    @Override
                    public void onFailure(@NonNull Call<RouteResponse> call,
                                          @NonNull Throwable t) {
                        binding.progressRuta.setVisibility(View.GONE);
                        Log.e(TAG, "Error llamando ORS: " + t.getMessage());
                        // Fallback: línea recta entre puntos
                        dibujarLineaSimple(puntos);
                    }
                });
    }

    /**
     * Dibuja la polilínea real de la ruta sobre el mapa.
     */
    private void dibujarRuta(List<List<Double>> coords,
                             RouteResponse.Segment segmento) {
        if (binding == null) return;

        List<GeoPoint> rutaPoints = new ArrayList<>();
        for (List<Double> coord : coords) {
            // ORS devuelve [lon, lat] → convertir a GeoPoint(lat, lon)
            rutaPoints.add(new GeoPoint(coord.get(1), coord.get(0)));
        }

        Polyline ruta = new Polyline();
        ruta.setPoints(rutaPoints);
        ruta.getOutlinePaint().setColor(Color.parseColor("#1B5E20"));
        ruta.getOutlinePaint().setStrokeWidth(10f);
        binding.mapa.getOverlays().add(0, ruta);
        binding.mapa.invalidate();

        // Mostrar distancia y tiempo
        double km = segmento.distance / 1000;
        double min = segmento.duration / 60;
        binding.tvDistancia.setText(
                String.format("%.1f km  •  %.0f min en auto", km, min));
    }

    /**
     * Fallback: línea recta si ORS falla o no hay internet.
     */
    private void dibujarLineaSimple(List<PuntoTuristicoEntity> puntos) {
        if (binding == null) return;

        List<GeoPoint> points = new ArrayList<>();
        for (PuntoTuristicoEntity p : puntos) {
            points.add(new GeoPoint(p.latitud, p.longitud));
        }

        Polyline linea = new Polyline();
        linea.setPoints(points);
        linea.getOutlinePaint().setColor(Color.parseColor("#E65100"));
        linea.getOutlinePaint().setStrokeWidth(6f);
        linea.getOutlinePaint().setAlpha(180);
        binding.mapa.getOverlays().add(0, linea);
        binding.mapa.invalidate();

        binding.tvDistancia.setText(calcularDistanciaTotal(points));
    }

    private String calcularDistanciaTotal(List<GeoPoint> puntos) {
        double totalMetros = 0;
        for (int i = 0; i < puntos.size() - 1; i++) {
            totalMetros += puntos.get(i)
                    .distanceToAsDouble(puntos.get(i + 1));
        }
        return totalMetros < 1000
                ? String.format("Distancia aprox: %.0f m", totalMetros)
                : String.format("Distancia aprox: %.1f km", totalMetros / 1000);
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.mapa.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.mapa.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}