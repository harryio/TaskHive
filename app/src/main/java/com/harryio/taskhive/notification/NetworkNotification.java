package com.harryio.taskhive.notification;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import com.harryio.taskhive.ui.MainActivity;
import com.harryio.taskhive.R;

import java.util.Timer;
import java.util.TimerTask;

import ch.dissem.bitmessage.BitmessageContext;
import ch.dissem.bitmessage.utils.Property;

/**
 * Shows the network status (as long as the client is connected as a full node)
 */
public class NetworkNotification extends AbstractNotification {
    public static final int ONGOING_NOTIFICATION_ID = 2;

    private final BitmessageContext bmc;
    private NotificationCompat.Builder builder;

    public NetworkNotification(Context ctx, BitmessageContext bmc) {
        super(ctx);
        this.bmc = bmc;
        builder = new NotificationCompat.Builder(ctx);
        builder.setSmallIcon(R.drawable.ic_notification_full_node)
                .setContentTitle(ctx.getString(R.string.bitmessage_full_node))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
    }

    @Override
    public Notification getNotification() {
        update();
        return notification;
    }

    @SuppressLint("StringFormatMatches")
    private boolean update() {
        boolean running = bmc.isRunning();
        builder.setOngoing(running);
        Property connections = bmc.status().getProperty("network").getProperty("connections");
        if (!running) {
            builder.setContentText(ctx.getString(R.string.connection_info_disconnected));
        } else if (connections.getProperties().length == 0) {
            builder.setContentText(ctx.getString(R.string.connection_info_pending));
        } else {
            StringBuilder info = new StringBuilder();
            for (Property stream : connections.getProperties()) {
                int streamNumber = Integer.parseInt(stream.getName().substring("stream ".length()));
                Integer nodeCount = (Integer) stream.getProperty("nodes").getValue();
                if (nodeCount == 1) {
                    info.append(ctx.getString(R.string.connection_info_1,
                            streamNumber));
                } else {
                    info.append(ctx.getString(R.string.connection_info_n,
                            streamNumber, nodeCount));
                }
                info.append('\n');
            }
            builder.setContentText(info);
        }
        Intent showMessageIntent = new Intent(ctx, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 1, showMessageIntent, 0);
        builder.setContentIntent(pendingIntent);
        notification = builder.build();
        return running;
    }

    @Override
    public void show() {
        update();
        super.show();

        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!update()) {
                    cancel();
                }
                NetworkNotification.super.show();
            }
        }, 10_000, 10_000);
    }

    @Override
    protected int getNotificationId() {
        return ONGOING_NOTIFICATION_ID;
    }
}
