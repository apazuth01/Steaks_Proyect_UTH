package com.uth.steaks.ui.menu.TabPages;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.uth.steaks.R;

public class FragmentMenuBebidas extends Fragment {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private MenuAdapter MenuAdapter;
    private final CollectionReference clt = db.collection("clt_menu");


    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_card, container, false);

        Query query = clt.whereEqualTo("doc_seccion", "BEBIDAS").orderBy("doc_fecha", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<MenuConstructor> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<MenuConstructor>()
                .setQuery(query, MenuConstructor.class).build();

        MenuAdapter = new MenuAdapter(firestoreRecyclerOptions, getContext());
        MenuAdapter.notifyDataSetChanged();
        RecyclerView recyclerView = view.findViewById(R.id.RecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(MenuAdapter);

        return view;


    }

    @Override
    public void onStart() {
        super.onStart();
        MenuAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        MenuAdapter.stopListening();
    }


}