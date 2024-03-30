package com.example.bloodfinder.Activities.Hospital;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bloodfinder.Model.HospitalModel;
import com.example.bloodfinder.Model.UserModel;
import com.example.bloodfinder.R;

import java.util.ArrayList;

public class VerifiedAdapter extends RecyclerView.Adapter<VerifiedAdapter.ViewHolder> {

    ArrayList<UserModel> arrayList;
    Context context;
    LinearLayout editUser, callUser, deleteUser;



    public VerifiedAdapter(ArrayList<UserModel> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custome_layout_verified_users,parent,false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        UserModel uModel = arrayList.get(position);
        holder.name.setText(uModel.getName());
        holder.email.setText(uModel.getEmail());
        holder.phone.setText(uModel.getPhone());
        holder.city.setText(uModel.getCity());
        holder.blood.setText(uModel.getBloodGroup());
        holder.verified.setText(uModel.getVerified());

        makeCall(uModel.getPhone());

    }
    private void makeCall(final String number) {
        callUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + number));
                context.startActivity(intent);
            }
        });
    }
    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void searchUsers (ArrayList<UserModel> usersList){
        arrayList = usersList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, email, phone, city, verified , blood;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.uName);
            email = itemView.findViewById(R.id.uEmail);
            phone = itemView.findViewById(R.id.uPhone);
            city = itemView.findViewById(R.id.uCity);
            blood = itemView.findViewById(R.id.uBloodType);
            verified = itemView.findViewById(R.id.uVerify);
            callUser = itemView.findViewById(R.id.makeHCall);
        }
    }

}
