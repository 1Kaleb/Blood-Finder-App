package com.example.bloodfinder.Activities.Admin;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bloodfinder.Activities.MainActivity;
import com.example.bloodfinder.Model.DonationModel;
import com.example.bloodfinder.Model.HospitalModel;
import com.example.bloodfinder.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    ArrayList<HospitalModel> arrayList;
    Context context;
    LinearLayout callHospital, deleteHospital, editHospital;
    DatabaseReference dbr, dbref;
    Button adminUpdateBtn;
    //    EditText edtAdminCity, edtAdminName, edtAdminPhone;
    TextInputLayout edtAdminCity, edtAdminName, edtAdminPhone;

    String hName, hCity, hPhone;

    public Adapter(ArrayList<HospitalModel> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custome_layout_hospital, parent, false);
        return new ViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        HospitalModel hModel = arrayList.get(position);
        holder.name.setText(hModel.getName());
        holder.email.setText(hModel.getEmail());
        holder.phone.setText(hModel.getPhoneNumber());
        holder.city.setText(hModel.getCity());

        makeCall(hModel.getPhoneNumber());
        deleteHosp(hModel.getEmail());
        editHosp(hModel.getEmail());

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void searchHospital(ArrayList<HospitalModel> hospitalList) {
        arrayList = hospitalList;
        notifyDataSetChanged();
    }


    private void makeCall(final String number) {
        callHospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + number));
                context.startActivity(intent);
            }
        });
    }

    private void editHosp(final String edit) {
        editHospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference usersRef = database.getReference("Admin-Hospital");

                // Find the user's UID using their email

                usersRef.orderByChild("email").equalTo(edit).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            String uid = userSnapshot.getKey();


                            // Delete the user from the Realtime Database

                            //Todo edit hospital

//                            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
//                            View dialogView = getLayoutInflater().inflate(R.layout.custome_edit_hospital, null);
//
//                            EditText emailBox = dialogView.findViewById(R.id.emailBox);
//                            builder.setView(dialogView);
//                            AlertDialog dialog = builder.create();
//
//                            dialogView.findViewById(R.id.btnReset).setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//
//                                    String userEmail = emailBox.getText().toString();
//                                    if (TextUtils.isEmpty(userEmail) && !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
//                                        Toast.makeText(builder.getContext(), "Enter your registered email id", Toast.LENGTH_SHORT).show();
//                                        return;
//                                    }
//
//
//                                }
//                            });
//
//                            if (dialog.getWindow() != null) {
//                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
//                            }
//                            dialog.show();


                            // Create an AlertDialog.Builder object
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);

// Inflate the layout
                            View view = LayoutInflater.from(context).inflate(R.layout.custome_edit_hospital, null);

// Set the view for the dialog
                            builder.setView(view);

// Create the dialog
                            AlertDialog dialog = builder.create();

// Show the dialog
                            dialog.show();

                            edtAdminName = dialog.findViewById(R.id.edtUpdateAdminName);
                            edtAdminCity = dialog.findViewById(R.id.edtUpdateAdminCity);
                            edtAdminPhone = dialog.findViewById(R.id.edtUpdateAdminPhone);

                            dbref.child("Admin-Hospital").child(uid).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.hasChildren()) {
                                        hName = snapshot.child("name").getValue(String.class);
                                        hCity = snapshot.child("city").getValue(String.class);
                                        hPhone = snapshot.child("phoneNumber").getValue(String.class);

                                        //todo fix update
                                        edtAdminName.getEditText().setText(hName);
                                        edtAdminCity.getEditText().setText(hCity);
                                        edtAdminPhone.getEditText().setText(hPhone);


//                                        if (edtAdminPhone != null) {
//                                            edtAdminPhone.getEditText().setText(hPhone);
//                                        }
//                                        if (edtAdminCity != null) {
//                                            edtAdminCity.getEditText().setText(hCity);
//                                        }
//                                        if (edtAdminName != null) {
//                                            edtAdminName.getEditText().setText(hName);
//                                        }

//                                        Toast.makeText(context, "hi  "+hPhone, Toast.LENGTH_SHORT).show();


                                        dialog.findViewById(R.id.adminUpdateBtn).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {


                                                //progressDialog.show();
                                                final ProgressDialog mDialog = new ProgressDialog(context);
                                                mDialog.setMessage("please wait...");

                                                mDialog.show();

                                                Map<String, Object> map = new HashMap<>();
                                                map.put("name", edtAdminName.getEditText().getText().toString());
                                                map.put("city", edtAdminCity.getEditText().getText().toString());
                                                map.put("phoneNumber", edtAdminPhone.getEditText().getText().toString());

                                                FirebaseDatabase.getInstance().getReference().child("Admin-Hospital").child(uid).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        mDialog.dismiss();
                                                        dialog.dismiss();
                                                        Toast.makeText(context, "Successful! Profile successfully Updated", Toast.LENGTH_SHORT).show();

                                                    }
                                                });

                                                Toast.makeText(context, "Successful! Profile successfully Updated", Toast.LENGTH_SHORT).show();

                                            }
                                        });


                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


//                            Toast.makeText(context, "Successful! Hospital updated", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(context, "Error finding user", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }


    private void deleteHosp(final String em) {
        deleteHospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//todo delete hospital check to delete user from hospital


                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference usersRef = database.getReference("Admin-Hospital");

                // Find the user's UID using their email

                usersRef.orderByChild("email").equalTo(em).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            String uid = userSnapshot.getKey();

                            // Delete the user from the Realtime Database
                            usersRef.child(uid).removeValue();

                            Toast.makeText(context, "Successful! Delete Hospital", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(context, "Error finding user", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, email, phone, city;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.hName);
            email = itemView.findViewById(R.id.hEmail);
            phone = itemView.findViewById(R.id.hPhone);
            city = itemView.findViewById(R.id.hCity);
            callHospital = itemView.findViewById(R.id.makeHCall);
            deleteHospital = itemView.findViewById(R.id.deleteHospital);
            editHospital = itemView.findViewById(R.id.editHospital);
            adminUpdateBtn = itemView.findViewById(R.id.adminUpdateBtn);
//            edtAdminName = itemView.findViewById(R.id.edtUpdateAdminName);
//            edtAdminCity = itemView.findViewById(R.id.edtUpdateAdminCity);
//            edtAdminPhone = itemView.findViewById(R.id.edtUpdateAdminPhone);
            dbref = FirebaseDatabase.getInstance().getReference();


            dbr = FirebaseDatabase.getInstance().getReference();
            dbr.child("Admin-Hospital").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChildren()) {

                        int role = snapshot.child("usertype").getValue(Integer.class);
                        if (role != 1) {
                            deleteHospital.setVisibility(View.GONE);
                            editHospital.setVisibility(View.GONE);
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
//    public boolean isValid() {
//        if (edtAdminName.getText().toString().isEmpty()) {
//            edtAdminName.setError("Full Name required");
//            return false;
//        } else if (edtAdminCity.getText().toString().isEmpty()) {
//            edtAdminCity.setError("City name required");
//            return false;
//        } else if (edtAdminPhone.getText().toString().isEmpty()) {
//            edtAdminPhone.setError("Phone number required");
//            return false;
//        } else {
//            return true;
//        }
//    }

}
