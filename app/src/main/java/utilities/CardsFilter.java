package utilities;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v4.util.TimeUtils;
import android.util.Log;

import com.memorycard.android.memorycardapp.CardsGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static utilities.TimeUtilities.convertToMillisByFrequence;

public class CardsFilter {

    private static final String TAG = "CardsFilter";

    public static boolean checkIsLongTimeNoStudy(Context context, CardsGroup cardsGroup) {

        int frequencyType = SettingsUtilities.getNoStudyFrequency(context);
        int frequencyNoStudy = SettingsUtilities.getNoStudyFrequencyValue(context);

        long longFrequenceValue = TimeUtilities.convertToMillisByFrequence(frequencyType, frequencyNoStudy);

        long lastPlayTimeInMillis = cardsGroup.getlLastModifTimeInMillis();

        long currentTime = TimeUtilities.getCurrentTimeInMillies();

        boolean flag = (currentTime - lastPlayTimeInMillis) > longFrequenceValue;
        return flag;
    }


    public static int getCurrentStudyDay(Context context) {

        int frequency = SettingsUtilities.getStudyFrequency(context);

        long lFrequency = TimeUtilities.convertToMillisByFrequence(frequency,1);

        long installTime = TimeUtilities.getInstallDate(context);

        long currentTime = TimeUtilities.getCurrentTimeInMillies();

        int day = new Long((currentTime-installTime)%lFrequency).intValue();

        return day;

    }

    public static int getCurrentDayofStudy(Context context) {

        int frequency = SettingsUtilities.getStudyFrequency(context);

        long lFrequency = TimeUtilities.convertToMillisByFrequence(frequency,1);

        long installTime = TimeUtilities.getInstallDate(context);

        long currentTime = TimeUtilities.getCurrentTimeInMillies();


        int day = ((currentTime-installTime)/lFrequency)>0?1:new Long((currentTime-installTime)/lFrequency).intValue();

        return day;

    }

    public static List<String> getNoStudyLongTimeGroupList(Context context,List<CardsGroup> cardsGroupList){
        List<String> listname = new ArrayList<>();

        for(CardsGroup cardsGroup:cardsGroupList){
            if(isNoStudyTimeExceed(context,cardsGroup)){
                long lastModif = cardsGroup.getlLastModifTimeInMillis();
                long currentTime = TimeUtilities.getCurrentTimeInMillies();
                long diff = currentTime - lastModif;
                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                Date date = new Date(diff);
                String res = cardsGroup.getName()+" : " +formatter.format(date);
                listname.add(res);
            }
        }

        return  listname;
    }

    private static boolean isNoStudyTimeExceed(Context context,CardsGroup cardsGroup){
        boolean isExceed = false;

        long lastModif = cardsGroup.getlLastModifTimeInMillis();
        long currentTime = TimeUtilities.getCurrentTimeInMillies();
        int noStudyFrequency = SettingsUtilities.getNoStudyFrequency(context);
        int noStudyFrequencyValue = SettingsUtilities.getNoStudyFrequencyValue(context);
        long interval = TimeUtilities.convertToMillisByFrequence(noStudyFrequency,noStudyFrequencyValue);
        long difference = currentTime - lastModif;
        isExceed = (difference>interval);
        return isExceed;
    }

}
