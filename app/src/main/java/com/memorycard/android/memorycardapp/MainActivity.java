package com.memorycard.android.memorycardapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import utilities.CardsFilter;
import utilities.SettingsUtilities;


public class MainActivity extends AppCompatActivity {

    private Context context;
    private Toolbar toolbar;
    public static boolean isNoStudyNotify;

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
                        new SectionDrawerItem().withName("Memorise Section"),
                        new SecondaryDrawerItem().withName("Cards Group List").withIcon(R.drawable.group),
                        new SecondaryDrawerItem().withName("Editing Cards Group").withIcon(R.drawable.editing),
                        new SecondaryDrawerItem().withName("Download").withIcon(R.drawable.download),
                        new SecondaryDrawerItem().withName("Contact us").withIcon(R.drawable.us),
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
                        if(position == 6) {
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("Group Projet")
                                    .setMessage("CHEN Si"+"\n"+"GUAN Liang")
                                    .setPositiveButton("BACK", null)
                                    .show();
                        }
                        if (position == 7) {
                            //finish();
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
                if (menuItem.getItemId() == R.id.menu_item2) {
                    //toolbar.setVisibility(View.GONE);
                    Intent intent = new Intent(context, SettingsActivity.class);
                    startActivity(intent);
                }

                if (!"".equals(msg)) {
                }
                return true;
            }
        };

        toolbar.setOnMenuItemClickListener(onMenuItemClick);

        isNoStudyNotify = SettingsUtilities.isNoStudyNotify(context);

        if(isNoStudyNotify){
            Intent intent = new Intent(MainActivity.this, LongTimeNoStudyNotificationService.class);
            intent.setAction("start");
            startService(intent);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }


}