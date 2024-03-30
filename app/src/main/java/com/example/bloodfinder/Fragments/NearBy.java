package com.example.bloodfinder.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.bloodfinder.Activities.AvailableBlood;
import com.example.bloodfinder.Activities.Hospital.ShowVerifiedUsers;
import com.example.bloodfinder.Activities.MessageActivity;
import com.example.bloodfinder.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.LOCATION_SERVICE;

public class NearBy extends Fragment {
    LocationManager locationManager;
    double longitude, latitude;
    SweetAlertDialog progressDialog;
    DatabaseReference dbref;
    TextInputLayout edtMessage;
    TextView tvName, tvAddress, tvBloodGroup, tvDesc;
    CircleImageView infowindowImage;
    ImageView btnClose, btnSend;
    LinearLayout btnsendSms, btnChat, btnMakecall, smsContainer;
    private boolean isOpen;
    RelativeLayout relativeLayout;
    Spinner spinnerBloodGroup;
    Button btnFilter;
    String fBloodGroup;
    int selectedPosition = 0;
    int itemNum;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_my_map, container, false);
        relativeLayout = mView.findViewById(R.id.parentLayoutRelative);
        getCurrentLocations();
//        chooseBloodGroup();
        return mView;
    }

    private void spinnerListener() {

        spinnerBloodGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                bloodGroup.setText(spinnerBloodGroup.getSelectedItem().toString());
                selectedPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

//        private boolean chooseBloodGroup(){
//        final Dialog dialog = new Dialog(getContext());
//        View mView = getLayoutInflater().inflate(R.layout.map_select_blood_group, null);
//        dialog.setContentView(mView);
//        dialog.show();
//        initViews(mView);
//        spinnerBloodGroup = mView.findViewById(R.id.bloodGroups);
//
//        spinnerListener();
//
//        btnFilter = mView.findViewById(R.id.btnBloodFilter);
//
//        btnFilter.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int bloodNumberGroup = selectedPosition;
//
////                Toast.makeText(getContext().getApplicationContext(), "blood Selected "+bloodNumberGroup, Toast.LENGTH_SHORT).show();
//
//                dialog.dismiss();
//            }
//        });
//
//        return true;
//        }


    @SuppressLint("MissingPermission")
    private void getCurrentLocations() {
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            try {
                Criteria criteria = new Criteria();
//                progressDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
//                progressDialog.setTitle("Please Wait");
//                progressDialog.show();
                final ProgressDialog mDialog = new ProgressDialog(getActivity());
                mDialog.setMessage("please wait...");
                mDialog.show();

                String provider = locationManager.getBestProvider(criteria, true);
                Location location = locationManager.getLastKnownLocation(provider);
                if (location != null) {
//                    progressDialog.dismissWithAnimation();
                    mDialog.dismiss();
                    longitude = location.getLongitude();
                    latitude = location.getLatitude();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Request Locaiton Provide")
                    .setMessage("Please turn on device location ")
                    .setPositiveButton("Locaitons", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            getActivity().startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            getCurrentLocations();
                        }
                    })
                    .setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getCurrentLocations();
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }


    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        public void onMapReady(GoogleMap googleMap) {
            BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.curentlocation);
            Bitmap b = bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, 84, 84, false);
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//            try {
//                boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.mapstyle));
//            } catch (Exception e) {
//
//            }
            List<Address> addresses = null;
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());

            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);

                if (addresses != null && !addresses.isEmpty()) {
                    String city = addresses.get(0).getLocality();
                    LatLng marker = new LatLng(latitude, longitude);
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(marker, 15);
                    googleMap.animateCamera(cameraUpdate);
                    googleMap.addMarker(new MarkerOptions().position(marker).title("You are at " + addresses.get(0).getLocality()).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
                    nearByusers(googleMap, city);
                }
            } catch (IOException e) {
                e.printStackTrace();
            };

            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    final Dialog dialog = new Dialog(getContext());
                    View mView = getLayoutInflater().inflate(R.layout.custome_alert_action, null);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.setContentView(mView);
                    dialog.show();
                    initViews(mView);
                    getUserInfo(marker.getTitle());
                    btnClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    return true;
                }
            });
        }
    };

    private void getUserInfo(String pushId) {
        DatabaseReference donationsRef = FirebaseDatabase.getInstance().getReference("Donations").child(pushId);
        donationsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    tvName.setText(snapshot.child("name").getValue(String.class));
                    tvAddress.setText(snapshot.child("city").getValue(String.class));
                    tvBloodGroup.setText(snapshot.child("bloodGoup").getValue(String.class));
                    tvDesc.setText(snapshot.child("descriptions").getValue(String.class));
                    getImage(snapshot.child("userKey").getValue(String.class));
                    sendSms(snapshot.child("phone").getValue(String.class));
                    makeCall(snapshot.child("phone").getValue(String.class));
                    //chat(snapshot.child("userkey").getValue(String.class),snapshot.child("name").getValue(String.class));
                    sendSmsTouser(snapshot.child("phone").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void sendSmsTouser(final String phone) {
        btnsendSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent smsIntent = new Intent(android.content.Intent.ACTION_VIEW);
                smsIntent.setType("vnd.android-dir/mms-sms");
                smsIntent.putExtra("address",phone);
                smsIntent.putExtra("sms_body","Message from Blood Finder App:\n");
                startActivity(smsIntent);

//                try {
//                    String message = edtMessage.getEditText().getText().toString();
//                    SmsManager smsManager = SmsManager.getDefault();
//                    smsManager.sendTextMessage(phone, null, "Message from Blood Finder App:\n" + message, null, null);
//                    Snackbar.make(relativeLayout, "Message Sent.", Snackbar.LENGTH_SHORT).show();
//                } catch (Exception e) {
//                    Snackbar.make(relativeLayout, "Error! Message not Send.", Snackbar.LENGTH_SHORT).show();
//                }
            }
        });

    }

    private void makeCall(final String phone) {
        btnMakecall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phone));
                startActivity(intent);
            }
        });
    }

    private void sendSms(String phone) {
        btnsendSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askForSmsPermission();
            }
        });
    }
