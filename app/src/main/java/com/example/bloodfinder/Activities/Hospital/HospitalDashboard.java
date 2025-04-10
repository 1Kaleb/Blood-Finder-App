package com.example.bloodfinder.Activities.Hospital;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.bloodfinder.Activities.AboutUs;
import com.example.bloodfinder.Activities.Admin.AdminMainActivity;
import com.example.bloodfinder.Activities.Admin.AdminProfile;
import com.example.bloodfinder.Activities.BloodInformation;
import com.example.bloodfinder.Activities.MainActivity;
import com.example.bloodfinder.Fragments.AdminHomeFragment;
import com.example.bloodfinder.Fragments.HospitalHomeFragment;
import com.example.bloodfinder.Fragments.NearBy;
import com.example.bloodfinder.Fragments.notificaitons;
import com.example.bloodfinder.R;
import com.example.bloodfinder.Utils.FirebaseOffline;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.UUID;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class HospitalDashboard extends AppCompatActivity {
    private static final int PICKIMAGE_CODE = 10;
    BottomNavigationView bottomNavigationView;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    LocationManager locationManager;
    DrawerLayout hospitalDrawerLayout;
    NavigationView navigationView;
    String id, adminName, email, phone, city, imageUrl;
    int usertype;
    DatabaseReference dbref;
    SweetAlertDialog progressDialog;
    TextView progress, tvEmail, tvUsername, tvBloodRequests;
    CircleImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_dashboard);
       // FirebaseOffline.getSync();
        runBackgroundThread();
        initViews();
        getuserInfo();
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.hospitalfragmentlayout, new HospitalHomeFragment()).commit();
    }


    private void getuserInfo() {
        dbref.child("Admin-Hospital").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()) {
                            email = dataSnapshot.child("email").getValue(String.class);
                            adminName = dataSnapshot.child("name").getValue(String.class);
                            phone = dataSnapshot.child("phone").getValue(String.class);
                            city = dataSnapshot.child("city").getValue(String.class);
                            usertype = dataSnapshot.child("usertype").getValue(int.class);
                            //  imageUrl = dataSnapshot.child("imageUrl").getValue(String.class);
//                            if (!imageUrl.equals("null")) {
//                                Picasso.get().load(imageUrl).fit().centerCrop().into(profileImage);
//                            }
                            tvUsername.setText(adminName);
                            tvEmail.setText(email);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.hospitaltoolbartop);
        dbref = FirebaseDatabase.getInstance().getReference();
        setSupportActionBar(toolbar);
        hospitalDrawerLayout = findViewById(R.id.hospitaldrawerparent);
        //Fetching ProgressDialog
        progressDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        progressDialog.setTitleText("Please Wait");

        //Drawwer Toggle.
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, hospitalDrawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        hospitalDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        bottomNavigationView = findViewById(R.id.hospitalbottomnavbar);
        navigationView = findViewById(R.id.hospital_nav_view);

        //Fetching Header Drawer Vivews
        View uView = navigationView.inflateHeaderView(R.layout.drawer_hospital_header);
        profileImage = uView.findViewById(R.id.profileImageHolder);
        tvUsername = uView.findViewById(R.id.tvuserName);
        tvEmail = uView.findViewById(R.id.tvEmail);
        getSupportFragmentManager().beginTransaction().replace(R.id.hospitalfragmentlayout, new HospitalHomeFragment()).commit();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment selectedFragment = null;
                switch (menuItem.getItemId()) {
//                    case R.id.interest:
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                startActivity(new Intent(HospitalDashboard.this, AvailableBlood.class));
//                            }
//                        }, 800);
//                        break;
//
//                    case R.id.bloodRequest:
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                startActivity(new Intent(HospitalDashboard.this, BloodRequests.class));
//                            }
//                        }, 800);
//                        break;
//
//                    case R.id.myRequests:
//                        Toast.makeText(HospitalDashboard.this, "MY requests", Toast.LENGTH_SHORT).show();
//                        //add my requeset to chacel request posted
//                        //
//                        //
//                        //
//                        break;
//
                    case R.id.hospitalProfile:
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(HospitalDashboard.this, HospitalProfile.class);
                                intent.putExtra("adminId", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                startActivity(intent);
                            }
                        }, 800);
                        break;
