package com.geartocare.mechanicP.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.geartocare.mechanicP.Models.ModelPart;
import com.geartocare.mechanicP.Models.ModelPartSv;
import com.geartocare.mechanicP.R;
import com.geartocare.mechanicP.databinding.CardPartBinding;

import java.util.ArrayList;
import java.util.HashMap;

public class PartAdapter extends RecyclerView.Adapter<PartAdapter.MyViewHolder> {
    Context context;
    ArrayList<ModelPartSv> mlist;
    HashMap<String, Object> updated_parts,sv_up_parts;

    public HashMap<String, Object> getSv_up_parts() {
        return sv_up_parts;
    }

    public void setSv_up_parts(HashMap<String, Object> sv_up_parts) {
        this.sv_up_parts = sv_up_parts;
    }

    public HashMap<String, Object> getUpdated_parts() {
        return updated_parts;
    }

    public void setUpdated_parts(HashMap<String, Object> updated_parts) {
        this.updated_parts = updated_parts;
    }

    public PartAdapter(Context context, ArrayList<ModelPartSv> mlist) {
        this.context = context;
        this.mlist = mlist;
        updated_parts = new HashMap<>();
        sv_up_parts = new HashMap<>();
    }

    public ArrayList<ModelPartSv> getMlist() {
        return mlist;
    }

    public void setMlist(ArrayList<ModelPartSv> mlist) {
        this.mlist = mlist;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PartAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.card_part, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ModelPartSv recordSv = mlist.get(position);
        ModelPart record = recordSv.getModelPart();
        String used_orignal = record.getUsed();
        String oc_orignal = record.getOriginalCount();

        holder.binding.partName.setText(record.getPartID());
        holder.binding.available.setText(record.getAvailable());
        holder.binding.used.setText(record.getUsed());
        holder.binding.use.setText(recordSv.getUse());
        holder.binding.originalCount.setText(record.getOriginalCount());



        holder.binding.increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer u = Integer.valueOf(holder.binding.use.getText().toString()) + 1;
                Integer us = Integer.valueOf(holder.binding.used.getText().toString()) + 1;
                Integer a = Integer.valueOf(holder.binding.available.getText().toString()) - 1;


                if(us<=Integer.valueOf(oc_orignal)){

                    mlist.get(position).getModelPart().setAvailable(a.toString());
                    mlist.get(position).getModelPart().setUsed(us.toString());
                    holder.binding.available.setText(a.toString());
                    holder.binding.used.setText(String.valueOf(us));
                    holder.binding.use.setText(String.valueOf(u));
                    updated_parts.put(record.getPartID(), mlist.get(position).getModelPart());
                    if(u==0){
                        sv_up_parts.put(record.getPartID(),null);
                    }else{
                        sv_up_parts.put(record.getPartID(),u.toString());
                    }
                }

            }
        });

        holder.binding.decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Integer u = Integer.valueOf(holder.binding.use.getText().toString()) - 1;
                Integer us = Integer.valueOf(holder.binding.used.getText().toString()) - 1;
                Integer a = Integer.valueOf(holder.binding.available.getText().toString()) + 1;



                if(u>=0){
                    holder.binding.use.setText(String.valueOf(u));
                    holder.binding.available.setText(a.toString());

                    mlist.get(position).getModelPart().setAvailable(a.toString());
                    mlist.get(position).getModelPart().setUsed(us.toString());

                    holder.binding.used.setText(String.valueOf(us));

                    updated_parts.put(record.getPartID(), mlist.get(position).getModelPart());
                    if(u==0){
                        sv_up_parts.put(record.getPartID(),null);
                    }else{
                        sv_up_parts.put(record.getPartID(),u.toString());
                    }


                    updated_parts.put(record.getPartID(), mlist.get(position).getModelPart());

                }


            }
        });

    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        CardPartBinding binding;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = CardPartBinding.bind(itemView);
        }
    }
}
