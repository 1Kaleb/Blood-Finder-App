package com.example.bloodfinder.Activities.Admin;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
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
import android.widget.TimePicker;
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
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AddEvent extends AppCompatActivity implements View.OnClickListener {
    DatabaseReference addEvent, userRefrence, dbref, notificaitonRefrence;
    FirebaseAuth firebaseAuth;
    RecyclerView recyclerView;
    Toolbar toolbar;
    String city,userCity, uid, mycity;
    LinearLayout postBloodrequest, postdialog, pickStartDate, pickEndDate, pickStartTime, pickEndTime, btnCall;
    boolean isUp;
    int eYear, eMonth, eDay, mYear, mMonth, mDay;;
    TextInputLayout eventName, edtAddress, ePhone, eDescription, eStartDate, eEndDate, eStartTime, eEndTime;
    DatabaseReference postEvent;
    Dialog dialog;
    FloatingActionButton togglePost;
    SweetAlertDialog sweetAlertDialog;
    ImageView imageView;
    Button btnPostEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        initViews();
        getEvents();
        // selectBloodGroup();
//      getCurrentUserDetails();
        //getLocaiton();
        // getcityName();
        //getCurrentLocation();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        postdialog.setVisibility(View.INVISIBLE);
        isUp = false;
        togglePost.setOnClickListener(this);
//        pickDate.setOnClickListener(this);
//        pickTime.setOnClickListener(this);
        //pickLocation.setOnClickListener(this);
//        imageView.setOnClickListener(this);
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


//    @SuppressLint("MissingPermission")
//    private void getcityName() {
//        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 5, AddEvent.this);
//    }

//    @Override
//    public void onLocationChanged(@NonNull Location location) {
//        sweetAlertDialog.dismissWithAnimation();
//        try {
//            Geocoder geocoder = new Geocoder(AddEvent.this, Locale.getDefault());
//            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
//            String _addres = addresses.get(0).getAddressLine(0);
//            mycity = addresses.get(0).getLocality();
//            edtAddress.getEditText().setText(_addres);
//            getEvents(mycity);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
    private void getEvents() {

        Query query = addEvent.child("Events").orderByChild("city");
        FirebaseRecyclerOptions<EventModel> options =
                new FirebaseRecyclerOptions.Builder<EventModel>().setQuery(query, EventModel.class).build();
        FirebaseRecyclerAdapter<EventModel, EventViewHolder> adapter = new FirebaseRecyclerAdapter<EventModel, EventViewHolder>(options) {
            //@SuppressLint({"StaticFieldLeak", "RestrictedApi"})
            @Override
            protected void onBindViewHolder(@NonNull final EventViewHolder holder, int position, @NonNull final EventModel model) {
                holder.meventName.setText(model.getName());
                holder.mAddress.setText(model.getCity());
                holder.eSDate.setText(model.getStart_date());
                holder.eEDate.setText(model.getEnd_date());
                holder.eSTime.setText(model.getStart_time());
                holder.eETime.setText(model.getEnd_time());
                holder.mAddress.setText(model.getCity());
                holder.tvEventDesc.setText(model.getDescription());
//                makeCall(model.getPhone());

                String ePNumber = model.getPhone().toString();

                final String aId = model.getaId();
                if (aId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    holder.btnCallEvent.setVisibility(View.GONE);
                    holder.btnDeleteEvent.setVisibility(View.VISIBLE);
//                    btnPostEvent.setVisibility(View.VISIBLE);
                    //togglePost.setVisibility(View.VISIBLE);
                }

                holder.btnDeleteEvent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Snackbar.make(findViewById(R.id.recyclerViewEvent), "The Event has been deleted!", Snackbar.LENGTH_LONG).show();
                        addEvent.child("Events").child(model.getEventId()).removeValue();

                    }
                });

                holder.btnCallEvent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + ePNumber));
                        startActivity(intent);
                    }
                });

            }


            @NonNull
            @Override
            public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View mView = LayoutInflater.from(AddEvent.this).inflate(R.layout.custome_add_event_layout, parent, false);
                return new EventViewHolder(mView);
            }
        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }


    private void initViews() {
        togglePost = findViewById(R.id.btnToogleEpostshit);
        addEvent = FirebaseDatabase.getInstance().getReference();
        recyclerView = findViewById(R.id.recyclerViewEvent);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        toolbar = findViewById(R.id.toolbarRequests);
        sweetAlertDialog = new SweetAlertDialog(AddEvent.this, SweetAlertDialog.PROGRESS_TYPE);
        postdialog = findViewById(R.id.postEventDialog);
        edtAddress = findViewById(R.id.edtAddress);
        eventName = findViewById(R.id.edtEventName);
        ePhone = findViewById(R.id.edtEventPhone);
        eStartDate = findViewById(R.id.edtStartDate);
        eEndDate = findViewById(R.id.edtEndDate);
        eStartTime = findViewById(R.id.edtStartTime);
        eEndTime = findViewById(R.id.edtEndTime);
        dbref = FirebaseDatabase.getInstance().getReference();
        eDescription = findViewById(R.id.postEventDesc);
        btnPostEvent = findViewById(R.id.btnPostEvent);
        dialog = new Dialog(this);
        View view = getLayoutInflater().inflate(R.layout.progress_dialog_wait, null);
        dialog.setContentView(view);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //sweetAlertDialog.show();

        eStartDate.getEditText().setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
//                // Create a Calendar instance
//                final Calendar calendar = Calendar.getInstance();
//
//                // Create a DatePickerDialog
//                DatePickerDialog datePickerDialog = new DatePickerDialog(AddEvent.this,
//                        new DatePickerDialog.OnDateSetListener() {
//                            @Override
//                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
//                                // Set the selected date to the eStartDate TextInputLayout
//                                eStartDate.getEditText().setText(day + "/" + (month + 1) + "/" + year);
//                            }
//                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
//
//                // Show the DatePickerDialog
//                datePickerDialog.show();


                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddEvent.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        eStartDate.getEditText().setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.setTitle("Pick Date");
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
        });

