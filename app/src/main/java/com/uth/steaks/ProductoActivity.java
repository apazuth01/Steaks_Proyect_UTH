package com.uth.steaks;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class ProductoActivity extends AppCompatActivity {

    private Double total = 0.0;
    private Double precio = 0.0;
    private int cant = 1;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private Double Lat,Lng;
    private String Ciudad, Direccion,token;



    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producto);

        locationStart();

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Bundle bundle = this.getIntent().getExtras();
        String itemDescripcion = bundle.getString("descripcion");
        String itemTitle = bundle.getString("titulo");
        String itemImagen = bundle.getString("imagen");
        String itemPrecio = bundle.getString("precio");
        precio = Double.parseDouble(itemPrecio);

        this.setTitle(itemTitle);

        TextView txtId = findViewById(R.id.txtDescripcion);
        txtId.setText(itemDescripcion);

        TextView txtPrecio = findViewById(R.id.txtPrecio);
        txtPrecio.setText("Lps. " + itemPrecio);

        TextView txtTotal = findViewById(R.id.txtTotal);
        txtTotal.setText("Lps. " + itemPrecio);

        total = Double.parseDouble(itemPrecio);

        ImageView imgImagen = findViewById(R.id.itemImage);
        Glide.with(this).load(itemImagen).into(imgImagen);

        Button btnMin = findViewById(R.id.btnMin);
        Button btnMax = findViewById(R.id.btnMax);

        EditText inputCantidad = findViewById(R.id.inputCantidad);

        btnMin.setOnClickListener(view -> {
            if (cant > 1){
                cant = cant - 1;
                inputCantidad.setText(String.valueOf(cant));
                total = precio * cant;
                txtTotal.setText("Lps. " + total);
            }
        });

        btnMax.setOnClickListener(view -> {
                cant =  cant + 1;
                inputCantidad.setText(String.valueOf(cant));
                total = precio * cant;
                txtTotal.setText("Lps. " + total);
        });

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    token = task.getResult();
                });

        SharedPreferences sharedPreferences2 = getSharedPreferences("getProfile",MODE_PRIVATE);
        String Phone = sharedPreferences2.getString("phone",null);
        String Nombre = sharedPreferences2.getString("nombres",null);
        String Apellido = sharedPreferences2.getString("apellidos",null);
        String Foto = sharedPreferences2.getString("foto",null);


        Log.d("TAGRessss", "onCreate: "+Phone);

        Button btnEnviar = findViewById(R.id.btnEnviar);
        btnEnviar.setOnClickListener(view ->{

            db.collection("clt_pedidos")
                    .whereEqualTo("doc_telefono",Phone)
                    .whereEqualTo("doc_estado","Editando")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                if (task.getResult().isEmpty()){
                                    String pOrden = "P"+String.valueOf(Generador());
                                    GeoPoint geoPoint = new GeoPoint(Lat, Lng);


                                    Map<String, Object> MapDetalles = new HashMap<>();
                                    MapDetalles.put("item_cantidad",cant);
                                    MapDetalles.put("item_descripcion",itemDescripcion);
                                    MapDetalles.put("item_titulo",itemTitle);
                                    MapDetalles.put("item_imagen",itemImagen);
                                    MapDetalles.put("item_total",total);
                                    MapDetalles.put("item_precio",precio);

                                    Map<String, Object> maps = new HashMap<>();
                                    maps.put("doc_cliente", Nombre+" "+Apellido);
                                    maps.put("doc_estado", "Editando");
                                    maps.put("doc_fecha", new Timestamp(new Date()));
                                    maps.put("doc_telefono", Phone);
                                    maps.put("doc_photo", Foto);
                                    maps.put("doc_etapa",0);
                                    maps.put("doc_token", token);
                                    maps.put("doc_total", Double.parseDouble(itemPrecio));
                                    maps.put("doc_ciudad", Ciudad);
                                    maps.put("doc_geo", geoPoint );
                                    maps.put("doc_direccion", Direccion);
                                    maps.put("doc_orden", pOrden);
                                    maps.put("doc_detalles", FieldValue.arrayUnion(MapDetalles));

                                    db.collection("clt_pedidos").document(pOrden).set(maps);

                                    Toast.makeText(getApplicationContext(),"Su Orden ha sido creada.",Toast.LENGTH_LONG).show();

                                    finish();


                                }else{

                                    String id = task.getResult().getDocuments().get(0).getId();

                                    Map<String, Object> MapDetalles = new HashMap<>();
                                    MapDetalles.put("item_cantidad",cant);
                                    MapDetalles.put("item_descripcion",itemDescripcion);
                                    MapDetalles.put("item_titulo",itemTitle);
                                    MapDetalles.put("item_imagen",itemImagen);
                                    MapDetalles.put("item_total",total);
                                    MapDetalles.put("item_precio",precio);

                                    db.collection("clt_pedidos").document(id)
                                            .update(
                                                    "doc_detalles", FieldValue.arrayUnion(MapDetalles),
                                                    "doc_total",Double.parseDouble(Objects.requireNonNull(task.getResult().getDocuments().get(0).get("doc_total")).toString()) +total
                                            );

                                    Toast.makeText(getApplicationContext(),"El producto ha sido agregado al pedido. ",Toast.LENGTH_LONG).show();

                                    finish();

                                }

                            }
                        }
                    });

        });

    }

    private int Generador(){
        return (int)(10000 * Math.random());
    }

    private void locationStart() {
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        ProductoActivity.Localizacion Local = new Localizacion();
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
        ProductoActivity mainActivity;

        public void setMainActivity(ProductoActivity mainActivity) {
            this.mainActivity = mainActivity;
        }
        @Override
        public void onLocationChanged(Location loc) {
            this.mainActivity.setLocation(loc);
        }

    }

    public void setLocation(Location loc) {
        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        loc.getLatitude(), loc.getLongitude(), 1);
                if (!list.isEmpty()) {
                    Address city = list.get(0);
                    Ciudad = city.getLocality();
                    Lat = city.getLatitude();
                    Lng = city.getLongitude();
                    Direccion = city.getAddressLine(0);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}