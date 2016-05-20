package com.harryio.taskhive;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;

import com.harryio.taskhive.service.BitmessageService;
import com.harryio.taskhive.service.BitmessageService.BitmessageBinder;
import com.harryio.taskhive.service.Singleton;

import ch.dissem.bitmessage.entity.BitmessageAddress;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String EXTRA_SHOW_MESSAGE = "com.harryio.taskhive.ShowMessage";
    public static final String ACTION_SHOW_INBOX = "com.harryio.taskhive.ShowInbox";

    private static BitmessageBinder service;
    private static boolean bound;
    private static ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MainActivity.service = (BitmessageBinder) service;
            MainActivity.bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            service = null;
            bound = false;
        }
    };

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SwitchCompat fullNodeSwitch = (SwitchCompat) findViewById(R.id.full_node_switch);
        fullNodeSwitch.setChecked(BitmessageService.isRunning());
        fullNodeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startFullNode();
                } else {
                    service.shutdownNode();
                }
            }
        });

        (findViewById(R.id.send_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BitmessageAddress address = Singleton.getIdentity(MainActivity.this);
                Log.i(TAG, "BitmessageAddress: " + address.getAddress());
                Singleton
                        .getBitmessageContext(MainActivity.this)
                        .send(address, new BitmessageAddress("BM-2cVBKttb72XtHtYRhmq3Ta5szfQU6j89Y8"),
                                "Test Task Hive", "Testing Task Hive.");
            }
        });

        (findViewById(R.id.join_chan_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = Singleton.getIdentity(MainActivity.this).getAddress();
                Log.i(TAG, "BitmessageAddress: " + address);
                Singleton.getBitmessageContext(MainActivity.this).joinChan("TaskHiveTest", address);
            }
        });
    }

    private void startFullNode() {
        if (service == null) {
            return;
        }

        new AlertDialog.Builder(this)
                .setMessage(R.string.full_node_warning)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        service.startupNode();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(new Intent(this, BitmessageService.class), connection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        if (bound) {
            unbindService(connection);
            bound = false;
        }

        super.onStop();
    }
}
