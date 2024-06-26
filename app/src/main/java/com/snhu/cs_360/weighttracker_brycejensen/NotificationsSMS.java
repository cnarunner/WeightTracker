package com.snhu.cs_360.weighttracker_brycejensen;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.SmsManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * This class is responsible for sending SMS notifications and handling the required permissions.
 * It provides methods to send SMS messages and handle the permission result.
 */
public class NotificationsSMS {
    private static final int REQUEST_SEND_SMS_PERMISSION = 1;
    private Context context;

    /**
     * Constructor that initializes the context.
     *
     * @param context The context of the application.
     */
    public NotificationsSMS(Context context) {
        this.context = context;
    }

    /**
     * Sends an SMS message to the specified phone number with the given message.
     * If the SEND_SMS permission is granted, the message is sent directly.
     * Otherwise, it requests the permission from the user.
     *
     * @param phoneNumber The phone number to send the SMS message to.
     * @param message     The message to be sent.
     */
    public void sendSMS(String phoneNumber, String message) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
        } else {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.SEND_SMS}, REQUEST_SEND_SMS_PERMISSION);
        }
    }

    /**
     * Handles the permission result for sending SMS messages.
     * If the permission is granted, it displays a toast message indicating that the SMS permission is granted.
     * If the permission is denied, it displays a toast message indicating that the SMS permission is denied.
     *
     * @param requestCode  The request code for the permission request.
     * @param grantResults The grant results for the permission request.
     */
    public void handlePermissionResult(int requestCode, int[] grantResults) {
        if (requestCode == REQUEST_SEND_SMS_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can send SMS
                Toast.makeText(context, "SMS permission granted", Toast.LENGTH_SHORT).show();
            } else {
                // Permission denied
                Toast.makeText(context, "SMS permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}