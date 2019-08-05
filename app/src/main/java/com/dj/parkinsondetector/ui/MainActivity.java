package com.dj.parkinsondetector.ui;

import android.Manifest;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.dj.parkinsondetector.R;
import com.dj.parkinsondetector.permision.PermissionProvider;
import com.dj.parkinsondetector.permision.PermissionProviderImpl;
import com.dj.parkinsondetector.utils.SharedPreferenceManager;
import com.dj.parkinsondetector.eventBus.ShakeEvent;
import com.dj.parkinsondetector.services.ShakeService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

import pl.bclogic.pulsator4droid.library.PulsatorLayout;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String PRIMARY_CONTACT = "PRIMARY_CONTACT";
    private static final String TAG = "MainActivity";
    private PulsatorLayout pulsatorLayout;
    private ImageView speakerIV;
    private FusedLocationProviderClient fusedLocationClient;
    private Location lastKnownLocation;
    private boolean isMessageSend = false;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUI();

        Intent intent = new Intent(this, ShakeService.class);
        startService(intent);
        fusedLocationClient = new FusedLocationProviderClient(this);
        checkPrimaryContact();
        checkPermision();
        getLocation();
        initMusic();
        //getLocation();
    }

    private void initMusic() {
        mediaPlayer = MediaPlayer.create(this, R.raw.alert);

    }


    private void getLocation() {
        //check permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 106);
        } else {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                lastKnownLocation = location;
                            }
                        }
                    });
            // already permission granted
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            Log.d(TAG, "onSuccess: first time location" + location.getLongitude() + " " + location.getLatitude());
                            lastKnownLocation = location;
                        }
                    }
                });
    }

    private void checkPrimaryContact() {
        String saveContact = SharedPreferenceManager.singleton().getString(PRIMARY_CONTACT);
        if (saveContact.equalsIgnoreCase("")) {
            showCustomDialog();
        }
    }

    private void setUI() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        pulsatorLayout = findViewById(R.id.pulsator);
        speakerIV = findViewById(R.id.speaker_iv);
        speakerIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakerIV.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.speaker_off));
                pulsatorLayout.stop();
                mediaPlayer.pause();
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_phone) {
            // Handle the camera action
            showCustomDialog();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void showCustomDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog);


        EditText editText = dialog.findViewById(R.id.primary_contact_ed);
        Button cancelButton = dialog.findViewById(R.id.dialog_cancel);
        Button okButton = dialog.findViewById(R.id.dialog_ok);

        String contact = SharedPreferenceManager.singleton().getString(PRIMARY_CONTACT);
        if (!contact.equalsIgnoreCase("")) {
            editText.setText(contact);
        }

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        okButton.setOnClickListener(v -> {
            String updatedContact = editText.getText().toString();
            if (!updatedContact.equalsIgnoreCase("")) {
                SharedPreferenceManager.singleton().save(PRIMARY_CONTACT, updatedContact);
            }
            dialog.dismiss();
        });

        dialog.show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShakeEvent(ShakeEvent event) {
        // TODO: 27/6/19 send sms to dependent
        speakerIV.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.speaker_on));
        pulsatorLayout.start();

        SmsManager smgr = SmsManager.getDefault();
        PendingIntent sentPI;

        String mobileNumber = SharedPreferenceManager.singleton().getString(PRIMARY_CONTACT);
        StringBuffer smsBody = new StringBuffer();

        smsBody.append("Test Alert: Parkinson detector found.   ");
        Log.d(TAG, "onShakeEvent: " + "sending message");
        if (lastKnownLocation != null) {

            smsBody.append("Last know location is: ");
            smsBody.append("http://maps.google.com?q=");
            smsBody.append(lastKnownLocation.getLatitude());
            smsBody.append(",");
            smsBody.append(lastKnownLocation.getLongitude());
            Log.d(TAG, "onShakeEvent: location :" + lastKnownLocation.getLatitude() + " " + lastKnownLocation.getLongitude());
        }
        if (!isMessageSend) {
            sentPI = PendingIntent.getBroadcast(this, 0, new Intent(smsBody.toString()), 0);
            smgr.sendTextMessage(mobileNumber, null, smsBody.toString(), sentPI, null);
            isMessageSend = true;

        }
        mediaPlayer.start();

    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        isMessageSend = false;
    }

    @Override
    public void onStop() {
        super.onStop();
        mediaPlayer.stop();
        EventBus.getDefault().unregister(this);
    }

    public void checkPermision() {
        PermissionProviderImpl provider = new PermissionProviderImpl(this);
        if (!provider.hasSendSMSPermission()) {
            provider.requestSensSMSPermission();
        }

        if (!provider.hasCoarseLocationPermission()) {
            provider.requestCoarseLocationPermission();
        }

        if (!provider.hasFineLocationPermission()) {
            provider.requestFineLocationPermission();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionProvider.REQUEST_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        getLocation();
                    }

                }
                return;
            }
            case PermissionProvider.REQUEST_COARSE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        getLocation();
                    }

                }
                return;
            }
        }
    }
}
