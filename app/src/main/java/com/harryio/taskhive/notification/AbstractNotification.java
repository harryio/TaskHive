package com.harryio.taskhive.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

/**
 * Some base class to create and handle notifications.
 */
public abstract class AbstractNotification {
    protected final Context ctx;
    protected final NotificationManager manager;
    protected Notification notification;


    public AbstractNotification(Context ctx) {
        this.ctx = ctx.getApplicationContext();
        this.manager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    /**
     * @return an id unique to this notification class
     */
    protected abstract int getNotificationId();

    public Notification getNotification() {
        return notification;
    }

    public void show() {
        manager.notify(getNotificationId(), notification);
    }

    public void hide() {
        manager.cancel(getNotificationId());
    }
}
