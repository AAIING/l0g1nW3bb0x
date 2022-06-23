package com.opencode.webboxdespacho.fragments.dialogs;

import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.opencode.webboxdespacho.R;
import com.opencode.webboxdespacho.config.Capture;
import com.opencode.webboxdespacho.models.Itemsid;
import com.opencode.webboxdespacho.models.Pedidos;
import com.opencode.webboxdespacho.models.Viajesd;
import com.opencode.webboxdespacho.sqlite.data.ViajesData;

import java.util.List;

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

    private int countCaja =0, countBolsa =0; //, countCajaEntregada =0, countBolsaEntregada =0
    private TextView viewBolsas, viewCajas, viewNumPedido, viewQrEscaneado;
    private Button btnEscan, btnClose;
    private ScanOptions options;
    private ViajesData viajesData;
    private List<Viajesd> listDespachosd;
    private String opt = "1";
    private Handler handler = new Handler();
    private Runnable runnable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_escanear_dialog, container, false);
        setCancelable(false);
        viajesData = new ViajesData(getContext());
        btnClose = view.findViewById(R.id.button_close_escan);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        btnEscan = view.findViewById(R.id.button_escan);
        btnEscan.setOnClickListener(onClickEscanear);
        viewBolsas = view.findViewById(R.id.view_num_bolsas);
        viewCajas = view.findViewById(R.id.view_num_cajas);
        viewNumPedido = view.findViewById(R.id.view_num_pedido_escan);
        viewQrEscaneado = view.findViewById(R.id.view_qr_msje_escan);

        if(getArguments() != null){
            opt = getArguments().getString("OPT");
        }

        return view;
    }

    private View.OnClickListener onClickEscanear = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            options = new ScanOptions();
            options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
            options.setPrompt("");
            options.setCameraId(0);  //Use a specific camera of the device
            options.setBeepEnabled(true);
            options.setBarcodeImageEnabled(true);
            options.setCaptureActivity(Capture.class);
            barcodeLauncher.launch(options);
        }
    };

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                //
                if(result.getContents() != null) {
                    //
                    String value = result.getContents();
                    if(opt.equals("1")) {
                        /** PARA SUBIDA DE ITEMS AL FURGON*/
                        try {

                            Itemsid itemid = viajesData.getItem(value);
                            String tipoenv = itemid.getTipoitem();
                            Viajesd despd = viajesData.getDespachod(String.valueOf(itemid.getPedidosregistro()));
                            Pedidos pedidos = despd.getPedidos();
                            //
                            handler.removeCallbacks(runnable);
                            runnable = new Runnable() {
                                @Override
                                public void run() {
                                    handler.postDelayed(this, 1000);
                                    //
                                    if (pedidos.getCajas() == despd.getCajascargadas() && pedidos.getBolsas() == despd.getBolsascargadas()) {
                                        viewNumPedido.setText("N° Pedido: " + itemid.getPedidosregistro());
                                        viewQrEscaneado.setText("PEDIDO COMPLETADO");
                                        viewQrEscaneado.setTextColor(Color.GREEN);
                                        viewCajas.setText("CAJAS: " + despd.getCajascargadas() + " DE " + pedidos.getCajas());
                                        viewBolsas.setText("BOLSAS: " + despd.getBolsascargadas() + " DE " + pedidos.getBolsas());
                                        return;
                                    }
                                }
                            };
                            handler.postDelayed(runnable, 0);

                            if (itemid.getEscaneado() > 0) {
                                viewNumPedido.setText("N° Pedido: " + itemid.getPedidosregistro());
                                viewQrEscaneado.setText("QR REPETIDO");
                                viewQrEscaneado.setTextColor(Color.RED);
                                viewCajas.setText("CAJAS: " + despd.getCajascargadas() + " DE " + pedidos.getCajas());
                                viewBolsas.setText("BOLSAS: " + despd.getBolsascargadas() + " DE " + pedidos.getBolsas());
                                return;

                            } else {
                                /** ESCANEADO IGUAL A 0*/
                                if (tipoenv.equals("CAJA")) {
                                    //
                                    if (pedidos.getCajas() != countCaja)
                                        countCaja++;
                                        viewCajas.setText("CAJAS: " + countCaja + " DE " + pedidos.getCajas());
                                        viewBolsas.setText("BOLSAS: " + countBolsa + " DE " + pedidos.getBolsas());
                                        viewQrEscaneado.setText("QR ACEPTADO");
                                        viewQrEscaneado.setTextColor(Color.GREEN);
                                        viajesData.updateCajaBolsaCount(opt, String.valueOf(itemid.getPedidosregistro()), "" + countBolsa, "" + countCaja);
                                        viajesData.updateItemEscaneado(value, opt);

                                } else if (tipoenv.equals("BOLSA")) {
                                    //
                                    if (pedidos.getBolsas() != countBolsa && pedidos.getBolsas() != despd.getBolsascargadas()) {
                                        countBolsa++;
                                        viewCajas.setText("CAJAS: " + countCaja + " DE " + pedidos.getCajas());
                                        viewBolsas.setText("BOLSAS: " + countBolsa + " DE " + pedidos.getBolsas());
                                        viewQrEscaneado.setText("QR ACEPTADO");
                                        viewQrEscaneado.setTextColor(Color.GREEN);
                                        viajesData.updateCajaBolsaCount(opt, String.valueOf(itemid.getPedidosregistro()), "" + countBolsa, "" + countCaja);
                                        viajesData.updateItemEscaneado(value, opt);

                                    } else {
                                        if (pedidos.getBolsas() == despd.getBolsascargadas())
                                            Toast.makeText(getContext(), "Bolsas Completas", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }

                            viewNumPedido.setText("N° Pedido: " + String.valueOf(itemid.getPedidosregistro()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }else if(opt.equals("2")){
                        /** PARA ENTREGA DE ITEMS A CLIENTE*/
                        try {
                            Itemsid itemid = viajesData.getItem(value);
                            String tipoenv = itemid.getTipoitem();
                            Viajesd despd = viajesData.getDespachod(String.valueOf(itemid.getPedidosregistro()));
                            Pedidos pedidos = despd.getPedidos();

                            if (pedidos.getCajas() == despd.getCajasentregadas() && pedidos.getBolsas() == despd.getBolsasentregadas()) {
                                //Toast.makeText(getContext(), "Faltan items para iniciar viaje", Toast.LENGTH_LONG).show();
                                viewNumPedido.setText("N° Pedido: " + itemid.getPedidosregistro());
                                viewQrEscaneado.setText("PEDIDO COMPLETADO");
                                viewQrEscaneado.setTextColor(Color.GREEN);
                                viewCajas.setText("CAJAS: " + despd.getCajasentregadas() + " DE " + pedidos.getCajas());
                                viewBolsas.setText("BOLSAS: " + despd.getBolsasentregadas() + " DE " + pedidos.getBolsas());
                                return;
                            }

                            if (itemid.getEscaneadoEntregado() > 0) {
                                //Toast.makeText(getContext(), "QR YA LEIDO..", Toast.LENGTH_LONG).show();
                                viewNumPedido.setText("N° Pedido: " + itemid.getPedidosregistro());
                                viewQrEscaneado.setText("QR REPETIDO");
                                viewQrEscaneado.setTextColor(Color.RED);
                                viewCajas.setText("CAJAS: " + despd.getCajasentregadas() + " DE " + pedidos.getCajas());
                                viewBolsas.setText("BOLSAS: " + despd.getBolsasentregadas() + " DE " + pedidos.getBolsas());
                                return;

                            } else {
                                /** ESCANEADO IGUAL A 0*/
                                if (tipoenv.equals("CAJA")) {
                                    //
                                    if (pedidos.getCajas() != countCaja)
                                        countCaja++;
                                        viewCajas.setText("CAJAS: " + countCaja + " DE " + pedidos.getCajas());
                                        viewBolsas.setText("BOLSAS: " + countBolsa + " DE " + pedidos.getBolsas());
                                        viewQrEscaneado.setText("QR ACEPTADO");
                                        viewQrEscaneado.setTextColor(Color.GREEN);
                                        viajesData.updateCajaBolsaCount(opt, String.valueOf(itemid.getPedidosregistro()), "" + countBolsa, "" + countCaja);
                                        viajesData.updateItemEscaneado(value, opt);

                                } else if(tipoenv.equals("BOLSA")) {
                                    //
                                    if (pedidos.getBolsas() != countBolsa && pedidos.getBolsas() != despd.getBolsasentregadas()) {
                                        countBolsa++;
                                        viewCajas.setText("CAJAS: " + countCaja + " DE " + pedidos.getCajas());
                                        viewBolsas.setText("BOLSAS: " + countBolsa + " DE " + pedidos.getBolsas());
                                        viewQrEscaneado.setText("QR ACEPTADO");
                                        viewQrEscaneado.setTextColor(Color.GREEN);
                                        viajesData.updateCajaBolsaCount(opt, String.valueOf(itemid.getPedidosregistro()), "" + countBolsa, "" + countCaja);
                                        viajesData.updateItemEscaneado(value, opt);
                                    } else {
                                        if (pedidos.getBolsas() == despd.getBolsasentregadas())
                                            Toast.makeText(getContext(), "Bolsas Completas", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                            viewNumPedido.setText("N° Pedido: " + String.valueOf(itemid.getPedidosregistro()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
}