package com.example.bloodfinder.Activities.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bloodfinder.Model.DonationModel;
import com.example.bloodfinder.Model.UserModel;
import com.example.bloodfinder.R;
import com.example.bloodfinder.Recievers.NetworkReciever;
import com.example.bloodfinder.Utils.FirebaseOffline;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AdminProfileUpdate extends AppCompatActivity implements LocationListener {

    LocationManager locationManager;
    String id, userName, email, phone, gander, city, bloodGroup, tokenId;
    TextInputLayout fullName, regEmail, regPass, newPass, newCpass, edtAddress, regPhone;
    String uName, uEmail, uPass, uAddress, uPhone;
    DatabaseReference dbref;
    FirebaseAuth firebaseAuth;
    //SweetAlertDialog progressDialog;
    TextView resetPassword, progress, tvEmail, tvUsername, tvBloodRequests;
    FusedLocationProviderClient mFuesdLocaitonProvide;
    NetworkReciever networkReciever = new NetworkReciever();
    Dialog progressDialog;
    Spinner spinnerBloodGroup;
    Toolbar toolbar;
    Button updateBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile_update);
       // FirebaseOffline.getSync();

        initViews();
        getUserIfo();
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getLocation();



        resetPassword = findViewById(R.id.admin_reset_password);

        resetPassword.setOnClickListener(new View.OnClickListener() {
            //// @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AdminProfileUpdate.this);
                View dialogView = getLayoutInflater().inflate(R.layout.activity_admin_reset_password, null);
                EditText emailBox = dialogView.findViewById(R.id.emailBox);
                emailBox.setText(uEmail);
                builder.setView(dialogView);
                AlertDialog dialog = builder.create();
                dialogView.findViewById(R.id.btnReset).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String userEmail = emailBox.getText().toString();
                        if (TextUtils.isEmpty(userEmail) && !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                            Toast.makeText(AdminProfileUpdate.this, "Enter your registered email id", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        firebaseAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(AdminProfileUpdate.this, "Successful! Check your email.", Toast.LENGTH_SHORT).show();

                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(AdminProfileUpdate.this, "Error! Unable to reset Password.", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                    }
                });
                dialogView.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                if (dialog.getWindow() != null) {
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                dialog.show();
            }
        });
    }

//    @RequiresApi(api = Build.VERSION_CODES.M)
//    public void GetCity(View view) {
//        getLocation();
//    }
//
//    @SuppressLint("MissingPermission")
//    private void getLocation() {
//        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 5, AdminProfileUpdate.this);
//    }

    private void getUserIfo() {
        dbref.child("Admin-Hospital").child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    uName = snapshot.child("name").getValue(String.class);
                    uEmail = snapshot.child("email").getValue(String.class);
                    uPass = snapshot.child("password").getValue(String.class);
                    uAddress = snapshot.child("city").getValue(String.class);
                    uPhone = snapshot.child("phoneNumber").getValue(String.class);

                    fullName.getEditText().setText(uName);
                    regEmail.getEditText().setText(uEmail);
                    regPass.getEditText().setText(uPass);
                    edtAddress.getEditText().setText(uAddress);
                    regPhone.getEditText().setText(uPhone);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initViews() {
        fullName = findViewById(R.id.edtFullName);
        regEmail = findViewById(R.id.edtregEmail);
        regPass = findViewById(R.id.edtregPass);
        edtAddress = findViewById(R.id.edtPickCity);
        regPhone = findViewById(R.id.edtregPhone);
        dbref = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        toolbar = findViewById(R.id.toolbardonaiton);
        updateBtn = findViewById(R.id.btn_updateProfile);
        progressDialog = new Dialog(this);
        View mView = getLayoutInflater().inflate(R.layout.progress_dialog_wait, null);
        progressDialog.setContentView(mView);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public void Update(View view) {
        if (isValid() == true) {
            updateBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //progressDialog.show();
                    final ProgressDialog mDialog = new ProgressDialog(AdminProfileUpdate.this);
                    mDialog.setMessage("please wait...");

                    mDialog.show();

                    Map<String, Object> map = new HashMap<>();
                    map.put("name", fullName.getEditText().getText().toString());
                    map.put("city", edtAddress.getEditText().getText().toString());
                    map.put("phoneNumber", regPhone.getEditText().getText().toString());

                    FirebaseDatabase.getInstance().getReference().child("Admin-Hospital").child(firebaseAuth.getCurrentUser().getUid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mDialog.dismiss();
                            // progressDialog.dismiss();
                           // Toast.makeText(AdminProfileUpdate.this, "Success Updated", Toast.LENGTH_SHORT).show();
                            Snackbar.make(findViewById(R.id.parentLayout), "Successful! Profile successfully Updated", Snackbar.LENGTH_LONG).show();

                        }
                    });

                }
            });
        }
    }


    public boolean isValid() {
        if (fullName.getEditText().getText().toString().isEmpty()) {
            fullName.setError("Full Name required");
            return false;
//    } else if (regEmail.getEditText().getText().toString().isEmpty()) {
//        regEmail.setError("Email required");
//        return false;
//    } else if (regPass.getEditText().getText().toString().isEmpty()) {
//        regPass.setError("Old Password required");
//        return false;
//    } else if (newCpass.getEditText().getText().toString().isEmpty()) {
//        newPass.setError("New Password required");
//        return false;
//    } else if (newCpass.getEditText().getText().toString().isEmpty()) {
//        newCpass.setError("Confirmed New Password required");
//        return false;
        } else if (edtAddress.getEditText().getText().toString().isEmpty()) {
            edtAddress.setError("City name required");
            return false;
        } else if (regPhone.getEditText().getText().toString().isEmpty()) {
            regPhone.setError("Phone number required");
            return false;
        } else {
            return true;
        }
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
//            lat = location.getLatitude();
//            lang = location.getLongitude();
            progressDialog.dismiss();
            try {
                Geocoder geocoder = new Geocoder(AdminProfileUpdate.this, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                String _addres = addresses.get(0).getAddressLine(0);
                String cityName = addresses.get(0).getLocality();
                uAddress = _addres;
                edtAddress.getEditText().setText(uAddress);
                city = cityName;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            progressDialog.dismiss();
        }
    }
}