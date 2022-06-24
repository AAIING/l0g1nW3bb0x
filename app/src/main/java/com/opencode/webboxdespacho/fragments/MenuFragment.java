package com.opencode.webboxdespacho.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.opencode.webboxdespacho.R;
import com.opencode.webboxdespacho.config.ApiConf;
import com.opencode.webboxdespacho.config.SessionDatos;
import com.opencode.webboxdespacho.config.SessionKeys;
import com.opencode.webboxdespacho.fragments.dialogs.EscanearDialog;
import com.opencode.webboxdespacho.fragments.dialogs.LoginDialog;
import com.opencode.webboxdespacho.models.Pedidos;
import com.opencode.webboxdespacho.models.Viajes;
import com.opencode.webboxdespacho.models.Viajesd;
import com.opencode.webboxdespacho.sqlite.data.ViajesData;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MenuFragment extends Fragment implements LoginDialog.OnInputSelected{

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

    private Button btnRevisar, btnCargarFurgon, btnIniciarViaje, btnSincronizaViaje, btnEntregaPedido, btnFinViaje;
    private AlertDialog alertDialog;
    private ViajesData viajesData;
    private List<Viajes> listViajes = new ArrayList<>();
    private int numviaje=0, count_total=0, count_despacho=0;
    private LinearLayout linearViajeSync;
    private SessionDatos sessionDatos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view  = inflater.inflate(R.layout.fragment_menu, container, false);
        sessionDatos = new SessionDatos(getContext());
        viajesData = new ViajesData(getContext());
        try {
            listViajes = viajesData.getDespachos();
            if(listViajes.size() > 0){
                Viajes viajes = listViajes.get(0);
                ((AppCompatActivity) getActivity()).getSupportActionBar().show();
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Bienvenido "+viajes.Chofer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        alertDialog = new AlertDialog.Builder(getContext()).create();
        btnRevisar = view.findViewById(R.id.btn_opcion_revisar);
        btnRevisar.setOnClickListener(onClickRevisar);
        btnCargarFurgon = view.findViewById(R.id.btn_opcion_cargar_furgon);
        btnCargarFurgon.setOnClickListener(onClickCargarFurgon);
        btnIniciarViaje = view.findViewById(R.id.btn_iniciar_viaje);
        btnIniciarViaje.setOnClickListener(onClickIniciarViaje);
        btnSincronizaViaje = view.findViewById(R.id.btn_sincroniza_pedido);
        btnSincronizaViaje.setOnClickListener(onClickSincrViaje);
        btnEntregaPedido = view.findViewById(R.id.btn_entrega_pedido);
        btnEntregaPedido.setOnClickListener(onClickEntregaPedido);
        btnFinViaje = view.findViewById(R.id.btn_finalizar_viaje);
        btnFinViaje.setOnClickListener(onClickFinViaje);
        linearViajeSync = view.findViewById(R.id.linear_viaje_sync);
        return view;
    }

    private View.OnClickListener onClickFinViaje = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            count_total =0;
            count_despacho =0;
            for(Viajes viajes: listViajes){
                List<Viajesd> viajesd = viajes.getViajesd();
                //
                for(Viajesd viajesd1: viajesd){
                    Pedidos pedidos = viajesd1.getPedidos();
                    count_total += pedidos.getCajas() + pedidos.getBolsas();
                    count_despacho += viajesd1.getCajasentregadas() + viajesd1.getBolsasentregadas();
                }
            }
            if(count_total == count_despacho){
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setTitle("Finalizar Viaje");
                alertDialog.setMessage("¿Desea finalizar viaje?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                        LoginDialog newFragment = new LoginDialog();
                        Bundle bundle = new Bundle();
                        bundle.putString("OPT", "3");
                        newFragment.setArguments(bundle);
                        newFragment.setTargetFragment(MenuFragment.this, 1);
                        newFragment.show(getFragmentManager(), "Dialog");
                    }
                });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Volver", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
                //
            }else{
                Toast.makeText(getContext(), "Faltan items para finalizar viaje", Toast.LENGTH_LONG).show();
            }
        }
    };

    private View.OnClickListener onClickEntregaPedido = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //5=INICIA VIAJE
            if(sessionDatos.getRecord().get(SessionKeys.estadoViaje).equals("5")) {
                ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
                RevisarFragment newFragment = new RevisarFragment();
                Bundle bundle = new Bundle();
                bundle.putString("OPT", "2");
                newFragment.setArguments(bundle);
                FragmentTransaction fm = getActivity().getSupportFragmentManager().beginTransaction();
                fm.replace(R.id.main_activity_frame, newFragment);
                fm.commit();
            }else{

                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setTitle("No has iniciado viaje");
                alertDialog.setMessage("Para entregar debes iniciar viaje");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();

            }
        }
    };

    private View.OnClickListener onClickSincrViaje= new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            LoginDialog newFragment = new LoginDialog();
            newFragment.setTargetFragment(MenuFragment.this, 1);
            newFragment.show(getFragmentManager(), "Dialog");
        }
    };

    private View.OnClickListener onClickIniciarViaje = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            count_total =0;
            count_despacho =0;
            try {
                listViajes = viajesData.getDespachos();
                for(Viajes viajes: listViajes){
                    List<Viajesd> viajesd = viajes.getViajesd();
                    for(Viajesd viajesd1: viajesd){
                        Pedidos pedidos = viajesd1.getPedidos();
                        count_total += pedidos.getCajas() + pedidos.getBolsas();
                        count_despacho += viajesd1.getCajascargadas() + viajesd1.getBolsascargadas();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            //LA SUMA DE TODAS LAS CAJAS Y BOLSAS
            Log.e("TAG", "TOTAL "+(count_total)+" DESPACHADOS " +(count_despacho));
            if(count_total  == count_despacho){
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setTitle("Iniciar Viaje");
                alertDialog.setMessage("¿Desea iniciar viaje?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                        LoginDialog newFragment = new LoginDialog();
                        Bundle bundle = new Bundle();
                        bundle.putString("OPT", "2");
                        newFragment.setArguments(bundle);
                        newFragment.setTargetFragment(MenuFragment.this, 1);
                        newFragment.show(getFragmentManager(), "Dialog");
                    }
                });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Volver", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
                //
            }else{
                Toast.makeText(getContext(), "Faltan items para iniciar viaje", Toast.LENGTH_LONG).show();
            }
        }
    };

    private View.OnClickListener onClickRevisar = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
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

    @Override
    public void rspta(boolean value, String opt, int numviaje) {
        //OPT:2=INICIA VIAJE,3=FIN VIAJE
        //ESTADO:5=INICIA,9=FIN
        /***/
        if(opt.equals("1")){
            sessionDatos.setIdViaje(String.valueOf(numviaje), "0");
            //
        } else if(opt.equals("2")){
            try {
                viajesData.updateEstadoViaje(String.valueOf(numviaje), "5");
                putEstadoViaje(numviaje, 5);
                sessionDatos.setIdViaje(String.valueOf(numviaje), "5");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if(opt.equals("3")){
            try {
                viajesData.updateEstadoViaje(String.valueOf(numviaje), "9");
                putEstadoViaje(numviaje, 9);
                sessionDatos.setIdViaje(String.valueOf(numviaje), "9");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //
    void putEstadoViaje(int numviaje, int estado){
        Call<Viajes> call = ApiConf.getData().putViajes(numviaje, estado);
        call.enqueue(new Callback<Viajes>() {
            @Override
            public void onResponse(Call<Viajes> call, Response<Viajes> response) {
                if(response.isSuccessful()){
                    if(estado == 9){
                        try {
                            /***/
                            viajesData.borrarPedidos();
                            Toast.makeText(getContext(),"Viaje cerrado", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else{
                        Toast.makeText(getContext(),"Viaje Actualizado..", Toast.LENGTH_LONG).show();
                    }
                }
            }
            @Override
            public void onFailure(Call<Viajes> call, Throwable t) {
                Log.e("ERROR--->", t.toString());
            }
        });
    }
}