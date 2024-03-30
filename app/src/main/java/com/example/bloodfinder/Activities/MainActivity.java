package com.example.bloodfinder.Activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bloodfinder.Activities.Admin.AdminDashboard;
import com.example.bloodfinder.Activities.Admin.AdminMainActivity;
import com.example.bloodfinder.Activities.Hospital.HospitalDashboard;
import com.example.bloodfinder.R;
import com.example.bloodfinder.Utils.FirebaseOffline;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 100;
    TextInputLayout edtEmail, edtPassword, forgotEmail;
    //new
    TextView forgotPassword, adminLogin;
    Button btnLogin;
    LinearLayout googleLogin, twitterLogin;
    TextView errorText, linkCreateAccount;
    // linkForgotPassword;
    FirebaseAuth firebaseAuth;
    Dialog progressDialog;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    DatabaseReference userRefrenc, dbr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //todo check .setPersistenceEnabled(true);
        // FirebaseOffline.getSync();
        mAuth = FirebaseAuth.getInstance();
        initViews();
        login();
        navigateToRegister();
        navigateToAdminPage();
        createRequest();
        //navigateToForgotPassword();

        //new

//        if(FirebaseAuth.getInstance().getCurrentUser() != null){
//            dbr = FirebaseDatabase.getInstance().getReference();
//            dbr.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
//                                                @Override
//                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                                    if (snapshot.hasChildren()) {
////                                                        String uName = snapshot.child("name").getValue(String.class);
////                                                        System.out.println("awa " + uName);
//                                                        startActivity(new Intent(MainActivity.this, Dashboard.class));
//                                                        finish();
//                                                    }
//
//                                                }
//
//                                                @Override
//                                                public void onCancelled(@NonNull DatabaseError error) {
//
//                                                }
//                                            });
//
//            dbr = FirebaseDatabase.getInstance().getReference();
//            dbr.child("Admin-Hospital").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    if (snapshot.hasChildren()) {
//                                                        int role = snapshot.child("usertype").getValue(Integer.class);
//
//                                                        Intent a;
//                                                        switch (role){
//                                                            case 0:
//                                                                a = new Intent(MainActivity.this, HospitalDashboard.class);
//                                                                break;
//                                                           default:
//                                                                a = new Intent(MainActivity.this, AdminDashboard.class);
//                                                                break;
//                                                        }
//                                                        startActivity(a);
//
//                    }
//
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });
//        }
        forgotPassword = findViewById(R.id.forgot_password);

//        linkForgotPassword = (TextView) findViewById(R.id.linkForgotPassword);
//        linkForgotPassword.setOnClickListener((View.OnClickListener) this);

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            //// @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                View dialogView = getLayoutInflater().inflate(R.layout.activity_forgot_password, null);
                EditText emailBox = dialogView.findViewById(R.id.emailBox);
                builder.setView(dialogView);
                AlertDialog dialog = builder.create();
                dialogView.findViewById(R.id.btnReset).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String userEmail = emailBox.getText().toString();
                        if (TextUtils.isEmpty(userEmail) && !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                            Toast.makeText(MainActivity.this, "Enter your email used for registration", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(MainActivity.this, "Successful! Check your email.", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(MainActivity.this, "Error! Unable to send email.", Toast.LENGTH_SHORT).show();
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
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtpassword);
        // forgotEmail = findViewById(R.id.forgotEmail);
        btnLogin = findViewById(R.id.btnLogin);
//      twitterLogin = findViewById(R.id.twitterLogin);
        errorText = findViewById(R.id.errorText);
        linkCreateAccount = findViewById(R.id.linkCreateaccount);
        // linkForgotPassword = findViewById(R.id.linkForgotPassword);
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new Dialog(this);
        View mView = getLayoutInflater().inflate(R.layout.progress_dialog_wait, null);
        progressDialog.setContentView(mView);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        userRefrenc = FirebaseDatabase.getInstance().getReference();
        adminLogin = findViewById(R.id.adminLoginPage);
    }

    private void login() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //progressDialog.show();
                final ProgressDialog mDialog = new ProgressDialog(MainActivity.this);
                mDialog.setMessage("please wait...");

                mDialog.show();
//

                String email = edtEmail.getEditText().getText().toString();
                String password = edtPassword.getEditText().getText().toString();
                if (TextUtils.isEmpty(email)) {
                    edtEmail.setError("Enter email");
                } else if (TextUtils.isEmpty(password)) {
                    edtPassword.setError("Enter password");
                } else {
                    edtEmail.setError(null);
                    edtPassword.setError(null);

                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String uid = task.getResult().getUser().getUid();
                                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                                firebaseDatabase.getReference().child("Users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        //todo check this

                                        if (firebaseAuth.getCurrentUser().isEmailVerified()) {

                                            if (snapshot.hasChildren()) {
                                            int usertype = snapshot.child("userType").getValue(Integer.class);

                                            if (usertype == 2) {

//                                                if (firebaseAuth.getCurrentUser().isEmailVerified()) {


//                                                    Toast.makeText(MainActivity.this, "You are verified", Toast.LENGTH_SHORT).show();
                                                    String tokenID = FirebaseInstanceId.getInstance().getToken();
                                                    userRefrenc.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("tokenId").setValue(tokenID).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
//                                                                progressDialog.dismiss();
                                                                mDialog.dismiss();
                                                                startActivity(new Intent(MainActivity.this, Dashboard.class));
                                                                finish();
                                                            }
                                                        }
                                                    });







                                            } else {
//                                                    progressDialog.dismiss();
                                                mDialog.dismiss();
                                                Toast.makeText(getApplicationContext(), "This login page is only for users!", Toast.LENGTH_SHORT).show();

                                            }




                                            }
                                        }
                                        else {

                                             FirebaseAuth.getInstance().signOut();
                                            mDialog.dismiss();
//                                            startActivity(new Intent(MainActivity.this, MainActivity.class));
                                            Snackbar.make(findViewById(R.id.layoutTop), "This is only for user.\nIf you are user Please Verify your email.", Snackbar.LENGTH_LONG).show();

                                            // finish();

                                            // Snackbar.make(findViewById(R.id.layoutTop), "This login page is only for users!", Snackbar.LENGTH_LONG).show();
                                        }



                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            } else {
                                // progressDialog.dismiss();
                                mDialog.dismiss();
                                Snackbar.make(findViewById(R.id.layoutTop), "Error! Login Failed Check Email or Password", Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

//   private void login(){
//       btnLogin.setOnClickListener(new View.OnClickListener() {
//           @Override
//           public void onClick(View view) {
//               startActivity(new Intent(MainActivity.this, AdminDashboard.class));
//                                               finish();
//           }
//       });
//   }


    private void navigateToRegister() {
        linkCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SignUp.class));
                finish();
            }
        });
    }

    private void navigateToAdminPage() {
        adminLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AdminMainActivity.class));
                finish();
            }
        });
    }


//    private void navigateToForgotPassword() {
//        linkForgotPassword.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this, ForgotPassword.class));
//                finish();
//            }
//        });
//    }


    public void googleLogin(View view) {
//        progressDialog.show();
        signIn();
    }

    private void createRequest() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signIn() {
        progressDialog.dismiss();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
//            progressDialog.show();
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (completedTask.isSuccessful()) {
                progressDialog.dismiss();
                FirebaseUser user = mAuth.getCurrentUser();
                startActivity(new Intent(MainActivity.this, Setup_Profile.class));
                finish();
            }
        } catch (ApiException e) {
            Toast.makeText(this, e.getStatusCode(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    public void phoneLogin(View view) {
    }
}