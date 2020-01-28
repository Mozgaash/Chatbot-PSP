package com.example.chatbotpsp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    public static final String TAG = "xyzyx";
    EditText etEmail, etPassword;
    Button btLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btLogin = findViewById(R.id.btLogin);

        initEvents();
    }

    private void initEvents() {
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initLogin();
            }
        });
    }

    private void initLogin() {
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(etEmail.getText().toString(), etPassword.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                // Log.v("xyz" , user.getEmail());
                if (task.isSuccessful()) {
                    Log.v(TAG, "Login correcto");
                    intentBotActivity();

                } else {
                    Toast.makeText(Login.this, "Email o contraseña erróneos", Toast.LENGTH_SHORT).show();
                    Log.v(TAG, task.getException().toString());
                }
            }
        });
    }

    private void intentBotActivity() {
        Intent intent = new Intent(this, BotActivity.class);
        startActivity(intent);
    }
}
