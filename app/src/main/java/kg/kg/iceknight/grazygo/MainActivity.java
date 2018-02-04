package kg.kg.iceknight.grazygo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.Toast;

import kg.kg.iceknight.grazygo.service.CoordService;

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
    Integer distance;
    Integer delay;
    Integer step = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            if (ActivityCompat
                    .checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                ActivityCompat.requestPermissions(this ,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
                ActivityCompat.requestPermissions(this ,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},2);
            }
            initialize(savedInstanceState);
            Bundle extras = getIntent().getExtras();
            handleNotification(extras);
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public Location setMockLocation(Location mockLocation) {

        Location location = new Location(LocationManager.GPS_PROVIDER);
        locationManager.addTestProvider(LocationManager.GPS_PROVIDER, false, false,
                false, false, true,
                true, true, 0, 1);
        location.setLatitude(mockLocation.getLatitude());
        location.setLongitude(mockLocation.getLongitude());
        location.setAccuracy(10);
        location.setTime(System.currentTimeMillis());
        location.setSpeed(150F);
        location.setBearing(45F);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            location.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        } else {
            Toast.makeText(MainActivity.this, "Требуется android версии 4.2 и выше", Toast.LENGTH_LONG).show();
            return null;
        }
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

    public void showNotification(int variant, Integer distance, Integer time) {
        try {
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.notification_icon)
                            .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
                                    R.mipmap.ic_launcher))
                            .setContentTitle("CrazyGo запущен")
                            .setContentText("Нажмите на уведомление, чтобы запустить сервис")
                            .setVisibility(Notification.VISIBILITY_PUBLIC);

            Intent resultIntent = new Intent(this, MainActivity.class);
            resultIntent.putExtra("message", "notification");
            resultIntent.putExtra("variant", variant);
            resultIntent.putExtra("time", time);
            resultIntent.putExtra("distance", distance);
            if (choosedLocation != null) {
                resultIntent.putExtra("latitude", choosedLocation.getLatitude());
                resultIntent.putExtra("longitude", choosedLocation.getLongitude());
            } else {
                resultIntent.putExtra("latitude", 0);
                resultIntent.putExtra("longitude", 0);
            }

            TaskStackBuilder stackBuilder = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                stackBuilder = TaskStackBuilder.create(this);
                stackBuilder.addParentStack(MainActivity.class);
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent =
                        null;
                resultPendingIntent = stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
                mBuilder.setContentIntent(resultPendingIntent);
                mBuilder.setAutoCancel(true);
                mNotificationManager.notify(7, mBuilder.build());
            } else {
                Toast.makeText(MainActivity.this, "Требуется android версии 4.2 и выше", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "Ошибка " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    public void mainBtnListener(View view) {
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

    }

    @SuppressLint("MissingPermission")
    public void initialize(Bundle bundle) {
        mainButton = findViewById(R.id.mainButton);
        editTextCoord = findViewById(R.id.editTextCoodr);
        setBtn = findViewById(R.id.setBtn);
        variant1 = findViewById(R.id.variant1);
        variant2 = findViewById(R.id.variant2);
        numberPicker1 = findViewById(R.id.numberPicker1);
        numberPicker2 = findViewById(R.id.numberPicker2);
        variant1.setOnClickListener(view -> {
            variant2.setChecked(false);
            numberPicker2.setEnabled(true);
            variant = 1;
        });

        variant2.setOnClickListener(view -> {
            variant1.setChecked(false);
            variant = 2;
            numberPicker2.setEnabled(false);
        });

        variant1.setChecked(true);
        variant = 1;
        numberPicker1.setMinValue(1);
        numberPicker1.setMaxValue(10);
        numberPicker1.setWrapSelectorWheel(true);
        numberPicker1.setOnValueChangedListener((numberPicker, i, i1) -> distance = numberPicker.getValue());
        numberPicker2.setMinValue(1);
        numberPicker2.setMaxValue(10);
        numberPicker2.setWrapSelectorWheel(true);
        numberPicker2.setOnValueChangedListener((numberPicker, i, i1) -> delay = numberPicker.getValue());
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void handleNotification(Bundle extras) {

        if (null != extras) {
            if ("notification".equals(extras.get("message"))) {
                try {
                    int variant = extras.getInt("variant");
                    Location resetLocation = currentLocation;
                    Double lat = extras.getDouble("latitude");
                    Double lon = extras.getDouble("longitude");
                    Location choosedLocation = new Location(currentLocation);
                    if ((lat != 0) || (lon != 0)) {
                        choosedLocation.setLatitude(lat);
                        choosedLocation.setLongitude(lon);
                    }

                    if (variant == 2) {
                        Integer distance = extras.getInt("distance");
                        Integer steps = (distance * 1000) / step;

                        new Thread(() -> {
                            for (int i = 0; i < steps; i++) {
                                try {
                                    Thread.sleep(777);
                                    Location location = CoordService.calcNextCoord(choosedLocation, 50L * i);
                                    setMockLocation(location);
                                } catch (Exception e) { }
                            }
                            disableMockLocation();
                        }).start();
                    }

                    if (variant == 1) {
                        Integer time = extras.getInt("time");
                        Integer distance = extras.getInt("distance");

                        new Thread(() -> {
                            try {
                                Location location = CoordService.calcNextCoord(choosedLocation, 1000L * distance);
                                setMockLocation(location);
                                Thread.sleep(time*1000);
                                setMockLocation(currentLocation);
                            } catch (Exception e) { }
                            disableMockLocation();
                        }).start();
                    }
                    finish();
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

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
            setMockLocation(choosedLocation);
            Toast.makeText(MainActivity.this, choosedLocation.getLatitude() + " " + choosedLocation.getLongitude() + " установлен", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "Введите корректные данные", Toast.LENGTH_LONG).show();
        }
    }

    public void resetLocation(View view) {
        try {
            disableMockLocation();
            Toast.makeText(MainActivity.this, "Настройки сброшены", Toast.LENGTH_LONG).show();
            choosedLocation = null;
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "Ошибка", Toast.LENGTH_LONG).show();
        }
    }

}
