package com.example.bloodfinder.Activities;

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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bloodfinder.Activities.Admin.AdminProfileUpdate;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ProfileUpdate extends AppCompatActivity implements LocationListener {

    LocationManager locationManager;
    String id, userName, email, phone, gander, city, tokenId;
    TextInputLayout fullName, regEmail, regPass, newPass, newCpass, edtAddress, regPhone, regGender, regBlood;
    String uName, uEmail, uPass, uAddress, uPhone, uGender, uBloodGroup;
    DatabaseReference dbref, cityRefrence;
    FirebaseAuth firebaseAuth;
    //SweetAlertDialog progressDialog;
    TextView resetPassword, progress, tvEmail, tvUsername, tvBloodRequests;
    FusedLocationProviderClient mFuesdLocaitonProvide;
    NetworkReciever networkReciever = new NetworkReciever();
    Dialog progressDialog;
    Spinner spinnerBloodGroup;
    Toolbar toolbar;
    Button updateBtn;
    CheckBox male, female;
    TextView bloodGroup;
    boolean isCheck = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_update);
        //FirebaseOffline.getSync();

        initViews();
        getUserInfo();
        //checkBoxValidaiton();
        //  spinnerListener();
        getLocation();
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);

        resetPassword = findViewById(R.id.admin_reset_password);

        resetPassword.setOnClickListener(new View.OnClickListener() {
            //// @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileUpdate.this);
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
                            Toast.makeText(ProfileUpdate.this, "Enter your registered email id", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        firebaseAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ProfileUpdate.this, "Successful! Check your email.", Toast.LENGTH_SHORT).show();

                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(ProfileUpdate.this, "Error! Unable to reset Password.", Toast.LENGTH_SHORT).show();

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

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void GetCity(View view) {
        getLocation();
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 5, ProfileUpdate.this);
    }

//    private void checkBoxValidaiton() {
//        male.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (male.isChecked()) {
//                    if (isCheck) {
//                        gander = "Male";
//                        female.setEnabled(false);
//                    } else {
//                        female.setEnabled(true);
//                    }
//                    isCheck = !isCheck;
//                }
//                Toast.makeText(ProfileUpdate.this, "Male", Toast.LENGTH_SHORT).show();
//            }
//        });
//        female.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (isCheck) {
//                    gander = "Female";
//                    male.setEnabled(false);
//                } else {
//                    male.setEnabled(true);
//                }
//                isCheck = !isCheck;
//                Toast.makeText(ProfileUpdate.this, "Female", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }


    private void getUserInfo() {
        dbref.child("Users").child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    uName = snapshot.child("name").getValue(String.class);
                    uEmail = snapshot.child("email").getValue(String.class);
//                    uPass = snapshot.child("password").getValue(String.class);
                    uAddress = snapshot.child("city").getValue(String.class);
                    uPhone = snapshot.child("phone").getValue(String.class);
                    uGender = snapshot.child("gander").getValue(String.class);
                    uBloodGroup = snapshot.child("bloodGroup").getValue(String.class);


                    fullName.getEditText().setText(uName);
                    regEmail.getEditText().setText(uEmail);
//                    regPass.getEditText().setText(uPass);
                    edtAddress.getEditText().setText(uAddress);
                    regPhone.getEditText().setText(uPhone);
                    regGender.getEditText().setText(uGender);
                    regBlood.getEditText().setText(uBloodGroup);
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
//        regPass = findViewById(R.id.edtregPass);
        edtAddress = findViewById(R.id.edtPickCity);
        regPhone = findViewById(R.id.edtregPhone);
        regGender = findViewById(R.id.edtGender);
        regBlood = findViewById(R.id.edtBloodGroup);
        // bloodGroup = findViewById(R.id.tvsignupBloodField);
        //spinnerBloodGroup = findViewById(R.id.signupBloodGroups);
//        male = findViewById(R.id.maleCheckbbox);
//        female = findViewById(R.id.femaleCheckbox);
        dbref = FirebaseDatabase.getInstance().getReference();
        cityRefrence = FirebaseDatabase.getInstance().getReference();
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
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
                   // progressDialog.show();
                    final ProgressDialog mDialog = new ProgressDialog(ProfileUpdate.this);
                    mDialog.setMessage("please wait...");

                    mDialog.show();

                    Map<String, Object> map = new HashMap<>();
                    map.put("name", fullName.getEditText().getText().toString());
                map.put("city", edtAddress.getEditText().getText().toString());
                    map.put("phone", regPhone.getEditText().getText().toString());
                    map.put("bloodGroup", regBlood.getEditText().getText().toString());
                    map.put("gander", regGender.getEditText().getText().toString());

                    FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseAuth.getCurrentUser().getUid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // progressDialog.dismiss();
                            // Toast.makeText(AdminProfileUpdate.this, "Success Updated", Toast.LENGTH_SHORT).show();
//                        Snackbar.make(findViewById(R.id.parentLayout), "Successful! Profile successfully Updated", Snackbar.LENGTH_LONG).show();

                            if (task.isSuccessful()) {


                                city = edtAddress.getEditText().getText().toString();

                                DatabaseReference nodeRef = FirebaseDatabase.getInstance().getReference();
                                Query query = nodeRef.child("Cities").orderByChild("city").equalTo(city);
                                query.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            progressDialog.dismiss();
                                            Snackbar.make(findViewById(R.id.parentLayout), "Successful! Profile successfully Updated", Snackbar.LENGTH_LONG).show();
                                            startActivity(new Intent(ProfileUpdate.this, UserProfile.class));
                                            finish();
                                        } else {
                                            HashMap<String, Object> cityMap = new HashMap<>();
                                            cityMap.put("city", city);
                                            //todo check
//                                            cityRefrence.child("Cities").push().setValue(cityMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            cityRefrence.child("Cities").push().updateChildren(cityMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        progressDialog.dismiss();
                                                        mDialog.dismiss();
                                                        Snackbar.make(findViewById(R.id.parentLayout), "Successful! Profile successfully Updated", Snackbar.LENGTH_LONG).show();
                                                        startActivity(new Intent(ProfileUpdate.this, UserProfile.class));
                                                        finish();
                                                    }
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            } else {
                                progressDialog.dismiss();
                                mDialog.dismiss();
                                Snackbar.make(findViewById(R.id.parentLayout), "Sorry! Profile Update Failed ", Snackbar.LENGTH_SHORT).show();
                            }

                        }
                    });


                }
            });


        }
    }


