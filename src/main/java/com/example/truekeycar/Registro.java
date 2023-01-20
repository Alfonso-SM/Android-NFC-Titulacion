package com.example.truekeycar;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Registro extends AppCompatActivity {

    MaterialButton CrearCuenta;
    TextInputEditText Numero_celular, Contraseña ,Contraseña2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        CrearCuenta = findViewById(R.id.Crear_Cuenta);
        Numero_celular = findViewById(R.id.PhoneNo_Text);
        Contraseña = findViewById(R.id.Contraseña_Text);
        Contraseña2 = findViewById(R.id.Contraseña2_Text);

        String checar  =  getIntent().getStringExtra("Notificacion");
        CrearCuenta.setText(checar);
        CrearCuenta.setOnClickListener(view -> {
            if (!validar()){
                return;
            }
            String Num = Numero_celular.getText().toString();
            String Con = Contraseña.getText().toString();
            String token = getIntent().getStringExtra("token");
            FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
            DatabaseReference reference = rootNode.getReference();
            ClassUser addNewUser= new ClassUser(Num,Con,token);
            reference.child("Usuarios").child(Num).setValue(addNewUser);  // Child es para poder almacenar los datos con el nombre que se quiera, Use el celular para que no haya mas de 1 usuario con el mismo celular
            Intent intent=new Intent(this,Log_in.class);
            startActivity(intent);
            finish();

        });

    }

    private boolean validar() {
        String Num = Numero_celular.getText().toString();
        String Con = Contraseña.getText().toString();
        return !(Num.isEmpty() | Con.isEmpty());
    }
}