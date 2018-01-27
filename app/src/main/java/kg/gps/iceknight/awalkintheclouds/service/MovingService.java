package kg.gps.iceknight.awalkintheclouds.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

/**
 * Created by bborbuev on 1/27/18.
 */

public class MovingService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    TimeUnit.SECONDS.sleep(2);
                    Toast.makeText(this, "Service + " + i, Toast.LENGTH_LONG).show();
                } catch (InterruptedException e) {
                    Toast.makeText(this, e.getMessage() + " service", Toast.LENGTH_LONG).show();
                }
            }
        }).start();
        return START_NOT_STICKY;
    }

    public void mockMoving() {

    }
}
