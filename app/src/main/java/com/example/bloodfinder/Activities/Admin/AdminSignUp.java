package com.example.bloodfinder.Activities.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
//import android.os.Message;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bloodfinder.Activities.MainActivity;
import com.example.bloodfinder.Activities.SignUp;
import com.example.bloodfinder.Model.Admin;
import com.example.bloodfinder.Model.UserModel;
import com.example.bloodfinder.R;
import com.example.bloodfinder.Recievers.NetworkReciever;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


//import papaya.in.sendmail.SendMail;

public class AdminSignUp extends AppCompatActivity {
    TextInputLayout edtAdminName, edtAdminEmail, edtAdminPassword, edtAdminCity, edtAdminPhone, edtAdminConfirmPassword;
    String edtName, edtEmail, edtPassword, edtCity, edtPhone, edtCPassword;
    // FirebaseAuth firebaseAuth;
    DatabaseReference adminReference;
    NetworkReciever networkReciever;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_sign_up);
        initViews();
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
        networkReciever = new NetworkReciever();

        Button adminBtnSingnup = findViewById(R.id.adminBtnSingnup);


        adminBtnSingnup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(edtAdminName.getEditText().getText().toString().isEmpty()){
//                   // Toast.makeText(getApplicationContext(), "Please Enter Your Name", Toast.LENGTH_SHORT).show();
//                    edtAdminName.setError("Please Enter Your Name");
//                }
//                if(edtAdminEmail.getEditText().getText().toString().isEmpty()){
//                    Toast.makeText(getApplicationContext(), "Please Enter Your Email Address", Toast.LENGTH_SHORT).show();
//                }
//                if(edtAdminPassword.getEditText().getText().toString().isEmpty()){
//                    Toast.makeText(getApplicationContext(), "Please Enter Your Password", Toast.LENGTH_SHORT).show();
//                }
//                if(edtAdminConfirmPassword.getEditText().getText().toString().isEmpty()){
//                    Toast.makeText(getApplicationContext(), "Please Enter Your Confirm Password", Toast.LENGTH_SHORT).show();
//                }

                //firebaseAuth = firebaseAuth.getInstance();


                if (isValid() == true) {
                    edtName = edtAdminName.getEditText().getText().toString();
                    edtEmail = edtAdminEmail.getEditText().getText().toString();
                    edtPassword = edtAdminPassword.getEditText().getText().toString();
                    edtCity = edtAdminCity.getEditText().getText().toString();
                    edtPhone = edtAdminPhone.getEditText().getText().toString();
                    edtCPassword = edtAdminConfirmPassword.getEditText().getText().toString();

                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

                    if (edtPassword.equals(edtCPassword)) {

                        final ProgressDialog mDialog = new ProgressDialog(AdminSignUp.this);
                        mDialog.setMessage("please wait...");
                        mDialog.show();

//                        firebaseAuth.createUserWithEmailAndPassword(edtEmail, edtPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                            @Override
//                            public void onComplete(@NonNull Task<AuthResult> task) {
//                                if(task.isSuccessful()){
////                                    Toast.makeText(getApplicationContext(), "Account Created Successfully!", Toast.LENGTH_SHORT).show();
////                                    Intent intent = new Intent(AdminSignUp.this, AdminDashboard.class);
////                                    startActivity(intent);
//
//                                    Toast.makeText(getApplicationContext(), "Account Created Successfully!", Toast.LENGTH_SHORT).show();
//                                    Admin Admin = new Admin();
//                                    Admin.setName(edtName);
//                                    Admin.setName(edtEmail);
//                                    Admin.setName(edtPassword);
//                                    Admin.setName(edtPhone);
//                                    Admin.setaId(firebaseAuth.getCurrentUser().getUid());
//                                    adminReference.child("Admin/Hospital").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                                            .setValue(Admin).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                @Override
//                                                public void onComplete(@NonNull Task<Void> task) {
//                                                    if(task.isSuccessful()){
//                                                        startActivity(new Intent(AdminSignUp.this, AdminMainActivity.class));
//                                                        finish();
//                                                    }else {
//                                                       // progressDialog.dismiss();
//                                                        //Snackbar.make(findViewById(R.id.parentLayout), "Sorry! Registration Failed ", Snackbar.LENGTH_SHORT).show();
//                                                        Toast.makeText(getApplicationContext(), "Sorry! Signup Failed ", Toast.LENGTH_SHORT).show();
//                                                    }
//                                                }
//                                            });
//                                }
////                                else{
////                                    Toast.makeText(getApplicationContext(),task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
////                                }
//                            }
//                        });



                        firebaseAuth.createUserWithEmailAndPassword(edtEmail, edtPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    String aId = task.getResult().getUser().getUid();


                                    Admin admin = new Admin(aId, edtName, edtCity, edtPhone, edtEmail, edtPassword, 0);


                                    firebaseDatabase.getReference().child("Admin-Hospital").child(aId).setValue(admin);



                                    String subject = "Blood Finder App: Hospital Account Registration Detail";
//                                    String message = "Dear "+edtName+",\n\n\n"+"I am writing to inform you that we have created an account to grant you access to our Blood Finder Application.\n\nThe login credentials are provided below: \n\n"+"<b>Email: </b> "+edtEmail+"\n"+"<b>Password: </b> "+edtPassword+"\nPlease use this information to login to our Application.\n\n\nThank You!";
                                    String message = "Dear "+edtName+",<br/><br/><br/>"+"I am writing to inform you that we have created an account to grant you access to our Blood Finder Application.<br/><br/>The login credentials are provided below: <br/><br/>"+"<b>Email: </b> "+edtEmail+"<br/>"+"<b>Password: </b> "+edtPassword+"<br/><br/>Please use this information to login to our Application.<br/><br/><br/>Thank You!";

                                    String senderEmail = "kalebamsalug@gmail.com";
                                    String senderPassword = "eunwnbpriacspveo";

                                    String host = "smtp.gmail.com";

                                    Properties properties = System.getProperties();

                                    properties.put("mail.smtp.auth", "true");
                                    properties.put("mail.smtp.starttls.enable", "true");
                                    properties.put("mail.smtp.host", host);
                                    properties.put("mail.smtp.port", "587");

                                    //todo signup sesion
                                    Session session = Session.getInstance(properties,
                                            new Authenticator() {
                                                @Override
                                                protected PasswordAuthentication getPasswordAuthentication() {
                                                    return new PasswordAuthentication(senderEmail, senderPassword);
                                                }
                                            });

                                    try{
                                        Message hMessage = new MimeMessage(session);
                                        hMessage.setFrom(new InternetAddress(senderEmail, "Blood Finder"));
                                        hMessage.setRecipients(Message.RecipientType.TO,InternetAddress.parse(edtEmail.toString()));
                                        hMessage.setSubject(subject);
                                       hMessage.setContent(message, "text/html; charset=utf-8");
                                        Transport.send(hMessage);

                                        mDialog.dismiss();
                                        Toast.makeText(AdminSignUp.this, "Successful! Email Sent to Hospital.", Toast.LENGTH_SHORT).show();
                                        Snackbar.make(findViewById(R.id.cardView), "Hospital Account Successfully Created!", Snackbar.LENGTH_SHORT).show();


                                    } catch (MessagingException | UnsupportedEncodingException e){
                                        throw new RuntimeException(e);
                                    }


//                                    properties.put("mail.smtp.host", host);
//                                    properties.put("mail.smtp.port", "4");
//                                    properties.put("mail.smtp.ssl.enable", "true");





//                                    Session session = Session.getInstance(properties, new Authenticator() {
//                                        @Override
//                                        protected PasswordAuthentication getPasswordAuthentication() {
//                                            return new PasswordAuthentication(senderEmail, senderPassword);
//                                        }
//                                    });

//                                    MimeMessage mimeMessage = new MimeMessage(session);

//                                    mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(edtEmail));

//
//                                    SendMail mail = new SendMail("kalebamsalug@gmail.com", "eunwnbpriacspveo",
//                                            edtEmail,
//                                            subject,
//                                            message);
//                                    mail.execute();

//                                    Snackbar.make(findViewById(R.id.cardView), "Hospital Account Successfully Created!", Snackbar.LENGTH_SHORT).show();



//                                    Intent intent = new Intent(AdminSignUp.this, AdminDashboard.class);
//                                    startActivity(intent);

                                } else {
                                    Toast.makeText(getApplicationContext(), task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        StrictMode.setThreadPolicy(policy);

                    } else {

                        Toast.makeText(getApplicationContext(), "Password and confirm password must be same", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Error! Fill Required Fields", Toast.LENGTH_SHORT).show();
                }

            }
        });


//        Button backToAdminLogin = findViewById(R.id.adminLoginPage);
//
//        backToAdminLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(AdminSignUp.this, AdminDashboard.class);
//                startActivity(intent);
//            }
//        });
    }

    private void initViews() {
        edtAdminName = findViewById(R.id.edtAdminName);
        edtAdminEmail = findViewById(R.id.edtAdminEmail);
        edtAdminPassword = findViewById(R.id.edtAdminPassword);
        edtAdminConfirmPassword = findViewById(R.id.edtAdminConfirmPassword);
        edtAdminCity = findViewById(R.id.edtAdminCity);
        edtAdminPhone = findViewById(R.id.edtAdminPhone);
        adminReference = FirebaseDatabase.getInstance().getReference();
    }

    public boolean isValid() {
        if (edtAdminName.getEditText().getText().toString().isEmpty()) {
            edtAdminName.setError("Full Name required");
            return false;
        } else if (edtAdminEmail.getEditText().getText().toString().isEmpty()) {
            edtAdminEmail.setError("Email is required");
            return false;
        } else if (edtAdminPassword.getEditText().getText().toString().isEmpty()) {
            edtAdminPassword.setError("Password is required");
            return false;
        } else if (edtAdminConfirmPassword.getEditText().getText().toString().isEmpty()) {
            edtAdminConfirmPassword.setError("Confirm Password is required");
            return false;
        } else if (edtAdminCity.getEditText().getText().toString().isEmpty()) {
            edtAdminCity.setError("Hospital city is required");
            return false;
        } else if (edtAdminPhone.getEditText().getText().toString().isEmpty()) {
            edtAdminPhone.setError("Phone number is required");
            return false;
        } else {
            return true;
        }
    }
}