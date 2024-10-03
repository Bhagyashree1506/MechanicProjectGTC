package com.geartocare.mechanicP.Adapters;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.geartocare.mechanicP.Activities.ImageViewerActivity;
import com.geartocare.mechanicP.Models.Model;
import com.geartocare.mechanicP.R;

import java.util.ArrayList;

public class ServiceImageListAdapter extends RecyclerView.Adapter<ServiceImageListAdapter.MyViewHolder>{

    private ArrayList<Model> mList;
    private Context context;

    public ServiceImageListAdapter(Context context , ArrayList<Model> mList){

        this.context = context;
        this.mList = mList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_image , parent ,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(mList.get(position).getImageUrl()).placeholder(R.drawable.cam_mecha).into(holder.imageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ImageViewerActivity.class).putExtra("imageUrl",mList.get(position).getImageUrl()));
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.img);
        }
    }
}