//    private void chat(String uid, String name) {
//        btnChat.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(uid)){
////                    Snackbar.make(getContext(), "You can't send message to yourself!", Snackbar.LENGTH_SHORT).show();
////                    Toast.makeText(getContext(), "You can't send message to yourself!", Toast.LENGTH_SHORT).show();
//
//                }else {
//                    Intent intent = new Intent(getContext(),MessageActivity.class);
////                    Intent intent = new Intent(getContext(), MessageActivity.class);
////                    startActivity(new Intent(getContext(), ShowVerifiedUsers.class));
//                    intent.putExtra("userId", uid);
//                    intent.putExtra("name", name);
//
//                    intent.putExtra("imageUrl", imageUrl);
////                    startActivity(intent);
//                    startActivity(intent);
//                }
//            }
//        });
//    }

    private void askForSmsPermission() {
        Dexter.withContext(getContext())
                .withPermission(Manifest.permission.SEND_SMS)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        isOpen = true;
                        if (isOpen) {
                            Animation aniFade = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
                            smsContainer.startAnimation(aniFade);
                            smsContainer.setVisibility(View.VISIBLE);
                        } else {
                            smsContainer.setVisibility(View.GONE);
                            Animation aniFadeout = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
                            smsContainer.startAnimation(aniFadeout);
                        }
                        isOpen = !isOpen;
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    private void getImage(String userKey) {
        DatabaseReference userImageRef = FirebaseDatabase.getInstance().getReference("Users").child(userKey);
        userImageRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String imageUrl = snapshot.child("imageUrl").getValue(String.class);
                Picasso.get().load(imageUrl).into(infowindowImage);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void initViews(View mView) {
        tvName = mView.findViewById(R.id.tvalertUsernmae);
        tvAddress = mView.findViewById(R.id.tvAlertAddress);
        tvBloodGroup = mView.findViewById(R.id.tvalertBloodroup);
        infowindowImage = mView.findViewById(R.id.alerDialogimage);
        edtMessage = mView.findViewById(R.id.edtAlertMessage);
        tvDesc = mView.findViewById(R.id.tvalertDesc);
        btnsendSms = mView.findViewById(R.id.sendSms);
        btnChat = mView.findViewById(R.id.btnChat);
        btnMakecall = mView.findViewById(R.id.makeCall);
        btnClose = mView.findViewById(R.id.btnClose);
        smsContainer = mView.findViewById(R.id.messageContainer);
        btnSend = mView.findViewById(R.id.btnSend);
    }


    private void nearByusers(final GoogleMap googleMap, String city) {

        dbref = FirebaseDatabase.getInstance().getReference("Donations");
        //  Query query = dbref.child("Donations").orderByChild("city").equalTo(city);
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        double lat = Double.parseDouble(ds.child("lat").getValue(String.class));
                        double lang = Double.parseDouble(ds.child("lang").getValue(String.class));
                        String bloodgroup = ds.child("bloodGoup").getValue(String.class);
                        String name = ds.child("name").getValue(String.class);
                        String pushId = ds.child("pushKey").getValue(String.class);
                        String address = ds.child("address").getValue(String.class);
                        LatLng latLng = new LatLng(lat, lang);
                        customeMarker(googleMap, bloodgroup, latLng, pushId);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }




    private void customeMarker(GoogleMap googleMap, String bloodgroup, LatLng latLng, String uId) {
        LatLng marker = latLng;

//        spinnerListener();
//
//                        btnFilter.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                int num = selectedPosition;
//                                Toast.makeText(getContext().getApplicationContext(), "blood Selected "+num, Toast.LENGTH_SHORT).show();
//                            }
//                        });


        final Dialog dialog = new Dialog(getContext());
        View mView = getLayoutInflater().inflate(R.layout.map_select_blood_group, null);
        dialog.setContentView(mView);
        dialog.show();
        initViews(mView);
        spinnerBloodGroup = mView.findViewById(R.id.bloodGroups);

        spinnerListener();

        btnFilter = mView.findViewById(R.id.btnBloodFilter);

        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int bloodNumberGroup = selectedPosition;

//                itemNum = bloodNumberGroup;

//                Toast.makeText(getContext().getApplicationContext(), "blood Selected "+bloodNumberGroup, Toast.LENGTH_SHORT).show();

                dialog.dismiss();

                if (bloodNumberGroup == 0) { // A+
                    switch (bloodgroup) {
                        case "A+":
                            BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.apositive);
                            Bitmap b = bitmapdraw.getBitmap();
                            Bitmap smallMarker = Bitmap.createScaledBitmap(b, 65, 84, false);
                            googleMap.addMarker(new MarkerOptions().position(marker).title(uId).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
                            break;

                        case "A-":
                            BitmapDrawable aneag = (BitmapDrawable) getResources().getDrawable(R.drawable.a_neagitive);
                            Bitmap A_neative = aneag.getBitmap();
                            Bitmap aneg_marker = Bitmap.createScaledBitmap(A_neative, 65, 84, false);
                            googleMap.addMarker(new MarkerOptions().position(marker).title(uId).icon(BitmapDescriptorFactory.fromBitmap(aneg_marker)));
                            break;

                        case "O+":
                            BitmapDrawable Opositive = (BitmapDrawable) getResources().getDrawable(R.drawable.o_positive);
                            Bitmap O_positiveBitmap = Opositive.getBitmap();
                            Bitmap oMarker = O_positiveBitmap.createScaledBitmap(O_positiveBitmap, 65, 84, false);
                            googleMap.addMarker(new MarkerOptions().position(marker).title(uId).icon(BitmapDescriptorFactory.fromBitmap(oMarker)));
                            break;

                        case "O-":
                            BitmapDrawable o_negative = (BitmapDrawable) getResources().getDrawable(R.drawable.o_negative);
                            Bitmap o_negbitmap = o_negative.getBitmap();
                            Bitmap oNmarker = o_negbitmap.createScaledBitmap(o_negbitmap, 65, 84, false);
                            googleMap.addMarker(new MarkerOptions().position(marker).title(uId).icon(BitmapDescriptorFactory.fromBitmap(oNmarker)));
                            break;
                    }
                } else if (bloodNumberGroup == 1) { // A-
                    switch (bloodgroup) {
                        case "A-":
                            BitmapDrawable aneag = (BitmapDrawable) getResources().getDrawable(R.drawable.a_neagitive);
                            Bitmap A_neative = aneag.getBitmap();
                            Bitmap aneg_marker = Bitmap.createScaledBitmap(A_neative, 65, 84, false);
                            googleMap.addMarker(new MarkerOptions().position(marker).title(uId).icon(BitmapDescriptorFactory.fromBitmap(aneg_marker)));
                            break;

                        case "O-":
                            BitmapDrawable o_negative = (BitmapDrawable) getResources().getDrawable(R.drawable.o_negative);
                            Bitmap o_negbitmap = o_negative.getBitmap();
                            Bitmap oNmarker = o_negbitmap.createScaledBitmap(o_negbitmap, 65, 84, false);
                            googleMap.addMarker(new MarkerOptions().position(marker).title(uId).icon(BitmapDescriptorFactory.fromBitmap(oNmarker)));
                            break;
                    }
                } else if (bloodNumberGroup == 2) { // B+
                    switch (bloodgroup) {
                        case "B+":
                            BitmapDrawable b_positive = (BitmapDrawable) getResources().getDrawable(R.drawable.b_positive);
                            Bitmap b_positivebitmap = b_positive.getBitmap();
                            Bitmap bPmarker = b_positivebitmap.createScaledBitmap(b_positivebitmap, 65, 84, false);
                            googleMap.addMarker(new MarkerOptions().position(marker).title(uId).icon(BitmapDescriptorFactory.fromBitmap(bPmarker)));
                            break;

                        case "B-":
                            BitmapDrawable b_negative = (BitmapDrawable) getResources().getDrawable(R.drawable.b_negative);
                            Bitmap b_negbitmap = b_negative.getBitmap();
                            Bitmap bmarker = b_negbitmap.createScaledBitmap(b_negbitmap, 65, 84, false);
                            googleMap.addMarker(new MarkerOptions().position(marker).title(uId).icon(BitmapDescriptorFactory.fromBitmap(bmarker)));
                            break;

                        case "O+":
                            BitmapDrawable Opositive = (BitmapDrawable) getResources().getDrawable(R.drawable.o_positive);
                            Bitmap O_positiveBitmap = Opositive.getBitmap();
                            Bitmap oMarker = O_positiveBitmap.createScaledBitmap(O_positiveBitmap, 65, 84, false);
                            googleMap.addMarker(new MarkerOptions().position(marker).title(uId).icon(BitmapDescriptorFactory.fromBitmap(oMarker)));
                            break;

                        case "O-":
                            BitmapDrawable o_negative = (BitmapDrawable) getResources().getDrawable(R.drawable.o_negative);
                            Bitmap o_negbitmap = o_negative.getBitmap();
                            Bitmap oNmarker = o_negbitmap.createScaledBitmap(o_negbitmap, 65, 84, false);
                            googleMap.addMarker(new MarkerOptions().position(marker).title(uId).icon(BitmapDescriptorFactory.fromBitmap(oNmarker)));
                            break;
                    }
                } else if (bloodNumberGroup == 3) { // B-
                    switch (bloodgroup) {

                        case "B-":
                            BitmapDrawable b_negative = (BitmapDrawable) getResources().getDrawable(R.drawable.b_negative);
                            Bitmap b_negbitmap = b_negative.getBitmap();
                            Bitmap bmarker = b_negbitmap.createScaledBitmap(b_negbitmap, 65, 84, false);
                            googleMap.addMarker(new MarkerOptions().position(marker).title(uId).icon(BitmapDescriptorFactory.fromBitmap(bmarker)));
                            break;


                        case "O-":
                            BitmapDrawable o_negative = (BitmapDrawable) getResources().getDrawable(R.drawable.o_negative);
                            Bitmap o_negbitmap = o_negative.getBitmap();
                            Bitmap oNmarker = o_negbitmap.createScaledBitmap(o_negbitmap, 65, 84, false);
                            googleMap.addMarker(new MarkerOptions().position(marker).title(uId).icon(BitmapDescriptorFactory.fromBitmap(oNmarker)));
                            break;
                    }
                } else if (bloodNumberGroup == 4) { // AB+
                    switch (bloodgroup) {
                        case "A+":
                            BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.apositive);
                            Bitmap b = bitmapdraw.getBitmap();
                            Bitmap smallMarker = Bitmap.createScaledBitmap(b, 65, 84, false);
                            googleMap.addMarker(new MarkerOptions().position(marker).title(uId).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
                            break;

                        case "A-":
                            BitmapDrawable aneag = (BitmapDrawable) getResources().getDrawable(R.drawable.a_neagitive);
                            Bitmap A_neative = aneag.getBitmap();
                            Bitmap aneg_marker = Bitmap.createScaledBitmap(A_neative, 65, 84, false);
                            googleMap.addMarker(new MarkerOptions().position(marker).title(uId).icon(BitmapDescriptorFactory.fromBitmap(aneg_marker)));
                            break;
                        case "B+":
                            BitmapDrawable b_positive = (BitmapDrawable) getResources().getDrawable(R.drawable.b_positive);
                            Bitmap b_positivebitmap = b_positive.getBitmap();
                            Bitmap bPmarker = b_positivebitmap.createScaledBitmap(b_positivebitmap, 65, 84, false);
                            googleMap.addMarker(new MarkerOptions().position(marker).title(uId).icon(BitmapDescriptorFactory.fromBitmap(bPmarker)));
                            break;

                        case "B-":
                            BitmapDrawable b_negative = (BitmapDrawable) getResources().getDrawable(R.drawable.b_negative);
                            Bitmap b_negbitmap = b_negative.getBitmap();
                            Bitmap bmarker = b_negbitmap.createScaledBitmap(b_negbitmap, 65, 84, false);
                            googleMap.addMarker(new MarkerOptions().position(marker).title(uId).icon(BitmapDescriptorFactory.fromBitmap(bmarker)));
                            break;
                        case "AB+":
                            BitmapDrawable ab_positive = (BitmapDrawable) getResources().getDrawable(R.drawable.ab_positive);
                            Bitmap ab_positivebitmap = ab_positive.getBitmap();
                            Bitmap abPmarker = ab_positivebitmap.createScaledBitmap(ab_positivebitmap, 65, 84, false);
                            googleMap.addMarker(new MarkerOptions().position(marker).title(uId).icon(BitmapDescriptorFactory.fromBitmap(abPmarker)));
                            break;

                        case "AB-":
                            BitmapDrawable ab_negative = (BitmapDrawable) getResources().getDrawable(R.drawable.ab_negative);
                            Bitmap ab_negbitmap = ab_negative.getBitmap();
                            Bitmap abmarker = ab_negbitmap.createScaledBitmap(ab_negbitmap, 65, 84, false);
                            googleMap.addMarker(new MarkerOptions().position(marker).title(uId).icon(BitmapDescriptorFactory.fromBitmap(abmarker)));
                            break;

                        case "O+":
                            BitmapDrawable Opositive = (BitmapDrawable) getResources().getDrawable(R.drawable.o_positive);
                            Bitmap O_positiveBitmap = Opositive.getBitmap();
                            Bitmap oMarker = O_positiveBitmap.createScaledBitmap(O_positiveBitmap, 65, 84, false);
                            googleMap.addMarker(new MarkerOptions().position(marker).title(uId).icon(BitmapDescriptorFactory.fromBitmap(oMarker)));
                            break;

                        case "O-":
                            BitmapDrawable o_negative = (BitmapDrawable) getResources().getDrawable(R.drawable.o_negative);
                            Bitmap o_negbitmap = o_negative.getBitmap();
                            Bitmap oNmarker = o_negbitmap.createScaledBitmap(o_negbitmap, 65, 84, false);
                            googleMap.addMarker(new MarkerOptions().position(marker).title(uId).icon(BitmapDescriptorFactory.fromBitmap(oNmarker)));
                            break;
                    }
                } else if (bloodNumberGroup == 5) { // AB-
                    switch (bloodgroup) {
                        case "A-":
                            BitmapDrawable aneag = (BitmapDrawable) getResources().getDrawable(R.drawable.a_neagitive);
                            Bitmap A_neative = aneag.getBitmap();
                            Bitmap aneg_marker = Bitmap.createScaledBitmap(A_neative, 65, 84, false);
                            googleMap.addMarker(new MarkerOptions().position(marker).title(uId).icon(BitmapDescriptorFactory.fromBitmap(aneg_marker)));
                            break;

                        case "B-":
                            BitmapDrawable b_negative = (BitmapDrawable) getResources().getDrawable(R.drawable.b_negative);
                            Bitmap b_negbitmap = b_negative.getBitmap();
                            Bitmap bmarker = b_negbitmap.createScaledBitmap(b_negbitmap, 65, 84, false);
                            googleMap.addMarker(new MarkerOptions().position(marker).title(uId).icon(BitmapDescriptorFactory.fromBitmap(bmarker)));
                            break;

                        case "AB-":
                            BitmapDrawable ab_negative = (BitmapDrawable) getResources().getDrawable(R.drawable.ab_negative);
                            Bitmap ab_negbitmap = ab_negative.getBitmap();
                            Bitmap abmarker = ab_negbitmap.createScaledBitmap(ab_negbitmap, 65, 84, false);
                            googleMap.addMarker(new MarkerOptions().position(marker).title(uId).icon(BitmapDescriptorFactory.fromBitmap(abmarker)));
                            break;

                        case "O-":
                            BitmapDrawable o_negative = (BitmapDrawable) getResources().getDrawable(R.drawable.o_negative);
                            Bitmap o_negbitmap = o_negative.getBitmap();
                            Bitmap oNmarker = o_negbitmap.createScaledBitmap(o_negbitmap, 65, 84, false);
                            googleMap.addMarker(new MarkerOptions().position(marker).title(uId).icon(BitmapDescriptorFactory.fromBitmap(oNmarker)));
                            break;
                    }
                } else if (bloodNumberGroup == 6) { // O+
                    switch (bloodgroup) {
                        case "O+":
                            BitmapDrawable Opositive = (BitmapDrawable) getResources().getDrawable(R.drawable.o_positive);
                            Bitmap O_positiveBitmap = Opositive.getBitmap();
                            Bitmap oMarker = O_positiveBitmap.createScaledBitmap(O_positiveBitmap, 65, 84, false);
                            googleMap.addMarker(new MarkerOptions().position(marker).title(uId).icon(BitmapDescriptorFactory.fromBitmap(oMarker)));
                            break;

                        case "O-":
                            BitmapDrawable o_negative = (BitmapDrawable) getResources().getDrawable(R.drawable.o_negative);
                            Bitmap o_negbitmap = o_negative.getBitmap();
                            Bitmap oNmarker = o_negbitmap.createScaledBitmap(o_negbitmap, 65, 84, false);
                            googleMap.addMarker(new MarkerOptions().position(marker).title(uId).icon(BitmapDescriptorFactory.fromBitmap(oNmarker)));
                            break;
                    }
                } else if (bloodNumberGroup == 7) { // O-
                    switch (bloodgroup) {
                        case "O-":
                            BitmapDrawable o_negative = (BitmapDrawable) getResources().getDrawable(R.drawable.o_negative);
                            Bitmap o_negbitmap = o_negative.getBitmap();
                            Bitmap oNmarker = o_negbitmap.createScaledBitmap(o_negbitmap, 65, 84, false);
                            googleMap.addMarker(new MarkerOptions().position(marker).title(uId).icon(BitmapDescriptorFactory.fromBitmap(oNmarker)));
                            break;

                    }

                }




            }
        });




