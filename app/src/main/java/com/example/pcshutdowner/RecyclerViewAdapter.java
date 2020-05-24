package com.example.pcshutdowner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private ArrayList<String> mIpAddresses ;
    private ArrayList<String> mComputerNames;
    private MainActivity mActivity;
    public RecyclerViewAdapter(ArrayList<String> ipAddresses, ArrayList<String> computerNames, MainActivity activity){
        mIpAddresses = ipAddresses;
        mComputerNames = computerNames;
        mActivity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem,parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.parentLayout.setOnClickListener(v -> {
            mActivity.connect(mIpAddresses.get(position));
        });
        holder.ipText.setText(mIpAddresses.get(position));
        holder.computerNameText.setText(mComputerNames.get(position));

    }

    @Override
    public int getItemCount() {
        return mIpAddresses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView ipText;
        TextView computerNameText;
        ImageView image;
        RelativeLayout parentLayout;

        public ViewHolder(View itemView){
            super(itemView);
            image =  itemView.findViewById(R.id.computerImage);
            ipText = itemView.findViewById(R.id.ipAddressText);
            computerNameText = itemView.findViewById(R.id.computerNameText);
            parentLayout = itemView.findViewById(R.id.parent_layout);

        }
    }
}
