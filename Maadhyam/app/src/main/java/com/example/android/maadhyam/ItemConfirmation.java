package com.example.android.maadhyam;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.ArrayAdapter;
import android.widget.Spinner;

import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class ItemConfirmation extends AppCompatActivity {


    TextView proceed,changeCategory_btn;
    ProgressDialog progress, reload;
    TextView amount;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final FirebaseUser user = mAuth.getCurrentUser();
    String user_id = Objects.requireNonNull(user).getPhoneNumber();

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref_order = database.getReference().child("Users/" + user_id + "/Order");
    DatabaseReference ref_cart = database.getReference().child("Users/" + user_id + "/Cart");
    DatabaseReference ref_bill;

    TextView[] item_category;
    TextView[][] item_brand, item_quantity, item_count;
    LinearLayout[][] category_details;
    ImageView[][] trash;
    LinearLayout[] inScroll;
    LinearLayout scroll_view;
    View[] view_line;
    LinearLayout.LayoutParams llp,scroll_llp,category_llp,view_param,text_llp_left,text_llp_center,text_llp_right;

    int category_count,brand_count,quantity_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_confirmation);

        amount = findViewById(R.id.amount);
        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false);
        progress.show();

        scroll_view = findViewById(R.id.in_scroll);
        fetch_from_db(ref_cart);
        set_price();
    }
    public void set_price() {
        //before updating order
        // copy cart object to order object
        final Boolean[] flag_bill = {true};
        ref_cart.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ref_order.setValue(snapshot.getValue(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                        if (firebaseError != null) {
                            System.out.println("Copy failed");
                        } else {
                            System.out.println("Success");
                            ref_order.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    SharedPreferences sharedPreferences = getSharedPreferences("deliveryAddressKey", MODE_PRIVATE);
                                    String address = sharedPreferences.getString("value", "");
                                    dataSnapshot.getRef().child("deliveryAddress").setValue(address);
                                    int billAmount = 0;
                                    if(flag_bill[0]) {
                                        for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                                            for (DataSnapshot brandSnapshot : categorySnapshot.getChildren()) {
                                                for (DataSnapshot quantitySnapshot : brandSnapshot.getChildren()) {
                                                    billAmount += quantitySnapshot.child("price").getValue(Long.class);
                                                    dataSnapshot.getRef().child("billAmount").setValue(billAmount);
                                                    //Toast.makeText(ConfirmationAndBilling.this, billAmount, LENGTH_LONG).show();
                                                }
                                            }
                                        }
                                        flag_bill[0] =false;
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    throw error.toException();
                                }
                            });
                        }
                    }
                });

                progress.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        progress = new ProgressDialog(this);
