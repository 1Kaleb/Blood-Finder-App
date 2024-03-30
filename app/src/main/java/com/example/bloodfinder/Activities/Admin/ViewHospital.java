package com.example.bloodfinder.Activities.Admin;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bloodfinder.Activities.Hospital.HospitalDashboard;
import com.example.bloodfinder.Activities.SplashScreen;
import com.example.bloodfinder.Model.Admin;
import com.example.bloodfinder.Model.DonationModel;
import com.example.bloodfinder.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class ViewHospital extends AppCompatActivity {

    Toolbar toolbar;
    String hospitalName;
    RecyclerView recyclerView;
    DatabaseReference databaseReference,dbr;
    FirebaseAuth firebaseAuth;
    FusedLocationProviderClient fusedLocationProviderClient;
    SweetAlertDialog mProgress;
    LinearLayout btnCall, btnSms, btnDelete;
    TextView tvHName, tvbloodGroup , tvHEmail, tvddress, tvHPhone, tvDescription;
    CircleImageView alertImage;
    LinearLayout linearLayoutContiner;
    String imageUrl;
    ImageView btnClose, btnsend;
    boolean isOpen;
    boolean isAdded;
    SweetAlertDialog sweetAlertDialog;
    TextInputLayout inputMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_hospital);
        initViews();
        //showAlert();
        //getLocaitons();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
         getSupportActionBar().setDisplayShowHomeEnabled(true);

//        dbr = FirebaseDatabase.getInstance().getReference();
//        dbr.child("Admin-Hospital").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.hasChildren()) {
//                    int role = snapshot.child("usertype").getValue(Integer.class);
//                    if (role == 1) {
//                        btnDelete.setVisibility(View.VISIBLE);
//                    }
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

    }

//    private void showAlert() {
//        mProgress.setTitleText("Please Wait")
//                .setCancelable(false);
//        mProgress.show();
//
//    }


    private void initViews() {
        toolbar = findViewById(R.id.toolbarViewHospital);
        recyclerView = findViewById(R.id.recyclerviewViewHospital);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        mProgress = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Admin-Hospital");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
               for (DataSnapshot snapshot : datasnapshot.getChildren()){
//                   String ma = snapshot.child("name").getValue().toString();
//                   System.out.println("The names are " + ma);


               }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


//    @SuppressLint("MissingPermission")
//    private void getLocaitons() {
//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
//        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
//            @Override
//            public void onSuccess(Location location) {
//                if (location != null) {
//                    try {
//                        Geocoder geocoder = new Geocoder(ViewHospital.this, Locale.getDefault());
//                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
//                        String _addres = addresses.get(0).getAddressLine(0);
//                        String hospitalName = addresses.get(0).getLocality();
//                        mProgress.dismissWithAnimation();
//                        fetchData(hospitalName);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
//    }

    private void fetchData(String hospitalName) {
        databaseReference = databaseReference.child("Admin-Hospital");
        Query query = databaseReference.orderByChild("name").equalTo(hospitalName);
        FirebaseRecyclerOptions<Admin> options = new FirebaseRecyclerOptions.Builder<Admin>().setQuery(query,
                Admin.class).build();
        FirebaseRecyclerAdapter<Admin, HospitalViewHolder> adaptor = new FirebaseRecyclerAdapter<Admin, HospitalViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final HospitalViewHolder holder, int position, @NonNull final Admin model) {
                holder.tvHName.setText(model.getName());
                holder.tvHEmail.setText(model.getEmail());
                holder.tvAddress.setText(model.getCity());
                holder.tvHPhone.setText(model.getPhoneNumber());


                DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Admin-Hospital");

                /// to check if it is hospital

                //put image in circle

//                dbref.child(model.getaId()).addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if (snapshot.hasChildren()) {
//                            imageUrl = snapshot.child("imageUrl").getValue(String.class);
//                            Picasso.get().load(imageUrl).into(holder.circleImageView);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                    }
//                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog dialog = new Dialog(ViewHospital.this);
                        View mView = getLayoutInflater().inflate(R.layout.custom_hospital_alert_action, null);
                        dialog.setContentView(mView);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.show();
                        initAlertViews(mView);
                        //toogleMessage();
                        makeCall(model.getPhoneNumber());
                        deleteHospital(model.getaId());
                       // sendSms(model.getPhoneNumber());
                        tvddress.setText(model.getCity());
                        tvHPhone.setText(model.getPhoneNumber());
                        tvHEmail.setText(model.getEmail());
                       // tvDescription.setText(model.getDescriptions());
                        tvHName.setText(model.getName());
                        //close alertdialog

                        btnClose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });


                        //Fetching Image


