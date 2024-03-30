package com.example.bloodfinder.Activities.Hospital;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.bloodfinder.Activities.Hospital.HospitalProfileUpdate;
import com.example.bloodfinder.Model.MyfavModel;
import com.example.bloodfinder.R;
import com.example.bloodfinder.Utils.FirebaseOffline;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class HospitalProfile extends AppCompatActivity {
    Toolbar toolbar;
    CircleImageView imageView;
    TextView tvName, tvEmail, tvPhone, tvAddress, tGander, tvBloodGroup, tvUname, myRequestCount, tvmyDonationcount;
    DatabaseReference dbref;
    FirebaseAuth firebaseAuth;
    Dialog progresDialog;
    ImageView imagAddtoFav;
    String uId;
    Button edit_Profile, updateProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_profile);
        // FirebaseOffline.getSync();
        initViews();
        getDetails();
        UpdateProfile();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void getDetails() {
        String adminId = getIntent().getStringExtra("adminId");
        dbref.child(adminId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    progresDialog.dismiss();
                    String name = snapshot.child("name").getValue(String.class);
                    String phone = snapshot.child("phoneNumber").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String address = snapshot.child("city").getValue(String.class);

                    //String imageUrl = snapshot.child("imageUrl").getValue(String.class);
                    //  uId = snapshot.child("uId").getValue(String.class);
//                    if (firebaseAuth.getCurrentUser().getUid().equals(uId)) {
//                        imagAddtoFav.setVisibility(View.GONE);
//                    }
                    tvUname.setText(name);
                    tvName.setText(name);
                    tvAddress.setText(address);
                    tvEmail.setText(email);
                    tvPhone.setText(phone);
                    //Picasso.get().load(imageUrl).into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbarProfile);
        tvName = findViewById(R.id.tvprofilenameAdmin);
        tvEmail = findViewById(R.id.tvEmailDetail);
        tvAddress = findViewById(R.id.tvAdminAddres);
        tvPhone = findViewById(R.id.tvContact);
        // imageView = findViewById(R.id.adminProfile);
        tvUname = findViewById(R.id.adminprofileName);
        updateProfile = findViewById(R.id.edit_AdminProfile);
        dbref = FirebaseDatabase.getInstance().getReference("Admin-Hospital");
        firebaseAuth = FirebaseAuth.getInstance();
        progresDialog = new Dialog(this);
        View mView = getLayoutInflater().inflate(R.layout.progress_dialog_wait, null);
        progresDialog.setContentView(mView);
        progresDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //progresDialog.show();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(HospitalProfile.this, HospitalDashboard.class));

    }



    public void UpdateProfile() {
        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HospitalProfile.this, HospitalProfileUpdate.class);
                intent.putExtra("adminId", FirebaseAuth.getInstance().getCurrentUser().getUid());
                startActivity(intent);
            }
        });
    }
}