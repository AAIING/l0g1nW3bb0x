package com.opencode.webboxdespacho.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.util.ArrayMap;
import android.util.Base64;
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
import com.opencode.webboxdespacho.config.Tools;
import com.opencode.webboxdespacho.fragments.dialogs.EscanearDialog;
import com.opencode.webboxdespacho.fragments.dialogs.LoginDialog;
import com.opencode.webboxdespacho.models.Fotos;
import com.opencode.webboxdespacho.models.Pedidos;
import com.opencode.webboxdespacho.models.Viajes;
import com.opencode.webboxdespacho.models.Viajesd;
import com.opencode.webboxdespacho.sqlite.data.ViajesData;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
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
    private SessionDatos sessionDatos;
    private ProgressDialog progressDialog;
    private Tools tools;
    private Handler handler = new Handler();
    private Runnable runnable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view  = inflater.inflate(R.layout.fragment_menu, container, false);
        tools = new Tools(getContext());
        sessionDatos = new SessionDatos(getContext());
        viajesData = new ViajesData(getContext());
        progressDialog = new ProgressDialog(getContext());
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
        return view;
    }

    private View.OnClickListener onClickFinViaje = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            /***/
            new Tools.InternetCheck(getActivity(), new Tools.InternetCheck.Consumer(){
                @Override
                public void accept(Boolean internet) {
                    if(internet){
                        try {
                            count_total =0;
                            count_despacho =0;
                            if(listViajes.size() == 0){
                                sessionDatos.setIdViaje(String.valueOf(0), "0");
                            }
                            for(Viajes viajes: listViajes){
                                List<Viajesd> viajesd = viajes.getViajesd();
                                for(Viajesd viajesd1: viajesd){
                                    Pedidos pedidos = viajesd1.getPedidos();
                                    count_total += pedidos.getCajas() + pedidos.getBolsas();
                                    count_despacho += viajesd1.getCajasentregadas() + viajesd1.getBolsasentregadas();
                                }
                            }
                            if(count_total == count_despacho && sessionDatos.getRecord().get(SessionKeys.estadoViaje).equals("5")){
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
                                alertDialog.show();
                                //
                            }else{

                                String msg ="Faltan items por entregar";
                                if(sessionDatos.getRecord().get(SessionKeys.estadoViaje).equals("0")){
                                    msg="Aun no has cargado e iniciado un viaje";
                                } else if(sessionDatos.getRecord().get(SessionKeys.estadoViaje).equals("9")){
                                    msg ="El Viaje ya esta cerrado";
                                }
                                alertDialog.setCanceledOnTouchOutside(false);
                                alertDialog.setTitle("No puedes finalizar el viaje");
                                alertDialog.setMessage(msg);
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
                        //
                    } else {
                        /***/
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.setTitle("Sin Conexión");
                        alertDialog.setMessage("Para finalizar el viaje se necesita estar conectado..");
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alertDialog.dismiss();
                            }
                        });
                        alertDialog.show();
                    }
                }
            });
        }
    };

    private View.OnClickListener onClickEntregaPedido = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //5=INICIA VIAJE
            if(sessionDatos.getRecord().get(SessionKeys.estadoViaje).equals("5") && listViajes.size() > 0) {
                ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
                PedidosFragment newFragment = new PedidosFragment();
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
            //
            count_total =0;
            count_despacho =0;
            try {
                listViajes = viajesData.getDespachos();
                if(listViajes.size() == 0){
                    sessionDatos.setIdViaje(String.valueOf(0), "0");
                }
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
            //Log.e("TAG", "TOTAL "+(count_total)+" DESPACHADOS " +(count_despacho));
            //sessionDatos.setIdViaje("22", "0");
            if(count_total > 0 && count_total  == count_despacho && sessionDatos.getRecord().get(SessionKeys.estadoViaje).equals("0")){
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
                alertDialog.show();
                //
            }else{
                if(!sessionDatos.getRecord().get(SessionKeys.estadoViaje).equals("0")){
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.setTitle("Viaje Iniciado");
                    alertDialog.setMessage("El viaje ya esta iniciado.");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();
                    return;
                }
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setTitle("No puedes iniciar el viaje");
                alertDialog.setMessage("Faltan items por cargar e iniciar el viaje..");
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

    private View.OnClickListener onClickRevisar = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                listViajes = viajesData.getDespachos();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(listViajes.size() > 0) {
                ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
                PedidosFragment newFragment = new PedidosFragment();
                Bundle bundle = new Bundle();
                bundle.putString("OPT", "1");
                newFragment.setArguments(bundle);
                FragmentTransaction fm = getActivity().getSupportFragmentManager().beginTransaction();
                fm.replace(R.id.main_activity_frame, newFragment);
                fm.commit();
            }else {
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setTitle("Sin pedidos");
                alertDialog.setMessage("No hay pedidos cargados previamente..");
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

    private View.OnClickListener onClickCargarFurgon = new View.OnClickListener() {
        @Override
        public void onClick(View view){
            if(listViajes.size() > 0){
                EscanearDialog newFragment = new EscanearDialog();
                Bundle bundle = new Bundle();
                bundle.putString("OPT", "1");
                newFragment.setArguments(bundle);
                newFragment.setTargetFragment(MenuFragment.this, 1);
                newFragment.show(getFragmentManager(), "Dialog");
            }else {
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setTitle("Sin pedidos");
                alertDialog.setMessage("No hay pedidos cargados previamente..");
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

    @Override
    public void rspta(boolean value, String opt, int numviaje) {
        /***/
        try {
            listViajes = viajesData.getDespachos();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //OPT:2=INICIA VIAJE,3=FIN VIAJE
        //ESTADO:5=INICIA,9 =FIN
        /***/
        if(opt.equals("1")){
            //sessionDatos.setIdViaje(String.valueOf(numviaje), "0");
            //
        } else if(opt.equals("2")){
            //
            progressDialog.setMessage("");
            String nviaje = sessionDatos.getRecord().get(SessionKeys.idViaje);
            String rut = sessionDatos.getRecord().get(SessionKeys.rutUsuario);
            putEstadoViaje(Integer.parseInt(nviaje), 5, Integer.parseInt(rut));
            sessionDatos.setIdViaje(nviaje, "5");
        } else if(opt.equals("3")){
            progressDialog.setMessage("");
            String nviaje = sessionDatos.getRecord().get(SessionKeys.idViaje);
            String rut = sessionDatos.getRecord().get(SessionKeys.rutUsuario);
            putEstadoViaje(Integer.parseInt(nviaje), 9,  Integer.parseInt(rut));
            sessionDatos.setIdViaje(nviaje, "9");
        }
        progressDialog.show();
    }

    void updatePedidoEntregado(int idpedido, String fecha, String hora, String obs){
        Map<String, Object> jsonArrayMap = new ArrayMap<>();
        jsonArrayMap.put("Registro", idpedido);
        jsonArrayMap.put("Fechaentrega", fecha);
        jsonArrayMap.put("Horaentrega", hora);
        jsonArrayMap.put("Obsdespacho", obs);
        RequestBody body = RequestBody.create(MediaType
                .parse("application/json; charset=utf-8"), (new JSONObject(jsonArrayMap)).toString());

        Call<Fotos> call = ApiConf.getData().updatePedidoEntregado(body);
        call.enqueue(new Callback<Fotos>() {
            @Override
            public void onResponse(Call<Fotos> call, Response<Fotos> response) {
                if(response.isSuccessful()){

                }
            }

            @Override
            public void onFailure(Call<Fotos> call, Throwable t) {
                Log.e("failureInst==>", t.toString());
            }
        });
    }

    void postFotosDespacho(int idpedido, byte[] foto){
        Map<String, Object> jsonArrayMap = new ArrayMap<>();
        jsonArrayMap.put("Pedidosregistro", idpedido);
        jsonArrayMap.put("Foto", Base64.encodeToString(foto, Base64.DEFAULT));
        RequestBody body = RequestBody.create(MediaType
                .parse("application/json; charset=utf-8"), (new JSONObject(jsonArrayMap)).toString());

        Call<Fotos> call = ApiConf.getData().postFotosDespacho(body);
        call.enqueue(new Callback<Fotos>() {
            @Override
            public void onResponse(Call<Fotos> call, Response<Fotos> response) {
                if(response.isSuccessful()){

                }
            }

            @Override
            public void onFailure(Call<Fotos> call, Throwable t) {
                Log.e("failureInst==>", t.toString());
            }
        });
    }

    void putEstadoViaje(int numviaje, int estado, int rut){
        Call<Viajes> call = ApiConf.getData().putViajes(numviaje, estado, rut);
        call.enqueue(new Callback<Viajes>() {
            @Override
            public void onResponse(Call<Viajes> call, Response<Viajes> response) {
                if(response.isSuccessful()){
                    progressDialog.dismiss();
                    if(estado == 9){
                        try {
                            viajesData.updateEstadoViaje(String.valueOf(numviaje), "9");
                            /***/
                            for(Fotos fotos: viajesData.getAllFotos()){
                                postFotosDespacho(fotos.getIdPedido(), tools.decodeByte(fotos.getRutaUrl()));
                            }
                            //
                            for(Viajes viajes: listViajes){
                                List<Viajesd> viajesd = viajes.getViajesd();
                                for(Viajesd viajesd1: viajesd){
                                    Pedidos pedidos = viajesd1.getPedidos();
                                    updatePedidoEntregado(pedidos.getRegistro(),
                                            pedidos.getFechaentrega(),
                                            pedidos.getHoraentrega(),
                                            pedidos.getObsdespacho());
                                }
                            }
                            File folder= new File(getContext().getFilesDir() + "/fotos");
                            if(!folder.exists())
                                folder.delete();
                            viajesData.borrarPedidos();
                            sessionDatos.Logout();
                            //
                            alertDialog.setCanceledOnTouchOutside(false);
                            alertDialog.setTitle("Estado viaje");
                            alertDialog.setMessage("Viaje Cerrado..");
                            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    alertDialog.dismiss();
                                }
                            });
                            alertDialog.show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else{
                        /***/
                        try {
                            viajesData.updateEstadoViaje(String.valueOf(numviaje), "5");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.setTitle("Estado viaje");
                        alertDialog.setMessage("Viaje Actualizado..");
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alertDialog.dismiss();
                            }
                        });
                        alertDialog.show();
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