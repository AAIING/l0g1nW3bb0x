package com.opencode.webboxdespacho.fragments.dialogs;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.opencode.webboxdespacho.R;
import com.opencode.webboxdespacho.config.ApiConf;
import com.opencode.webboxdespacho.config.SessionDatos;
import com.opencode.webboxdespacho.config.SessionKeys;
import com.opencode.webboxdespacho.models.Viajes;
import com.opencode.webboxdespacho.models.Viajesd;
import com.opencode.webboxdespacho.models.Login;
import com.opencode.webboxdespacho.sqlite.data.ViajesData;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginDialog extends DialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoginDialog() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginDialog newInstance(String param1, String param2) {
        LoginDialog fragment = new LoginDialog();
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
        void rspta(boolean value, String opt, int numviaje);
    }

    public OnInputSelected mOnInputSelected;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mOnInputSelected = (OnInputSelected) getTargetFragment();
        }catch (ClassCastException e){
            Log.e("LoginDialog", "onAttach: ClassCastException : " + e.getMessage() );
        }
    }

    private EditText etUser, etPassword, etNumViaje;
    private Button btnLogin, btnCerrarLogin;
    private boolean isLogin =false;
    private ViajesData viajesData;
    private AlertDialog alertDialog;
    private SessionDatos sessionDatos;
    private ProgressDialog progressDialog;
    private String opt="1";
    private List<Viajes> listViajes = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        setCancelable(false);
        progressDialog = new ProgressDialog(getContext());
        sessionDatos = new SessionDatos(getContext());
        alertDialog = new AlertDialog.Builder(getContext()).create();
        viajesData = new ViajesData(getContext());
        try {
            listViajes = viajesData.getDespachos();
        } catch (Exception e) {
            e.printStackTrace();
        }
        etUser = view.findViewById(R.id.et_user);
        etUser.setText("");
        etPassword = view.findViewById(R.id.et_passwd);
        etPassword.setText("");
        btnLogin = view.findViewById(R.id.button_login);
        btnLogin.setOnClickListener(onClickLogin);
        etNumViaje = view.findViewById(R.id.et_num_viaje);
        etNumViaje.setVisibility(View.GONE);
        btnCerrarLogin = view.findViewById(R.id.button_close_login);
        btnCerrarLogin.setOnClickListener(onClickCerrarLogin);
        if(getArguments() != null){
            opt = getArguments().getString("OPT");
        }
        return view;
    }

    private  View.OnClickListener onClickCerrarLogin = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismiss();
        }
    };

    private  View.OnClickListener onClickLogin = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //
            if(opt.equals("1")) {
                if (!isLogin) {
                    String usuario = etUser.getText().toString();
                    String contrasena = etPassword.getText().toString();
                    if (!usuario.isEmpty() && !contrasena.isEmpty()) {
                        progressDialog.show();
                        progressDialog.setMessage("Sincronizando..");
                        login(usuario, contrasena);
                    } else {
                        Toast.makeText(getContext(), "Faltan parametro(s)", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    String numviaje = etNumViaje.getText().toString();
                    if (!numviaje.isEmpty()) {
                        progressDialog.show();
                        progressDialog.setMessage("Cargando viaje..");
                        cargarViaje(Integer.parseInt(numviaje));
                    } else {
                        Toast.makeText(getContext(), "Faltan parametro(s)", Toast.LENGTH_SHORT).show();
                    }
                }

            }else if(opt.equals("2")){
                String usuario = etUser.getText().toString();
                String contrasena = etPassword.getText().toString();
                if (!usuario.isEmpty() && !contrasena.isEmpty()) {
                    progressDialog.show();
                    progressDialog.setMessage("Sincronizando..");
                    login(usuario, contrasena);
                } else {
                    Toast.makeText(getContext(), "Faltan parametro(s)", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    /***/
    void login(String usr, String contrasena){
        Call<Login> call = ApiConf.getData().getLogin(usr, contrasena, 0);
        call.enqueue(new Callback<Login>(){
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                if(response.isSuccessful()){
                    Login login = response.body();
                    isLogin = true;
                    if(opt.equals("1")) {
                        btnLogin.setText("Cargar Viaje");
                        etUser.setVisibility(View.GONE);
                        etPassword.setVisibility(View.GONE);
                        etNumViaje.setVisibility(View.VISIBLE);
                    }else if(opt.equals("2")){
                        dismiss();
                        String numviaje = sessionDatos.getRecord().get(SessionKeys.idViaje);
                        mOnInputSelected.rspta(isLogin, opt, Integer.parseInt(numviaje));
                    }
                    progressDialog.dismiss();
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Error login..\ncompruebe usuario y/o contraseña", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                Log.e("ERROR--->", t.toString());
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Error.. ", Toast.LENGTH_LONG).show();
            }
        });
    }

    void cargarViaje(int numviaje){
        Call<List<Viajes>> call = ApiConf.getData().getViajes(numviaje);
        call.enqueue(new Callback<List<Viajes>>() {
            @Override
            public void onResponse(Call<List<Viajes>> call, Response<List<Viajes>> response) {
                //
                if(response.isSuccessful()){
                    List<Viajes> respta = response.body();
                    try {

                        if(listViajes.size() != 0) {
                            viajesData.borrarPedidos();
                            viajesData.insertPedidos(respta);
                            alertDialog.setCanceledOnTouchOutside(false);
                            alertDialog.setTitle("Cargar Viaje");
                            alertDialog.setMessage("Los pedidos han sido cargados..");
                            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                /*
                                MenuFragment newFragment = new MenuFragment();
                                FragmentTransaction fm = getActivity().getSupportFragmentManager().beginTransaction();
                                fm.replace(R.id.main_activity_frame, newFragment);
                                fm.commit();
                                */
                                    mOnInputSelected.rspta(isLogin, opt, numviaje);
                                    dismiss();
                                    alertDialog.dismiss();
                                }
                            });
                            alertDialog.show();
                            //sessionDatos.setIsLoggedIn();
                            progressDialog.dismiss();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(getContext(),"NUMERO DE VIAJE NO EXISTE", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<List<Viajes>> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("ERROR--->", t.toString());
            }
        });
    }
}