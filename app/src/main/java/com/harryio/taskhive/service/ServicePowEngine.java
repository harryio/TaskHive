package com.harryio.taskhive.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.harryio.taskhive.service.ProofOfWorkService.PowItem;

import java.util.LinkedList;
import java.util.Queue;

import ch.dissem.bitmessage.ports.ProofOfWorkEngine;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * Proof of Work engine that uses the Proof of Work service.
 */

public class ServicePowEngine implements ProofOfWorkEngine {
    private final Context ctx;

    private static final Object lock = new Object();
    private Queue<PowItem> queue = new LinkedList<>();
    private ProofOfWorkService.PowBinder service;

    public ServicePowEngine(Context ctx) {
        this.ctx = ctx;
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            synchronized (lock) {
                ServicePowEngine.this.service = (ProofOfWorkService.PowBinder) service;
                while (!queue.isEmpty()) {
                    ServicePowEngine.this.service.process(queue.poll());
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            service = null;
        }
    };

    @Override
    public void calculateNonce(byte[] initialHash, byte[] targetValue, Callback callback) {
        PowItem item = new PowItem(initialHash, targetValue, callback);
        synchronized (lock) {
            if (service != null) {
                service.process(item);
            } else {
                queue.add(item);
                ctx.bindService(new Intent(ctx, ProofOfWorkService.class), connection,
                        BIND_AUTO_CREATE);
            }
        }
    }
}
