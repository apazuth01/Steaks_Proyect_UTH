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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrenUser;
    private String credentialID;
    EditText phone, editCode;
    TextView ciudadActual;
    String Phone;
    String code, getCity;
    private String token = "";

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setTitle("Login");

        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        }

        locationStart();

        ciudadActual = findViewById(R.id.txtCiudadActual);


        mAuth  = FirebaseAuth.getInstance();
        mCurrenUser = mAuth.getCurrentUser();

        ProgressBar progressBar =  findViewById(R.id.progressBar);

        phone = findViewById(R.id.editTextPhone);
        editCode = findViewById(R.id.txtCode);

        Button btnInicar = findViewById(R.id.btnIniciar);
        Button btnConfirmar = findViewById(R.id.btnConfirmar);

        btnInicar.setOnClickListener(view -> {
            Phone = phone.getText().toString();

            if (Phone.isEmpty() || Phone.length() < 8){
                progressBar.setVisibility(View.GONE);
            }else{

                progressBar.setVisibility(View.VISIBLE);

                PhoneAuthOptions options =
                        PhoneAuthOptions.newBuilder(mAuth)
                                .setPhoneNumber("+504"+Phone)
                                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                .setActivity(this)                 // Activity (for callback binding)
                                .setCallbacks(mCallBack)          // OnVerificationStateChangedCallbacks
                                .build();
                PhoneAuthProvider.verifyPhoneNumber(options);
            }
        });

        btnConfirmar.setOnClickListener(view -> {
            verifyCode(credentialID,editCode.getText().toString());
        });



        mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                code = phoneAuthCredential.getSmsCode();
                verifyCode(credentialID,code);
                Toast.makeText(getApplicationContext(),code,Toast.LENGTH_LONG).show();
                Log.d("CODE SMS", code);

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.d("CODE SMS", "onVerificationFailed: "+e);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Log.d("CODE SMS", "onCodeSent Token: "+s);
                credentialID = s;
            }



        };


        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    token = task.getResult();
                });

        SharedPreferences sharedPreferences = getSharedPreferences("getToken", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token",token);
        editor.apply();



    }


    private void verifyCode(String credentialId, String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(credentialId,code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    db.collection("clt_clientes")
                            .whereEqualTo("doc_telefono",Phone)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                               @Override
                               public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                   if (task.isSuccessful()) {
                                       if (task.getResult().isEmpty()) {

                                           SharedPreferences sharedPreferences = getSharedPreferences("getProfile", MODE_PRIVATE);
                                           SharedPreferences.Editor editor = sharedPreferences.edit();
                                           editor.putString("phone",Phone);
                                           editor.putString("nombres","Mi Nombre");
                                           editor.putString("apellidos","Mi Apellido");
                                           editor.putString("ciudad",getCity);
                                           editor.putString("foto","https://firebasestorage.googleapis.com/v0/b/provenir-steaks.appspot.com/o/user.png?alt=media&token=c7d36336-2370-4a27-91d5-0a37fc77369b");
                                           editor.apply();

                                           Map<String, Object> maps = new HashMap<>();
                                           maps.put("doc_nombres", "Mi Nombre");
                                           maps.put("doc_apellidos", "Mi Apellido");
                                           maps.put("doc_estado", "Activo");
                                           maps.put("doc_fecha", new Timestamp(new Date()));
                                           maps.put("doc_telefono", Phone);
                                           maps.put("doc_photo", "https://firebasestorage.googleapis.com/v0/b/provenir-steaks.appspot.com/o/user.png?alt=media&token=c7d36336-2370-4a27-91d5-0a37fc77369b");
                                           maps.put("doc_token", token);
                                           if(getCity == null){
                                               maps.put("doc_ciudad", "Sin Ciudad");
                                           }else{
                                               maps.put("doc_ciudad", getCity);
                                           }
                                           maps.put("doc_delivery", false);

                                           db.collection("clt_clientes").document(Phone).set(maps);
                                       }else{
                                           Map<String, Object> map = new HashMap<>();
                                           map.put("doc_token", token);
                                           if(getCity == null){
                                               map.put("doc_ciudad", "Sin Ciudad");
                                           }else{
                                               map.put("doc_ciudad", getCity);
                                           }

                                           db.collection("clt_clientes").document(Phone).update(map);
                                       }
                                   }
                               }
                           });



                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();


                }else{
                    Toast.makeText(LoginActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }

            }
        });
    }




    private void locationStart() {
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Localizacion Local = new Localizacion();
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
        LoginActivity mainActivity;

        public void setMainActivity(LoginActivity mainActivity) {
            this.mainActivity = mainActivity;
        }
        @Override
        public void onLocationChanged(Location loc) {
            this.mainActivity.setLocation(loc);
        }

    }



    @SuppressLint("SetTextI18n")
    public void setLocation(Location loc) {
        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        loc.getLatitude(), loc.getLongitude(), 1);
                if (!list.isEmpty()) {
                    Address city = list.get(0);
                    getCity = city.getLocality();
                    ciudadActual.setText("Ciudad : "+getCity);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }





}