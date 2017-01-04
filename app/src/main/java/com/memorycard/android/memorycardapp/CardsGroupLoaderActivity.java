package com.memorycard.android.memorycardapp;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import utilities.CircleIndicator;
import utilities.DataBaseManager;
import utilities.ProgressCircle;

import static android.content.ContentValues.TAG;

public class CardsGroupLoaderActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor>  {

    private ListView listView;
    private static List<CardsGroup>mCardsGroupsList;
    private Context context;
    private ProgressCircle progressCircle;
    private TextView tot;
    private TextView ald;
    public CircleIndicator indicat;
    private static final int CALLBACK_REQUEST = 1;
    private SimpleCursorAdapter mAdapter;


    public static final String EXTRA_TAB_NAME = "com.memorycard.android.memorycardapp.extra_tabname";
    public static final String EXTRA_TAB_POSITION = "com.memorycard.android.memorycardapp.extra_tabposition";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_cards_group_loader);

        indicat = (CircleIndicator)findViewById(R.id.circleindicator);

        listView = this.getListView();
        listView.setDivider(null);

        //listener
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    indicat.setCurrentPage(view.getFirstVisiblePosition());
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });


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
                        if (position == 6) {
                            new AlertDialog.Builder(CardsGroupLoaderActivity.this)
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

        LoadCardsGroupListTask mytask = new LoadCardsGroupListTask();
        mytask.execute();
    }


    private class LoadCardsGroupListTask extends AsyncTask<String,Integer,List<CardsGroup>> {

        @Override
        protected void onPreExecute() {
            Log.d(TAG,"onPreExecute");
            super.onPreExecute();
        }

        @Override
        protected List<CardsGroup> doInBackground(String... params) {
            DataBaseManager dbmanager = DataBaseManager.getDbManager(context);
            List<CardsGroup> list = dbmanager.loadCardsGroupList();
            return list;
        }


        @Override //update UI list
        protected void onPostExecute(List<CardsGroup> result)
        {
            super.onPostExecute(result);
            mCardsGroupsList = result;
            List<String> tmplist = new ArrayList<>();

            for(CardsGroup cg:result){
                tmplist.add(cg.getName());
            }
            String[] namelist = tmplist.toArray(new String[tmplist.size()]);
            listView.setAdapter(new ArrayAdapter<>(context, R.layout.cards_group_list_item, R.id.text_view, namelist));
            indicat.initData(tmplist.size(), 0);
            indicat.setCurrentPage(0);
        }
    }

    @Override
    protected void onListItemClick(ListView lv, View v, int position, long id) {
        super.onListItemClick(lv, v, position, id);

        progressCircle = (ProgressCircle)findViewById(R.id.circleProgressbar);
        listView.getItemAtPosition(position);

        tot = (TextView)findViewById(R.id.text_tot);
        ald = (TextView)findViewById(R.id.text_ald);

        //get selected items
        CardsGroup cg =  mCardsGroupsList.get(position);
        String tabname = cg.getTab_name();
        String desc = cg.getDiscription();
/*        String selectedValue = (String) getListAdapter().getItem(position);
        Toast.makeText(this, selectedValue, Toast.LENGTH_SHORT).show();*/

        Intent intent = new Intent(this, CardsManagerActivity.class);

        intent.putExtra(EXTRA_TAB_NAME,tabname);
        intent.putExtra(EXTRA_TAB_POSITION,position);
        startActivityForResult(intent,CALLBACK_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == CALLBACK_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The Intent's data Uri identifies which contact was selected.
                //update progressvalue
                int progressValue = data.getIntExtra("correctResponse",1);
                int total = data.getIntExtra("total",1);
                int result = progressValue*100/total;

                tot.setText(String.valueOf(total));
                ald.setText(String.valueOf(progressValue));

                progressCircle.setProgress(result);

                //update actual accuracy
                int position  = data.getIntExtra("position",1);
                CardsGroup mcg = mCardsGroupsList.get(position);
                mcg.setAccuracy(progressValue);
                mCardsGroupsList.set(position,mcg);

            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Uri baseUri = Uri.withAppendedPath(MemoryCardContentProvider.BASE_URI,DataBaseManager.LIST_TAB+"/1");
        String select = null;
        return new CursorLoader(context,baseUri,DataBaseManager.listProjection,select,null,null);
    }




    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}