//        switch (bloodgroup) {
//            case "A+":
//                BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.apositive);
//                Bitmap b = bitmapdraw.getBitmap();
//                Bitmap smallMarker = Bitmap.createScaledBitmap(b, 65, 84, false);
//                googleMap.addMarker(new MarkerOptions().position(marker).title(uId).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
//                break;
//
//            case "A-":
//                BitmapDrawable aneag = (BitmapDrawable) getResources().getDrawable(R.drawable.a_neagitive);
//                Bitmap A_neative = aneag.getBitmap();
//                Bitmap aneg_marker = Bitmap.createScaledBitmap(A_neative, 65, 84, false);
//                googleMap.addMarker(new MarkerOptions().position(marker).title(uId).icon(BitmapDescriptorFactory.fromBitmap(aneg_marker)));
//                break;
//            case "B+":
//                BitmapDrawable b_positive = (BitmapDrawable) getResources().getDrawable(R.drawable.b_positive);
//                Bitmap b_positivebitmap = b_positive.getBitmap();
//                Bitmap bPmarker = b_positivebitmap.createScaledBitmap(b_positivebitmap, 65, 84, false);
//                googleMap.addMarker(new MarkerOptions().position(marker).title(uId).icon(BitmapDescriptorFactory.fromBitmap(bPmarker)));
//                break;
//
//            case "B-":
//                BitmapDrawable b_negative = (BitmapDrawable) getResources().getDrawable(R.drawable.b_negative);
//                Bitmap b_negbitmap = b_negative.getBitmap();
//                Bitmap bmarker = b_negbitmap.createScaledBitmap(b_negbitmap, 65, 84, false);
//                googleMap.addMarker(new MarkerOptions().position(marker).title(uId).icon(BitmapDescriptorFactory.fromBitmap(bmarker)));
//                break;
//            case "AB+":
//                BitmapDrawable ab_positive = (BitmapDrawable) getResources().getDrawable(R.drawable.ab_positive);
//                Bitmap ab_positivebitmap = ab_positive.getBitmap();
//                Bitmap abPmarker = ab_positivebitmap.createScaledBitmap(ab_positivebitmap, 65, 84, false);
//                googleMap.addMarker(new MarkerOptions().position(marker).title(uId).icon(BitmapDescriptorFactory.fromBitmap(abPmarker)));
//                break;
//
//            case "AB-":
//                BitmapDrawable ab_negative = (BitmapDrawable) getResources().getDrawable(R.drawable.ab_negative);
//                Bitmap ab_negbitmap = ab_negative.getBitmap();
//                Bitmap abmarker = ab_negbitmap.createScaledBitmap(ab_negbitmap, 65, 84, false);
//                googleMap.addMarker(new MarkerOptions().position(marker).title(uId).icon(BitmapDescriptorFactory.fromBitmap(abmarker)));
//                break;
//
//            case "O+":
//                BitmapDrawable Opositive = (BitmapDrawable) getResources().getDrawable(R.drawable.o_positive);
//                Bitmap O_positiveBitmap = Opositive.getBitmap();
//                Bitmap oMarker = O_positiveBitmap.createScaledBitmap(O_positiveBitmap, 65, 84, false);
//                googleMap.addMarker(new MarkerOptions().position(marker).title(uId).icon(BitmapDescriptorFactory.fromBitmap(oMarker)));
//                break;
//
//            case "O-":
//                BitmapDrawable o_negative = (BitmapDrawable) getResources().getDrawable(R.drawable.o_negative);
//                Bitmap o_negbitmap = o_negative.getBitmap();
//                Bitmap oNmarker = o_negbitmap.createScaledBitmap(o_negbitmap, 65, 84, false);
//                googleMap.addMarker(new MarkerOptions().position(marker).title(uId).icon(BitmapDescriptorFactory.fromBitmap(oNmarker)));
//                break;
//        }
    }
}

