package com.example.truekeycar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

public class SignUp3 extends AppCompatActivity {

    CountryCodePicker countryCodePicker;
    TextInputLayout phoneNo;
    String _getUsernamePhoneNumber;
    boolean validation = false;
    // ...
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up3);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Hooks
        countryCodePicker = findViewById(R.id.country_code);
        phoneNo = findViewById(R.id.SignUp_phoneNo);

        // Initialize Firebase Auth
      //  mAuth = FirebaseAuth.getInstance();

    }

    public void CallNextSignUpScreenCode(View view) {
        _getUsernamePhoneNumber = phoneNo.getEditText().getText().toString().trim();
        if (!validatePhoneNumber()) {
           /* FirebaseUser currentUser = mAuth.getCurrentUser();
            updateUI(currentUser); */
            return;
        }

            //Obtener toda la informacion de las actividades pasadas
            String _fullname = getIntent().getStringExtra("fullname");
            String _username = getIntent().getStringExtra("username");
            String _email = getIntent().getStringExtra("email");
            String _password = getIntent().getStringExtra("password");
            String _gender = getIntent().getStringExtra("gender");
            String _date = getIntent().getStringExtra("date");
            //Tomamos el telefono ingresado

            String _phoneNo = "+" + countryCodePicker.getFullNumber() + _getUsernamePhoneNumber;

            Intent intent = new Intent(getApplicationContext(), VerifyOTP.class);
            //pasamos todos los datos a la siguiente actividad
            intent.putExtra("fullname", _fullname);
            intent.putExtra("username", _username);
            intent.putExtra("email", _email);
            intent.putExtra("password", _password);
            intent.putExtra("date", _date);
            intent.putExtra("gender", _gender);
            intent.putExtra("phoneNoComplete", _phoneNo);
            intent.putExtra("phoneNo", _getUsernamePhoneNumber);
            intent.putExtra("Forget", "NewUser");
            startActivity(intent);


    }

    private void updateUI(FirebaseUser currentUser) {
       String hola = currentUser.getUid();
        Toast.makeText(this, hola, Toast.LENGTH_SHORT).show();
    }

    private boolean validatePhoneNumber() {
        Query checkUser = FirebaseDatabase.getInstance().getReference("Users").orderByChild("phoneNo").equalTo(_getUsernamePhoneNumber);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    phoneNo.setError(null);
                    phoneNo.setErrorEnabled(false);
                  validation = true;
                } else {
                    phoneNo.setError("Numero celular ya registrado");
                    validation = false;
                    Toast.makeText(SignUp3.this, "Numero celular ya registrado", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SignUp3.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        return validation;
    }
    public void back7(View view){
        super.onBackPressed();
    }
}