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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.androidproject.AddMealActivity;
import com.example.androidproject.R;
import com.example.androidproject.Subscription;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class OrdersFragment extends Fragment {

    private String TAG = "order_list";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_orders, container, false);

        getOrderData(root);

        return root;
    }

    private void getOrderData(View root){
        final ArrayList<String> ordersArray = new ArrayList<String>();
        final ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, ordersArray);
        ListView listView = (ListView) root.findViewById(R.id.order_lv);
        listView.setAdapter(itemsAdapter);
        db.collection("order")
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String ps = String.valueOf(document.getData().get("parking_spot"));
                            if(!ps.contentEquals("null")) {
                                ordersArray.add("User: " + String.valueOf(document.getData().get("user_id")) + "\nOrder-Id:" + String.valueOf(document.getData().get("order_id")) + "\nParking-Spot: " + String.valueOf(document.getData().get("parking_spot")));
                            }
                        }
                        itemsAdapter.notifyDataSetChanged();
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                }
            });
    }
}