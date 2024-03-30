package com.example.bloodfinder.Fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bloodfinder.Activities.Dashboard;
import com.example.bloodfinder.Activities.Hospital.SendAlert;
import com.example.bloodfinder.Activities.MessageActivity;
import com.example.bloodfinder.Model.AlertModel;
import com.example.bloodfinder.Model.NotificaiotnsModel;
import com.example.bloodfinder.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class notificaitons extends Fragment {
    RecyclerView recyclerView, adminrecyclerView, recyclerViewAlert;
    Dialog dialog;
    String url;
    private String uName;
    TextView tvClear;
    DatabaseReference removeRefrence, addAlert;
    SweetAlertDialog pprogDialog;
    private String imageUrl;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_notificaitons, container, false);
        initViews(mView);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getnotfCount();
        getNotificatoins();
        getAlert();
        removeNotificaitons();
    }

    private void getnotfCount() {
        DatabaseReference count = FirebaseDatabase.getInstance().getReference("notifications");
        count.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    tvClear.setVisibility(View.VISIBLE);
                } else {
                    tvClear.setText("No Notifications");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void removeNotificaitons() {
        tvClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE);
                sweetAlertDialog.setTitleText("Are you sure");
                sweetAlertDialog.setContentText("Your notifications will be removed");
                sweetAlertDialog.setConfirmText("Yes! Remove");
                sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(final SweetAlertDialog sweetAlertDialog) {

                        removeRefrence = FirebaseDatabase.getInstance().getReference("notifications");
                        removeRefrence.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            sweetAlertDialog.dismissWithAnimation();
                                            Snackbar.make(tvClear, "Notifications Removed", Snackbar.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                        //todo clear alert from admin in recyclerViewEvent
                    }
                });
                sweetAlertDialog.show();
            }
        });

    }

    private void getNotificatoins() {
//        pprogDialog.show();


        DatabaseReference dbrefd = FirebaseDatabase.getInstance().getReference("notifications");
        FirebaseRecyclerOptions<NotificaiotnsModel> options = new FirebaseRecyclerOptions.Builder<NotificaiotnsModel>()
                .setQuery(dbrefd.child(FirebaseAuth.getInstance().getCurrentUser().getUid()), NotificaiotnsModel.class)
                .build();


        FirebaseRecyclerAdapter<NotificaiotnsModel, NotificationViewHolder> adapter = new FirebaseRecyclerAdapter<NotificaiotnsModel, NotificationViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final NotificationViewHolder holder, int position, @NonNull final NotificaiotnsModel model) {
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users");
                userRef.child(model.getFrom()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChildren()) {
                            uName = snapshot.child("name").getValue(String.class);
                            imageUrl = snapshot.child("imageUrl").getValue(String.class);
                            holder.tvName.setText(uName);
                            Picasso.get().load(imageUrl).into(holder.circleImageView);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

                holder.btnShowchat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Animation slide = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in);
                        holder.btnChat.startAnimation(slide);
                        holder.btnChat.setVisibility(View.VISIBLE);
                    }
                });

                holder.btnChat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), MessageActivity.class);
                        intent.putExtra("userId", model.getFrom());
                        intent.putExtra("name", uName);
                        intent.putExtra("imageUrl", imageUrl);
                        startActivity(intent);
                    }
                });

            }

            @NonNull
            @Override
            public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View mView = LayoutInflater.from(getContext()).inflate(R.layout.custom_notification_layout, parent, false);
                return new NotificationViewHolder(mView);
            }
        };
