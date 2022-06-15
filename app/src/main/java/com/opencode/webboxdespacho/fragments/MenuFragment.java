package com.opencode.webboxdespacho.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.opencode.webboxdespacho.R;
import com.opencode.webboxdespacho.fragments.dialogs.EscanearDialog;
import com.opencode.webboxdespacho.fragments.dialogs.LoginDialog;
import com.opencode.webboxdespacho.models.Despachosd;
import com.opencode.webboxdespacho.sqlite.data.DespachosdData;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MenuFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MenuFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MenuFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MenuFragment newInstance(String param1, String param2) {
        MenuFragment fragment = new MenuFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private Button btnRevisar, btnCargarFurgon, btnIniciarViaje, btnSincronizaViaje;
    private AlertDialog alertDialog;
    private DespachosdData despachosdData;
    private List<Despachosd> listDespachosd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view  = inflater.inflate(R.layout.fragment_menu, container, false);
        alertDialog = new AlertDialog.Builder(getContext()).create();
        btnRevisar = view.findViewById(R.id.btn_opcion_revisar);
        btnRevisar.setOnClickListener(onClickRevisar);
        btnCargarFurgon = view.findViewById(R.id.btn_opcion_cargar_furgon);
        btnCargarFurgon.setOnClickListener(onClickCargarFurgon);
        btnIniciarViaje = view.findViewById(R.id.btn_iniciar_viaje);
        btnIniciarViaje.setOnClickListener(onClickIniciarViaje);
        btnSincronizaViaje = view.findViewById(R.id.btn_sincroniza_pedido);
        btnSincronizaViaje.setOnClickListener(onClickSincrViaje);

        return view;
    }

    private View.OnClickListener onClickSincrViaje= new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            LoginDialog newFragment = new LoginDialog();
            newFragment.setTargetFragment(MenuFragment.this, 1);
            newFragment.show(getFragmentManager(), "EscanearDialog");
        }
    };

    private View.OnClickListener onClickIniciarViaje = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int count_total = 0;
            int count_despacho = 0;
            //LA SUMA DE TODAS LAS CAJAS Y BOLSAS
            try {
                listDespachosd = despachosdData.getDespachos();
                for(Despachosd item: listDespachosd){
                    count_total =count_total+(item.Bolsas+item.Cajas);
                    count_despacho =count_despacho+(item.Bolsascargadas+item.Cajascargadas);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            Log.e("TAG", ""+(count_total + count_despacho));

            if(count_total == count_despacho) {
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setTitle("Iniciar Viaje");
                alertDialog.setMessage("¿Desea iniciar viaje?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
            }else{
                Toast.makeText(getContext(), "Faltan items para iniciar viaje", Toast.LENGTH_LONG).show();
            }

        }
    };

    private View.OnClickListener onClickRevisar = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RevisarFragment newFragment = new RevisarFragment();
            FragmentTransaction fm = getActivity().getSupportFragmentManager().beginTransaction();
            fm.replace(R.id.main_activity_frame, newFragment);
            fm.commit();
        }
    };

    private View.OnClickListener onClickCargarFurgon = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
             EscanearDialog newFragment = new EscanearDialog();
             newFragment.setTargetFragment(MenuFragment.this, 1);
             newFragment.show(getFragmentManager(), "EscanearDialog");
        }
    };

}