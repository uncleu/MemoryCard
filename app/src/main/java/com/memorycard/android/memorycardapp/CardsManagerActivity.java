package com.memorycard.android.memorycardapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.TimeUtils;
import android.support.v4.view.ViewPager;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
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

import java.util.ArrayList;
import java.util.List;

import utilities.DataBaseManager;
import utilities.SettingsUtilities;
import utilities.TimeUtilities;

import static android.content.ContentValues.TAG;

public class CardsManagerActivity extends FragmentActivity implements CardFragment.OnFragmentInteractionListener {

    private static List<Card> cardslist;
    String tabName;
    int tabPosition;
    public Context context;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    public static int countdownValue;
    public static boolean isCountdownTimer;
    private int index;
    public static int correctResponse = 0;


    ViewPager mPager;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards_manager);
        correctResponse = 0;//reset
        cardslist = new ArrayList<Card>();
        context = this;
        mPager = (ViewPager)findViewById(R.id.CardsViewPager);
        fragmentManager = this.getSupportFragmentManager();
        Intent intent = getIntent();
        tabName = intent.getStringExtra(CardsGroupLoaderActivity.EXTRA_TAB_NAME);
        tabPosition = intent.getIntExtra(CardsGroupLoaderActivity.EXTRA_TAB_POSITION,0);


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
                        new SectionDrawerItem().withName("Memrise Section"),
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
                            new AlertDialog.Builder(CardsManagerActivity.this)
                                    .setTitle("Group Projet")
                                    .setMessage("CHEN Si"+"\n"+"GUAN Liang")
                                    .setPositiveButton("BACK", null)
                                    .show();
                        }
                        if (position == 7) {
                            finish();
                        }
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .withAccountHeader(headerResult)
                .build();


        isCountdownTimer = SettingsUtilities.isCountdownTimerOn(context);
        countdownValue = SettingsUtilities.getCountdownByMilliSec(context);
        LoadCardsListTask mytask = new LoadCardsListTask();
        mytask.execute();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("CardsManager Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {

        super.onStop();



        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
        //finish();
    }

    @Override
    public void onBackPressed() {
        //update late modification date

        CardsGroup mCardsGroup =new CardsGroup();
        mCardsGroup.setlLastModifTimeInMillis(TimeUtilities.getCurrentTimeInMillies());
        mCardsGroup.setTab_name(tabName);

        DataBaseManager dbmanager = DataBaseManager.getDbManager(context);
        dbmanager.updateCardsGroup(mCardsGroup);


        Intent intent=getIntent();
        Bundle bundle = new Bundle();
        bundle.putInt("correctResponse",correctResponse );
        bundle.putInt("total",cardslist.size() );
        bundle.putInt("position",tabPosition );
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

        //Toast.makeText(MainActivity.this,"this is："+uri,Toast.LENGTH_SHORT).show();

        if(mPager != null){
            int currentIndex = mPager.getCurrentItem();
            if((currentIndex++) <= cardslist.size()){
                mPager.setCurrentItem(currentIndex);
            }
        }

    }

    public static Card getCardFromCardsList(int position) {
        Card card = null;
        if(cardslist != null) {
            card = cardslist.get(position);
        }

        return card;
    }


    private class LoadCardsListTask extends AsyncTask<String, Integer, List<Card>> {

        @Override
        protected void onPreExecute() {
            Log.d(TAG, "onPreExecute");
            super.onPreExecute();
        }

        @Override
        protected List<Card> doInBackground(String... params) {

            DataBaseManager dbmanager = DataBaseManager.getDbManager(context);
            cardslist = dbmanager.loadCardsByTabName(tabName);
            return cardslist;
        }


        @Override //update UI list
        protected void onPostExecute(List<Card> result) {
            super.onPostExecute(result);
            mPager.setAdapter(new CardFragmentAdapter(fragmentManager));
            //mPager.setCurrentItem(0);
            mPager.setOffscreenPageLimit(0);
            mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener(){

                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });

        }
    }



    private class CardFragmentAdapter extends FragmentStatePagerAdapter {

        public CardFragmentAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            CardFragment cf = CardFragment.newInstance(position);
            return cf;
        }

        @Override
        public int getCount() {
            return cardslist.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
        }
    }




}
