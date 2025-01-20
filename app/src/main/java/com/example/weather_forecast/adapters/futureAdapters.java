package com.example.weather_forecast.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.weather_forecast.R;
import com.example.weather_forecast.domains.future;

import java.util.ArrayList;

public class futureAdapters extends RecyclerView.Adapter<futureAdapters.viewHolder>     {
    ArrayList<future> items;
    Context context;

    public futureAdapters(ArrayList<future> items) {
        this.items = items;
    }



    @NonNull
    @Override
    public futureAdapters.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_future, parent, false);
        context = parent.getContext();
        return new viewHolder(inflate);
    }
    @Override
    public void onBindViewHolder(@NonNull futureAdapters.viewHolder holder, int position) {
        holder.dayTxt.setText(items.get(position).getDay());
        holder.dayMonthTxt.setText(items.get(position).getDayMonth());
        holder.statusTxt.setText(items.get(position).getStatus());
        holder.tempTxt.setText(items.get(position).getTemp()+"°C");

        Glide.with(context)
                .load(items.get(position).getPicPath()) // URL của ảnh
                .placeholder(R.drawable.circulcar_loading) // Ảnh mặc định khi đang tải
                .error(R.drawable.circulcar_loading) // Ảnh lỗi nếu tải thất bại
                .into(holder.pic);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        TextView dayTxt, dayMonthTxt, statusTxt, tempTxt;
        ImageView pic;
        public viewHolder(@NonNull View itemView) {
            super(itemView);

            dayTxt = itemView.findViewById(R.id.dayWeekTxt);
            dayMonthTxt = itemView.findViewById(R.id.dayMonthTxt);
            statusTxt = itemView.findViewById(R.id.statusTxt);
            tempTxt = itemView.findViewById(R.id.tempTxt);
            pic = itemView.findViewById(R.id.pic);
        }
    }
}
