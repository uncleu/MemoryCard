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


}
