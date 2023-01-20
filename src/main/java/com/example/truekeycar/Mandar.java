package com.example.truekeycar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.truekeycar.Constants.AllConstants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Mandar extends AppCompatActivity {
    String NumUnicoGuardado;
    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState );
        String checar  =  getIntent().getStringExtra("Notificacion");
        Toast.makeText(this, checar, Toast.LENGTH_SHORT).show();
        getToken(checar);
        startActivity(new Intent(this,MainActivity.class));
    }

    private void getToken(String message) {
        SessionManager sessionManager =  new SessionManager(this,SessionManager.SESSION_USERSESSION);
        HashMap<String,String> phone = sessionManager.getUsersDetailFromSession();
        String IdUser = phone.get(SessionManager.KEY_PHONENO);
        NumUnicoGuardado = phone.get(SessionManager.KEY_LASTCARCODE);
        FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
        DatabaseReference reference = rootNode.getReference("NFC").child(NumUnicoGuardado).child("Token");
        Query query = reference;
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    JSONObject to = new JSONObject();
                    JSONObject data = new JSONObject();
                    try {
                        data.put("title", "TRUE KEY USE");
                        data.put("message", message);
                        data.put("id", IdUser);
                        to.put("to", snapshot.getValue().toString());
                        to.put("data", data);
                        // sessionManager.createLoginSession(IdUser,UltimoCocheUsado);
                        sendNotification(to);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void sendNotification(JSONObject to) {

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, AllConstants.NOTIFICATION_URL, to, response -> {

        }, error -> {

        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> map = new HashMap<>();
                map.put("Authorization", "key=" + AllConstants.SERVER_KEY);
                map.put("Content-Type", "application/json");
                return map;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        request.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
    }
}

