package com.uth.steaks;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng lg;
    private Double lat,lng;
    private Double clat,clng;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String Pedido,Cliente,Direccion;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        TextView txtCliente = findViewById(R.id.Cliente);
        txtCliente.setText("Nombre del Cliente: "+Cliente);

        TextView txtDir = findViewById(R.id.dir);
        txtDir.setText("Dirección: "+Direccion);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {

        Bundle bundle = this.getIntent().getExtras();
        Cliente = (String) bundle.get("Cliente");
        Direccion = (String) bundle.get("Direccion");
        Pedido = (String) bundle.get("Pedido");
        Log.d("TAGGEO", "onCreate: "+Pedido);

        db.collection("clt_pedidos").document(Pedido)
                .get()
                .addOnCompleteListener(task -> {
                    clat = Objects.requireNonNull(task.getResult().getGeoPoint("doc_geo")).getLatitude();
                    clng = Objects.requireNonNull(task.getResult().getGeoPoint("doc_geo")).getLongitude();
                });
        locationStart();
        return super.onCreateView(name, context, attrs);
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        LatLng Cliente = new LatLng(clat,clng);


        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Cliente,14));

        mMap.addMarker(
                new MarkerOptions()
                        .position(Cliente)
                        .title("Cliente")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin)
                        )
        );

        LatLng geoDelivery = new LatLng(lat, lng);
        mMap.addMarker(
                new MarkerOptions()
                        .position(geoDelivery)
                        .title("Aqui estoy")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.delivery)
                        )
        );

        Button btnEntregar = findViewById(R.id.btnEntregado);

        btnEntregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> map = new HashMap<>();
                map.put("doc_estado", "Entregado");
                map.put("doc_fecha", new Timestamp(new Date()));

                db.collection("clt_pedidos").document(Pedido).update(map);

                Toast.makeText(getApplicationContext(),"Su Orden ha sido entregada.",Toast.LENGTH_LONG).show();
                finish();
            }
        });

    }



    private void locationStart() {
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        MapActivity.Localizacion Local = new Localizacion();
        Local.setMainActivity(this);
        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        }

        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) Local);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) Local);

    }



    public static class Localizacion implements LocationListener {
        MapActivity mainActivity;

        public void setMainActivity(MapActivity mainActivity) {
            this.mainActivity = mainActivity;
        }
        @Override
        public void onLocationChanged(Location loc) {
            this.mainActivity.setLocation(loc);
        }


    }



    public void setLocation(Location loc) {
                    lat = loc.getLatitude();
                    lng = loc.getLongitude();


        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        loc.getLatitude(), loc.getLongitude(), 1);
                if (!list.isEmpty()) {
                    Address geo = list.get(0);
                    lat = geo.getLatitude();
                    lng = geo.getLongitude();


                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }







}