// Set an OnClickListener for the eEndDate TextInputLayout
        eEndDate.getEditText().setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                // Create a Calendar instance
                final Calendar calendar = Calendar.getInstance();

                // Create a DatePickerDialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddEvent.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                // Set the selected date to the eEndDate TextInputLayout
                                eEndDate.getEditText().setText(day + "/" + (month + 1) + "/" + year);
                            }
                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

                // Show the DatePickerDialog
                datePickerDialog.show();
            }
        });


        //Time piker

        eStartTime.getEditText().setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                // Create a Calendar instance
                final Calendar calendar = Calendar.getInstance();

                // Create a TimePickerDialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddEvent.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                                // Set the selected time to the eStartTime TextInputLayout
                                eStartTime.getEditText().setText(String.format("%02d:%02d", hour, minute));
                            }
                        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);

                // Show the TimePickerDialog
                timePickerDialog.show();
            }
        });

        // Set an OnClickListener for the eEndTime TextInputLayout
        eEndTime.getEditText().setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                // Create a Calendar instance
                final Calendar calendar = Calendar.getInstance();

                // Create a TimePickerDialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddEvent.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                                // Set the selected time to the eEndTime TextInputLayout
                                eEndTime.getEditText().setText(String.format("%02d:%02d", hour, minute));
                            }
                        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);

                // Show the TimePickerDialog
                timePickerDialog.show();
            }
        });
    }


    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView meventName, eSDate, eEDate, eSTime, eETime, mPhone, mAddress, tvDate, tvEventDesc;
        TextView btnCallEvent, btnDeleteEvent;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            meventName = itemView.findViewById(R.id.tvEventName);
            tvDate = itemView.findViewById(R.id.tvBloodPostDate);
            eSDate = itemView.findViewById(R.id.tvEventStartDate);
            eEDate = itemView.findViewById(R.id.tvEventEndDate);
            eSTime = itemView.findViewById(R.id.tvEventStartTime);
            eETime = itemView.findViewById(R.id.tvEventEndTime);
            mAddress = itemView.findViewById(R.id.tvAddres);
            btnCallEvent = itemView.findViewById(R.id.callEvent);
            btnDeleteEvent = itemView.findViewById(R.id.deleteEvent);
            tvEventDesc = itemView.findViewById(R.id.tvEventDecription);
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
//        if (v == pickEndDate) {
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
//            datePickerDialog.setTitle("Pick End Date");
//            datePickerDialog.show();
//        }
//        if (v == imageView) {
//            slideDown(postdialog);
//            Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_out);
//            imageView.startAnimation(animation);
//            imageView.setVisibility(View.GONE);
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


    public void postEvent(View view) {
        if (isValid() == true) {

            final ProgressDialog mDialog = new ProgressDialog(AddEvent.this);
            mDialog.setMessage("please wait...");

            mDialog.show();

//            dialog.show();

            String eName = eventName.getEditText().getText().toString();
            String sDate = eStartDate.getEditText().getText().toString();
            String eDate = eEndDate.getEditText().getText().toString();
            String sTime = eStartTime.getEditText().getText().toString();
            String eTime = eEndTime.getEditText().getText().toString();
            String address = edtAddress.getEditText().getText().toString();
            String phone = ePhone.getEditText().getText().toString();
            String desc = eDescription.getEditText().getText().toString();
            postEvent = FirebaseDatabase.getInstance().getReference();

            final String eventId = postEvent.push().getKey();
//            postEvent = postEvent.child("Events").child(eventId);

            DatabaseReference events = FirebaseDatabase.getInstance().getReference();

            final EventModel eventModel = new EventModel();

            eventModel.setName(eName);
            eventModel.setCity(address);
            eventModel.setStart_date(sDate);
            eventModel.setEnd_date(eDate);
            eventModel.setStart_time(sTime);
            eventModel.setEnd_time(eTime);
            eventModel.setPhone(phone);
            eventModel.setDescription(desc);
            eventModel.setEventId(eventId);
            eventModel.setaId(FirebaseAuth.getInstance().getCurrentUser().getUid());


            events.child("Events").child(eventId).setValue(eventModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                   if (task.isSuccessful()){
                       mDialog.dismiss();
//                       dialog.dismiss();
                       Snackbar.make(findViewById(R.id.recyclerViewEvent), "Successfully Event Posted", Snackbar.LENGTH_SHORT).show();
                       startActivity(new Intent(AddEvent.this, AddEvent.class));
                       finish();
                   } else {
                       Snackbar.make(findViewById(R.id.recyclerViewEvent), "Error! Please Try Again.", Snackbar.LENGTH_SHORT).show();
                   }
                }
            });


