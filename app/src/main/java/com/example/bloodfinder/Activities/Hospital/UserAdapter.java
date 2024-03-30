package com.example.bloodfinder.Activities.Hospital;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bloodfinder.Model.Admin;
import com.example.bloodfinder.Model.HospitalModel;
import com.example.bloodfinder.Model.UserModel;
import com.example.bloodfinder.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    ArrayList<UserModel> arrayList;
    Context context;
    LinearLayout editUser, callUser, verifyUser;
    FirebaseAuth firebaseAuth;
    String uName,hName;
//    Dialog progressDialog;
    //Dialog progressDialog;




    public UserAdapter(ArrayList<UserModel> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custome_layout_users,parent,false);



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
        verifyUsers(uModel.getuId());

    }
    private void verifyUsers(final String id) {
        verifyUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                progressDialog.show();
                DatabaseReference href = FirebaseDatabase.getInstance().getReference("Admin-Hospital").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                href.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()) {
                            hName = dataSnapshot.child("name").getValue(String.class);
//                            Toast.makeText(context, "verified by " + hName, Toast.LENGTH_SHORT).show();
                        }
                        Map<String, Object> map = new HashMap<>();
                                map.put("verified", hName);
                        FirebaseDatabase.getInstance().getReference().child("Users").child(id).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
//                                progressDialog.dismiss();
                                Toast.makeText(context, "Successful! User Verified", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
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

        TextView name, email, phone, city,blood, verified;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.uName);
            email = itemView.findViewById(R.id.uEmail);
            phone = itemView.findViewById(R.id.uPhone);
            city = itemView.findViewById(R.id.uCity);
            blood = itemView.findViewById(R.id.uBloodType);
            verified = itemView.findViewById(R.id.uVerify);
            callUser = itemView.findViewById(R.id.makeHCall);
            verifyUser = itemView.findViewById(R.id.verifyUser);
        }
    }

}
