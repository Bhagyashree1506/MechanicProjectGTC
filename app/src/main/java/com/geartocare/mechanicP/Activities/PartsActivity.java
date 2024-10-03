package com.geartocare.mechanicP.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.geartocare.mechanicP.Adapters.PartAdapter;
import com.geartocare.mechanicP.Helpers.CustomProgressDialog;
import com.geartocare.mechanicP.Models.ModelPart;
import com.geartocare.mechanicP.Models.ModelPartSv;
import com.geartocare.mechanicP.Models.ModelService;
import com.geartocare.mechanicP.R;
import com.geartocare.mechanicP.SessionManager;
import com.geartocare.mechanicP.databinding.ActivityPartsBinding;

import java.util.ArrayList;
import java.util.HashMap;

public class PartsActivity extends AppCompatActivity {
    ActivityPartsBinding binding;
    ModelService serviceDetails;
    DatabaseReference SvRef, DBref;
    SessionManager sessionManager;
    ArrayList<ModelPartSv> parts;
    PartAdapter adapter;
    CustomProgressDialog progressDialog;
    HashMap<String, Object> par_map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPartsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(ContextCompat.getColor(PartsActivity.this, R.color.card_black));

        serviceDetails = (ModelService) getIntent().getSerializableExtra("serviceDetails");
        progressDialog = new CustomProgressDialog(PartsActivity.this);
        sessionManager = new SessionManager(PartsActivity.this);
        SvRef = FirebaseDatabase.getInstance().getReference("Users").child(serviceDetails.getUid()).child("vehicles").child(serviceDetails.getVehicleID())
                .child("services").child(serviceDetails.getServiceID());

        DBref = FirebaseDatabase.getInstance().getReference("mechanics").child(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_MECH_ID)).child("PartList");
        parts = new ArrayList<>();

        par_map = new HashMap<>();
        adapter = new PartAdapter(PartsActivity.this, parts);

        binding.rvParts.setAdapter(adapter);
        binding.rvParts.setHasFixedSize(true);
        binding.rvParts.setLayoutManager(new LinearLayoutManager(PartsActivity.this));


        makeList();


        binding.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                DBref.updateChildren(adapter.getUpdated_parts()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            SvRef.child("PartList").updateChildren(adapter.getSv_up_parts()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        progressDialog.dismiss();
                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(PartsActivity.this, "Some error occurred", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(PartsActivity.this, "Some error occurred", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        });






    }

    private void makeList() {
        progressDialog.show();
        parts.clear();
        DBref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot partListSnap) {


                if (partListSnap.exists()) {


                    SvRef.child("PartList").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot svPartsSnap) {


                            for (DataSnapshot partSnap : partListSnap.getChildren()) {

                                ModelPartSv m = new ModelPartSv();

                                m.setModelPart(partSnap.getValue(ModelPart.class));

                                if (svPartsSnap.child(partSnap.getKey()).exists()) {
                                    m.setUse(svPartsSnap.child(partSnap.getKey()).getValue(String.class));
                                } else {
                                    m.setUse("0");
                                }

                                parts.add(m);

                            }
                            adapter.notifyDataSetChanged();


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }
                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}