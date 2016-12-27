package utilities;

import java.util.Calendar;

public class TimeUtilities {

    public static long getCurrentTimeInMillies(){
        Calendar calendar = Calendar.getInstance();
        long startTime = calendar.getTimeInMillis();
        return startTime;
    }




}
