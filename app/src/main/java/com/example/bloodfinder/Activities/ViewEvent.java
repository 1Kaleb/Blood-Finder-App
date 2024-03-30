package com.example.bloodfinder.Activities;

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

import com.example.bloodfinder.Activities.Admin.AdminDashboard;
import com.example.bloodfinder.Activities.Admin.AdminProfile;
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

public class ViewEvent extends AppCompatActivity {
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
        setContentView(R.layout.activity_view_event);
        initViews();
        getEvents();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

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
                View mView = LayoutInflater.from(ViewEvent.this).inflate(R.layout.custome_add_event_layout, parent, false);
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
        sweetAlertDialog = new SweetAlertDialog(ViewEvent.this, SweetAlertDialog.PROGRESS_TYPE);
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


}