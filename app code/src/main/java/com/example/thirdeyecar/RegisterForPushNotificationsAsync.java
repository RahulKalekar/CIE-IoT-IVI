package com.example.thirdeyecar;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import java.net.URL;

import me.pushy.sdk.Pushy;

public class RegisterForPushNotificationsAsync extends AsyncTask<Void, Void, Object> {
    private Activity mActivity;

    public RegisterForPushNotificationsAsync(Activity activity) {
        this.mActivity = activity;
    }

    @Override
    protected Object doInBackground(Void... params) {
        try {
            String deviceToken = Pushy.register(mActivity);
            Log.d("Pushy", "Pushy device token: " + deviceToken);
            new URL("https://{YOUR_API_HOSTNAME}/register/device?token=" + deviceToken).openConnection();
            return deviceToken;
        } catch (Exception exc) {
            return exc;
        }
    }

    @Override
    protected void onPostExecute(Object result) {
        String message;
        if (result instanceof Exception) {
            Log.e("Pushy", result.toString());
            message = ((Exception) result).getMessage();
        } else {
            message = "Pushy device token: " + result.toString();
        }
        new android.app.AlertDialog.Builder(mActivity)
                .setTitle("Pushy")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }
}
