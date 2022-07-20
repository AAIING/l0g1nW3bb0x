package com.opencode.webboxdespacho.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.opencode.webboxdespacho.R;
import com.opencode.webboxdespacho.fragments.adapters.ViajesRecyclerAdapter;
import com.opencode.webboxdespacho.fragments.dialogs.EscanearDialog;
import com.opencode.webboxdespacho.fragments.dialogs.LoginDialog;
import com.opencode.webboxdespacho.fragments.dialogs.ObservacionDialog;
import com.opencode.webboxdespacho.fragments.dialogs.VerPedidoDialog;
import com.opencode.webboxdespacho.models.Viajes;
import com.opencode.webboxdespacho.models.Viajesd;
import com.opencode.webboxdespacho.models.Pedidos;
import com.opencode.webboxdespacho.sqlite.data.ViajesData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PedidosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PedidosFragment extends Fragment implements EscanearDialog.OnInputSelected{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PedidosFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PedidosFragment newInstance(String param1, String param2) {
        PedidosFragment fragment = new PedidosFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private TextView viewIconCargarViaje, viewTotalPedidos, viewMenu, viewBack, viewIconCarga,
            viewIconEntrega, viewTitulo, viewVerPedido, viewCheck;
    private AlertDialog alertDialog;
    private ViajesData viajesData;
    private int viajesOptions =2; //CAMBIAR A 1
    private List<Viajes> listViajes = new ArrayList<>();
    private List<Viajesd> listViajesd = new ArrayList<>();
    private RecyclerView recyclerView;
    private ViajesRecyclerAdapter viajesRecyclerAdapter;
    private String opt="1";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void rsptaEscaner(boolean value) {
        try {
            listViajes = viajesData.getDespachos();
            loadPedidos();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("NewApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_revisar, container, false);
        alertDialog = new AlertDialog.Builder(getContext()).create();
        viajesData = new ViajesData(getContext());
        recyclerView = view.findViewById(R.id.recycler_pedidos_lista);
        viewIconCargarViaje = view.findViewById(R.id.view_menu_icon_carga_viaje);
        viewIconCargarViaje.setOnClickListener(onClickCargarViaje);
        viewTotalPedidos = view.findViewById(R.id.view_count_pedidos);
        viewTotalPedidos.setVisibility(View.GONE);
        viewBack = view.findViewById(R.id.view_menu_backnav);
        viewBack.setOnClickListener(onClickBack);
        viewIconCarga = view.findViewById(R.id.view_icon_carga);
        viewIconEntrega = view.findViewById(R.id.view_icon_entrega);
        viewTitulo = view.findViewById(R.id.view_menu_titulo);
        viewVerPedido = view.findViewById(R.id.view_icon_ver_pedido);
        viewCheck = view.findViewById(R.id.view_icon_check);
        //
        if(getArguments() != null){
            opt = getArguments().getString("OPT");
            if(opt.equals("2")) {
                viewIconCarga.setText("Entrega");
                viewIconEntrega.setText("Observ");
                viewTitulo.setText("Entregar Pedido");
                //ACTUALIZAR PRIORIDAD
                if(getArguments().getString("RET_CAM") != null){
                    try {
                        viajesData.updatePrioridadPedido();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }else{
                viewCheck.setVisibility(View.GONE);
            }
        }
        //
        int countCaja = 0;
        int countBolsa = 0;
        int countCajaBolsa = 0;
        try {
            listViajes = viajesData.getDespachos();
            //
            for(Viajes itemviaje: listViajes){
                listViajesd = itemviaje.getViajesd();
                Collections.sort(listViajesd, Comparator.comparingInt(Viajesd::getPrioridad));
                for(Viajesd viajesd: listViajesd ){
                    Pedidos pedidos = viajesd.getPedidos();
                    countCaja += pedidos.getCajas();
                    countBolsa += pedidos.getBolsas();
                    countCajaBolsa += pedidos.getCajas() + pedidos.getBolsas();
                }
            }
            loadPedidos();
            viewTotalPedidos.setText("TOTAL PEDIDOS: "+listViajesd.size() +"\n"+"TOTAL ITEMS: "+countCajaBolsa+" CAJAS: "+countCaja+" BOLSAS: "+countBolsa);
            viewTotalPedidos.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    void loadPedidos(){
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        viajesRecyclerAdapter = new ViajesRecyclerAdapter(getContext(), listViajesd, opt);
        recyclerView.setAdapter(viajesRecyclerAdapter);
        viajesRecyclerAdapter.notifyDataSetChanged();
        viajesRecyclerAdapter.setOnClickListener(new ViajesRecyclerAdapter.OnClickListener() {

            @Override
            public void onVerPedido(View view, int position) {
                Viajesd item2 = listViajesd.get(position);
                Pedidos pedidos = item2.getPedidos();

                VerPedidoDialog newFragment = new VerPedidoDialog();
                Bundle bundle = new Bundle();
                bundle.putString("CLIENTE", pedidos.getCliente());
                bundle.putString("DIRENVIO", pedidos.getDireccionenvio());
                bundle.putString("COMUNA", pedidos.getComunaenvio());
                bundle.putString("CONDOMINIO", pedidos.getCondominioenvio());
                bundle.putString("CAJAS", String.valueOf(pedidos.getCajas()));
                bundle.putString("BOLSAS", String.valueOf(pedidos.getBolsas()));
                newFragment.setArguments(bundle);
                newFragment.setTargetFragment(PedidosFragment.this, 1);
                newFragment.show(getFragmentManager(), "Dialog");
            }

            @Override
            public void onEntregarPedido(View view, int position) {
                Viajesd item2 = listViajesd.get(position);
                Pedidos pedidos = item2.getPedidos();

                try {

                    int npedido = viajesData.getPrioridadPedido();

                    if(item2.getPrioridad() == 1 || npedido == 0) {

                        EscanearDialog newFragment = new EscanearDialog();
                        Bundle bundle = new Bundle();
                        bundle.putString("OPT", "2");
                        bundle.putString("NUMPEDIDO", String.valueOf(pedidos.getRegistro()));
                        bundle.putString("NOMCLIENTE", pedidos.getCliente());
                        newFragment.setArguments(bundle);
                        newFragment.setTargetFragment(PedidosFragment.this, 1);
                        newFragment.show(getFragmentManager(), "EscanearDialog");
                    } else {

                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.setTitle("Alerta");
                        alertDialog.setMessage("El pedido N°"+npedido+" es el siguiente por entregar..");
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alertDialog.dismiss();
                            }
                        });
                        alertDialog.show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onObservacion(View view, int position) {
                Viajesd item2 = listViajesd.get(position);
                Pedidos pedidos = item2.getPedidos();

                ObservacionDialog newFragment = new ObservacionDialog();
                Bundle bundle = new Bundle();
                bundle.putString("OPT", "2");
                bundle.putString("NUMPEDIDO", String.valueOf(pedidos.getRegistro()));
                bundle.putString("NOMCLIENTE", pedidos.getCliente());
                newFragment.setArguments(bundle);
                newFragment.setTargetFragment(PedidosFragment.this, 1);
                newFragment.show(getFragmentManager(), "Dialog");
            }
        });
    }

    private View.OnClickListener onClickBack = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MenuFragment newFragment = new MenuFragment();
            FragmentTransaction fm = getActivity().getSupportFragmentManager().beginTransaction();
            fm.replace(R.id.main_activity_frame, newFragment);
            fm.commit();
        }
    };

    private View.OnClickListener onClickCargarViaje = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //
        }
    };

}