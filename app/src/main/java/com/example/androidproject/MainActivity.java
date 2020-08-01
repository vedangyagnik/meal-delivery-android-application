package com.example.androidproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private String TAG = "subscription_list";
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Meal Subscriptions");
        getSubscriptionData();
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
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void getSubscriptionData(){
        final ArrayList<Subscription> subscriptionArray = new ArrayList<Subscription>();
        final ArrayList<String> noOfMealsArray = new ArrayList<String>();
        final ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, noOfMealsArray);
        ListView listView = (ListView) findViewById(R.id.meals_lv);
        listView.setAdapter(itemsAdapter);
        db.collection("subscription")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Subscription ss = new Subscription(document.getId(), String.valueOf(document.getData().get("length")), String.valueOf(document.getData().get("price")), String.valueOf(document.getData().get("discount")), String.valueOf(document.getData().get("description")), String.valueOf(document.getData().get("allergy_warning")), String.valueOf(document.getData().get("no_of_meals")), String.valueOf(document.getData().get("image_url")));
                                subscriptionArray.add(ss);
                            }
                            for(Subscription c : subscriptionArray){
                                noOfMealsArray.add(c.getLength()+ " Month(s) Plan \n"+ c.getMealCount() + " Meals per month\nDiscount: " + c.getDiscount() + "%\nPrice: $" + c.getPrice() + (Integer.valueOf(c.getDiscount()) != 0 ? "\nDiscounted Price: $" + String.format("%.2f", (Double.valueOf(c.getPrice()) - Double.valueOf(c.getPrice()) * (Double.valueOf(c.getDiscount())/100.0))) : ""));
                            }
                            itemsAdapter.notifyDataSetChanged();
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(MainActivity.this, SubscriptionDetailsActivity.class);
                i.putExtra("subscriptionObj", subscriptionArray.get(position));
                startActivity(i);
            }
        });
    }
}