//                        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference();
//                        dbref.child("Users").child(model.getaId()).addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                if (snapshot.hasChildren()) {
//                                    String image = snapshot.child("imageUrl").getValue(String.class);
//                                    String phone = snapshot.child("phone").getValue(String.class);
//                                    Picasso.get().load(image).into(alertImage);
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//                            }
//                        });
                    }
                });


            }

            @NonNull
            @Override
            public HospitalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View mView = LayoutInflater.from(ViewHospital.this).inflate(R.layout.custome_hospital_view, parent, false);
                return new HospitalViewHolder(mView);
            }
        };
        adaptor.startListening();
        recyclerView.setAdapter(adaptor);
    }



//    private void sendSms(final String phone) {
//        btnsend.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//                    String message = inputMessage.getEditText().getText().toString();
//                    SmsManager smsManager = SmsManager.getDefault();
//                    smsManager.sendTextMessage(phone, null, "Message From the donation post" + message, null, null);
//                    Snackbar.make(linearLayoutContiner, "Message Sent", Snackbar.LENGTH_SHORT).show();
//                } catch (Exception e) {
//                    Snackbar.make(linearLayoutContiner, e.getMessage(), Snackbar.LENGTH_SHORT).show();
//
//                }
//            }
//        });
//    }

    private void makeCall(final String number) {
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + number));
                startActivity(intent);
            }
        });
    }

    private void deleteHospital(final String id) {
    btnDelete.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Toast.makeText(ViewHospital.this, "Hospital Deleted", Toast.LENGTH_SHORT).show();

        }
    });
}

//    private void toogleMessage() {
//
//        btnSms.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                askForSmsPermission();
//            }
//        });
//    }

//    private void askForSmsPermission() {
//        Dexter.withContext(ViewHospital.this)
//                .withPermission(Manifest.permission.SEND_SMS)
//                .withListener(new PermissionListener() {
//                    @Override
//                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
//                        //  sweetAlertDialog.dismissWithAnimation();
//                        isOpen = true;
//                        if (isOpen) {
//                            Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
//                            linearLayoutContiner.startAnimation(aniFade);
//                            linearLayoutContiner.setVisibility(View.VISIBLE);
//                        } else {
//                            linearLayoutContiner.setVisibility(View.GONE);
//                            Animation aniFadeout = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
//                            linearLayoutContiner.startAnimation(aniFadeout);
//
//                        }
//                        isOpen = !isOpen;
//                    }
//
//                    @Override
//                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
//                        requestpermission();
//                    }
//
//                    @Override
//                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
//                        permissionToken.continuePermissionRequest();
//                    }
//                }).check();
//    }

//    private void requestpermission() {
//
//        sweetAlertDialog.setTitleText("Permission Required")
//                .setContentText("Please allow app to SMS permission")
//                .setConfirmText("Okay")
//                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                    @Override
//                    public void onClick(SweetAlertDialog sweetAlertDialog) {
//                        Intent intent = new Intent();
//                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                        Uri uri = Uri.fromParts("package", getPackageName(), null);
//                        intent.setData(uri);
//                        startActivity(intent);
//                    }
//                }).show();
//    }

    private void initAlertViews(View mView) {
        btnCall = mView.findViewById(R.id.makeCall);
        btnDelete = mView.findViewById(R.id.deleteHospital);
        btnSms = mView.findViewById(R.id.sendSms);
        tvHName = mView.findViewById(R.id.tvalertHospitalnmae);
        tvHEmail = mView.findViewById(R.id.tvHospitalEmail);

        tvbloodGroup = mView.findViewById(R.id.tvalertBloodroup);
        tvddress = mView.findViewById(R.id.tvHospitalCity);
        tvHPhone = mView.findViewById(R.id.tvHospitalPhone);
        tvDescription = mView.findViewById(R.id.tvalertDesc);
        alertImage = mView.findViewById(R.id.alerDialogimage);
        linearLayoutContiner = mView.findViewById(R.id.messageContainer);
        btnClose = mView.findViewById(R.id.btnClose);
        btnsend = mView.findViewById(R.id.btnSend);
        inputMessage = mView.findViewById(R.id.edtAlertMessage);
    }


    class HospitalViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView tvHName, tvHEmail ,tvBloodGroup, tvAddress, tvHPhone,tvOnlineStatus;

        public HospitalViewHolder(@NonNull View itemView) {
            super(itemView);
           // tvName = itemView.findViewById(R.id.uNmaeAvaillableBlood);
            tvHName = itemView.findViewById(R.id.hospitalName);
            tvHEmail = itemView.findViewById(R.id.hospitalEmail);

           // tvBloodGroup = itemView.findViewById(R.id.viewHospitalgroup);
            tvAddress = itemView.findViewById(R.id.hospitalAddress);
            tvHPhone = itemView.findViewById(R.id.tvHospitalPhone);
          //  tvOnlineStatus = itemView.findViewById(R.id.availablStatus);
           // circleImageView = itemView.findViewById(R.id.imageViewAvalableBlood);
        }
    }
}