package com.example.androidproject.admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.androidproject.AddMealActivity;
import com.example.androidproject.R;
import com.example.androidproject.Subscription;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MealsFragment extends Fragment {

    private String TAG = "subscription_list";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_meals, container, false);

        FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), AddMealActivity.class);
                startActivity(i);
            }
        });

        getSubscriptionData(root);

        return root;
    }

    private void getSubscriptionData(View root){
        final ArrayList<Subscription> subscriptionArray = new ArrayList<Subscription>();
        final ArrayList<String> noOfMealsArray = new ArrayList<String>();
        final ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, noOfMealsArray);
        ListView listView = (ListView) root.findViewById(R.id.meals_lv);
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
                            noOfMealsArray.add(c.getLength()+ " Month(s) Plan \n"+ c.getMealCount() + " Meals per month \nPrice: $" + c.getPrice());
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
                Intent i = new Intent(getActivity(), AddMealActivity.class);
                i.putExtra("subscriptionObj", subscriptionArray.get(position));
                startActivity(i);
            }
        });
    }
}