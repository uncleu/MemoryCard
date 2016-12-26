package utilities;

import java.util.Calendar;

/**
 * Created by yina on 2016/12/18.
 */

public class TimeUtilities {

    public static long getCurrentTimeInMillies(){
        Calendar calendar = Calendar.getInstance();
        long startTime = calendar.getTimeInMillis();
        return startTime;
    }




}
