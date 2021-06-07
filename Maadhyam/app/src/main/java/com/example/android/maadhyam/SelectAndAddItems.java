package com.example.android.maadhyam;

import androidx.annotation.NonNull;

import androidx.annotation.RequiresApi;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.RelativeLayout;

import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.util.Objects;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)

public class SelectAndAddItems extends AppCompatActivity {

    LinearLayout[] item_layout, add_item_layout;
    TextView[] item_name, item_cost, show_count;
    Button[] incr_count, decr_count;
    Spinner[] quantity;
    LinearLayout parent_layout;
    String selected_category;
    DatabaseReference database;
    List<String> brands;
    Long cost;
    ProgressDialog progress;
    HashMap<String,Object> cart_snapshot;
    Button back_btn, next_btn,changeCategory_btn;

    LinearLayout.LayoutParams llp, blp_1, blp_0, tlp, item_llp;
    Spinner.LayoutParams slp;
    int brands_count = 0, first = 0;


    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final FirebaseUser user = mAuth.getCurrentUser();
    String user_id = Objects.requireNonNull(user).getPhoneNumber();
    FirebaseDatabase databasex = FirebaseDatabase.getInstance();
    DatabaseReference ref_cart = databasex.getReference().child("Users/"+user_id+"/Cart");
//    String timestamp = String.valueOf(System.currentTimeMillis());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_and_add_items);

        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false);
        progress.show();

        back_btn = findViewById(R.id.back_btn);
        next_btn = findViewById(R.id.next_btn);
        changeCategory_btn = findViewById(R.id.changeCategory_btn);

        changeCategory_btn = findViewById(R.id.changeCategory_btn);

        brands = new ArrayList<>();
        parent_layout = findViewById(R.id.parent_layout);

        Intent temp = getIntent();
        selected_category = temp.getStringExtra("Category");


        get_cart();
        fetch_from_db("items/" + selected_category);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        changeCategory_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent categoryChange = new Intent(SelectAndAddItems.this,category.class);
                startActivity(categoryChange);
            }
        });
        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent next_screen = new Intent(SelectAndAddItems.this,ItemConfirmation.class);
                startActivity(next_screen);
            }
        });
    }

    private void fetch_from_db(final String firebase_path) {

        database = FirebaseDatabase.getInstance().getReference().child(firebase_path);

        first = 1;
        llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        slp = new Spinner.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        item_llp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,1f);
        blp_0 = new LinearLayout.LayoutParams(0, 65);
        blp_1 = new LinearLayout.LayoutParams(55, 65);
        tlp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 65);


        final ValueEventListener vl = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                brands_count = (int) snapshot.getChildrenCount();
                //Toast.makeText(SelectAndAddItems.this,firebase_path+brands_count,Toast.LENGTH_LONG).show();

                item_layout = new LinearLayout[brands_count];
                item_name = new TextView[brands_count];
                item_cost = new TextView[brands_count];
                quantity = new Spinner[brands_count];
                incr_count = new Button[brands_count];
                decr_count = new Button[brands_count];
                show_count = new TextView[brands_count];
                add_item_layout = new LinearLayout[brands_count];

                int i_items = 0;

                for (DataSnapshot into_brand : snapshot.getChildren()) {

                    String name = into_brand.getKey();
                    HashMap<String, Object> into_quant = (HashMap<String, Object>) into_brand.getValue();

                    final ArrayAdapter<CharSequence> sadapter
                            = new ArrayAdapter<CharSequence>(SelectAndAddItems.this,
                            android.R.layout.simple_spinner_item, into_quant.keySet().toArray(new CharSequence[0])) {

                        public View getView(int position, View convertView, ViewGroup parent) {

                            View v = super.getView(position, convertView, parent);
                            v.setBackgroundColor(Color.WHITE);
                            TextView tv = ((TextView) v);
                            tv.setTextColor(Color.BLACK);
                            tv.setTextSize(12);
                            return v;
                        }
                    };
                    sadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    item_layout[i_items] = new LinearLayout(SelectAndAddItems.this);
                    item_name[i_items] = new TextView(SelectAndAddItems.this);
                    item_cost[i_items] = new TextView(SelectAndAddItems.this);
                    quantity[i_items] = new Spinner(SelectAndAddItems.this);
                    incr_count[i_items] = new Button(SelectAndAddItems.this);
                    decr_count[i_items] = new Button(SelectAndAddItems.this);
                    show_count[i_items] = new TextView(SelectAndAddItems.this);
                    add_item_layout[i_items] = new LinearLayout(SelectAndAddItems.this);

                    item_layout[i_items].setLayoutParams(llp);
                    item_layout[i_items].setOrientation(LinearLayout.VERTICAL);
                    item_layout[i_items].setId(i_items);
                    item_layout[i_items].setPadding(10, 15, 10, 15);

                    add_item_layout[i_items].setLayoutParams(llp);
                    add_item_layout[i_items].setOrientation(LinearLayout.HORIZONTAL);
                    add_item_layout[i_items].setId(i_items);
                    //add_item_layout[i_items].setBackgroundColor(Color.WHITE);
                    add_item_layout[i_items].setGravity(Gravity.CENTER_VERTICAL);
                    add_item_layout[i_items].setPadding(230, 20, 5, 20);

                    show_count[i_items].setId(i_items);
                    show_count[i_items].setText("Add item");
                    show_count[i_items].setLayoutParams(tlp);
                    show_count[i_items].setPadding(5,5,5,5);
                    show_count[i_items].setGravity(Gravity.CENTER);
                    show_count[i_items].setBackground(getResources().getDrawable(R.drawable.hollow_border_left_cornered));

                    incr_count[i_items].setId(i_items);
                    incr_count[i_items].setLayoutParams(blp_1);
                    incr_count[i_items].setText("+");
                    incr_count[i_items].setPadding(5,5,5,5);
                    incr_count[i_items].setGravity(Gravity.CENTER);
                    incr_count[i_items].setBackground(getResources().getDrawable(R.drawable.solid_border_right_cornered));

                    decr_count[i_items].setId(i_items);
                    decr_count[i_items].setLayoutParams(blp_0);
                    decr_count[i_items].setText("-");
                    decr_count[i_items].setPadding(5,5,5,5) ;
                    decr_count[i_items].setGravity(Gravity.CENTER);
                    decr_count[i_items].setBackground(getResources().getDrawable(R.drawable.solid_border_left_cornered));

                    item_name[i_items].setText(name);
                    item_name[i_items].setTextColor(Color.BLACK);
                    item_name[i_items].setPadding(235, 0, 10, 10);

                    item_cost[i_items].setTextColor(Color.BLACK);
                    item_cost[i_items].setPadding(235, 10, 10, 10);

                    quantity[i_items].setId(i_items);
                    quantity[i_items].setLayoutParams(slp);
                    quantity[i_items].setOverScrollMode(Spinner.MODE_DROPDOWN);
                    quantity[i_items].setAdapter(sadapter);

                    quantity[i_items].setBackground(getResources().getDrawable(R.drawable.spinner_border));      //.setElevation(10);
                    quantity[i_items].setPadding(3, 10, 3, 10);

                   


                    quantity[i_items].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {

                            int id = adapterView.getId();

                            String quantity_selected = (String) adapterView.getItemAtPosition(pos);

                            Integer count = 0;
                            if (cart_snapshot != null)
                                count = retain_cart_items(id, sadapter, quantity_selected, first);
                            fetch_cost_or_stock(firebase_path + "/" + item_name[id].getText() + "/" + quantity_selected,id,"amount");
                            if (count==0) {
                                show_count[id].setText("Add item");
                                show_count[id].setBackground(getResources().getDrawable(R.drawable.hollow_border_left_cornered));
                                show_count[id].setLayoutParams(tlp);

                                decr_count[id].setLayoutParams(blp_0);
                            }
                            else {
                                show_count[id].setText(count.toString());
                                show_count[id].setLayoutParams(blp_1);
                                show_count[id].setBackgroundColor(Color.WHITE);

                                decr_count[id].setLayoutParams(blp_1);
                            }
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                    if (cart_snapshot != null)
                        fetch_start(i_items, sadapter);

                    incr_count[i_items].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int id = view.getId();
                            String path = firebase_path + "/" + item_name[id].getText() + "/" + quantity[id].getSelectedItem();
                            //Toast.makeText(SelectAndAddItems.this,"clicked..",Toast.LENGTH_LONG).show();
                            fetch_cost_or_stock(path, id, "in_stock");
                        }
                    });

                    decr_count[i_items].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int id = view.getId();

                            if (show_count[id].getText().equals("1")) {
                                show_count[id].setText("Add item");
                                show_count[id].setLayoutParams(tlp);
                                show_count[id].setBackground(getResources().getDrawable(R.drawable.hollow_border_left_cornered));
                                decr_count[id].setLayoutParams(blp_0);
                            } else {
                                int count = Integer.parseInt(show_count[id].getText().toString());
                                show_count[id].setText("" + (count - 1));
                            }
                            if (!show_count[id].getText().equals("Add item")) {
                                //Toast.makeText(SelectAndAddItems.this, selected_category + " " + item_name[id].getText() + " " + quantity[id].getSelectedItem().toString() + " " + show_count[id].getText(), Toast.LENGTH_LONG).show();
                                ref_cart.child(selected_category+"/"+item_name[id].getText()+"/"+quantity[id].getSelectedItem().toString()+"/count").setValue(show_count[id].getText());
                                ref_cart.child(selected_category+"/"+item_name[id].getText()+"/"+quantity[id].getSelectedItem().toString()+"/price").setValue(Integer.parseInt((String)show_count[id].getText())*Integer.parseInt( ((String)item_cost[id].getText()).replaceAll("\\D+","") ) );
                            }
                            else{
                                //Toast.makeText(SelectAndAddItems.this, selected_category + " " + item_name[id].getText() + " " + quantity[id].getSelectedItem().toString() + " " + show_count[id].getText(), Toast.LENGTH_LONG).show();
                                ref_cart.child(selected_category+"/"+item_name[id].getText()+"/"+quantity[id].getSelectedItem().toString()).removeValue();
                            }
                        }
                    });


                    View line = new View(SelectAndAddItems.this);
                    line.setBackgroundColor(Color.BLACK);
                    line.setMinimumHeight(1);


                    RelativeLayout temp_left = new RelativeLayout(SelectAndAddItems.this);
                    temp_left.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                    temp_left.setPadding(10,10,5,10);

                    temp_left.setGravity(Gravity.LEFT);
                    temp_left.setGravity(Gravity.CENTER_VERTICAL);
                  //  temp_left.setBackground(getResources().getDrawable(R.drawable.spinner_border));

                    LinearLayout temp_right = new LinearLayout(SelectAndAddItems.this);
                    temp_right.setLayoutParams(item_llp);
                    //temp_right.setPadding(5,2,2,5);
                    temp_right.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);

                    temp_left.addView(quantity[i_items]);

                    temp_right.addView(decr_count[i_items]);
                    temp_right.addView(show_count[i_items]);
                    temp_right.addView(incr_count[i_items]);

                    add_item_layout[i_items].addView(temp_left);
                    add_item_layout[i_items].addView(temp_right);

                    item_layout[i_items].addView(item_name[i_items]);
                    item_layout[i_items].addView(item_cost[i_items]);
                    item_layout[i_items].addView(add_item_layout[i_items]);
                    item_layout[i_items].addView(line);

                    parent_layout.addView(item_layout[i_items]);
                    i_items++;
                }

                progress.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        database.addValueEventListener(vl);
    }

    //upload details of adding items into cart to firebase inside this functions
    private void fetch_cost_or_stock (String db_path, final int id, final String req){

        database = FirebaseDatabase.getInstance().getReference().child(db_path);
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot s : snapshot.getChildren()) {
                    //Toast.makeText(SelectAndAddItems.this,s.toString(),Toast.LENGTH_SHORT).show();
                    if(req.equals("amount")) {
                        if(s.getKey().equals(req)) {
                            cost = s.getValue(Long.class);
                            item_cost[id].setText("Rs. "+cost);
                        }
                    }
                    else if(req.equals("in_stock") && s.getKey().equals(req)) {
                        Long stk = s.getValue(Long.class);
                        String text = show_count[id].getText().toString();
                        Long count = new Long(0);
                        if(!text.equals("Add item"))
                            count = Long.parseLong(text);
                        if(count+1<=stk) {
                            if(count==0) {
                                show_count[id].setText("1");
                                show_count[id].setLayoutParams(blp_1);
                                show_count[id].setBackgroundColor(Color.WHITE);

                                decr_count[id].setLayoutParams(blp_1);
                            }
                            else
                                show_count[id].setText(""+(count+1));
                        }
                        else{
                            //Toast.makeText(SelectAndAddItems.this,"Out Of Stock",Toast.LENGTH_SHORT).show();
                        }

                        //edited from here
                        //Toast.makeText(SelectAndAddItems.this,selected_category+" "+item_name[id].getText()+" "+quantity[id].getSelectedItem().toString()+" "+show_count[id].getText(),Toast.LENGTH_LONG).show();
                        ref_cart.child(selected_category+"/"+item_name[id].getText()+"/"+quantity[id].getSelectedItem().toString()+"/count").setValue(show_count[id].getText());
                        ref_cart.child(selected_category+"/"+item_name[id].getText()+"/"+quantity[id].getSelectedItem().toString()+"/price").setValue(Integer.parseInt((String)show_count[id].getText())*Integer.parseInt( ((String)item_cost[id].getText()).replaceAll("\\D+","") ) );
                        //till here   // str = str.replaceAll("\\D+","");
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private int retain_cart_items(final int id, ArrayAdapter<CharSequence> sadapter, String quant, int first) {
        String name = item_name[id].getText().toString();
        for(String s : cart_snapshot.keySet()) {
            if (s.equals(name)) {
                HashMap<String,Object> item_quants = (HashMap<String,Object>) cart_snapshot.get(s);
                for(String i :item_quants.keySet()) {

                    HashMap<String,String> item_details = (HashMap<String, String>) item_quants.get(i);
                    Toast.makeText(SelectAndAddItems.this,""+first,Toast.LENGTH_SHORT).show();
                    if (quant.equals(i)) {
                        //quantity[id].setSelection(sadapter.getPosition(i));
                        return Integer.parseInt(item_details.get("count"));
                    }
                }
            }
        }
        return 0;
    }

    private int fetch_start (final int id, ArrayAdapter<CharSequence> sadapter) {
        String name = item_name[id].getText().toString();
        for (String s : cart_snapshot.keySet()) {
            if (s.equals(name)) {
                HashMap<String, Object> item_quants = (HashMap<String, Object>) cart_snapshot.get(s);
                for (String i : item_quants.keySet()) {

                    HashMap<String, String> item_details = (HashMap<String, String>) item_quants.get(i);
                    Toast.makeText(SelectAndAddItems.this, "" + first, Toast.LENGTH_SHORT).show();
                    for (int x = 0; x < sadapter.getCount(); x++) {
                        if (sadapter.getItem(x).equals(i)) {
                            quantity[id].setSelection(sadapter.getPosition(i));
                            return Integer.parseInt(item_details.get("count"));
                        }
                    }

                }
            }
        }
        return 0;
    }

    private void get_cart() {

        database = FirebaseDatabase.getInstance().getReference().child("Users/"+ user_id + "/Cart");
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot s : snapshot.getChildren()) {
                    if (s.getKey().equals(selected_category))
                        cart_snapshot = (HashMap<String, Object>) s.getValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
