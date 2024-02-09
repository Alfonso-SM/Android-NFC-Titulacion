package com.example.truekeycar;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

public class SignUp extends AppCompatActivity {

    TextInputLayout fullname, username, email, password,confirmPassword;
    TextView Creat;
    Button Next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        fullname = findViewById(R.id.signup_fullname);
        username = findViewById(R.id.signup_username);
        email = findViewById(R.id.signup_email);
        password = findViewById(R.id.signup_password);
        confirmPassword = findViewById(R.id.signup_password2);
        Next = findViewById(R.id.signup_next_button);
        Creat = findViewById(R.id.Title_signup);

    }


    public void CallNextSignUpScreen(View view) {


        if (!validateFullName() | !validateEmail() | !validateUserName() | !validatePassword() | !validateConfirmPassword()) {
            return;
        } else {
            //add Transition
            Pair[] pairs = new Pair[2];
            pairs[0] = new Pair<View, String>(Creat, "transition_Text");
            pairs[1] = new Pair<View, String>(Next, "transition_Next");

            Intent intent = new Intent(getApplicationContext(), SignUp2.class);
            intent.putExtra("fullname", fullname.getEditText().getText().toString().trim());
            intent.putExtra("email", email.getEditText().getText().toString().trim());
            intent.putExtra("username", username.getEditText().getText().toString().trim());
            intent.putExtra("password", password.getEditText().getText().toString().trim());

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUp.this, pairs);
                startActivity(intent, options.toBundle());
            } else {
                startActivity(intent);

            }
        }
    }


    private boolean validateFullName() {
        String val = fullname.getEditText().getText().toString().trim();

        if (val.isEmpty()) {
            fullname.setError("Favor de ingresar un nombre");
            return false;
        } else {
            fullname.setError(null);
            fullname.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateUserName() {
        String val = username.getEditText().getText().toString().trim();
        String CheckSpaces = "\\A\\w{1,20}\\z";

        if (val.isEmpty()) {
            username.setError("Favor de ingresar un nombre de usario");
            return false;
        } else if (val.length() > 20) {
            username.setError("Nombre de usuario no puede tener mas de 20 caracteres");
            return false;
        } else if (!val.matches(CheckSpaces)) {
            username.setError("No se puede ingresar espacios en blanco");
            return false;
        } else {
            username.setError(null);
            username.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateEmail() {
        String val = email.getEditText().getText().toString().trim();
        String CheckEmail = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (val.isEmpty()) {
            email.setError("Favor de ingresar un Email");
            return false;
        } else if (!val.matches(CheckEmail)) {
            email.setError("Email no valido");
            return false;
        } else {
            email.setError(null);
            email.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validatePassword() {
        String val = password.getEditText().getText().toString().trim();
        //"(?=.*[0-9])" +  // Un numero
        //"(?=.*[a-z])" + // Una minuscula
        //"(?=.*[A-Z])" +  // Una mayuscula
        //Cualquier letra
        // Caracter especial
        // Espacios en blanco
         String CheckPassword = "[a-zA-Z0-9]";
        if (val.isEmpty()) {
            password.setError("Favor de ingresar una contraseña");
            return false;
        }/*else if (!val.matches(CheckPassword)){
            password.setError("Password too weak");
            return false;
        } */else {
            password.setError(null);
            password.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateConfirmPassword() {
        String val = password.getEditText().getText().toString().trim();
        String val1 = confirmPassword.getEditText().getText().toString().trim();
        if (!val.equals(val1)) {
            confirmPassword.setError("Las contraseñas no son iguales");
            return false;
        } else {
            confirmPassword.setError(null);
            confirmPassword.setErrorEnabled(false);
            return true;
        }
    }

    public void back2(View view){
        super.onBackPressed();
    }


}