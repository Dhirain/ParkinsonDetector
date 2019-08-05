package com.dj.parkinsondetector.permision;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;


/**
 * Created by Dhirain on 3/2/17.
 */

public class PermissionProviderImpl implements PermissionProvider {

    private static final String[] WRITE_EXTERNAL_STORAGE_PERMISSION = new String[]{

       Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static final String[] AUDIO_PERMISION = new String[]{

            Manifest.permission.RECORD_AUDIO
    };

    private static final String[] SEND_SMS = new String[]{
      Manifest.permission.SEND_SMS
    };

    private static final String[] FINE_LOCATION = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private static final String[] COARSE_LOCATION = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    private Context context;

    public PermissionProviderImpl(Context context) {
        this.context = context;
    }


    @Override
    public boolean hasWriteExternalStoragePermission() {

        return ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

    }

    @Override
    public void requestWriteExternalStoragePermission() {

        ActivityCompat.requestPermissions((Activity) context, WRITE_EXTERNAL_STORAGE_PERMISSION, REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION);

    }


    @Override
    public boolean hasWriteAudioPermission() {

        return ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;

    }

    @Override
    public void requestAudioPermission() {

        ActivityCompat.requestPermissions((Activity) context, AUDIO_PERMISION, REQUEST_AUDIO_PERMISION);

    }

    @Override
    public boolean hasSendSMSPermission() {

        return ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;

    }

    @Override
    public void requestSensSMSPermission() {

        ActivityCompat.requestPermissions((Activity) context, SEND_SMS, REQUEST_SEND_SMS);

    }

    @Override
    public boolean hasFineLocationPermission() {

        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

    }

    @Override
    public void requestFineLocationPermission() {

        ActivityCompat.requestPermissions((Activity) context, FINE_LOCATION, REQUEST_FINE_LOCATION);

    }

    @Override
    public boolean hasCoarseLocationPermission() {

        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

    }

    @Override
    public void requestCoarseLocationPermission() {

        ActivityCompat.requestPermissions((Activity) context, COARSE_LOCATION, REQUEST_COARSE_LOCATION);

    }


}
