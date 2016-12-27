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
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.List;

import utilities.DataBaseManager;

import static android.content.ContentValues.TAG;

public class CardsManagerActivity extends FragmentActivity implements CardFragment.OnFragmentInteractionListener {

    private static List<Card> cardslist;
    String tabName;
    public static Context context;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private int index;

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
        cardslist = new ArrayList<Card>();
        context = this;
        mPager = (ViewPager)findViewById(R.id.CardsViewPager);
        fragmentManager = this.getSupportFragmentManager();
        Intent intent = getIntent();
        tabName = intent.getStringExtra(CardsGroupLoaderActivity.EXTRA_TAB_NAME);
        String cardsGroupDescription = intent.getStringExtra(CardsGroupLoaderActivity.EXTRA_CARDSGROUP_DESCRIPTION);

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