//            events.child("Blood Events").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(eventId).setValue(eventModel).addOnCompleteListener(new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(@NonNull Task<Void> task) {
//                    if (task.isSuccessful())
//                        postEvent.child(eventId).setValue(eventModel).addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isSuccessful()) {
//                                    dialog.dismiss();
//                                    Snackbar.make(findViewById(R.id.postDialog), "Success! Blood Collection Event Have Been posted", Snackbar.LENGTH_LONG).show();
//                                    slideDown(postdialog);
//                                    Animation slide = AnimationUtils.loadAnimation(AddEvent.this, R.anim.slide_in);
////                                    imageView.startAnimation(slide);
////                                    imageView.setVisibility(View.VISIBLE);
//                                } else {
//                                    dialog.dismiss();
//                                    Snackbar.make(findViewById(R.id.postDialog), "Sorry! Blood Collection Event Not Posted", Snackbar.LENGTH_LONG).show();
//                                }
//                            }
//                        });
//                }
//            });





        } else {
            Snackbar.make(findViewById(R.id.recyclerViewEvent), "Error! Please Fill The Required Fields", Snackbar.LENGTH_LONG).show();
        }
    }

    public boolean isValid() {
        if (eventName.getEditText().getText().toString().isEmpty()) {
            eventName.setError("Event Name Required");
            return false;
        } else if (eStartDate.getEditText().getText().toString().isEmpty()) {
            eStartDate.setError("Start Date Required");
            return false;
        } else if (eEndDate.getEditText().getText().toString().isEmpty()) {
            eEndDate.setError("End Date Required");
            return false;
        } else if (eStartTime.getEditText().getText().toString().isEmpty()) {
            eStartTime.setError("Start Time Required");
            return false;
        } else if (eEndTime.getEditText().getText().toString().isEmpty()) {
            eEndTime.setError("End Time Required");
            return false;
        } else if (edtAddress.getEditText().getText().toString().isEmpty()) {
            edtAddress.setError("Address Required");
            return false;
        } else if (eDescription.getEditText().getText().toString().isEmpty()) {
            eDescription.setError("Description Required");
            return false;
        } else if (ePhone.getEditText().getText().toString().isEmpty()) {
            ePhone.setError("Phone Number Required");
            return false;
        } else {
            return true;
        }
    }
}