package com.harryio.taskhive.notification;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import com.harryio.taskhive.ui.MainActivity;
import com.harryio.taskhive.R;

/**
 * Ongoing notification while proof of work is in progress.
 */
public class ProofOfWorkNotification extends AbstractNotification {
    public static final int ONGOING_NOTIFICATION_ID = 3;

    public ProofOfWorkNotification(Context ctx) {
        super(ctx);
        update(1);
    }

    @Override
    protected int getNotificationId() {
        return ONGOING_NOTIFICATION_ID;
    }

    public ProofOfWorkNotification update(int numberOfItems) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx);

        Intent showMessageIntent = new Intent(ctx, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0, showMessageIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setUsesChronometer(true)
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_notification_proof_of_work)
                .setContentTitle(ctx.getString(R.string.proof_of_work_title))
                .setContentText(ctx.getString(R.string.proof_of_work_text, numberOfItems))
                .setContentIntent(pendingIntent);

        notification = builder.build();
        return this;
    }
}
