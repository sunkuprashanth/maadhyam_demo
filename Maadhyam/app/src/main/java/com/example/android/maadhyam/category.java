package com.example.android.maadhyam;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;

import android.os.Build;


import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import android.widget.RelativeLayout;

import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class category extends AppCompatActivity {

    LinearLayout linear_lay_in_card, drop_layout;
    TextView title_items, drop_list;
    Button back, next;
    int items = 0;
    Intent i_data;
    String selected;
    ProgressDialog progress;
    DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        linear_lay_in_card = findViewById(R.id.linear_lay_in_card);
        title_items = findViewById(R.id.title_items);
        drop_layout = findViewById(R.id.drop_layout);
        back = findViewById(R.id.back);
        next = findViewById(R.id.next);


        title_items.setText("Select Category");


        drop_list = findViewById(R.id.drop_list);

        drop_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress = new ProgressDialog(category.this);
                progress.setTitle("Loading");
                progress.setMessage("Wait while loading...");
                progress.setCancelable(false);
                progress.show();

                fetch_from_rdb();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(0);
                onBackPressed();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent confirmation_intent = new Intent(category.this, SelectAndAddItems.class);
                confirmation_intent.putExtra("Category", selected);
                startActivity(confirmation_intent);

            }
        });
    }
    private void fetch_from_rdb() {

        database = FirebaseDatabase.getInstance().getReference().child("items");
        ValueEventListener eventListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                items = (int) dataSnapshot.getChildrenCount();
                drop_layout.removeAllViews();
                TextView rb[] = new TextView[items];
                int i=0;
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String name = ds.getKey();
                    //Toast.makeText(category.this,name,Toast.LENGTH_SHORT).show();

                    rb[i] = new TextView(category.this);
                    rb[i].setId(i);

                    rb[i].setLayoutParams (new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    rb[i].setText(name);

                    rb[i].setPadding(0,0,90,0);
                    rb[i].setText(name);
                    rb[i].setGravity(Gravity.LEFT);
                    rb[i].setPadding(40,0,0,10);
                    rb[i].setTextColor(Color.BLACK);
                    rb[i].setTextSize(16);

                    View line = new View(category.this);
                    //line.setMinimumHeight(1);
                    line.setBackgroundColor(getResources().getColor(R.color.gradient_end));
                    line.setPadding(0,0,0,10);
                    line.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,2));

                    drop_layout.addView(rb[i]);
                    drop_layout.addView(line);
                    rb[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            TextView t = findViewById(view.getId());
                            selected = t.getText().toString();
                            t.setTextColor(getResources().getColor(R.color.gradient_end));
                            next.setEnabled(true);
                        }
                    });
                    i++;
                }
                progress.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        database.addListenerForSingleValueEvent(eventListener);
    }
}