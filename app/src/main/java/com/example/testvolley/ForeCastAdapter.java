package com.example.testvolley;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ForeCastAdapter extends RecyclerView.Adapter<ForeCastAdapter.ViewHolder> {

    Context context;
    List<ForeCastModel> list;

    public ForeCastAdapter(Context context, List<ForeCastModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.forecast,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ForeCastModel foreCastModel = list.get(position);

        holder.textDate.setText(foreCastModel.getDate());
        holder.textTemp.setText(foreCastModel.getAvgtemp_c());
        holder.textCon.setText(foreCastModel.getText());

        String imUrl = foreCastModel.getIcon();
        Picasso.get().load(imUrl).into(holder.cardIV);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textDate,textTemp,textCon;
        ImageView cardIV;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textDate = itemView.findViewById(R.id.cardtxtDate);
            textTemp = itemView.findViewById(R.id.cardTemp);
            textCon = itemView.findViewById(R.id.cardCondition);
            cardIV = itemView.findViewById(R.id.cardCloudIV);
        }
    }
}
