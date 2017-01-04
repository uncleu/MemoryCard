package utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SettingsUtilities {

    private static int getCountdownBySec(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return Integer.parseInt(prefs.getString("countdown_value","30"));
    }
    public static int getCountdownByMilliSec(Context context){
        return getCountdownBySec(context)*1000;
    }

    public static boolean isCountdownTimerOn(Context context){
        SharedPreferences prefs =PreferenceManager.getDefaultSharedPreferences(context);
        boolean is = prefs.getBoolean("switch_preference_isCount",false);
        return is;
    }

    public static int getStudyFrequency(Context context){
        SharedPreferences prefs =PreferenceManager.getDefaultSharedPreferences(context);
        String sFrq = prefs.getString("frequency_type","4");
        return Integer.parseInt(sFrq);
    }
    public static int getNoStudyFrequency(Context context){
        SharedPreferences prefs =PreferenceManager.getDefaultSharedPreferences(context);
        String sFrq = prefs.getString("nostudy_frequency_type","4");
        return Integer.parseInt(sFrq);
    }
    public static int getNoStudyFrequencyValue(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return Integer.parseInt(prefs.getString("nostudy_value","30"));
    }
}
