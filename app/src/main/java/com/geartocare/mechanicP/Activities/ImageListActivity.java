package com.geartocare.mechanicP.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.geartocare.mechanicP.Helpers.CustomProgressDialog;
import com.geartocare.mechanicP.Models.Model;
import com.geartocare.mechanicP.Models.ModelService;
import com.geartocare.mechanicP.R;
import com.geartocare.mechanicP.Adapters.ServiceImageListAdapter;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ImageListActivity extends AppCompatActivity {

    FloatingActionButton fab;
    private RecyclerView recyclerView;
    private ArrayList<Model> list;
    ModelService serviceDetails;
    SimpleDateFormat sdf;
    private ServiceImageListAdapter adapter;
    private static final int CAMERA_REQUEST_CODE = 1;
    CustomProgressDialog progressDialog;
    private DatabaseReference root;
    TextView imageList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list);
        getWindow().setStatusBarColor(ContextCompat.getColor(ImageListActivity.this, R.color.card_black));


        sdf = new SimpleDateFormat("dd_MM_yyyy");
        serviceDetails = (ModelService) getIntent().getSerializableExtra("serviceDetails");
        root = FirebaseDatabase.getInstance().getReference("Users").child(serviceDetails.getUid())
                .child("vehicles").child(serviceDetails.getVehicleID()).child("services").child(serviceDetails.getServiceID())
                .child("images");
        progressDialog = new CustomProgressDialog(ImageListActivity.this);

        fab = findViewById(R.id.fab);
        recyclerView = findViewById(R.id.rv_imageList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ImageListActivity.this));
        list = new ArrayList<>();
        adapter = new ServiceImageListAdapter(ImageListActivity.this, list);
        recyclerView.setAdapter(adapter);
        imageList = findViewById(R.id.imageList);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ImageListActivity.this, UploadImageActivity.class)
                        .putExtra("serviceDetails", serviceDetails)


                );
            }
        });


        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    progressDialog.show();
                    list.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Model model = new Model(dataSnapshot.child("imageUrl").getValue(String.class));
                        list.add(model);
                    }
                    adapter.notifyDataSetChanged();
                    progressDialog.dismiss();
                }


            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }


}