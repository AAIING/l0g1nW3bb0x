package com.opencode.webboxdespacho.fragments.dialogs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.opencode.webboxdespacho.R;
import com.opencode.webboxdespacho.config.Capture;

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

    private int countCaja =0, countBolsa =0;
    private TextView viewBolsas, viewCajas;
    private Button btnEscan, btnClose;
    private ScanOptions options;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_escanear_dialog, container, false);
        setCancelable(false);

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

        options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        options.setPrompt("");
        options.setCameraId(0);  //Use a specific camera of the device
        options.setBeepEnabled(true);
        options.setBarcodeImageEnabled(true);
        options.setCaptureActivity(Capture.class);
        barcodeLauncher.launch(options);

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
                    //Toast.makeText(MyActivity.this, "Cancelled", Toast.LENGTH_LONG).show();
                    String value = result.getContents();

                    if(value.equals("CAJA")){
                        countCaja++;
                        viewCajas.setText("CAJAS: "+countCaja+" DE Y");
                    }else if(value.equals("BOLSA")){
                        countBolsa++;
                        viewBolsas.setText("BOLSAS: "+countBolsa+" DE Y");
                    }


                } else {
                    //Toast.makeText(MyActivity.this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                }

            });

    /*
    ActivityResultLauncher<Intent> launchSomeActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        // your operation....
                        //String filePath = data.getStringExtra("result");
                        //tipoArchivo="1";
                    }
                    //
                    //loadList();
                }
            });
     */
}