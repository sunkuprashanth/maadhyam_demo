package com.example.android.maadhyam;

import android.Manifest;
import android.content.Intent;

import android.content.SharedPreferences;

import android.location.Address;
import android.location.Geocoder;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class location extends AppCompatActivity {
    EditText tvAddress;
    Button btnNext;
    TextView maps;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.location);

        tvAddress = findViewById(R.id.tvAddress);
        btnNext = findViewById(R.id.btnNext);
        maps = findViewById(R.id.choose_maps);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(location.this, DeliveryMaps.class),0);
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String address_str = tvAddress.getText().toString();

                if(tvAddress == null || address_str.isEmpty())
                {
                    Toast.makeText(location.this, "Please enter delivery address", Toast.LENGTH_SHORT).show();
                }
                else
                {
//                    if(Pattern.matches("[0-9-/A-Za-z]*([,][a-zA-Z0-9& ]*)+",address_str )) {
                        SharedPreferences sharedPref = getSharedPreferences("deliveryAddressKey", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("value", address_str);
                        editor.apply();
                        Intent intent = new Intent(location.this, category.class);
                        startActivity(intent);
//                    }
//                    else
//                        Toast.makeText(location.this, "Please enter valid address", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==1) {

            List<Address> addresses;
            double latitude = Double.parseDouble(data.getStringExtra("latitude"));
            double longitude = Double.parseDouble(data.getStringExtra("longitude"));
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                tvAddress.setText(addresses.get(0).getAddressLine(0));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}