package com.example.android.maadhyam;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class UserType extends AppCompatActivity {

    RadioGroup rg;
    RadioButton rb;
    Button next;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_type);

        rg = findViewById(R.id.radio_reg_unreg);
        next = findViewById(R.id.next_button);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i!=1)
                    next.setEnabled(true);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reg_intent;
                int select_id = rg.getCheckedRadioButtonId();
                rb = findViewById(select_id);
                String selected = rb.getText().toString();
                if(selected.equals("Unregistered User")) {
                    reg_intent = new Intent(UserType.this, UnregisteredUserForm.class);
                }
                else {
                    reg_intent = new Intent(UserType.this, RegisteredUserForm.class);
                }
                startActivity(reg_intent);
            }
        });
    }
    @Override
    public void onRestart()
    {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}