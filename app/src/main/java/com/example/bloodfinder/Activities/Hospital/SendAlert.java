package com.example.bloodfinder.Activities.Hospital;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.util.Calendar;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bloodfinder.Activities.AvailableBlood;
import com.example.bloodfinder.Activities.BloodRequests;
import com.example.bloodfinder.Activities.MainActivity;
import com.example.bloodfinder.Activities.SignUp;
import com.example.bloodfinder.Model.AlertModel;
import com.example.bloodfinder.Model.BloodRequestModel;
import com.example.bloodfinder.Model.EventModel;
import com.example.bloodfinder.Model.NotificaiotnsModel;
import com.example.bloodfinder.Model.UserModel;
import com.example.bloodfinder.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.DateFormat;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class SendAlert extends AppCompatActivity implements View.OnClickListener {
    DatabaseReference addAlert, userRefrence, dbref, removeRefrence;
    FirebaseAuth firebaseAuth;
    RecyclerView recyclerView;
    Toolbar toolbar;
    String city,userCity, uid, mycity,aBloodGroup;
    LinearLayout postBloodrequest, postdialog, pickStartDate, pickEndDate, pickStartTime, pickEndTime, btnCall;
    boolean isUp;
    int eYear, eMonth, eDay;
    TextInputLayout alertName, edtAddress, edtBloodGroup,  aDate;
    DatabaseReference sendAlert;
    Spinner spinnerBloodGroup;
    TextView bloodGroup, tvClear;;
    Dialog dialog;
    FloatingActionButton togglePost;
    SweetAlertDialog sweetAlertDialog;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_alert);
        initViews();
        getAlerts();
        removeAlerts();
        getUserInfo();
        // selectBloodGroup();
//      getCurrentUserDetails();
        //getCurrentLocation();
        spinnerListener();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        postdialog.setVisibility(View.INVISIBLE);
        isUp = false;
        togglePost.setOnClickListener(this);
