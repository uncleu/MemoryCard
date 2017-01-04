package utilities;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtilities {
    private static final String TAG = "TimeUtilities";

    public static long getCurrentTimeInMillies(){
        Calendar calendar = Calendar.getInstance();
        long startTime = calendar.getTimeInMillis();
        return startTime;
    }

    public  static long convertToMillisByFrequence(int frequenceType,int frequenceValue){

        long millis = 0;
        switch (frequenceType){

            case 1:{
                millis = 24*60*60*7*1000*frequenceValue;
                break;
            }
            case 2:{
                millis = 24*60*60*1000*frequenceValue;
                break;
            } case 3:{
                millis = 60*60*1000*frequenceValue;
                break;
            } case 4:{
                millis = 60*1000*frequenceValue;
                break;
            } default:{
                break;
            }

        }


        return millis;
    }


    public static long getInstallDate(Context context) {
        // get app installation date

        PackageManager packageManager =  context.getPackageManager();
        long installTimeInMilliseconds = 0; // install time is conveniently provided in milliseconds

        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            installTimeInMilliseconds = packageInfo.firstInstallTime;
        }
        catch (PackageManager.NameNotFoundException e) {
            // an error occurred, so display the Unix epoch
            Log.e(TAG,"error during getting install time");
        }

        return installTimeInMilliseconds;
    }

    public static String dateFormat = "dd-MM-yyyy hh:mm";
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);

    public static String getInstallDate(long milliSeconds){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return simpleDateFormat.format(calendar.getTime());
    }

}
