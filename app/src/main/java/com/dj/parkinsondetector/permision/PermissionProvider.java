package com.dj.parkinsondetector.permision;

/**
 * Created by DJ on 03-09-2017.
 */

public interface PermissionProvider {

    public static final int REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION = 103;

    public static final int REQUEST_AUDIO_PERMISION = 104;

    public static final int REQUEST_SEND_SMS = 105;

    public static final int REQUEST_FINE_LOCATION = 106;

    public static final int REQUEST_COARSE_LOCATION = 107;

    /**
     * Returns true if the app has permission to write to external storage.
     * @return
     */
    boolean hasWriteExternalStoragePermission();

    /**
     * Request permission to write to external storage.
     */
    void requestWriteExternalStoragePermission();

    boolean hasWriteAudioPermission();

    void requestAudioPermission();

    boolean hasSendSMSPermission();

    void requestSensSMSPermission();

    boolean hasFineLocationPermission();

    void requestFineLocationPermission();

    boolean hasCoarseLocationPermission();

    void requestCoarseLocationPermission();
}

