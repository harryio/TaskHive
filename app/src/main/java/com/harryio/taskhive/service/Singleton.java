package com.harryio.taskhive.service;

import android.content.Context;

import com.harryio.taskhive.adapter.AndroidCryptography;
import com.harryio.taskhive.listener.MessageListener;
import com.harryio.taskhive.repository.AndroidAddressRepository;
import com.harryio.taskhive.repository.AndroidInventory;
import com.harryio.taskhive.repository.AndroidMessageRepository;
import com.harryio.taskhive.repository.AndroidProofOfWorkRepository;
import com.harryio.taskhive.repository.SqlHelper;

import java.util.List;

import ch.dissem.bitmessage.BitmessageContext;
import ch.dissem.bitmessage.entity.BitmessageAddress;
import ch.dissem.bitmessage.networking.DefaultNetworkHandler;
import ch.dissem.bitmessage.ports.MemoryNodeRegistry;
import ch.dissem.bitmessage.ports.MessageRepository;
import ch.dissem.bitmessage.utils.TTL;
import ch.dissem.bitmessage.utils.UnixTime;


public class Singleton {
    public static final Object lock = new Object();
    private static BitmessageContext bitmessageContext;
    private static MessageListener messageListener;
    private static BitmessageAddress channelAddress;
    private static AndroidProofOfWorkRepository powRepo;

    public static BitmessageContext getBitmessageContext(Context context) {
        if (bitmessageContext == null) {
            synchronized (lock) {
                if (bitmessageContext == null) {
                    TTL.msg(UnixTime.HOUR);
                    TTL.getpubkey(UnixTime.HOUR);
                    TTL.pubkey(UnixTime.HOUR);
                    final Context ctx = context.getApplicationContext();
                    SqlHelper sqlHelper = new SqlHelper(ctx);
                    powRepo = new AndroidProofOfWorkRepository(sqlHelper);
                    bitmessageContext = new BitmessageContext.Builder()
                            .proofOfWorkEngine(new ServicePowEngine(context))
                            .cryptography(new AndroidCryptography())
                            .nodeRegistry(new MemoryNodeRegistry())
                            .inventory(new AndroidInventory(sqlHelper))
                            .addressRepo(new AndroidAddressRepository(sqlHelper))
                            .messageRepo(new AndroidMessageRepository(sqlHelper, ctx))
                            .powRepo(powRepo)
                            .networkHandler(new DefaultNetworkHandler())
                            .listener(getMessageListener(ctx))
                            .doNotSendPubkeyOnIdentityCreation()
                            .build();
                }
            }
        }
        return bitmessageContext;
    }

    public static BitmessageAddress getChannelAddress(Context context) {
        if (channelAddress == null) {
            synchronized (Singleton.class) {
                if (channelAddress == null) {
                    BitmessageContext bmc = getBitmessageContext(context);
                    List<BitmessageAddress> chans = bmc.addresses().getChans();
                    if (chans.size() > 0) {
                        channelAddress = chans.get(0);
                    } else {
                        channelAddress = bmc.createChan("TaskHive");
                    }
                }
            }
        }

        return channelAddress;
    }

    public static MessageListener getMessageListener(Context ctx) {
        if (messageListener == null) {
            synchronized (Singleton.class) {
                if (messageListener == null) {
                    messageListener = new MessageListener(ctx);
                }
            }
        }
        return messageListener;
    }

    public static MessageRepository getMessageRepository(Context context) {
        return getBitmessageContext(context).messages();
    }
}
