package com.memorycard.android.memorycardapp;

import android.app.LoaderManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.List;

import utilities.CardsFilter;
import utilities.DataBaseManager;

public class LongTimeNoStudyNotificationService extends Service {

    private static final String TAG = "LongTimeNoStudyService";
    private Context context;
    private NotificationManager notificationMgr;

    public LongTimeNoStudyNotificationService() {
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

        new Thread(new LongTimeNoStudyNotificationService.ServiceWorker(), "LongTimeNoStudyService")
                .start();

        return START_NOT_STICKY;
    }




    class ServiceWorker implements Runnable{
        public List<CardsGroup> cardsgrouplist;

        public ServiceWorker() { }

        public void run() {
            final String TAG2 = "NoStudyNotifService:" + Thread.currentThread().getId();
            // do background processing here...
            try {
                DataBaseManager dataBaseManager = DataBaseManager.getDbManager(context);
                cardsgrouplist = dataBaseManager.loadCardsGroupList();
                if(cardsgrouplist != null && cardsgrouplist.size()>0){
                    List<String>namelist = CardsFilter.getNoStudyLongTimeGroupList(context,cardsgrouplist);

                    String title = "Memorise";
                    String message = "";
                    for(String name : namelist){
                        message += name + "\n";
                    }
                    message += "Upon MemoryCard Groups haven't been studied for a while";
                    NotificationCompat.BigTextStyle inboxStyle = new NotificationCompat.BigTextStyle();
                    inboxStyle.setBigContentTitle(title);
                    inboxStyle.setSummaryText("Memorise Notification");
                    displayNotificationMessage(title,message,inboxStyle);
                }
            } catch (Exception e) {
                Log.v(TAG2, "no study noficiation error");
            }
        }


        private void displayNotificationMessage(String titile, String message, NotificationCompat.BigTextStyle inboxStyle) {
            Bitmap aBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.notification);

            Notification notification = new NotificationCompat.Builder(context)
                    .setContentTitle(titile)
                    .setStyle(inboxStyle.bigText(message))
                    .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                    .setTicker("News!!!")
                    .setLargeIcon(aBitmap)
                    .setOngoing(false)
                    .build();

            notificationMgr.notify(1, notification);
        }
    }


    @Override
    public void onDestroy() {
        Log.v(TAG, "in onDestroy(). Interrupting threads and cancelling notifications");
        //interrupt all threads in ThreadGroup
        //supprimer la notification
        notificationMgr.cancelAll();
        super.onDestroy();
    }



}
