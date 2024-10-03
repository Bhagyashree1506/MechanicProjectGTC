package com.geartocare.mechanicP.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.geartocare.mechanicP.Helpers.CustomProgressDialog;
import com.geartocare.mechanicP.Helpers.PaymentHelper;
import com.geartocare.mechanicP.Helpers.VehicleHelper;
import com.geartocare.mechanicP.Models.ModelLocation;
import com.geartocare.mechanicP.Models.ModelService;
import com.geartocare.mechanicP.R;
import com.geartocare.mechanicP.SessionManager;
import com.geartocare.mechanicP.databinding.EditVehicleBinding;
import com.geartocare.mechanicP.databinding.PaymentConfirmationCardBinding;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.HashMap;

public class ServiceDetailActivity extends AppCompatActivity {
    TextInputLayout customer_fullname, customer_phone, customer_email;
    TextInputLayout customer_service_date, customer_service_time, customer_service_vehicle_no, customer_service_address;
    DatabaseReference DB_Cust;

    MaterialCardView uploadImage, getLocation, done, parts, editVh;
    ModelService serviceDetails;
    SimpleDateFormat sdf;
    CustomProgressDialog progressDialog;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_detail);
        getWindow().setStatusBarColor(ContextCompat.getColor(ServiceDetailActivity.this, R.color.card_black));
        sdf = new SimpleDateFormat("dd_MM_yyyy");
        serviceDetails = (ModelService) getIntent().getSerializableExtra("serviceDetails");
        customer_fullname = findViewById(R.id.customer_fullname);
        customer_phone = findViewById(R.id.customer_phone);
        customer_email = findViewById(R.id.customer_email);
        customer_service_address = findViewById(R.id.customer__service_address);
        customer_service_date = findViewById(R.id.customer_service_date);
        customer_service_time = findViewById(R.id.customer_service_time);
        customer_service_vehicle_no = findViewById(R.id.customer__service_vehicleNo);
        uploadImage = findViewById(R.id.upload_image_btn);
        getLocation = findViewById(R.id.get_location_btn);
        done = findViewById(R.id.done_btn);
        parts = findViewById(R.id.parts_btn);
        editVh = findViewById(R.id.edit_vh);
        progressDialog = new CustomProgressDialog(ServiceDetailActivity.this);
        sessionManager = new SessionManager(ServiceDetailActivity.this);

        DB_Cust = FirebaseDatabase.getInstance().getReference("Users").child(serviceDetails.getUid());

        progressDialog.show();
        DB_Cust.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    String fullName = snapshot.child("fullName").getValue(String.class);

                    String phone = snapshot.child("phone").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);

                    String date = sdf.format(Long.valueOf(serviceDetails.getDate()));
                    String time = snapshot.child("vehicles").child(serviceDetails.getVehicleID()).child("services")
                            .child(serviceDetails.getServiceID()).child("time").getValue(String.class);
                    String address = snapshot.child("vehicles").child(serviceDetails.getVehicleID()).child("services")
                            .child(serviceDetails.getServiceID()).child("location").child("txt").getValue(String.class);
                    String vehicle_no = snapshot.child("vehicles").child(serviceDetails.getVehicleID()).child("vehicleNo").getValue(String.class);

                    customer_phone.getEditText().setText(phone);
                    customer_fullname.getEditText().setText(fullName);
                    customer_email.getEditText().setText(email);
                    customer_service_date.getEditText().setText(date);
                    customer_service_time.getEditText().setText(time);
                    customer_service_address.getEditText().setText(address);
                    customer_service_vehicle_no.getEditText().setText(vehicle_no);

                    ModelLocation location = snapshot.child("vehicles").child(serviceDetails.getVehicleID()).child("services").child(serviceDetails.getServiceID())
                            .child("location").getValue(ModelLocation.class);

                    String LatLng = location.getLat() + "," + location.getLng();
                    getLocation.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String uri = "http://maps.google.co.in/maps?q=" + LatLng;
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                            startActivity(intent);
                        }
                    });


                    uploadImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            startActivity(new Intent(ServiceDetailActivity.this, ImageListActivity.class)
                                    .putExtra("serviceDetails", serviceDetails)


                            );

                        }
                    });


                    parts.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            startActivity(new Intent(ServiceDetailActivity.this, PartsActivity.class)
                                    .putExtra("serviceDetails", serviceDetails)
                            );

                        }
                    });


                    PaymentConfirmationCardBinding pbind = PaymentConfirmationCardBinding.inflate(getLayoutInflater());

                    Dialog d = new Dialog(ServiceDetailActivity.this);
                    d.setContentView(pbind.getRoot());

                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(d.getWindow().getAttributes());
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    d.getWindow().setAttributes(lp);

                    ArrayAdapter<String> myDapter = new ArrayAdapter<String>(ServiceDetailActivity.this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.payment_type));
                    myDapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    pbind.spinner1.setAdapter(myDapter);


                    String totalPrice = snapshot.child("vehicles").child(serviceDetails.getVehicleID()).child("services")
                            .child(serviceDetails.getServiceID()).child("payment").child("price").getValue(String.class);

                    //pbind.price.setText("Total Price : " + totalPrice);


                    pbind.proceed.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            d.dismiss();
                            progressDialog.show();
                            String[] time_arr = serviceDetails.getTime().split(" ")[0].split(":");
                            FirebaseDatabase.getInstance().getReference("AppManager").child("SlotManager").child("Slots")
                                    .child(sdf.format(Long.valueOf(serviceDetails.getDate()))).child(time_arr[0] + "_" + time_arr[1]).child(serviceDetails.getServiceID()).
                                    child("status").setValue("Done")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                HashMap<String, Object> updated_details = new HashMap<>();
                                                PaymentHelper paymentHelper = new PaymentHelper(pbind.price.getText().toString(), pbind.spinner1.getSelectedItem().toString(), "Done");
                                                updated_details.put("status", "Done");
                                                updated_details.put("payment", paymentHelper);

                                                FirebaseDatabase.getInstance().getReference("Users").child(serviceDetails.getUid()).child("vehicles").child(serviceDetails.getVehicleID())
                                                        .child("services").child(serviceDetails.getServiceID()).updateChildren(updated_details)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    progressDialog.dismiss();
                                                                    Toast.makeText(ServiceDetailActivity.this, "Service marked as done\nCheck Approval List", Toast.LENGTH_LONG).show();
                                                                    Intent intent = new Intent(ServiceDetailActivity.this, DoneListActivity.class);
                                                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //stack clear karne
                                                                    startActivity(intent);
                                                                    finish();
                                                                }

                                                            }
                                                        });

                                            }
                                        }
                                    });

                        }
                    });

                    done.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            d.show();


                        }
                    });


                    customer_phone.getEditText().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + customer_phone.getEditText().getText().toString()));
                            startActivity(intent);
                        }
                    });


                    // Edit vehicle


                    EditVehicleBinding asb = EditVehicleBinding.inflate(getLayoutInflater());
                    Dialog dialog = new Dialog(ServiceDetailActivity.this);
                    dialog.setContentView(asb.getRoot());

                    WindowManager.LayoutParams lp1 = new WindowManager.LayoutParams();
                    lp1.copyFrom(dialog.getWindow().getAttributes());
                    lp1.width = WindowManager.LayoutParams.MATCH_PARENT;
                    lp1.height = WindowManager.LayoutParams.WRAP_CONTENT;

                    dialog.getWindow().setAttributes(lp1);

                    editVh.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.show();

                        }
                    });


                    asb.update.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            String vhNo = asb.vehicleNo.getText().toString();
                            if (vhNo.isEmpty() || vhNo.contains(" ")) {
                                Toast.makeText(ServiceDetailActivity.this, "Please enter valid vehicle no.", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if (vhNo.equals(customer_service_vehicle_no.getEditText().getText().toString())) {
                                Toast.makeText(ServiceDetailActivity.this, "Please enter different vehicle no.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            dialog.dismiss();
                            progressDialog.show();

                            DB_Cust.child("vehicles").child(serviceDetails.getVehicleID()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot vsnap) {

                                    if(vsnap.exists()){
                                        String cdb = vsnap.child("company").getValue(String.class);
                                        String mdb = vsnap.child("model").getValue(String.class);
                                        String vid = cdb + "_" + mdb + "_" + vhNo;
                                        VehicleHelper vehicleHelper = new VehicleHelper(cdb, mdb, vhNo);

                                        DB_Cust.child("vehicles").child(vid).setValue(vehicleHelper).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful()) {

                                                    DB_Cust.child("vehicles").child(vid).child("services").setValue(vsnap.child("services").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {


                                                            if (task.isSuccessful()) {

                                                                DB_Cust.child("vehicles").child(serviceDetails.getVehicleID()).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                        if (task.isSuccessful()) {
                                                                            serviceDetails.setVehicleID(vid);
                                                                            String[] time_arr = serviceDetails.getTime().split(" ")[0].split(":");
                                                                            FirebaseDatabase.getInstance().getReference("AppManager").child("SlotManager").child("Slots")
                                                                                    .child(sdf.format(Long.valueOf(serviceDetails.getDate()))).child(time_arr[0] + "_" + time_arr[1]).child(serviceDetails.getServiceID())
                                                                                    .child("vehicleID").setValue(vid).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()) {

                                                                                        FirebaseDatabase.getInstance().getReference("mechanics").child(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_MECH_ID))
                                                                                                .child("Service_List").child(serviceDetails.getServiceID()).child("vehicleID").setValue(vid).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                if (task.isSuccessful()) {
                                                                                                    customer_service_vehicle_no.getEditText().setText(vhNo);
                                                                                                    progressDialog.dismiss();
                                                                                                } else {
                                                                                                    Toast.makeText(ServiceDetailActivity.this, "Some error occurred", Toast.LENGTH_SHORT).show();
                                                                                                    progressDialog.dismiss();
                                                                                                }
                                                                                            }
                                                                                        });


                                                                                    } else {
                                                                                        Toast.makeText(ServiceDetailActivity.this, "Some Error occurred", Toast.LENGTH_SHORT).show();
                                                                                        progressDialog.dismiss();
                                                                                    }
                                                                                }
                                                                            });
                                                                        } else {
                                                                            Toast.makeText(ServiceDetailActivity.this, "Some Error occurred", Toast.LENGTH_SHORT).show();
                                                                            progressDialog.dismiss();
                                                                        }


                                                                    }
                                                                });
                                                            } else {
                                                                Toast.makeText(ServiceDetailActivity.this, "Some Error occurred", Toast.LENGTH_SHORT).show();
                                                                progressDialog.dismiss();
                                                            }


                                                        }
                                                    });

                                                } else {
                                                    Toast.makeText(ServiceDetailActivity.this, "Some Error occurred", Toast.LENGTH_SHORT).show();
                                                    progressDialog.dismiss();
                                                }

                                            }
                                        });

                                    }else{
                                        progressDialog.dismiss();
                                    }



                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                        }
                    });

                    ServiceDetailActivity.this.progressDialog.dismiss();
                }else{
                    progressDialog.dismiss();
                }


            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }


        });


    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ServiceDetailActivity.this, ServiceListActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)

        );
        finish();
    }
}