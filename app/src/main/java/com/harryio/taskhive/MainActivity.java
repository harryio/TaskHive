package com.harryio.taskhive;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.widget.CompoundButton;

import com.harryio.taskhive.adapter.InboxAdapter;
import com.harryio.taskhive.service.BitmessageService;
import com.harryio.taskhive.service.BitmessageService.BitmessageBinder;
import com.harryio.taskhive.service.Singleton;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ch.dissem.bitmessage.BitmessageContext;
import ch.dissem.bitmessage.entity.Plaintext;
import ch.dissem.bitmessage.entity.valueobject.Label;

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
    @BindView(R.id.full_node_switch)
    SwitchCompat fullNodeSwitch;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setUpFullNodeSwitch();
        setUpRecyclerView();
    }

    private void setUpFullNodeSwitch() {
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
    }

    private void setUpRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        BitmessageContext bitmessageContext = Singleton.getBitmessageContext(this);
        List<Label> labels = bitmessageContext.messages().getLabels();
        Label inboxLabel = null;
        for (Label label : labels) {
            if (label.getType() == Label.Type.INBOX) {
                inboxLabel = label;
                break;
            }
        }

        if (inboxLabel != null) {
            List<Plaintext> messages = Singleton.getMessageRepository(this).findMessages(inboxLabel);
            if (messages.size() > 0) {
                InboxAdapter inboxAdapter = new InboxAdapter(messages);
                recyclerView.setAdapter(inboxAdapter);
            } else {
                //todo show empty view here
            }
        } else {
            //todo show empty view here
        }
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
