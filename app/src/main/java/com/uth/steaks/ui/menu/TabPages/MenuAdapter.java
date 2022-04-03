package com.uth.steaks.ui.menu.TabPages;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.uth.steaks.ProductoActivity;
import com.uth.steaks.R;

public class MenuAdapter extends FirestoreRecyclerAdapter<MenuConstructor, MenuAdapter.ViewHolder> {
    private final Context context;

    public MenuAdapter(@NonNull FirestoreRecyclerOptions<MenuConstructor> options, Context context) {
        super(options);
        this.context = context;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull MenuAdapter.ViewHolder holder, int position, @NonNull MenuConstructor model) {
        final String documentId = getSnapshots().getSnapshot(position).getId();

        holder.titulo.setText(model.getDoc_titulo());
        holder.descripcion.setText(model.getDoc_descripcion());
        holder.precio.setText(String.valueOf(model.getDoc_precio()));

        Glide.with(context).load(model.getDoc_image()).into(holder.imagen);

        holder.itemCard.setOnClickListener(view -> {
            Intent intent = new Intent(context, ProductoActivity.class);
            intent.putExtra("id",documentId);
            intent.putExtra("imagen",model.getDoc_image());
            intent.putExtra("precio",String.valueOf(model.getDoc_precio()));
            intent.putExtra("titulo",model.getDoc_titulo());
            intent.putExtra("descripcion",model.getDoc_descripcion());

            context.startActivity(intent);
        });

    }

    @NonNull
    @Override
    public MenuAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rst_menu,parent,false);
        return new ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView titulo,descripcion,precio;
        private final ImageView imagen;
        private final CardView itemCard;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.titulo);
            descripcion = itemView.findViewById(R.id.descripcion);
            imagen = itemView.findViewById(R.id.imagen);
            precio = itemView.findViewById(R.id.precio);
            itemCard = itemView.findViewById(R.id.itemCard);

        }
    }
}
