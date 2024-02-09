package com.example.truekeycar;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class contenedorFragmentoGPS extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contenedor_fragmento_gps);
        FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
        DatabaseReference reference = rootNode.getReference();
        SessionManager sessionManager =  new SessionManager(this,SessionManager.SESSION_USERSESSION);
        HashMap<String,String> last = sessionManager.getUsersDetailFromSession();
        String LastCar =  last.get(SessionManager.KEY_LASTCARCODE);
        reference.child("NFC").child(LastCar).child("GPS").setValue("1");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Maps page = new Maps();
        transaction.replace(R.id.container, page);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}