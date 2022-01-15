package com.example.mainpro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.SimpleTimeZone;

public class WeatherRVAdapter extends RecyclerView.Adapter<WeatherRVAdapter.ViewHolder> {
    private Context context;
    private ArrayList<WeatherRVModel> weatherRVModelArrayList;

    public WeatherRVAdapter(Context context, ArrayList<WeatherRVModel> weatherRVModelArrayList) {
        this.context = context;
        this.weatherRVModelArrayList = weatherRVModelArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
View view= LayoutInflater.from(context).inflate(R.layout.weather_rv_item,parent,false);
return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WeatherRVModel model=weatherRVModelArrayList.get(position);
        holder.tempTv.setText(model.getTemperature()+"\t\t℃");
        Picasso.get().load("http:".concat(model.getIcon())).into(holder.conditionTV);
        holder.windTv.setText(model.getWindspeed()+"km/hr");
        SimpleDateFormat input=new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat output=new SimpleDateFormat("hh:mm aa");
        try
        {
            Date t=input.parse(model.getTime());
            holder.timeTV.setText(output.format(t));

        }catch (ParseException e)

        {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return weatherRVModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView windTv,tempTv,timeTV;
        private ImageView conditionTV;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            windTv=itemView.findViewById(R.id.idtvwindspeed);
            tempTv=itemView.findViewById(R.id.idtvtemperature);
            timeTV=itemView.findViewById(R.id.idtvtime);
            conditionTV=itemView.findViewById(R.id.idtvcondition);
        }
    }
}
