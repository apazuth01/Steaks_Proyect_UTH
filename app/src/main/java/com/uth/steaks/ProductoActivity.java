package com.uth.steaks;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProductoActivity extends AppCompatActivity {

    private Double total = 0.0;
    private Double precio = 0.0;
    private int cant = 1;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();



    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producto);

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

        SharedPreferences sharedPreferences = getSharedPreferences("getToken",MODE_PRIVATE);
        String token = sharedPreferences.getString("token",null);

        SharedPreferences sharedPreferences2 = getSharedPreferences("getProfile",MODE_PRIVATE);
        String Phone = sharedPreferences2.getString("phone",null);
        String Nombre = sharedPreferences2.getString("nombres",null);
        String Apellido = sharedPreferences2.getString("apellidos",null);
        String Foto = sharedPreferences2.getString("foto",null);
        String Ciudad = sharedPreferences2.getString("ciudad",null);


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
                                    maps.put("doc_total", txtTotal.getText().toString());
                                    maps.put("doc_ciudad", Ciudad);
                                    maps.put("doc_orden", "P"+String.valueOf(Generador()));
                                    maps.put("doc_detalles", FieldValue.arrayUnion(MapDetalles));

                                    db.collection("clt_pedidos").document().set(maps);


                                }else{

                                    String id = task.getResult().getDocuments().get(0).getId();


                                    Log.d("TAGResult", "onComplete: Something");
                                    Map<String, Object> MapDetalles = new HashMap<>();
                                    MapDetalles.put("item_cantidad",cant);
                                    MapDetalles.put("item_descripcion",itemDescripcion);
                                    MapDetalles.put("item_titulo",itemTitle);
                                    MapDetalles.put("item_imagen",itemImagen);
                                    MapDetalles.put("item_total",total);
                                    MapDetalles.put("item_precio",precio);

                                    db.collection("clt_pedidos").document(id)
                                            .update(
                                                    "doc_detalles", FieldValue.arrayUnion(MapDetalles)
                                            );

                                }

                            }
                        }
                    });

        });

    }

    private int Generador(){
        return (int)(10000 * Math.random());
    }

}