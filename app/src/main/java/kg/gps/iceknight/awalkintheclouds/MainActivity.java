package kg.gps.iceknight.awalkintheclouds;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import kg.gps.iceknight.awalkintheclouds.service.GpsCoordService;
import kg.gps.iceknight.awalkintheclouds.service.MockService;
import kg.gps.iceknight.awalkintheclouds.service.MovingService;

public class MainActivity extends AppCompatActivity {

    Location currentLocation;

    LocationManager locationManager;
    NotificationManager mNotificationManager;
    NumberPicker numberPicker1;
    NumberPicker numberPicker2;

    Long speed;
    Long distance;
    Long delay;

    @SuppressLint({"MissingPermission", "WrongConstant"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        numberPicker1 = findViewById(R.id.numberPicker1);
        numberPicker2 = findViewById(R.id.numberPicker2);
        numberPicker1.setMinValue(0);
        //Specify the maximum value/number of NumberPicker
        numberPicker1.setMaxValue(100);

        //Gets whether the selector wheel wraps when reaching the min/max value.
        numberPicker1.setWrapSelectorWheel(true);

        numberPicker2.setMinValue(0);
        //Specify the maximum value/number of NumberPicker
        numberPicker2.setMaxValue(100);

        //Gets whether the selector wheel wraps when reaching the min/max value.
        numberPicker2.setWrapSelectorWheel(true);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Bundle extras = getIntent().getExtras();
        if (null != extras) {
            if ("notification".equals(extras.get("message"))) {
                try {
                    new Thread(() -> {
                        for (int i = 0; i < 100; i++) {
                            try {
                                Thread.sleep(1000);
                                Location location = GpsCoordService.calcNextCoord(currentLocation, 10L * i);
                                setMockLocation(location);
                            } catch (Exception e) { }
                        }
                    }).start();
                    finish();
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, e.getMessage() + " setLocation", Toast.LENGTH_LONG).show();
                }
            }
        }

    }

    @SuppressLint("SetTextI18n")
    public void invokeSetMockLocation(View view) {
        try {
            Location location = GpsCoordService.calcNextCoord(currentLocation, 1000L);
            setMockLocation(location);
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, e.getMessage() + " setLocation", Toast.LENGTH_LONG).show();
        }
    }

    @SuppressLint("NewApi")
    public Location setMockLocation(Location mockLocation) {

        Location location = new Location(LocationManager.GPS_PROVIDER);
        locationManager.addTestProvider(LocationManager.GPS_PROVIDER, false, false,
                false, false, true,
                true, true, 0, 1);
        location.setLatitude(mockLocation.getLatitude());
        location.setLongitude(mockLocation.getLongitude());
        location.setAccuracy(10);
        location.setTime(System.currentTimeMillis());
        location.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        locationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true);
        locationManager.setTestProviderStatus(
                LocationManager.GPS_PROVIDER,
                LocationProvider.AVAILABLE,
                null,
                System.currentTimeMillis());
        locationManager.setTestProviderLocation(LocationManager.GPS_PROVIDER, location);

        return mockLocation;
    }

    public void resetLocation(View view) {
        try {
            disableMockLocation();
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, e.getMessage() + " setLocation", Toast.LENGTH_LONG).show();
        }

    }

    @SuppressLint("MissingPermission")
    public void disableMockLocation() {
        currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        setMockLocation(currentLocation);
        if (locationManager.getProvider(LocationManager.GPS_PROVIDER) != null) {
            locationManager.removeTestProvider(LocationManager.GPS_PROVIDER);
        }
    }

    public void showNotificationInvoke(View view) {
        showNotification();
        this.finish();
    }

    @SuppressLint({"MissingPermission", "NewApi", "WrongConstant"})
    public void showNotification() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.notification_icon)
                        .setContentTitle("CrazyGo запущен")
                        .setContentText("Нажмите на уведомление, чтобы запустить сервис");

        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.putExtra("message", "notification");
        resultIntent.putExtra("speed", speed);
        resultIntent.putExtra("distance", distance);
        resultIntent.putExtra("delay", delay);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                stackBuilder.addParentStack(MainActivity.class);
                stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setAutoCancel(true);
        mNotificationManager.notify(7, mBuilder.build());
    }

}
