package com.opencode.webboxdespacho.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.opencode.webboxdespacho.R;
import com.opencode.webboxdespacho.fragments.adapters.DespachosdRecyclerAdapter;
import com.opencode.webboxdespacho.fragments.dialogs.EscanearDialog;
import com.opencode.webboxdespacho.models.Despachosd;
import com.opencode.webboxdespacho.models.Pedidos;
import com.opencode.webboxdespacho.sqlite.data.DespachosdData;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RevisarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RevisarFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RevisarFragment() {
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
    public static RevisarFragment newInstance(String param1, String param2) {
        RevisarFragment fragment = new RevisarFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private TextView viewIconCargarViaje, viewTotalPedidos, viewMenu, viewBack;
    private AlertDialog alertDialog;
    private DespachosdData despachosdData;
    private int viajesOptions = 2; //CAMBIAR A 1
    private List<Despachosd> listPedidos = new ArrayList<>();
    private RecyclerView recyclerView;
    private DespachosdRecyclerAdapter despachosdRecyclerAdapter;
    //

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_revisar, container, false);
        alertDialog = new AlertDialog.Builder(getContext()).create();
        despachosdData = new DespachosdData(getContext());
        recyclerView = view.findViewById(R.id.recycler_pedidos_lista);
        viewIconCargarViaje = view.findViewById(R.id.view_menu_icon_carga_viaje);
        viewIconCargarViaje.setOnClickListener(onClickCargarViaje);
        viewTotalPedidos = view.findViewById(R.id.view_count_pedidos);
        viewTotalPedidos.setVisibility(View.GONE);
        viewBack = view.findViewById(R.id.view_menu_backnav);
        viewBack.setOnClickListener(onClickBack);
        //
        try {
            listPedidos = despachosdData.getDespachos();
            loadPedidos();
            viewTotalPedidos.setText("TOTAL PEDIDOS: "+listPedidos.size());
            viewTotalPedidos.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    void loadPedidos(){
        //
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        despachosdRecyclerAdapter = new DespachosdRecyclerAdapter(getContext(), listPedidos);
        recyclerView.setAdapter(despachosdRecyclerAdapter);
        despachosdRecyclerAdapter.setOnClickListener(new DespachosdRecyclerAdapter.OnClickListener() {
            @Override
            public void onVerPedido(View view, int position) {
                Despachosd item = listPedidos.get(position);
                Pedidos pedidos = item.getPedidos();

                String msje = "Dirección Despacho: "+pedidos.getDireccionenvio() + "\n" ;
                       msje +="Comuna: "+pedidos.getComunaenvio() + "\n" ;
                       msje +="Condominio: "+pedidos.getCondominioenvio() + "\n" ;
                       msje +="Cajas: "+pedidos.getCajas() + "\n";
                       msje +="Bolsas: "+pedidos.getBolsas();

                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setTitle("Ver Pedido");
                alertDialog.setMessage(msje);
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
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
            switch (viajesOptions){
                case 1:
                    /*
                    LoginFragment newFragment = new LoginFragment();
                    newFragment.setTargetFragment(MenuFragment.this, 1);
                    newFragment.show(getFragmentManager(), "LoginDialog");
                    */
                    break;
                case 2:
                    //
                    /*
                    PopupMenu popupMenu = new PopupMenu(getContext(), view);
                    popupMenu.getMenuInflater().inflate(R.menu.menu_popup, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.item_gen_revisar_viaje:

                                    popupMenu.dismiss();
                                    return true;
                                case R.id.item_gen_cargar_furgon:

                                    return true;
                                case R.id.item_gen_inicia_viaje:

                                    return true;
                                case R.id.item_gen_entrega_pedido:

                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });
                    popupMenu.show();
                    break;
                    */
                default:
            }
        }
    };
}