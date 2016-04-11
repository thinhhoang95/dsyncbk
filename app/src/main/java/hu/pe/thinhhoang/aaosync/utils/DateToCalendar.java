package hu.pe.thinhhoang.aaosync.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by hoang on 2/6/2016.
 */
public class DateToCalendar {
    public static Calendar convert(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }
}
