package com.memorycard.android.memorycardapp;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.SimpleCursorAdapter;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.app.LoaderManager;
import android.content.Loader;
import android.widget.Toast;

import java.util.List;
import java.util.Map;

import utilities.DataBaseManager;

public class MainCustomSettingsActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        MainCustomSettingsFragment.OnFragmentInteractionListener {

    private static final String TAG = "CustomSettings";
    private LoaderManager.LoaderCallbacks<Cursor> mCallbacks;
    private ListView listView;
    private Context context;
    private Activity activity;
    private SimpleCursorAdapter mAdapter;
    private List<Map<String,Object>> list;
    private Button addButton;
    private Cursor cursor;
    public List<CardsGroup> cardsgrouplist;
    public int currentPosition;
    public final static String EXTRA_MESSAGE_TABNAME = "com.memorycard.android.memorycardapp.tabname";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        activity = this;
        setContentView(R.layout.activity_main_custom_settings);
        listView = this.getListView();
        listView.setDivider(null);

/*        Uri baseUri = Uri.withAppendedPath(MemoryCardContentProvider.BASE_URI,DataBaseManager.LIST_TAB);
        Cursor cursor = getContentResolver().query(baseUri, null, null, null, null);*/

        mAdapter = new SimpleCursorAdapter(activity,
                R.layout.cards_group_list_item2,
                null,
                new String[]{DataBaseManager.COL_LIST_NAME},
                new int[]{R.id.custom_cardsgroup_name},
                0);

        listView.setAdapter(mAdapter);

        addButton = (Button)findViewById(R.id.add_new_group_button2);
        addButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.activity_main_custom_settings, new MainCustomSettingsFragment()).commit();
            }
        });



        activity.getLoaderManager().initLoader(0, null, this);
        mCallbacks = this;

        this.registerForContextMenu(getListView());

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        // TODO Auto-generated method stub
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, Menu.FIRST,0,"add a new card");
        menu.add(0,Menu.FIRST+1,0,"delete this cards list");
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        String tab_name = cardsgrouplist.get(currentPosition).getTab_name();
        switch(item.getItemId())
        {
            case Menu.FIRST:
                Intent intent = new Intent(this,CardCustomSettingsActivity.class);
                intent.putExtra(EXTRA_MESSAGE_TABNAME,tab_name);
                startActivity(intent);
                break;
            case Menu.FIRST+1:
                DataBaseManager dataBaseManager = DataBaseManager.getDbManager(context);
                dataBaseManager.deleteCardsGroupFromDataBase(getContentResolver(),tab_name);
                activity.getLoaderManager().restartLoader(0, null, this);
                Toast.makeText(context,"delete cards group successful",Toast.LENGTH_SHORT);
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onListItemClick(ListView lv, View v, int position, long id) {
        super.onListItemClick(lv, v, position, id);
        currentPosition = position;
        activity.openContextMenu(lv);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Uri baseUri = Uri.withAppendedPath(MemoryCardContentProvider.BASE_URI,DataBaseManager.LIST_TAB);
        //Uri completeUri = Uri.withAppendedPath(MemoryCardContentProvider.BASE_URI,DataBaseManager.LIST_TAB+"/1");
        String select = null;
        MemoryCardContentProvider.checkURI(baseUri,DataBaseManager.LIST_TAB);

        return new CursorLoader(context,baseUri,DataBaseManager.listProjection,select,null,null);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cardsgrouplist = DataBaseManager.extractCGfromCursor(data);
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
