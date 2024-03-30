package com.example.bloodfinder.Activities.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bloodfinder.Activities.Dashboard;
import com.example.bloodfinder.Activities.Hospital.HospitalDashboard;
import com.example.bloodfinder.Activities.MainActivity;
import com.example.bloodfinder.R;
import com.example.bloodfinder.Recievers.NetworkReciever;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminMainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    TextInputLayout edtAdminPassword, edtAdminEmail;
    TextView forgotPassword, backtouser;
    String edtEmail, edtPassword;
    NetworkReciever networkReciever;
    // FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);
        initViews();
        networkReciever = new NetworkReciever();

        // firebaseDatabase.getReference().child("Node1").child("node2").setValue("NodeValue");
        // firebaseDatabase.getReference().child("Node").setValue("NodeValue");

        Button btnLogin = findViewById(R.id.adminBtnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValid() == true) {

                    final ProgressDialog mDialog = new ProgressDialog(AdminMainActivity.this);
                    mDialog.setMessage("please wait...");

                    mDialog.show();

                    edtEmail = edtAdminEmail.getEditText().getText().toString();
                    edtPassword = edtAdminPassword.getEditText().getText().toString();
                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    firebaseAuth.signInWithEmailAndPassword(edtEmail, edtPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                String aid = task.getResult().getUser().getUid();

                                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                                firebaseDatabase.getReference().child("Admin-Hospital").child(aid).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.hasChildren()) {
                                            int usertype = snapshot.child("usertype").getValue(Integer.class);

                                            if (usertype == 0) {
                                                mDialog.dismiss();
                                                Intent intent = new Intent(AdminMainActivity.this, HospitalDashboard.class);
                                                startActivity(intent);
                                                finish();
                                            } else if (usertype == 1) {
                                                mDialog.dismiss();

                                                Intent intent = new Intent(AdminMainActivity.this, AdminDashboard.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        } else {
                                            mDialog.dismiss();
                                            FirebaseAuth.getInstance().signOut();
                                            Toast.makeText(getApplicationContext(), "You are not allowed to login here!", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
//                                Intent intent = new Intent(AdminMainActivity.this, AdminDashboard.class);
//                                startActivity(intent);
                            } else {
                                mDialog.dismiss();
                                Toast.makeText(getApplicationContext(), task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Error! Fill Required Fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

//        Button adminCreateBtn = findViewById(R.id.adminBtnCreate);
//
//        adminCreateBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(AdminMainActivity.this, AdminSignUp.class);
//                startActivity(intent);
//            }
//        });


        backtouser = findViewById(R.id.ifURUser);

        backtouser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminMainActivity.this, MainActivity.class));
                finish();
            }
        });

        forgotPassword = findViewById(R.id.forgot_password);

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            //// @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(AdminMainActivity.this);
                View dialogView = getLayoutInflater().inflate(R.layout.activity_forgot_password, null);
                EditText emailBox = dialogView.findViewById(R.id.emailBox);
                builder.setView(dialogView);
                AlertDialog dialog = builder.create();
                dialogView.findViewById(R.id.btnReset).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String userEmail = emailBox.getText().toString();
                        if (TextUtils.isEmpty(userEmail) && !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                            Toast.makeText(AdminMainActivity.this, "Enter your registered email id", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(AdminMainActivity.this, "Successful! Check your email.", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(AdminMainActivity.this, "Error! Unable to reset Password.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                dialogView.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                if (dialog.getWindow() != null) {
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                dialog.show();
            }
        });
    }

    private void initViews() {
        edtAdminEmail = findViewById(R.id.edtAdminEmail);
        edtAdminPassword = findViewById(R.id.edtAdminPassword);
    }

    public boolean isValid() {
        if (edtAdminEmail.getEditText().getText().toString().isEmpty()) {
            edtAdminEmail.setError("Email is required");
            return false;
        } else if (edtAdminPassword.getEditText().getText().toString().isEmpty()) {
            edtAdminPassword.setError("Password is required");
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(AdminMainActivity.this, MainActivity.class));
        finish();
    }
}