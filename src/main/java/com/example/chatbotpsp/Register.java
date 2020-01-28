package com.example.chatbotpsp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class Register extends AppCompatActivity {

    Button btRegister;
    TextView tvEmail, tvPassword, tvRepeatPassword;
    EditText etEmail, etPassword, etRepeatPassword;
    public static final String TAG = "xyzyx";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initComponents();
        initEvents();

    }

    private void initComponents() {
        btRegister = findViewById(R.id.btRegister);
        tvEmail = findViewById(R.id.tvEmail);
        tvPassword = findViewById(R.id.tvPassword);
        tvRepeatPassword = findViewById(R.id.tvRepeatPassword);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etRepeatPassword = findViewById(R.id.etRepeatPassword);
    }

    private void initEvents() {
        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!comprobarValores()) {
                    Log.v(TAG, "Fallo al registrarse");
                } else {
                    register();
                }
            }
        });
    }



    private void register() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(etEmail.getText().toString(),
                etPassword.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //Log.v("xyz" , user.getEmail());
                if (task.isSuccessful()) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    Log.v(TAG, "Registro completo");
                    Log.v("xyz" , user.getEmail());
                    intentBotActivity();
                    // Toast.makeText(this, "Te has registrado !!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.v(TAG, task.getException().toString());
                }
            }
        });
    }

    private void intentBotActivity() {
        Intent intent = new Intent(this, BotActivity.class);
        startActivity(intent);
    }



    private boolean comprobarValores() {
        if (!validarEmail(etEmail.getText().toString())) {
            etEmail.setError("Email no válido");
            return false;
        } else if (etPassword.getText().toString().compareTo(etRepeatPassword.getText().toString()) != 0) {
            //Toast.makeText(Register.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            etPassword.setText("");
            etRepeatPassword.setText("");
            alertDialog();
            return false;
        } else if (etPassword.getText().toString().length() <= 6) {
            Toast.makeText(this, "La contraseña debe ser al menos de 6 caracteres", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
            }
        }

    private boolean validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    private Dialog alertDialog() {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(R.string.dialog_pass_register)
                .setTitle(R.string.dialog_title_fail_password)
                .setPositiveButton(R.string.corregir, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                }).show();
        // Create the AlertDialog object and return it
        return builder.create();
    }












/*

    private void initLogin() {
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(etUser.getText().toString(), etPassword.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Bienvenido", Toast.LENGTH_SHORT).show();
                    startChatActivity();
                } else {
                    Toast.makeText(LoginActivity.this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                    Log.v(TAG, task.getException().toString());
                }
            }
        });
    }

    private void startChatActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void registerUser(String user, String password) {
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(user, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    Toast.makeText(LoginActivity.this, "Registro completado, ya puedes iniciar sesion", Toast.LENGTH_SHORT).show();
                } else {
                    Log.v(TAG, task.getException().toString());
                    Toast.makeText(LoginActivity.this, "Error, " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void registerMenu() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.register_dialog, null);
        final EditText registerUsername, registerPassword;

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Registrarse");
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);
        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        registerUsername = alertLayout.findViewById(R.id.registerUsername);
        registerPassword = alertLayout.findViewById(R.id.registerPassword);

        alert.setPositiveButton("Registrar", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String user = registerUsername.getText().toString();
                String pass = registerPassword.getText().toString();
                registerUser(user, pass);
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

*/
}

