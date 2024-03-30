package com.example.bloodfinder.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.bloodfinder.Activities.Admin.AddHospital;
import com.example.bloodfinder.Activities.Admin.AdminDashboard;
import com.example.bloodfinder.Activities.Admin.AdminProfile;
import com.example.bloodfinder.Activities.Admin.AdminSendAlert;
import com.example.bloodfinder.Activities.Admin.AdminSignUp;
import com.example.bloodfinder.Activities.AvailableBlood;
import com.example.bloodfinder.Activities.BloodRequests;
import com.example.bloodfinder.Activities.DonationPost;
import com.example.bloodfinder.Activities.Hospital.HospitalDashboard;
import com.example.bloodfinder.Activities.Hospital.SendAlert;
import com.example.bloodfinder.Activities.Hospital.ShowUsers;
import com.example.bloodfinder.Activities.Hospital.ShowVerifiedUsers;
import com.example.bloodfinder.Activities.Reminder;
import com.example.bloodfinder.R;
import com.example.bloodfinder.Utils.FirebaseOffline;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Locale;

public class HospitalHomeFragment extends Fragment {
    private static FusedLocationProviderClient fusedLocationProviderClient;
    CardView cardViewUsers, cardViewVerifiedUsers, addHospitalMap, cardViewHospitalSendHAlert;
    TextView tvBloodRequests, tvDonationInterest;
    DatabaseReference refrence, interestRef;
    String city;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.hospital_fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       // FirebaseOffline.getSync();
        getLocaiton(getContext());
        initViews(view);

        //hospital dashboard

        cardViewUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ShowUsers.class));
            }
        });
        cardViewVerifiedUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ShowVerifiedUsers.class));
            }
        });
//        addHospitalMap.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getContext(), AddHospital.class));
//

//
//
//            }
//        });
        cardViewHospitalSendHAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), SendAlert.class));
            }
        });
    }


    private void initViews(View view) {
        cardViewUsers = view.findViewById(R.id.cardViewUsers);
        cardViewVerifiedUsers = view.findViewById(R.id.cardViewVerifiedUsers);
//        addHospitalMap = view.findViewById(R.id.addHospitalMap);
        tvBloodRequests = view.findViewById(R.id.tvpeopleRequests);
        tvDonationInterest = view.findViewById(R.id.tvDonationInterest);
        cardViewHospitalSendHAlert = view.findViewById(R.id.cardViewHospitalSendHAlert);
        refrence = FirebaseDatabase.getInstance().getReference("Blood Requests");
        interestRef = FirebaseDatabase.getInstance().getReference("Donations");
    }

    @SuppressLint("MissingPermission")
    public void getLocaiton(final Context context) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {

                    try {
                        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        String _addres = addresses.get(0).getAddressLine(0);
                        city = addresses.get(0).getLocality();
                        getReqeustCount(city);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void getReqeustCount(String city) {
        Query query = refrence.orderByChild("city").equalTo(city);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    long count = snapshot.getChildrenCount();
                    tvBloodRequests.setText(String.valueOf(count));
                } else {
                    tvBloodRequests.setText("-");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Query donQuery = interestRef.orderByChild("city").equalTo(city);
        donQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    long count = snapshot.getChildrenCount();
                    tvDonationInterest.setText(String.valueOf(count));
                } else {
                    tvDonationInterest.setText("-");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}