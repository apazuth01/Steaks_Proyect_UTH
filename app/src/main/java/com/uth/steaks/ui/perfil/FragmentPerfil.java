package com.uth.steaks.ui.perfil;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.uth.steaks.R;

import java.util.HashMap;
import java.util.Map;

public class FragmentPerfil extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    String Phone;
    String Photo ="";
    ImageView imageView;
    private final int file = 1;

    @SuppressLint({"SetTextI18n", "CutPasteId"})
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("getProfile",MODE_PRIVATE);
        Phone = sharedPreferences.getString("phone",null);

        Button btnCerrar = view.findViewById(R.id.btnCerarSesion);

        TextInputEditText txtNombre = view.findViewById(R.id.txtNombre);
        TextInputEditText txtApellido = view.findViewById(R.id.txtApellido);
        TextInputEditText txtCiudad= view.findViewById(R.id.txtCiudad);
        imageView = view.findViewById(R.id.photoPerfil);

        imageView.setOnClickListener(view1 -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(intent,file);
        });


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
            if (!Photo.equals("")){
                map.put("doc_photo", Photo);
            }

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == file){
            if (resultCode == RESULT_OK){
                Uri FileUri = data.getData();
                StorageReference folder = FirebaseStorage.getInstance().getReference().child("Clientes");
                StorageReference fileName = folder.child("file"+FileUri.getPathSegments());

                fileName.putFile(FileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        fileName.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                Photo = task.getResult().toString();
                                Glide.with(getActivity()).load(task.getResult()).into(imageView);
                            }
                        });
                    }
                });


            }
        }

    }
}