package com.example.bloodfinder.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bloodfinder.Activities.Admin.AdminDashboard;
import com.example.bloodfinder.Activities.Admin.AdminMainActivity;
import com.example.bloodfinder.Activities.Hospital.HospitalDashboard;
import com.example.bloodfinder.R;
import com.example.bloodfinder.Recievers.NetworkReciever;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;
import com.sun.mail.imap.protocol.UID;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SplashScreen extends AppCompatActivity implements LocationListener {
    FirebaseUser user;
    FirebaseAuth mAuth;
    DatabaseReference dbref;
    DatabaseReference adbref, dbr;
    LocationManager locationManager;
    double Lat, lang;
    NetworkReciever networkReciever;
    SweetAlertDialog requestAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        requestAccess = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        Dexter.withContext(SplashScreen.this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 5, SplashScreen.this);
                        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                            try {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
//                                        if (FirebaseAuth.getInstance().getCurrentUser() != null && FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {


//                                        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                                        //todo  if uid exit login directly

//                                            DatabaseReference dbr = FirebaseDatabase.getInstance().getReference().child("Users");
//                                            dbr.addValueEventListener(new ValueEventListener() {
//                                                @Override
//                                                public void onDataChange(@NonNull DataSnapshot datasnapshot) {
//                                                    for (DataSnapshot snapshot : datasnapshot.getChildren()){
//                                                        String userId = snapshot.child("uId").getValue().toString();
//                                                        if(FirebaseAuth.getInstance().getCurrentUser().getUid().toString().equals(userId)){
//                                                            startActivity(new Intent(SplashScreen.this, Dashboard.class));
//                                                            finish();
//                                                        }
//                                                    }
//
//                                                }
//
//                                                @Override
//                                                public void onCancelled(@NonNull DatabaseError error) {
//
//                                                }
//                                            });
//


//                                            dbref.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
//                                                @Override
//                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                                    if (snapshot.hasChildren()) {
//                                                        String uName = snapshot.child("name").getValue(String.class);
//                                                        System.out.println("awa " + uName);
//                                                    }
//
//                                                }
//
//                                                @Override
//                                                public void onCancelled(@NonNull DatabaseError error) {
//
//                                                }
//                                            });
//                                            DatabaseReference aDBR = FirebaseDatabase.getInstance().getReference().child("Admin-Hospital");
//                                            aDBR.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
//                                                @Override
//                                                public void onDataChange(@NonNull DataSnapshot datasnapshot) {
//                                                    if (datasnapshot.hasChildren()){
//                                                        String userRole = datasnapshot.child("usertype").getValue(String.class);
////
//                                                         System.out.println("hello =      "+userRole);
//                                                    }
//
////                                                    for (DataSnapshot snapshot : datasnapshot.getChildren()){
////                                                        //  String userId = snapshot.getValue().toString();
////                                                        String userRole = snapshot.child("usertype").getValue().toString();
////
////                                                         System.out.println("hello =      "+userRole);
//
////                                                        if(userRole.equals(1)){
//////                                                            startActivity(new Intent(SplashScreen.this, AdminDashboard.class));
//////                                                           finish();
////                                                            System.out.println("h1h");
////                                                        }
////                                                        if(userRole.equals(0)){
//////                                                            startActivity(new Intent(SplashScreen.this, HospitalDashboard.class));
//////                                                           finish();
////                                                            System.out.println("h2h");
////                                                        }
////                                                    }
//
//                                                }
//
//                                                @Override
//                                                public void onCancelled(@NonNull DatabaseError error) {
//
//                                                }
//                                            });


//                                            dbref = FirebaseDatabase.getInstance().getReference("Users");
//                                            dbref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
//                                                @Override
//                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                                    if (snapshot.hasChildren()) {
//
//                                                        String uid = snapshot.child("uId").getValue(String.class);
//
////                                                        if(FirebaseAuth.getInstance().getCurrentUser().getUid().toString().equals(uid)){
////                                                            startActivity(new Intent(SplashScreen.this, Dashboard.class));
////                                                           // finish();
////                                                        }
//                                                        System.out.println("h1h"+ uid);
//                                                    }
//
//
//                                                }
//
//                                                @Override
//                                                public void onCancelled(@NonNull DatabaseError error) {
//
//                                                }
//                                            });
//
//                                            adbref = FirebaseDatabase.getInstance().getReference("Admin-Hospital");
//                                            adbref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
//                                                @Override
//                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                                    if (snapshot.hasChildren()) {
//
//                                                        String userRole = snapshot.child("usertype").getValue(String.class);
//
//                                                        System.out.println("h1h"+ userRole);
////                                                        if(userRole.equals(1)){
////                                                            startActivity(new Intent(SplashScreen.this, AdminDashboard.class));
////                                                           finish();
////                                                        }
////                                                        if(userRole.equals(0)){
////                                                            startActivity(new Intent(SplashScreen.this, HospitalDashboard.class));
////                                                           finish();
////                                                        }
//                                                    }
//
//
//                                                }
//
//                                                @Override
//                                                public void onCancelled(@NonNull DatabaseError error) {
//
//                                                }
//                                            });

                                        //todo this below

//                                            startActivity(new Intent(SplashScreen.this, Dashboard.class));


//                                        }
//
                                        if (FirebaseAuth.getInstance().getCurrentUser() != null) {


                                            dbr = FirebaseDatabase.getInstance().getReference();
                                            dbr.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if (snapshot.hasChildren()) {
//                                                        String uName = snapshot.child("name").getValue(String.class);
//                                                        System.out.println("awa " + uName);
                                                        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
//                                                        if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                                                        startActivity(new Intent(SplashScreen.this, Dashboard.class));
                                                        finish();
//                                                        }else{
////                                                            FirebaseAuth.getInstance().signOut();
//                                                            Toast.makeText(SplashScreen.this, "not verified", Toast.LENGTH_SHORT).show();
//                                                        }

                                                    }

                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

                                            dbr = FirebaseDatabase.getInstance().getReference();
                                            dbr.child("Admin-Hospital").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if (snapshot.hasChildren()) {
                                                        int role = snapshot.child("usertype").getValue(Integer.class);

                                                        Intent a;
                                                        switch (role) {
                                                            case 0:
                                                                a = new Intent(SplashScreen.this, HospitalDashboard.class);
                                                                break;
                                                            default:
                                                                a = new Intent(SplashScreen.this, AdminDashboard.class);
                                                                break;
                                                        }
                                                        startActivity(a);

                                                    }

                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        } else {
                                            startActivity(new Intent(SplashScreen.this, MainActivity.class));
                                        }
                                    }
//                                }, 4000);
                                }, 2000);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            asktoLocationAccess();
                        }
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        requestPermission();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    private void requestPermission() {
        requestAccess.setTitleText("Permission Required")
                .setContentText("Please allow application to use location")
                .setConfirmText("Allow")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                }).show();


//        new android.app.AlertDialog.Builder(this)
//                .setTitle("Permisison Required")
//                .setMessage("Please allow application to use this feature")
//                .setCancelable(false)
//                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Intent intent = new Intent();
//                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                        Uri uri = Uri.fromParts("package", getPackageName(), null);
//                        intent.setData(uri);
//                        startActivity(intent);
//                    }
//                })
//                .setNegativeButton("CANCLE", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                }).show();
    }

    public void asktoLocationAccess() {


        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Enable GPS")
                .setContentText("Please Enable your device GPS")
                .showCancelButton(true)
                .setConfirmText("Yes,Enable").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        SplashScreen.this.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                }).show();


//        new AlertDialog.Builder(SplashScreen.this)
//                .setTitle("Request Locaiton Provide")
//                .setMessage("Please turn on device location ")
//                .setPositiveButton("Locaitons", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
//                        SplashScreen.this.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//                        paramDialogInterface.dismiss();
//                    }
//                })
//                .setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                })
//                .show();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Lat = location.getLatitude();
        lang = location.getLongitude();
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