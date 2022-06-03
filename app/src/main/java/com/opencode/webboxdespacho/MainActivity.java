package com.opencode.webboxdespacho;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.opencode.webboxdespacho.fragments.LoginFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction().replace(R.id.main_activity_frame, new LoginFragment()).commit();
    }
}