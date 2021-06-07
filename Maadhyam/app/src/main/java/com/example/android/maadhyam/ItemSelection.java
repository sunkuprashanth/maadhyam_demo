package com.example.android.maadhyam;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ItemSelection extends AppCompatActivity {

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref_categories = database.getReference().child("items");
    int category_count;

    LinearLayout in_scroll;
    TextView[] category_txt;
    View[] viewLine;
    LinearLayout.LayoutParams textparam,viewparam;

    Intent i_data;
    static String category;
    Button back, next;

    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_selection_new);

        i_data = new Intent(ItemSelection.this,SelectAndAddItems.class);
        in_scroll = findViewById(R.id.in_scroll);

        back = findViewById(R.id.back_button);
        next = findViewById(R.id.next_button);

        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false);
        progress.show();

        fetch_from_db(ref_categories);

        progress.dismiss();

        ref_categories.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                category_count = (int) snapshot.getChildrenCount();
                for(int i=0; i<category_count;i++){
                    Toast.makeText(ItemSelection.this,"entered",Toast.LENGTH_SHORT).show();
                    final int finalI = i;
                    category_txt[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(ItemSelection.this,"Selected",Toast.LENGTH_SHORT).show();
                            category_txt[finalI].setTextColor(Color.rgb(75,160,174));//4ba0ae
                            category=category_txt[finalI].getText().toString();
                            for(int in=0; in<category_count;in++){
                                if(in!=finalI){
                                    category_txt[in].setTextColor(Color.BLACK);
                                }
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        for(int i=0; i<category_count;i++){
            Toast.makeText(ItemSelection.this,"entered",Toast.LENGTH_SHORT).show();
            final int finalI = i;
            category_txt[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(ItemSelection.this,"Selected",Toast.LENGTH_SHORT).show();
                    category_txt[finalI].setTextColor(Color.rgb(75,160,174));//4ba0ae
                    category=category_txt[finalI].getText().toString();
                    for(int in=0; in<category_count;in++){
                        if(in!=finalI){
                            category_txt[in].setTextColor(Color.BLACK);
                        }
                    }
                }
            });
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if(category!=null) {
                Intent confirmation_intent = new Intent(ItemSelection.this, SelectAndAddItems.class);
                confirmation_intent.putExtra("Category", category);
                startActivity(confirmation_intent);
            }
            else{
                Toast.makeText(ItemSelection.this,"Please select all the fields",Toast.LENGTH_SHORT).show();
            }
            }
        });

    }

    private void fetch_from_db(final  DatabaseReference firebase_path){
        viewparam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,2);
        viewparam.setMargins(9,2,7,0);
        textparam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        textparam.setMargins(15,6,0,0);

        firebase_path.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                category_count = (int) snapshot.getChildrenCount();
                category_txt = new TextView[category_count];
                viewLine = new View[category_count];
                int i_items = 0;
                if(snapshot.getValue()==null) {
                    next.setClickable(false);
                    return;
                }
                for (final DataSnapshot category : snapshot.getChildren()) {
                    String category_string = category.getKey();
                    category_txt[i_items] = new TextView(ItemSelection.this);
                    viewLine[i_items] = new View(ItemSelection.this);

                    category_txt[i_items].setLayoutParams(textparam);
                    category_txt[i_items].setId(i_items);
                    category_txt[i_items].setGravity(Gravity.START);
                    category_txt[i_items].setGravity(Gravity.CENTER);
                    category_txt[i_items].setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f);
                    category_txt[i_items].setTextColor(Color.BLACK);
                    category_txt[i_items].setText(category_string);
                    category_txt[i_items].setClickable(true);

                    viewLine[i_items].setLayoutParams(viewparam);
                    viewLine[i_items].setBackgroundColor(Color.rgb(75,160,174));//4ba0ae

                    in_scroll.addView(category_txt[i_items]);
                    in_scroll.addView(viewLine[i_items]);

                    i_items++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void goto_category(int req_code,String sel) {

        i_data.putExtra("selection",sel);

        startActivityForResult(i_data,req_code);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String returned_selection;
        if(resultCode != 0) {
            if (requestCode == 0) {
                returned_selection = data.getStringExtra("item");
                category = returned_selection;

                i_data.putExtra("selected_category", category);
            }
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ItemSelection.this,location.class));
    }
}
