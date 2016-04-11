package hu.pe.thinhhoang.aaosync.utils;

/**
 * Created by hoang on 2/2/2016.
 */
public class HoraresLookup {
    private static final String[] horaresEntryF1 = {"06:30","07:15","08:10","09:05","10:00","10:45","12:30","13:15","14:10","15:05","16:00","16:45","17:30","18:15","19:00","19:55","20:40"};
    private static final String[] horaresExitF1 = {"07:15","08:00","08:55","09:50","10:45","11:30","13:15","14:00","14:55","15:50","16:45","17:30","18:15","19:00","19:45","20:40","21:25"};
    private static final String[] horaresEntryF2 = {"06:50","07:35","08:30","09:15","10:10","10:55","12:30","13:15","14:10","14:55","15:50","16:35","17:30","18:15","19:10","19:55","20:40"};
    private static final String[] horaresExitF2 = {"07:35","08:20","09:15","10:00","10:55","11:40","13:15","14:00","14:55","15:40","16:35","17:20","18:15","19:00","19:55","20:40","21:25"};

    public static String getEntryHorareF1(int x)
    {
        return horaresEntryF1[x-1];
    }

    public static String getExitHorareF1(int x)
    {
        return horaresExitF1[x-1];
    }

    public static String getEntryHorareF2(int x)
    {
        return horaresEntryF2[x-1];
    }

    public static String getExitHorareF2(int x)
    {
        return horaresExitF2[x-1];
    }
}
