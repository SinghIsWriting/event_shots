package com.example.myshots;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myshots.Models.dataholder;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import es.dmoral.toasty.Toasty;

public
class UploadActivity extends AppCompatActivity {

    EditText roll, name, note;
    TextView tvTotalEvents;
    ImageView img;
    Uri filepath;
    Button browse, upload, showAll;
    ActivityResultLauncher<Intent> activityResultLauncher;
    private int countEvents = 0;
    private final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("events");

    @Override
    protected
    void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        getSupportActionBar().hide();

        img = findViewById(R.id.imageView);
        browse = findViewById(R.id.browse);
        upload = findViewById(R.id.upload);
        showAll = findViewById(R.id.btnShowall);
        tvTotalEvents = findViewById(R.id.tvTotalEvent);
        roll = findViewById(R.id.roll);

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public
            void onActivityResult(ActivityResult result) {
                if (result.getResultCode()  == RESULT_OK && result.getData() != null){
                    filepath = result.getData().getData();
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(filepath);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        img.setImageBitmap(bitmap);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });

        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference().child("events");

        eventRef.addValueEventListener(new ValueEventListener() {
            @Override
            public
            void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()){
                    countEvents = (int) snapshot.getChildrenCount();
                    tvTotalEvents.setText("Total Events: " + Integer.toString(countEvents));
                }else {
                    tvTotalEvents.setText("No Event");
                }
            }
            @Override
            public
            void onCancelled(@NonNull DatabaseError error) {

            }
        });

        browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public
            void onClick(View view) {
                Dexter.withContext(UploadActivity.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {

                            @Override
                            public
                            void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                Intent intent = new Intent(Intent.ACTION_PICK);
                                intent.setType("image/*");
                                activityResultLauncher.launch(intent);
                            }

                            @Override
                            public
                            void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                            }

                            @Override
                            public
                            void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();
                            }
                        }).check();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public
            void onClick(View view) {
                if (roll.getText().toString().isEmpty()){
                    Toasty.warning(getApplicationContext(), "Please fill event number!", Toasty.LENGTH_SHORT, true).show();
//                    Toast.makeText(UploadActivity.this, "Please fill event number", Toast.LENGTH_SHORT).show();
                }else {
                    uploadtofirebase();
                }
            }
        });

        showAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public
            void onClick(View view) {
                startActivity(new Intent(UploadActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    private
    void uploadtofirebase() {

        if (filepath != null){
            ProgressDialog dialog = new ProgressDialog(this);
            dialog.setTitle("Uploading...");
            dialog.setCancelable(false);
            dialog.show();

            name = findViewById(R.id.etName);
            note = findViewById(R.id.note);
            roll = findViewById(R.id.roll);

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference uploader = storage.getReference("Image1" + new Random().nextInt(1000));

            uploader.putFile(filepath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public
                void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Date date = new Date();
                    uploader.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public
                        void onSuccess(Uri uri) {
                            dialog.dismiss();
                            FirebaseDatabase db = FirebaseDatabase.getInstance();
                            DatabaseReference root = db.getReference("events");

                            dataholder obj = new dataholder(name.getText().toString(), note.getText().toString(), uri.toString(), date.getTime());
                            root.child(roll.getText().toString()).setValue(obj);

                            name.setText("");
                            roll.setText("");
                            note.setText("");
                            img.setImageResource(R.drawable.ic_image);
                            Toasty.success(getApplicationContext(), "Event uploaded successfully!", Toasty.LENGTH_SHORT, true).show();
//                            Toast.makeText(UploadActivity.this, "Uploaded event", Toast.LENGTH_SHORT).show();
                        }
                    });

//                    HashMap<String, Object> uploadTime = new HashMap<>();
//                    uploadTime.put("timing", date.getTime());
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public
                void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    float percent = (100 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                    dialog.setMessage("Uploaded: " + (int)percent + " %");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public
                void onFailure(@NonNull Exception e) {
                    Toasty.error(getApplicationContext(), "Failed to upload data!", Toasty.LENGTH_SHORT, true).show();
//                    Toast.makeText(UploadActivity.this, "Failed to upload data...", Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            Toasty.warning(getApplicationContext(), "Please browse an image!", Toasty.LENGTH_SHORT, true).show();
//            Toast.makeText(this, "Please choose an image", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public
    void onBackPressed() {
        Intent intent = new Intent(UploadActivity.this, MainActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }

}