package com.memorycard.android.memorycardapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;


import java.io.IOException;
import java.io.InputStream;

import utilities.DataBaseManager;
import utilities.XmlUtilities;

public class MainActivity extends AppCompatActivity {


    private Context context;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();


        //initToolBar();
        XmlUtilities xmltool = new XmlUtilities();
        InputStream  in= null;
        try {
            //in = getAssets().open("food.xml");
            in = getAssets().open("family.xml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        CardsGroup cg= xmltool.xmlReader(in);
        cg.getTotal();

        DataBaseManager dbmanager = DataBaseManager.getDbManager(this);
        MemoryCardDataBaseHelper helper = dbmanager.getDatabaseHelper();
/*      dbmanager.dropTable("t1");
        dbmanager.dropTable("t2");
        dbmanager.dropTable("t3");
        dbmanager.dropTable("t4");
        dbmanager.dropTable("t5");
        dbmanager.dropTable("t6");
        dbmanager.dropTable("cardsgroup_list");*/
        //dbmanager.createNewCardsGroupTab(cg);


        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_main);
        Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                String msg = "";
/*                switch (menuItem.getItemId()) {

                    case R.id.menu_item1:
                        Intent intent = new Intent(context, CardsGroupLoaderActivity.class);
                        startActivity(intent);
                        break;

                    case R.id.menu_item2:
                        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
                        break;


                }*/
                if(menuItem.getItemId() == R.id.menu_item1){
                    Intent intent = new Intent(context, CardsGroupLoaderActivity.class);
                    startActivity(intent);
                } else if(menuItem.getItemId() == R.id.menu_item2){
                    //toolbar.setVisibility(View.GONE);
                    Intent intent = new Intent(context, SettingsActivity.class);
                    startActivity(intent);
                }

                if(!msg.equals("")) {
                }
                return true;
            }
        };

        toolbar.setOnMenuItemClickListener(onMenuItemClick);
        //setSupportActionBar(toolbar);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void sendMessage(View view) {
        Intent intent = new Intent(context, CardsGroupLoaderActivity.class);
        startActivity(intent);
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