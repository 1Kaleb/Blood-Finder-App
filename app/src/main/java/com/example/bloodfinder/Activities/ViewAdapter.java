package com.example.bloodfinder.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bloodfinder.Activities.MainActivity;
import com.example.bloodfinder.Model.DonationModel;
import com.example.bloodfinder.Model.HospitalModel;
import com.example.bloodfinder.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViewAdapter extends RecyclerView.Adapter<ViewAdapter.ViewHolder> {

    ArrayList<HospitalModel> arrayList;
    Context context;
    LinearLayout callHospital, deleteHospital, editHospital;
    DatabaseReference dbr, dbref;
    Button adminUpdateBtn;
    //    EditText edtAdminCity, edtAdminName, edtAdminPhone;
    TextInputLayout edtAdminCity, edtAdminName, edtAdminPhone;

    String hName, hCity, hPhone;

    public ViewAdapter(ArrayList<HospitalModel> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cutome_user_view_hospital, parent, false);
        return new ViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        HospitalModel hModel = arrayList.get(position);
        holder.name.setText(hModel.getName());
        holder.email.setText(hModel.getEmail());
        holder.phone.setText(hModel.getPhoneNumber());
        holder.city.setText(hModel.getCity());

        makeCall(hModel.getPhoneNumber());


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void searchHospital(ArrayList<HospitalModel> hospitalList) {
        arrayList = hospitalList;
        notifyDataSetChanged();
    }


    private void makeCall(final String number) {
        callHospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + number));
                context.startActivity(intent);
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, email, phone, city;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.hName);
            email = itemView.findViewById(R.id.hEmail);
            phone = itemView.findViewById(R.id.hPhone);
            city = itemView.findViewById(R.id.hCity);
            callHospital = itemView.findViewById(R.id.makeHCall);

            adminUpdateBtn = itemView.findViewById(R.id.adminUpdateBtn);


        }
    }


}
