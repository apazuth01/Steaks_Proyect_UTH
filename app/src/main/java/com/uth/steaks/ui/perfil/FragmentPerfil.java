package com.uth.steaks.ui.perfil;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uth.steaks.R;

import java.util.HashMap;
import java.util.Map;

public class FragmentPerfil extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    String Phone;

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("getProfile",MODE_PRIVATE);
        Phone = sharedPreferences.getString("phone",null);

        Button btnCerrar = view.findViewById(R.id.btnCerarSesion);

        TextInputEditText txtNombre = view.findViewById(R.id.txtNombre);
        TextInputEditText txtApellido = view.findViewById(R.id.txtApellido);
        TextInputEditText txtCiudad= view.findViewById(R.id.txtCiudad);


        btnCerrar.setOnClickListener(view1 -> {
            FirebaseAuth.getInstance().signOut();
            requireActivity().finish();
        });

        Button btnGuardar = view.findViewById(R.id.btnGuardar);
        btnGuardar.setOnClickListener(view1 -> {

            SharedPreferences sharedPreferences2 = getContext().getSharedPreferences("getProfile", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences2.edit();
            editor.putString("phone",Phone);
            editor.putString("nombres",txtNombre.getText().toString());
            editor.putString("apellidos",txtApellido.getText().toString());
            editor.putString("ciudad",txtCiudad.getText().toString());
            editor.apply();


            Map<String, Object> map = new HashMap<>();
            map.put("doc_nombres", txtNombre.getText().toString());
            map.put("doc_apellidos", txtApellido.getText().toString());
            map.put("doc_ciudad", txtCiudad.getText().toString());

            db.collection("clt_clientes").document(Phone).update(map);
            Toast.makeText(getContext(),"Datos Actualizados Exitosamente",Toast.LENGTH_LONG).show();
        });


        Log.d("TAG Phone", "onCreateView: " + Phone );
        DocumentReference docRef = db.collection("clt_clientes").document(Phone);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();

                if (document.exists()) {

                    TextView txtPhone = view.findViewById(R.id.txtPhone);
                    txtPhone.setText("+504"+document.get("doc_telefono").toString());

                    txtNombre.setText(document.get("doc_nombres").toString());

                    txtApellido.setText(document.get("doc_apellidos").toString());

                    txtCiudad.setText(document.get("doc_ciudad").toString());

                    ImageView PhotoPerfil = view.findViewById(R.id.photoPerfil);
                    Glide.with(view.getContext()).load(document.get("doc_photo")).into(PhotoPerfil);

                } else {
                    Log.d("TAGDocument", "No such document");
                }
            } else {
                Log.d("TAGDocument", "get failed with ", task.getException());
            }
        });


        return view;
    }


}