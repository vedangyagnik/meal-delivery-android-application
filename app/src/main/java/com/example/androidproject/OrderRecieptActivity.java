package com.example.androidproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class OrderRecieptActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    TextView subtotal;
    TextView total;
    TextView discount;
    TextView orderId;
    TextView tax;
    String orderDocId;
    private String TAG = "order_receipt";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<String> orderData = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_reciept);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Order Receipt");

        Intent i = getIntent();
        Subscription s = (Subscription) i.getSerializableExtra("order_detail_obj");

        subtotal = (TextView) findViewById(R.id.subtotal);
        total = (TextView) findViewById(R.id.total);
        discount = (TextView) findViewById(R.id.discount);
        orderId = (TextView) findViewById(R.id.order_id);
        tax = (TextView) findViewById(R.id.tax);

        if (i.getExtras() != null) {
            String order_id = orderIdGenrator(5);
            orderId.setText(order_id);
            subtotal.setText("Subtotal: $" + s.getPrice());

            double taxAmount = Double.valueOf(s.getPrice()) * 0.13;
            tax.setText("Tax(13%): $" + String.format("%.2f", taxAmount));

            double discountAmount = (Double.valueOf(s.getDiscount()) / 100.0) * Double.valueOf(s.getPrice());
            discount.setText("Discount(" + s.getDiscount() + "%): -$" + String.format("%.2f", discountAmount));

            double totalAmount = Double.valueOf(s.getPrice()) + taxAmount - discountAmount;
            total.setText("Total: " + String.format("%.2f", totalAmount));

            Map<String, Object> order = new HashMap<>();
            order.put("user_id", mAuth.getInstance().getCurrentUser().getEmail());
            order.put("order_id", order_id);
            order.put("subscription_id", s.getId());
            order.put("initial_order_date", String.valueOf(java.time.LocalDate.now()));
            order.put("next_pick_up_date", String.valueOf(java.time.LocalDate.now().plusDays(7)));
            order.put("parking_spot", null);
            orderData.add(mAuth.getInstance().getCurrentUser().getEmail());
            orderData.add(order_id);
            orderData.add(s.getId());
            orderData.add(String.valueOf(java.time.LocalDate.now()));
            orderData.add(String.valueOf(java.time.LocalDate.now().plusDays(7)));

            db.collection("order")
                .add(order)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        orderDocId = documentReference.getId();
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
        }
    }

    private String orderIdGenrator(int length){

        String randomString = "";
        String alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String num = "0123456789";
        char[] alphaChars = alpha.toCharArray();
        char[] numChars = num.toCharArray();
        for(int i=1; i<=length; i++){
            Random random = new Random();
            if(random.nextBoolean()){
                int randomInt = (int)(Math.random() * (((alpha.length()-1) - 0) + 1)) + 0;
                randomString += String.valueOf(alphaChars[randomInt]);
            } else {
                int randomInt = (int)(Math.random() * (((num.length()-1) - 0) + 1)) + 0;
                randomString += String.valueOf(numChars[randomInt]);
            }
        }

        return randomString;

    }

    public void pickupOrderAction(View view) {
        Intent i = new Intent(OrderRecieptActivity.this, OrderCheckInActivity.class);
        i.putExtra("docId", orderDocId);
        i.putStringArrayListExtra("orderData", orderData);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.logout_menu){
            mAuth.getInstance().signOut();
            Intent i = new Intent(OrderRecieptActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}