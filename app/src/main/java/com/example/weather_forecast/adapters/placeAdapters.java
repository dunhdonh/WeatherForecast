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

import com.example.weather_forecast.domains.savedPlace;
import com.example.weather_forecast.domains.OnItemClickListener;

import java.util.ArrayList;

public class placeAdapters extends RecyclerView.Adapter<placeAdapters.viewHolder> {
    ArrayList<savedPlace> items;
    Context context;
    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
    public placeAdapters(ArrayList<savedPlace>items){
        this.items = items;
    }

    @NonNull
    @Override
    public placeAdapters.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_place, parent, false);
        context = parent.getContext();
        return new viewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull placeAdapters.viewHolder holder, int position) {
        holder.placeTXT.setText(items.get(position).getPlace());
        holder.tempTXT.setText(items.get(position).getTemp()+"°C");

        Glide.with(context)
                .load(items.get(position).getIcon()) // URL của ảnh
                .placeholder(R.drawable.circulcar_loading) // Ảnh mặc định khi đang tải
                .error(R.drawable.circulcar_loading) // Ảnh lỗi nếu tải thất bại
                .into(holder.pic);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{

        TextView placeTXT, tempTXT;
        ImageView pic;

        public viewHolder (@NonNull View itemView){
            super(itemView);
            placeTXT = itemView.findViewById(R.id.place);
            tempTXT = itemView.findViewById(R.id.temp);

            pic = itemView.findViewById(R.id.pic);
        }


    }
}
