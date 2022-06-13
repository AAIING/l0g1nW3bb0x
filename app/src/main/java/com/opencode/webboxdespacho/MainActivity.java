package com.opencode.webboxdespacho;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.opencode.webboxdespacho.fragments.MenuFragment;
import com.opencode.webboxdespacho.fragments.dialogs.LoginDialog;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        getSupportFragmentManager().beginTransaction().replace(R.id.main_activity_frame, new MenuFragment()).commit();
    }
}