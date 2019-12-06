package com.example.cwpar.mymp3playerproject2;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;

class MyDataAdapter extends RecyclerView.Adapter<MyDataAdapter.CustomViewHolder> {

    private int layout;
    private ArrayList<MyDataDAO> list;
    private  int currentPosition;
    private int lastCheckedPosition = -1;
    private RadioButton selectedRadioButton;


    public MyDataAdapter(int layout, ArrayList<MyDataDAO> list) {
        this.layout = layout;
        this.list = list;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public int getLastCheckedPosition() {
        return lastCheckedPosition;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(layout, viewGroup, false);
        CustomViewHolder holder = new CustomViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder customViewHolder, final int position) {
        currentPosition = position;
        customViewHolder.tvSinger.setText(list.get(currentPosition).getSinger());
        customViewHolder.tvTitle.setText(list.get(currentPosition).getTitle());
        customViewHolder.tvJanre.setText(list.get(currentPosition).getJanre());
        customViewHolder.tvRate.setText(String.valueOf(list.get(currentPosition).getRate()));
        customViewHolder.radioButton.setChecked(lastCheckedPosition == position);
        customViewHolder.itemView.setTag(currentPosition);

    }

    @Override
    public int getItemCount() {
        return (list != null) ? list.size() : 0;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView tvSinger, tvTitle, tvJanre, tvRate;
        RadioButton radioButton;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSinger = itemView.findViewById(R.id.tvSinger);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvJanre = itemView.findViewById(R.id.tvJanre);
            tvRate = itemView.findViewById(R.id.tvRate);
            radioButton = itemView.findViewById(R.id.radioButton);

            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lastCheckedPosition = getAdapterPosition();

                    notifyDataSetChanged();
                }
            });
        }

    }
}
