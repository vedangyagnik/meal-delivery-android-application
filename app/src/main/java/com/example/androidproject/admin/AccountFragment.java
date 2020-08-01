package com.example.androidproject.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.androidproject.LoginActivity;
import com.example.androidproject.R;
import com.google.firebase.auth.FirebaseAuth;

public class AccountFragment extends Fragment {

    private FirebaseAuth mAuth;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_account, container, false);

        Button menuButton = (Button) root.findViewById(R.id.logout);
        menuButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mAuth.getInstance().signOut();
                Intent i = new Intent(getActivity(), LoginActivity.class);
                startActivity(i);
                getActivity().finish();
            }
        });

        return root;
    }
}