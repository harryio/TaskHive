package com.harryio.taskhive.ui;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;

import com.harryio.taskhive.R;
import com.harryio.taskhive.service.BitmessageService;
import com.harryio.taskhive.service.BitmessageService.BitmessageBinder;
import com.harryio.taskhive.service.Singleton;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SwitchDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ch.dissem.bitmessage.BitmessageContext;
import ch.dissem.bitmessage.entity.BitmessageAddress;
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
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private BitmessageContext bitmessageContext;
    private Label selectedLabel;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        bitmessageContext = Singleton.getBitmessageContext(this);
        List<Label> labels = bitmessageContext.messages().getLabels();
        createDrawer(toolbar, labels);
    }

    private void createDrawer(Toolbar toolbar, List<Label> labels) {
        BitmessageAddress channelAddress = Singleton.getChannelAddress(this);
        IProfile channelProfile = new ProfileDrawerItem()
                .withName(channelAddress.toString())
                .withNameShown(true)
                .withEmail(channelAddress.getAddress())
                .withTextColorRes(android.R.color.white)
                .withTag(channelAddress);

        ColorDrawable colorDrawable = new ColorDrawable(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        AccountHeader accountHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(colorDrawable)
                .withProfiles(Collections.singletonList(channelProfile))
                .build();

        ArrayList<IDrawerItem> drawerItems = new ArrayList<>();
        for (Label label : labels) {
            PrimaryDrawerItem item = new PrimaryDrawerItem();
            switch (label.getType()) {
                case INBOX:
                    item.withName(label.toString())
                            .withTag(label)
                            .withIcon(R.drawable.ic_inbox);
                    break;

                case SENT:
                    item.withName(label.toString())
                            .withTag(label)
                            .withIcon(R.drawable.ic_sent);
                    break;

                case UNREAD:
                    item.withName(label.toString())
                            .withTag(label)
                            .withIcon(R.drawable.ic_unread);
                    break;
            }

            drawerItems.add(item);
        }



        Drawer drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(accountHeader)
                .withDrawerItems(drawerItems)
                .addStickyDrawerItems(new SwitchDrawerItem()
                        .withName("Full Node")
                        .withIcon(R.drawable.ic_full_node)
                        .withCheckable(BitmessageService.isRunning())
                        .withOnCheckedChangeListener(new OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {
                                if (isChecked) {
                                    startFullNode();
                                } else {
                                    service.shutdownNode();
                                }
                            }
                        })
                        .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                            @Override
                            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                if (drawerItem.getTag() instanceof Label) {
                                    selectedLabel = (Label) drawerItem.getTag();
                                    showSelectedLabel();
                                    return false;
                                }
                                return false;
                            }
                        })

                )
                .withShowDrawerOnFirstLaunch(true)
                .build();
    }

    private void showSelectedLabel() {

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