//
//                    case R.id.makeChat:
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                startActivity(new Intent(HospitalDashboard.this, ChatActivity.class));
//                            }
//                        }, 800);
//                        break;
//
//                    //close when clicked
//
//                    case R.id.achievement:
////                        new Handler().postDelayed(new Runnable() {
////                            @Override
////                            public void run() {
////                                startActivity(new Intent(Dashboard.this, Achievement.class));
////                            }
////                        }, 800);
//                        Toast.makeText(HospitalDashboard.this, "Achievement", Toast.LENGTH_SHORT).show();
//                        break;
                    case R.id.infoPage:
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(HospitalDashboard.this, BloodInformation.class));
                            }
                        }, 800);
                        break;
                    case R.id.aboutUs:
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(HospitalDashboard.this, AboutUs.class));
                            }
                        }, 800);
                        break;
                    case R.id.logout:
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(HospitalDashboard.this, AdminMainActivity.class));
                                finish();
                            }
                        }, 2000);
                        break;
                }
                hospitalDrawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }


    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragement = null;
                    switch (menuItem.getItemId()) {
                        case R.id.hospitaldashboard:
                            selectedFragement = new HospitalHomeFragment();
                            break;
                       case R.id.hospitalnearby:
                            selectedFragement = new NearBy();
                            break;
//                        case R.id.hospitalalert:
//                            selectedFragement = new notificaitons();
//                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.hospitalfragmentlayout, selectedFragement).commit();
                    return true;
                }
            };

//    public void profileImage(View view) {
//        Dexter.withContext(this)
//                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
//                .withListener(new PermissionListener() {
//                    @Override
//                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
//                        CropImage.activity()
//                                .setGuidelines(CropImageView.Guidelines.ON)
//                                .setAspectRatio(1, 1)
//                                .start(HospitalDashboard.this);
//                    }
//
//
//                    @Override
//                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
//                        requsetPermission();
//                    }
//
//                    @Override
//                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
//                        permissionToken.continuePermissionRequest();
//                    }
//                }).check();
//    }

    private void requsetPermission() {
        new AlertDialog.Builder(this)
                .setTitle("Permisison Required")
                .setMessage("Please allow application to use this feature")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("CANCLE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == PICKIMAGE_CODE && resultCode == RESULT_OK && data.getData() != null) {
//            Uri imageUri = data.getData();
//        }
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            Uri resutlUri = result.getUri();
//            uploadImage(resutlUri);
//            try {
//                CircleImageView circleImageView = findViewById(R.id.profileImageHolder);
//                circleImageView.setImageBitmap(BitmapFactory.decodeFile(resutlUri.toString()));
//                circleImageView.setImageBitmap(MediaStore.Images.Media.getBitmap(HospitalDashboard.this.getContentResolver(), resutlUri));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//    }


//    private void uploadImage(Uri resutlUri) {
//        progressDialog.setCancelable(false);
//        progressDialog.show();
//        firebaseStorage = FirebaseStorage.getInstance();
//        storageReference = firebaseStorage.getReference();
//        storageReference = storageReference.child("AdminHospitalImages/" + UUID.randomUUID().toString());
//        storageReference.putFile(resutlUri).addOnSuccessListener(   new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
//                while (!uri.isComplete()) ;
//                Uri photoUri = uri.getResult();
//                dbref.child("Admin-Hospital").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("imageUrl").setValue(photoUri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            progressDialog.dismissWithAnimation();
//                            Snackbar.make(findViewById(R.id.toolbartop), "Success! Profile Image Uploaded", Snackbar.LENGTH_SHORT).show();
//                        } else {
//                            progressDialog.dismissWithAnimation();
//                            Snackbar.make(findViewById(R.id.toolbartop), "Error! Upload Failed", Snackbar.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//            }
//        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
//                double uploadProgress = (100 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
//                progressDialog.setContentText(String.valueOf((int) uploadProgress) + "%");
//            }
//        });
//    }

    // Runable Thread
    private void runBackgroundThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MapView mv = new MapView(getApplicationContext());
                    mv.onCreate(null);
                    mv.onPause();
                    mv.onDestroy();
                } catch (Exception ignored) {
                }
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        if (hospitalDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            hospitalDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            finishAffinity();
        }
    }
}