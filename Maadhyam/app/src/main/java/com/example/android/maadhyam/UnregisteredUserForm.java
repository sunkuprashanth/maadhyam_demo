package com.example.android.maadhyam;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class UnregisteredUserForm extends AppCompatActivity {

    EditText pan_number,aadhar_number;
    Button btn_back, btn_next;
    TextView tvLater;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final FirebaseUser user = mAuth.getCurrentUser();
    String user_id = Objects.requireNonNull(user).getPhoneNumber();

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference().child("Users/"+user_id);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unregistered_users);
        pan_number = findViewById(R.id.pan_number);
        aadhar_number = findViewById(R.id.aadhar_number);
        btn_back = findViewById(R.id.btn_back);
        btn_next = findViewById(R.id.btn_next);

        tvLater = findViewById(R.id.tvLater);

        tvLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UnregisteredUserForm.this, location.class);
                startActivity(intent);
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pan_number_str = pan_number.getText().toString();
                String aadhar_number_str = aadhar_number.getText().toString();

                if (pan_number_str.isEmpty() || aadhar_number_str.isEmpty()) {
                    Toast.makeText(UnregisteredUserForm.this, "Please Enter the details", Toast.LENGTH_SHORT).show();
                }
                else {
                    if(Pattern.matches("[A-Z]{5}[0-9]{4}[A-Z]{1}",pan_number_str)
                            && Pattern.matches("[0-9]{12}",aadhar_number_str)) {
                        Map<String, Object> users = new HashMap<>();
                        users.put("Unregistered", new Unregistered(pan_number_str, aadhar_number_str));
                        ref.updateChildren(users);
                        Intent intent = new Intent(UnregisteredUserForm.this, location.class);
                        startActivity(intent);
                    }
                    else
                        Toast.makeText(UnregisteredUserForm.this, "Please Enter valid details", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}
