package com.memorycard.android.memorycardapp;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import utilities.CircleIndicator;
import utilities.DataBaseManager;
import utilities.ProgressCircle;

import static android.content.ContentValues.TAG;

public class CardsGroupLoaderActivity extends ListActivity {

    private ListView listView;
    private static List<CardsGroup>mCardsGroupsList;
    private Context context;
    private ProgressCircle progressCircle;
    private TextView tot;
    private TextView ald;
    public CircleIndicator indicat;
    private static final int CALLBACK_REQUEST = 1;

    public static final String EXTRA_TAB_NAME = "com.memorycard.android.memorycardapp.extra_tabname";
    public static final String EXTRA_CARDSGROUP_DESCRIPTION = "com.memorycard.android.memorycardapp.extra_cardsgroup_description";
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
        intent.putExtra(EXTRA_CARDSGROUP_DESCRIPTION,desc);
        //startActivity(intent);
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
                int progressValue = data.getIntExtra("progressValue",1);
                int total = data.getIntExtra("total",1);
                int result = progressValue*100/total;

                tot.setText(String.valueOf(total));
                ald.setText(String.valueOf(progressValue));

                progressCircle.setProgress(result);

            }
        }
    }
}



