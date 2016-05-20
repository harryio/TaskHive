package com.harryio.taskhive.listener;

import android.content.Context;

import com.harryio.taskhive.notification.NewMessageNotification;

import java.util.Deque;
import java.util.LinkedList;

import ch.dissem.bitmessage.BitmessageContext;
import ch.dissem.bitmessage.entity.Plaintext;

/**
 * Listens for decrypted Bitmessage messages. Does show a notification.
 * <p>
 * Should show a notification when the app isn't running, but update the message list when it is.
 * Also,
 * notifications should be combined.
 * </p>
 */
public class MessageListener implements BitmessageContext.Listener {
    private final Deque<Plaintext> unacknowledged = new LinkedList<>();
    private int numberOfUnacknowledgedMessages = 0;
    private final NewMessageNotification notification;

    public MessageListener(Context ctx) {
        this.notification = new NewMessageNotification(ctx);
    }

    @Override
    public void receive(final Plaintext plaintext) {
        synchronized (unacknowledged) {
            unacknowledged.addFirst(plaintext);
            numberOfUnacknowledgedMessages++;
            if (unacknowledged.size() > 5) {
                unacknowledged.removeLast();
            }
        }

        if (numberOfUnacknowledgedMessages == 1) {
            notification.singleNotification(plaintext);
        } else {
            notification.multiNotification(unacknowledged, numberOfUnacknowledgedMessages);
        }
        notification.show();
    }

    public void resetNotification() {
        notification.hide();
        synchronized (unacknowledged) {
            unacknowledged.clear();
            numberOfUnacknowledgedMessages = 0;
        }
    }
}
