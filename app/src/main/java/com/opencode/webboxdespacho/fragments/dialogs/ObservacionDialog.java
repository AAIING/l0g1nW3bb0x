package com.opencode.webboxdespacho.fragments.dialogs;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.opencode.webboxdespacho.R;
import com.opencode.webboxdespacho.models.Pedidos;
import com.opencode.webboxdespacho.sqlite.data.ViajesData;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ObservacionDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ObservacionDialog extends DialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ObservacionDialog() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ObservacionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ObservacionDialog newInstance(String param1, String param2) {
        ObservacionDialog fragment = new ObservacionDialog();
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

    private TextView viewNumPedido;
    private String opt="1", numpedido="", nomcliente="";
    private ViajesData viajesData;
    private Button btnAceptar;
    private EditText editObservacion;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_observacion, container, false);
        viajesData = new ViajesData(getContext());
        viewNumPedido = view.findViewById(R.id.view_num_pedido_observacion);
        btnAceptar = view.findViewById(R.id.btn_aceptar_observacion);
        btnAceptar.setOnClickListener(onClickObs);
        editObservacion = view.findViewById(R.id.edit_observacion_item);
        if(getArguments() != null){
            opt = getArguments().getString("OPT");
            if(opt.equals("2")) {
                numpedido = getArguments().getString("NUMPEDIDO");
                nomcliente = getArguments().getString("NOMCLIENTE");
                viewNumPedido.setText("N° Pedido: " + numpedido +"\n"+"\n"+"Cliente: "+nomcliente);
                //
                try {
                    Pedidos pedidos = viajesData.getPedidoObserv(numpedido);
                    editObservacion.setText(pedidos.getObs());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return view;
    }

    private View.OnClickListener onClickObs = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                viajesData.updatePedidoObserv(numpedido, editObservacion.getText().toString());
                dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}