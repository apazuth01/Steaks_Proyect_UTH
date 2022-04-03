package com.uth.steaks.ui.pedidos;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.uth.steaks.R;

import java.text.SimpleDateFormat;
import java.util.Objects;


public class PedidosAdaptador extends FirestoreRecyclerAdapter<PedidosConstructor,PedidosAdaptador.ViewHolder> {
    static int PEDIDO_FINALIZADO = 1;

    private Context ctx;
    private FirestoreRecyclerOptions<PedidosConstructor> pedidosConstructorList;
    SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss a, dd-MM-yy");

    public PedidosAdaptador(@NonNull FirestoreRecyclerOptions<PedidosConstructor> options, Context ctx) {
        super(options);
        this.ctx = ctx;
        this.pedidosConstructorList = options;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull PedidosConstructor model) {
        holder.txtEstado.setText("Estado: "+model.getDoc_estado());
        holder.txtPedido.setText("#Pedido: "+model.getDoc_orden());
        holder.txtTotales.setText(model.getDoc_total());
        holder.txtFecha.setText(format.format(model.getDoc_fecha()));

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == PEDIDO_FINALIZADO){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rst_pedido, parent,false);
        }else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rst_pedido_finalizado, parent,false);
        }

        return new PedidosAdaptador.ViewHolder(view);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtEstado, txtPedido,txtCantidad, txtTotales, txtFecha;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtEstado = itemView.findViewById(R.id.txtEstado);
            txtPedido = itemView.findViewById(R.id.txtPedido);
            txtTotales = itemView.findViewById(R.id.txtTotales);
            txtFecha = itemView.findViewById(R.id.txtFecha);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (Objects.equals(pedidosConstructorList.getSnapshots().getSnapshot(position).get("doc_estado"), "Editando")){
            return 1;
        }else{
            return 0;
        }

    }

}
