package com.example.androidproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private String TAG = "login";
    private FirebaseAuth mAuth;
    private EditText etEmail;
    private EditText etPassword;
    private String adminUserId = "827h3YLb49f9qrwgFnMIjxIjdp52";
    CallbackManager mCallbackManager = CallbackManager.Factory.create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        FacebookSdk.fullyInitialize();
        LoginButton loginButton = findViewById(R.id.login_button);
        loginButton.setPermissions("email");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
                Log.i(TAG, "onSuccess: logged in successfully");
                redirectToMain(false);
                //Getting the user information
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        // Application code
                        Log.i(TAG, "onCompleted: response: " + response.toString());
                        try {
                            String email = object.getString("email");
                            Log.i(TAG, "onCompleted: Email: " + email);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.i(TAG, "onCompleted: JSON exception");
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
            }
        });

    }

    public void loginAction(View view){
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
            signin(email, password);
        }
    }

    public void toSignUpAct(View view){
        Intent i = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(i);
    }

    private void signin(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        if(user.getUid().contentEquals(adminUserId)){
                            redirectToMain(true);
                        } else {
                            redirectToMain(false);
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

    private void redirectToMain(boolean isAdmin){
        Intent i;
        if (isAdmin){
            i = new Intent(LoginActivity.this, AdminPanelActivity.class);
        } else {
            i = new Intent(LoginActivity.this, MainActivity.class);
        }
        startActivity(i);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        Log.d("hell", user.getEmail());
                        redirectToMain(false);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }
}