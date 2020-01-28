package com.example.chatbotpsp;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {

    Button btRegisterMain, btLoginMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponents();
        initEvents();
    }

    private void initComponents() {
        btRegisterMain = findViewById(R.id.btRegisterMain);
        btLoginMain = findViewById(R.id.btLoginMain);
    }

    private void initEvents() {
        btRegisterMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog();
            }
        });

        btLoginMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentLogin();
            }
        });
    }

    private void intentLogin() {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

    private Dialog alertDialog() {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_title_main);
        builder.setMessage(R.string.dialog_conf_go_register)
                .setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        intentRegisterActivity();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
        // Create the AlertDialog object and return it
        return builder.create();
    }

    private void intentRegisterActivity() {
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }

}

