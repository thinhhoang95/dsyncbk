package hu.pe.thinhhoang.aaosync.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by hoang on 2/3/2016.
 */
public class ActiveClassChecker {
    public enum ClassType { ONGOING, ABOUTTO, NOT};
    public static final String STANDARD_TIME_FORMAT = "HH:mm:ss";

    public static ClassType check(int Thu, String gioBatDau, String gioKetThuc)
    {
        SimpleDateFormat sdf = new SimpleDateFormat(STANDARD_TIME_FORMAT, Locale.FRANCE);
        Calendar now = DateToCalendar.convert(new Date());
        try {
            if(now.get(Calendar.DAY_OF_WEEK)==Thu)
            {
                Calendar gBatDau = DateToCalendar.convert(sdf.parse(gioBatDau));
                Calendar gKetThuc = DateToCalendar.convert(sdf.parse(gioKetThuc));
                gBatDau.set(now.get(Calendar.YEAR),now.get(Calendar.MONTH),now.get(Calendar.DAY_OF_MONTH));
                gKetThuc.set(now.get(Calendar.YEAR),now.get(Calendar.MONTH),now.get(Calendar.DAY_OF_MONTH));
                if(now.compareTo(gBatDau)>0 && now.compareTo(gKetThuc)<0)
                {
                    return ClassType.ONGOING;
                } else {
                    gBatDau.add(Calendar.MINUTE,-30);
                    if(now.compareTo(gBatDau)>0 && now.compareTo(gKetThuc)<0)
                    {
                        return ClassType.ABOUTTO;
                    } else {
                        return ClassType.NOT;
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return ClassType.NOT;
    }
}
