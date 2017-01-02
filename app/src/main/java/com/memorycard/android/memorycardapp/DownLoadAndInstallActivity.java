package com.memorycard.android.memorycardapp;

import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import utilities.DownLoadAndInstallUtilities;

import static android.R.attr.id;

public class DownLoadAndInstallActivity extends AppCompatActivity {

    private static final String TAG = "DOWNLOAD";


    private Context context;
    private String downloadFileName;
    private ListView listView;
    private DownloadManager dm;
    private Long nodifyId;
    private TextView tet;
    private String uriFile;
    private Notification baseNF;
    private NotificationManager nm;
    private String[] list = {"family","food"};
    private ArrayAdapter<String> listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_down_load_and_install);

         context = getApplicationContext();
        tet = (TextView)findViewById(R.id.download_textview);

        listView = (ListView)findViewById(R.id.download_listview);
        listAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,list);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(position == 0){
                    downloadFileName = "family.xml";
                    String s = "http://t.jgz.la/attachment/201701/0210/nanasisi-135397/5869b6b2e2f23.zip";
                    //String s = "http://t.jgz.la/attachment/201612/3005/nanasisi-135397/58658007cfef9.zip";
                    new downloadTask().execute(s);
                }else if(position == 1){
                    downloadFileName = "food.xml";
                    String s = "http://t.jgz.la/attachment/201701/0210/nanasisi-135397/5869b6b2ecd95.zip";
                    new downloadTask().execute(s);
                }

            }
        });


    }


    class downloadTask extends AsyncTask<String, Integer, String> {
        int progres = 0;

        @Override
        protected void onPreExecute() {
            progres = 0;
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                Uri uri = Uri.parse(params[0]);
                DownloadManager.Request req = new DownloadManager.Request(uri);
                req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
                req.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS,downloadFileName);
                nodifyId = dm.enqueue(req);
                broadCast();

                for (int i = 0; i < 10; i++) {
                    progres += 10;
                    publishProgress(progres);
                    Thread.sleep(250);
                }
                return downloadFileName;
            } catch (Exception e) {
                Log.e(TAG, "Error doInBackGround " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            tet.setText(""+values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            tet.setText("ok");
        }

        private void broadCast(){
            IntentFilter intent = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
            BroadcastReceiver recervier = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    context.unregisterReceiver(this);
                    long reference  = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                    if(reference == nodifyId){
                        DownloadManager.Query question = new DownloadManager.Query();
                        question.setFilterById(nodifyId).setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);
                        Cursor cur= dm.query(question);
                        if(cur.moveToNext()){
                            int fileNameIndex = cur.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
                            int fUri = cur.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
                            String downloadFilePath = cur.getString(fileNameIndex);
                            uriFile = cur.getString(fUri);
                            //Unzip
                            File path = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
                            File file = new File(downloadFilePath);
                            String absoluPath = file.getAbsolutePath();
                            DownLoadAndInstallUtilities.Unzip.start(absoluPath);
                            DownLoadAndInstallUtilities.install(context,path+File.separator+downloadFileName);

                            Toast.makeText(getApplicationContext(), "Download " + downloadFileName+" Cards successful", Toast.LENGTH_SHORT).show();


/*
                            baseNF = new Notification();
                            baseNF.tickerText = "Download of "+downloadFileName+" finished!";
                            nm.notify(110, baseNF);*/
                        }
                        cur.close();
                    }else{
                        Log.e(TAG, "broadcast");
                    }
                }
            };
            registerReceiver(recervier, intent);
        }

    }

}
