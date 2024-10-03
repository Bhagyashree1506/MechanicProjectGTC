package com.geartocare.mechanicP.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.geartocare.mechanicP.Activities.ServiceDetailActivity;
import com.geartocare.mechanicP.Helpers.CustomProgressDialog;
import com.geartocare.mechanicP.Helpers.PaymentHelper;
import com.geartocare.mechanicP.Models.ModelService;
import com.geartocare.mechanicP.R;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class ServiceListAdapter extends RecyclerView.Adapter<ServiceListAdapter.MyViewHolder> {
    ArrayList<ModelService> list;
    Context context;
    SimpleDateFormat sdf;
    CustomProgressDialog progressDialog;
    SimpleDateFormat SlashSdf;


    public ServiceListAdapter(ArrayList<ModelService> list, Context context) {
        this.list = list;
        this.context = context;
        sdf = new SimpleDateFormat("dd_MM_yyyy");
        SlashSdf = new SimpleDateFormat("dd/MM/yyyy");
        progressDialog = new CustomProgressDialog(context);
    }


    @NonNull
    @NotNull
    @Override
    public ServiceListAdapter.MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_mechanic_service, parent, false);
        return new ServiceListAdapter.MyViewHolder(v);
    }

    @Override

    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        ModelService m = list.get(position);
        Long dt = Long.valueOf(m.getDate());
        holder.date.setText(SlashSdf.format(dt));
        holder.time.setText(m.getTime());


        holder.name.setText(m.getName());

        if (m.getStatus().equals("Done")) {
            holder.service_details_btn.setVisibility(View.GONE);

            holder.modify_btn.setVisibility(View.VISIBLE);
            holder.modify_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.show();
                    String[] time_arr = m.getTime().split(" ")[0].split(":");
                    FirebaseDatabase.getInstance().getReference("AppManager").child("SlotManager").child("Slots")
                            .child(sdf.format(Long.valueOf(m.getDate()))).child(time_arr[0]+"_"+time_arr[1]).child(m.getServiceID()).
                            child("status").setValue("Assigned").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                HashMap<String,Object> updated_details = new HashMap<>();
                                PaymentHelper paymentHelper = new PaymentHelper(m.getPrice(),"payAfterService","On_Hold");
                                updated_details.put("status","Assigned");
                                updated_details.put("payment",paymentHelper);

                                FirebaseDatabase.getInstance().getReference("Users").child(m.getUid()).child("vehicles").child(m.getVehicleID()).child("services")
                                        .child(m.getServiceID()).updateChildren(updated_details).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            list.remove(position);
                                            notifyDataSetChanged();
                                            progressDialog.dismiss();
                                            Toast.makeText(context, "Service is marked as not done\nCheck Service List", Toast.LENGTH_SHORT).show();


                                        } else {
                                            Toast.makeText(context, "Some error occurred", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();

                                        }
                                    }
                                });

                            } else {
                                Toast.makeText(context, "Some error occurred", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();

                            }

                        }
                    });

                }
            });
        } else {


            holder.service_details_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ServiceDetailActivity.class);
                    intent.putExtra("serviceDetails", m);

                    context.startActivity(intent);

                }
            });
        }


    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView date, time, name;
        Button service_details_btn, modify_btn;


        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.serviceCard_date);
            time = itemView.findViewById(R.id.serviceCard_time);

            modify_btn = itemView.findViewById(R.id.modify_btn);


            name = itemView.findViewById(R.id.serviceCard_custName);
            service_details_btn = itemView.findViewById(R.id.service_detail_btn);
        }


    }
}
