package com.example.androidproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class SubscriptionDetailsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    TextView alWarning;
    TextView description;
    TextView mealCount;
    TextView price;
    TextView subPlan;
    ImageView subImage;
    Subscription s;
    private String TAG = "sub_Detail";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Subscription Details");

        alWarning = (TextView) findViewById(R.id.al_warning);
        description = (TextView) findViewById(R.id.description);
        mealCount = (TextView) findViewById(R.id.meal_count);
        price = (TextView) findViewById(R.id.price);
        subPlan = (TextView) findViewById(R.id.sub_plan);
        subImage = (ImageView) findViewById(R.id.mealImage);

        Intent i = getIntent();
        if (i.getExtras() != null) {
            s = (Subscription) i.getSerializableExtra("subscriptionObj");
            alWarning.setText("Allergy Warning: " + s.getAlWarning());
            description.setText(s.getDescription());
            mealCount.setText(s.getMealCount());
            price.setText("$" + s.getPrice());
            subPlan.setText(s.getLength() + " Month(s)");
            Picasso.get().load(s.getImgUrl()).into(subImage);

        }
    }

    public void placeOrderAction(View view) {
        Intent i = new Intent(SubscriptionDetailsActivity.this, OrderRecieptActivity.class);
        i.putExtra("order_detail_obj", s);
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
            Intent i = new Intent(SubscriptionDetailsActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}