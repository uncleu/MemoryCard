package com.memorycard.android.memorycardapp;

import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import java.io.File;

import utilities.DownLoadAndInstallUtilities;

public class DownLoadService extends Service {
    private static final String TAG = "DownLoadService";
    private NotificationManager notificationMgr;
    private ThreadGroup myThreads = new ThreadGroup("ServiceWorker");
    private DownloadManager dm;
    private Context context;
    private Long nodifyId;


    public DownLoadService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        Log.v(TAG, "in onCreate()");
        notificationMgr = (NotificationManager) getSystemService(
                NOTIFICATION_SERVICE);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        super.onStartCommand(intent, flags, startId);
        String downloadFileName = intent.getExtras().getString("downloadFileName");
        String getDownloadFileUrl =  intent.getExtras().getString("getDownloadFileUrl");
        Context context = getApplicationContext();

        Log.v(TAG, "in onStartCommand(), downloadFileName = " + downloadFileName +
                ", getDownloadFileUrl = " + getDownloadFileUrl);


        new Thread(myThreads, new DownLoadService.ServiceWorker(context,downloadFileName,getDownloadFileUrl), "DownLoadService")
                .start();

        return START_NOT_STICKY;
    }

    class ServiceWorker implements Runnable {
        public String getDownloadFileUrl;
        private String downloadFileName;
        private Context context;
        private Long nodifyId;

        public ServiceWorker(Context context, String downloadFileName, String getDownloadFileUrl) {
            this.context = context;
            this.downloadFileName = downloadFileName;
            this.getDownloadFileUrl = getDownloadFileUrl;
        }

        public void run() {
            final String TAG2 = "DownLoadService:" + Thread.currentThread().getId();
            // do background processing here...
            try {
                dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                Uri uri = Uri.parse(getDownloadFileUrl);
                DownloadManager.Request req = new DownloadManager.Request(uri);
                req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
                req.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS,downloadFileName+".zip");
                nodifyId = dm.enqueue(req);
                broadCast();
                Log.v(TAG2, "download service. downloadFileName = " + downloadFileName);
            } catch (Exception e) {
                Log.v(TAG2, "... download service interrupted");
            }
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
                            String downloadFilePath = cur.getString(fileNameIndex);
                            //unzip
                            File path = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
                            File file = new File(downloadFilePath);
                            DownLoadAndInstallUtilities.unzipFunction(path.toString(),file.getAbsolutePath());

                            displayNotificationMessage("DownLoad Service finished");
                            Toast.makeText(getApplicationContext(), "Download " + downloadFileName+" Cards successful", Toast.LENGTH_SHORT).show();
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
    private void displayNotificationMessage(String message) {
        Bitmap aBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.downloadicon);

        Notification notification = new NotificationCompat.Builder(context)
                .setContentTitle(message)
                .setContentText("Touch to turn off service")
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setTicker("Starting up!!!")
                .setLargeIcon(aBitmap)
                .setOngoing(false)
                .build();

        notificationMgr.notify(0, notification);
    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "in onDestroy(). Interrupting threads and cancelling notifications");
        //interrupt all threads in ThreadGroup
        myThreads.interrupt();
        //supprimer la notification
        notificationMgr.cancelAll();
        super.onDestroy();
    }
}
