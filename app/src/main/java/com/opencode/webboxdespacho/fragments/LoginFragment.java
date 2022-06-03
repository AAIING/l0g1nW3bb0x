package com.opencode.webboxdespacho.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.opencode.webboxdespacho.R;
import com.opencode.webboxdespacho.config.ApiConf;
import com.opencode.webboxdespacho.models.Login;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoginFragment() {
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
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
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


    private EditText etUser, etPassword;
    private Button btnLogin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        etUser = view.findViewById(R.id.et_user);
        etUser.setText("");
        etPassword = view.findViewById(R.id.et_passwd);
        etPassword.setText("");
        btnLogin = view.findViewById(R.id.button_login);
        btnLogin.setOnClickListener(onClickLogin);

        return view;
    }

    private  View.OnClickListener onClickLogin = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String usuario = etUser.getText().toString();
            String contrasena = etPassword.getText().toString();
            if(!usuario.isEmpty() && !contrasena.isEmpty()){
                login(usuario, contrasena);
                //
            } else {
                Toast.makeText(getContext(), "Faltan parametro(s)", Toast.LENGTH_SHORT).show();
            }
        }
    };

    /***/
    void login(String usr, String contrasena) {
        Call<Login> call = ApiConf.getData().getLogin(usr, contrasena, 0);
        call.enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                if(response.isSuccessful()){
                    Login login = response.body();
                    //
                    MainFragment newFragment = new MainFragment();
                    FragmentTransaction fm = getActivity().getSupportFragmentManager().beginTransaction();
                    fm.replace(R.id.main_activity_frame, newFragment);
                    fm.commit();
                }
            }
            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                Log.e("ERROR--->", t.toString());

                Toast.makeText(getContext(), "Error login..\ncompruebe usuario y/o contraseña", Toast.LENGTH_LONG).show();
            }
        });
    }
}