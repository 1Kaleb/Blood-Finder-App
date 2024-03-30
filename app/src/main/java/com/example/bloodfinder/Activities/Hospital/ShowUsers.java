package com.example.bloodfinder.Activities.Hospital;

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
import com.example.bloodfinder.Model.UserModel;
import com.example.bloodfinder.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

public class ShowUsers extends AppCompatActivity {

    RecyclerView uRecyclerView;
    SearchView searchView;
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference reference = db.getReference().child("Users");
    Query uQuery = reference.orderByChild("verified").equalTo("null");
//Query uQuery = reference.orderByChild("name");
    // LinearLayout callHospital, deleteHospital;

    UserAdapter adapter;
    ArrayList<UserModel> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_users);

        uRecyclerView = findViewById(R.id.uRecycler_view);
        uRecyclerView.setHasFixedSize(true);
        uRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchView = findViewById(R.id.uSearch);
        searchView.clearFocus();

        list = new ArrayList<>();
        adapter = new UserAdapter(list, this);

        uRecyclerView.setAdapter(adapter);

        uQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    UserModel model = dataSnapshot.getValue(UserModel.class);

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
                usersList(newText);
                return true;
            }
        });

    }




    public void usersList (String text){
        ArrayList<UserModel> usersList = new ArrayList<>();
        for (UserModel userModel: list){
            if (userModel.getName().toLowerCase().contains(text.toLowerCase())){
                usersList.add(userModel);
            }
            adapter.searchUsers(usersList);

        }
    }
}