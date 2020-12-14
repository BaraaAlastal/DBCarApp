package com.example.dbcarapp;

import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CarRecyclerViewAdapter extends RecyclerView.Adapter<CarRecyclerViewAdapter.carViewHolder> {
    public ArrayList<Car> getCars() {
        return cars;
    }

    public void setCars(ArrayList<Car> cars) {
        this.cars = cars;
    }

    private ArrayList<Car>cars;
    private onRecyclerViewItemClickListener listener;
    public CarRecyclerViewAdapter(ArrayList<Car>cars,onRecyclerViewItemClickListener lisetner){
        this.cars=cars;
        this.listener=lisetner;
    }
    @NonNull
    @Override
    public carViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_car_layout,null,false);
        carViewHolder viewHolder=new carViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull carViewHolder holder, int position) {
       Car c=cars.get(position);
       if(c.getImage()!=null && !c.getImage().isEmpty())
           holder.iv.setImageURI(Uri.parse(c.getImage()));
       else
           holder.iv.setImageResource(R.drawable.city);
       holder.tv_model.setText(c.getModel());
       holder.tv_color.setText(c.getColor());
       try {
           holder.tv_color.setTextColor(Color.parseColor(c.getColor()));
       }
       catch (Exception e){

       }
       holder.tv_dpl.setText(String.valueOf(c.getDpl()));
       //we use setTag to store hidden object
        //we make this line to use it in onClick method below to get id for each car.
       holder.id=c.getId();
    }

    @Override
    public int getItemCount() {
        return cars.size();
    }

    class carViewHolder extends RecyclerView.ViewHolder{
        ImageView iv;
        TextView tv_model,tv_color,tv_dpl;
        public int id;
        public carViewHolder(@NonNull View itemView) {
            super(itemView);
            iv=itemView.findViewById(R.id.custom_card_iv);
            tv_model=itemView.findViewById(R.id.custom_car_tv_model);
            tv_color=itemView.findViewById(R.id.custom_car_tv_color);
            tv_dpl=itemView.findViewById(R.id.custom_car_tv_dpl);
            //we create special listener Because we can't take card view to main activity
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(id);
                }
            });
        }
    }
}
