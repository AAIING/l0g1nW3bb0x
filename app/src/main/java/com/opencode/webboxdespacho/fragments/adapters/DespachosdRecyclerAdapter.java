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
import com.opencode.webboxdespacho.models.Despachosd;
import com.opencode.webboxdespacho.models.Pedidos;

import java.util.List;

public class DespachosdRecyclerAdapter extends RecyclerView.Adapter<DespachosdRecyclerAdapter.ViewHolder>{

    private Context context;
    private List<Despachosd> listPedidos;

    private OnClickListener onClickListener = null;

    public interface OnClickListener {
        void onVerPedido(View view, int position);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public DespachosdRecyclerAdapter(Context context, List<Despachosd> listPedidos) {
        this.context = context;
        this.listPedidos = listPedidos;
    }

    @NonNull
    @Override
    public DespachosdRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pedido_rv_card, parent, false);
        return new DespachosdRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DespachosdRecyclerAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Despachosd item = listPedidos.get(holder.getAdapterPosition());
        Pedidos pedidos = item.getPedidos();

        holder.viewNumPedido.setText(String.valueOf(item.getPedido()));
        holder.viewNombreCliente.setText(pedidos.getCliente());

        holder.viewVerPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onClickListener == null) return;
                onClickListener.onVerPedido(view, position);
            }
        });

        //holder.viewCheckCargadas.setVisibility(View.GONE);
        //holder.viewCheckEntregadas.setVisibility(View.GONE);

        if(item.getCajas() == item.getCajascargadas()){
            Drawable drawable = context.getResources().getDrawable(R.drawable.ic_baseline_green_check_24);
            holder.viewCheckCargadas.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        }

    }

    @Override
    public int getItemCount() {
        return listPedidos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView viewNumPedido, viewNombreCliente, viewVerPedido, viewCheckCargadas, viewCheckEntregadas;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //
            viewCheckCargadas = itemView.findViewById(R.id.view_check_pedido_cargado);
            viewCheckEntregadas = itemView.findViewById(R.id.view_check_pedido_entregado);
            viewNumPedido = itemView.findViewById(R.id.view_numero_pedido);
            viewNombreCliente = itemView.findViewById(R.id.view_nombre_cliente);
            viewVerPedido = itemView.findViewById(R.id.view_btn_ver_pedido);
        }
    }
}
