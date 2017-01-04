package com.memorycard.android.memorycardapp;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.mikepenz.materialize.util.UIUtils;

import java.io.IOException;
import java.io.InputStream;

import utilities.CardsFilter;
import utilities.DataBaseManager;
import utilities.XmlUtilities;

public class MainActivity extends AppCompatActivity {

    private Context context;
    private Toolbar toolbar;

    public static int day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();


        day = CardsFilter.getCurrentDayofStudy(context);

        TextView displayDay = (TextView) findViewById(R.id.display_day);
        displayDay.setText(Integer.toString(day));

        //drawerBuilder
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.bg_card)
                .addProfiles(
                        new ProfileDrawerItem().withName("Chen Si").withEmail("chensi@gmail.com").withIcon(getResources().getDrawable(R.drawable.cat))
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();


        new DrawerBuilder(this)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("Home").withIcon(R.drawable.profile),
                        new SectionDrawerItem().withName("SectiongDrawer"),
                        new SecondaryDrawerItem().withName("Cards Group List").withIcon(R.drawable.group),
                        new SecondaryDrawerItem().withName("Editing Cards Group").withIcon(R.drawable.editing),
                        new SecondaryDrawerItem().withName("Download").withIcon(R.drawable.download),
                        new SecondaryDrawerItem().withName("Contact us").withIcon(R.drawable.us).withEnabled(false),
                        new SecondaryDrawerItem().withName("Quit").withIcon(R.drawable.us)

                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                        if (position == 3) {
                            Intent intent = new Intent(context, CardsGroupLoaderActivity.class);
                            startActivity(intent);
                        }
                        if (position == 4) {
                            Intent intent = new Intent(context, MainCustomSettingsActivity.class);
                            startActivity(intent);
                        }
                        if (position == 5) {
                            Intent intent = new Intent(context, DownLoadAndInstallActivity.class);
                            startActivity(intent);
                        }
                        if (position == 6) {
                            Intent intent = new Intent(context, DownLoadAndInstallActivity.class);
                            finish();
                        }
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .withAccountHeader(headerResult)
                .build();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_main);
        setSupportActionBar(toolbar);
        Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                String msg = "";
                if (menuItem.getItemId() == R.id.menu_item1) {
                    Intent intent = new Intent(context, CardsGroupLoaderActivity.class);
                    startActivity(intent);
                } else if (menuItem.getItemId() == R.id.menu_item2) {
                    //toolbar.setVisibility(View.GONE);
                    Intent intent = new Intent(context, SettingsActivity.class);
                    startActivity(intent);
                }

                if (!msg.equals("")) {
                }
                return true;
            }
        };

        toolbar.setOnMenuItemClickListener(onMenuItemClick);


        Intent intent = new Intent(MainActivity.this, LongTimeNoStudyNotificationService.class);
        intent.setAction("start");
        startService(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

/*    public void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.toolbarTitle);

        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_toolbar_arrow);
        toolbar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(AndroidToolbarExample.this, "clicking the toolbar!", Toast.LENGTH_SHORT).show();
                    }
                }

        );
    }*/


}