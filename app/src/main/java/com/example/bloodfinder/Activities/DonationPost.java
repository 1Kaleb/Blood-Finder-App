package com.example.bloodfinder.Activities;

import static java.lang.String.valueOf;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.bloodfinder.Model.DonationModel;
import com.example.bloodfinder.R;
import com.example.bloodfinder.Recievers.NetworkReciever;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DonationPost extends AppCompatActivity implements LocationListener {
    TextInputLayout edtname, edtPhone, edtAddress, edtlname, edtMessage, edtbGroup;
    DatabaseReference dbref;
    FirebaseAuth firebaseAuth;
    String uName, email, phone, address, message, city, bloodGrp, uVerify;
    FusedLocationProviderClient mFuesdLocaitonProvide;
    NetworkReciever networkReciever = new NetworkReciever();
    Dialog progressDialog;
    Spinner spinnerBloodGroup;
    Double lat, lang;
    TextView bloodGroup;
    LinearLayout donatePage, donorLayout;
    Button deletePost, updateLocation;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_post);

        initView();
        getLocation();
        //spinnerListener();
        getUserIfo();


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

//    private void spinnerListener() {
//        spinnerBloodGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                bloodGroup.setText(spinnerBloodGroup.getSelectedItem().toString());
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 5, DonationPost.this);
    }

    private void getUserIfo() {
        dbref.child("Users").child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    uName = snapshot.child("name").getValue(String.class);
                    phone = snapshot.child("phone").getValue(String.class);
                    bloodGrp = snapshot.child("bloodGroup").getValue(String.class);
                    edtname.getEditText().setText(uName);
                    edtPhone.getEditText().setText(phone);
                    edtbGroup.getEditText().setText(bloodGrp);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initView() {
        edtname = findViewById(R.id.edtdonateFname);
        edtPhone = findViewById(R.id.edtDonatePhone);
        edtAddress = findViewById(R.id.edtDonateAddress);
        edtbGroup = findViewById(R.id.edtbGroup);
        edtMessage = findViewById(R.id.edtDonorMessage);
        dbref = FirebaseDatabase.getInstance().getReference();
        // spinnerBloodGroup = findViewById(R.id.donateBloodGroup);
        bloodGroup = findViewById(R.id.tvdonateBloodField);
        deletePost = findViewById(R.id.btnDeleteDonation);
        updateLocation = findViewById(R.id.btnUpdateLocation);
        firebaseAuth = FirebaseAuth.getInstance();
        toolbar = findViewById(R.id.toolbardonaiton);
        donatePage = findViewById(R.id.myDonation);
        donorLayout = findViewById(R.id.donorLayout);
        progressDialog = new Dialog(this);
        View mView = getLayoutInflater().inflate(R.layout.progress_dialog_wait, null);
        progressDialog.setContentView(mView);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //todo progresss dialog
        //progressDialog.show();


        // Only 1 post per donor , delete post, update location

        final DatabaseReference dRef = FirebaseDatabase.getInstance().getReference().child("Donations");
        dRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String donationkey = snapshot.child("userKey").getValue(String.class);
                    String pushKey = snapshot.child("pushKey").getValue(String.class);
//            System.out.println("UserKey in Donations : "+donationkey);
                    if (firebaseAuth.getCurrentUser().getUid().equals(donationkey)) {
                        donatePage.setVisibility(View.GONE);
                        donorLayout.setVisibility(View.VISIBLE);

                        //delete post
                        deletePost.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                FirebaseDatabase.getInstance().getReference().child("Donations").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        dbref.child("Donations").child(pushKey).removeValue();
                                        Snackbar.make(findViewById(R.id.linearLayout5), "Your Donation Post has been deleted!", Snackbar.LENGTH_LONG).show();
                                        refreshActivity();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });

                        // Update post location

                        updateLocation.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                FirebaseDatabase.getInstance().getReference().child("Donations").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        Map<String, Object> map = new HashMap<>();

                                        map.put("address", address);
                                        map.put("city", city);
//                                        map.put("lang", Double.parseDouble(valueOf(lang)));
//                                        map.put("lat", Double.parseDouble(valueOf(lat)));
                                        map.put("lang", valueOf(lang));
                                        map.put("lat", valueOf(lat));

                                        dbref.child("Donations").child(pushKey).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Snackbar.make(findViewById(R.id.linearLayout5), "Your Donation Post location is updated!", Snackbar.LENGTH_LONG).show();

                                                }
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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public void refreshActivity() {
        startActivity(getIntent());
        finish();
    }

    public void PostDonation(View view) {
        final ProgressDialog mDialog = new ProgressDialog(DonationPost.this);
        mDialog.setMessage("please wait...");
        mDialog.show();
        dbref.child("Users").child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //check if user is verified to be a donor
                if (snapshot.hasChildren()) {
                    uVerify = snapshot.child("verified").getValue(String.class);

                }
                if (uVerify.equals("null")) {
                    mDialog.dismiss();
                    Toast.makeText(DonationPost.this, "You must be verified by Hospital to be a donor.", Toast.LENGTH_LONG).show();

//                    Intent intent = new Intent(DonationPost.this, Dashboard.class);
//                    startActivity(intent);
                } else {
//        sweetAlertDialog.setTitleText("Please wait").show();


                    final DatabaseReference refrence = FirebaseDatabase.getInstance().getReference();
                    final DonationModel donationModel = new DonationModel();
                    String key = refrence.child("Donations").push().getKey();

                    donationModel.setName(uName);
                    donationModel.setAddress(address);
                    donationModel.setLat(valueOf(lat));
                    donationModel.setLang(valueOf(lang));
                    donationModel.setDescriptions(edtMessage.getEditText().getText().toString());
                    //donationModel.setBloodGoup(bloodGroup.getText().toString());
                    donationModel.setBloodGoup(bloodGrp);
                    donationModel.setUserKey(firebaseAuth.getCurrentUser().getUid());
                    donationModel.setPhone(edtPhone.getEditText().getText().toString());
                    donationModel.setCity(city);
                    donationModel.setPushKey(key);


//                    Toast.makeText(DonationPost.this, "keyy   "+ key, Toast.LENGTH_SHORT).show();

                    refrence.child("Donations").child(key).setValue(donationModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                mDialog.dismiss();


                                Snackbar.make(findViewById(R.id.linearLayout5), "Successful! You are now a donor.", Snackbar.LENGTH_SHORT).show();
//                                new Handler().postDelayed(new Runnable() {
//                                                @Override
//                                                public void run() {
//                                                    startActivity(new Intent(DonationPost.this, Dashboard.class));
//                                                }
//                                            }, 1000);


                                // todo below is working code
//                                refrence.child("My Donations").child(firebaseAuth.getCurrentUser().getUid()).push().setValue(donationModel).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//                                        if (task.isSuccessful()) {
//                                            // sweetAlertDialog.dismissWithAnimation();
//                                            Snackbar.make(findViewById(R.id.linearLayout5), "Thank you for your nobel deed", Snackbar.LENGTH_SHORT).show();
//                                            new Handler().postDelayed(new Runnable() {
//                                                @Override
//                                                public void run() {
//                                                    startActivity(new Intent(DonationPost.this, Dashboard.class));
//                                                }
//                                            }, 1000);
//                                        } else {
//                                            //  sweetAlertDialog.dismissWithAnimation();
//                                        }
//                                    }
//                                });
                            }else{
                                mDialog.dismiss();
                                Snackbar.make(findViewById(R.id.linearLayout5), "Error! Try again.", Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(networkReciever, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));

    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(networkReciever);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if (location != null) {
            lat = location.getLatitude();
            lang = location.getLongitude();
            progressDialog.dismiss();
            try {
                Geocoder geocoder = new Geocoder(DonationPost.this, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                String _addres = addresses.get(0).getAddressLine(0);
                String cityName = addresses.get(0).getLocality();
                address = _addres;
                edtAddress.getEditText().setText(address);
                city = cityName;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            progressDialog.dismiss();
        }
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }
}