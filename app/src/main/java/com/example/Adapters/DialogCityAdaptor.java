package com.example.Adapters;

import com.example.firstapp.R;
import com.example.weather.data.CityModel;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DialogCityAdaptor extends RecyclerView.Adapter<DialogCityAdaptor.DialogCityViewHolder> {

    private List<CityModel> cityList;
    private OnCityClickListener listener;

    // Constructor
    public DialogCityAdaptor(List<CityModel> cityList, OnCityClickListener listener) {
        this.cityList = cityList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DialogCityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_city_item, parent, false);
        return new DialogCityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DialogCityViewHolder holder, int position) {
        CityModel city = cityList.get(position);
        holder.bind(city, listener);
    }

    @Override
    public int getItemCount() {
        return cityList.size();
    }

    public static class DialogCityViewHolder extends RecyclerView.ViewHolder {

        private TextView cityName;

        public DialogCityViewHolder(@NonNull View itemView) {
            super(itemView);
            cityName = itemView.findViewById(R.id.dialog_city_item_name);
        }

        public void bind(CityModel city, OnCityClickListener listener) {
            cityName.setText(city.getName());
            itemView.setOnClickListener(v -> listener.onCityClick(city));
        }
    }

    public interface OnCityClickListener {
        void onCityClick(CityModel city);
    }
}