//        pprogDialog.dismissWithAnimation();
        adapter.startListening();
        recyclerView.setAdapter(adapter);

    }

    private void getAlert() {
        Query query = addAlert.child("Alerts").orderByChild("city");
        FirebaseRecyclerOptions<AlertModel> options =
                new FirebaseRecyclerOptions.Builder<AlertModel>().setQuery(query, AlertModel.class).build();
        FirebaseRecyclerAdapter<AlertModel, AlertViewHolder> adapterAlert = new FirebaseRecyclerAdapter<AlertModel, AlertViewHolder>(options) {
            @SuppressLint("StaticFieldLeak")
            @Override
            protected void onBindViewHolder(@NonNull final AlertViewHolder holder, int position, @NonNull final AlertModel model) {
                holder.malertName.setText(model.getName());
                holder.mAddress.setText(model.getCity());
                holder.aBGroup.setText(model.getBloodGroup());
                holder.aDate.setText(model.getDate());

            }


            @NonNull
            @Override
            public AlertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View mView = LayoutInflater.from(getContext()).inflate(R.layout.custom_alert, parent, false);
                return new AlertViewHolder(mView);
            }
        };

        adapterAlert.startListening();
        recyclerViewAlert.setAdapter(adapterAlert);

        if (adapterAlert.getItemCount() != 0){

            createNotificcation();
            // Create an explicit intent for an Activity in your app
//            Intent intent = new Intent(this.getContext(), notificaitons.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            PendingIntent pendingIntent = PendingIntent.getActivity(this.getContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);
//
//            NotificationCompat.Builder builder = new NotificationCompat.Builder(this.getContext(), "Blood Finder")
//                    .setSmallIcon(R.drawable.ic_notifications_24)
//                    .setContentTitle("Alert")
//                    .setContentText("Check your application. There is new alert!")
//                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                    // Set the intent that will fire when the user taps the notification
//                    .setContentIntent(pendingIntent)
//                    .setAutoCancel(true);
//
////            NotificationCompat.Builder builder = new NotificationCompat.Builder(this.getContext(), "blood finder")
////                    .setSmallIcon(R.drawable.ic_notifications_24)
////                    .setContentTitle("hi")
////                    .setContentText("hello")
////                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
//
//            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this.getContext());
//
//// notificationId is a unique int for each notification that you must define
//            //NotificationManagerCompat.notify(1)
//            notificationManager.notify(1, builder.build());
        }

    }

    private void createNotificcation(){
        String channelID = "01";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            CharSequence name = getString("Blood Finder");
//            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelID, "Blood Finder", importance);
//            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getContext().getSystemService(NotificationManager.class);


            Intent intent = new Intent(getContext(), notificaitons.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

            notificationManager.createNotificationChannel(channel);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), channelID)
                    .setSmallIcon(R.drawable.ic_notifications_24)
                    .setContentTitle("Alert")
                    .setContentText("Check your application. There is new alert!");
//                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    // Set the intent that will fire when the user taps the notification
//                    .setContentIntent(pendingIntent)
//                    .setAutoCancel(true);

           // NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
            notificationManager.notify(0, builder.build());
        }else{

            NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), "blood finder")
                    .setSmallIcon(R.drawable.ic_notifications_24)
                    .setContentTitle("hi")
                    .setContentText("hell00o")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());

// notificationId is a unique int for each notification that you must define
            //NotificationManagerCompat.notify(1)
            notificationManager.notify(1, builder.build());
        }
    }

    private void initViews(View mView) {
        tvClear = mView.findViewById(R.id.tvClearAll);
        recyclerView = mView.findViewById(R.id.recyclerViewNotification);
        recyclerViewAlert = mView.findViewById(R.id.recyclerViewAlert);
      //  adminrecyclerView = mView.findViewById(R.id.recyclerViewEvent);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewAlert.setLayoutManager(new LinearLayoutManager(getContext()));
        addAlert = FirebaseDatabase.getInstance().getReference();

        // alert from admin

//        adminrecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        pprogDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
//        pprogDialog.show();



    }

    class NotificationViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView tvName;
        LinearLayout btnChat, btnShowchat;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.userprofileImage);
            tvName = itemView.findViewById(R.id.tvuserNameNotificaiton);
            btnChat = itemView.findViewById(R.id.btnChat);
            btnShowchat = itemView.findViewById(R.id.btnShowChat);
        }
    }

    class AlertViewHolder extends RecyclerView.ViewHolder {
        TextView malertName, aDate, aBGroup, mAddress;

        public AlertViewHolder(@NonNull View itemView) {
            super(itemView);
            malertName = itemView.findViewById(R.id.tvAlertName);
            aDate = itemView.findViewById(R.id.tvAlertSentDate);
            aBGroup = itemView.findViewById(R.id.tvBloodType);
            mAddress = itemView.findViewById(R.id.tvAlertAddress);
        }
    }
}