//    public boolean isNameChanged(){
//        if (!uName.equals(fullName.getEditText().getText().toString())){
//            dbref.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("name").setValue(fullName.getEditText().getText().toString());
//            uName = fullName.getEditText().getText().toString();
//            return true;
////            Map<String,Object> map = new HashMap<>();
////            map.put("name",id.getText().toString());
////            FirebaseDatabase.getInstance().getReference().child("users")
////                    .child(getReferrer(position).getkey()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
////                        @Override
////                        public void onComplete(@NonNull Task<Void> task) {
////
////                        }
////                    });
//
//        } else{
//            return false;
//        }
//    }

    //    public boolean isEmailChanged(){
//        if (!emailUser.equals(editName.getText().toString())){
//            reference.child(usernameUser).child("email").setValue(editEmail.getText().toString());
//            emailUser = editEmail.getText().toString();
//            return true;
//        } else{
//            return false;
//        }
//    }
//
//    public boolean isPasswordChanged(){
//        if (!passwordUser.equals(editPassword.getText().toString())){
//            reference.child(usernameUser).child("password").setValue(editPassword.getText().toString());
//            passwordUser = editPassword.getText().toString();
//            return true;
//        } else{
//            return false;
//        }
//    }
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
    } else if (regBlood.getEditText().getText().toString().isEmpty()) {
            regBlood.setError("Blood Type is required");
        return false;
    } else if (regGender.getEditText().getText().toString().isEmpty()) {
            regGender.setError("Gender is required");
        return false;
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
                Geocoder geocoder = new Geocoder(ProfileUpdate.this, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                String _addres = addresses.get(0).getAddressLine(0);
                String cityName = addresses.get(0).getLocality();
                uAddress = _addres;
//                edtAddress.getEditText().setText(uAddress);
                edtAddress.getEditText().setText(city);
                city = cityName;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            progressDialog.dismiss();
        }
    }

//    private void getuserInfo() {
//        dbref.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        if (dataSnapshot.hasChildren()) {
//                            userName = dataSnapshot.child("name").getValue(String.class);
//                            email = dataSnapshot.child("email").getValue(String.class);
//                            phone = dataSnapshot.child("phone").getValue(String.class);
//                            // gander = dataSnapshot.child("gander").getValue(String.class);
//                           // bloodGroup = dataSnapshot.child("bloodGroup").getValue(String.class);
//                            city = dataSnapshot.child("city").getValue(String.class);
//
//                            tvUsername.setText(userName);
//                            tvEmail.setText(email);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//    }
}