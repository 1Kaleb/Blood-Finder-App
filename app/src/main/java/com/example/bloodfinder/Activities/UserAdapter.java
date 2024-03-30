package com.example.bloodfinder.Activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bloodfinder.Model.DonationModel;
import com.example.bloodfinder.R;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    ArrayList<DonationModel> arrayList;
    Context context;
    DatabaseReference dbr;

    public UserAdapter(ArrayList<DonationModel> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custome_available_blood,parent,false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        DonationModel uModel = arrayList.get(position);
        holder.tvName.setText(uModel.getName());
        holder.tvAddress.setText(uModel.getCity());
        holder.tvBloodGroup.setText(uModel.getBloodGoup());

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void searchUser (ArrayList<DonationModel> uList){
        arrayList = uList;
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvAddress,tvBloodGroup,tvName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.uNmaeAvaillableBlood);
            tvBloodGroup = itemView.findViewById(R.id.availableBloodgroup);
            tvAddress = itemView.findViewById(R.id.availableBloodAddress);

        }
    }

}

