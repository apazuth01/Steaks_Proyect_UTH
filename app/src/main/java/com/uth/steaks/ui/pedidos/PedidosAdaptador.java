package com.uth.steaks.ui.pedidos;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uth.steaks.MapActivity;
import com.uth.steaks.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class PedidosAdaptador extends FirestoreRecyclerAdapter<PedidosConstructor,PedidosAdaptador.ViewHolder> {
    static int PEDIDO_FINALIZADO = 1;
    static int PEDIDO_COCINANDO = 2;

    private Context ctx;
    private FirestoreRecyclerOptions<PedidosConstructor> pedidosConstructorList;
    SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss a, dd-MM-yy");
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

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
        holder.txtTotales.setText(model.getDoc_total().toString());
        holder.txtFecha.setText(format.format(model.getDoc_fecha()));

        holder.itemPedido.setOnClickListener(view -> {

            if (model.getDoc_estado().equals("Editando")){


                AlertDialog.Builder builder1 = new AlertDialog.Builder(ctx);
                builder1.setMessage("Confirma que la Orden esta lista?.");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        (dialog, id) -> {

                            Map<String, Object> map = new HashMap<>();
                            map.put("doc_estado", "Pendiente");
                            map.put("doc_fecha", new Timestamp(new Date()));

                            db.collection("clt_pedidos").document(model.getDoc_orden()).update(map);

                            Toast.makeText(ctx,"Su Orden ha sido enviada.",Toast.LENGTH_LONG).show();
                            dialog.cancel();

                        });

                builder1.setNegativeButton(
                        "No",
                        (dialog, id) -> dialog.cancel());

                AlertDialog alert11 = builder1.create();
                alert11.show();

            }else if(model.getDoc_estado().equals("Enviado")){

//                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
//                builder.setMessage("Confirma la entrega del pedido?.");
//                builder.setCancelable(true);
//
//                builder.setPositiveButton(
//                        "Yes",
//                        (dialog, id) -> {
//
//                            Map<String, Object> map = new HashMap<>();
//                            map.put("doc_estado", "Entregado");
//                            map.put("doc_fecha", new Timestamp(new Date()));
//
//                                db.collection("clt_pedidos").document(model.getDoc_orden()).update(map);
//
//                            Toast.makeText(ctx,"Su Orden ha sido entregada.",Toast.LENGTH_LONG).show();
//                            dialog.cancel();
//
//                        });
//
//                builder.setNegativeButton(
//                        "No",
//                        (dialog, id) -> dialog.cancel());
//
//                AlertDialog alert = builder.create();
//                alert.show();

                Intent intent = new Intent(ctx, MapActivity.class);
                intent.putExtra("Pedido",model.getDoc_orden());
                intent.putExtra("Geo", String.valueOf(model.getDoc_geo()));
                intent.putExtra("Cliente",model.getDoc_cliente());
                intent.putExtra("Direccion",model.getDoc_direccion());

                ctx.startActivity(intent);


            }

        });



    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == PEDIDO_FINALIZADO){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rst_pedido, parent,false);
        }else if(viewType == PEDIDO_COCINANDO) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rst_pedido_cocinando, parent,false);
        }else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rst_pedido_finalizado, parent,false);
        }
        return new PedidosAdaptador.ViewHolder(view);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtEstado, txtPedido, txtTotales, txtFecha;
        CardView itemPedido;

        public ViewHolder(@NonNull View view) {
            super(view);

            txtEstado = view.findViewById(R.id.txtEstado);
            txtPedido = view.findViewById(R.id.txtPedido);
            txtTotales = view.findViewById(R.id.txtTotales);
            txtFecha = view.findViewById(R.id.txtFecha);
            itemPedido = view.findViewById(R.id.PedidoCard);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (Objects.equals(pedidosConstructorList.getSnapshots().getSnapshot(position).get("doc_estado"), "Editando")){
            return 1;
        }else if(Objects.equals(pedidosConstructorList.getSnapshots().getSnapshot(position).get("doc_estado"), "Cocinando")){
            return 2;
        }else{
            return 0;
        }

    }

}
