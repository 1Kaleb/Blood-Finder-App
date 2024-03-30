package com.example.bloodfinder.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bloodfinder.Activities.Admin.Adapter;
import com.example.bloodfinder.Model.DonationModel;
import com.example.bloodfinder.Model.HospitalModel;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class AvailableBlood extends AppCompatActivity {

    Toolbar toolbar;
    EditText searchField;
    String cityName;
    RecyclerView recyclerView;
    SearchView searchView;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    FusedLocationProviderClient fusedLocationProviderClient;
    SweetAlertDialog mProgress;
    ProgressDialog mDialog;
    LinearLayout btnCall, btnSms, btnChat;
    TextView tvName, tvbloodGroup, tvddress, tvDescription;
    CircleImageView alertImage;
    LinearLayout linearLayoutContiner;
    String imageUrl;
    ImageView btnClose, btnsend;
    ImageButton search;
    boolean isOpen;
    boolean isAdded;
    SweetAlertDialog sweetAlertDialog;
    TextInputLayout inputMessage;

    UserAdapter adapter;
    ArrayList<DonationModel> list;

    //undo last



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_blood);
        initViews();
        showAlert();
        getLocaitons();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        searchView = findViewById(R.id.uSearch);
        searchView.clearFocus();

        list = new ArrayList<>();
        adapter = new UserAdapter(list, this);

        recyclerView.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                recyclerView.setAdapter(adapter);
                filterList(newText);
                return true;
            }
        });


    }

    private void filterList(String newText) {
        ArrayList<DonationModel> uList = new ArrayList<>();
        for (DonationModel uModel : list) {
            if (uModel.getName().toLowerCase().contains(newText.toLowerCase()) || uModel.getCity().toLowerCase().contains(newText.toLowerCase()) || uModel.getBloodGoup().toLowerCase().contains(newText.toLowerCase())) {
                uList.add(uModel);
            }
            adapter.searchUser(uList);
        }

    }

    private void showAlert() {
//        mProgress.setTitleText("Fetching Locaiton Please Wait")
//                .setCancelable(false);
//        mProgress.show();

        mDialog.setMessage("please wait...");

        mDialog.show();

    }


    private void initViews() {
        toolbar = findViewById(R.id.toolbarAvailableBlood);
        //  searchField = findViewById(R.id.search_field);
        //search = findViewById(R.id.search_button);
        recyclerView = findViewById(R.id.recyclerviewAvailableBlood);
//        searchView = findViewById(R.id.uSearch);
//        searchView.clearFocus();


        //Grid or Linear


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        mProgress = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        mDialog = new ProgressDialog(AvailableBlood.this);

        sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
    }


    @SuppressLint("MissingPermission")
    private void getLocaitons() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    try {
                        Geocoder geocoder = new Geocoder(AvailableBlood.this, Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        String _addres = addresses.get(0).getAddressLine(0);
                        String cityName = addresses.get(0).getLocality();
//                        mProgress.dismissWithAnimation();
                        mDialog.dismiss();
                        fetchData(cityName);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void fetchData(String cityName) {

        databaseReference = databaseReference.child("Donations");
        Query query = databaseReference.orderByChild("city").equalTo(cityName);
        FirebaseRecyclerOptions<DonationModel> options = new FirebaseRecyclerOptions.Builder<DonationModel>().setQuery(query,
                DonationModel.class).build();
        FirebaseRecyclerAdapter<DonationModel, DonationViewHolder> adaptor = new FirebaseRecyclerAdapter<DonationModel, DonationViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final DonationViewHolder holder, int position, @NonNull final DonationModel model) {
                holder.tvName.setText(model.getName());
                holder.tvAddress.setText(model.getCity());
                holder.tvBloodGroup.setText(model.getBloodGoup());


                DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Users");
                dbref.child(model.getUserKey()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChildren()) {
                            imageUrl = snapshot.child("imageUrl").getValue(String.class);
//                            Picasso.get().load(imageUrl).into(holder.circleImageView);
                            Picasso.get().load(R.mipmap.ic_launcher).into(holder.circleImageView);
                            if (!imageUrl.equals("null")){
//                                Picasso.get().load(R.mipmap.ic_launcher).into(holder.circleImageView);
                                Picasso.get().load(imageUrl).into(holder.circleImageView);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog dialog = new Dialog(AvailableBlood.this);
                        View mView = getLayoutInflater().inflate(R.layout.custome_alert_action, null);
                        dialog.setContentView(mView);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.show();
                        initAlertViews(mView);
                        toogleMessage();
                        makeCall(model.getPhone());
                        sendSms(model.getPhone());
                        chat(model.getUserKey(), model.getName());
                        tvddress.setText(model.getCity());
                        tvbloodGroup.setText(model.getBloodGoup());
                        tvDescription.setText(model.getDescriptions());
                        tvName.setText(model.getName());
                        //close alertdialog

                        btnClose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        //Fetching Image
                        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference();
                        dbref.child("Users").child(model.getUserKey()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.hasChildren()) {
                                    String image = snapshot.child("imageUrl").getValue(String.class);
                                    String phone = snapshot.child("phone").getValue(String.class);
//                                    Picasso.get().load(image).into(alertImage);
                                    Picasso.get().load(R.mipmap.ic_launcher).into(holder.circleImageView);

                                    if (!image.equals("null")){
//                                        Picasso.get().load(R.mipmap.ic_launcher).into(holder.circleImageView);
                                        Picasso.get().load(image).into(alertImage);

                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    }
                });


            }

            @NonNull
            @Override
            public DonationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View mView = LayoutInflater.from(AvailableBlood.this).inflate(R.layout.custome_available_blood, parent, false);
                return new DonationViewHolder(mView);
            }
        };
        adaptor.startListening();
        recyclerView.setAdapter(adaptor);
    }

    private void sendSms(final String phone) {
        btnSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent smsIntent = new Intent(android.content.Intent.ACTION_VIEW);
                smsIntent.setType("vnd.android-dir/mms-sms");
                smsIntent.putExtra("address", phone);
                smsIntent.putExtra("sms_body", "Blood Finder - Message From your blood donation post:\n");
                startActivity(smsIntent);


//                try {
//                    String message = inputMessage.getEditText().getText().toString();
//                    SmsManager smsManager = SmsManager.getDefault();
//                    smsManager.sendTextMessage(phone, null, "Blood Finder - Message From your blood donation post:\n" + message, null, null);
//                    Snackbar.make(linearLayoutContiner, "Message Sent", Snackbar.LENGTH_SHORT).show();
//                } catch (Exception e) {
//                    Snackbar.make(linearLayoutContiner, e.getMessage(), Snackbar.LENGTH_SHORT).show();
//
//                }
            }
        });
    }

    private void chat(final String uid, String name) {
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(uid)) {
                    Snackbar.make(findViewById(R.id.recyclerviewAvailableBlood), "You can't send message to yourself!", Snackbar.LENGTH_SHORT).show();

                } else {
                    Intent intent = new Intent(AvailableBlood.this, MessageActivity.class);
                    intent.putExtra("userId", uid);
                    intent.putExtra("name", name);
                    intent.putExtra("imageUrl", imageUrl);
                    startActivity(intent);
                }
            }
        });

    }

    // call user
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

    private void toogleMessage() {

        btnSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askForSmsPermission();
            }
        });
    }

    private void askForSmsPermission() {
        Dexter.withContext(AvailableBlood.this)
                .withPermission(Manifest.permission.SEND_SMS)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        //  sweetAlertDialog.dismissWithAnimation();
                        isOpen = true;
                        if (isOpen) {
                            Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
                            linearLayoutContiner.startAnimation(aniFade);
                            linearLayoutContiner.setVisibility(View.VISIBLE);
                        } else {
                            linearLayoutContiner.setVisibility(View.GONE);
                            Animation aniFadeout = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
                            linearLayoutContiner.startAnimation(aniFadeout);

                        }
                        isOpen = !isOpen;
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        requestpermission();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    private void requestpermission() {

        sweetAlertDialog.setTitleText("Permission Required")
                .setContentText("Please allow app to SMS permission")
                .setConfirmText("Okay")
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
    }

    private void initAlertViews(View mView) {
        btnCall = mView.findViewById(R.id.makeCall);
        btnSms = mView.findViewById(R.id.sendSms);
        btnChat = mView.findViewById(R.id.btnChat);
        tvName = mView.findViewById(R.id.tvalertUsernmae);
        tvbloodGroup = mView.findViewById(R.id.tvalertBloodroup);
        tvddress = mView.findViewById(R.id.tvAlertAddress);
        tvDescription = mView.findViewById(R.id.tvalertDesc);
        alertImage = mView.findViewById(R.id.alerDialogimage);
        linearLayoutContiner = mView.findViewById(R.id.messageContainer);
        btnClose = mView.findViewById(R.id.btnClose);
        btnsend = mView.findViewById(R.id.btnSend);
        inputMessage = mView.findViewById(R.id.edtAlertMessage);
    }


    class DonationViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView tvName, tvBloodGroup, tvAddress, tvOnlineStatus;

        public DonationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.uNmaeAvaillableBlood);
            tvBloodGroup = itemView.findViewById(R.id.availableBloodgroup);
            tvAddress = itemView.findViewById(R.id.availableBloodAddress);
//            tvOnlineStatus = itemView.findViewById(R.id.availablStatus);
            circleImageView = itemView.findViewById(R.id.imageViewAvalableBlood);
        }
    }
}