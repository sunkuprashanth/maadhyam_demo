package com.example.android.maadhyam;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class RegisteredUserForm extends AppCompatActivity {

    EditText gst_number, address, alt_mobile;
    Button next_button, back_button;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final FirebaseUser user = mAuth.getCurrentUser();
    String user_id = Objects.requireNonNull(user).getPhoneNumber();

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference().child("Users/"+user_id);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered_user_form);

        gst_number = findViewById(R.id.gst_number);
        address = findViewById(R.id.address);
        alt_mobile = findViewById(R.id.alt_mobile);

        next_button = findViewById(R.id.next_button);
        back_button = findViewById(R.id.back_button);
        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String alternate_mobile_str = alt_mobile.getText().toString();
                String gst_number_str = gst_number.getText().toString();
                String address_str = address.getText().toString();

                if( Pattern.matches("^[+]?[0-9]{10,13}$", alternate_mobile_str) &&
                        Pattern.matches("\\d{2}[A-Z]{5}\\d{4}[A-Z]{1}[A-Z\\d]{1}[Z]{1}[A-Z\\d]{1}",gst_number_str) &&
                            Pattern.matches("\\d+[-]?\\d+([,][a-zA-Z ]+)+",address_str )) {

                    Map<String, Object> users = new HashMap<>();
                    users.put("Registered", new Registered(gst_number_str.trim(),address_str.trim(),alternate_mobile_str.trim()));
                    ref.updateChildren(users);

                    Toast.makeText(RegisteredUserForm.this,"Details stored successfully",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisteredUserForm.this, location.class));
                }
                else
                    Toast.makeText(RegisteredUserForm.this,"Enter valid details",Toast.LENGTH_SHORT).show();
            }
        });

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }
}