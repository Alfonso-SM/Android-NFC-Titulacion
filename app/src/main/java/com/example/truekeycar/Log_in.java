package com.example.truekeycar;

import static androidx.biometric.BiometricPrompt.AuthenticationCallback;
import static androidx.biometric.BiometricPrompt.AuthenticationResult;
import static androidx.biometric.BiometricPrompt.PromptInfo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;


public class Log_in extends AppCompatActivity {
    TextView phoneNumberEdit, passwordEdit;
    TextInputLayout NumeroDeCelular, password;
    private String token;
    boolean auth = false;

    //// finger
    private PromptInfo promptInfo;
    ////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //hooks
        NumeroDeCelular = findViewById(R.id.SignIn_PhoneNo);
        password = findViewById(R.id.SignIn_Password);
        phoneNumberEdit = findViewById(R.id.SignIn_PhoneNo_Text);
        passwordEdit = findViewById(R.id.SignIn_Password_Text);
        /////// Tomar el token de notificacion
        notification();
        SessionManager sessionManager = new SessionManager(Log_in.this, SessionManager.SESSION_USERSESSION);
        if (sessionManager.checkLogin()) {
            HashMap<String, String> phone = sessionManager.getUsersDetailFromSession();
            phoneNumberEdit.setText(phone.get(SessionManager.KEY_PHONENO));
            /* Checar si el celular tiene autentificacion biometrica*/
            if (BiometricManager.from(this).canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.DEVICE_CREDENTIAL) == BiometricManager.BIOMETRIC_SUCCESS) {
                promptInfo = new PromptInfo.Builder()
                        .setTitle("Autentificacion biometrica")
                        .setSubtitle("Autenticate utilizando el sensor biometrico")
                        .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.DEVICE_CREDENTIAL).build();
                autenticate();
            }
        }

    }

    public void callSignup(View view) {
        Intent intent = new Intent(Log_in.this, SignUp.class).putExtra("token", token);
        startActivity(intent);
    }

    public void LoginNext(View view) {
        if (!validateFields()) {
            return;
        }
        GoMain();
    }

    private void GoMain() {
        // get data
        String _NumeroDeCelular = NumeroDeCelular.getEditText().getText().toString().trim();
        String _password = password.getEditText().getText().toString().trim();
        // DataBase
        Query checkUser = FirebaseDatabase.getInstance().getReference("Usuarios").child(_NumeroDeCelular);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    NumeroDeCelular.setError(null);
                    NumeroDeCelular.setErrorEnabled(false);
                    String systemPassword = snapshot.child("password").getValue(String.class);
                    if (systemPassword.equals(_password)) {
                        password.setError(null);
                        password.setErrorEnabled(false);
                        /////////////////////////////////// Tomar informacion del usuario desde Firebase

                        ///////////////////////////////////   Pasar al la siguiente pagina
                        if (!new SessionManager(Log_in.this, SessionManager.SESSION_USERSESSION).checkLogin()) {
                            SessionManager sessionManager = new SessionManager(Log_in.this, SessionManager.SESSION_USERSESSION);
                            sessionManager.createLoginSession(_NumeroDeCelular, "", "");
                        }
                        startActivity(new Intent(Log_in.this, MainActivity.class)
                                /*.putExtra("Token", token)*/);
                        finish();
                    } else {
                        Toast.makeText(Log_in.this, "Contraseña Incorrecta", Toast.LENGTH_SHORT).show();
                        password.setError("Contraseña Incorrecta");
                    }
                } else {
                    Toast.makeText(Log_in.this, "Celular incorrecto", Toast.LENGTH_SHORT).show();
                    NumeroDeCelular.setError("Celular incorrecto");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Log_in.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private boolean validateFields() {
        String _phoneNumber = NumeroDeCelular.getEditText().getText().toString().trim();
        String _password = password.getEditText().getText().toString().trim();
        if (_phoneNumber.isEmpty()) {
            NumeroDeCelular.setError("Ingrese numero de celular registrado");
            NumeroDeCelular.requestFocus();
            return false;
        } else if (_password.isEmpty()) {
            password.setError("Ingrese una contraseña");
            password.requestFocus();
            return false;
        } else {
            NumeroDeCelular.setError(null);
            NumeroDeCelular.setErrorEnabled(false);
            password.setError(null);
            password.setErrorEnabled(false);
            return true;
        }
    }

    public void GotoForgetPass(View view) {
        //Intent intent = new Intent(Log_in.this, ForgetPassword.class);
        //startActivity(intent);
    }

    private void notification() {
        FirebaseMessaging.getInstance().getToken()
                .addOnSuccessListener(s -> token = s);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    private void autenticate() {
        new BiometricPrompt(this,
                ContextCompat.getMainExecutor(this),
                new AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(@NonNull AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        auth = true;
                        startActivity(new Intent(Log_in.this, MainActivity.class)/*.putExtra("Token", token)*/);
                        finish();
                    }
                }).authenticate(promptInfo);


    }

}


