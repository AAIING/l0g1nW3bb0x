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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VerPedidoDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VerPedidoDialog extends DialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public VerPedidoDialog() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VerPedidoDialog.
     */
    // TODO: Rename and change types and number of parameters
    public static VerPedidoDialog newInstance(String param1, String param2) {
        VerPedidoDialog fragment = new VerPedidoDialog();
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

    private EditText editNomCliente, editDirDesp, editComuna, editCondominio;
    private TextView viewCajas, viewBolsas;
    private Button btnAceptar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ver_pedido_dialog, container, false);
        editNomCliente = view.findViewById(R.id.edit_nombre_cliente);
        editDirDesp = view.findViewById(R.id.edit_direccion_cliente);
        editComuna = view.findViewById(R.id.edit_comuna_cliente);
        editCondominio = view.findViewById(R.id.edit_condominio_cliente);
        viewCajas = view.findViewById(R.id.view_cajas_cliente);
        viewBolsas = view.findViewById(R.id.view_bolsas_cliente);
        btnAceptar = view.findViewById(R.id.btn_aceptar_verpedido);  
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        if(getArguments() != null){
            editNomCliente.setText(getArguments().getString("CLIENTE"));
            editDirDesp.setText(getArguments().getString("DIRENVIO"));
            editComuna.setText(getArguments().getString("COMUNA"));
            editCondominio.setText(getArguments().getString("CONDOMINIO"));
            viewCajas.setText("CAJAS: "+getArguments().getString("CAJAS"));
            viewBolsas.setText("BOLSAS: "+getArguments().getString("BOLSAS"));
        }

        return view;
    }
}