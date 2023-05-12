package com.wavesciences.phonearray.helpers;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import java.util.Arrays;


public class PermissionsHelper {


    final Activity callingActivity;

    public PermissionsHelper(Activity callingActivity){
        this.callingActivity = callingActivity;
    }


    /**
     * Simple callback on the permission check
     */
    public interface PermissionRequestCallback{
        void onPermissionsGranted();
        void onPermissionDenied(String permission);
    }

    private static final String TAG = PermissionsHelper.class.getSimpleName();

    static final String[] PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    static final String[] PERMISSIONS_RATIONALE = {
            "This permission is required to record the audio.",
            "This permission is required to save recorded audio to the device.",
    };

    static final Boolean[] PERMISSIONS_GRANTED = new Boolean[PERMISSIONS.length];


    /**
     * Checks all of the permissions required by the application.
     * @param callback called when the permission check is complete.
     */
    public void checkPermissions(PermissionRequestCallback callback){
        checkPermission(0,  callback);
        Arrays.fill(PERMISSIONS_GRANTED, false);
    }

    private void checkPermission(int index, PermissionRequestCallback callback){
        if (index == PERMISSIONS.length){

            boolean hasAllPermissions = true;
            for (Boolean b: PERMISSIONS_GRANTED) if (!b) {
                hasAllPermissions = false;
                break;
            }

            Log.i(TAG, "Permission Check Complete:" + hasAllPermissions);
            if (hasAllPermissions) {
                Log.d(TAG, "[checkPermissions] App has all Permissions");
                if (callback != null) callback.onPermissionsGranted();
            } else {
                Log.w(TAG, "One or more permissions are missing.");
                for (int i=0; i < PERMISSIONS.length; i++){
                    Log.i(TAG, "[checkPermission] Permission Status: " + PERMISSIONS[i] + ": " + PERMISSIONS_GRANTED[i]);
                    if (!PERMISSIONS_GRANTED[i]) callback.onPermissionDenied(PERMISSIONS[i]);
                }

            }
            return;
        }

        if (ContextCompat.checkSelfPermission(callingActivity.getApplicationContext(), PERMISSIONS[index]) != PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(callingActivity, PERMISSIONS[index])) {
                Log.i(TAG, "[checkPermissions]  Presenting Permission Rationale for:" + PERMISSIONS[index]);
                showExplanation(PERMISSIONS_RATIONALE[index], PERMISSIONS[index], index);
            } else {
                Log.i(TAG, "[checkPermissions]  requestPermissions for:" + PERMISSIONS[index]);
                ActivityCompat.requestPermissions(callingActivity, new String[]{PERMISSIONS[index]}, index);
            }
        } else {
            //Permission already granted
            Log.i(TAG, "[checkPermissions] Permission already granted: index:" + PERMISSIONS[index]);
            PERMISSIONS_GRANTED[index] = true;
            checkPermission(index+1, callback);
        }

    }

    private void showExplanation(String message,
                                 final String permission,
                                 final int permissionRequestCode) {

        String title = permission.substring(permission.lastIndexOf(".")+1);
        AlertDialog.Builder builder = new AlertDialog.Builder(callingActivity.getApplicationContext());
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, (dialog, id) -> requestPermission(permission, permissionRequestCode));

        builder.create().show();
    }


    private void requestPermission(String permissionName, int permissionRequestCode) {
        Log.i(TAG, "[requestPermission] Requesting permission for " + permissionName);
        ActivityCompat.requestPermissions(callingActivity,
                new String[]{permissionName}, permissionRequestCode);
    }

}
