package com.example.bloodfinder.Activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bloodfinder.R;
import com.example.bloodfinder.Recievers.NetworkReciever;

public class RecieverTest extends AppCompatActivity {
    NetworkReciever reciever;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reciever_test);
    }

}