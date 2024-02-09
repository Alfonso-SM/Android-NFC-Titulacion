package com.example.truekeycar;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class SignUp2 extends AppCompatActivity {

    RadioGroup radioGroup;
    RadioButton selectedGender;
    DatePicker datePicker;
    Button Next;
    TextView Creat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up2);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        radioGroup = findViewById(R.id.SignUp_gender);
        datePicker = findViewById(R.id.signup_Date);
        Next = findViewById(R.id.signup2_next_button);
        Creat = findViewById(R.id.Title_signup2);

    }

    public void CallNextSignUpScreen2(View view) {


        if (!validateAge() | !validateGender()) {
            return;
        }

        //add Transition
        Pair[] pairs = new Pair[2];
        pairs[0] = new Pair<View, String>(Creat, "transition_Text");
        pairs[1] = new Pair<View, String>(Next, "transition_Next");

        selectedGender = findViewById(radioGroup.getCheckedRadioButtonId());
        String Gender = selectedGender.getText().toString();

        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();
        String date = day + "/" + (month + 1) + "/" + year;

        String _fullname = getIntent().getStringExtra("fullname");
        String _username = getIntent().getStringExtra("username");
        String _email = getIntent().getStringExtra("email");
        String _password = getIntent().getStringExtra("password");

        Intent intent = new Intent(getApplicationContext(), SignUp3.class);
        intent.putExtra("fullname", _fullname);
        intent.putExtra("username", _username);
        intent.putExtra("email", _email);
        intent.putExtra("password", _password);
        intent.putExtra("date", date);
        intent.putExtra("gender", Gender);


        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUp2.this, pairs);
        startActivity(intent, options.toBundle());


    }


    private boolean validateGender() {
        if (radioGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Porfavor selecciona algun genero", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }

    }

    private boolean validateAge() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int UserAge = datePicker.getYear();
        int isAgeValid = currentYear - UserAge;

        if (isAgeValid < 16) {
            Toast.makeText(this, "No eres elegible por la edad", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }

    }
    public void back3(View view){
        super.onBackPressed();
    }
}