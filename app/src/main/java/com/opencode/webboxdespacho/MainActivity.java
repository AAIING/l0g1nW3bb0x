package com.opencode.webboxdespacho;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.opencode.webboxdespacho.config.SessionDatos;
import com.opencode.webboxdespacho.fragments.MenuFragment;
import com.opencode.webboxdespacho.fragments.PedidosFragment;

public class MainActivity extends AppCompatActivity {

    private SessionDatos sessionDatos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        sessionDatos = new SessionDatos(MainActivity.this);
        Bundle extras = getIntent().getExtras();

        if(extras != null){
            String key = extras.getString("RET_CAM");
            //OPT 2 RETORNA PARA ENTREGA PEDIDO

            PedidosFragment newFragment = new PedidosFragment();
            Bundle bundle = new Bundle();
            bundle.putString("OPT", "2");
            bundle.putString("RET_CAM", key);

            newFragment.setArguments(bundle);
            FragmentTransaction fm = getSupportFragmentManager().beginTransaction();
            fm.replace(R.id.main_activity_frame, newFragment);
            fm.commit();
        }
        else
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_activity_frame, new MenuFragment()).commit();
        }

        //
        /*
        if(sessionDatos.CheckSession())
          getSupportFragmentManager().beginTransaction().replace(R.id.main_activity_frame, new LoginFragment()).commit();
        */
    }
}