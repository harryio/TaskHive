package com.harryio.taskhive.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.harryio.taskhive.notification.ProofOfWorkNotification;

import java.util.LinkedList;
import java.util.Queue;

import ch.dissem.bitmessage.ports.MultiThreadedPOWEngine;
import ch.dissem.bitmessage.ports.ProofOfWorkEngine;
import static com.harryio.taskhive.notification.ProofOfWorkNotification.ONGOING_NOTIFICATION_ID;

/**
 * The Proof of Work Service makes sure POW is done in a foreground process, so it shouldn't be
 * killed by the system before the nonce is found.
 */
public class ProofOfWorkService extends Service {
    // Object to use as a thread-safe lock
    private static final Object lock = new Object();
    private static ProofOfWorkEngine engine;
    private static boolean calculating;
    private static final Queue<PowItem> queue = new LinkedList<>();
    private static ProofOfWorkNotification notification;

    @Override
    public void onCreate() {
        synchronized (lock) {
            if (engine == null) {
                engine = new MultiThreadedPOWEngine();
            }
        }
        notification = new ProofOfWorkNotification(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new PowBinder(this);
    }

    public static class PowBinder extends Binder {
        private final ProofOfWorkService service;

        private PowBinder(ProofOfWorkService service) {
            this.service = service;
        }

        public void process(PowItem item) {
            synchronized (queue) {
                service.startService(new Intent(service, ProofOfWorkService.class));
                service.startForeground(ONGOING_NOTIFICATION_ID,
                        notification.getNotification());
                if (!calculating) {
                    calculating = true;
                    service.calculateNonce(item);
                } else {
                    queue.add(item);
                    notification.update(queue.size()).show();
                }
            }
        }
    }


    static class PowItem {
        private final byte[] initialHash;
        private final byte[] targetValue;
        private final ProofOfWorkEngine.Callback callback;

        PowItem(byte[] initialHash, byte[] targetValue, ProofOfWorkEngine.Callback callback) {
            this.initialHash = initialHash;
            this.targetValue = targetValue;
            this.callback = callback;
        }
    }

    private void calculateNonce(final PowItem item) {
        engine.calculateNonce(item.initialHash, item.targetValue, new ProofOfWorkEngine.Callback() {
            @Override
            public void onNonceCalculated(byte[] initialHash, byte[] nonce) {
                try {
                    item.callback.onNonceCalculated(initialHash, nonce);
                } finally {
                    PowItem item;
                    synchronized (queue) {
                        item = queue.poll();
                        if (item == null) {
                            calculating = false;
                            stopForeground(true);
                            stopSelf();
                        } else {
                            notification.update(queue.size()).show();
                        }
                    }
                    if (item != null) {
                        calculateNonce(item);
                    }
                }
            }
        });
    }
}
