package io.dispatch.dispatch;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Daniel on 5/16/2015.
 */
public class CrashService extends Service {
    private CrashListener listener;
    private Handler handler;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private float currentSpeed=0;
    private float pastSpeed=0;

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        listener = (CrashListener) intent.getExtras().get("listener");

        handler = new Handler();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void checkCrash(final Context context){
        locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {

            public void onLocationChanged(Location location) {

                // Called when a new location is found by the network location provider.
                Log.d("Long/Lat", location.getLatitude() + " " + location.getLongitude());
                Log.d("Speed",location.getSpeed()+"");

                currentSpeed = location.getSpeed();

                if(currentSpeed - pastSpeed < -13){
                    listener.onPossibleCrash(CrashService.this, context);
                    stopLocationListener();
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d("Status Changed", "has been enabled");
            }

            public void onProviderEnabled(String provider) {

                Log.d("Enabled", "has been enabled");
            }

            public void onProviderDisabled(String provider) {
                Log.d("Disabled happened", "has been enabled");
            }
        };

        startLocationListener();
    }

    public void stopLocationListener() {
        locationManager.removeUpdates(locationListener);
    }

    public void startLocationListener() {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }
}
