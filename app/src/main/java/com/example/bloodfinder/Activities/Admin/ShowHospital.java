package com.example.bloodfinder.Activities.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.bloodfinder.Model.HospitalModel;
import com.example.bloodfinder.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

public class ShowHospital extends AppCompatActivity {

    RecyclerView hRecyclerView;
    SearchView searchView;
    //LinearLayout btnDelete;
    //DatabaseReference dbr;
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference reference = db.getReference().child("Admin-Hospital");
    Query hQuery = reference.orderByChild("usertype").equalTo(0);
   // LinearLayout callHospital, deleteHospital;
//   TextInputLayout edtAdminCity, edtAdminName, edtAdminPhone;

    Adapter adapter;
    ArrayList<HospitalModel> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_hospital);



        hRecyclerView = findViewById(R.id.recycler_view);
        hRecyclerView.setHasFixedSize(true);
        hRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchView = findViewById(R.id.hSearch);
        searchView.clearFocus();

//        edtAdminName = findViewById(R.id.edtUpdateAdminName);
//        edtAdminCity = findViewById(R.id.edtUpdateAdminCity);
//        edtAdminPhone = findViewById(R.id.edtUpdateAdminPhone);


//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);

        list = new ArrayList<>();
        adapter = new Adapter(list, this);

        hRecyclerView.setAdapter(adapter);

        hQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    HospitalModel model = dataSnapshot.getValue(HospitalModel.class);

                    list.add(model);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                hospitalList(newText);
                return true;
            }
        });

    }




    public void hospitalList (String text){
        ArrayList<HospitalModel> hospitalList = new ArrayList<>();
        for (HospitalModel hospitalModel: list){
            if (hospitalModel.getName().toLowerCase().contains(text.toLowerCase()) || hospitalModel.getCity().toLowerCase().contains(text.toLowerCase())) {
                hospitalList.add(hospitalModel);
            }
            adapter.searchHospital(hospitalList);

        }
    }
}