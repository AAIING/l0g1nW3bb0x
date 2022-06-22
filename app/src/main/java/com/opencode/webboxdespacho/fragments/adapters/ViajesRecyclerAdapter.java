package com.opencode.webboxdespacho.fragments.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.opencode.webboxdespacho.R;
import com.opencode.webboxdespacho.models.Viajes;
import com.opencode.webboxdespacho.models.Viajesd;
import com.opencode.webboxdespacho.models.Pedidos;

import java.util.List;

public class ViajesRecyclerAdapter extends RecyclerView.Adapter<ViajesRecyclerAdapter.ViewHolder>{

    private Context context;
    //private List<Viajes> listViajes;
    private List<Viajesd> listViajesd;

    private OnClickListener onClickListener = null;

    public interface OnClickListener {
        void onVerPedido(View view, int position);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public ViajesRecyclerAdapter(Context context, List<Viajesd> listViajesd) {
        this.context = context;
        this.listViajesd = listViajesd;
    }

    @NonNull
    @Override
    public ViajesRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pedido_rv_card, parent, false);
        return new ViajesRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViajesRecyclerAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Viajesd item2 = listViajesd.get(holder.getAdapterPosition());
        Pedidos pedidos = item2.getPedidos();

        holder.viewVerPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onClickListener == null) return;
                onClickListener.onVerPedido(view, position);
            }
        });

        holder.viewNumPedido.setText(String.valueOf(item2.getPedido()));
        holder.viewNombreCliente.setText(pedidos.getCliente());

        if(pedidos.getCajas() == item2.getCajascargadas()){
            Drawable drawable = context.getResources().getDrawable(R.drawable.ic_baseline_green_check_24);
            holder.viewCheckCargadas.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        }
    }

    @Override
    public int getItemCount() {
        //return listViajes.size();
        return listViajesd.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView viewNumPedido, viewNombreCliente, viewVerPedido, viewCheckCargadas, viewCheckEntregadas;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            viewCheckCargadas = itemView.findViewById(R.id.view_check_pedido_cargado);
            viewCheckEntregadas = itemView.findViewById(R.id.view_check_pedido_entregado);
            viewNumPedido = itemView.findViewById(R.id.view_numero_pedido);
            viewNombreCliente = itemView.findViewById(R.id.view_nombre_cliente);
            viewVerPedido = itemView.findViewById(R.id.view_btn_ver_pedido);
        }
    }
}
