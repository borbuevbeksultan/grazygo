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
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
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
    Button mainButton;
    EditText coordinatesEditTxt;
    Button setBtn;
    RadioButton variant1;
    RadioButton variant2;
    int variant;
    Long speed;
    Integer distance;
    Integer delay;

    @SuppressLint({"MissingPermission", "WrongConstant"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
        Bundle extras = getIntent().getExtras();
        handleNotification(extras);
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

    @SuppressLint("MissingPermission")
    public void disableMockLocation() {
        currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        setMockLocation(currentLocation);
        if (locationManager.getProvider(LocationManager.GPS_PROVIDER) != null) {
            locationManager.removeTestProvider(LocationManager.GPS_PROVIDER);
        }
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

    public void mainBtnListener(View view) {
        if ((delay != null) && (distance != null)) {
            switch (variant) {
                case 1: {
                    //TODO:
                    break;
                }
                case 2: {
                    //TODO:
                    break;
                }
                default:
                    Toast.makeText(MainActivity.this, "Введите корректные данные", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(MainActivity.this, "Введите корректные данные", Toast.LENGTH_LONG).show();
        }
    }

    @SuppressLint("MissingPermission")
    public void initialize() {
        mainButton = findViewById(R.id.mainButton);
//        coordinatesEditTxt = findViewById(R.id.inputCoords);
//        setBtn = findViewById(R.id.setButton);
        variant1 = findViewById(R.id.variant1);
        variant2 = findViewById(R.id.variant2);
        variant1.setOnClickListener(view -> {
            variant2.setChecked(false);
            variant = 1;
        });

        variant2.setOnClickListener(view -> {
            variant1.setChecked(false);
            variant = 2;
        });

        variant1.setChecked(true);
        variant = 1;
        numberPicker1 = findViewById(R.id.numberPicker1);
        numberPicker2 = findViewById(R.id.numberPicker2);
        numberPicker1.setMinValue(1);
        numberPicker1.setMaxValue(100);
        numberPicker1.setWrapSelectorWheel(true);
        numberPicker1.setOnValueChangedListener((numberPicker, i, i1) -> distance = numberPicker.getValue());
        numberPicker2.setMinValue(1);
        numberPicker2.setMaxValue(100);
        numberPicker2.setWrapSelectorWheel(true);
        numberPicker2.setOnValueChangedListener((numberPicker, i, i1) -> delay = numberPicker.getValue());

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

    }

    public void handleNotification(Bundle extras) {

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
}
