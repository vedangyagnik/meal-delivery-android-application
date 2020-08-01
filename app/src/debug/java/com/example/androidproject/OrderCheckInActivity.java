package com.example.androidproject;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrderCheckInActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Double distance;
    private boolean locationPermissionGranted;
    private static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final LatLng defaultLocation = new LatLng(43.6761, -79.4105);
    private Location lastKnownLocation;
    private static final int DEFAULT_ZOOM = 10;
    Context context;
    private FusedLocationProviderClient fusedLocationProviderClient;
    LatLng location;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_check_in);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        Intent i = getIntent();
        ArrayList<String> orderData = i.getStringArrayListExtra("orderData");
        Toast.makeText(getApplicationContext(), "Your scheduled order pickup date is " + orderData.get(4), Toast.LENGTH_LONG).show();
    }
    
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //Pickup Location
        LatLng restaurant = new LatLng(43.676591, -79.411494);
        LatLng home = new LatLng(43.676767, -79.410802);
        mMap.addMarker(new MarkerOptions().position(restaurant).title("Pickup point"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(restaurant));
        mMap.getUiSettings().setZoomControlsEnabled(true);

        getLocationPermission();
        updateLocationUI();
        getDeviceLocation();

        distance = SphericalUtil.computeDistanceBetween(restaurant, home);
        Log.d("distance", String.valueOf(distance));
    }

    public void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSION_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                } else {
                    mMap.addMarker(new MarkerOptions().position(defaultLocation).title("defaultLocation is here!!!"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(defaultLocation));
                    mMap.getUiSettings().setZoomControlsEnabled(true);
                }
            }
        }
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            Log.d("abc", String.valueOf(lastKnownLocation));
                            if (lastKnownLocation != null) {

                                location = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                                mMap.addMarker(new MarkerOptions().position(location).title("current location"));
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                        } else {
                            Log.d("abc", "abcd", task.getException());
                            mMap.addMarker(new MarkerOptions().position(defaultLocation).title("default location"));
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    public void checkInAction(View view) {
        EditText spot = (EditText) findViewById(R.id.spotEditTxt);
        final String spotValue = spot.getText().toString();
        Intent i = getIntent();
        String docId =  i.getStringExtra("docId");
        ArrayList<String> orderData = i.getStringArrayListExtra("orderData");
        if(!orderData.get(4).contentEquals(String.valueOf(java.time.LocalDate.now()))) {
            Toast.makeText(getApplicationContext(), "Your scheduled order pickup date is " + orderData.get(4), Toast.LENGTH_SHORT).show();
        } else {
            if (distance > 100) {
                Toast.makeText(getApplicationContext(), "You are not near parking spot yet... " + orderData.get(4), Toast.LENGTH_SHORT).show();
            } else {
                Map<String, Object> order = new HashMap<>();
                order.put("user_id", orderData.get(0));
                order.put("order_id", orderData.get(1));
                order.put("subscription_id", orderData.get(2));
                order.put("initial_order_date", orderData.get(3));
                order.put("next_pick_up_date", orderData.get(4));
                order.put("parking_spot", spotValue);
                db.collection("order")
                        .document(docId)
                        .set(order)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(), "Order picked up successfully from parking spot: " + spotValue, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }
}