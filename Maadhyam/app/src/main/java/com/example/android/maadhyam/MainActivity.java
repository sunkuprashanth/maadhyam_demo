package com.example.android.maadhyam;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    String otpCheck;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button next_button = findViewById(R.id.next_button);
        final EditText mobile_no = findViewById(R.id.mobile_no);

        SharedPreferences sharedPreferences = getSharedPreferences("otpCheckKey", MODE_PRIVATE);
        otpCheck = sharedPreferences.getString("value","");
        if(otpCheck==null)otpCheck="unverified";
        if(otpCheck.equals("verified")){
            startActivity(new Intent(MainActivity.this,UserType.class));
        }
        else {
            next_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isValidMobile(mobile_no.getText().toString())) {
                        Intent intent = new Intent(MainActivity.this, OTPVerification_1.class);
                        intent.putExtra("MOBILE_NO", mobile_no.getText().toString().trim());
                        startActivity(intent);
                    } else {
                        Toast.makeText(MainActivity.this, "Enter valid mobile number", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    private boolean isValidMobile(String phone) {
        if(Pattern.matches("^[+]?[0-9]{10,13}$", phone)) {
            return true;
        }
        return false;
    }
}