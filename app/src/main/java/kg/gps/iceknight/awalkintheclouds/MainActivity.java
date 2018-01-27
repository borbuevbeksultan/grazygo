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
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import kg.gps.iceknight.awalkintheclouds.service.GpsCoordService;
import kg.gps.iceknight.awalkintheclouds.service.MovingService;

public class MainActivity extends AppCompatActivity {

    Location currentLocation;

    LocationManager locationManager;
    TextView status;
    Button showNotificationBtn;
    NotificationManager mNotificationManager;

    @SuppressLint({"MissingPermission", "WrongConstant"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        status = findViewById(R.id.status);
        showNotificationBtn = findViewById(R.id.notification);
        currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

//        Bundle extras = getIntent().getExtras();
//        if (null != extras) {
//            if ("notification".equals(extras.get("message"))) {
//                try {
////                    Location location = GpsCoordService.calcNextCoord(currentLocation, 1000L);
////                    setMockLocation(location);
////                    disableMockLocation();
////                    Toast.makeText(MainActivity.this, "Прыжок выполнен", Toast.LENGTH_LONG).show();
////                    finish();
//                    new Thread(() -> {
//                        for (int i = 0; i < 10; i++) {
//                            try {
//                                Toast.makeText(this, "Service + " + i, Toast.LENGTH_LONG).show();
//                                Thread.sleep(1000);
//                            } catch (InterruptedException e) {
//                                Toast.makeText(this, e.getMessage() + " service", Toast.LENGTH_LONG).show();
//                            }
//                        }
//                    }).start();
//                } catch (Exception e) {
//                    Toast.makeText(MainActivity.this, e.getMessage() + " setLocation", Toast.LENGTH_LONG).show();
//                }
//            }
//        }

    }

    @SuppressLint("SetTextI18n")
    public void invokeSetMockLocation(View view) {
        try {
            Location location = GpsCoordService.calcNextCoord(currentLocation, 1000L);
            setMockLocation(location);
            status.setText("coord: "+ location.getLatitude() + location.getLongitude());
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
            status.setText("coord: disabled");
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
                        .setContentTitle("Прогулка по облакам запущена")
                        .setContentText("Нажмите на уведомление чтоб запустить сервис");

        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.putExtra("message", "notification");
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
