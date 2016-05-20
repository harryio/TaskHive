package com.harryio.taskhive.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.harryio.taskhive.notification.NetworkNotification;

import ch.dissem.bitmessage.BitmessageContext;

import static com.harryio.taskhive.notification.NetworkNotification.ONGOING_NOTIFICATION_ID;

/**
 * Define a Service that returns an IBinder for the
 * sync adapter class, allowing the sync adapter framework to call
 * onPerformSync().
 */
public class BitmessageService extends Service {
    // Object to use as a thread-safe lock
    private static final Object lock = new Object();

    private static NetworkNotification notification = null;
    private static BitmessageContext bmc = null;

    private static volatile boolean running = false;

    public static boolean isRunning() {
        return running && bmc.isRunning();
    }

    @Override
    public void onCreate() {
        synchronized (lock) {
            if (bmc == null) {
                bmc = Singleton.getBitmessageContext(this);
                notification = new NetworkNotification(this, bmc);
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (bmc.isRunning()) bmc.shutdown();
        running = false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new BitmessageBinder();
    }

    public class BitmessageBinder extends Binder {
        public void startupNode() {
            startService(new Intent(BitmessageService.this, BitmessageService.class));
            running = true;
            startForeground(ONGOING_NOTIFICATION_ID, notification.getNotification());
            if (!bmc.isRunning()) {
                bmc.startup();
            }
            notification.show();
        }

        public void shutdownNode() {
            if (bmc.isRunning()) {
                bmc.shutdown();
            }
            running = false;
            stopForeground(false);
            stopSelf();
        }
    }
}
