package com.geartocare.mechanicP.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.geartocare.mechanicP.Helpers.CustomProgressDialog;
import com.geartocare.mechanicP.Models.Model;
import com.geartocare.mechanicP.Models.ModelService;
import com.geartocare.mechanicP.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UploadImageActivity extends AppCompatActivity {
    Button uploadImage, addImage;
    DatabaseReference root;
    CustomProgressDialog progressDialog;
    ImageView imageView;
    private Uri imageUri;
    StorageReference reference;
    ProgressDialog dialog;
    View.OnClickListener s, c;
    Bitmap bm;
    String currentPhotoPath;
    ModelService serviceDetails;


    private static final int REQUEST_IMAGE_CAPTURE = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);
        getWindow().setStatusBarColor(ContextCompat.getColor(UploadImageActivity.this, R.color.card_black));
        dialog = new ProgressDialog(UploadImageActivity.this);
        dialog.setMessage("Uploading Image");
        dialog.setCancelable(false);

        progressDialog = new CustomProgressDialog(UploadImageActivity.this);
        imageView = findViewById(R.id.image_view);

        serviceDetails = (ModelService) getIntent().getSerializableExtra("serviceDetails");
        root = FirebaseDatabase.getInstance().getReference("Users").child(serviceDetails.getUid())
                .child("vehicles").child(serviceDetails.getVehicleID()).child("services").child(serviceDetails.getServiceID())
                .child("images");


        reference = FirebaseStorage.getInstance().getReference();
        addImage = findViewById(R.id.addImage);
        uploadImage = findViewById(R.id.upload);


        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasCamera()) {
                    ImagePicker
                            .Companion
                            .with(UploadImageActivity.this)
                            .crop()
                            .start();

                }
            }
        });


        s = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri != null) {
                    dialog.show();
                    uploadToFirebase(imageUri);
                } else {
                    Toast.makeText(UploadImageActivity.this, "Please Select Image", Toast.LENGTH_SHORT).show();
                }
            }
        };

        c = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri != null) {
                    dialog.show();
                    uploadToFirebase(imageUri);
                } else {
                    Toast.makeText(UploadImageActivity.this, "Please Select Image", Toast.LENGTH_SHORT).show();
                }
            }
        };


        c = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bm != null) {
                    dialog.show();
                    encodeBitmapAndSaveToFirebase();
                } else {
                    Toast.makeText(UploadImageActivity.this, "Please Select Image", Toast.LENGTH_SHORT).show();
                }
            }
        };
        uploadImage.setOnClickListener(s);

    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                imageUri = FileProvider.getUriForFile(this,
                        "com.geartocare.mechanic",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }


    public void encodeBitmapAndSaveToFirebase() {


        ByteArrayOutputStream baos = new ByteArrayOutputStream();


        bm.compress(Bitmap.CompressFormat.PNG, 5, baos);
        byte[] fileInBytes = baos.toByteArray();

        String key = root.child("services").child(serviceDetails.getServiceID()).child("images").push().getKey();

        final StorageReference fileRef = reference.child("services").child(serviceDetails.getServiceID()).child(key);
        fileRef.putBytes(fileInBytes).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Model model = new Model(uri.toString());
                        String modelId = root.push().getKey();
                        root.child("services").child(serviceDetails.getServiceID()).child("images").child(key).setValue(model);
                        dialog.dismiss();
                        Toast.makeText(UploadImageActivity.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                        imageView.setImageResource(R.drawable.cam_mecha);
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                dialog.show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(UploadImageActivity.this, "Uploading Failed !!", Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {

            imageUri = data.getData();
            imageView.setImageURI(imageUri);

        }

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);


        }


        if (resultCode == RESULT_OK) {
            if (data != null) {
                imageUri = data.getData();
                imageView.setImageURI(imageUri);
            }
        }


    }
    //camera


    private boolean hasCamera() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }


    private void uploadToFirebase(Uri uri) {
        // Uri selectedImageUri = uri.getData();

        Bitmap bmp = null;
        try {
            bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        //here you can choose quality factor in third parameter(ex. i choosen 25)
        bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
        byte[] fileInBytes = baos.toByteArray();

        String key = root.child("services").child(serviceDetails.getServiceID()).child("images").push().getKey();

        final StorageReference fileRef = reference.child("services").child(serviceDetails.getServiceID()).child(key);
        fileRef.putBytes(fileInBytes).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Model model = new Model(uri.toString());

                        root.child(key).setValue(model);
                        dialog.dismiss();
                        Toast.makeText(UploadImageActivity.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                        imageView.setImageResource(R.drawable.cam_mecha);
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                dialog.show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(UploadImageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getFileExtension(Uri mUri) {

        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));

    }
}