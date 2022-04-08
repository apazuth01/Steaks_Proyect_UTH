package com.uth.steaks;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uth.steaks.databinding.ActivityMainBinding;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_menu, R.id.navigation_pedidos, R.id.navigation_perfil,R.id.navigation_delivery)
                .build();

        NavController navController = Navigation.findNavController(this,
                R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        SharedPreferences sharedPreferences = getSharedPreferences("getProfile", MODE_PRIVATE);
        String Phone = sharedPreferences.getString("phone",null);

        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
        if(Phone != null){
            db.collection("clt_clientes").document(Phone)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (Objects.equals(task.getResult().get("doc_delivery"), true)){
                            bottomNavigationView.getMenu().getItem(3).setVisible(true);
                        }else{
                            bottomNavigationView.getMenu().getItem(3).setVisible(false);
                        }
                    });
        }else{
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);

        }


    }



}