package com.opencode.webboxdespacho;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.opencode.webboxdespacho.config.SessionDatos;
import com.opencode.webboxdespacho.fragments.LoginFragment;
import com.opencode.webboxdespacho.fragments.MenuFragment;

public class MainActivity extends AppCompatActivity {

    private SessionDatos sessionDatos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        sessionDatos = new SessionDatos(MainActivity.this);

        //
        if(sessionDatos.CheckSession())
          getSupportFragmentManager().beginTransaction().replace(R.id.main_activity_frame, new MenuFragment()).commit();
        else
          getSupportFragmentManager().beginTransaction().replace(R.id.main_activity_frame, new LoginFragment()).commit();
    }
}