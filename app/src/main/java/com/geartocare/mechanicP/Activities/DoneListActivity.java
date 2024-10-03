package com.geartocare.mechanicP.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.geartocare.mechanicP.Helpers.CustomProgressDialog;
import com.geartocare.mechanicP.Models.ModelAssignService;
import com.geartocare.mechanicP.Models.ModelService;
import com.geartocare.mechanicP.Models.ModelSv;
import com.geartocare.mechanicP.R;
import com.geartocare.mechanicP.Adapters.ServiceListAdapter;
import com.geartocare.mechanicP.SessionManager;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class DoneListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DatabaseReference DBref,BoH,Mechanics;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    SessionManager sessionManager;
    RecyclerView recyclerView;
    ServiceListAdapter adapter;
    SwipeRefreshLayout srl;
    ArrayList<ModelAssignService> slotList;
    ArrayList<ModelService> service_list;
    ArrayList<ModelSv> mechList;
    CustomProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_done_list);

        getWindow().setStatusBarColor(ContextCompat.getColor(DoneListActivity.this,R.color.card_black));

        progressDialog = new CustomProgressDialog(DoneListActivity.this);



        /*------------------------------Hooks start---------------------------------------*/
        srl = findViewById(R.id.swiperefresh);
        drawerLayout = findViewById(R.id.drawer_layout_ninja);
        navigationView = findViewById(R.id.nav_view_ninja);
        toolbar = findViewById(R.id.toolbar_ninja);
        recyclerView = findViewById(R.id.rv_service_list);





        /*------------------------------Hooks end---------------------------------------*/






        /*------------------------------Variables---------------------------------------*/
        DBref = FirebaseDatabase.getInstance().getReference("AppManager").child("SlotManager").child("Slots");
        BoH = FirebaseDatabase.getInstance().getReference("Bookings_on_hold");
        Mechanics = FirebaseDatabase.getInstance().getReference("mechanics");
        sessionManager = new SessionManager(DoneListActivity.this);
        service_list = new ArrayList<>();
        mechList = new ArrayList<>();
        slotList = new ArrayList<>();
        adapter = new ServiceListAdapter(service_list, DoneListActivity.this);




        /*------------------------------Variables end---------------------------------------*/




        /*------------------------------Navigation Part Starts---------------------------------------*/
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");
        Menu menu = navigationView.getMenu();
        menu.findItem(R.id.nav_login).setVisible(false);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(DoneListActivity.this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(DoneListActivity.this);
        navigationView.setCheckedItem(R.id.done_list);
        navigationView.bringToFront();
        navigationView.requestLayout();

        /*------------------------------Navigation Part Ends---------------------------------------*/

        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(DoneListActivity.this));


        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                makeList();
                srl.setRefreshing(false);
            }
        });

        makeList();
    }


    private void makeList() {
        progressDialog.show();
        mechList.clear();
        slotList.clear();
        service_list.clear();

        Mechanics.child(sessionManager.getUsersDetailsFromSessions().get(sessionManager.KEY_MECH_ID)).child("Service_List").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot bookings) {
                if (bookings.exists()) {
                    findViewById(R.id.emp).setVisibility(View.GONE);


                    for (DataSnapshot singleService : bookings.getChildren()) {

                        mechList.add(singleService.getValue(ModelSv.class));

                    }

                    SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy");

                    for (ModelSv sv : mechList) {


                        DBref.child(sdf.format(Long.valueOf(sv.getDate()))).child(sv.getTime()).child(sv.getServiceID()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot service) {


                                if (service.exists()) {

                                    ModelAssignService mas = service.getValue(ModelAssignService.class);

                                    if(mas.getStatus().equals("Done")){
                                        FirebaseDatabase.getInstance().getReference("Users").child(mas.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot userSnap) {
                                                if (userSnap.exists()) {
                                                    DataSnapshot svSnap = userSnap.child("vehicles").child(mas.getVehicleID()).child("services").child(mas.getServiceID());
                                                    if (svSnap.exists()) {
                                                        ModelService m = new ModelService();
                                                        m.setServiceID(mas.getServiceID());
                                                        m.setVehicleID(mas.getVehicleID());
                                                        m.setName(userSnap.child("fullName").getValue(String.class));
                                                        m.setPhone(svSnap.child("phone").getValue(String.class));
                                                        m.setDate(svSnap.child("date").getValue(String.class));
                                                        m.setTime(svSnap.child("time").getValue(String.class));
                                                        m.setPrice(svSnap.child("payment").child("price").getValue(String.class));
                                                        m.setUid(mas.getUid());
                                                        m.setStatus(mas.getStatus());

                                                        service_list.add(m);
                                                        adapter.notifyDataSetChanged();

                                                    }
                                                }


                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                    }


                                }


                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                            }
                        });


                    }


                    progressDialog.dismiss();

                } else {
                    progressDialog.dismiss();
                    findViewById(R.id.emp).setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }







    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else
        {
            Intent intent = new Intent(DoneListActivity.this, ServiceListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NotNull MenuItem item) {

        switch (item.getItemId()){



            case R.id.nav_ninja:
                Intent intent = new Intent(DoneListActivity.this, ServiceListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                break;




            case R.id.nav_profile:
                startActivity(new Intent(DoneListActivity.this,UserProfileActivity.class));
                finish();


                break;

            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                sessionManager.logoutSession();
                startActivity(new Intent(DoneListActivity.this,LoginActivity.class));
                finish();
                break;

            case R.id.done_list:
                break;




        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;

    }
}