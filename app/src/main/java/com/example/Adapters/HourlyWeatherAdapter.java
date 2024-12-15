package com.example.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstapp.R;

import java.util.List;

public class HourlyWeatherAdapter extends RecyclerView.Adapter<HourlyWeatherAdapter.HourlyViewHolder> {

    private List<HourlyWeatherDto> hourlyWeatherList;

    public HourlyWeatherAdapter(List<HourlyWeatherDto> hourlyWeatherList) {
        this.hourlyWeatherList = hourlyWeatherList;
    }

    @NonNull
    @Override
    public HourlyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.hourly_item, parent, false);
        return new HourlyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HourlyViewHolder holder, int position) {
        HourlyWeatherDto weather = hourlyWeatherList.get(position);
        holder.hourText.setText(weather.getHour());
        holder.temperatureText.setText(weather.getTemperature());
        holder.weatherIcon.setImageResource(weather.getIconResource());
    }

    @Override
    public int getItemCount() {
        return hourlyWeatherList.size();
    }

    public static class HourlyViewHolder extends RecyclerView.ViewHolder {
        TextView hourText, temperatureText;
        ImageView weatherIcon;

        public HourlyViewHolder(@NonNull View itemView) {
            super(itemView);
            hourText = itemView.findViewById(R.id.text_hour);
            temperatureText = itemView.findViewById(R.id.text_degree);
            weatherIcon = itemView.findViewById(R.id.hourly_icon);
        }
    }
}
