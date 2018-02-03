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

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import kg.gps.iceknight.awalkintheclouds.service.GpsCoordService;
import kg.gps.iceknight.awalkintheclouds.service.MockService;
import kg.gps.iceknight.awalkintheclouds.service.MovingService;

public class MainActivity extends AppCompatActivity {

    Location choosedLocation;

    Location currentLocation;
    LocationManager locationManager;
    NotificationManager mNotificationManager;
    NumberPicker numberPicker1;
    NumberPicker numberPicker2;
    Button mainButton;
    EditText editTextCoord;
    Button setBtn;
    RadioButton variant1;
    RadioButton variant2;
    int variant;
    Long speed;
    Integer distance;
    Integer delay;
    Integer step = 50;

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
    public void showNotification(int variant, Integer distance, Integer time) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.notification_icon)
                        .setContentTitle("CrazyGo запущен")
                        .setContentText("Нажмите на уведомление, чтобы запустить сервис");

        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.putExtra("message", "notification");
        resultIntent.putExtra("variant", variant);
        resultIntent.putExtra("time", time);
        resultIntent.putExtra("distance", distance);
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
//        if ((delay != null) && (distance != null)) {
        distance = numberPicker1.getValue();
        delay = numberPicker2.getValue();
        switch (variant) {
            case 1: {
                showNotification(1, distance, delay);
                finish();
                break;
            }
            case 2: {
                showNotification(2, distance, delay);
                finish();
                break;
            }
            default:
                Toast.makeText(MainActivity.this, "Введите корректные данные", Toast.LENGTH_LONG).show();
        }
//        } else {
//            Toast.makeText(MainActivity.this, "Введите корректные данные", Toast.LENGTH_LONG).show();
//        }
    }

    @SuppressLint("MissingPermission")
    public void initialize() {
        mainButton = findViewById(R.id.mainButton);
        editTextCoord = findViewById(R.id.editTextCoodr);
        setBtn = findViewById(R.id.setBtn);
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
        numberPicker1.setMaxValue(10);
        numberPicker1.setWrapSelectorWheel(true);
        numberPicker1.setOnValueChangedListener((numberPicker, i, i1) -> distance = numberPicker.getValue());
        numberPicker2.setMinValue(1);
        numberPicker2.setMaxValue(10);
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
                    int variant = extras.getInt("variant");
                    if (variant == 2) {
                        Integer time = extras.getInt("time");
                        Integer distance = extras.getInt("distance");
                        Integer steps = (distance * 1000) / step;
                        Double delay = time.doubleValue() * 1000 / steps.doubleValue();

                        new Thread(() -> {
                            for (int i = 0; i < steps; i++) {
                                try {
                                    Thread.sleep(777);
                                    Location location = GpsCoordService.calcNextCoord(currentLocation, 50L * i);
                                    setMockLocation(location);
                                } catch (Exception e) { }
                            }
                            setMockLocation(currentLocation);
                        }).start();
                    }
                    if (variant == 1) {
                        Integer time = extras.getInt("time");
                        Integer distance = extras.getInt("distance");
                        Integer steps = (distance * 1000) / step;

                        new Thread(() -> {
                            try {
                                Location location = GpsCoordService.calcNextCoord(currentLocation, 1000L * distance);
                                setMockLocation(location);
                                Thread.sleep(time);
                            } catch (Exception e) { }
                            setMockLocation(currentLocation);
                        }).start();
                    }
                    finish();
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @SuppressLint("NewApi")
    public void setLocation(View view) {
        try {
            Location location = new Location("");
            String[] coordString = editTextCoord.getText().toString().split(" ");
            for (int i = 0; i < coordString.length; i++) {
                coordString[i] = coordString[i].trim().replace(",", ".");
            }
            Double latitude = Double.parseDouble(coordString[0]);
            Double longitute = Double.parseDouble(coordString[1]);
            location.setLatitude(latitude);
            location.setLongitude(longitute);
            choosedLocation = location;
            Toast.makeText(MainActivity.this, choosedLocation.getLatitude() + " " + choosedLocation.getLongitude(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "Введите корректные данные", Toast.LENGTH_LONG).show();
        }
    }

    public void resetLocation(View view) {
        try {
            disableMockLocation();
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "Ошибка", Toast.LENGTH_LONG).show();
        }
    }
}
