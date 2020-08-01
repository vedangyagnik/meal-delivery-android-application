package com.example.androidproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignupActivity extends AppCompatActivity {

    private String TAG = "signup";
    private FirebaseAuth mAuth;
    private EditText etEmail;
    private EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getSupportActionBar().hide();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    public void signUpAction(View view){
        etEmail = (EditText) findViewById(R.id.email);
        etPassword = (EditText) findViewById(R.id.password);
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        boolean error = false;
        if(email.contentEquals("")){
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            error = true;
        }else if(password.contentEquals("")){
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            error = true;
        }
        if (!error) {
            createAccount(email, password);
        }
    }

    public void toSignInAct(View view){
        redirectToSignIn();
    }

    private void createAccount(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        redirectToSignIn();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(SignupActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
//                        updateUI(null);
                    }
                }
            });
    }

    private void redirectToSignIn(){
        Intent i = new Intent(SignupActivity.this, LoginActivity.class);
        startActivity(i);
    }
}