package com.example.truekeycar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class VerifyOTP extends AppCompatActivity {

    PinView pinFromUser;
    String codeBySystem;
    FirebaseAuth mAuth;
    String phoneNo,fullname,username,email,password,gender,date,phoneNoComplete,Forget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_o_t_p);
        //hooks
        pinFromUser = findViewById(R.id.pinview);
        // Sacar las variables de actividades pasadas
        phoneNo = getIntent().getStringExtra("phoneNo");
        phoneNoComplete = getIntent().getStringExtra("phoneNoComplete");
        fullname=getIntent().getStringExtra("fullname");
        username=getIntent().getStringExtra("username");
        email=getIntent().getStringExtra("email");
        password=getIntent().getStringExtra("password");
        gender=getIntent().getStringExtra("gender");
        date=getIntent().getStringExtra("date");
        Forget=getIntent().getStringExtra("Forget");

        sendVerificationCodeToUser(phoneNoComplete);

    }

    private void sendVerificationCodeToUser(String phoneNoComplete) {
        mAuth = FirebaseAuth.getInstance();

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNoComplete)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(VerifyOTP.this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }


    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            codeBySystem = s;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                pinFromUser.setText(code);
                verifyCode(code);

            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(VerifyOTP.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeBySystem, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)  {
                        if (task.isSuccessful()) {
                            if (Forget.equals("updateData")) {
                                updateUsersData();
                            }else{
                                storeNewUsersData();
                            }

                            Toast.makeText(VerifyOTP.this,getResources().getString(R.string.Verificacion_completada), Toast.LENGTH_LONG).show();
                        } else {

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(VerifyOTP.this, "Verificacion no completada. Inetntalo nuevamente", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    private void updateUsersData() {
       // Intent intent=new Intent(getApplicationContext(),SetNewPassword.class);
       // intent.putExtra("phoneNo",phoneNo);
       // startActivity(intent);
       // finish();

    }

    private void storeNewUsersData() {
        FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
        DatabaseReference reference = rootNode.getReference("Users");
        //UserHelperClass addNewUser= new UserHelperClass(fullname, username, email,phoneNo, password, date, gender,phoneNoComplete);
      //  reference.child(phoneNo).setValue(addNewUser);  // Child es para poder almacenar los datos con el nombre que se quiera, Use el celular para que no haya mas de 1 usuario con el mismo celular
        Intent intent=new Intent(VerifyOTP.this,Log_in.class);
        startActivity(intent);
        finish();
    }


    public void callNextScreenFromOTP(View view) {

        String code = pinFromUser.getText().toString();
        if (!code.isEmpty()) {
            verifyCode(code);
        }
    }

    public void close(View view){
        startActivity(new Intent(VerifyOTP.this,Log_in.class));
        finish();
    }
}