//        progress.setTitle("Loading");
//        progress.setMessage("Wait while loading...");
//        progress.setCancelable(false);
//        progress.show();

        //after updating order
        ref_bill = FirebaseDatabase.getInstance().getReference("Users/" + user_id + "/Order/billAmount");
        ref_bill.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue(Integer.class)!=null) {
                    int bill= snapshot.getValue(Integer.class);
                    String net_amount = "Rs. "+bill;
                    amount.setText(net_amount);
                }
                progress.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        proceed = findViewById(R.id.proceed);
        changeCategory_btn = findViewById(R.id.changeCategory_btn);
        changeCategory_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent categoryChange = new Intent(ItemConfirmation.this,category.class);
                startActivity(categoryChange);
            }
        });
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ItemConfirmation.this, "Details stored successfully", Toast.LENGTH_SHORT).show();
                ref_cart.setValue(null);
                startActivity(new Intent(ItemConfirmation.this, ThankYou.class));
            }
        });
        if(reload!=null) {
            reload.dismiss();
            reload = null;
        }
    }
    private void fetch_from_db(final  DatabaseReference firebase_path){
        scroll_llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);//(int) getResources().getDimension(R.dimen.linear_textsize));
        llp.setMargins(0,16,0,0);
        category_llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        category_llp.setMargins(20,20,20,0);//l,t,r,b
        view_param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,2);
        view_param.setMargins(20,0,20,0);
        text_llp_left = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,2f);
        text_llp_left.setMargins(20,0,20,0);
        text_llp_center = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,1f);
        text_llp_center.setMargins(10,0,2,0);
        text_llp_right = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,1f);
        text_llp_right.setMargins(0,0,8,0);

        firebase_path.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                category_count = (int) snapshot.getChildrenCount();
                inScroll = new LinearLayout[category_count];
                item_category = new TextView[category_count];
                view_line = new View[category_count];
                int i_items = 0;
                //Toast.makeText(ItemConfirmation.this,snapshot.toString(),Toast.LENGTH_SHORT).show();

                if(snapshot.getValue()==null) {
                    proceed.setClickable(false);
                    amount.setText("Rs. 0");

                    TextView empty_cart = new TextView(ItemConfirmation.this);
                    empty_cart.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    empty_cart.setText("Your Cart is Empty");
                    empty_cart.setTextSize(18);
                    empty_cart.setGravity(Gravity.CENTER);
                    scroll_view.addView(empty_cart);
                    empty_cart.setPadding(0,10,0,100);

                    Button go_back_shop = new Button(ItemConfirmation.this);
                    go_back_shop.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    go_back_shop.setText("Go back to shopping");
                    go_back_shop.setGravity(Gravity.CENTER);
                    go_back_shop.setTextColor(Color.WHITE);
                    go_back_shop.setPadding(10,0,20,0);
                    go_back_shop.setBackground(getResources().getDrawable(R.drawable.solid_button));

                    LinearLayout temp = new LinearLayout(ItemConfirmation.this);
                    temp.setLayoutParams(scroll_llp);
                    temp.setGravity(Gravity.CENTER);
                    temp.addView(go_back_shop);
                    scroll_view.addView(temp);
                    go_back_shop.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(ItemConfirmation.this,category.class));
                        }
                    });
                    return;
                }

                for (final DataSnapshot category : snapshot.getChildren()) {

                    String category_txt = category.getKey();
                    inScroll[i_items] = new LinearLayout(ItemConfirmation.this);
                    item_category[i_items] = new TextView(ItemConfirmation.this);
                    view_line[i_items] = new View(ItemConfirmation.this);

                    inScroll[i_items].setLayoutParams(scroll_llp);
                    inScroll[i_items].setOrientation(LinearLayout.VERTICAL);
                    inScroll[i_items].setId(i_items);
                    inScroll[i_items].setGravity(Gravity.CENTER_HORIZONTAL);

                    item_category[i_items].setLayoutParams(category_llp);
                    item_category[i_items].setId(i_items);
                    item_category[i_items].setGravity(Gravity.START);
//                    item_category[i_items].setTextSize((int) getResources().getDimension(R.dimen.text_size));
                    item_category[i_items].setTextColor(Color.BLACK);
                    item_category[i_items].setText(category_txt);
                    //Toast.makeText(ItemConfirmation.this, category_txt, Toast.LENGTH_LONG).show();

                    view_line[i_items].setLayoutParams(view_param);
                    view_line[i_items].setBackgroundColor(Color.rgb(0,0,0));

                    inScroll[i_items].addView(item_category[i_items]);
                    inScroll[i_items].addView(view_line[i_items]);

                    brand_count = (int) category.getChildrenCount();
                    int i_brands=0;
                    for (DataSnapshot brand : category.getChildren()) {
                        String brand_txt = brand.getKey();
                        quantity_count = (int) brand.getChildrenCount();

                        category_details = new LinearLayout[brand_count][quantity_count];
                        item_brand = new TextView[brand_count][quantity_count];
                        item_quantity = new TextView[brand_count][quantity_count];
                        item_count = new TextView[brand_count][quantity_count];
                        trash = new ImageView[brand_count][quantity_count];

                        int i_quantity = 0;
                        for (DataSnapshot quantity : brand.getChildren()) {
                            Integer spec_id = Integer.parseInt(""+(i_items+1)+""+""+(i_brands+1)+""+""+(i_quantity+1));
                            String quantity_txt = quantity.getKey();
                            String count_txt = (String) quantity.child("count").getValue();
                            String price_txt = quantity.child("price").getValue().toString();

                            category_details[i_brands][i_quantity] = new LinearLayout(ItemConfirmation.this);
                            item_brand[i_brands][i_quantity] = new TextView(ItemConfirmation.this);
                            item_quantity[i_brands][i_quantity] = new TextView(ItemConfirmation.this);
                            item_count[i_brands][i_quantity] = new TextView(ItemConfirmation.this);
                            trash[i_brands][i_quantity] =new ImageView(ItemConfirmation.this);

                            category_details[i_brands][i_quantity].setLayoutParams(llp);
                            category_details[i_brands][i_quantity].setId(spec_id);
                            category_details[i_brands][i_quantity].setOrientation(LinearLayout.HORIZONTAL);
                            category_details[i_brands][i_quantity].setGravity(Gravity.CENTER_HORIZONTAL);

                            item_brand[i_brands][i_quantity].setLayoutParams(text_llp_left);
                            item_brand[i_brands][i_quantity].setId(i_brands+i_quantity);
//                            item_brand[i_brands][i_quantity].setTextSize((int) getResources().getDimension(R.dimen.text_size));
                            item_brand[i_brands][i_quantity].setTextColor(Color.BLACK);
                            item_brand[i_brands][i_quantity].setText(brand_txt+"  "+quantity_txt);

                            item_quantity[i_brands][i_quantity].setLayoutParams(text_llp_center);
                            item_quantity[i_brands][i_quantity].setId(i_brands+i_quantity);
//                            item_quantity[i_brands][i_quantity].setTextSize((int) getResources().getDimension(R.dimen.text_size));
                            item_quantity[i_brands][i_quantity].setTextColor(Color.BLACK);
                            item_quantity[i_brands][i_quantity].setText("Rs. " + price_txt);

                            item_count[i_brands][i_quantity].setLayoutParams(text_llp_right);
                            item_count[i_brands][i_quantity].setId(i_brands+i_quantity);
//                            item_count[i_brands][i_quantity].setTextSize((int) getResources().getDimension(R.dimen.text_size));
                            item_count[i_brands][i_quantity].setTextColor(Color.BLACK);
                            item_count[i_brands][i_quantity].setText(count_txt);

                            trash[i_brands][i_quantity].setId(spec_id);
                            trash[i_brands][i_quantity].setPadding(10,0,10,0);
                            trash[i_brands][i_quantity].setImageDrawable(getResources().getDrawable(getResources().getIdentifier("@drawable/trash",null,getPackageName())));
                            //trash[i_brands][i_quantity]

                            category_details[i_brands][i_quantity].addView(item_brand[i_brands][i_quantity]);
                            category_details[i_brands][i_quantity].addView(item_count[i_brands][i_quantity]);
                            category_details[i_brands][i_quantity].addView(item_quantity[i_brands][i_quantity]);
                            category_details[i_brands][i_quantity].addView(trash[i_brands][i_quantity]);

                            inScroll[i_items].addView(category_details[i_brands][i_quantity]);

                            trash[i_brands][i_quantity].setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    int id = view.getId();

                                    LinearLayout temp = findViewById(id);
                                    temp.setLayoutParams(new LinearLayout.LayoutParams(0,0));
                                    LinearLayout temp_parent = (LinearLayout) temp.getParent();
                                    TextView cat_view = (TextView) temp_parent.getChildAt(0);
                                    String cat = cat_view.getText().toString();
                                    TextView brand_quant_view = (TextView) temp.getChildAt(0);
                                    String brand_quant = brand_quant_view.getText().toString();
                                    brand_quant = brand_quant.replace("  ","/");


                                    DatabaseReference f_db = database.getReference().child("Users/" + user_id + "/Cart/"+cat+"/"+brand_quant);
                                    f_db.setValue(null);
                                    scroll_view.removeAllViews();
                                    reload = new ProgressDialog(ItemConfirmation.this);
                                    reload.setTitle("Loading");
                                    reload.setMessage("Wait while loading...");
                                    reload.setCancelable(false);
                                    reload.show();

                                    fetch_from_db(ref_cart);
                                    set_price();


                                }
                            });

                            i_quantity++;
                        }
                        i_brands++;
                    }

                    scroll_view.addView(inScroll[i_items]);

                    i_items++;
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if(reload!=null) {
            reload.dismiss();
            reload = null;
        }
    }
}
