package com.example.androidproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AddMealActivity extends AppCompatActivity {

    private String TAG = "subscription_add";
    Intent i;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Uri photoURI;

    public final static int  TAKE_PHOTO_ACTIVITY_REQUEST_CODE = 100;
    public final static int  GET_PHOTO_FROM_GALLERY_REQUEST_CODE = 200;
    public String fileName = "photo.jpg";
    File photoFile;
    private StorageReference mStorageRef;
    String downloadUrl;


    ImageView subImgView;
    EditText subLength;
    EditText subPrice;
    EditText subDiscount;
    EditText subDescription;
    EditText subAllergyWarning;
    EditText noOfMeals;
    Button addSub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meal);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Add Meal");
        mStorageRef = FirebaseStorage.getInstance().getReference();

        i = getIntent();
        if (i.getExtras() != null) {
            Subscription s = (Subscription) i.getSerializableExtra("subscriptionObj");
            subLength = (EditText) findViewById(R.id.sub_length);
            subPrice = (EditText) findViewById(R.id.sub_price);
            subDiscount = (EditText) findViewById(R.id.sub_discount);
            subDescription = (EditText) findViewById(R.id.sub_description);
            subAllergyWarning = (EditText) findViewById(R.id.sub_allergy_warning);
            noOfMeals = (EditText) findViewById(R.id.no_of_meals);
            subLength.setText(s.getLength());
            subPrice.setText(s.getPrice());
            subDiscount.setText(s.getDiscount());
            subDescription.setText(s.getDescription());
            subAllergyWarning.setText(s.getAlWarning());
            noOfMeals.setText(s.getMealCount());
            addSub = (Button) findViewById(R.id.add_sub);
            addSub.setText("Edit Subscription");
        }
    }


    public void saveSubscriptionAction(View view) {
        uploadToCloud(System.currentTimeMillis() + ".jpg");
    }

    private void redirectBack(){
        Intent i = new Intent(AddMealActivity.this, AdminPanelActivity.class);
        startActivity(i);
    }

    public void takePhotoAction(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoFile = getFileForPhoto(fileName);
        Uri fileProvider = FileProvider.getUriForFile(AddMealActivity.this, "com.example.androidproject", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, TAKE_PHOTO_ACTIVITY_REQUEST_CODE);
        }
    }

    public File getFileForPhoto(String fileName) {
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Camera");
        if (mediaStorageDir.exists() == false && mediaStorageDir.mkdirs() == false) {
            Log.d("Camera", "Cannot create directory for storing photos");
        }
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    public void selectFromGalleryAction(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, GET_PHOTO_FROM_GALLERY_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_PHOTO_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bitmap image = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                subImgView = findViewById(R.id.sub_photo);
                subImgView.setImageBitmap(image);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), image, "Title", null);
                photoURI = Uri.parse(path);
            } else {
                Toast t = Toast.makeText(this, "Not able to take photo", Toast.LENGTH_SHORT);
                t.show();
            }
        }
        if (requestCode == GET_PHOTO_FROM_GALLERY_REQUEST_CODE) {
            if (data != null) {
                photoURI = data.getData();
                try {
                    Bitmap selectedPhoto = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoURI);
                    Log.d("photo", String.valueOf(photoURI));;
                    subImgView = findViewById(R.id.sub_photo);
                    subImgView.setImageBitmap(selectedPhoto);
                }
                catch (FileNotFoundException e) {
                    Log.d("camera", "FileNotFoundException: Unable to open photo gallery file");
                    e.printStackTrace();
                }
                catch (IOException e) {
                    Log.d("camera", "IOException: Unable to open photo gallery file");
                    e.printStackTrace();
                }
            }
        }
    }

    private void uploadToCloud(String fileName){
        //Upload to cloud
        StorageReference ref = mStorageRef.child(fileName);
        ref.putFile(photoURI)
            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> task = taskSnapshot.getStorage().getDownloadUrl();
                    while (!task.isComplete());
                        downloadUrl = task.getResult().toString();
                        subLength = (EditText) findViewById(R.id.sub_length);
                        subPrice = (EditText) findViewById(R.id.sub_price);
                        subDiscount = (EditText) findViewById(R.id.sub_discount);
                        subDescription = (EditText) findViewById(R.id.sub_description);
                        subAllergyWarning = (EditText) findViewById(R.id.sub_allergy_warning);
                        noOfMeals = (EditText) findViewById(R.id.no_of_meals);

                        String length = subLength.getText().toString();
                        String price = subPrice.getText().toString();
                        String discount = subDiscount.getText().toString();
                        String description = subDescription.getText().toString();
                        String alWarning = subAllergyWarning.getText().toString();
                        String mealCount = noOfMeals.getText().toString();

                        Map<String, Object> subscription = new HashMap<>();
                        subscription.put("length", length);
                        subscription.put("price", price);
                        subscription.put("discount", discount);
                        subscription.put("description", description);
                        subscription.put("allergy_warning", alWarning);
                        subscription.put("no_of_meals", mealCount);
                        subscription.put("image_url", downloadUrl);

                        if (i.getExtras() != null) {
                            Subscription s = (Subscription) i.getSerializableExtra("subscriptionObj");
                            db.collection("subscription")
                                    .document(s.getId())
                                    .set(subscription)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            redirectBack();
                                        }
                                    });
                        } else {
                            db.collection("subscription")
                                    .add(subscription)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            redirectBack();
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
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    // ...
                }
            });
        }
    }