package com.example.truekeycar;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.truekeycar.Constants.AllConstants;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class MainActivity extends AppCompatActivity implements NfcAdapter.ReaderCallback{

    MaterialButton Registrar, Prender, Abrir, Cerrar;
    NfcAdapter nfcAdapter;
    MaterialButton idNfc;
    TextView Aviso;
    TextInputEditText NumUnico, NameCar;
    boolean read = false;
    AlertDialog dialog;
    String NumUnicoGuardado, EstadoMotor = "Apagar";
    ArrayList<ClassCars> Prueba = new ArrayList<>();
    ////////////////////////////////////  Drop Down Para escoger coche
    ArrayList<String> Coche = new ArrayList<>();
    ArrayAdapter<String> dataAdapter;
    AutoCompleteTextView autoCompleteTextView;
    ///////////////////////////////////  Open/Close
    public Boolean Door = false;
    int changes = 0;
    PendingIntent pendingIntent;

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (!nfcAdapter.isEnabled())
            startActivity(new Intent("android.settings.NFC_SETTINGS"));

        ////////
        Registrar = findViewById(R.id.registrar);
        Prender = findViewById(R.id.prender);
        Abrir = findViewById(R.id.Abrir);
        Cerrar = findViewById(R.id.Cerrar);
        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        SessionManager sessionManager = new SessionManager(MainActivity.this, SessionManager.SESSION_USERSESSION);
        HashMap<String, String> last = sessionManager.getUsersDetailFromSession();
        if (last.get(SessionManager.KEY_LASTCARNAME) != null) {
            autoCompleteTextView.setText(last.get(SessionManager.KEY_LASTCARNAME));
        }
        /////////////.....................
        String checar = getIntent().getStringExtra("Notificacion");
        if (checar != null)
            getToken(getIntent().getStringExtra("Notificacion"));
        String phone = last.get(SessionManager.KEY_PHONENO);
        Registrar.setOnClickListener(view -> {
            SessionManager sessionManager1 = new SessionManager(MainActivity.this, SessionManager.SESSION_USERSESSION);
            HashMap<String, String> last1 = sessionManager1.getUsersDetailFromSession();
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
            final View popup_WaitNfcs = getLayoutInflater().inflate(R.layout.pop_up_registar, null);
            idNfc = popup_WaitNfcs.findViewById(R.id.idNFC);
            Aviso = popup_WaitNfcs.findViewById(R.id.Aviso);
            NumUnico = popup_WaitNfcs.findViewById(R.id.NumUnico_Text);
            NameCar = popup_WaitNfcs.findViewById(R.id.Nombre_Text);
            if (!last1.get(SessionManager.KEY_LASTCARNAME).equals("")) {
                NumUnico.setText(last1.get(SessionManager.KEY_LASTCARCODE));
                NumUnico.setInputType(InputType.TYPE_NULL);
                NameCar.setText(last1.get(SessionManager.KEY_LASTCARNAME));
                NameCar.setInputType(InputType.TYPE_NULL);
            }

            read = true;
            dialogBuilder.setView(popup_WaitNfcs);
            dialog = dialogBuilder.create();
            dialog.show();
            idNfc.setOnClickListener(view1 -> Aviso.setText(R.string.Aviso)); ////// Tenemos que ver si el numero unico existe
            dialog.setOnDismissListener(dialogInterface -> read = false);
        });
        /* Estas funciones talvez tendran que cambiar */
        Prender.setOnClickListener(view -> {
            if (EstadoMotor.equals("Apagar")) {
                EstadoMotor = "Prender";
                Prender.setText("Apagar Coche");
            } else {
                EstadoMotor = "Apagar";
                Prender.setText("Prender Coche");
            }
            WriteToFirebase("Estado Actual Motor", EstadoMotor);
        });
        Abrir.setOnClickListener(view -> WriteToFirebase("Estado Actual apertura", "Abrir"));
        Cerrar.setOnClickListener(view -> WriteToFirebase("Estado Actual apertura", "Cerrar"));
        Query query = FirebaseDatabase.getInstance().getReference("Usuarios").child(phone).child("Cars");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    if (snapshot1.exists()) {
                        ClassCars classCars = snapshot1.getValue(ClassCars.class);
                        Prueba.add(classCars);
                    }
                }
                for (int j = 0; j < Prueba.size(); j++) {
                    Coche.add(Prueba.get(j).Name);
                }
                Coche.add("Añadir Coche");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        dataAdapter = new ArrayAdapter(this, R.layout.dropdown_item, Coche);
        autoCompleteTextView.setAdapter(dataAdapter);
        autoCompleteTextView.setOnItemClickListener((parent, root, position, id) -> {
            if (position <= Prueba.size() - 1) {
                SessionManager sessionManagerSave = new SessionManager(this, SessionManager.SESSION_USERSESSION);
                sessionManagerSave.createLoginSession(phone, Prueba.get(position).Name, Prueba.get(position).Code);
            } else {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
                final View popup_WaitNfcs = getLayoutInflater().inflate(R.layout.pop_up_registar, null);
                idNfc = popup_WaitNfcs.findViewById(R.id.idNFC);
                Aviso = popup_WaitNfcs.findViewById(R.id.Aviso);
                NumUnico = popup_WaitNfcs.findViewById(R.id.NumUnico_Text);
                NameCar = popup_WaitNfcs.findViewById(R.id.Nombre_Text);
                dialogBuilder.setView(popup_WaitNfcs);
                dialog = dialogBuilder.create();
                dialog.show();
                idNfc.setOnClickListener(view -> {
                    if (NameCar.getText().toString() != "") {
                        if (NumUnico.getText().toString() != "") {
                            SessionManager sessionManagerSave = new SessionManager(this, SessionManager.SESSION_USERSESSION);
                            sessionManagerSave.createLoginSession(phone, NameCar.getText().toString(), NumUnico.getText().toString());
                            NewCar(NameCar.getText().toString(), NumUnico.getText().toString(), phone);
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
        pendingIntent = PendingIntent.getActivity(this,0,new Intent(this,this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),PendingIntent.FLAG_MUTABLE);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        resolveIntent(intent);
    }

    private void resolveIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Tag tag = (Tag) intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            assert tag != null;
            onTagDiscovered(tag);
        }
    }

    private void WriteToFirebase(String Path, String value) {
        SessionManager sessionManager = new SessionManager(this, SessionManager.SESSION_USERSESSION);
        HashMap<String, String> phone = sessionManager.getUsersDetailFromSession();
        NumUnicoGuardado = phone.get(SessionManager.KEY_LASTCARCODE);
        FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
        DatabaseReference reference = rootNode.getReference();
        reference.child("NFC").child(NumUnicoGuardado).child(Path).setValue(value);
        pop_up(value, Path);
    }


    // Converting byte[] to hex string:
    private String ByteArrayToHexString(byte[] inarray) {
        int i, j, in;
        String[] hex = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
        String out = "";
        for (j = 0; j < inarray.length; ++j) {
            in = (int) inarray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i];
        }
        return out;
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
        nfcAdapter.disableReaderMode(this);
        //nfcAdapter.enableReaderMode(this,this::onTagDiscovered,NfcAdapter.FLAG_READER_NFC_A);
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onResume() {
        super.onResume();
        nfcAdapter.enableReaderMode(this, this,PendingIntent.FLAG_IMMUTABLE,null);
        //nfcAdapter.disableReaderMode(this);
        nfcAdapter.enableForegroundDispatch(this, pendingIntent,null, null);
    }


    private void getToken(String message) {
        SessionManager sessionManager = new SessionManager(this, SessionManager.SESSION_USERSESSION);
        HashMap<String, String> phone = sessionManager.getUsersDetailFromSession();
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
                } else {
                    Toast.makeText(MainActivity.this, "El Numero del coche no existe", Toast.LENGTH_SHORT).show();
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

    private void NewCar(String Name, String Code, String phone) {
        ClassCars classCars = new ClassCars(Name, Code);
        FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
        DatabaseReference reference = rootNode.getReference();
        reference.child("Usuarios").child(phone).child("Cars").child(Name).setValue(classCars);  // Child es para poder almacenar los datos con el nombre que se quiera, Use el celular para que no haya mas de 1 usuario con el mismo celular
        Query query = FirebaseDatabase.getInstance().getReference("Usuarios").child(phone).child("Cars");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    if (snapshot1.exists()) {
                        ClassCars classCars = snapshot1.getValue(ClassCars.class);
                        Prueba.add(classCars);
                    }
                }
                Coche.clear();
                for (int j = 0; j < Prueba.size(); j++) {
                    Coche.add(Prueba.get(j).Name);
                }
                Coche.add("Añadir Coche");
                dataAdapter = new ArrayAdapter(MainActivity.this, R.layout.dropdown_item, Coche);
                autoCompleteTextView.setAdapter(dataAdapter);
                dataAdapter.notifyDataSetChanged();
                autoCompleteTextView.setText(Name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });
    }



    @Override
    public void onTagDiscovered(Tag tag) {
        if (!read)
            return;
        byte[] id = null;
        id = tag.getId();
        SessionManager sessionManager = new SessionManager(MainActivity.this, SessionManager.SESSION_USERSESSION);
        HashMap<String, String> phone = sessionManager.getUsersDetailFromSession();
        String numero = phone.get(SessionManager.KEY_PHONENO);
        String ID = ByteArrayToHexString(id);
        FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
        DatabaseReference reference = rootNode.getReference("NFC").child(NumUnico.getText().toString()).child(numero).child("nfcCard");
        Query query = reference;
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;
                Boolean repeat = false;
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    if (snapshot1.getValue().toString().equals(ID)) {
                        repeat = true;
                        Toast.makeText(MainActivity.this, "Tarjeta o pin previamente registrado", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                    i++;
                }

                if (!repeat) {
                    reference.child("IdNfc " + i).setValue(ID);  // Child es para poder almacenar los datos con el nombre que se quiera, Use el celular para que no haya mas de 1 usuario con el mismo celular
                    Toast.makeText(MainActivity.this, "Tarjeta o pin registrados exitosamente", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        read = false;
    }

    @SuppressLint("SetTextI18n")
    public void pop_up(String Accion, String Path) {
        changes = 0;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
        final View popup_Status = getLayoutInflater().inflate(R.layout.pop_up_status_door, null);
        TextView notification = popup_Status.findViewById(R.id.NotificationSend);
        TextView ofWhat = popup_Status.findViewById(R.id.OfWhat);
        ofWhat.setText(Accion);
        dialogBuilder.setView(popup_Status);
        AlertDialog dialog1 = dialogBuilder.create();
        dialog1.show();
        dialog1.setOnDismissListener(dialogInterface -> {
            if (changes != 0) {
                Toast.makeText(MainActivity.this, "No se tendra notificacion si se ejecuta la accion", Toast.LENGTH_SHORT).show();
                changes = 0;
            }
        });

        SessionManager sessionManager = new SessionManager(this, SessionManager.SESSION_USERSESSION);
        HashMap<String, String> phone = sessionManager.getUsersDetailFromSession();
        NumUnicoGuardado = phone.get(SessionManager.KEY_LASTCARCODE);
        FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
        DatabaseReference reference = rootNode.getReference().child("NFC").child(NumUnicoGuardado).child(Path);
        Query query = reference;
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (changes != 0) {
                    notification.setText("Accion\ncompletada");
                    notification.setTextSize(30);
                    ofWhat.setText("");
                    changes = 0;
                } else
                    changes++;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void callGPS(View view) {
        SessionManager sessionManager = new SessionManager(this, SessionManager.SESSION_USERSESSION);
        HashMap<String, String> phone = sessionManager.getUsersDetailFromSession();
        NumUnicoGuardado = phone.get(SessionManager.KEY_LASTCARCODE);

        if (!NumUnicoGuardado.isEmpty()) {
            Intent intent = new Intent(MainActivity.this, contenedorFragmentoGPS.class);
            intent.putExtra("numeroUnico", NumUnicoGuardado);
            startActivity(intent);
        }
    }

    public void admin(View view) {
        Toast.makeText(this, "Aqui se podra administrar las tarjetas guardadas ya sea para borrar o agregar", Toast.LENGTH_SHORT).show();
    }
}