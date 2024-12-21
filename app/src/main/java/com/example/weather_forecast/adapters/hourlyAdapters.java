package com.example.weather_forecast.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.weather_forecast.R;
import com.example.weather_forecast.domains.hourly;

import java.util.ArrayList;

public class hourlyAdapters extends RecyclerView.Adapter<hourlyAdapters.viewHolder>     {
    ArrayList<hourly> items;
    Context context;

    public hourlyAdapters(ArrayList<hourly> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public hourlyAdapters.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_hourly, parent, false);
        context = parent.getContext();
        return new viewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull hourlyAdapters.viewHolder holder, int position) {
        holder.hourTxt.setText(items.get(position).getHour());
        holder.tempTxt.setText(items.get(position).getTemp() + "°");

        Glide.with(context)
                .load(items.get(position).getIconURL()) // URL của ảnh
                .placeholder(R.drawable.circulcar_loading) // Ảnh mặc định khi đang tải
                .error(R.drawable.circulcar_loading) // Ảnh lỗi nếu tải thất bại
                .into(holder.pic);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        TextView hourTxt, tempTxt;
        ImageView pic;
        public viewHolder(@NonNull View itemView) {
            super(itemView);

            hourTxt = itemView.findViewById(R.id.hourTxt);
            tempTxt = itemView.findViewById(R.id.tempTxt);
            pic = itemView.findViewById(R.id.pic);
        }
    }
}