//        pickDate.setOnClickListener(this);
    }

    private void spinnerListener() {
        spinnerBloodGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                bloodGroup.setText(spinnerBloodGroup.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    private void getUserInfo() {
//        userRefrence = FirebaseDatabase.getInstance().getReference("Admin-Hospital");
//        userRefrence.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
////            @RequiresApi(api = Build.VERSION_CODES.N)
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.hasChildren()) {
//                    hName = snapshot.child("name").getValue(String.class);
//                    hAddress = snapshot.child("city").getValue(String.class);
//
//
//                    alertName.getEditText().setText(hName);
//                    edtAddress.getEditText().setText(hAddress);
////                    Calendar calendar = Calendar.getInstance();
////                    String currentDate = DateFormat.getDateInstance(DateFormat.DATE_FIELD).format((calendar.getTime()));
////                    aDate.getEditText().setText(currentDate);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });


        DatabaseReference href = FirebaseDatabase.getInstance().getReference().child("Admin-Hospital");
        href.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
//                if (datasnapshot.hasChildren()) {
//                    hName = datasnapshot.child("name").getValue().toString();
//                    hAddress = datasnapshot.child("city").getValue().toString();
//
//
//                    System.out.println("name "+hName+" add "+hAddress);
//                    alertName.getEditText().setText(hName);
//                    edtAddress.getEditText().setText(hAddress);
//
//                }
//                for (DataSnapshot snapshot : datasnapshot.getChildren()){
////                    String ma = snapshot.child("name").getValue().toString();
//                    String hName = snapshot.child("name").getValue().toString();
//                    String hAddress = snapshot.child("city").getValue().toString();
//                    alertName.getEditText().setText(hName);
//                    edtAddress.getEditText().setText(hAddress);
////                    System.out.println("  name "+hName+" add "+hAddress);
//                }
                if (datasnapshot.hasChildren()) {
                    String hName = datasnapshot.child("name").getValue().toString();
                    String hAddress = datasnapshot.child("city").getValue().toString();
                    alertName.getEditText().setText(hName);
                    edtAddress.getEditText().setText(hAddress);
                }
                Calendar calendar = Calendar.getInstance();
                    String currentDate = DateFormat.getDateInstance(DateFormat.DATE_FIELD).format((calendar.getTime()));
                    aDate.getEditText().setText(currentDate);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

        private void removeAlerts() {
        tvClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Query alertQuery = FirebaseDatabase.getInstance().getReference().child("Alerts").orderByChild("aId").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());

                removeRefrence = FirebaseDatabase.getInstance().getReference("Alerts");
                removeRefrence.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
//                                    sweetAlertDialog.dismissWithAnimation();
                                    Snackbar.make(tvClear, "Alerts are Removed!", Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

    }

//    private void getCurrentUserDetails() {
//        userRefrence = FirebaseDatabase.getInstance().getReference("Users");
//        userRefrence.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.hasChildren()) {
//                    userCity = snapshot.child("city").getValue(String.class);
////                    imageUrl = snapshot.child("imageUrl").getValue(String.class);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//            }
//        });
//
//    }


//private void selectBloodGroup() {
//    bloodGroupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//        @Override
//        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//            bloodGroup.setText(bloodGroupSpinner.getSelectedItem().toString());
//        }
//
//        @Override
//        public void onNothingSelected(AdapterView<?> parent) {
//
//        }
//    });
//
//}


//    @SuppressLint("MissingPermission")
//    private void getcityName() {
//        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 5, addAlert.this);
//    }

    //    @Override
//    public void onLocationChanged(@NonNull Location location) {
//        sweetAlertDialog.dismissWithAnimation();
//        try {
//            Geocoder geocoder = new Geocoder(addAlert.this, Locale.getDefault());
//            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
//            String _addres = addresses.get(0).getAddressLine(0);
//            mycity = addresses.get(0).getLocality();
//            edtAddress.getEditText().setText(_addres);
//            getEvents(mycity);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
    private void getAlerts() {

        Query query = addAlert.child("Alerts").orderByChild("city");
        FirebaseRecyclerOptions<AlertModel> options =
                new FirebaseRecyclerOptions.Builder<AlertModel>().setQuery(query, AlertModel.class).build();
        FirebaseRecyclerAdapter<AlertModel, AlertViewHolder> adapter = new FirebaseRecyclerAdapter<AlertModel, AlertViewHolder>(options) {
            @SuppressLint("StaticFieldLeak")
            @Override
            protected void onBindViewHolder(@NonNull final AlertViewHolder holder, int position, @NonNull final AlertModel model) {
                holder.malertName.setText(model.getName());
                holder.mAddress.setText(model.getCity());
                holder.aBGroup.setText(model.getBloodGroup());
                holder.aDate.setText(model.getDate());

//                final String aId = model.getaId();
//                if (aId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
//                    holder.btnCallAlert.setVisibility(View.GONE);
//                    holder.btnDeleteAlert.setVisibility(View.VISIBLE);
//                }

//                holder.btnDeleteAlert.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Snackbar.make(findViewById(R.id.recyclerViewAlert), "The Alert has been deleted!", Snackbar.LENGTH_LONG).show();
//                        addAlert.child("Alerts").child(model.getAlertId()).removeValue();
//
//                    }
//                });
//
//                holder.btnCallAlert.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent intent = new Intent(Intent.ACTION_DIAL);
//                        intent.setData(Uri.parse("tel:" + ePNumber));
//                        startActivity(intent);
//                    }
//                });

            }


            @NonNull
            @Override
            public AlertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View mView = LayoutInflater.from(SendAlert.this).inflate(R.layout.custom_alert, parent, false);
                return new AlertViewHolder(mView);
            }
        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }


    private void initViews() {
        togglePost = findViewById(R.id.btnAlertPosts);
        addAlert = FirebaseDatabase.getInstance().getReference();
        recyclerView = findViewById(R.id.recyclerViewAlert);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        toolbar = findViewById(R.id.toolbarRequests);
        sweetAlertDialog = new SweetAlertDialog(SendAlert.this, SweetAlertDialog.PROGRESS_TYPE);
        postdialog = findViewById(R.id.postAlertDialog);
        edtAddress = findViewById(R.id.edtAddress);
        alertName = findViewById(R.id.edtAlertName);
        bloodGroup = findViewById(R.id.tvAlertBloodField);
        spinnerBloodGroup = findViewById(R.id.alertBloodGroups);
        tvClear = findViewById(R.id.tvClearAll);
        aDate = findViewById(R.id.edtAlertDate);
        dbref = FirebaseDatabase.getInstance().getReference();
        dialog = new Dialog(this);
        View view = getLayoutInflater().inflate(R.layout.progress_dialog_wait, null);
        dialog.setContentView(view);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //sweetAlertDialog.show();
    }


    static class AlertViewHolder extends RecyclerView.ViewHolder {
        TextView malertName, aDate, aBGroup, mAddress;

        public AlertViewHolder(@NonNull View itemView) {
            super(itemView);
            malertName = itemView.findViewById(R.id.tvAlertName);
            aDate = itemView.findViewById(R.id.tvAlertSentDate);
            aBGroup = itemView.findViewById(R.id.tvBloodType);
            mAddress = itemView.findViewById(R.id.tvAlertAddress);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        if (v == togglePost) {
            if (isUp) {
                slideDown(postdialog);
            } else {
                slideUp(postdialog);
                // todo: check how this work
//                Toast.makeText(SendAlert.this, "I am "+hName+" City "+hAddress+" Date ", Toast.LENGTH_SHORT).show();

                Animation slide = AnimationUtils.loadAnimation(this, R.anim.slide_in);
//                imageView.startAnimation(slide);
//                imageView.setVisibility(View.VISIBLE);
            }
        }
//        if (v == pickStartDate) {
//            final Calendar c = Calendar.getInstance();
//            mYear = c.get(Calendar.YEAR);
//            mMonth = c.get(Calendar.MONTH);
//            mDay = c.get(Calendar.DAY_OF_MONTH);
//            DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
//                @Override
//                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                    dateInput.getEditText().setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
//                }
//            }, mYear, mMonth, mDay);
//            datePickerDialog.setTitle("Pick Start Date");
//            datePickerDialog.show();
//        }

    }

    public void slideUp(View view) {
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                view.getHeight(),  // fromYDelta
                0);                // toYDelta
        animate.setDuration(500);
        view.startAnimation(animate);
    }


    // slide the view from its current position to below itself
    public void slideDown(View view) {
        view.setVisibility(View.INVISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                view.getHeight()); // toYDelta
        animate.setDuration(500);
        view.startAnimation(animate);
    }


    public void sendAlert(View view) {
        if (isValid() == true) {
            final ProgressDialog mDialog = new ProgressDialog(SendAlert.this);
            mDialog.setMessage("please wait...");

            mDialog.show();
           // dialog.show();

            String aName = alertName.getEditText().getText().toString() + " Alert";
            String sDate = aDate.getEditText().getText().toString();
            String address = edtAddress.getEditText().getText().toString();
            aBloodGroup = spinnerBloodGroup.getSelectedItem().toString();

            sendAlert = FirebaseDatabase.getInstance().getReference();

            final String alertId = sendAlert.push().getKey();

            DatabaseReference alerts = FirebaseDatabase.getInstance().getReference();

            final AlertModel alertModel = new AlertModel();

            alertModel.setName(aName);
            alertModel.setBloodGroup(aBloodGroup);
            alertModel.setCity(address);
            alertModel.setDate(sDate);
            alertModel.setAlertId(alertId);
            alertModel.setaId(FirebaseAuth.getInstance().getCurrentUser().getUid());


            alerts.child("Alerts").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(alertModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        //dialog.dismiss();
                        mDialog.dismiss();
                        Snackbar.make(findViewById(R.id.recyclerViewAlert), "Successfully Alert Sent", Snackbar.LENGTH_SHORT).show();

                        //todo notification to user about the alert




                        startActivity(new Intent(SendAlert.this, SendAlert.class));
                        finish();
                    } else {
                        Snackbar.make(findViewById(R.id.recyclerViewAlert), "Error! Please Try Again.", Snackbar.LENGTH_SHORT).show();
                    }
                }
            });


        } else {
            Snackbar.make(findViewById(R.id.recyclerViewAlert), "Error! Please Fill The Required Fields", Snackbar.LENGTH_LONG).show();
        }
    }

    public boolean isValid() {
        if (alertName.getEditText().getText().toString().isEmpty()) {
            alertName.setError("Alert Name Required");
            return false;
        } else if (aDate.getEditText().getText().toString().isEmpty()) {
            aDate.setError("Alert Sent Date Required");
            return false;
        } else if (edtAddress.getEditText().getText().toString().isEmpty()) {
            edtAddress.setError("Address Required");
            return false;
        }else {
            return true;
        }
    }
}