package com.example.bloodfinder.Activities;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static java.lang.String.valueOf;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bloodfinder.Model.DonationModel;
import com.example.bloodfinder.Model.ReminderModel;
import com.example.bloodfinder.Model.UserModel;
import com.example.bloodfinder.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Reminder extends AppCompatActivity {
    CalendarView calendarView;
    int day, mMonth, mHour, mMinutes, mSeconds, mYear;
    TextInputLayout edtTitle, edtReminderDate, edtReminderDesc;
    String edtNTitle, edtNReminderDate, edtNReminderDesc;
    Button btnCreateReminder, btnNextReminder;
    ProgressBar progbarReminder;
    private String selectedDate;
    RecyclerView recyclerView;
    TextView tvNextDont, tvNextReminder;
    DatabaseReference remDBref, dbref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        initViews();
        getDateCalander();
        setNextDonation();
        getReminderList();

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void setNextDonation() {

        edtNTitle = "Next Donation Date";
        Calendar c = Calendar.getInstance();

        c.add(Calendar.DATE, 90);

        // Date next =c.getTime();


        String next = DateFormat.getDateInstance(DateFormat.DATE_FIELD).format(c.getTime());

        edtNReminderDate = next;
        edtNReminderDesc = "You can donate from this day on. Thank you for your good deed!";

        btnNextReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //progbarReminder.setVisibility(View.VISIBLE);
                DatabaseReference reminderRef = FirebaseDatabase.getInstance().getReference("Reminders");
                String key = reminderRef.push().getKey();
                ReminderModel reminderModel = new ReminderModel();
                reminderModel.setTitle(edtNTitle);
                reminderModel.setDate(edtNReminderDate);
                reminderModel.setDescription(edtNReminderDesc);
                reminderModel.setKey(key);
                reminderRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(key).setValue(reminderModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
//                        progbarReminder.setVisibility(View.GONE);

                        //fix the button to go forever 90 days

                        /// Add number of donation with every click


                        //todo addreminder clicked fix

                        tvNextDont.setVisibility(VISIBLE);
                        btnNextReminder.setVisibility(GONE);
                        tvNextReminder.setVisibility(GONE);

                        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

                        FirebaseDatabase.getInstance().getReference().child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                Map<String, Object> map = new HashMap<>();

                                map.put("lastDonation", date);


                                remDBref.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {


                                            // delete donation post when user add reminder

                                            final DatabaseReference dRef = FirebaseDatabase.getInstance().getReference().child("Donations");
                                            dRef.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                        String donationkey = snapshot.child("userKey").getValue(String.class);
                                                        String pushKey = snapshot.child("pushKey").getValue(String.class);


                                                        //delete post


                                                        FirebaseDatabase.getInstance().getReference().child("Donations").addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                                dbref.child("Donations").child(pushKey).removeValue();
                                                                Toast.makeText(Reminder.this, "Your donation post is removed!", Toast.LENGTH_SHORT).show();

                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });


                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });


                                            Snackbar.make(findViewById(R.id.recyclerviewReminder), "Reminder Added!", Snackbar.LENGTH_LONG).show();

                                        }
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

//                        UserModel userModel = new UserModel();
//                        DatabaseReference remRefrence = FirebaseDatabase.getInstance().getReference();
//
//                        userModel.setLastDonation(date);
//                        remRefrence.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(userModel)


