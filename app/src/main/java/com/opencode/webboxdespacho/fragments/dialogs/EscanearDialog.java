package com.opencode.webboxdespacho.fragments.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.opencode.webboxdespacho.R;
import com.opencode.webboxdespacho.config.CameraXActivity;
import com.opencode.webboxdespacho.config.Capture;
import com.opencode.webboxdespacho.config.SessionKeys;
import com.opencode.webboxdespacho.models.Itemsid;
import com.opencode.webboxdespacho.models.Pedidos;
import com.opencode.webboxdespacho.models.Viajes;
import com.opencode.webboxdespacho.models.Viajesd;
import com.opencode.webboxdespacho.sqlite.data.ViajesData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EscanearDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EscanearDialog extends DialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EscanearDialog() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EscanearDialog.
     */
    // TODO: Rename and change types and number of parameters
    public static EscanearDialog newInstance(String param1, String param2) {
        EscanearDialog fragment = new EscanearDialog();
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

    public interface OnInputSelected {
        void rsptaEscaner(boolean value);
    }

    public OnInputSelected mOnInputSelected;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mOnInputSelected = (OnInputSelected) getTargetFragment();
        }catch (ClassCastException e){
            Log.e("Dialog", "onAttach: ClassCastException : " + e.getMessage() );
        }
    }

    private int countCaja =0, countBolsa =0, cajaEntr=0, bolsaEntr=0, count_foto=0, countdown=0;
    private TextView viewBolsas, viewCajas, viewNumPedido, viewQrEscaneado;
    private Button btnEscan, btnClose;
    private ScanOptions options;
    private ViajesData viajesData;
    private List<Viajesd> listDespachosd;
    private List<Pedidos> listPedidos  = new ArrayList<>();
    private String opt="1", numpedido="", nomcliente="";
    private Handler handler = new Handler();
    private Runnable runnable;
    private boolean sacaFoto =false, verFoto =false, flag=false;
    private AlertDialog alertDialog;
    private Map<Integer, Integer> mapCajas = new HashMap<>();
    private Map<Integer, Integer> mapBolsas = new HashMap<>();
    private List<Viajes> listViajes = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_escanear_dialog, container, false);
        setCancelable(false);
        alertDialog = new AlertDialog.Builder(getContext()).create();
        viajesData = new ViajesData(getContext());
        btnClose = view.findViewById(R.id.button_close_escan);
        btnEscan = view.findViewById(R.id.button_escan);
        btnEscan.setOnClickListener(onClickEscanear);
        viewBolsas = view.findViewById(R.id.view_num_bolsas);
        viewCajas = view.findViewById(R.id.view_num_cajas);
        viewNumPedido = view.findViewById(R.id.view_num_pedido_escan);
        viewQrEscaneado = view.findViewById(R.id.view_qr_msje_escan);
        viewNumPedido = view.findViewById(R.id.view_num_pedido_escan);
        if(getArguments() != null){
            opt = getArguments().getString("OPT");
            if(opt.equals("2")) {
                numpedido = getArguments().getString("NUMPEDIDO");
                nomcliente = getArguments().getString("NOMCLIENTE");
                viewNumPedido.setText("N?? Pedido: " + numpedido +"\n"+"\n"+"Cliente: "+nomcliente);
                /***/
                try {
                        int count_foto = viajesData.getFotos(numpedido);
                        Viajesd despd = viajesData.getDespachod(numpedido);
                        Pedidos pedidos = despd.getPedidos();
                        viewCajas.setText("CAJAS: " + despd.getCajasentregadas() + " DE " + pedidos.getCajas());
                        viewBolsas.setText("BOLSAS: " + despd.getBolsasentregadas() + " DE " + pedidos.getBolsas());
                        //
                        if(count_foto > 0) {
                            btnEscan.setText("COMPLETADO");
                            btnEscan.setEnabled(false);
                            btnEscan.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        }
                        else
                        {
                            if (despd.getCajasentregadas() == pedidos.getCajas() && despd.getBolsasentregadas() == pedidos.getBolsas()) {
                                sacaFoto = true;
                                btnEscan.setText("TOMAR FOTO");
                                btnEscan.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                            }
                        }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if(opt.equals("1")) {
                int count_total=0;
                int count_despacho=0;
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
                /***/
                if(count_total > 0 && count_total == count_despacho ) {
                    viewQrEscaneado.setText("EL FURG??N YA ESTA CARGADO");
                    viewQrEscaneado.setTextColor(Color.RED);
                    handler.removeCallbacks(runnable);
                    flag=false;
                    btnEscan.setText("COMPLETADO");
                    btnEscan.setEnabled(false);
                    btnEscan.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                }
            }
        }
        //
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(opt.equals("2")) {
                    //
                    try {
                        int count_foto = viajesData.getFotos(numpedido);
                        Viajesd despd = viajesData.getDespachod(numpedido);
                        Pedidos pedidos = despd.getPedidos();
                        //
                        if(count_foto > 0) {
                            mOnInputSelected.rsptaEscaner(true);
                            dismiss();
                        }
                        else if(despd.getCajasentregadas() != pedidos.getCajas())
                        {
                            dismiss();
                        }
                        else
                        {
                            alertDialog.setCanceledOnTouchOutside(false);
                            alertDialog.setTitle("Alerta");
                            alertDialog.setMessage("Debes sacar 1 foto minimo por pedido");
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
                else
                {
                    dismiss();
                    handler.removeCallbacks(runnable);
                }
            }
        });
        return view;
    }

    private View.OnClickListener onClickEscanear = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //
            if(sacaFoto){
                Intent intent = new Intent(getActivity(), CameraXActivity.class);
                intent.putExtra("NUMPEDIDO", numpedido);
                startActivity(intent);
                //
            } else {

                if(flag){
                    handler.removeCallbacks(runnable);
                    btnEscan.setText("REANUDAR");
                    flag=false;
                    return;
                }

                //countdown =4;
                flag=true;
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        handler.postDelayed(this, 1000);
                        //
                        if(countdown == 0) {
                            options = new ScanOptions();
                            options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
                            options.setPrompt("");
                            options.setCameraId(0);  //Use a specific camera of the device
                            options.setBeepEnabled(true);
                            options.setBarcodeImageEnabled(true);
                            options.setCaptureActivity(Capture.class);
                            barcodeLauncher.launch(options);
                            //flag=false;
                            //countdown =4;
                        }

                        if(countdown > 0) {
                            btnEscan.setText("PAUSAR .." + countdown);
                            countdown--;
                        }
                    }
                };
                handler.postDelayed(runnable, 0);

            }
        }
    };

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                int count_total=0;
                int count_despacho=0;
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
                //
                flag =true;
                countdown =4;
                //btnEscan.setText("PAUSAR");
                //btnEscan.setEnabled(false);
                btnEscan.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                if(result.getContents() != null) {
                    String value = result.getContents();
                    if(opt.equals("1")) {
                        /**PARA SUBIDA DE ITEMS AL FURGON*/
                        try {
                            Itemsid itemid = viajesData.getItem(value);

                            /*
                            if(itemid.getCodigo() == null){
                                viewQrEscaneado.setText("EL QR NO CORRESPONDE A LA CARGA");
                                viewQrEscaneado.setTextColor(Color.RED);
                                handler.removeCall   backs(runnable);
                                btnEscan.setText("Escanear");
                                btnEscan.setEnabled(true);
                                return;
                            }
*/
                            String tipoenv = itemid.getTipoitem();
                            Viajesd despd = viajesData.getDespachod(String.valueOf(itemid.getPedidosregistro()));
                            Pedidos pedidos = despd.getPedidos();
                            if(despd.getCajascargadas() > 0){
                                mapCajas.put(itemid.getPedidosregistro(), despd.getCajascargadas());
                            }
                            if(despd.getBolsascargadas() > 0){
                                mapBolsas.put(itemid.getPedidosregistro(), despd.getBolsascargadas());
                            }
                            if(count_total > 0 && count_total == count_despacho ) {
                                viewQrEscaneado.setText("EL FURG??N YA ESTA CARGADO");
                                viewQrEscaneado.setTextColor(Color.RED);
                                handler.removeCallbacks(runnable);
                                flag=false;
                                btnEscan.setText("COMPLETADO");
                                btnEscan.setEnabled(false);
                                btnEscan.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                                return;
                            }
                            if(pedidos.getCajas() == despd.getCajascargadas() && pedidos.getBolsas() == despd.getBolsascargadas()) {
                                viewNumPedido.setText("N?? Pedido: " + itemid.getPedidosregistro());
                                viewQrEscaneado.setText("EL PEDIDO YA ESTA CARGADO");
                                viewQrEscaneado.setTextColor(Color.GREEN);
                                viewCajas.setText("CAJAS: " + despd.getCajascargadas() + " DE " + pedidos.getCajas());
                                viewBolsas.setText("BOLSAS: " + despd.getBolsascargadas() + " DE " + pedidos.getBolsas());
                                return;
                            }
                            if(itemid.getEscaneado() > 0) {
                                viewNumPedido.setText("N?? Pedido: " + itemid.getPedidosregistro());
                                viewQrEscaneado.setText("QR REPETIDO");
                                viewQrEscaneado.setTextColor(Color.RED);
                                viewCajas.setText("CAJAS: " + despd.getCajascargadas() + " DE " + pedidos.getCajas());
                                viewBolsas.setText("BOLSAS: " + despd.getBolsascargadas() + " DE " + pedidos.getBolsas());
                                return;
                            } else {
                                /** ESCANEADO IGUAL A 0*/
                                int countCaja=0;
                                int countBolsa=0;
                                if(tipoenv.equals("CAJA")) {
                                    //
                                    if(mapCajas.containsKey(itemid.getPedidosregistro()))
                                        countCaja =mapCajas.get(itemid.getPedidosregistro());

                                    if(mapBolsas.containsKey(itemid.getPedidosregistro()))
                                        countBolsa =mapBolsas.get(itemid.getPedidosregistro());

                                    if(pedidos.getCajas() != countCaja) {
                                        countCaja++;
                                        viewCajas.setText("CAJAS: " + countCaja + " DE " + pedidos.getCajas());
                                        viewBolsas.setText("BOLSAS: " + countBolsa + " DE " + pedidos.getBolsas());
                                        viewQrEscaneado.setText("QR ACEPTADO");
                                        viewQrEscaneado.setTextColor(Color.GREEN);
                                        viajesData.updateCajaBolsaCount(opt, String.valueOf(itemid.getPedidosregistro()), "" + countBolsa, "" + countCaja);
                                        viajesData.updateItemEscaneado(value, opt);
                                        //
                                        mapCajas.put(itemid.getPedidosregistro(), countCaja);
                                    }

                                } else if(tipoenv.equals("BOLSA")) {
                                    //
                                    if(despd.getCajascargadas() > 0){
                                        mapCajas.put(itemid.getPedidosregistro(), despd.getCajascargadas());
                                    }
                                    if(despd.getBolsascargadas() > 0){
                                        mapBolsas.put(itemid.getPedidosregistro(), despd.getBolsascargadas());
                                    }

                                    if(pedidos.getBolsas() != countBolsa && pedidos.getBolsas() != despd.getBolsascargadas()) {
                                        //
                                        if(mapCajas.containsKey(itemid.getPedidosregistro()))
                                            countCaja = mapCajas.get(itemid.getPedidosregistro());

                                        if(mapBolsas.containsKey(itemid.getPedidosregistro()) )
                                            countBolsa = mapBolsas.get(itemid.getPedidosregistro());

                                        if(pedidos.getBolsas() > 0)
                                            countBolsa++;

                                        viewCajas.setText("CAJAS: " + countCaja + " DE " + pedidos.getCajas());
                                        viewBolsas.setText("BOLSAS: " + countBolsa + " DE " + pedidos.getBolsas());
                                        viewQrEscaneado.setText("QR ACEPTADO");
                                        viewQrEscaneado.setTextColor(Color.GREEN);
                                        viajesData.updateCajaBolsaCount(opt, String.valueOf(itemid.getPedidosregistro()), "" + countBolsa, "" + countCaja);
                                        viajesData.updateItemEscaneado(value, opt);
                                        //
                                        mapBolsas.put(itemid.getPedidosregistro(), countBolsa);

                                    } else {
                                        if (pedidos.getBolsas() == despd.getBolsascargadas())
                                            Toast.makeText(getContext(), "Bolsas Completas", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                            viewNumPedido.setText("N?? Pedido: " + String.valueOf(itemid.getPedidosregistro()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        /***/
                    } else if(opt.equals("2")){
                        /** PARA ENTREGA DE ITEMS A CLIENTE*/
                        try {
                            Itemsid itemid = viajesData.getItem(value);
/*
                            if(itemid.getCodigo() == null){
                                viewQrEscaneado.setText("EL QR NO CORRESPONDE A LA CARGA");
                                viewQrEscaneado.setTextColor(Color.RED);
                                handler.removeCallbacks(runnable);
                                btnEscan.setText("Escanear");
                                btnEscan.setEnabled(true);
                                return;
                            }
*/
                            String tipoenv = itemid.getTipoitem();
                            Viajesd despd = viajesData.getDespachod(String.valueOf(itemid.getPedidosregistro()));
                            Pedidos pedidos = despd.getPedidos();

                            if(pedidos.getCajas() == despd.getCajasentregadas() && pedidos.getBolsas() == despd.getBolsasentregadas()) {
                                handler.removeCallbacks(runnable);
                                flag = false;
                                btnEscan.setText("COMPLETADO");
                                btnEscan.setEnabled(false);
                                btnEscan.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                            }
                            if(!String.valueOf(itemid.getPedidosregistro()).equals(numpedido)){
                                viewQrEscaneado.setText("QR NO CORRESPONDE AL PEDIDO");
                                viewQrEscaneado.setTextColor(Color.RED);
                                return;
                            }
                            if (itemid.getEscaneadoEntregado() > 0){
                                viewNumPedido.setText("N?? Pedido: " + itemid.getPedidosregistro());
                                viewQrEscaneado.setText("QR REPETIDO");
                                viewQrEscaneado.setTextColor(Color.RED);
                                viewCajas.setText("CAJAS: " + despd.getCajasentregadas() + " DE " + pedidos.getCajas());
                                viewBolsas.setText("BOLSAS: " + despd.getBolsasentregadas() + " DE " + pedidos.getBolsas());
                                return;
                            } else {
                                /** ESCANEADO IGUAL A 0*/
                                if (tipoenv.equals("CAJA")){
                                    if (pedidos.getCajas() != countCaja){
                                        countCaja++;
                                    }
                                        viewCajas.setText("CAJAS: " + countCaja + " DE " + pedidos.getCajas());
                                        viewBolsas.setText("BOLSAS: " + countBolsa + " DE " + pedidos.getBolsas());
                                        viewQrEscaneado.setText("QR ACEPTADO");
                                        viewQrEscaneado.setTextColor(Color.GREEN);
                                        viajesData.updateCajaBolsaCount(opt, String.valueOf(itemid.getPedidosregistro()), "" + countBolsa, "" + countCaja);
                                        viajesData.updateItemEscaneado(value, opt);
                                        if (countCaja == pedidos.getCajas() && countBolsa == pedidos.getBolsas()){
                                            sacaFoto = true;
                                            btnEscan.setText("TOMAR FOTO");
                                            btnEscan.setEnabled(true);
                                            btnEscan.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                                            handler.removeCallbacks(runnable);
                                        }
                                } else if(tipoenv.equals("BOLSA")){
                                    if (pedidos.getBolsas() != countBolsa && pedidos.getBolsas() != despd.getBolsasentregadas()) {
                                        if(pedidos.getBolsas() > 0) {
                                            countBolsa++;
                                        }
                                        viewCajas.setText("CAJAS: " + countCaja + " DE " + pedidos.getCajas());
                                        viewBolsas.setText("BOLSAS: " + countBolsa + " DE " + pedidos.getBolsas());
                                        viewQrEscaneado.setText("QR ACEPTADO");
                                        viewQrEscaneado.setTextColor(Color.GREEN);
                                        viajesData.updateCajaBolsaCount(opt, String.valueOf(itemid.getPedidosregistro()), "" + countBolsa, "" + countCaja);
                                        viajesData.updateItemEscaneado(value, opt);
                                        if (countCaja == pedidos.getCajas() && countBolsa == pedidos.getBolsas()){
                                            sacaFoto = true;
                                            btnEscan.setText("TOMAR FOTO");
                                            btnEscan.setEnabled(true);
                                            btnEscan.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                                            handler.removeCallbacks(runnable);
                                        }
                                    } else {
                                        if (pedidos.getBolsas() == despd.getBolsasentregadas()){
                                            Toast.makeText(getContext(), "Bolsas Completas", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
                            }
                            viewNumPedido.setText("N?? Pedido: " + String.valueOf(itemid.getPedidosregistro()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
}