//                        clearFields();
                    }
                });
            }
        });
    }

    private void getReminderList() {
        DatabaseReference remidnerRef = FirebaseDatabase.getInstance().getReference("Reminders");
        remidnerRef = remidnerRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        FirebaseRecyclerOptions<ReminderModel> options = new FirebaseRecyclerOptions.Builder<ReminderModel>()
                .setQuery(remidnerRef, ReminderModel.class).build();
        FirebaseRecyclerAdapter<ReminderModel, ReminderViewHolder> reminderAdaptor = new FirebaseRecyclerAdapter<ReminderModel, ReminderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ReminderViewHolder holder, int position, @NonNull final ReminderModel model) {
                holder.tvRemidnerTitle.setText(model.getTitle());
                holder.tvReminderDate.setText(model.getDate());
                holder.tvReminderDesc.setText(model.getDescription());

                //todo delete reminder

//                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
//                    @Override
//                    public boolean onLongClick(View v) {
//                        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(Reminder.this, SweetAlertDialog.WARNING_TYPE);
//                        sweetAlertDialog.setTitleText("Confirmation");
//                        sweetAlertDialog.setContentText("Are your Sure to delete reminder");
//                        sweetAlertDialog.setConfirmText("Ok");
//                        sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                            @Override
//                            public void onClick(SweetAlertDialog sweetAlertDialog) {
//                                DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Reminders").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
//                                dbref.child(model.getKey()).removeValue();
//                                sweetAlertDialog.dismissWithAnimation();
//                            }
//                        });
//                        sweetAlertDialog.show();
//                        return false;
//                    }
//                });
            }

            @NonNull
            @Override
            public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View mView = LayoutInflater.from(Reminder.this).inflate(R.layout.custom_reminder_layout, parent, false);
                return new ReminderViewHolder(mView);
            }
        };
        reminderAdaptor.startListening();
        recyclerView.setAdapter(reminderAdaptor);

        if (reminderAdaptor.getItemCount() > 0) {
            tvNextDont.setVisibility(VISIBLE);
            btnNextReminder.setVisibility(GONE);
            tvNextReminder.setVisibility(GONE);
        }
    }

    private void getDateCalander() {

        //todo removed calander

//        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
//            @Override
//            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
//                calendarView.setDate(calendarView.getDate());
//                Calendar mcurrentTime = Calendar.getInstance();
//                final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
//                int minute = mcurrentTime.get(Calendar.MINUTE);
//                day = dayOfMonth;
//                mMonth = month + 1;
//                mYear = year;
//                selectedDate = mMonth + "/" + day + "/" + mYear;
////                mTimePicker = new TimePickerDialog(Reminder.this, new TimePickerDialog.OnTimeSetListener() {
////                    private TimePicker view;
////                    private int hourOfDay;
////                    private int minute;
////
////                    @Override
////                    public void onTimeSet(Ti mePicker view, int hourOfDay, int minute) {
////                        this.view = view;
////                        this.hourOfDay = hourOfDay;
////                        this.minute = minute;
////
////                        mHour = hour;
////                        mMinutes = minute;
////                    }
////
////                }, hour, minute, true);//Yes 24 hour time
////                mTimePicker.setTitle("Select Time");
////                mTimePicker.show();
//                final AlertDialog.Builder reminderDialog = new AlertDialog.Builder(Reminder.this);
//                View mView = getLayoutInflater().inflate(R.layout.custom_reminder_alert, null);
//                reminderDialog.setView(mView);
//                Dialog dialog = reminderDialog.create();
//                dialog.show();
//                getReminderDilogViews(mView);
//
//                edtReminderDate.getEditText().setText(selectedDate);
//                btnCreateReminder.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (isnotEmipt() == true) {
//                            progbarReminder.setVisibility(View.VISIBLE);
//                            DatabaseReference reminderRef = FirebaseDatabase.getInstance().getReference("Reminders");
//                            String key = reminderRef.push().getKey();
//                            ReminderModel reminderModel = new ReminderModel();
//                            reminderModel.setTitle(edtTitle.getEditText().getText().toString());
//                            reminderModel.setDate(edtReminderDate.getEditText().getText().toString());
//                            reminderModel.setDescription(edtReminderDesc.getEditText().getText().toString());
//                            reminderModel.setKey(key);
//                            reminderRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(key).setValue(reminderModel).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    progbarReminder.setVisibility(View.GONE);
//                                    clearFields();
//                                }
//                            });
//                        } else {
//                            Toast.makeText(Reminder.this, "Please Fill the Required Fields", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//            }
//        });

    }

    private void clearFields() {
        edtTitle.getEditText().setText("");
        edtReminderDate.getEditText().setText("");
        edtTitle.getEditText().setText("");
    }

    private void getReminderDilogViews(View mView) {
        edtTitle = mView.findViewById(R.id.reminderTitle);
        edtReminderDate = mView.findViewById(R.id.reminderDate);
        edtReminderDesc = mView.findViewById(R.id.reminderDescription);
        btnCreateReminder = mView.findViewById(R.id.btnCreateReminder);
        progbarReminder = mView.findViewById(R.id.progbarReminderAlert);
    }

    public boolean isnotEmipt() {
        if (!edtTitle.getEditText().getText().toString().isEmpty()) {
            return true;
        }
        if (!edtReminderDate.getEditText().getText().toString().isEmpty()) {
            return true;
        }
        if (!edtReminderDesc.getEditText().getText().toString().isEmpty()) {
            return true;
        } else {
            return false;
        }

    }

    private void initViews() {
//        calendarView = findViewById(R.id.calanderViewReminder);
        recyclerView = findViewById(R.id.recyclerviewReminder);
        btnNextReminder = findViewById(R.id.btnNextReminder);
        tvNextReminder = findViewById(R.id.tvNextReminder);
        tvNextDont = findViewById(R.id.tvNextDont);
        remDBref = FirebaseDatabase.getInstance().getReference();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dbref = FirebaseDatabase.getInstance().getReference();
    }

    public class ReminderViewHolder extends RecyclerView.ViewHolder {
        TextView tvRemidnerTitle, tvReminderDate, tvReminderDesc;

        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRemidnerTitle = itemView.findViewById(R.id.reminderLayoutTitle);
            tvReminderDate = itemView.findViewById(R.id.reminderLayoutDate);
            tvReminderDesc = itemView.findViewById(R.id.remidnerLayoutDescription);
        }
    }

}